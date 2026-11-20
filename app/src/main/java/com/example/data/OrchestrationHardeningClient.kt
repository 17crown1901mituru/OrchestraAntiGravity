package com.example.data

import com.example.model.AuditTraceEntry
import com.example.model.OrchestrationHardeningState
import com.example.model.PendingApprovalItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OrchestrationHardeningClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
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
        approve: Boolean
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val body = JSONObject().apply {
            put("approvalId", approvalId)
            put("approve", approve)
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

    private fun executeGet(url: String, bearerToken: String, firebaseIdToken: String?): Pair<Int, String> {
        val builder = Request.Builder().url(url).get()
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
            val builder = Request.Builder().url(url).post(payload.toRequestBody(jsonMediaType))
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
            PendingApprovalItem(
                approvalId = id,
                action = obj.optString("action", ""),
                repo = obj.optString("repo", ""),
                targetEnv = obj.optString("targetEnv", "dev"),
                summary = obj.optString("summary", ""),
                traceId = obj.optString("traceId", ""),
                expiresAtMs = obj.optLong("expiresAtMs", 0L)
            )
        }
    }

    private fun parseAuditLogs(raw: String): List<AuditTraceEntry> {
        val arr = runCatching { JSONObject(raw).optJSONArray("logs") }.getOrNull() ?: return emptyList()
        return (0 until arr.length()).mapNotNull { idx ->
            val obj = arr.optJSONObject(idx) ?: return@mapNotNull null
            AuditTraceEntry(
                traceId = obj.optString("traceId", ""),
                source = obj.optString("source", ""),
                taskType = obj.optString("taskType", ""),
                ok = obj.optBoolean("ok", false),
                summary = obj.optString("summary", ""),
                timestamp = obj.optString("timestamp", "")
            )
        }
    }

    private fun normalizeUrl(raw: String): String {
        val trimmed = raw.trim().ifBlank { "https://gc2223.com" }
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
        return withScheme.trimEnd('/')
    }
}
