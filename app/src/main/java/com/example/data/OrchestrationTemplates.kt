package com.example.data

import com.example.model.StackTemplate

object OrchestrationTemplates {
    val all: List<StackTemplate> = listOf(
        StackTemplate(
            id = "terraform_llm_infra",
            title = "Terraform: GCP LLM基盤 + Pub/Sub実遮断 + Cloudflare Email Routing/KV/R2",
            category = "TERRAFORM",
            targetHost = "infra/main.tf",
            subtitle = "GCP Cloud Run/LLM基盤・Pub/Sub連動$3/月実遮断フックに加え、Cloudflare Email Routing (tf@/ci-heal@/task@/crawl@)・KV・R2を一括宣言",
            freeTierSummary = "Cloud Run min=0 & Cloudflare Email Routing / KV / R2 / Pub/Sub 無料枠",
            content = """# infra/main.tf — AntiGravity Hardened Zero-Cost Orchestration Infrastructure
terraform {
  required_version = ">= 1.9.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.0"
    }
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "~> 4.40"
    }
  }
  # Optional: store tfstate in Cloudflare R2 (S3-compatible free 10GB)
  # backend "s3" {
  #   bucket                      = "antigravity-tfstate"
  #   key                         = "prod/terraform.tfstate"
  #   region                      = "auto"
  #   skip_credentials_validation = true
  #   skip_region_validation      = true
  #   skip_requesting_account_id  = true
  #   use_path_style              = true
  # }
}

variable "gcp_project_id" {
  type    = string
  default = "gen-lang-client-0439741065"
}

variable "gcp_region" {
  type    = string
  default = "us-central1"
}

variable "billing_account_id" {
  type    = string
  default = ""
}

variable "alert_email" {
  type    = string
  default = "17crown2223@gmail.com"
}

variable "cloudflare_account_id" {
  type    = string
  default = ""
}

variable "cloudflare_zone_id" {
  type    = string
  default = ""
}

variable "domain_name" {
  type    = string
  default = "gc2223.com"
}

variable "worker_name" {
  type    = string
  default = "antigravity-selfheal-gateway"
}

variable "budget_hook_url" {
  type    = string
  default = "https://gc2223.com/v1/gcp-budget-hook"
}

variable "llm_container_image" {
  type    = string
  default = "us-docker.pkg.dev/cloudrun/container/hello"
}

provider "google" {
  project = var.gcp_project_id
  region  = var.gcp_region
}

provider "cloudflare" {
  # Reads CLOUDFLARE_API_TOKEN from environment automatically
}

locals {
  required_apis = toset([
    "run.googleapis.com",
    "aiplatform.googleapis.com",
    "generativelanguage.googleapis.com",
    "workflows.googleapis.com",
    "billingbudgets.googleapis.com",
    "monitoring.googleapis.com",
    "pubsub.googleapis.com"
  ])
  email_routes = toset(["ci-heal", "tf", "task", "crawl"])
}

resource "google_project_service" "apis" {
  for_each           = local.required_apis
  service            = each.value
  disable_on_destroy = false
}

# Always-Free LLM Execution / Orchestrator Runtime (scales to 0 when idle)
resource "google_cloud_run_v2_service" "llm_runtime" {
  name     = "antigravity-llm-runtime"
  location = var.gcp_region
  ingress  = "INGRESS_TRAFFIC_ALL"

  template {
    scaling {
      min_instance_count = 0
      max_instance_count = 1
    }
    containers {
      image = var.llm_container_image
      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
        cpu_idle = true
      }
      env {
        name  = "GCP_PROJECT_ID"
        value = var.gcp_project_id
      }
      env {
        name  = "COST_GUARD_DAILY_USD"
        value = "0.10"
      }
    }
  }

  depends_on = [google_project_service.apis]
}

# Pub/Sub Topic + Push Subscription to trigger SAFE_MODE circuit breaker on 90%+ budget
resource "google_pubsub_topic" "budget_alerts" {
  name       = "antigravity-budget-guard"
  depends_on = [google_project_service.apis]
}

resource "google_pubsub_subscription" "budget_hook_push" {
  name  = "antigravity-budget-worker-hook"
  topic = google_pubsub_topic.budget_alerts.id

  push_config {
    push_endpoint = var.budget_hook_url
  }
}

# Email Notification Channel for Budget & Self-Heal Alerts
resource "google_monitoring_notification_channel" "email_alert" {
  display_name = "AntiGravity Alert (${'$'}{var.alert_email})"
  type         = "email"
  labels = {
    email_address = var.alert_email
  }
  depends_on = [google_project_service.apis]
}

# $3/month (~$0.10/day) Budget Guard wired to Email + Pub/Sub Circuit Breaker
resource "google_billing_budget" "daily_guard" {
  count           = var.billing_account_id != "" ? 1 : 0
  billing_account = var.billing_account_id
  display_name    = "AntiGravity $0.10/day ($3/mo) Guard"

  budget_filter {
    projects = ["projects/${'$'}{var.gcp_project_id}"]
  }

  amount {
    specified_amount {
      currency_code = "USD"
      units         = "3"
    }
  }

  threshold_rules { threshold_percent = 0.5 }
  threshold_rules { threshold_percent = 0.9 }
  threshold_rules { threshold_percent = 1.0 }

  all_updates_rule {
    monitoring_notification_channels = [google_monitoring_notification_channel.email_alert.id]
    pubsub_topic                     = google_pubsub_topic.budget_alerts.id
  }
}

# Cloudflare R2 Bucket for Audit JSONL, Termux Logs, Knowledge Base & Terraform State (10GB Free)
resource "cloudflare_r2_bucket" "artifacts" {
  count      = var.cloudflare_account_id != "" ? 1 : 0
  account_id = var.cloudflare_account_id
  name       = "antigravity-artifacts"
  location   = "APAC"
}

# Cloudflare KV for One-Time Approvals, Termux Lease/DLQ, Rate Limits, Thread Context & Trace Timeline
resource "cloudflare_workers_kv_namespace" "orchestration_kv" {
  count      = var.cloudflare_account_id != "" ? 1 : 0
  account_id = var.cloudflare_account_id
  title      = "antigravity-orchestration-kv"
}

# Enable Cloudflare Email Routing on gc2223.com
resource "cloudflare_email_routing_settings" "domain_email" {
  count   = var.cloudflare_zone_id != "" ? 1 : 0
  zone_id = var.cloudflare_zone_id
  enabled = "true"
}

# Route ci-heal@, tf@, task@, crawl@gc2223.com -> Cloudflare Email Worker
resource "cloudflare_email_routing_rule" "worker_routes" {
  for_each = var.cloudflare_zone_id != "" ? local.email_routes : toset([])
  zone_id  = var.cloudflare_zone_id
  name     = "antigravity-${'$'}{each.value}-router"
  enabled  = true

  matcher {
    type  = "literal"
    field = "to"
    value = "${'$'}{each.value}@${'$'}{var.domain_name}"
  }

  action {
    type  = "worker"
    value = [var.worker_name]
  }

  depends_on = [cloudflare_email_routing_settings.domain_email]
}

output "llm_runtime_url" {
  value = google_cloud_run_v2_service.llm_runtime.uri
}

output "orchestration_kv_id" {
  value = length(cloudflare_workers_kv_namespace.orchestration_kv) > 0 ? cloudflare_workers_kv_namespace.orchestration_kv[0].id : ""
}
"""
        ),
        StackTemplate(
            id = "github_actions_terraform",
            title = "GitHub Actions: Terraform plan/apply + trace_id & 承認連携",
            category = "TERRAFORM",
            targetHost = ".github/workflows/antigravity-terraform.yml",
            subtitle = "trace_id を引き回しつつ plan/apply を実行し、失敗時はログを gc2223.com/v1/workflow-error へ自動通報",
            freeTierSummary = "GitHub Actions 無料枠 + trace_id 追跡 + Self-Heal 連携",
            content = """name: AntiGravity Terraform Provisioner

on:
  workflow_dispatch:
    inputs:
      tf_action:
        description: "Terraform action (plan / apply / destroy)"
        required: true
        default: "plan"
        type: choice
        options:
          - plan
          - apply
          - destroy
      target_env:
        description: "Target environment"
        required: false
        default: "dev"
      triggered_by:
        description: "Trigger source"
        required: false
        default: "antigravity-android"
      retry_count:
        description: "Self-heal retry counter"
        required: false
        default: "0"
      trace_id:
        description: "Distributed trace ID across Worker/Actions/Termux"
        required: false
        default: ""

jobs:
  terraform:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      id-token: write
    defaults:
      run:
        working-directory: infra

    steps:
      - uses: actions/checkout@v4

      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: "1.9.8"

      - name: Terraform Init & Validate
        id: tf_init
        env:
          CLOUDFLARE_API_TOKEN: ${'$'}{{ secrets.CLOUDFLARE_API_TOKEN }}
        run: |
          echo "trace_id=${'$'}{{ inputs.trace_id }}" | tee ../tf_run.log
          terraform init -input=false 2>&1 | tee -a ../tf_run.log
          terraform validate 2>&1 | tee -a ../tf_run.log

      - name: Terraform Plan
        id: tf_plan
        env:
          CLOUDFLARE_API_TOKEN: ${'$'}{{ secrets.CLOUDFLARE_API_TOKEN }}
          TF_VAR_cloudflare_account_id: ${'$'}{{ secrets.CLOUDFLARE_ACCOUNT_ID }}
          TF_VAR_cloudflare_zone_id: ${'$'}{{ secrets.CLOUDFLARE_ZONE_ID }}
          TF_VAR_billing_account_id: ${'$'}{{ secrets.GCP_BILLING_ACCOUNT_ID }}
        run: |
          terraform plan -no-color -input=false -out=tfplan 2>&1 | tee -a ../tf_run.log

      - name: Terraform Apply
        if: inputs.tf_action == 'apply'
        env:
          CLOUDFLARE_API_TOKEN: ${'$'}{{ secrets.CLOUDFLARE_API_TOKEN }}
          TF_VAR_cloudflare_account_id: ${'$'}{{ secrets.CLOUDFLARE_ACCOUNT_ID }}
          TF_VAR_cloudflare_zone_id: ${'$'}{{ secrets.CLOUDFLARE_ZONE_ID }}
          TF_VAR_billing_account_id: ${'$'}{{ secrets.GCP_BILLING_ACCOUNT_ID }}
        run: |
          terraform apply -no-color -input=false -auto-approve tfplan 2>&1 | tee -a ../tf_run.log

      - name: Terraform Destroy
        if: inputs.tf_action == 'destroy'
        env:
          CLOUDFLARE_API_TOKEN: ${'$'}{{ secrets.CLOUDFLARE_API_TOKEN }}
          TF_VAR_cloudflare_account_id: ${'$'}{{ secrets.CLOUDFLARE_ACCOUNT_ID }}
          TF_VAR_cloudflare_zone_id: ${'$'}{{ secrets.CLOUDFLARE_ZONE_ID }}
          TF_VAR_billing_account_id: ${'$'}{{ secrets.GCP_BILLING_ACCOUNT_ID }}
        run: |
          terraform destroy -no-color -input=false -auto-approve 2>&1 | tee -a ../tf_run.log

      - name: Notify Email Self-Heal Worker on Failure
        if: failure()
        env:
          GATEWAY_URL: ${'$'}{{ secrets.ANTIGRAVITY_GATEWAY_URL }}
          BEARER_TOKEN: ${'$'}{{ secrets.AGENT_BEARER_TOKEN }}
        run: |
          LOG_TAIL=$(tail -n 120 ../tf_run.log | jq -Rs .)
          curl -sS -X POST "${'$'}{GATEWAY_URL:-https://gc2223.com}/v1/workflow-error" \
            -H "Authorization: Bearer ${'$'}{BEARER_TOKEN}" \
            -H "Content-Type: application/json" \
            -d "{
              \"recipientEmail\": \"17crown2223@gmail.com\",
              \"repo\": \"${'$'}{{ github.repository }}\",
              \"workflow\": \"antigravity-terraform.yml\",
              \"runId\": ${'$'}{{ github.run_id }},
              \"retryCount\": ${'$'}{{ inputs.retry_count }},
              \"traceId\": \"${'$'}{{ inputs.trace_id }}\",
              \"errorLog\": ${'$'}{LOG_TAIL}
            }"
"""
        ),
        StackTemplate(
            id = "github_actions_orchestrator",
            title = "GitHub Actions: 全体オーケストレーション自動化パイプライン",
            category = "GITHUB",
            targetHost = ".github/workflows/antigravity-orchestrator.yml",
            subtitle = "trace_id 引き回し対応・Worker更新・Terraform検証・ヘルス監査を連鎖実行し失敗時は自己修復へエスカレーション",
            freeTierSummary = "GitHub Actions 無料枠 ・ trace_id 横断追跡対応",
            content = """name: AntiGravity Full Orchestrator

on:
  workflow_dispatch:
    inputs:
      pipeline_mode:
        description: "Orchestration mode (full_sync / worker_deploy / terraform_plan / health_audit)"
        required: true
        default: "full_sync"
        type: choice
        options:
          - full_sync
          - worker_deploy
          - terraform_plan
          - health_audit
      target_env:
        description: "Environment (dev / prod)"
        required: false
        default: "dev"
      triggered_by:
        description: "Origin (android / email-worker / cron / termux)"
        required: false
        default: "antigravity-orchestrator"
      trace_id:
        description: "Distributed trace ID"
        required: false
        default: ""

jobs:
  deploy-gateway-worker:
    if: inputs.pipeline_mode == 'full_sync' || inputs.pipeline_mode == 'worker_deploy'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Deploy Unified Worker (gc2223.com)
        if: hashFiles('worker/wrangler.toml') != ''
        uses: cloudflare/wrangler-action@v3
        with:
          workingDirectory: worker
          apiToken: ${'$'}{{ secrets.CLOUDFLARE_API_TOKEN }}
          accountId: ${'$'}{{ secrets.CLOUDFLARE_ACCOUNT_ID }}

  terraform-verify:
    if: inputs.pipeline_mode == 'full_sync' || inputs.pipeline_mode == 'terraform_plan'
    needs: [deploy-gateway-worker]
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: infra
    steps:
      - uses: actions/checkout@v4
      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: "1.9.8"
      - name: Terraform Validate & Plan
        env:
          CLOUDFLARE_API_TOKEN: ${'$'}{{ secrets.CLOUDFLARE_API_TOKEN }}
          TF_VAR_cloudflare_account_id: ${'$'}{{ secrets.CLOUDFLARE_ACCOUNT_ID }}
          TF_VAR_cloudflare_zone_id: ${'$'}{{ secrets.CLOUDFLARE_ZONE_ID }}
        run: |
          terraform init -input=false
          terraform validate
          terraform plan -no-color -input=false

  health-audit:
    if: always() && (inputs.pipeline_mode == 'full_sync' || inputs.pipeline_mode == 'health_audit')
    needs: [deploy-gateway-worker, terraform-verify]
    runs-on: ubuntu-latest
    steps:
      - name: Probe Gateway & Notify Orchestrator
        env:
          GATEWAY_URL: ${'$'}{{ secrets.ANTIGRAVITY_GATEWAY_URL }}
          BEARER_TOKEN: ${'$'}{{ secrets.AGENT_BEARER_TOKEN }}
        run: |
          curl -fsS "${'$'}{GATEWAY_URL:-https://gc2223.com}/health"
          if [ "${'$'}{{ needs.deploy-gateway-worker.result }}" = "failure" ] || [ "${'$'}{{ needs.terraform-verify.result }}" = "failure" ]; then
            curl -sS -X POST "${'$'}{GATEWAY_URL:-https://gc2223.com}/v1/workflow-error" \
              -H "Authorization: Bearer ${'$'}{BEARER_TOKEN}" \
              -H "Content-Type: application/json" \
              -d "{\"recipientEmail\":\"17crown2223@gmail.com\",\"repo\":\"${'$'}{{ github.repository }}\",\"workflow\":\"antigravity-orchestrator.yml\",\"runId\":${'$'}{{ github.run_id }},\"retryCount\":0,\"traceId\":\"${'$'}{{ inputs.trace_id }}\",\"errorLog\":\"Orchestrator failure in ${'$'}{{ inputs.pipeline_mode }}\"}"
            exit 1
          fi
"""
        ),
        StackTemplate(
            id = "cf_email_autofix_worker",
            title = "Cloudflare Hardened Worker: TTL承認・Lease/DLQ・SAFE_MODE・監査ログ・スレッド学習",
            category = "SELF_HEAL",
            targetHost = "gc2223.com",
            subtitle = "ワンタイム承認(30m TTL)・Termuxリース/DLQ・レート制限SAFE_MODE・Pub/Sub予算遮断・trace_id R2監査・スレッド継続＆CIナレッジ蓄積を実装",
            freeTierSummary = "Workers + Email Routing + KV + R2 + Workers AI 無料枠完全対応",
            content = """// worker/src/worker.js — AntiGravity Hardened Orchestrator, Email Task Router & Circuit Breaker
import puppeteer from "@cloudflare/puppeteer";

const TRUSTED_SENDERS = ["17crown2223@gmail.com", "github.com", "gc2223.com"];
const HOURLY_LIMITS = { ai: 30, gh: 15, email: 15 };
const LEASE_MS = 10 * 60 * 1000;
const APPROVAL_TTL_SEC = 1800;

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    if (url.pathname === "/health") {
      const safeMode = await isSafeMode(env);
      return Response.json({
        ok: true,
        service: "AntiGravity Hardened Orchestrator",
        safeMode,
        routes: ["ci-heal@", "tf@", "task@", "crawl@"],
        timestamp: new Date().toISOString()
      });
    }

    // Pub/Sub Budget Alert Push Hook (90%+ usage -> auto SAFE_MODE circuit breaker)
    if (url.pathname === "/v1/gcp-budget-hook" && request.method === "POST") {
      const raw = await request.json().catch(() => ({}));
      const decoded = raw?.message?.data ? JSON.parse(atob(raw.message.data)) : raw;
      const costRatio = Number(decoded?.costAmount || 0) / Math.max(Number(decoded?.budgetAmount || 3), 0.01);
      if (costRatio >= 0.9 || raw?.forceSafeMode === true) {
        await setSafeMode(env, true, `GCP Budget Alert ${'$'}{(costRatio * 100).toFixed(0)}% exceeded`);
        await sendResendEmail(env, "17crown2223@gmail.com", "[CIRCUIT BREAKER] SAFE_MODE Activated",
          `GCP budget ratio reached ${'$'}{(costRatio * 100).toFixed(1)}%. Auto-execution paused.`, "budget-hook");
      }
      return Response.json({ ok: true, safeMode: await isSafeMode(env), costRatio });
    }

    const auth = request.headers.get("Authorization") || "";
    const bearerOk = Boolean(env.AGENT_BEARER_TOKEN && auth === `Bearer ${'$'}{env.AGENT_BEARER_TOKEN}`);
    if (!bearerOk && !request.headers.get("X-Firebase-Token")) {
      return Response.json({ error: "Unauthorized" }, { status: 401 });
    }

    // 1. Unified Orchestration API (/v1/orchestrate) with trace_id & Circuit Breaker
    if (url.pathname === "/v1/orchestrate" && request.method === "POST") {
      const req = await request.json();
      const traceId = req.traceId || newTraceId();
      if (await isSafeMode(env)) {
        return Response.json({
          ok: false,
          traceId,
          detail: "SAFE_MODE_ACTIVE: サーキットブレーカー作動中のため自動実行を停止しました。アプリから解除してください。"
        }, { status: 429 });
      }
      const result = await executeOrchestrationTask(env, { ...req, traceId });
      await recordAuditEvent(env, { traceId, source: "http:/v1/orchestrate", route: req.routeAddress, taskType: req.taskType, ok: result.ok, summary: result.detail });
      return Response.json({ ...result, traceId }, { status: result.ok ? 200 : 400 });
    }

    // 2. Hardening State & Circuit Breaker Control (/v1/orchestrate/state & /v1/circuit-breaker)
    if (url.pathname === "/v1/orchestrate/state" && request.method === "GET") {
      const state = await buildHardeningSnapshot(env);
      return Response.json({ ok: true, ...state });
    }
    if (url.pathname === "/v1/circuit-breaker" && request.method === "POST") {
      const { safeMode, reason = "manual" } = await request.json();
      await setSafeMode(env, Boolean(safeMode), reason);
      const traceId = newTraceId();
      await recordAuditEvent(env, { traceId, source: "app:circuit-breaker", taskType: safeMode ? "enable_safe_mode" : "reset_safe_mode", ok: true, summary: reason });
      return Response.json({ ok: true, safeMode: Boolean(safeMode), traceId });
    }

    // 3. One-Time TTL Approval API (/v1/approvals & /v1/approvals/decide)
    if (url.pathname === "/v1/approvals" && request.method === "GET") {
      const items = await listPendingApprovals(env);
      return Response.json({ ok: true, approvals: items });
    }
    if (url.pathname === "/v1/approvals/decide" && request.method === "POST") {
      const { approvalId, approve } = await request.json();
      const decision = await decideApproval(env, approvalId, Boolean(approve), "android-app");
      return Response.json(decision, { status: decision.ok ? 200 : 404 });
    }

    // 4. Audit Timeline (/v1/audit)
    if (url.pathname === "/v1/audit" && request.method === "GET") {
      const logs = env.ORCH_KV ? (await env.ORCH_KV.get("audit:recent", "json")) || [] : [];
      return Response.json({ ok: true, logs });
    }

    // 5. Termux Pull Queue with Lease, DLQ, Battery Guard & Heartbeat (/v1/termux/next-task & /ack)
    if (url.pathname === "/v1/termux/next-task" && request.method === "GET") {
      const battery = Number(request.headers.get("X-Device-Battery") || 100);
      const charging = request.headers.get("X-Device-Charging") === "true";
      const network = request.headers.get("X-Device-Network") || "unknown";
      await recordTermuxHeartbeat(env, { battery, charging, network });

      const pending = env.ORCH_KV ? await env.ORCH_KV.get("termux:pending_task", "json") : null;
      if (!pending) return Response.json({ ok: true, hasTask: false, task: null });

      const now = Date.now();
      if (pending.status === "LEASED" && pending.leaseExpiresAt > now) {
        return Response.json({ ok: true, hasTask: false, leasedUntil: pending.leaseExpiresAt });
      }
      if ((pending.attempts || 0) >= 3) {
        await moveTaskToDlq(env, pending, "MAX_RETRIES_OR_LEASE_EXPIRED");
        return Response.json({ ok: true, hasTask: false, dlqMoved: true });
      }
      const heavyPreset = ["tf_apply", "wrangler_deploy"].includes(pending.preset);
      if (heavyPreset && battery < 15 && !charging) {
        return Response.json({ ok: true, hasTask: false, deferredReason: `LOW_BATTERY_${'$'}{battery}%` });
      }

      const leasedTask = {
        ...pending,
        status: "LEASED",
        attempts: (pending.attempts || 0) + 1,
        leaseExpiresAt: now + LEASE_MS
      };
      if (env.ORCH_KV) await env.ORCH_KV.put("termux:pending_task", JSON.stringify(leasedTask), { expirationTtl: 86400 });
      return Response.json({ ok: true, hasTask: true, task: leasedTask });
    }

    if (url.pathname === "/v1/termux/ack" && request.method === "POST") {
      const ack = await request.json();
      const traceId = ack.traceId || newTraceId();
      const cur = env.ORCH_KV ? await env.ORCH_KV.get("termux:pending_task", "json") : null;
      let logObjectKey = "";
      if (env.ORCH_R2) {
        const day = new Date().toISOString().slice(0, 10);
        logObjectKey = `termux-logs/${'$'}{day}/${'$'}{traceId}_${'$'}{Date.now()}.json`;
        await env.ORCH_R2.put(logObjectKey, JSON.stringify({ ...ack, savedAt: new Date().toISOString() }));
      }
      if (ack.ok === false && cur && (cur.attempts || 1) < 3) {
        const requeue = { ...cur, status: "PENDING", leaseExpiresAt: 0, lastError: (ack.stderr || "").slice(0, 400) };
        if (env.ORCH_KV) await env.ORCH_KV.put("termux:pending_task", JSON.stringify(requeue), { expirationTtl: 86400 });
      } else if (ack.ok === false && cur) {
        await moveTaskToDlq(env, cur, (ack.stderr || "EXEC_FAILED").slice(0, 400));
      } else if (env.ORCH_KV) {
        await env.ORCH_KV.delete("termux:pending_task");
      }
      await recordAuditEvent(env, { traceId, source: "termux:ack", taskType: ack.preset || "termux", ok: Boolean(ack.ok), summary: `exit=${'$'}{ack.exitCode} r2=${'$'}{logObjectKey}` });
      return Response.json({ ok: true, logObjectKey, traceId });
    }

    // 6. Edge Web Crawl & Browser Rendering Screenshot
    if (url.pathname === "/v1/crawl" && request.method === "POST") {
      const { url: targetUrl } = await request.json();
      const summary = await crawlAndSummarize(env, targetUrl);
      return Response.json({ ok: true, targetUrl, summary });
    }
    if (url.pathname === "/v1/screenshot" && request.method === "POST") {
      const { url: targetUrl, width = 412, height = 915, userAgent } = await request.json();
      if (!env.MYBROWSER) return Response.json({ error: "MYBROWSER binding not configured" }, { status: 501 });
      const browser = await puppeteer.launch(env.MYBROWSER);
      try {
        const page = await browser.newPage();
        await page.setViewport({ width: Number(width), height: Number(height), deviceScaleFactor: 2 });
        if (userAgent) await page.setUserAgent(userAgent);
        await page.goto(targetUrl, { waitUntil: "networkidle2", timeout: 25000 });
        const imgBuffer = await page.screenshot({ type: "png", fullPage: false });
        return new Response(imgBuffer, { headers: { "Content-Type": "image/png" } });
      } finally {
        await browser.close();
      }
    }

    // 7. Workflow Error Self-Heal Webhook (/v1/workflow-error) with CI Knowledge Base
    if (url.pathname === "/v1/workflow-error" && request.method === "POST") {
      const payload = await request.json();
      const traceId = payload.traceId || newTraceId();
      const retryCount = Number(payload.retryCount || 0);
      const repo = payload.repo || env.DEFAULT_GITHUB_REPO;
      const pastFix = await loadCiKnowledge(env, repo);
      const diagnosis = await diagnoseWithAi(env, payload.errorLog || "", pastFix);
      await saveCiKnowledge(env, repo, { errorSnippet: (payload.errorLog || "").slice(0, 500), diagnosis: diagnosis.slice(0, 1200), runId: payload.runId });

      let rerunStatus = "SKIPPED_AWAITING_APPROVAL";
      let approvalNote = "";
      if (!(await isSafeMode(env)) && retryCount < 2 && repo && payload.runId && env.GITHUB_PAT && payload.autoRerun !== false) {
        rerunStatus = await triggerGithubRerun(env.GITHUB_PAT, repo, payload.runId);
      } else if (payload.runId) {
        const appr = await createPendingApproval(env, {
          action: "ci_rerun",
          repo,
          runId: payload.runId,
          targetEnv: "ci",
          summary: `Rerun ${'$'}{repo} #${'$'}{payload.runId}`,
          traceId
        });
        approvalNote = `\nReply 'APPROVE RERUN: ${'$'}{appr.approvalId}' (expires in 30m) or approve in AntiGravity Hub App.`;
      }

      await sendResendEmail(
        env,
        payload.recipientEmail || "17crown2223@gmail.com",
        `[Self-Heal #${'$'}{payload.runId || 0}] ${'$'}{repo} [tid:${'$'}{traceId}]`,
        `Trace: ${'$'}{traceId}\nRepo: ${'$'}{repo}\nRetry: ${'$'}{retryCount}/2\nRerun: ${'$'}{rerunStatus}\n\nAI Diagnosis:\n${'$'}{diagnosis}${'$'}{approvalNote}`,
        traceId
      );
      await recordAuditEvent(env, { traceId, source: "webhook:/v1/workflow-error", taskType: "self_heal", ok: true, summary: `${'$'}{repo} #${'$'}{payload.runId} -> ${'$'}{rerunStatus}` });
      return Response.json({ ok: true, rerunStatus, diagnosis, traceId });
    }

    return Response.json({ error: "Not found" }, { status: 404 });
  },

  // 8. Multi-Address Email Worker Router with Thread Context & One-Time Approval Verification
  async email(message, env, ctx) {
    const sender = (message.from || "").toLowerCase();
    const recipient = (message.to || "").toLowerCase();
    const subject = message.headers.get("subject") || "";
    const messageId = message.headers.get("message-id") || `${'$'}{sender}:${'$'}{Date.now()}`;
    const inReplyTo = message.headers.get("in-reply-to") || "";
    const references = message.headers.get("references") || "";
    const authRes = (message.headers.get("authentication-results") || "").toLowerCase();
    const rawEmail = await new Response(message.raw).text();

    if (!TRUSTED_SENDERS.some((s) => sender.includes(s))) {
      message.setReject("Untrusted sender");
      return;
    }
    if (authRes && (authRes.includes("dkim=fail") || authRes.includes("spf=fail"))) {
      message.setReject("Failed SPF/DKIM verification");
      return;
    }
    if (env.ORCH_KV) {
      const dedupKey = `msg:${'$'}{messageId}`;
      if (await env.ORCH_KV.get(dedupKey)) return;
      await env.ORCH_KV.put(dedupKey, "1", { expirationTtl: 86400 });
    }

    const traceId = (subject.match(/\[tid:([a-zA-Z0-9-]+)\]/)?.[1]) || newTraceId();
    if (await isSafeMode(env)) {
      await sendResendEmail(env, "17crown2223@gmail.com", `[SAFE_MODE] Ignored ${'$'}{subject.slice(0, 40)}`, "SAFE_MODE is active. Reset in AntiGravity Hub App.", traceId);
      return;
    }

    // Check for One-Time Approval Command: APPROVE APPLY: <id> or APPROVE RERUN: <id>
    const approveMatch = rawEmail.match(/APPROVE\s+(APPLY|RERUN)\s*:\s*([a-zA-Z0-9_-]+)/i);
    if (approveMatch) {
      const decision = await decideApproval(env, approveMatch[2], true, `email:${'$'}{sender}`);
      await sendResendEmail(env, "17crown2223@gmail.com", `[Approval ${'$'}{approveMatch[2]}] ${'$'}{decision.ok ? "Executed" : "Rejected"}`, JSON.stringify(decision, null, 2), traceId);
      return;
    }

    const threadRef = (inReplyTo || references.split(/\s+/).pop() || "").trim();
    const priorContext = threadRef && env.ORCH_KV ? await env.ORCH_KV.get(`thread:${'$'}{threadRef}`) : null;
    const localPart = recipient.split("@")[0] || "task";
    const repo = extractRepo(rawEmail, env.DEFAULT_GITHUB_REPO);
    let report = "";

    if (localPart === "ci-heal") {
      const runMatch = subject.match(/#(\d+)/) || rawEmail.match(/runId[:\s]+(\d+)/i);
      const fix = await diagnoseWithAi(env, `${'$'}{priorContext ? "Thread:\n" + priorContext + "\n\n" : ""}${'$'}{rawEmail.slice(0, 3500)}`, await loadCiKnowledge(env, repo));
      const runId = runMatch ? runMatch[1] : null;
      const appr = runId ? await createPendingApproval(env, { action: "ci_rerun", repo, runId, targetEnv: "ci", summary: `CI rerun ${'$'}{repo}#${'$'}{runId}`, traceId }) : null;
      report = `[ci-heal] trace=${'$'}{traceId}\n${'$'}{fix}\n${'$'}{appr ? `\nReply 'APPROVE RERUN: ${'$'}{appr.approvalId}' (TTL 30m) or approve in App.` : ""}`;
    } else if (localPart === "tf") {
      const text = `${'$'}{subject}\n${'$'}{rawEmail}`.toLowerCase();
      const action = text.includes("destroy") ? "destroy" : text.includes("apply") ? "apply" : "plan";
      const envName = text.includes("prod") ? "prod" : "dev";
      if (action === "apply" || action === "destroy") {
        const appr = await createPendingApproval(env, { action: `tf_${'$'}{action}`, repo, targetEnv: envName, summary: `Terraform ${'$'}{action} (${'$'}{envName})`, traceId });
        report = `[tf] Prepared ${'$'}{action} (${'$'}{envName}).\nReply 'APPROVE APPLY: ${'$'}{appr.approvalId}' within 30m or tap Approve in AntiGravity Hub.`;
      } else {
        const res = await dispatchGithubWorkflow(env, repo, "antigravity-terraform.yml", { tf_action: "plan", target_env: envName, triggered_by: "email-worker", trace_id: traceId });
        const appr = await createPendingApproval(env, { action: "tf_apply", repo, targetEnv: envName, summary: `Apply after plan (${'$'}{envName})`, traceId });
        report = `[tf] Plan dispatched -> ${'$'}{res}\nTo apply next: Reply 'APPROVE APPLY: ${'$'}{appr.approvalId}' (TTL 30m).`;
      }
    } else if (localPart === "crawl") {
      const targetUrl = rawEmail.match(/https?:\/\/[^\s"'<>]+/i)?.[0] || "https://gc2223.com";
      report = await crawlAndSummarize(env, targetUrl);
    } else {
      const combined = priorContext ? `Previous Thread Context:\n${'$'}{priorContext}\n\nNew Follow-up:\n${'$'}{rawEmail.slice(0, 2000)}` : rawEmail.slice(0, 2500);
      const plan = await classifyEmailTaskWithAi(env, subject, combined);
      const execRes = await executeOrchestrationTask(env, { taskType: plan.taskType || "ai_task_router", routeAddress: recipient, repo, traceId, payload: { prompt: `${'$'}{subject}\n${'$'}{combined}`, ...plan } });
      report = `[task] trace=${'$'}{traceId} intent=${'$'}{plan.taskType}\n${'$'}{execRes.detail}`;
    }

    if (env.ORCH_KV && messageId) {
      await env.ORCH_KV.put(`thread:${'$'}{messageId}`, report.slice(0, 3000), { expirationTtl: 172800 });
    }
    await recordAuditEvent(env, { traceId, source: `email:${'$'}{localPart}@`, route: recipient, taskType: localPart, ok: true, summary: subject.slice(0, 120) });
    await sendResendEmail(env, "17crown2223@gmail.com", `[AntiGravity ${'$'}{localPart}@] ${'$'}{subject.slice(0, 50)} [tid:${'$'}{traceId}]`, report, traceId, messageId);
  },

  // 9. Scheduled Cron Watchdog (CI Monitor + Token Expiration + Termux Lease Recovery)
  async scheduled(event, env, ctx) {
    const repo = env.DEFAULT_GITHUB_REPO;
    const warnings = [];

    // Recover expired Termux leases automatically
    if (env.ORCH_KV) {
      const pending = await env.ORCH_KV.get("termux:pending_task", "json");
      if (pending && pending.status === "LEASED" && pending.leaseExpiresAt < Date.now()) {
        if ((pending.attempts || 0) >= 3) {
          await moveTaskToDlq(env, pending, "CRON_LEASE_TIMEOUT");
          warnings.push(`Moved stale Termux task '${'$'}{pending.preset}' to DLQ.`);
        } else {
          await env.ORCH_KV.put("termux:pending_task", JSON.stringify({ ...pending, status: "PENDING", leaseExpiresAt: 0 }), { expirationTtl: 86400 });
        }
      }
    }

    if (repo && env.GITHUB_PAT) {
      const res = await fetch(`https://api.github.com/repos/${'$'}{repo}/actions/runs?per_page=1`, {
        headers: { "Authorization": `Bearer ${'$'}{env.GITHUB_PAT}`, "Accept": "application/vnd.github+json", "User-Agent": "AntiGravity-Cron-Watchdog" }
      });
      const patExpiry = res.headers.get("github-authentication-token-expiration") || "";
      if (patExpiry && (Date.parse(patExpiry) - Date.now()) < 7 * 86400 * 1000) {
        warnings.push(`GITHUB_PAT expires soon (${'$'}{patExpiry}).`);
      }
      if (res.ok) {
        const data = await res.json();
        const latest = (data.workflow_runs || [])[0];
        if (env.ORCH_KV) {
          await env.ORCH_KV.put("cron:last_heartbeat", JSON.stringify({ at: new Date().toISOString(), runId: latest?.id || 0, conclusion: latest?.conclusion || "unknown", patExpiry }));
        }
        if (latest && latest.conclusion === "failure") {
          const alertKey = `cron:alerted:${'$'}{latest.id}`;
          if (!env.ORCH_KV || !(await env.ORCH_KV.get(alertKey))) {
            if (env.ORCH_KV) await env.ORCH_KV.put(alertKey, "1", { expirationTtl: 86400 });
            warnings.push(`Workflow '${'$'}{latest.name}' (#${'$'}{latest.id}) failed: ${'$'}{latest.html_url}`);
          }
        }
      }
    }

    if (warnings.length > 0) {
      await sendResendEmail(env, "17crown2223@gmail.com", `[Watchdog] ${'$'}{warnings.length} item(s) require attention`, warnings.join("\n"), newTraceId());
    }
  }
};

function newTraceId() {
  return crypto.randomUUID().slice(0, 12);
}

async function isSafeMode(env) {
  if (!env.ORCH_KV) return false;
  return (await env.ORCH_KV.get("circuit:safe_mode")) === "true";
}

async function setSafeMode(env, enabled, reason) {
  if (!env.ORCH_KV) return;
  await env.ORCH_KV.put("circuit:safe_mode", enabled ? "true" : "false");
  await env.ORCH_KV.put("circuit:safe_mode_reason", String(reason || ""), { expirationTtl: 604800 });
}

async function checkAndBumpQuota(env, bucket) {
  if (!env.ORCH_KV) return true;
  const hourKey = `rate:${'$'}{bucket}:${'$'}{new Date().toISOString().slice(0, 13)}`;
  const cur = Number((await env.ORCH_KV.get(hourKey)) || 0) + 1;
  await env.ORCH_KV.put(hourKey, String(cur), { expirationTtl: 7200 });
  if (cur > (HOURLY_LIMITS[bucket] || 30)) {
    await setSafeMode(env, true, `Hourly rate limit exceeded for ${'$'}{bucket} (${'$'}{cur})`);
    return false;
  }
  return true;
}

async function createPendingApproval(env, spec) {
  const approvalId = newTraceId().slice(0, 8);
  const now = Date.now();
  const item = { approvalId, ...spec, createdAtMs: now, expiresAtMs: now + APPROVAL_TTL_SEC * 1000 };
  if (env.ORCH_KV) {
    await env.ORCH_KV.put(`approval:${'$'}{approvalId}`, JSON.stringify(item), { expirationTtl: APPROVAL_TTL_SEC });
    const ids = ((await env.ORCH_KV.get("approvals:index", "json")) || []).filter((id) => id !== approvalId);
    ids.unshift(approvalId);
    await env.ORCH_KV.put("approvals:index", JSON.stringify(ids.slice(0, 20)), { expirationTtl: 86400 });
  }
  return item;
}

async function listPendingApprovals(env) {
  if (!env.ORCH_KV) return [];
  const ids = (await env.ORCH_KV.get("approvals:index", "json")) || [];
  const items = [];
  const activeIds = [];
  for (const id of ids) {
    const raw = await env.ORCH_KV.get(`approval:${'$'}{id}`, "json");
    if (raw && raw.expiresAtMs > Date.now()) {
      items.push(raw);
      activeIds.push(id);
    }
  }
  if (activeIds.length !== ids.length) {
    await env.ORCH_KV.put("approvals:index", JSON.stringify(activeIds), { expirationTtl: 86400 });
  }
  return items;
}

async function decideApproval(env, approvalId, approve, actor) {
  if (!env.ORCH_KV) return { ok: false, detail: "ORCH_KV binding missing" };
  const key = `approval:${'$'}{approvalId}`;
  const item = await env.ORCH_KV.get(key, "json");
  if (!item || item.expiresAtMs < Date.now()) {
    return { ok: false, detail: `Approval '${'$'}{approvalId}' not found or expired` };
  }
  // Consume immediately (one-time use / replay protection)
  await env.ORCH_KV.delete(key);
  let execSummary = "REJECTED";
  if (approve) {
    if (item.action === "ci_rerun" && item.runId) {
      execSummary = await triggerGithubRerun(env.GITHUB_PAT, item.repo, item.runId);
    } else if (item.action?.startsWith("tf_")) {
      const tfAct = item.action.replace("tf_", "");
      execSummary = await dispatchGithubWorkflow(env, item.repo, "antigravity-terraform.yml", {
        tf_action: tfAct, target_env: item.targetEnv || "dev", triggered_by: actor, trace_id: item.traceId || ""
      });
    }
  }
  await recordAuditEvent(env, { traceId: item.traceId || approvalId, source: actor, taskType: `approval:${'$'}{item.action}`, ok: true, summary: `${'$'}{approvalId} -> ${'$'}{execSummary}` });
  return { ok: true, approvalId, approved: approve, detail: execSummary, traceId: item.traceId };
}

async function recordTermuxHeartbeat(env, hb) {
  if (!env.ORCH_KV) return;
  await env.ORCH_KV.put("termux:heartbeat", JSON.stringify({ ...hb, seenAt: new Date().toISOString() }), { expirationTtl: 86400 });
}

async function moveTaskToDlq(env, task, reason) {
  if (!env.ORCH_KV) return;
  await env.ORCH_KV.delete("termux:pending_task");
  const dlqEntry = { ...task, dlqReason: reason, failedAt: new Date().toISOString() };
  await env.ORCH_KV.put(`dlq:${'$'}{task.traceId || Date.now()}`, JSON.stringify(dlqEntry), { expirationTtl: 604800 });
  const curCount = Number((await env.ORCH_KV.get("dlq:count")) || 0) + 1;
  await env.ORCH_KV.put("dlq:count", String(curCount), { expirationTtl: 604800 });
  await sendResendEmail(env, "17crown2223@gmail.com", `[Termux DLQ] ${'$'}{task.preset} moved to DLQ`, JSON.stringify(dlqEntry, null, 2), task.traceId || "dlq");
}

async function recordAuditEvent(env, evt) {
  const entry = { ...evt, timestamp: new Date().toISOString() };
  if (env.ORCH_R2) {
    const day = entry.timestamp.slice(0, 10);
    await env.ORCH_R2.put(`audit/${'$'}{day}/${'$'}{entry.traceId || "notrace"}_${'$'}{Date.now()}.json`, JSON.stringify(entry) + "\n");
  }
  if (env.ORCH_KV) {
    const cur = (await env.ORCH_KV.get("audit:recent", "json")) || [];
    cur.unshift(entry);
    await env.ORCH_KV.put("audit:recent", JSON.stringify(cur.slice(0, 20)), { expirationTtl: 604800 });
  }
}

async function buildHardeningSnapshot(env) {
  const hourPrefix = new Date().toISOString().slice(0, 13);
  const safeMode = await isSafeMode(env);
  const safeReason = env.ORCH_KV ? (await env.ORCH_KV.get("circuit:safe_mode_reason")) || "" : "";
  const heartbeat = env.ORCH_KV ? await env.ORCH_KV.get("termux:heartbeat", "json") : null;
  const cron = env.ORCH_KV ? await env.ORCH_KV.get("cron:last_heartbeat", "json") : null;
  const pendingTask = env.ORCH_KV ? await env.ORCH_KV.get("termux:pending_task", "json") : null;
  const dlqCount = env.ORCH_KV ? Number((await env.ORCH_KV.get("dlq:count")) || 0) : 0;
  const aiHour = env.ORCH_KV ? Number((await env.ORCH_KV.get(`rate:ai:${'$'}{hourPrefix}`)) || 0) : 0;
  const ghHour = env.ORCH_KV ? Number((await env.ORCH_KV.get(`rate:gh:${'$'}{hourPrefix}`)) || 0) : 0;
  const approvals = await listPendingApprovals(env);
  return { safeMode, safeReason, heartbeat, cron, pendingTask, dlqCount, aiHour, ghHour, approvalsCount: approvals.length };
}

async function loadCiKnowledge(env, repo) {
  if (!env.ORCH_KV || !repo) return "";
  const item = await env.ORCH_KV.get(`knowledge:${'$'}{repo}`, "json");
  return item ? `Past CI Fix (${'$'}{item.runId}): ${'$'}{item.diagnosis}` : "";
}

async function saveCiKnowledge(env, repo, doc) {
  if (!repo) return;
  if (env.ORCH_KV) await env.ORCH_KV.put(`knowledge:${'$'}{repo}`, JSON.stringify(doc), { expirationTtl: 2592000 });
  if (env.ORCH_R2) await env.ORCH_R2.put(`knowledge/${'$'}{repo.replace("/", "_")}/latest.json`, JSON.stringify(doc));
}

async function executeOrchestrationTask(env, req) {
  const taskType = req.taskType || "ai_task_router";
  const repo = req.repo || env.DEFAULT_GITHUB_REPO;
  const payload = req.payload || {};
  const traceId = req.traceId || newTraceId();

  if (taskType === "tf_plan") {
    const status = await dispatchGithubWorkflow(env, repo, "antigravity-terraform.yml", { tf_action: "plan", target_env: payload.targetEnv || "dev", triggered_by: req.routeAddress || "orchestrator", trace_id: traceId });
    return { ok: status.startsWith("OK"), taskType, detail: status };
  }
  if (taskType === "tf_apply") {
    const appr = await createPendingApproval(env, { action: "tf_apply", repo, targetEnv: payload.targetEnv || "dev", summary: `Terraform apply (${'$'}{payload.targetEnv || "dev"})`, traceId });
    return { ok: true, taskType, detail: `APPROVAL_REQUIRED (${'$'}{appr.approvalId}, TTL 30m). Approve in App or reply 'APPROVE APPLY: ${'$'}{appr.approvalId}'` };
  }
  if (taskType === "dispatch_github") {
    const status = await dispatchGithubWorkflow(env, repo, payload.workflow || "antigravity-orchestrator.yml", { pipeline_mode: payload.mode || "full_sync", triggered_by: req.routeAddress || "orchestrator", trace_id: traceId });
    return { ok: status.startsWith("OK"), taskType, detail: status };
  }
  if (taskType === "queue_termux") {
    if (!env.ORCH_KV) return { ok: false, taskType, detail: "ORCH_KV binding missing" };
    const queued = { preset: payload.preset || "git_pull", workdir: payload.workdir || "~/OrchestraAntiGravity", status: "PENDING", attempts: 0, leaseExpiresAt: 0, traceId, queuedAt: new Date().toISOString() };
    await env.ORCH_KV.put("termux:pending_task", JSON.stringify(queued), { expirationTtl: 86400 });
    return { ok: true, taskType, detail: `Queued '${'$'}{queued.preset}' (trace=${'$'}{traceId}, lease=10m, maxAttempts=3)` };
  }
  if (taskType === "crawl_analyze") {
    return { ok: true, taskType, detail: await crawlAndSummarize(env, payload.url || "https://gc2223.com") };
  }
  return { ok: true, taskType, detail: await diagnoseWithAi(env, payload.prompt || JSON.stringify(payload), "") };
}

async function classifyEmailTaskWithAi(env, subject, body) {
  if (!env.AI || !(await checkAndBumpQuota(env, "ai"))) return { taskType: "ai_task_router" };
  try {
    const res = await env.AI.run("@cf/meta/llama-3.1-8b-instruct-fast", {
      messages: [
        { role: "system", content: "Return strict JSON only: {\"taskType\":\"tf_plan|dispatch_github|queue_termux|crawl_analyze|ai_task_router\",\"targetEnv\":\"dev\",\"preset\":\"git_pull\",\"url\":\"\"}" },
        { role: "user", content: `Subject: ${'$'}{subject}\nBody: ${'$'}{body}` }
      ]
    });
    return JSON.parse((res.response || "{}").match(/\{[\s\S]*\}/)?.[0] || "{}");
  } catch {
    return { taskType: "ai_task_router" };
  }
}

async function crawlAndSummarize(env, targetUrl) {
  const cleanUrl = targetUrl.startsWith("http") ? targetUrl : `https://${'$'}{targetUrl}`;
  const resp = await fetch(cleanUrl, { headers: { "User-Agent": "AntiGravityBot/1.4" } });
  const text = (await resp.text()).replace(/<[^>]+>/g, " ").replace(/\s+/g, " ").slice(0, 3500);
  if (!env.AI || !(await checkAndBumpQuota(env, "ai"))) return `Fetched ${'$'}{cleanUrl} (${'$'}{resp.status}): ${'$'}{text.slice(0, 500)}`;
  const ai = await env.AI.run("@cf/meta/llama-3.1-8b-instruct-fast", {
    messages: [
      { role: "system", content: "Summarize this webpage in Japanese and propose a zero-cost automation architecture." },
      { role: "user", content: `URL: ${'$'}{cleanUrl}\n${'$'}{text}` }
    ]
  });
  return ai.response || text.slice(0, 500);
}

async function dispatchGithubWorkflow(env, repo, workflow, inputs) {
  if (!env.GITHUB_PAT || !repo) return "MISSING_GITHUB_PAT_OR_REPO";
  if (!(await checkAndBumpQuota(env, "gh"))) return "RATE_LIMITED_SAFE_MODE";
  const res = await fetch(`https://api.github.com/repos/${'$'}{repo}/actions/workflows/${'$'}{workflow}/dispatches`, {
    method: "POST",
    headers: { "Authorization": `Bearer ${'$'}{env.GITHUB_PAT}`, "Accept": "application/vnd.github+json", "User-Agent": "AntiGravity-Orchestrator" },
    body: JSON.stringify({ ref: "main", inputs })
  });
  return res.status === 204 ? `OK_DISPATCHED_${'$'}{workflow}` : `ERR_HTTP_${'$'}{res.status}`;
}

function extractRepo(raw, fallback) {
  return raw.match(/([a-zA-Z0-9_.-]+\/[a-zA-Z0-9_.-]+)/)?.[1] || fallback;
}

async function sendResendEmail(env, to, subject, text, traceId, replyToMsgId) {
  if (!env.RESEND_API_KEY || !(await checkAndBumpQuota(env, "email"))) return;
  const headers = replyToMsgId ? { "In-Reply-To": replyToMsgId, "References": replyToMsgId } : undefined;
  await fetch("https://api.resend.com/emails", {
    method: "POST",
    headers: { "Authorization": `Bearer ${'$'}{env.RESEND_API_KEY}`, "Content-Type": "application/json" },
    body: JSON.stringify({
      from: env.ALERT_FROM_EMAIL || "ci-heal@gc2223.com",
      to: [to],
      reply_to: "ci-heal@gc2223.com",
      subject: traceId && !subject.includes("[tid:") ? `${'$'}{subject} [tid:${'$'}{traceId}]` : subject,
      text,
      headers
    })
  });
}

async function diagnoseWithAi(env, errorLog, pastFix) {
  if (!env.AI || !(await checkAndBumpQuota(env, "ai"))) return "Workers AI unavailable or rate-limited.";
  const prompt = pastFix ? `${'$'}{pastFix}\n\nCurrent Error:\n${'$'}{errorLog.slice(0, 2800)}` : errorLog.slice(0, 3500);
  const res = await env.AI.run("@cf/meta/llama-3.1-8b-instruct-fast", {
    messages: [
      { role: "system", content: "Analyze Terraform/GitHub Actions errors concisely in Japanese and output exact fix commands or HCL/YAML patch." },
      { role: "user", content: prompt }
    ]
  });
  return res.response || "";
}

async function triggerGithubRerun(pat, repo, runId) {
  const res = await fetch(`https://api.github.com/repos/${'$'}{repo}/actions/runs/${'$'}{runId}/rerun-failed-jobs`, {
    method: "POST",
    headers: { "Authorization": `Bearer ${'$'}{pat}`, "Accept": "application/vnd.github+json", "User-Agent": "AntiGravity-SelfHeal-Worker" }
  });
  return res.status === 201 ? "RERUN_TRIGGERED" : `RERUN_HTTP_${'$'}{res.status}`;
}
"""
        ),
        StackTemplate(
            id = "wrangler_email_selfheal",
            title = "wrangler.toml: KV + R2 + Email Routing + AI + Cron Watchdog 設定",
            category = "SELF_HEAL",
            targetHost = "worker/wrangler.toml",
            subtitle = "ORCH_KV (承認/リース/DLQ/レート制限)・ORCH_R2 (監査ログ/Termux出力/CIナレッジ)・6時間Cron を統合した Wrangler 設定",
            freeTierSummary = "Email Routing + KV + R2 + Workers AI + Cron 無料枠対応",
            content = """name = "antigravity-selfheal-gateway"
main = "src/worker.js"
compatibility_date = "2026-01-01"
compatibility_flags = ["nodejs_compat"]

routes = [
  { pattern = "gc2223.com/*", zone_name = "gc2223.com" }
]

[ai]
binding = "AI"

# Create KV via Terraform (`terraform apply`) or: npx wrangler kv namespace create ORCH_KV
# [[kv_namespaces]]
# binding = "ORCH_KV"
# id = "REPLACE_WITH_ORCHESTRATION_KV_ID"

# Create R2 via Terraform (`terraform apply`) or: npx wrangler r2 bucket create antigravity-artifacts
# [[r2_buckets]]
# binding = "ORCH_R2"
# bucket_name = "antigravity-artifacts"

# Optional (Workers Paid): Puppeteer Browser Rendering
# [browser]
# binding = "MYBROWSER"

[triggers]
crons = ["0 */6 * * *"]

[vars]
DEFAULT_GITHUB_REPO = "17crown1901mituru/OrchestraAntiGravity"
ALERT_FROM_EMAIL = "ci-heal@gc2223.com"

# Required secrets (set once via Wrangler CLI):
# npx wrangler secret put AGENT_BEARER_TOKEN
# npx wrangler secret put GITHUB_PAT
# npx wrangler secret put RESEND_API_KEY
"""
        )
    )
}
