/**
 * AntiGravity Stateless Zero-PII MCP Router (gc2223.com/*)
 *
 * Design guarantees:
 * 1. Zero-PII: Never reads or holds any single-user emails, PATs, Cloudflare tokens,
 *    or Firebase project IDs. Uses zero KV / R2 / D1 / Cache storage.
 * 2. In-memory only: Validates X-Virtual-Alias (u-{12hex}@gc2223.com) and optional
 *    Google ID Token (JWT sub hash binding + exp + iss) purely in ephemeral isolate memory.
 * 3. Stateless delegation & ephemeral relay: Either returns structured DELEGATE_TO_APK
 *    instructions for client-side OAuth execution, or relays a single-request scoped
 *    header token (e.g. X-Scoped-GitHub-Token) and wipes references immediately.
 * 4. Guardrails: In-memory sliding-window rate limiter, JSON Schema tool argument validation,
 *    prompt-injection sanitization, SSRF URL blocking, and DLP secret redaction.
 */

const PROTOCOL_VERSION = "2024-11-05";
const SERVER_INFO = Object.freeze({
  name: "antigravity-stateless-mcp",
  version: "3.0.0",
});

const VIRTUAL_ALIAS_REGEX = /^u-([0-9a-f]{12})@gc2223\.com$/i;
const SAFE_REPO_REGEX = /^[A-Za-z0-9_.-]+\/[A-Za-z0-9_.-]+$/;
const SAFE_WORKFLOW_REGEX = /^[A-Za-z0-9_.-]+\.(ya?ml)$/;
const SAFE_REF_REGEX = /^[A-Za-z0-9_./-]{1,120}$/;

// Ephemeral per-isolate sliding window map (key = bucketKey, value = timestamps[]).
// Never persisted outside isolate RAM.
const RATE_LIMIT_BUCKETS = new Map();
const RATE_WINDOW_MS = 60_000;
const MAX_RATE_BUCKET_KEYS = 2048;

const CORS_HEADERS = Object.freeze({
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
  "Access-Control-Allow-Headers":
    "Content-Type, Authorization, X-Virtual-Alias, X-Google-Sub, X-Google-Id-Token, X-GCP-Project, X-Scoped-GitHub-Token",
  "Cache-Control": "no-store, no-cache, must-revalidate, private",
  Pragma: "no-cache",
});

export const MCP_TOOLS = Object.freeze([
  {
    name: "mcp_inspect_session_context",
    description:
      "現在のステートレス MCP セッション検証状態（仮想エイリアス形式・レート制限残量・認可レベル）を返す（PII非保持）",
    executionMode: "EDGE_STATELESS",
    inputSchema: {
      type: "object",
      additionalProperties: false,
      properties: {},
    },
  },
  {
    name: "mcp_sanitize_prompt",
    description:
      "外部入力やエージェント間テキストから制御文字・プロンプトインジェクション記号・漏洩トークンパターンをサニタイズする",
    executionMode: "EDGE_STATELESS",
    inputSchema: {
      type: "object",
      additionalProperties: false,
      required: ["text"],
      properties: {
        text: { type: "string", maxLength: 8000 },
        maxLength: { type: "number", minimum: 16, maximum: 8000 },
      },
    },
  },
  {
    name: "mcp_validate_payload",
    description:
      "ワークフロー引数・外部URL（SSRF防止）・GitHubリポジトリ形式を事前検証する",
    executionMode: "EDGE_STATELESS",
    inputSchema: {
      type: "object",
      additionalProperties: false,
      properties: {
        externalUrl: { type: "string", maxLength: 512 },
        githubRepo: { type: "string", maxLength: 140 },
        workflowFile: { type: "string", maxLength: 120 },
      },
    },
  },
  {
    name: "gcp_discover_resources",
    description:
      "ログイン中の Google OAuth トークンを用いて GCP 組織・プロジェクト・利用可能 Gemini モデルを自動検出する（APK委譲）",
    executionMode: "DELEGATE_TO_APK",
    inputSchema: {
      type: "object",
      additionalProperties: false,
      properties: {
        organizationId: { type: "string", maxLength: 64 },
      },
    },
  },
  {
    name: "gcp_enable_service_apis",
    description:
      "選択した GCP プロジェクトに対して Firebase Management / Firestore / AI Logic API を有効化する（APK委譲）",
    executionMode: "DELEGATE_TO_APK",
    inputSchema: {
      type: "object",
      additionalProperties: false,
      required: ["projectId"],
      properties: {
        projectId: { type: "string", maxLength: 80 },
        apis: { type: "array", maxItems: 20, items: { type: "string", maxLength: 100 } },
      },
    },
  },
  {
    name: "github_relay_dispatch_workflow",
    description:
      "一時ヘッダー X-Scoped-GitHub-Token が渡された場合は単発中継し即時破棄、未指定時は APK の Device Flow 連携へ委譲する",
    executionMode: "EPHEMERAL_RELAY_OR_DELEGATE",
    inputSchema: {
      type: "object",
      additionalProperties: false,
      required: ["repo", "workflow"],
      properties: {
        repo: { type: "string", maxLength: 140 },
        workflow: { type: "string", maxLength: 120 },
        ref: { type: "string", maxLength: 120 },
      },
    },
  },
  {
    name: "termux_run_preset",
    description:
      "端末内 Termux ローカル CLI デーモン（127.0.0.1）へサブセッション付きで安全プリセット実行を委譲する",
    executionMode: "DELEGATE_TO_APK",
    inputSchema: {
      type: "object",
      additionalProperties: false,
      required: ["preset"],
      properties: {
        preset: { type: "string", maxLength: 64 },
        workdir: { type: "string", maxLength: 160 },
      },
    },
  },
]);

