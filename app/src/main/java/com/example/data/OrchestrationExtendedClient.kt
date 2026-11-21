package com.example.data

import com.example.model.AskRagCitation
import com.example.model.AskRagResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

data class ApprovalChallengeResult(
    val success: Boolean,
    val statusCode: Int,
    val approvalId: String = "",
    val actionHash: String = "",
    val challenge: String = "",
    val keyId: String = "",
    val requiresHardwareSignature: Boolean = false,
    val message: String = ""
)

class OrchestrationExtendedClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
        .build()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun registerHardwarePublicKey(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        keyId: String,
        spkiB64: String,
        alg: String = "ECDSA_P256_SHA256"
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val payload = JSONObject().apply {
            put("keyId", keyId)
            put("spkiB64", spkiB64)
            put("alg", alg)
        }.toString()
        executePost("$cleanBase/v1/approvals/register-key", bearerToken, firebaseIdToken, payload) { code, raw ->
            val json = runCatching { JSONObject(raw) }.getOrNull()
            val ok = code in 200..299 && (json?.optBoolean("ok", true) == true)
            val serverKeyId = json?.optString("keyId").orEmpty().ifBlank { keyId }
            BridgeCallResult(
                success = ok,
                statusCode = code,
                message = if (ok) {
                    "ハードウェア ECDSA P-256 公開鍵を登録しました (Key: $serverKeyId)"
                } else {
                    "生体署名鍵の登録失敗 (HTTP $code: ${raw.take(120)})"
                }
            )
        }
    }

    suspend fun requestApprovalChallenge(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        approvalId: String
    ): ApprovalChallengeResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val payload = JSONObject().apply { put("approvalId", approvalId) }.toString()
        try {
            val builder = Request.Builder()
                .url("$cleanBase/v1/approvals/challenge")
                .header("traceparent", newTraceparent())
                .post(payload.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            if (!firebaseIdToken.isNullOrBlank()) builder.header("X-Firebase-Token", firebaseIdToken)
            client.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                val json = runCatching { JSONObject(raw) }.getOrNull()
                val ok = resp.code in 200..299 && (json?.optBoolean("ok", false) == true)
                ApprovalChallengeResult(
                    success = ok,
                    statusCode = resp.code,
                    approvalId = json?.optString("approvalId").orEmpty().ifBlank { approvalId },
                    actionHash = json?.optString("actionHash").orEmpty(),
                    challenge = json?.optString("challenge").orEmpty(),
                    keyId = json?.optString("keyId").orEmpty(),
                    requiresHardwareSignature = json?.optBoolean("requiresHardwareSignature", false) ?: false,
                    message = if (ok) "Challenge issued" else "チャレンジ取得失敗 (HTTP ${resp.code}: ${raw.take(120)})"
                )
            }
        } catch (e: Exception) {
            ApprovalChallengeResult(
                success = false,
                statusCode = 0,
                approvalId = approvalId,
                message = e.localizedMessage ?: "チャレンジ通信エラー"
            )
        }
    }

    suspend fun askAuditKnowledge(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        question: String
    ): AskRagResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeUrl(baseUrl)
        val cleanQ = question.trim()
        val payload = JSONObject().apply { put("question", cleanQ) }.toString()
        try {
            val builder = Request.Builder()
                .url("$cleanBase/v1/ask")
                .header("traceparent", newTraceparent())
                .post(payload.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            if (!firebaseIdToken.isNullOrBlank()) builder.header("X-Firebase-Token", firebaseIdToken)
            client.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                val json = runCatching { JSONObject(raw) }.getOrNull()
                val ok = resp.code in 200..299 && (json?.optBoolean("ok", false) == true)
                val citationsArr = json?.optJSONArray("citations")
                val citations = if (citationsArr != null) {
                    (0 until citationsArr.length()).mapNotNull { i ->
                        val item = citationsArr.optJSONObject(i) ?: return@mapNotNull null
                        AskRagCitation(
                            traceId = item.optString("traceId", "-"),
                            source = item.optString("source", "audit"),
                            summary = item.optString("summary", ""),
                            score = item.optDouble("score", 0.0)
                        )
                    }
                } else emptyList()
                AskRagResult(
                    ok = ok,
                    question = cleanQ,
                    answer = json?.optString("answer").orEmpty().ifBlank {
                        if (ok) "該当する監査ログが見つかりませんでした。" else "RAG 検索エラー (HTTP ${resp.code})"
                    },
                    citations = citations,
                    usedEmbeddings = json?.optBoolean("usedEmbeddings", false) ?: false,
                    traceId = json?.optString("traceId").orEmpty(),
                    statusMessage = if (ok) {
                        "監査知識RAG検索完了 (引用 ${citations.size}件)"
                    } else {
                        "RAG 照会失敗 (HTTP ${resp.code}: ${raw.take(120)})"
                    }
                )
            }
        } catch (e: Exception) {
            AskRagResult(
                ok = false,
                question = cleanQ,
                answer = e.localizedMessage ?: "通信エラー",
                statusMessage = "RAG 通信エラー: ${e.localizedMessage ?: "Unknown"}"
            )
        }
    }

    private fun newTraceparent(): String {
        val traceHex = UUID.randomUUID().toString().replace("-", "").lowercase()
        val spanHex = UUID.randomUUID().toString().replace("-", "").take(16).lowercase()
        return "00-$traceHex-$spanHex-01"
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

    private fun normalizeUrl(raw: String): String {
        val trimmed = raw.trim().ifBlank { "https://gc2223.com" }
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
        return withScheme.trimEnd('/')
    }
}
