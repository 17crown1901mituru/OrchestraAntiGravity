const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_17crown";
const BOB_UID = "bob_other";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validNodePayload(uid) {
  const now = new Date(Date.now() - 2000);
  return {
    userId: uid,
    provider: "CLOUDFLARE",
    title: "gc2223.com LLM Gateway",
    endpointOrTarget: "https://gc2223.com",
    accountHint: "17crown2223@gmail.com",
    status: "CONFIGURED",
    monthlyFreeQuota: "100k req/day Free",
    lastCheckedNote: "Ready for Wrangler deployment",
    createdAt: now,
    updatedAt: now,
  };
}

function validBlueprintPayload(uid) {
  const now = new Date(Date.now() - 2000);
  return {
    userId: uid,
    title: "AntiGravityAgent Termux CLI",
    category: "TERMUX",
    summary: "Termux CLI client connecting to gc2223.com",
    content: "#!/data/data/com.termux/files/usr/bin/bash\necho AntiGravityAgent",
    targetHost: "gc2223.com",
    status: "READY",
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated users cannot read or write nodes, blueprints, or cost collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("nodes").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("nodes").doc("cf_1").set(validNodePayload(ALICE_UID))
  );
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("blueprints").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("cost_entries").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("cost_config").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("cost_alerts").get());
});

test("Owner can list, create, partial update, and delete WorkflowNode; ghost fields and immutable mutations fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const nodesRef = aliceDb.collection("users").doc(ALICE_UID).collection("nodes");

  await assertSucceeds(nodesRef.get());
  await assertSucceeds(nodesRef.doc("cloudflare_gc2223").set(validNodePayload(ALICE_UID)));
  await assertSucceeds(
    nodesRef.doc("cloudflare_gc2223").update({
      status: "ACTIVE",
      lastCheckedNote: "HTTP 200 OK",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    nodesRef.doc("cloudflare_2").set({
      ...validNodePayload(ALICE_UID),
      id: "cloudflare_2",
    })
  );
  await assertFails(
    nodesRef.doc("cloudflare_gc2223").update({
      createdAt: new Date(Date.now() - 100),
      updatedAt: new Date(Date.now() - 100),
    })
  );
  await assertSucceeds(nodesRef.doc("cloudflare_gc2223").delete());
});

test("Cross-user isolation: Bob cannot read or modify Alice's nodes, blueprints, or cost entries", async () => {
  const now = new Date(Date.now() - 10000);
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("users").doc(ALICE_UID).collection("nodes").doc("cf_1").set(validNodePayload(ALICE_UID));
    await db.collection("users").doc(ALICE_UID).collection("blueprints").doc("bp_1").set(validBlueprintPayload(ALICE_UID));
    await db.collection("users").doc(ALICE_UID).collection("cost_entries").doc("ce_1").set({
      userId: ALICE_UID,
      provider: "GCP",
      dateKey: "2026-11-18",
      amountUsd: 0.05,
      usageSummary: "Cloud Run",
      source: "MANUAL",
      createdAt: now,
      updatedAt: now,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("nodes").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("blueprints").doc("bp_1").delete());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("cost_entries").get());
});

test("AgentBlueprint lifecycle: ARCHIVED status locks further updates", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bpRef = aliceDb.collection("users").doc(ALICE_UID).collection("blueprints").doc("bp_termux");

  await assertSucceeds(bpRef.set(validBlueprintPayload(ALICE_UID)));
  await assertSucceeds(
    bpRef.update({
      status: "ARCHIVED",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    bpRef.update({
      status: "READY",
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("Owner can manage CostConfig, CostEntry, and CostAlert with strict bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 2000);

  const cfgRef = aliceDb.collection("users").doc(ALICE_UID).collection("cost_config").doc("daily_budget");
  await assertSucceeds(
    cfgRef.set({
      userId: ALICE_UID,
      alertEmail: "17crown2223@gmail.com",
      dailyThresholdUsd: 0.1,
      autoEmailEnabled: true,
      webhookPath: "/v1/cost-alert",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    cfgRef.update({
      dailyThresholdUsd: 0.15,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    cfgRef.update({
      dailyThresholdUsd: 0,
      updatedAt: new Date(Date.now() - 200),
    })
  );

  const entryRef = aliceDb.collection("users").doc(ALICE_UID).collection("cost_entries").doc("ce_gcp_1");
  await assertSucceeds(
    entryRef.set({
      userId: ALICE_UID,
      provider: "GCP",
      dateKey: "2026-11-18",
      amountUsd: 0.12,
      usageSummary: "Cloud Run burst",
      source: "MANUAL",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("cost_entries").doc("ce_bad").set({
      userId: ALICE_UID,
      provider: "GCP",
      dateKey: "2026-11-18",
      amountUsd: -1,
      usageSummary: "Negative",
      source: "MANUAL",
      createdAt: now,
      updatedAt: now,
    })
  );

  const alertRef = aliceDb.collection("users").doc(ALICE_UID).collection("cost_alerts").doc("al_1");
  await assertSucceeds(
    alertRef.set({
      userId: ALICE_UID,
      dateKey: "2026-11-18",
      totalUsd: 0.12,
      thresholdUsd: 0.1,
      recipientEmail: "17crown2223@gmail.com",
      deliveryStatus: "QUEUED_LOCAL",
      deliveryDetail: "Logged in Firestore",
      createdAt: now,
      updatedAt: now,
    })
  );
});
