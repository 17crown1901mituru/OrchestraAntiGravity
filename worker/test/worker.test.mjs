import test from "node:test";
import assert from "node:assert/strict";
import crypto from "node:crypto";
import {
  handleRequest,
  deriveVirtualAliasFromSub,
  assertSafeExternalUrl,
  redactSensitiveText,
} from "../src/worker.js";

function makeUnsignedTestJwt(payloadObj) {
  const header = Buffer.from(JSON.stringify({ alg: "none", typ: "JWT" })).toString("base64url");
  const payload = Buffer.from(JSON.stringify(payloadObj)).toString("base64url");
  return `${header}.${payload}.testsig`;
}

test("GET /health and /v1/client-config report zero-PII stateless MCP mode", async () => {
  const healthRes = await handleRequest(new Request("https://gc2223.com/health"));
  assert.equal(healthRes.status, 200);
  const health = await healthRes.json();
  assert.equal(health.ok, true);
  assert.equal(health.zeroPii, true);
  assert.equal(health.mode, "stateless-mcp-zero-pii");

  const cfgRes = await handleRequest(new Request("https://gc2223.com/v1/client-config"));
  assert.equal(cfgRes.status, 200);
  const cfg = await cfgRes.json();
  assert.equal(cfg.statelessMcp, true);
  assert.equal("allowedEmail" in cfg, false);
  assert.equal("firebaseProjectId" in cfg, false);
});

test("POST /mcp verifies Google sub <-> X-Virtual-Alias binding in memory and delegates safely", async () => {
  const sub = "10887766554433221100";
  const alias = await deriveVirtualAliasFromSub(sub);
  assert.match(alias, /^u-[0-9a-f]{12}@gc2223\.com$/);

  const expectedHex = crypto.createHash("sha256").update(sub).digest("hex").slice(0, 12);
  assert.equal(alias, `u-${expectedHex}@gc2223.com`);

  const jwt = makeUnsignedTestJwt({
    sub,
    iss: "https://accounts.google.com",
    exp: Math.floor(Date.now() / 1000) + 3600,
    email: "private-user@example.com",
  });

  // 1. Initialize
  const initRes = await handleRequest(
    new Request("https://gc2223.com/mcp", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Virtual-Alias": alias,
        "X-Google-Id-Token": jwt,
      },
      body: JSON.stringify({ jsonrpc: "2.0", id: "1", method: "initialize", params: {} }),
    })
  );
  assert.equal(initRes.status, 200);
  const initJson = await initRes.json();
  assert.equal(initJson.result.statelessMeta.virtualAlias, alias);
  assert.equal(initJson.result.statelessMeta.zeroPii, true);
  assert.equal(JSON.stringify(initJson).includes("private-user@example.com"), false);

  // 2. Reject mismatched alias
  const mismatchRes = await handleRequest(
    new Request("https://gc2223.com/mcp", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Virtual-Alias": "u-000000000000@gc2223.com",
        "X-Google-Id-Token": jwt,
      },
      body: JSON.stringify({ jsonrpc: "2.0", id: "2", method: "tools/list", params: {} }),
    })
  );
  assert.equal(mismatchRes.status, 403);

  // 3. Tool delegation when X-Scoped-GitHub-Token omitted
  const callRes = await handleRequest(
    new Request("https://gc2223.com/mcp", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Virtual-Alias": alias,
        "X-Google-Id-Token": jwt,
      },
      body: JSON.stringify({
        jsonrpc: "2.0",
        id: "3",
        method: "tools/call",
        params: {
          name: "github_relay_dispatch_workflow",
          arguments: { repo: "acme/app", workflow: "build.yml", ref: "main" },
        },
      }),
    })
  );
  assert.equal(callRes.status, 200);
  const callJson = await callRes.json();
  assert.equal(callJson.result.isError, false);
  assert.ok(callJson.result.content[0].text.includes("DELEGATE_TO_APK"));
});

test("POST /mcp ephemerally relays X-Scoped-GitHub-Token once without leaking secret", async () => {
  let seenAuthHeader = "";
  let callCount = 0;
  const mockFetch = async (_url, init) => {
    seenAuthHeader = init.headers.Authorization;
    callCount += 1;
    if (callCount === 1) {
      return new Response(null, { status: 204 });
    }
    return new Response("Upstream error leaking ghp_secret1234567890abcd", { status: 422 });
  };

  const okRes = await handleRequest(
    new Request("https://gc2223.com/mcp", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Virtual-Alias": "u-abcdef123456@gc2223.com",
        "X-Scoped-GitHub-Token": "gho_ephemeral_token_999",
      },
      body: JSON.stringify({
        jsonrpc: "2.0",
        id: "relay-1",
        method: "tools/call",
        params: {
          name: "github_relay_dispatch_workflow",
          arguments: { repo: "acme/app", workflow: "ci.yml", ref: "main" },
        },
      }),
    }),
    {},
    mockFetch
  );
  assert.equal(okRes.status, 200);
  assert.equal(seenAuthHeader, "Bearer gho_ephemeral_token_999");
  const okPayload = await okRes.json();
  assert.equal(okPayload.result.isError, false);
  assert.ok(okPayload.result.content[0].text.includes("Dispatched"));

  const errRes = await handleRequest(
    new Request("https://gc2223.com/mcp", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Virtual-Alias": "u-abcdef123456@gc2223.com",
        "X-Scoped-GitHub-Token": "gho_ephemeral_token_999",
      },
      body: JSON.stringify({
        jsonrpc: "2.0",
        id: "relay-2",
        method: "tools/call",
        params: {
          name: "github_relay_dispatch_workflow",
          arguments: { repo: "acme/app", workflow: "ci.yml", ref: "main" },
        },
      }),
    }),
    {},
    mockFetch
  );
  assert.equal(errRes.status, 200);
  const errPayload = await errRes.json();
  assert.equal(errPayload.result.isError, true);
  const errText = errPayload.result.content[0].text;
  assert.equal(errText.includes("ghp_secret1234567890abcd"), false);
  assert.ok(errText.includes("ghp_[REDACTED]"));
});

test("Guardrails block SSRF internal hosts and redact secrets", () => {
  assert.throws(() => assertSafeExternalUrl("http://gc2223.com/mcp"));
  assert.throws(() => assertSafeExternalUrl("https://127.0.0.1:8787/run"));
  assert.throws(() => assertSafeExternalUrl("https://169.254.169.254/latest/meta-data"));
  assert.equal(assertSafeExternalUrl("https://api.github.com/user"), "https://api.github.com/user");

  const scrubbed = redactSensitiveText("Authorization: Bearer abc.def.ghi AIzaSyDummyKeyValue1234567890");
  assert.equal(scrubbed.includes("abc.def.ghi"), false);
  assert.equal(scrubbed.includes("AIzaSyDummyKeyValue1234567890"), false);
});
