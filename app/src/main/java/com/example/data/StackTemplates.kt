package com.example.data

import com.example.model.StackTemplate

object StackTemplates {
    val all: List<StackTemplate> = listOf(
        StackTemplate(
            id = "cf_worker_gc2223",
            title = "gc2223.com Cloudflare Worker (SSO + Cost Alert + LLM)",
            category = "WORKER",
            targetHost = "gc2223.com",
            subtitle = "Google/Firebase JWT SSO 検証、Workers AI、および $0.10/日超過メール通知 (/v1/cost-alert) を備えたゲートウェイ",
            freeTierSummary = "Cloudflare Workers 100k req/day 無料 ・ Workers AI / Resend 無料枠対応",
            content = """// src/worker.js — Deploy to gc2223.com via Cloudflare Workers
export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);

    // 1. Public health endpoint for Android / Termux checks
    if (url.pathname === "/health") {
      return Response.json({
        ok: true,
        service: "AntiGravityAgent Gateway",
        host: url.hostname,
        timestamp: new Date().toISOString()
      });
    }

    // 2. Termux one-line installer script endpoint
    if (url.pathname === "/install.sh") {
      return new Response(TERMUX_INSTALL_SCRIPT(url.origin), {
        headers: { "content-type": "text/plain; charset=utf-8" }
      });
    }

    // 3. Seamless SSO + Timing-Safe Bearer Auth Guard
    const auth = request.headers.get("Authorization") || "";
    const fbToken = request.headers.get("X-Firebase-Token") || "";
    const bearerOk = Boolean(env.AGENT_BEARER_TOKEN && timingSafeEqualStrings(auth, `Bearer ${'$'}{env.AGENT_BEARER_TOKEN}`));
    const ssoUser = fbToken ? await verifyFirebaseToken(fbToken, env.FIREBASE_PROJECT_ID, env.ALLOWED_FIREBASE_EMAIL) : null;

    if (!bearerOk && !ssoUser) {
      return Response.json({ error: "Unauthorized (Bearer or verified Google SSO token required)" }, { status: 401 });
    }

    // 4. LLM Chat endpoint (/v1/chat)
    if (url.pathname === "/v1/chat" && request.method === "POST") {
      const { prompt, mode = "cli" } = await request.json();
      if (!prompt) return Response.json({ error: "Missing prompt" }, { status: 400 });

      const systemPrompt = mode === "cli"
        ? "You are AntiGravityAgent, a concise CLI assistant for Termux, Cloudflare, GitHub, Firebase, and GCP."
        : "You are AntiGravityAgent cloud orchestrator.";

      const aiResp = await env.AI.run("@cf/meta/llama-3.1-8b-instruct-fast", {
        messages: [
          { role: "system", content: systemPrompt },
          { role: "user", content: prompt }
        ]
      });

      return Response.json({
        reply: aiResp.response,
        user: ssoUser?.email || "bearer-client",
        provider: "cloudflare-workers-ai",
        costUsd: 0
      });
    }

    // 5. Daily Cost Threshold Email Alert (/v1/cost-alert)
    if (url.pathname === "/v1/cost-alert" && request.method === "POST") {
      const { recipientEmail = "17crown2223@gmail.com", dateKey, totalUsd, thresholdUsd = 0.10, breakdown = {} } = await request.json();
      if (!env.RESEND_API_KEY) {
        return Response.json({ error: "RESEND_API_KEY secret is not configured on Worker" }, { status: 501 });
      }
      const mailRes = await fetch("https://api.resend.com/emails", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${'$'}{env.RESEND_API_KEY}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          from: env.ALERT_FROM_EMAIL || "alerts@gc2223.com",
          to: [recipientEmail],
          subject: `[AntiGravity Cost Alert] ${'$'}{dateKey}: ${'$'}${'$'}{Number(totalUsd).toFixed(4)} exceeded ${'$'}${'$'}{Number(thresholdUsd).toFixed(2)}/day`,
          text: `Daily cost alert for 17crown2223@gmail.com\nDate: ${'$'}{dateKey}\nTotal: $${'$'}{Number(totalUsd).toFixed(4)}\nThreshold: $${'$'}{Number(thresholdUsd).toFixed(2)}/day\nBreakdown: ${'$'}{JSON.stringify(breakdown, null, 2)}`
        })
      });
      return Response.json({ sent: mailRes.ok, status: mailRes.status });
    }

    // 6. GitHub Actions remote dispatch (/v1/dispatch-github)
    if (url.pathname === "/v1/dispatch-github" && request.method === "POST") {
      const { repo, workflow = "antigravity-deploy.yml", ref = "main" } = await request.json();
      const ghRes = await fetch(
        `https://api.github.com/repos/${'$'}{repo}/actions/workflows/${'$'}{workflow}/dispatches`,
        {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${'$'}{env.GITHUB_PAT}`,
            "Accept": "application/vnd.github+json",
            "X-GitHub-Api-Version": "2022-11-28",
            "User-Agent": "AntiGravityAgent-Worker"
          },
          body: JSON.stringify({ ref })
        }
      );
      return Response.json({ dispatched: ghRes.status === 204, status: ghRes.status });
    }

    return Response.json({ error: "Not found" }, { status: 404 });
  }
};

function timingSafeEqualStrings(a, b) {
  const enc = new TextEncoder();
  const aBuf = enc.encode(String(a || ""));
  const bBuf = enc.encode(String(b || ""));
  if (aBuf.byteLength !== bBuf.byteLength) {
    crypto.subtle.timingSafeEqual(aBuf, aBuf);
    return false;
  }
  return crypto.subtle.timingSafeEqual(aBuf, bBuf);
}

let cachedFirebaseJwks = null;
let cachedFirebaseJwksExpMs = 0;

async function fetchFirebasePublicJwks() {
  if (cachedFirebaseJwks && Date.now() < cachedFirebaseJwksExpMs) {
    return cachedFirebaseJwks;
  }
  const res = await fetch("https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com");
  if (!res.ok) return cachedFirebaseJwks || [];
  const cc = res.headers.get("Cache-Control") || "";
  const maxAge = Number((cc.match(/max-age=(\d+)/i) || [])[1] || 3600);
  const data = await res.json();
  cachedFirebaseJwks = Array.isArray(data.keys) ? data.keys : [];
  cachedFirebaseJwksExpMs = Date.now() + maxAge * 1000;
  return cachedFirebaseJwks;
}

function base64UrlToUint8Array(b64u) {
  const b64 = b64u.replace(/-/g, "+").replace(/_/g, "/") + "===".slice((b64u.length + 3) % 4);
  const bin = atob(b64);
  const bytes = new Uint8Array(bin.length);
  for (let i = 0; i < bin.length; i++) bytes[i] = bin.charCodeAt(i);
  return bytes;
}

async function verifyFirebaseToken(idToken, expectedProject, allowedEmail = "17crown2223@gmail.com") {
  try {
    if (!expectedProject) return null;
    const parts = idToken.split(".");
    if (parts.length !== 3) return null;
    const header = JSON.parse(new TextDecoder().decode(base64UrlToUint8Array(parts[0])));
    if (header.alg !== "RS256" || !header.kid) return null;
    const payload = JSON.parse(new TextDecoder().decode(base64UrlToUint8Array(parts[1])));
    const nowSec = Math.floor(Date.now() / 1000);
    if (payload.aud !== expectedProject) return null;
    if (payload.iss !== `https://securetoken.google.com/${'$'}{expectedProject}`) return null;
    if (typeof payload.sub !== "string" || payload.sub.length === 0 || payload.sub.length > 128) return null;
    if (typeof payload.iat !== "number" || payload.iat > nowSec + 300) return null;
    if (typeof payload.exp !== "number" || payload.exp <= nowSec) return null;
    if (allowedEmail && (payload.email !== allowedEmail || payload.email_verified !== true)) return null;

    const jwks = await fetchFirebasePublicJwks();
    const jwk = jwks.find(k => k.kid === header.kid);
    if (!jwk) return null;
    const pubKey = await crypto.subtle.importKey("jwk", jwk, { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" }, false, ["verify"]);
    const signedData = new TextEncoder().encode(`${'$'}{parts[0]}.${'$'}{parts[1]}`);
    const sig = base64UrlToUint8Array(parts[2]);
    const valid = await crypto.subtle.verify("RSASSA-PKCS1-v1_5", pubKey, sig, signedData);
    return valid ? payload : null;
  } catch {
    return null;
  }
}

function TERMUX_INSTALL_SCRIPT(origin) {
  return `#!/data/data/com.termux/files/usr/bin/bash
set -e
mkdir -p "${'$'}HOME/.local/bin"
cat > "${'$'}HOME/.local/bin/antigravity" << 'EOF'
#!/data/data/com.termux/files/usr/bin/bash
GATEWAY="${'$'}{ANTIGRAVITY_URL:-${'$'}{origin}}"
TOKEN="${'$'}{ANTIGRAVITY_TOKEN:-}"
PROMPT="${'$'}*"
if [ -z "${'$'}PROMPT" ]; then echo "Usage: antigravity <prompt>"; exit 1; fi
curl -sS -X POST "${'$'}GATEWAY/v1/chat" \\
  -H "Authorization: Bearer ${'$'}TOKEN" \\
  -H "Content-Type: application/json" \\
  -d "${'$'}(jq -n --arg p "${'$'}PROMPT" '{prompt:${'$'}p,mode:"cli"}')" | jq -r '.reply // .'
EOF
chmod +x "${'$'}HOME/.local/bin/antigravity"
echo "Installed AntiGravityAgent CLI to ~/.local/bin/antigravity"
`;
}"""
        ),
        StackTemplate(
            id = "wrangler_toml_gc2223",
            title = "wrangler.toml (gc2223.com ルーティング設定)",
            category = "WORKER",
            targetHost = "gc2223.com",
            subtitle = "gc2223.com カスタムドメイン・Workers AI・Firebase SSO プロジェクト設定",
            freeTierSummary = "Custom Domain 紐付け無料 ・ Workers AI Binding 込み",
            content = """name = "antigravity-gateway"
main = "src/worker.js"
compatibility_date = "2026-01-01"
compatibility_flags = ["nodejs_compat"]
workers_dev = false

routes = [
  { pattern = "gc2223.com/*", zone_name = "gc2223.com" }
]

[vars]
FIREBASE_PROJECT_ID = "gen-lang-client-0439741065"
ALERT_FROM_EMAIL = "alerts@gc2223.com"

[ai]
binding = "AI"

[triggers]
crons = ["0 */6 * * *"]

# Set secrets via CLI (never commit to git):
# npx wrangler secret put AGENT_BEARER_TOKEN
# npx wrangler secret put GITHUB_PAT
# npx wrangler secret put RESEND_API_KEY
"""
        ),
        StackTemplate(
            id = "termux_antigravity_cli",
            title = "Termux AntiGravityAgent CLI + /status & 安全な /run 実行ブリッジ",
            category = "TERMUX",
            targetHost = "Termux (Android)",
            subtitle = "127.0.0.1:8787 で /status 同期とホワイトリスト制 POST /run を提供し、pull-task でバッテリー/回線ヘッダ付き Lease 実行に対応",
            freeTierSummary = "端末ローカル実行（¥0）＋ 双方向実行・Self-Heal 連携",
            content = """#!/data/data/com.termux/files/usr/bin/bash
# Run in Termux on Android
pkg update -y && pkg install -y curl jq git python

mkdir -p "${'$'}HOME/.local/bin" "${'$'}HOME/.config/antigravity"
export PATH="${'$'}HOME/.local/bin:${'$'}PATH"

cat > "${'$'}HOME/.local/bin/antigravity-status-server.py" << 'PYEOF'
#!/usr/bin/env python3
import http.server, json, os, subprocess, sys, time

PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 8787
STATE_FILE = os.path.expanduser("~/.config/antigravity/state.json")
TOKEN = os.environ.get("ANTIGRAVITY_LOCAL_TOKEN", "").strip()
HOME_DIR = os.path.realpath(os.path.expanduser("~"))
START_TIME = time.time()

ALLOWED_PRESETS = {
    "antigravity_health": [os.path.expanduser("~/.local/bin/antigravity"), "health"],
    "git_pull": ["git", "pull", "--ff-only"],
    "tf_plan": ["terraform", "-chdir=infra", "plan", "-no-color", "-input=false"],
    "tf_apply": ["terraform", "-chdir=infra", "apply", "-no-color", "-input=false", "-auto-approve"],
    "wrangler_deploy": ["npx", "wrangler", "deploy"],
}

def read_state():
    data = {}
    if os.path.exists(STATE_FILE):
        try:
            with open(STATE_FILE, "r", encoding="utf-8") as f:
                data = json.load(f)
        except Exception:
            data = {}
    return {
        "agentName": "AntiGravityAgent",
        "version": "1.4.0",
        "state": data.get("state", "IDLE"),
        "pid": os.getpid(),
        "activeTask": data.get("activeTask", ""),
        "lastCommand": data.get("lastCommand", ""),
        "lastExitCode": data.get("lastExitCode", 0),
        "lastRunAt": data.get("lastRunAt", ""),
        "requestsToday": int(data.get("requestsToday", 0)),
        "uptimeSeconds": int(time.time() - START_TIME),
        "timestamp": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
    }

def write_state(state, active_task, last_cmd, exit_code):
    cur = read_state()
    payload = {
        "state": state,
        "activeTask": active_task,
        "lastCommand": last_cmd,
        "lastExitCode": exit_code,
        "lastRunAt": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        "requestsToday": cur.get("requestsToday", 0) + 1,
    }
    os.makedirs(os.path.dirname(STATE_FILE), exist_ok=True)
    with open(STATE_FILE, "w", encoding="utf-8") as f:
        json.dump(payload, f)

class Handler(http.server.BaseHTTPRequestHandler):
    def _authorized(self):
        if TOKEN and self.headers.get("Authorization", "") != f"Bearer {TOKEN}":
            self.send_response(401)
            self.end_headers()
            self.wfile.write(b'{"error":"Unauthorized"}')
            return False
        return True

    def _send_json(self, code, obj):
        body = json.dumps(obj).encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        if not self._authorized():
            return
        if self.path.split("?")[0] in ("/status", "/health"):
            self._send_json(200, read_state())
        else:
            self._send_json(404, {"error": "Not found"})

    def do_POST(self):
        if not self._authorized():
            return
        if self.path.split("?")[0] != "/run":
            self._send_json(404, {"error": "Not found"})
            return
        length = int(self.headers.get("Content-Length", "0") or 0)
        raw = self.rfile.read(min(length, 16384)).decode("utf-8", errors="replace")
        try:
            req = json.loads(raw or "{}")
        except Exception:
            self._send_json(400, {"error": "Invalid JSON"})
            return
        preset = str(req.get("preset", "")).strip()
        if preset not in ALLOWED_PRESETS:
            self._send_json(403, {"error": f"Preset '{preset}' is not whitelisted"})
            return
        raw_dir = str(req.get("workdir", "")).strip()
        cwd = os.path.realpath(os.path.expanduser(raw_dir)) if raw_dir else HOME_DIR
        if not (cwd == HOME_DIR or cwd.startswith(HOME_DIR + os.sep)) or not os.path.isdir(cwd):
            cwd = HOME_DIR
        cmd = ALLOWED_PRESETS[preset]
        write_state("RUNNING", preset, " ".join(cmd), 0)
        t0 = time.time()
        try:
            proc = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True, timeout=180)
            dur_ms = int((time.time() - t0) * 1000)
            final_state = "IDLE" if proc.returncode == 0 else "ERROR"
            write_state(final_state, "", " ".join(cmd), proc.returncode)
            self._send_json(200, {
                "ok": proc.returncode == 0,
                "preset": preset,
                "exitCode": proc.returncode,
                "stdout": (proc.stdout or "")[-4000:],
                "stderr": (proc.stderr or "")[-4000:],
                "durationMs": dur_ms,
            })
        except Exception as exc:
            dur_ms = int((time.time() - t0) * 1000)
            write_state("ERROR", "", " ".join(cmd), 1)
            self._send_json(500, {"ok": False, "preset": preset, "exitCode": 1, "stderr": str(exc), "durationMs": dur_ms})

    def log_message(self, format, *args):
        pass

http.server.HTTPServer(("127.0.0.1", PORT), Handler).serve_forever()
PYEOF

cat > "${'$'}HOME/.local/bin/antigravity" << 'EOF'
#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail
CONFIG="${'$'}HOME/.config/antigravity/env"
STATE_FILE="${'$'}HOME/.config/antigravity/state.json"
[ -f "${'$'}CONFIG" ] && source "${'$'}CONFIG"

GATEWAY="${'$'}{ANTIGRAVITY_URL:-https://gc2223.com}"
TOKEN="${'$'}{ANTIGRAVITY_TOKEN:-}"

record_state() {
  local st="${'$'}1" active="${'$'}2" cmd="${'$'}3" code="${'$'}4"
  local ts prev_count
  ts="${'$'}(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  prev_count=0
  if [ -f "${'$'}STATE_FILE" ]; then
    prev_count="${'$'}(jq -r '.requestsToday // 0' "${'$'}STATE_FILE" 2>/dev/null || echo 0)"
  fi
  jq -n --arg s "${'$'}st" --arg a "${'$'}active" --arg c "${'$'}cmd" --argjson e "${'$'}code" --arg t "${'$'}ts" --argjson n "${'$'}((prev_count + 1))" \
    '{state:${'$'}s,activeTask:${'$'}a,lastCommand:${'$'}c,lastExitCode:${'$'}e,lastRunAt:${'$'}t,requestsToday:${'$'}n}' > "${'$'}STATE_FILE"
}

case "${'$'}{1:-}" in
  serve)
    PORT="${'$'}{2:-8787}"
    echo "AntiGravityAgent status & /run bridge listening on http://127.0.0.1:${'$'}{PORT}"
    exec python3 "${'$'}HOME/.local/bin/antigravity-status-server.py" "${'$'}PORT"
    ;;
  health)
    record_state "RUNNING" "health-check" "antigravity health" 0
    curl -sS "${'$'}GATEWAY/health" | jq .
    record_state "IDLE" "" "antigravity health" 0
    ;;
  dispatch)
    REPO="${'$'}{2:?Usage: antigravity dispatch owner/repo [workflow.yml]}"
    WF="${'$'}{3:-antigravity-deploy.yml}"
    record_state "RUNNING" "dispatch:${'$'}REPO" "antigravity dispatch ${'$'}REPO" 0
    curl -sS -X POST "${'$'}GATEWAY/v1/dispatch-github" \
      -H "Authorization: Bearer ${'$'}TOKEN" \
      -H "Content-Type: application/json" \
      -d "${'$'}(jq -n --arg r "${'$'}REPO" --arg w "${'$'}WF" '{repo:${'$'}r,workflow:${'$'}w}')" | jq .
    record_state "IDLE" "" "antigravity dispatch ${'$'}REPO" 0
    ;;
  tf)
    ACTION="${'$'}{2:-plan}"
    ENV_NAME="${'$'}{3:-dev}"
    record_state "RUNNING" "tf:${'$'}ACTION" "antigravity tf ${'$'}ACTION ${'$'}ENV_NAME" 0
    if [ -d "./infra" ] && command -v terraform >/dev/null 2>&1; then
      terraform -chdir=infra init -input=false && terraform -chdir=infra "${'$'}ACTION" -no-color
    else
      echo "Remote Terraform dispatch via GitHub Actions (${'$'}ACTION / ${'$'}ENV_NAME):"
      echo "Use Hub App or: gh workflow run antigravity-terraform.yml -f tf_action=${'$'}ACTION -f target_env=${'$'}ENV_NAME"
    fi
    record_state "IDLE" "" "antigravity tf ${'$'}ACTION" 0
    ;;
  heal)
    LOG_FILE="${'$'}{2:?Usage: antigravity heal <error_log_file> [owner/repo]}"
    REPO="${'$'}{3:-17crown1901mituru/OrchestraAntiGravity}"
    LOG_TAIL="${'$'}(tail -n 80 "${'$'}LOG_FILE")"
    record_state "RUNNING" "self-heal" "antigravity heal ${'$'}LOG_FILE" 0
    curl -sS -X POST "${'$'}GATEWAY/v1/workflow-error" \
      -H "Authorization: Bearer ${'$'}TOKEN" \
      -H "Content-Type: application/json" \
      -d "${'$'}(jq -n --arg r "${'$'}REPO" --arg l "${'$'}LOG_TAIL" '{repo:${'$'}r,workflow:"local-termux",runId:0,retryCount:0,errorLog:${'$'}l}')" | jq .
    record_state "IDLE" "" "antigravity heal" 0
    ;;
  crawl)
    TARGET="${'$'}{2:?Usage: antigravity crawl <url_or_domain>}"
    record_state "RUNNING" "crawl:${'$'}TARGET" "antigravity crawl ${'$'}TARGET" 0
    curl -sS -X POST "${'$'}GATEWAY/v1/chat" \
      -H "Authorization: Bearer ${'$'}TOKEN" \
      -H "Content-Type: application/json" \
      -d "${'$'}(jq -n --arg p "Analyze and summarize solution architecture for ${'$'}TARGET" '{prompt:${'$'}p,mode:"cli"}')" | jq -r '.reply // .'
    record_state "IDLE" "" "antigravity crawl" 0
    ;;
  pull-task)
    LOCAL_PORT="${'$'}{2:-8787}"
    NODE_ID="${'$'}{ANTIGRAVITY_NODE_ID:-android-termux-primary}"
    NODE_CAPS="${'$'}{ANTIGRAVITY_NODE_CAPS:-termux,android_sms}"
    BAT_PCT=100
    BAT_TEMP=28.0
    CHARGING="true"
    if command -v termux-battery-status >/dev/null 2>&1; then
      BAT_JSON="${'$'}(termux-battery-status 2>/dev/null || echo '{}')"
      BAT_PCT="${'$'}(echo "${'$'}BAT_JSON" | jq -r '.percentage // 100')"
      BAT_TEMP="${'$'}(echo "${'$'}BAT_JSON" | jq -r '.temperature // 28.0')"
      BAT_ST="${'$'}(echo "${'$'}BAT_JSON" | jq -r '.status // "CHARGING"')"
      [ "${'$'}BAT_ST" = "CHARGING" ] || [ "${'$'}BAT_ST" = "FULL" ] && CHARGING="true" || CHARGING="false"
    fi
    TASK_JSON="${'$'}(curl -sS -H "Authorization: Bearer ${'$'}TOKEN" -H "X-Node-Id: ${'$'}NODE_ID" -H "X-Node-Capabilities: ${'$'}NODE_CAPS" -H "X-Device-Battery: ${'$'}BAT_PCT" -H "X-Device-Temp: ${'$'}BAT_TEMP" -H "X-Device-Charging: ${'$'}CHARGING" -H "X-Device-Network: termux" -H "X-Termux-Version: 1.5.0" "${'$'}GATEWAY/v1/termux/next-task?nodeId=${'$'}NODE_ID")"
    POLL_SEC="${'$'}(echo "${'$'}TASK_JSON" | jq -r '.recommendedPollSec // 60')"
    HAS_TASK="${'$'}(echo "${'$'}TASK_JSON" | jq -r '.hasTask // false')"
    if [ "${'$'}HAS_TASK" != "true" ]; then
      echo "No executable Termux task (${'$'}(echo "${'$'}TASK_JSON" | jq -r '.deferredReason // "queue empty"')). Next adaptive poll: ${'$'}{POLL_SEC}s"
      exit 0
    fi
    PRESET="${'$'}(echo "${'$'}TASK_JSON" | jq -r '.task.preset')"
    WORKDIR="${'$'}(echo "${'$'}TASK_JSON" | jq -r '.task.workdir // ""')"
    TRACE_ID="${'$'}(echo "${'$'}TASK_JSON" | jq -r '.task.traceId // ""')"
    echo "Executing leased preset '${'$'}PRESET' on node '${'$'}NODE_ID' (trace=${'$'}TRACE_ID, temp=${'$'}BAT_TEMP°C) via local bridge..."
    RUN_RES="${'$'}(curl -sS -X POST "http://127.0.0.1:${'$'}{LOCAL_PORT}/run" \
      -H "Content-Type: application/json" \
      ${'$'}{ANTIGRAVITY_LOCAL_TOKEN:+-H "Authorization: Bearer ${'$'}ANTIGRAVITY_LOCAL_TOKEN"} \
      -d "${'$'}(jq -n --arg p "${'$'}PRESET" --arg w "${'$'}WORKDIR" '{preset:${'$'}p,workdir:${'$'}w}')")"
    ACK_PAYLOAD="${'$'}(echo "${'$'}RUN_RES" | jq --arg tid "${'$'}TRACE_ID" --arg nid "${'$'}NODE_ID" '. + {traceId: ${'$'}tid, nodeId: ${'$'}nid}')"
    curl -sS -X POST "${'$'}GATEWAY/v1/termux/ack" \
      -H "Authorization: Bearer ${'$'}TOKEN" \
      -H "Content-Type: application/json" \
      -d "${'$'}ACK_PAYLOAD" >/dev/null
    echo "${'$'}ACK_PAYLOAD" | jq --argjson poll "${'$'}POLL_SEC" '. + {recommendedPollSec: ${'$'}poll}'
    ;;
  register-node)
    NODE_ID="${'$'}{2:-${'$'}{ANTIGRAVITY_NODE_ID:-android-termux-primary}}"
    NODE_TYPE="${'$'}{3:-termux}"
    CAPS_CSV="${'$'}{4:-${'$'}{ANTIGRAVITY_NODE_CAPS:-termux,android_sms}}"
    CAPS_JSON="${'$'}(echo "${'$'}CAPS_CSV" | jq -Rc 'split(",")')"
    curl -sS -X POST "${'$'}GATEWAY/v1/nodes/register" \
      -H "Authorization: Bearer ${'$'}TOKEN" \
      -H "Content-Type: application/json" \
      -d "${'$'}(jq -n --arg id "${'$'}NODE_ID" --arg t "${'$'}NODE_TYPE" --argjson c "${'$'}CAPS_JSON" '{nodeId:${'$'}id,nodeType:${'$'}t,capabilities:${'$'}c}')" | jq .
    ;;
  "")
    echo "AntiGravityAgent CLI v1.5"
    echo "  antigravity serve [port]                      # Start /status + whitelisted /run bridge (default 8787)"
    echo "  antigravity health"
    echo "  antigravity dispatch owner/repo [workflow.yml]"
    echo "  antigravity tf <plan|apply> [env]             # Run local or print remote Terraform dispatch"
    echo "  antigravity heal <logfile> [repo]             # Send error log to Email Self-Heal Worker"
    echo "  antigravity crawl <url>                       # Summarize URL/domain solution via Gateway"
    echo "  antigravity pull-task [local_port]            # Pull & run Email-queued safe task on federated node"
    echo "  antigravity register-node [id] [type] [caps]  # Register federated node (e.g. home-pc docker heavy_build)"
    echo "  antigravity <natural language prompt>"
    ;;
  *)
    PROMPT="${'$'}*"
    record_state "RUNNING" "llm-prompt" "antigravity ${'$'}{PROMPT:0:40}" 0
    if curl -sS -X POST "${'$'}GATEWAY/v1/chat" \
      -H "Authorization: Bearer ${'$'}TOKEN" \
      -H "Content-Type: application/json" \
      -d "${'$'}(jq -n --arg p "${'$'}PROMPT" '{prompt:${'$'}p,mode:"cli"}')" | jq -r '.reply // .'; then
      record_state "IDLE" "" "antigravity ${'$'}{PROMPT:0:40}" 0
    else
      record_state "ERROR" "" "antigravity ${'$'}{PROMPT:0:40}" 1
      exit 1
    fi
    ;;
esac
EOF

chmod +x "${'$'}HOME/.local/bin/antigravity" "${'$'}HOME/.local/bin/antigravity-status-server.py"
grep -q 'HOME/.local/bin' "${'$'}HOME/.bashrc" 2>/dev/null || echo 'export PATH="${'$'}HOME/.local/bin:${'$'}PATH"' >> "${'$'}HOME/.bashrc"

cat > "${'$'}HOME/.config/antigravity/env" << 'EOF'
ANTIGRAVITY_URL="https://gc2223.com"
ANTIGRAVITY_TOKEN="YOUR_BEARER_TOKEN_HERE"
# Optional token matching Secure Settings -> Termux Local Bearer Token:
# ANTIGRAVITY_LOCAL_TOKEN=""
EOF

echo "Done! Run 'antigravity serve' in Termux for /status sync + whitelisted /run bridge."
"""
        ),
        StackTemplate(
            id = "github_actions_deploy",
            title = "GitHub Actions 自動デプロイ (gc2223.com)",
            category = "GITHUB",
            targetHost = "GitHub Actions",
            subtitle = "push 時または Android / Termux からの workflow_dispatch で gc2223.com Worker を自動更新",
            freeTierSummary = "Public Repo 無制限無料 ・ Private Repo 月 2,000 分無料",
            content = """name: Deploy AntiGravity Gateway to gc2223.com

on:
  push:
    branches: [main]
  workflow_dispatch:
    inputs:
      triggered_by:
        description: "Trigger source (android / termux)"
        required: false
        default: "manual"

permissions: {}

concurrency:
  group: deploy-worker-${'$'}{{ github.ref }}
  cancel-in-progress: false

jobs:
  deploy-cloudflare-worker:
    runs-on: ubuntu-latest
    timeout-minutes: 10
    permissions:
      contents: read
    steps:
      - uses: actions/checkout@v4
      - uses: cloudflare/wrangler-action@v3
        with:
          apiToken: ${'$'}{{ secrets.CLOUDFLARE_API_TOKEN }}
          accountId: ${'$'}{{ secrets.CLOUDFLARE_ACCOUNT_ID }}
"""
        ),
        StackTemplate(
            id = "gcp_zero_cost_guard",
            title = "GCP Always Free + $0.10/日 ($3/月) + Cloud Quotas & TLS 1.2+ ガード",
            category = "GCP",
            targetHost = "Google Cloud Platform",
            subtitle = "17crown2223@gmail.com へ自動メール通知する GCP 予算アラート・Cloud Quotas 監視・TLS 1.2+ / WIF 最少権限デプロイ",
            freeTierSummary = "Cloud Run 月 200万 req 無料 ・ Workflows 月 5,000 ステップ無料 ・ Cloud Quotas 監視",
            content = """# 1. Set your GCP project (linked to 17crown2223@gmail.com)
export PROJECT_ID="gen-lang-client-0439741065"
gcloud config set project "${'$'}PROJECT_ID"

# 2. Enable free-tier-friendly APIs, Cloud Quotas & Budget Notifications
gcloud services enable \
  run.googleapis.com \
  workflows.googleapis.com \
  billingbudgets.googleapis.com \
  monitoring.googleapis.com \
  pubsub.googleapis.com \
  cloudquotas.googleapis.com

# 3. Inspect active service quotas (prevent runaway free-tier exhaustion)
gcloud quotas info list --service=run.googleapis.com --project="${'$'}PROJECT_ID" --limit=10

# 4. Create email notification channel for 17crown2223@gmail.com
gcloud beta monitoring channels create \
  --display-name="AntiGravity Cost Alert Email" \
  --type=email \
  --channel-labels=email_address=17crown2223@gmail.com

# 5. Create $3.00/month ($0.10/day equivalent) budget alert
# Find Billing ID via: gcloud billing accounts list
gcloud billing budgets create \
  --billing-account="YOUR_BILLING_ACCOUNT_ID" \
  --display-name="AntiGravity $0.10/day ($3/mo) Guard" \
  --budget-amount=3USD \
  --threshold-rule=percent=0.1 \
  --threshold-rule=percent=0.5 \
  --threshold-rule=percent=1.0

# 6. Deploy Always-Free Cloud Run backend (min-instances=0 -> idle ¥0)
gcloud run deploy antigravity-core \
  --source . \
  --region us-central1 \
  --min-instances 0 \
  --max-instances 1 \
  --cpu 1 --memory 256Mi \
  --allow-unauthenticated

# 7. API Key & Quota Project Header Hygiene (never pass ?key= in URL queries):
# curl -H "x-goog-api-key: ${'$'}API_KEY" -H "x-goog-user-project: ${'$'}PROJECT_ID" ...
"""
        ),
        StackTemplate(
            id = "gcp_docs_well_architected",
            title = "Google Cloud Docs Best Practices 統合監査 (Well-Architected + OWASP 2025)",
            category = "GCP",
            targetHost = "https://cloud.google.com/docs",
            subtitle = "Well-Architected 6本の柱・OWASP Top 10:2025・Terraform vet・Org Policy TLS 1.2+・SA鍵禁止・CUD/Quota チェックリスト",
            freeTierSummary = "cloud.google.com/docs 再帰取得 .md 知見を IaC / CI / クライアントへ反映",
            content = """# Google Cloud Well-Architected & Security Hardening Checklist
# Derived from https://docs.cloud.google.com/docs recursive .md audit

## 1. Operational Excellence & IaC Policy Validation
- Remote encrypted state with Object Versioning & Lifecycle (GCS bucket or R2 S3 backend).
- Never store plaintext secrets in terraform.tfstate; mark secret variables `sensitive = true`.
- Gate `terraform apply` in CI with `terraform show -json tfplan > tfplan.json` + `gcloud beta terraform vet tfplan.json --policy-library=./policy-library` (fails on exit code 2).

## 2. Security, Privacy & OWASP Top 10:2025 Compliance
- Keyless CI/CD via Workload Identity Federation (`iam.disableServiceAccountKeyCreation = true`).
- Disable automatic default SA IAM grants (`iam.automaticIamGrantsForDefaultServiceAccounts = true`).
- Enforce TLS 1.2+ via `constraints/gcp.restrictTLSVersion` (deny TLS 1.0 / TLS 1.1) and OkHttp `ConnectionSpec.MODERN_TLS`.
- Protect Firebase/LLM endpoints with Firebase App Check + GenAI Model Armor / DLP redaction.
- Never place API keys in URL query strings; always use `x-goog-api-key` and `x-goog-user-project` HTTP headers.
- Secret Manager automatic 90-day rotation schedule (`rotation_period = "7776000s"`).

## 3. Reliability, Quotas & Endpoint Governance
- Enable `cloudquotas.googleapis.com` and Cloud Monitoring Consumer Quota alerts (`serviceruntime.googleapis.com/quota/allocation/usage >= 80%`).
- For strict residency / FedRAMP workloads: enforce `constraints/gcp.restrictEndpointUsage` + `AGY_CLI_DISABLE_AUTO_UPDATE=true` + regional endpoints.

## 4. Cost Optimization & Sustainability
- Scale-to-zero Cloud Run (`min_instance_count = 0`, `cpu_idle = true`) + $0.10/day ($3/mo) Budget Pub/Sub circuit breaker.
- Compare Spend-Based CUDs only after sustained baseline utilization; stay inside Always Free + Frugal AI routing first.

## 5. Multi-Platform Official Docs Alignment (Cloudflare / Firebase / GitHub / Terraform)
- Cloudflare Workers: Use `crypto.subtle.timingSafeEqual` for secret comparison; set `workers_dev = false` when serving exclusively via custom domain routes.
- Firebase Auth & App Check: Always verify `RS256` signature via `securetoken@system.gserviceaccount.com` JWK + `iss`/`aud`/`sub`/`iat`/`exp` claims on Worker; use Play Integrity API for Google Play production Android releases.
- GitHub REST & Actions: Send `X-GitHub-Api-Version: 2022-11-28` on all REST calls; restrict top-level workflow `permissions: {}`, define `concurrency`, and set `timeout-minutes`.
- Terraform Google v6+: Explicitly set `deletion_protection`, apply provider `default_labels`, protect stateful buckets/KV with `lifecycle { prevent_destroy }`, and avoid persisting secrets inside `tfstate`.
"""
        )
    ) + OrchestrationTemplates.all
}