export const MCP_RESOURCES = Object.freeze([
  {
    uri: "mcp://gc2223.com/session/context",
    name: "Stateless Session Context",
    description: "リクエスト単位でメモリ検証された仮想エイリアスと認可メタデータ（PII非保存）",
    mimeType: "application/json",
  },
  {
    uri: "mcp://gc2223.com/security/policy",
    name: "Zero-PII Stateless Security Policy",
    description: "KV/R2非保持・SSRFガード・DLPスクラバー・スキーマ検証ポリシー",
    mimeType: "application/json",
  },
  {
    uri: "mcp://gc2223.com/connectors/catalog",
    name: "Pluggable Connector Architecture",
    description: "Google OAuth / GitHub Device Flow / Termux CLI / Custom MCP の連携仕様",
    mimeType: "application/json",
  },
]);

// ============================================================================
// Pure Utility & Guardrail Functions (Exported for unit testing)
// ============================================================================

export function redactSensitiveText(input) {
  if (typeof input !== "string" || input.length === 0) return "";
  return input
    .replace(/Bearer\s+[A-Za-z0-9\-._~+/]+=*/gi, "Bearer [REDACTED]")
    .replace(/\b(ghp|gho|ghu|ghs|ghr)_[A-Za-z0-9_]{12,}\b/g, "$1_[REDACTED]")
    .replace(/\bgithub_pat_[A-Za-z0-9_]{16,}\b/g, "github_pat_[REDACTED]")
    .replace(/\bAIza[0-9A-Za-z_-]{20,}\b/g, "AIza[REDACTED]")
    .replace(/\bya29\.[0-9A-Za-z_-]{16,}\b/g, "ya29.[REDACTED]")
    .replace(/\beyJ[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}\b/g, "[JWT_REDACTED]");
}

export function sandboxUntrustedText(raw, maxLength = 2000) {
  if (typeof raw !== "string") return "";
  const clamped = Math.max(16, Math.min(Number(maxLength) || 2000, 8000));
  const cleaned = raw
    // Strip ASCII control characters except tab/newline
    .replace(/[\u0000-\u0008\u000B\u000C\u000E-\u001F\u007F]/g, "")
    // Neutralize common prompt injection role/fence tokens
    .replace(/<\|?(?:im_start|im_end|system|assistant|user)\|?>/gi, "[sanitized-tag]")
    .replace(/\b(ignore\s+previous\s+instructions|system\s+prompt)\b/gi, "[sanitized-directive]");
  return redactSensitiveText(cleaned).slice(0, clamped).trim();
}

