package com.example.data

import com.example.model.AuditTraceEntry
import com.example.model.FederationNodeItem
import com.example.model.OrchestrationHardeningState
import com.example.model.PendingApprovalItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OrchestrationHardeningClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
        .build()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun fetchHardeningSnapshot(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?
    ): OrchestrationHardeningState = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        try {
            val stateResp = executeGet("$cleanBase/v1/orchestrate/state", bearerToken, firebaseIdToken)
            if (stateResp.first !in 200..299) {
                val hint = if (stateResp.first == 404) {
                    "統合 Worker 未更新（Deploy Kit から Stack一式 Push してください）"
                } else {
                    "状態取得 HTTP ${stateResp.first}: ${stateResp.second.take(120)}"
                }
                return@withContext OrchestrationHardeningState(statusMessage = hint)
            }
            val stateJson = JSONObject(stateResp.second)
            val hb = stateJson.optJSONObject("heartbeat")
            val cron = stateJson.optJSONObject("cron")

            val approvalsResp = executeGet("$cleanBase/v1/approvals", bearerToken, firebaseIdToken)
            val approvals = if (approvalsResp.first in 200..299) {
                parseApprovals(approvalsResp.second)
            } else {
                emptyList()
            }

            val auditResp = executeGet("$cleanBase/v1/audit", bearerToken, firebaseIdToken)
            val auditLogs = if (auditResp.first in 200..299) {
                parseAuditLogs(auditResp.second)
            } else {
                emptyList()
            }

            val sus = stateJson.optJSONObject("sustainability")
            val r2 = sus?.optJSONObject("r2")
            val aiSavedTotal = (sus?.optInt("aiCachedCount", 0) ?: 0) + (sus?.optInt("aiFrugalCount", 0) ?: 0)
            val aiTotal = sus?.optInt("aiTotalCount", 0) ?: 0
            val computedSavingsPct = if (sus != null && sus.has("aiSavingsPct")) {
                sus.optInt("aiSavingsPct", 0)
            } else if (aiTotal > 0) {
                ((aiSavedTotal * 100.0) / aiTotal).toInt().coerceIn(0, 100)
            } else 0

            val gov = stateJson.optJSONObject("governance")
            val sre = stateJson.optJSONObject("sre")
            val matrix = stateJson.optJSONObject("integrityMatrix")
            val auditVerify = stateJson.optJSONObject("auditVerification")
            val chaos = stateJson.optJSONObject("chaos")
            val sec = stateJson.optJSONObject("security")
            val fed = stateJson.optJSONObject("federation")
            val tuning = stateJson.optJSONObject("autoTuning")
            val dr = stateJson.optJSONObject("dr")
            val freeTier = stateJson.optJSONObject("freeTierGuard")
            val deadman = stateJson.optJSONObject("deadman")
            val hwAuth = stateJson.optJSONObject("hardwareAuth")
            val ciQuota = stateJson.optJSONObject("ciQuota")
            val telemetry = stateJson.optJSONObject("platformTelemetry")
            val finopsJson = stateJson.optJSONObject("finopsBreakdown")
            val finopsMap = buildMap<String, Int> {
                if (finopsJson != null) {
                    val keys = finopsJson.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        put(k, finopsJson.optInt(k, 0))
                    }
                }
            }
            val parsedNodes = parseNodes(fed?.optJSONArray("nodes"))

            OrchestrationHardeningState(
                safeMode = stateJson.optBoolean("safeMode", false),
                safeReason = stateJson.optString("safeReason", ""),
                aiHourCount = stateJson.optInt("aiHour", 0),
                ghHourCount = stateJson.optInt("ghHour", 0),
                dlqCount = stateJson.optInt("dlqCount", 0),
                approvalsCount = approvals.size.takeIf { it > 0 } ?: stateJson.optInt("approvalsCount", 0),
                termuxBatteryPct = if (hb != null && hb.has("battery")) hb.optInt("battery") else null,
                termuxCharging = if (hb != null && hb.has("charging")) hb.optBoolean("charging") else null,
                termuxNetwork = hb?.optString("network", "").orEmpty(),
                termuxLastSeen = hb?.optString("seenAt", "").orEmpty(),
                cronLastAt = cron?.optString("at", "").orEmpty(),
                cronLastConclusion = cron?.optString("conclusion", "").orEmpty(),
                patExpiryWarning = cron?.optString("patExpiry", "").orEmpty(),
                operatingMode = sus?.optString("operatingMode", "ACTIVE").orEmpty().ifBlank { "ACTIVE" },
                greenDeferredCount = sus?.optInt("greenDeferredCount", 0) ?: 0,
                carbonIntensityGco2 = sus?.optInt("carbonIntensityGco2", 0) ?: 0,
                lastCfeScore = sus?.optDouble("lastCfeScore", 0.72) ?: 0.72,
                aiSavingsPct = computedSavingsPct,
                aiCachedCount = sus?.optInt("aiCachedCount", 0) ?: 0,
                aiFrugalCount = sus?.optInt("aiFrugalCount", 0) ?: 0,
                aiTotalCount = aiTotal,
                termuxTempC = if (hb != null && hb.has("tempC") && !hb.isNull("tempC")) hb.optDouble("tempC") else null,
                batteryGuardCount = sus?.optInt("batteryGuardCount", 0) ?: 0,
                recommendedPollSec = sus?.optInt("recommendedPollSec", 60) ?: 60,
                r2AuditCount = r2?.optInt("auditCount", 0) ?: 0,
                r2TermuxLogCount = r2?.optInt("termuxLogCount", 0) ?: 0,
                r2NextPurgeDays = r2?.optInt("nextPurgeDays", 30) ?: 30,
                lastDepAuditNote = sus?.optString("lastDepAuditNote", "").orEmpty(),
                policyVersion = gov?.optString("policyVersion", "2026.11.20").orEmpty().ifBlank { "2026.11.20" },
                saifMinConfidence = gov?.optDouble("saifMinConfidence", 0.85) ?: 0.85,
                wifKeylessEnabled = gov?.optBoolean("wifKeylessEnabled", true) ?: true,
                secretLastRotatedAt = gov?.optString("secretLastRotatedAt", "").orEmpty(),
                sliSuccessPct = sre?.optDouble("sliSuccessPct", 100.0) ?: 100.0,
                errorBudgetRemainingPct = sre?.optDouble("errorBudgetRemainingPct", 100.0) ?: 100.0,
                burnRate = sre?.optDouble("burnRate", 0.0) ?: 0.0,
                avgLatencyMs = sre?.optLong("avgLatencyMs", 0L) ?: 0L,
                lastAuditHash = sre?.optString("lastAuditHash", "").orEmpty(),
                gitCommitSha = matrix?.optString("gitCommitSha", "-").orEmpty().ifBlank { "-" },
                workerVersion = matrix?.optString("workerVersion", "-").orEmpty().ifBlank { "-" },
                terraformRevision = matrix?.optString("terraformRevision", "-").orEmpty().ifBlank { "-" },
                termuxCliVersion = matrix?.optString("termuxCliVersion", "1.5.0").orEmpty().ifBlank { "1.5.0" },
                versionDriftDetected = matrix?.optBoolean("driftDetected", false) ?: false,
                auditChainStatus = auditVerify?.optString("status", "UNVERIFIED").orEmpty().ifBlank { "UNVERIFIED" },
                auditVerifiedCount = auditVerify?.optInt("checkedCount", 0) ?: 0,
                lastChaosScorePct = if (chaos != null && chaos.has("scorePct") && !chaos.isNull("scorePct")) chaos.optInt("scorePct") else null,
                lastChaosDrilledAt = chaos?.optString("drilledAt", "").orEmpty(),
                dlpRedactionsCount = sec?.optInt("dlpRedactionsCount", 0) ?: 0,
                taintBlocksCount = sec?.optInt("taintBlocksCount", 0) ?: 0,
                federatedNodes = parsedNodes,
                preferredNodeId = fed?.optString("preferredNodeId", "default-termux").orEmpty().ifBlank { "default-termux" },
                lastTuningPrUrl = tuning?.optString("prUrl", "").orEmpty(),
                lastTuningSummary = tuning?.optString("summary", "").orEmpty(),
                lastDrSnapshotKey = dr?.optString("lastSnapshotKey", "").orEmpty(),
                lastDrSnapshotAt = dr?.optString("lastSnapshotAt", "").orEmpty(),
                kvWritesToday = freeTier?.optInt("kvWritesToday", 0) ?: 0,
                kvDailyLimit = freeTier?.optInt("kvDailyLimit", 1000) ?: 1000,
                aiNeuronsToday = freeTier?.optInt("aiNeuronsToday", 0) ?: 0,
                aiNeuronDailyLimit = freeTier?.optInt("aiNeuronDailyLimit", 10000) ?: 10000,
                frugalFallbackActive = freeTier?.optBoolean("frugalFallbackActive", false) ?: false,
                deadmanConfigured = deadman?.optBoolean("configured", false) ?: false,
                lastDeadmanPingAt = deadman?.optString("lastPingAt", "").orEmpty(),
                goldenScorePct = tuning?.optInt("goldenScorePct", 100) ?: 100,
                autoTuneBlockedByGolden = tuning?.optBoolean("blockedByGoldenGate", false) ?: false,
                drKdfAlgorithm = dr?.optString("kdf", "PBKDF2-SHA256").orEmpty().ifBlank { "PBKDF2-SHA256" },
                hardwareKeyRegistered = !hwAuth?.optString("registeredKeyId", "").isNullOrBlank(),
                hardwareKeyId = hwAuth?.optString("registeredKeyId", "").orEmpty(),
                ciMinutesUsedMonth = ciQuota?.optInt("minutesUsedMonth", 0) ?: 0,
                ciMinutesLimit = ciQuota?.optInt("minutesLimit", 2000) ?: 2000,
                ciOffloadActive = ciQuota?.optBoolean("offloadActive", false) ?: false,
                actualKvWrites = telemetry?.optInt("actualKvWrites", 0) ?: 0,
                actualR2Ops = telemetry?.optInt("actualR2Ops", 0) ?: 0,
                actualWorkerCpuMs = telemetry?.optInt("actualCpuMs", 0) ?: 0,
                deltaKvWrites = telemetry?.optInt("deltaKvWrites", 0) ?: 0,
                telemetrySyncedAt = telemetry?.optString("syncedAt", "").orEmpty(),
                finopsBreakdown = finopsMap,
                approvals = approvals,
                recentAuditLogs = auditLogs,
                statusMessage = if (stateJson.optBoolean("safeMode", false)) {
                    "SAFE_MODE 作動中: ${stateJson.optString("safeReason", "レート制限/予算超過")}"
                } else {
                    "ハードニング監視正常 • 承認待ち ${approvals.size}件 • DLQ ${stateJson.optInt("dlqCount", 0)}件"
                }
            )
        } catch (e: Exception) {
            OrchestrationHardeningState(
                statusMessage = e.localizedMessage ?: "ハードニング状態の取得に失敗しました"
            )
        }
    }

    suspend fun decideApproval(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        approvalId: String,
        approve: Boolean,
        challenge: String? = null,
        actionHash: String? = null,
        signatureB64: String? = null,
        keyId: String? = null
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val body = JSONObject().apply {
            put("approvalId", approvalId)
            put("approve", approve)
            if (!challenge.isNullOrBlank()) put("challenge", challenge)
            if (!actionHash.isNullOrBlank()) put("actionHash", actionHash)
            if (!signatureB64.isNullOrBlank()) put("signatureB64", signatureB64)
            if (!keyId.isNullOrBlank()) put("keyId", keyId)
        }.toString()
        executePost("$cleanBase/v1/approvals/decide", bearerToken, firebaseIdToken, body) { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val detail = json?.optString("detail").orEmpty().ifBlank { raw.take(180) }
            val trace = json?.optString("traceId").orEmpty()
            val label = if (approve) "承認実行" else "却下"
            BridgeCallResult(
                success = code in 200..299 && (json?.optBoolean("ok", true) == true),
                statusCode = code,
                message = "$label ($approvalId): $detail" + if (trace.isNotBlank()) " [tid:$trace]" else ""
            )
        }
    }

    suspend fun toggleCircuitBreakerSafeMode(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        enableSafeMode: Boolean,
        reason: String
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val body = JSONObject().apply {
            put("safeMode", enableSafeMode)
            put("reason", reason)
        }.toString()
        executePost("$cleanBase/v1/circuit-breaker", bearerToken, firebaseIdToken, body) { code, raw ->
            val ok = code in 200..299
            val stateLabel = if (enableSafeMode) "SAFE_MODE 有効化" else "SAFE_MODE 解除"
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) "$stateLabel 完了" else "$stateLabel 失敗 (HTTP $code: ${raw.take(120)})"
            )
        }
    }

    suspend fun setOperatingMode(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        mode: String
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val body = JSONObject().apply {
            put("mode", mode)
        }.toString()
        executePost("$cleanBase/v1/sustainability/mode", bearerToken, firebaseIdToken, body) { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val flushed = json?.optInt("flushedGreenTasks", 0) ?: 0
            val suffix = if (flushed > 0) "（GreenOps 待機 ${flushed}件を再解放）" else ""
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) "稼働モードを $mode に切替しました$suffix" else "稼働モード切替失敗 (HTTP $code: ${raw.take(120)})"
            )
        }
    }

    suspend fun syncGovernancePolicy(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        policyJsonRaw: String
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        executePost("$cleanBase/v1/policy/sync", bearerToken, firebaseIdToken, policyJsonRaw) { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val version = json?.optString("policyVersion").orEmpty()
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) "Policy-as-Code v$version を単一ソース同期しました" else "ポリシー同期失敗 (HTTP $code: ${raw.take(120)})"
            )
        }
    }

    suspend fun verifyAuditChain(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        try {
            val (code, raw) = executeGet("$cleanBase/v1/audit/verify", bearerToken, firebaseIdToken)
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val status = json?.optString("status", "UNVERIFIED").orEmpty()
            val checked = json?.optInt("checkedCount", 0) ?: 0
            val ok = code in 200..299 && status == "VERIFIED"
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (code in 200..299) {
                    "監査ハッシュ検証: $status (${checked}件チェック)"
                } else {
                    "監査ハッシュ検証失敗 (HTTP $code: ${raw.take(120)})"
                }
            )
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "監査検証に失敗しました")
        }
    }

    suspend fun runChaosDrill(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val body = JSONObject().apply { put("dry_run", true) }.toString()
        executePost("$cleanBase/v1/chaos/drill", bearerToken, firebaseIdToken, body) { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", false) == true)
            val score = json?.optInt("scorePct", 0) ?: 0
            val passed = json?.optInt("passedChecks", 0) ?: 0
            val total = json?.optInt("totalChecks", 0) ?: 0
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) "カオス診断 (Dry-Run) 完了: スコア $score% ($passed/$total 合格)" else "カオス診断失敗 (HTTP $code: ${raw.take(120)})"
            )
        }
    }

    suspend fun registerFederatedNode(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        nodeId: String,
        nodeType: String,
        capabilities: List<String>,
        batteryPct: Int = 100,
        charging: Boolean = true
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val body = JSONObject().apply {
            put("nodeId", nodeId)
            put("nodeType", nodeType)
            put("capabilities", JSONArray(capabilities))
            put("battery", batteryPct)
            put("charging", charging)
        }.toString()
        executePost("$cleanBase/v1/nodes/register", bearerToken, firebaseIdToken, body) { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val count = json?.optInt("activeNodesCount", 1) ?: 1
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) "ノード '$nodeId' を登録しました (稼働ノード計 ${count}台)" else "ノード登録失敗 (HTTP $code: ${raw.take(120)})"
            )
        }
    }

    suspend fun triggerPolicyAutoTune(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        ownerRepo: String
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val body = JSONObject().apply {
            put("repo", ownerRepo)
        }.toString()
        executePost("$cleanBase/v1/policy/auto-tune", bearerToken, firebaseIdToken, body) { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val summary = json?.optString("proposalSummary").orEmpty()
            val prUrl = json?.optString("prUrl").orEmpty()
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) {
                    "自己進化ルール分析完了: ${summary.ifBlank { "提案生成" }}${if (prUrl.isNotBlank()) " ($prUrl)" else ""}"
                } else {
                    "自己進化 PR 失敗 (HTTP $code: ${raw.take(120)})"
                }
            )
        }
    }

    suspend fun createDrSnapshot(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        executePost("$cleanBase/v1/dr/snapshot", bearerToken, firebaseIdToken, "{}") { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val key = json?.optString("snapshotKey").orEmpty()
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) "暗号化 DR スナップショット保存完了 ($key)" else "DR 保存失敗 (HTTP $code: ${raw.take(120)})"
            )
        }
    }

    suspend fun restoreDrSnapshot(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        executePost("$cleanBase/v1/dr/restore", bearerToken, firebaseIdToken, "{}") { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val restored = json?.optInt("restoredKeysCount", 0) ?: 0
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) "DR スナップショットから ${restored}件の制御状態を復元しました" else "DR 復元失敗 (HTTP $code: ${raw.take(120)})"
            )
        }
    }

    suspend fun pingDeadmanSwitch(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        executePost("$cleanBase/v1/health/deadman-ping", bearerToken, firebaseIdToken, "{}") { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val pinged = json?.optBoolean("pinged", false) ?: false
            val msg = when {
                !ok -> "Dead Man Ping 失敗 (HTTP $code: ${raw.take(120)})"
                pinged -> "外部 Dead Man's Switch へ疎通送信しました"
                else -> "Dead Man Ping 記録完了 (DEADMAN_PING_URL 未設定)"
            }
            BridgeCallResult(success = ok, statusCode = code, message = msg)
        }
    }

    private fun newTraceparent(): String {
        val traceHex = java.util.UUID.randomUUID().toString().replace("-", "").lowercase()
        val spanHex = java.util.UUID.randomUUID().toString().replace("-", "").take(16).lowercase()
        return "00-$traceHex-$spanHex-01"
    }

    private fun executeGet(url: String, bearerToken: String, firebaseIdToken: String?): Pair<Int, String> {
        val builder = Request.Builder().url(url).header("traceparent", newTraceparent()).get()
        if (bearerToken.isNotBlank()) builder.header("Authorization", "Bearer ${bearerToken.trim()}")
        if (!firebaseIdToken.isNullOrBlank()) builder.header("X-Firebase-Token", firebaseIdToken)
        return client.newCall(builder.build()).execute().use { resp ->
            resp.code to resp.body?.string().orEmpty()
        }
    }

    private fun executePost(
        url: String,
        bearerToken: String,
        firebaseIdToken: String?,
        payload: String,
        mapper: (Int, String) -> BridgeCallResult
    ): BridgeCallResult {
        return try {
            val builder = Request.Builder()
                .url(url)
                .header("traceparent", newTraceparent())
                .post(payload.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            if (!firebaseIdToken.isNullOrBlank()) builder.header("X-Firebase-Token", firebaseIdToken)
            client.newCall(builder.build()).execute().use { resp ->
                mapper(resp.code, resp.body?.string().orEmpty())
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Request failed")
        }
    }

    private fun parseApprovals(raw: String): List<PendingApprovalItem> {
        val arr = runCatching { JSONObject(raw).optJSONArray("approvals") }.getOrNull() ?: return emptyList()
        return (0 until arr.length()).mapNotNull { idx ->
            val obj = arr.optJSONObject(idx) ?: return@mapNotNull null
            val id = obj.optString("approvalId").ifBlank { return@mapNotNull null }
            val conf = if (obj.has("confidence") && !obj.isNull("confidence")) obj.optDouble("confidence") else null
            val warnArr = obj.optJSONArray("criticWarnings")
            val warnings = if (warnArr != null) {
                (0 until warnArr.length()).mapNotNull { warnArr.optString(it).takeIf { w -> w.isNotBlank() } }
            } else emptyList()
            PendingApprovalItem(
                approvalId = id,
                action = obj.optString("action", ""),
                repo = obj.optString("repo", ""),
                targetEnv = obj.optString("targetEnv", "dev"),
                summary = obj.optString("summary", ""),
                traceId = obj.optString("traceId", ""),
                expiresAtMs = obj.optLong("expiresAtMs", 0L),
                aiConfidence = conf,
                aiReasoning = obj.optString("reasoning", ""),
                actionHash = obj.optString("actionHash", ""),
                criticWarnings = warnings,
                criticRiskLevel = obj.optString("criticRiskLevel", "")
            )
        }
    }

    private fun parseNodes(arr: JSONArray?): List<FederationNodeItem> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).mapNotNull { idx ->
            val obj = arr.optJSONObject(idx) ?: return@mapNotNull null
            val id = obj.optString("nodeId").ifBlank { return@mapNotNull null }
            val capsArr = obj.optJSONArray("capabilities")
            val caps = if (capsArr != null) {
                (0 until capsArr.length()).mapNotNull { capsArr.optString(it).takeIf { s -> s.isNotBlank() } }
            } else emptyList()
            FederationNodeItem(
                nodeId = id,
                nodeType = obj.optString("nodeType", "termux"),
                capabilities = caps,
                batteryPct = if (obj.has("battery") && !obj.isNull("battery")) obj.optInt("battery") else null,
                tempC = if (obj.has("tempC") && !obj.isNull("tempC")) obj.optDouble("tempC") else null,
                charging = if (obj.has("charging") && !obj.isNull("charging")) obj.optBoolean("charging") else null,
                online = obj.optBoolean("online", true),
                lastSeenAt = obj.optString("lastSeenAt", "")
            )
        }
    }

    private fun parseAuditLogs(raw: String): List<AuditTraceEntry> {
        val arr = runCatching { JSONObject(raw).optJSONArray("logs") }.getOrNull() ?: return emptyList()
        return (0 until arr.length()).mapNotNull { idx ->
            val obj = arr.optJSONObject(idx) ?: return@mapNotNull null
            val conf = if (obj.has("confidence") && !obj.isNull("confidence")) obj.optDouble("confidence") else null
            AuditTraceEntry(
                traceId = obj.optString("traceId", ""),
                source = obj.optString("source", ""),
                taskType = obj.optString("taskType", ""),
                ok = obj.optBoolean("ok", false),
                summary = obj.optString("summary", ""),
                timestamp = obj.optString("timestamp", ""),
                traceparent = obj.optString("traceparent", ""),
                prevHash = obj.optString("prev_hash", ""),
                entryHash = obj.optString("entry_hash", ""),
                aiConfidence = conf,
                aiReasoning = obj.optString("reasoning", "")
            )
        }
    }

    private fun normalizeUrl(raw: String): String {
        val trimmed = raw.trim().ifBlank { "https://gc2223.com" }
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
        return withScheme.trimEnd('/')
    }
}