export function assertSafeExternalUrl(rawUrl) {
  if (typeof rawUrl !== "string" || !rawUrl.trim()) {
    throw new Error("URL must be a non-empty string");
  }
  let parsed;
  try {
    parsed = new URL(rawUrl.trim());
  } catch {
    throw new Error("Malformed external URL");
  }
  if (parsed.protocol !== "https:") {
    throw new Error("Only https:// external URLs are permitted");
  }
  const host = parsed.hostname.toLowerCase().replace(/^\[|\]$/g, "");
  if (
    host === "localhost" ||
    host.endsWith(".local") ||
    host.endsWith(".internal") ||
    host.endsWith(".localhost") ||
    host === "::1" ||
    host.startsWith("fe80:") ||
    host.startsWith("fc") ||
    host.startsWith("fd")
  ) {
    throw new Error(`Blocked local/private hostname: ${host}`);
  }
  const ipv4Match = host.match(/^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/);
  if (ipv4Match) {
    const [a, b] = [Number(ipv4Match[1]), Number(ipv4Match[2])];
    if (
      a === 10 ||
      a === 127 ||
      a === 0 ||
      (a === 169 && b === 254) ||
      (a === 172 && b >= 16 && b <= 31) ||
      (a === 192 && b === 168)
    ) {
      throw new Error(`Blocked private/link-local IPv4 host: ${host}`);
    }
  }
  return parsed.toString();
}

export function validateToolArguments(toolDef, rawArgs) {
  const args = rawArgs == null ? {} : rawArgs;
  if (typeof args !== "object" || Array.isArray(args)) {
    throw new Error("Tool arguments must be a JSON object");
  }
  const schema = toolDef.inputSchema || {};
  const properties = schema.properties || {};
  const required = Array.isArray(schema.required) ? schema.required : [];

  for (const reqKey of required) {
    if (
      !(reqKey in args) ||
      args[reqKey] === null ||
      args[reqKey] === undefined ||
      (typeof args[reqKey] === "string" && args[reqKey].trim() === "")
    ) {
      throw new Error(`Missing required argument: ${reqKey}`);
    }
  }

  if (schema.additionalProperties === false) {
    for (const key of Object.keys(args)) {
      if (!(key in properties)) {
        throw new Error(`Unexpected argument key: ${key}`);
      }
    }
  }

  const sanitized = {};
  for (const [key, propSchema] of Object.entries(properties)) {
    if (!(key in args) || args[key] === undefined || args[key] === null) continue;
    const val = args[key];
    if (propSchema.type === "string") {
      if (typeof val !== "string") {
        throw new Error(`Argument '${key}' must be a string`);
      }
      const maxLen = propSchema.maxLength || 2000;
      if (val.length > maxLen) {
        throw new Error(`Argument '${key}' exceeds maxLength ${maxLen}`);
      }
      sanitized[key] = sandboxUntrustedText(val, maxLen);
    } else if (propSchema.type === "number") {
      const num = Number(val);
      if (!Number.isFinite(num)) {
        throw new Error(`Argument '${key}' must be a finite number`);
      }
      if (propSchema.minimum !== undefined && num < propSchema.minimum) {
        throw new Error(`Argument '${key}' must be >= ${propSchema.minimum}`);
      }
      if (propSchema.maximum !== undefined && num > propSchema.maximum) {
        throw new Error(`Argument '${key}' must be <= ${propSchema.maximum}`);
      }
      sanitized[key] = num;
    } else if (propSchema.type === "array") {
      if (!Array.isArray(val)) {
        throw new Error(`Argument '${key}' must be an array`);
      }
      if (propSchema.maxItems !== undefined && val.length > propSchema.maxItems) {
        throw new Error(`Argument '${key}' exceeds maxItems ${propSchema.maxItems}`);
      }
      sanitized[key] = val.map((item) => {
        if (typeof item !== "string") {
          throw new Error(`Items in '${key}' must be strings`);
        }
        return sandboxUntrustedText(item, propSchema.items?.maxLength || 128);
      });
    }
  }
  return sanitized;
}

function decodeBase64UrlToString(segment) {
  const padded = segment.replace(/-/g, "+").replace(/_/g, "/").padEnd(
    segment.length + ((4 - (segment.length % 4)) % 4),
    "="
  );
  const bin = atob(padded);
  const bytes = new Uint8Array(bin.length);
  for (let i = 0; i < bin.length; i++) {
    bytes[i] = bin.charCodeAt(i);
  }
  return new TextDecoder().decode(bytes);
}

export async function deriveVirtualAliasFromSub(sub) {
  const bytes = new TextEncoder().encode(String(sub).trim());
  const digest = await crypto.subtle.digest("SHA-256", bytes);
  const hex = Array.from(new Uint8Array(digest))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("")
    .slice(0, 12);
  return `u-${hex}@gc2223.com`;
}

/**
 * Validates incoming stateless identity headers purely in memory.
 * Never retains raw tokens, real email addresses, or unhashed Google subs.
 */
export async function validateCallerHeadersInMemory(request, nowEpochSec = Math.floor(Date.now() / 1000)) {
  const virtualAliasRaw = (request.headers.get("X-Virtual-Alias") || "").trim().toLowerCase();
  const aliasMatch = virtualAliasRaw ? virtualAliasRaw.match(VIRTUAL_ALIAS_REGEX) : null;
  if (virtualAliasRaw && !aliasMatch) {
    return {
      ok: false,
      status: 400,
      error: "Invalid X-Virtual-Alias format; expected u-<12hex>@gc2223.com",
    };
  }

  const rawIdToken = (request.headers.get("X-Google-Id-Token") || "").trim();
  const authHeader = (request.headers.get("Authorization") || "").trim();
  const bearerJwt =
    !rawIdToken && authHeader.toLowerCase().startsWith("bearer eyj")
      ? authHeader.slice(7).trim()
      : rawIdToken;

  if (!bearerJwt) {
    return {
      ok: true,
      authLevel: aliasMatch ? "virtual-alias-only" : "anonymous-discovery",
      virtualAlias: aliasMatch ? virtualAliasRaw : "anonymous@gc2223.com",
      aliasHashPrefix: aliasMatch ? aliasMatch[1] : "anon00000000",
      tokenVerified: false,
    };
  }

  const parts = bearerJwt.split(".");
  if (parts.length !== 3) {
    return { ok: false, status: 401, error: "Malformed X-Google-Id-Token JWT" };
  }

  let payload;
  try {
    payload = JSON.parse(decodeBase64UrlToString(parts[1]));
  } catch {
    return { ok: false, status: 401, error: "Unreadable Google ID token payload" };
  }

  const sub = typeof payload.sub === "string" ? payload.sub.trim() : "";
  if (!sub) {
    return { ok: false, status: 401, error: "Google ID token missing subject (sub)" };
  }

  const iss = typeof payload.iss === "string" ? payload.iss.trim() : "";
  if (iss && iss !== "https://accounts.google.com" && iss !== "accounts.google.com") {
    return { ok: false, status: 401, error: "Untrusted JWT issuer" };
  }

  const exp = Number(payload.exp);
  if (Number.isFinite(exp) && exp + 60 < nowEpochSec) {
    return { ok: false, status: 401, error: "Google ID token expired" };
  }

  const expectedAlias = await deriveVirtualAliasFromSub(sub);
  if (virtualAliasRaw && virtualAliasRaw !== expectedAlias) {
    return {
      ok: false,
      status: 403,
      error: "X-Virtual-Alias does not match SHA-256 hash of Google ID token subject",
    };
  }

  return {
    ok: true,
    authLevel: "id-token-verified",
    virtualAlias: expectedAlias,
    aliasHashPrefix: expectedAlias.slice(2, 14),
    tokenVerified: true,
  };
}

export function checkInMemoryRateLimit(bucketKey, limitPerMinute = 60, nowMs = Date.now()) {
  const limit = Math.max(5, Math.min(Number(limitPerMinute) || 60, 600));
  if (RATE_LIMIT_BUCKETS.size >= MAX_RATE_BUCKET_KEYS && !RATE_LIMIT_BUCKETS.has(bucketKey)) {
    const oldestKey = RATE_LIMIT_BUCKETS.keys().next().value;
    if (oldestKey !== undefined) RATE_LIMIT_BUCKETS.delete(oldestKey);
  }
  const existing = RATE_LIMIT_BUCKETS.get(bucketKey) || [];
  const fresh = existing.filter((ts) => nowMs - ts < RATE_WINDOW_MS);
  if (fresh.length >= limit) {
    RATE_LIMIT_BUCKETS.set(bucketKey, fresh);
    return { allowed: false, remaining: 0, limit };
  }
  fresh.push(nowMs);
  RATE_LIMIT_BUCKETS.set(bucketKey, fresh);
  return { allowed: true, remaining: Math.max(0, limit - fresh.length), limit };
}

// ============================================================================
// HTTP & JSON-RPC 2.0 Formatting Helpers
// ============================================================================

function jsonResponse(body, status = 200, extraHeaders = {}) {
  return new Response(JSON.stringify(body, null, 2), {
    status,
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      ...CORS_HEADERS,
      ...extraHeaders,
    },
  });
}

function rpcResult(id, result) {
  return { jsonrpc: "2.0", id: id ?? null, result };
}

function rpcError(id, code, message, data = undefined) {
  const err = { code, message };
  if (data !== undefined) err.data = data;
  return { jsonrpc: "2.0", id: id ?? null, error: err };
}

// ============================================================================
// MCP Tool & Resource Execution
// ============================================================================

async function executeToolCall(toolName, rawArguments, caller, request, fetchImpl = fetch) {
  const toolDef = MCP_TOOLS.find((t) => t.name === toolName);
  if (!toolDef) {
    return {
      isError: true,
      content: [{ type: "text", text: `Unknown MCP tool: ${toolName}` }],
    };
  }

  let args;
  try {
    args = validateToolArguments(toolDef, rawArguments);
  } catch (err) {
    return {
      isError: true,
      content: [{ type: "text", text: `Schema validation error: ${err.message}` }],
    };
  }

  switch (toolName) {
    case "mcp_inspect_session_context": {
      const payload = {
        stateless: true,
        zeroPii: true,
        virtualAlias: caller.virtualAlias,
        authLevel: caller.authLevel,
        tokenVerified: caller.tokenVerified,
        protocolVersion: PROTOCOL_VERSION,
        storageBindings: [],
      };
      return {
        isError: false,
        content: [{ type: "text", text: JSON.stringify(payload, null, 2) }],
      };
    }

    case "mcp_sanitize_prompt": {
      const sanitized = sandboxUntrustedText(args.text, args.maxLength || 2000);
      return {
        isError: false,
        content: [{ type: "text", text: sanitized }],
      };
    }

    case "mcp_validate_payload": {
      const checks = { valid: true };
      try {
        if (args.externalUrl) checks.externalUrl = assertSafeExternalUrl(args.externalUrl);
        if (args.githubRepo) {
          if (!SAFE_REPO_REGEX.test(args.githubRepo)) {
            throw new Error("githubRepo must match owner/repo format");
          }
          checks.githubRepo = args.githubRepo;
        }
        if (args.workflowFile) {
          if (!SAFE_WORKFLOW_REGEX.test(args.workflowFile)) {
            throw new Error("workflowFile must match <name>.yml or <name>.yaml");
          }
          checks.workflowFile = args.workflowFile;
        }
      } catch (err) {
        return {
          isError: true,
          content: [{ type: "text", text: `Validation rejected: ${err.message}` }],
        };
      }
      return {
        isError: false,
        content: [{ type: "text", text: JSON.stringify(checks, null, 2) }],
      };
    }

    case "github_relay_dispatch_workflow": {
      if (!SAFE_REPO_REGEX.test(args.repo)) {
        return { isError: true, content: [{ type: "text", text: "Invalid repo format (owner/repo expected)" }] };
      }
      if (!SAFE_WORKFLOW_REGEX.test(args.workflow)) {
        return { isError: true, content: [{ type: "text", text: "Invalid workflow filename (.yml/.yaml expected)" }] };
      }
      const ref = args.ref || "main";
      if (!SAFE_REF_REGEX.test(ref)) {
        return { isError: true, content: [{ type: "text", text: "Invalid git ref" }] };
      }

      // Stateless ephemeral relay: only if caller provides X-Scoped-GitHub-Token for this single call.
      let scopedToken = (request.headers.get("X-Scoped-GitHub-Token") || "").trim();
      if (!scopedToken) {
        const delegation = {
          action: "DELEGATE_TO_APK",
          connector: "github_oauth",
          virtualAlias: caller.virtualAlias,
          validatedArgs: { repo: args.repo, workflow: args.workflow, ref },
          reason: "No X-Scoped-GitHub-Token provided; execute using local Google-sub isolated GitHub token on device.",
        };
        return {
          isError: false,
          content: [{ type: "text", text: JSON.stringify(delegation, null, 2) }],
        };
      }

      try {
        const dispatchUrl = `https://api.github.com/repos/${args.repo}/actions/workflows/${encodeURIComponent(args.workflow)}/dispatches`;
        const ghResp = await fetchImpl(dispatchUrl, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${scopedToken}`,
            Accept: "application/vnd.github+json",
            "User-Agent": "AntiGravity-Stateless-MCP/3.0",
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ ref }),
        });
        const ghText = redactSensitiveText(await ghResp.text());
        return {
          isError: !ghResp.ok,
          content: [
            {
              type: "text",
              text: JSON.stringify(
                {
                  relayedStatelessly: true,
                  status: ghResp.status,
                  repo: args.repo,
                  workflow: args.workflow,
                  ref,
                  detail: ghText.slice(0, 300) || (ghResp.ok ? "Dispatched" : "GitHub API error"),
                },
                null,
                2
              ),
            },
          ],
        };
      } finally {
        // Drop ephemeral reference immediately after relay completes.
        scopedToken = "";
      }
    }

    case "gcp_discover_resources":
    case "gcp_enable_service_apis":
    case "termux_run_preset": {
      const delegation = {
        action: "DELEGATE_TO_APK",
        toolName,
        virtualAlias: caller.virtualAlias,
        authLevel: caller.authLevel,
        validatedArgs: args,
        note: "Stateless Zero-PII router does not retain user OAuth tokens; execute via APK connector with validated parameters.",
      };
      return {
        isError: false,
        content: [{ type: "text", text: JSON.stringify(delegation, null, 2) }],
      };
    }

    default:
      return {
        isError: true,
        content: [{ type: "text", text: `Unhandled MCP tool: ${toolName}` }],
      };
  }
}

function readMcpResource(uri, caller) {
  switch (uri) {
    case "mcp://gc2223.com/session/context":
      return {
        contents: [
          {
            uri,
            mimeType: "application/json",
            text: JSON.stringify(
              {
                virtualAlias: caller.virtualAlias,
                authLevel: caller.authLevel,
                tokenVerified: caller.tokenVerified,
                zeroPii: true,
                stateless: true,
              },
              null,
              2
            ),
          },
        ],
      };
    case "mcp://gc2223.com/security/policy":
      return {
        contents: [
          {
            uri,
            mimeType: "application/json",
            text: JSON.stringify(
              {
                zeroPii: true,
                persistedKvOrR2: false,
                allowedEnvSecrets: [],
                enforcedGuardrails: [
                  "in-memory-virtual-alias-binding",
                  "sliding-window-rate-limit",
                  "json-schema-argument-validation",
                  "ssrf-external-https-only",
                  "dlp-token-redaction",
                ],
              },
              null,
              2
            ),
          },
        ],
      };
    case "mcp://gc2223.com/connectors/catalog":
      return {
        contents: [
          {
            uri,
            mimeType: "application/json",
            text: JSON.stringify(
              {
                connectors: [
                  { id: "gcp_discovery", mode: "DELEGATE_TO_APK" },
                  { id: "github_oauth_device_flow", mode: "EPHEMERAL_RELAY_OR_DELEGATE" },
                  { id: "termux_local_cli", mode: "DELEGATE_TO_APK" },
                  { id: "stateless_mcp_edge", mode: "EDGE_STATELESS" },
                ],
              },
              null,
              2
            ),
          },
        ],
      };
    default:
      return null;
  }
}

async function handleJsonRpcMethod(rpcRequest, caller, request, fetchImpl) {
  const { id, method, params } = rpcRequest || {};
  if (typeof method !== "string" || !method.trim()) {
    return { status: 400, payload: rpcError(id, -32600, "Invalid JSON-RPC Request: missing method") };
  }

  switch (method) {
    case "initialize":
      return {
        status: 200,
        payload: rpcResult(id, {
          protocolVersion: PROTOCOL_VERSION,
          serverInfo: SERVER_INFO,
          capabilities: {
            tools: { listChanged: false },
            resources: { subscribe: false, listChanged: false },
          },
          statelessMeta: {
            zeroPii: true,
            virtualAlias: caller.virtualAlias,
            authLevel: caller.authLevel,
          },
        }),
      };

    case "notifications/initialized":
    case "ping":
      return { status: 200, payload: rpcResult(id, {}) };

    case "tools/list":
      return {
        status: 200,
        payload: rpcResult(id, {
          tools: MCP_TOOLS.map(({ name, description, inputSchema }) => ({
            name,
            description,
            inputSchema,
          })),
        }),
      };

    case "tools/call": {
      const toolName = typeof params?.name === "string" ? params.name.trim() : "";
      if (!toolName) {
        return { status: 400, payload: rpcError(id, -32602, "tools/call requires params.name") };
      }
      const result = await executeToolCall(toolName, params?.arguments || {}, caller, request, fetchImpl);
      return { status: 200, payload: rpcResult(id, result) };
    }

    case "resources/list":
      return { status: 200, payload: rpcResult(id, { resources: MCP_RESOURCES }) };

    case "resources/read": {
      const uri = typeof params?.uri === "string" ? params.uri.trim() : "";
      const res = readMcpResource(uri, caller);
      if (!res) {
        return { status: 404, payload: rpcError(id, -32002, `Resource not found: ${uri}`) };
      }
      return { status: 200, payload: rpcResult(id, res) };
    }

    default:
      return { status: 404, payload: rpcError(id, -32601, `Method not found: ${method}`) };
  }
}

// ============================================================================
// Worker Fetch Entrypoint
// ============================================================================

export async function handleRequest(request, env = {}, fetchImpl = fetch) {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: CORS_HEADERS });
  }

  const url = new URL(request.url);
  const path = url.pathname.replace(/\/+$/, "") || "/";

  if (request.method === "GET" && (path === "/health" || path === "/v1/health")) {
    return jsonResponse({
      ok: true,
      mode: "stateless-mcp-zero-pii",
      server: SERVER_INFO,
      protocolVersion: PROTOCOL_VERSION,
      zeroPii: true,
      routes: ["/mcp", "/v1/mcp/tools", "/v1/mcp/tools/call", "/v1/mcp/resources", "/v1/client-config"],
    });
  }

  if (request.method === "GET" && path === "/v1/client-config") {
    return jsonResponse({
      ok: true,
      mode: "stateless-mcp-zero-pii",
      statelessMcp: true,
      zeroPii: true,
      requiresClientOAuthDiscovery: true,
      mcpEndpoint: "/mcp",
      protocolVersion: PROTOCOL_VERSION,
      supportedAuthFlows: ["google-oauth-credential-manager", "github-oauth-device-flow"],
    });
  }

  // Validate caller headers strictly in memory (zero PII retention)
  const caller = await validateCallerHeadersInMemory(request);
  if (!caller.ok) {
    return jsonResponse({ ok: false, error: caller.error }, caller.status);
  }

  // Sliding-window rate limit keyed solely by non-PII alias bucket in isolate memory
  const limitPerMinute = Number(env?.MAX_REQUESTS_PER_MINUTE || 60);
  const rate = checkInMemoryRateLimit(`mcp:${caller.aliasHashPrefix}`, limitPerMinute);
  if (!rate.allowed) {
    return jsonResponse(
      rpcError(null, -32029, "Rate limit exceeded for stateless MCP session"),
      429,
      { "X-RateLimit-Remaining": "0", "Retry-After": "60" }
    );
  }
  const rateHeaders = {
    "X-RateLimit-Limit": String(rate.limit),
    "X-RateLimit-Remaining": String(rate.remaining),
    "X-Stateless-Auth-Level": caller.authLevel,
  };

  if (request.method === "GET" && (path === "/mcp" || path === "/v1/mcp")) {
    return jsonResponse(
      {
        ok: true,
        protocolVersion: PROTOCOL_VERSION,
        serverInfo: SERVER_INFO,
        capabilities: { tools: true, resources: true },
        tools: MCP_TOOLS.map(({ name, description, executionMode }) => ({
          name,
          description,
          executionMode,
        })),
        resources: MCP_RESOURCES,
        caller: {
          virtualAlias: caller.virtualAlias,
          authLevel: caller.authLevel,
        },
      },
      200,
      rateHeaders
    );
  }

  if (request.method === "GET" && path === "/v1/mcp/tools") {
    const out = await handleJsonRpcMethod({ jsonrpc: "2.0", id: "rest-tools", method: "tools/list" }, caller, request, fetchImpl);
    return jsonResponse(out.payload, out.status, rateHeaders);
  }

  if (request.method === "GET" && path === "/v1/mcp/resources") {
    const out = await handleJsonRpcMethod({ jsonrpc: "2.0", id: "rest-res", method: "resources/list" }, caller, request, fetchImpl);
    return jsonResponse(out.payload, out.status, rateHeaders);
  }

  if (request.method === "POST" && path === "/v1/mcp/tools/call") {
    let body;
    try {
      body = await request.json();
    } catch {
      return jsonResponse(rpcError(null, -32700, "Parse error: invalid JSON"), 400, rateHeaders);
    }
    const out = await handleJsonRpcMethod(
      { jsonrpc: "2.0", id: body?.id || "rest-call", method: "tools/call", params: body },
      caller,
      request,
      fetchImpl
    );
    return jsonResponse(out.payload, out.status, rateHeaders);
  }

  if (request.method === "POST" && path === "/v1/mcp/resources/read") {
    let body;
    try {
      body = await request.json();
    } catch {
      return jsonResponse(rpcError(null, -32700, "Parse error: invalid JSON"), 400, rateHeaders);
    }
    const out = await handleJsonRpcMethod(
      { jsonrpc: "2.0", id: body?.id || "rest-read", method: "resources/read", params: body },
      caller,
      request,
      fetchImpl
    );
    return jsonResponse(out.payload, out.status, rateHeaders);
  }

  if (
    request.method === "POST" &&
    (path === "/mcp" || path === "/v1/mcp" || path === "/v1/mcp/rpc")
  ) {
    let rpcPayload;
    try {
      rpcPayload = await request.json();
    } catch {
      return jsonResponse(rpcError(null, -32700, "Parse error: invalid JSON"), 400, rateHeaders);
    }
    if (!rpcPayload || typeof rpcPayload !== "object" || Array.isArray(rpcPayload)) {
      return jsonResponse(rpcError(null, -32600, "Invalid Request: expected JSON-RPC object"), 400, rateHeaders);
    }
    const out = await handleJsonRpcMethod(rpcPayload, caller, request, fetchImpl);
    return jsonResponse(out.payload, out.status, rateHeaders);
  }

  return jsonResponse({ ok: false, error: "Not Found", path }, 404, rateHeaders);
}

export default {
  async fetch(request, env) {
    return handleRequest(request, env, fetch);
  },
};
