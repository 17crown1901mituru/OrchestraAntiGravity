package com.example.data

import android.util.Base64
import com.example.model.CloudflareSyncStatus
import com.example.model.CloudflareWorkerSummary
import com.example.model.EmailRouteTarget
import com.example.model.GitHubSyncStatus
import com.example.model.OrchestrationRunResult
import com.example.model.OrchestrationTaskType
import com.example.model.SyncHealthState
import com.example.model.TerraformAction
import com.example.model.TermuxAgentSyncStatus
import com.example.model.TermuxCommandResult
import com.example.model.TermuxPresetCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

data class BridgeCallResult(
    val success: Boolean,
    val statusCode: Int,
    val message: String
)

class CloudBridgeClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
        .build(),
    private val githubApiBaseUrl: String = "https://api.github.com"
) {
    private val cleanGithubApiBase = githubApiBaseUrl.trimEnd('/')

    private val localClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(4, TimeUnit.SECONDS)
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun pingGc2223Gateway(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String? = null
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeBaseUrl(baseUrl)
        val url = "$cleanBase/health"
        try {
            val builder = Request.Builder().url(url).get()
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }
            if (!firebaseIdToken.isNullOrBlank()) {
                builder.header("X-Firebase-Token", firebaseIdToken)
            }
            client.newCall(builder.build()).execute().use { resp ->
                val body = resp.body?.string().orEmpty().take(300)
                BridgeCallResult(
                    success = resp.isSuccessful,
                    statusCode = resp.code,
                    message = if (resp.isSuccessful) {
                        "HTTP ${resp.code}: ${body.ifBlank { "Gateway OK" }}"
                    } else {
                        "HTTP ${resp.code}: ${body.ifBlank { resp.message }}"
                    }
                )
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Connection failed")
        }
    }

    suspend fun queryGc2223Gateway(
        baseUrl: String,
        bearerToken: String,
        prompt: String,
        firebaseIdToken: String? = null
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeBaseUrl(baseUrl)
        val url = "$cleanBase/v1/chat"
        try {
            val payload = JSONObject().apply {
                put("prompt", prompt)
                put("client", "antigravity-android")
            }.toString()

            val builder = Request.Builder()
                .url(url)
                .post(payload.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }
            if (!firebaseIdToken.isNullOrBlank()) {
                builder.header("X-Firebase-Token", firebaseIdToken)
            }

            client.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    return@withContext BridgeCallResult(
                        success = false,
                        statusCode = resp.code,
                        message = "Gateway HTTP ${resp.code}: ${raw.take(300).ifBlank { resp.message }}"
                    )
                }
                val parsedText = try {
                    val json = JSONObject(raw)
                    json.optString("reply").ifBlank {
                        json.optString("response").ifBlank { raw }
                    }
                } catch (_: Exception) {
                    raw
                }
                BridgeCallResult(true, resp.code, parsedText.ifBlank { "Empty response from gc2223.com" })
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Request to gc2223.com failed")
        }
    }

    suspend fun sendCostAlertWebhook(
        baseUrl: String,
        webhookPath: String,
        bearerToken: String,
        firebaseIdToken: String?,
        recipientEmail: String,
        dateKey: String,
        totalUsd: Double,
        thresholdUsd: Double,
        breakdownByProvider: Map<String, Double>
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeBaseUrl(baseUrl)
        val cleanPath = "/" + webhookPath.trim().trimStart('/').ifBlank { "v1/cost-alert" }
        val url = "$cleanBase$cleanPath"
        try {
            val breakdownJson = JSONObject()
            breakdownByProvider.forEach { (k, v) -> breakdownJson.put(k, v) }

            val payload = JSONObject().apply {
                put("recipientEmail", recipientEmail)
                put("dateKey", dateKey)
                put("totalUsd", totalUsd)
                put("thresholdUsd", thresholdUsd)
                put("breakdown", breakdownJson)
            }.toString()

            val builder = Request.Builder()
                .url(url)
                .post(payload.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }
            if (!firebaseIdToken.isNullOrBlank()) {
                builder.header("X-Firebase-Token", firebaseIdToken)
            }

            client.newCall(builder.build()).execute().use { resp ->
                val body = resp.body?.string().orEmpty().take(240)
                BridgeCallResult(
                    success = resp.isSuccessful,
                    statusCode = resp.code,
                    message = if (resp.isSuccessful) {
                        "Alert webhook HTTP ${resp.code}: ${body.ifBlank { "Dispatched" }}"
                    } else {
                        "Alert webhook HTTP ${resp.code}: ${body.ifBlank { resp.message }}"
                    }
                )
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Cost alert webhook unreachable")
        }
    }

    suspend fun verifyCloudflareToken(apiToken: String): BridgeCallResult =
        withContext(Dispatchers.IO) {
            if (apiToken.isBlank()) {
                return@withContext BridgeCallResult(false, 0, "Cloudflare API Token is empty.")
            }
            try {
                val request = Request.Builder()
                    .url("https://api.cloudflare.com/client/v4/user/tokens/verify")
                    .header("Authorization", "Bearer ${apiToken.trim()}")
                    .get()
                    .build()
                client.newCall(request).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    val summary = try {
                        val json = JSONObject(raw)
                        val ok = json.optBoolean("success", false)
                        val status = json.optJSONObject("result")?.optString("status").orEmpty()
                        if (ok) "Cloudflare Token valid (status: $status)" else raw.take(220)
                    } catch (_: Exception) {
                        raw.take(220)
                    }
                    BridgeCallResult(resp.isSuccessful, resp.code, summary)
                }
            } catch (e: Exception) {
                BridgeCallResult(false, 0, e.localizedMessage ?: "Cloudflare verify failed")
            }
        }

    suspend fun verifyGitHubPat(pat: String): BridgeCallResult =
        withContext(Dispatchers.IO) {
            if (pat.isBlank()) {
                return@withContext BridgeCallResult(false, 0, "GitHub PAT is empty.")
            }
            try {
                val request = Request.Builder()
                    .url("https://api.github.com/user")
                    .header("Authorization", "Bearer ${pat.trim()}")
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .get()
                    .build()
                client.newCall(request).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        return@withContext BridgeCallResult(false, resp.code, "GitHub HTTP ${resp.code}: ${raw.take(200)}")
                    }
                    val login = try {
                        JSONObject(raw).optString("login", "unknown")
                    } catch (_: Exception) {
                        "authenticated"
                    }
                    BridgeCallResult(true, resp.code, "GitHub connected as @$login")
                }
            } catch (e: Exception) {
                BridgeCallResult(false, 0, e.localizedMessage ?: "GitHub verify failed")
            }
        }

    suspend fun dispatchGitHubWorkflow(
        pat: String,
        ownerRepo: String,
        workflowFile: String,
        ref: String = "main",
        inputs: Map<String, String> = mapOf("triggered_by" to "antigravity-android")
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        if (pat.isBlank() || !ownerRepo.contains("/")) {
            return@withContext BridgeCallResult(false, 0, "Set GitHub PAT and owner/repo (e.g. user/repo) first.")
        }
        val cleanRepo = ownerRepo.trim().removePrefix("https://github.com/").trim('/')
        val cleanWorkflow = workflowFile.trim().ifBlank { "antigravity-deploy.yml" }
        val url = "$cleanGithubApiBase/repos/$cleanRepo/actions/workflows/$cleanWorkflow/dispatches"
        try {
            val inputsJson = JSONObject()
            inputs.forEach { (k, v) -> inputsJson.put(k, v) }
            val bodyJson = JSONObject().apply {
                put("ref", ref)
                put("inputs", inputsJson)
            }.toString()
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer ${pat.trim()}")
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .post(bodyJson.toRequestBody(jsonMediaType))
                .build()
            client.newCall(request).execute().use { resp ->
                if (resp.code == 204 || resp.isSuccessful) {
                    BridgeCallResult(true, resp.code, "Dispatched $cleanWorkflow on $cleanRepo ($ref)")
                } else {
                    val err = resp.body?.string().orEmpty().take(240)
                    BridgeCallResult(false, resp.code, "HTTP ${resp.code}: ${err.ifBlank { resp.message }}")
                }
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Workflow dispatch failed")
        }
    }

    suspend fun dispatchTerraformWorkflow(
        pat: String,
        ownerRepo: String,
        workflowFile: String = "antigravity-terraform.yml",
        action: TerraformAction,
        targetEnv: String = "dev",
        ref: String = "main",
        retryCount: Int = 0
    ): BridgeCallResult = dispatchGitHubWorkflow(
        pat = pat,
        ownerRepo = ownerRepo,
        workflowFile = workflowFile.ifBlank { "antigravity-terraform.yml" },
        ref = ref,
        inputs = mapOf(
            "tf_action" to action.cliValue,
            "target_env" to targetEnv.ifBlank { "dev" },
            "triggered_by" to "antigravity-android",
            "retry_count" to retryCount.toString()
        )
    )

    suspend fun commitFileToGitHub(
        pat: String,
        ownerRepo: String,
        filePath: String,
        content: String,
        commitMessage: String,
        branch: String = "main"
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        if (pat.isBlank() || !ownerRepo.contains("/")) {
            return@withContext BridgeCallResult(false, 0, "GitHub PAT と owner/repo をセキュア設定で登録してください。")
        }
        val cleanRepo = ownerRepo.trim().removePrefix("https://github.com/").trim('/')
        val cleanPath = filePath.trim().trimStart('/')
        if (cleanPath.isBlank()) {
            return@withContext BridgeCallResult(false, 0, "ファイルパスが空です。")
        }
        val url = "$cleanGithubApiBase/repos/$cleanRepo/contents/$cleanPath"
        try {
            var existingSha: String? = null
            val getReq = Request.Builder()
                .url("$url?ref=$branch")
                .header("Authorization", "Bearer ${pat.trim()}")
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .get()
                .build()
            client.newCall(getReq).execute().use { getResp ->
                if (getResp.isSuccessful) {
                    val existingJson = runCatching { JSONObject(getResp.body?.string().orEmpty()) }.getOrNull()
                    existingSha = existingJson?.optString("sha")?.takeIf { it.isNotBlank() }
                }
            }

            val encodedContent = Base64.encodeToString(content.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            val bodyJson = JSONObject().apply {
                put("message", commitMessage)
                put("content", encodedContent)
                put("branch", branch)
                if (!existingSha.isNullOrBlank()) {
                    put("sha", existingSha)
                }
            }.toString()

            val putReq = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer ${pat.trim()}")
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .put(bodyJson.toRequestBody(jsonMediaType))
                .build()

            client.newCall(putReq).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (resp.isSuccessful) {
                    val commitSha = runCatching {
                        JSONObject(raw).optJSONObject("commit")?.optString("sha")?.take(7)
                    }.getOrNull().orEmpty()
                    BridgeCallResult(
                        success = true,
                        statusCode = resp.code,
                        message = "GitHub Push 完了: $cleanPath ($branch ${if (commitSha.isNotBlank()) commitSha else ""})".trim()
                    )
                } else {
                    BridgeCallResult(false, resp.code, "GitHub Push HTTP ${resp.code}: ${raw.take(220)}")
                }
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "GitHub file commit failed")
        }
    }

    suspend fun rerunFailedGitHubWorkflow(
        pat: String,
        ownerRepo: String,
        runId: Long
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        if (pat.isBlank() || !ownerRepo.contains("/") || runId <= 0L) {
            return@withContext BridgeCallResult(false, 0, "有効な Run ID・GitHub PAT・owner/repo が必要です。")
        }
        val cleanRepo = ownerRepo.trim().removePrefix("https://github.com/").trim('/')
        val rerunFailedUrl = "$cleanGithubApiBase/repos/$cleanRepo/actions/runs/$runId/rerun-failed-jobs"
        try {
            val req = Request.Builder()
                .url(rerunFailedUrl)
                .header("Authorization", "Bearer ${pat.trim()}")
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .post("".toRequestBody(jsonMediaType))
                .build()
            client.newCall(req).execute().use { resp ->
                if (resp.code == 201 || resp.isSuccessful) {
                    return@withContext BridgeCallResult(true, resp.code, "失敗ジョブ (Run #$runId) の再実行を開始しました")
                }
                val err = resp.body?.string().orEmpty().take(200)
                BridgeCallResult(false, resp.code, "再実行 HTTP ${resp.code}: ${err.ifBlank { resp.message }}")
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Workflow rerun failed")
        }
    }

    suspend fun triggerEmailSelfHealWorker(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        recipientEmail: String,
        ownerRepo: String,
        workflowFile: String,
        runId: Long,
        errorLog: String,
        suggestedPatch: String,
        retryCount: Int
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeBaseUrl(baseUrl)
        val url = "$cleanBase/v1/workflow-error"
        try {
            val payload = JSONObject().apply {
                put("recipientEmail", recipientEmail)
                put("repo", ownerRepo)
                put("workflow", workflowFile)
                put("runId", runId)
                put("errorLog", errorLog.take(6000))
                put("suggestedPatch", suggestedPatch.take(6000))
                put("retryCount", retryCount)
                put("source", "antigravity-android")
            }.toString()

            val builder = Request.Builder()
                .url(url)
                .post(payload.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }
            if (!firebaseIdToken.isNullOrBlank()) {
                builder.header("X-Firebase-Token", firebaseIdToken)
            }

            client.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty().take(260)
                BridgeCallResult(
                    success = resp.isSuccessful,
                    statusCode = resp.code,
                    message = if (resp.isSuccessful) {
                        "Email Worker 自己修復送信完了 (HTTP ${resp.code}): ${raw.ifBlank { "Queued" }}"
                    } else {
                        "Email Worker HTTP ${resp.code}: ${raw.ifBlank { resp.message }}"
                    }
                )
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Email Worker endpoint unreachable")
        }
    }

    suspend fun triggerOrchestrationTask(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        routeTarget: EmailRouteTarget,
        taskType: OrchestrationTaskType,
        ownerRepo: String,
        promptOrInput: String,
        targetEnv: String = "dev",
        termuxPreset: TermuxPresetCommand = TermuxPresetCommand.GIT_PULL
    ): OrchestrationRunResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeBaseUrl(baseUrl)
        val url = "$cleanBase/v1/orchestrate"
        val traceHex = java.util.UUID.randomUUID().toString().replace("-", "").lowercase()
        val spanHex = java.util.UUID.randomUUID().toString().replace("-", "").take(16).lowercase()
        val traceId = traceHex.take(12)
        val traceparent = "00-$traceHex-$spanHex-01"
        try {
            val innerPayload = JSONObject().apply {
                put("prompt", promptOrInput.trim())
                put("targetEnv", targetEnv.ifBlank { "dev" })
                put("preset", termuxPreset.presetId)
                if (promptOrInput.contains("http") || promptOrInput.contains(".")) {
                    put("url", promptOrInput.trim())
                }
            }
            val payload = JSONObject().apply {
                put("traceId", traceId)
                put("traceparent", traceparent)
                put("taskType", taskType.taskId)
                put("routeAddress", routeTarget.address)
                put("repo", ownerRepo.trim())
                put("payload", innerPayload)
            }.toString()

            val builder = Request.Builder()
                .url(url)
                .header("traceparent", traceparent)
                .post(payload.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }
            if (!firebaseIdToken.isNullOrBlank()) {
                builder.header("X-Firebase-Token", firebaseIdToken)
            }

            client.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                val json = runCatching { JSONObject(raw) }.getOrNull()
                val ok = resp.isSuccessful && (json?.optBoolean("ok", true) == true)
                val returnedTraceId = json?.optString("traceId").orEmpty().ifBlank { traceId }
                val returnedTraceparent = json?.optString("traceparent").orEmpty().ifBlank { traceparent }
                val confidence = if (json != null && json.has("confidence") && !json.isNull("confidence")) {
                    json.optDouble("confidence")
                } else null
                val reasoning = json?.optString("reasoning").orEmpty()
                val detail = json?.optString("detail").orEmpty().ifBlank { raw.take(600) }
                val summary = if (ok) {
                    "${routeTarget.address} -> ${taskType.label} 実行完了 (HTTP ${resp.code})"
                } else {
                    val hint = if (resp.code == 404) {
                        "（Deploy Kit から worker/src/worker.js を Push・デプロイしてください）"
                    } else ""
                    "オーケストレーション HTTP ${resp.code}: ${detail.take(160)}$hint"
                }
                OrchestrationRunResult(
                    success = ok,
                    routeAddress = routeTarget.address,
                    taskId = taskType.taskId,
                    statusCode = resp.code,
                    summary = summary,
                    detail = detail,
                    traceId = returnedTraceId,
                    traceparent = returnedTraceparent,
                    aiConfidence = confidence,
                    aiReasoning = reasoning
                )
            }
        } catch (e: Exception) {
            OrchestrationRunResult(
                success = false,
                routeAddress = routeTarget.address,
                taskId = taskType.taskId,
                statusCode = 0,
                summary = e.localizedMessage ?: "オーケストレーション接続失敗",
                detail = e.localizedMessage.orEmpty(),
                traceId = traceId,
                traceparent = traceparent
            )
        }
    }

    suspend fun fetchPendingTermuxQueueTask(
        baseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?
    ): BridgeCallResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeBaseUrl(baseUrl)
        val url = "$cleanBase/v1/termux/next-task"
        try {
            val builder = Request.Builder().url(url).get()
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }
            if (!firebaseIdToken.isNullOrBlank()) {
                builder.header("X-Firebase-Token", firebaseIdToken)
            }
            client.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                val json = runCatching { JSONObject(raw) }.getOrNull()
                val hasTask = json?.optBoolean("hasTask", false) == true
                val preset = json?.optJSONObject("task")?.optString("preset").orEmpty()
                val msg = when {
                    !resp.isSuccessful -> "Termux キュー確認 HTTP ${resp.code}: ${raw.take(120)}"
                    hasTask -> "保留タスクあり: preset=$preset（Termux で `antigravity pull-task` 実行可）"
                    else -> "Cloudflare KV キューに保留中の Termux タスクはありません"
                }
                BridgeCallResult(resp.isSuccessful, resp.code, msg)
            }
        } catch (e: Exception) {
            BridgeCallResult(false, 0, e.localizedMessage ?: "Termux queue check failed")
        }
    }

    private val gitHubActivityService by lazy {
        GitHubActivityService(client = client, apiBaseUrl = cleanGithubApiBase)
    }

    suspend fun fetchGitHubSyncStatus(
        pat: String,
        ownerRepo: String,
        workflowFile: String
    ): GitHubSyncStatus = gitHubActivityService.fetchRepositoryActivitySummary(
        pat = pat,
        ownerRepo = ownerRepo,
        workflowFile = workflowFile
    )

    suspend fun fetchCloudflareSyncStatus(
        apiToken: String,
        accountId: String,
        gatewayUrl: String,
        bearerToken: String,
        firebaseIdToken: String? = null
    ): CloudflareSyncStatus = withContext(Dispatchers.IO) {
        val cleanToken = apiToken.trim()
        val cleanGateway = normalizeBaseUrl(gatewayUrl)
        var resolvedAccountId = accountId.trim()
        var accountName = ""
        var tokenStatus = ""
        val workersList = mutableListOf<CloudflareWorkerSummary>()
        var apiError: String? = null

        // 1. Check gc2223.com Gateway /health latency
        val startMs = System.currentTimeMillis()
        val gatewayResult = pingGc2223Gateway(
            baseUrl = cleanGateway,
            bearerToken = bearerToken,
            firebaseIdToken = firebaseIdToken
        )
        val latencyMs = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)

        if (cleanToken.isBlank()) {
            val health = if (gatewayResult.success) SyncHealthState.PARTIAL else SyncHealthState.MISSING_CREDENTIALS
            val msg = if (gatewayResult.success) {
                "Gateway OK (${latencyMs}ms) • Cloudflare API Token 未入力"
            } else {
                "Cloudflare API Token 未入力 (${gatewayResult.message})"
            }
            return@withContext CloudflareSyncStatus(
                health = health,
                gatewayUrl = cleanGateway,
                gatewayHttpCode = gatewayResult.statusCode,
                gatewayLatencyMs = latencyMs,
                gatewayHealthMessage = gatewayResult.message,
                summaryMessage = msg
            )
        }

        try {
            // 2. Verify Cloudflare Token
            val verifyReq = Request.Builder()
                .url("https://api.cloudflare.com/client/v4/user/tokens/verify")
                .header("Authorization", "Bearer $cleanToken")
                .get()
                .build()
            client.newCall(verifyReq).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                val json = runCatching { JSONObject(raw) }.getOrNull()
                val ok = resp.isSuccessful && (json?.optBoolean("success", false) == true)
                tokenStatus = json?.optJSONObject("result")?.optString("status").orEmpty()
                if (!ok) {
                    val errDetail = parseCloudflareError(json, raw, resp.code)
                    return@withContext CloudflareSyncStatus(
                        health = SyncHealthState.ERROR,
                        tokenStatus = tokenStatus.ifBlank { "invalid" },
                        gatewayUrl = cleanGateway,
                        gatewayHttpCode = gatewayResult.statusCode,
                        gatewayLatencyMs = latencyMs,
                        gatewayHealthMessage = gatewayResult.message,
                        summaryMessage = "Cloudflare Token エラー: $errDetail"
                    )
                }
            }

            // 3. Auto-discover Account ID if empty or resolve account name
            val accountsReq = Request.Builder()
                .url("https://api.cloudflare.com/client/v4/accounts?per_page=5")
                .header("Authorization", "Bearer $cleanToken")
                .get()
                .build()
            client.newCall(accountsReq).execute().use { resp ->
                if (resp.isSuccessful) {
                    val json = runCatching { JSONObject(resp.body?.string().orEmpty()) }.getOrNull()
                    val arr = json?.optJSONArray("result")
                    if (arr != null && arr.length() > 0) {
                        var matched: JSONObject? = null
                        if (resolvedAccountId.isNotBlank()) {
                            for (i in 0 until arr.length()) {
                                val candidate = arr.optJSONObject(i) ?: continue
                                if (candidate.optString("id") == resolvedAccountId) {
                                    matched = candidate
                                    break
                                }
                            }
                        }
                        val chosen = matched ?: arr.optJSONObject(0)
                        if (chosen != null) {
                            if (resolvedAccountId.isBlank()) {
                                resolvedAccountId = chosen.optString("id")
                            }
                            accountName = chosen.optString("name")
                        }
                    }
                }
            }

            // 4. List Cloudflare Workers scripts for the account
            if (resolvedAccountId.isNotBlank()) {
                val workersReq = Request.Builder()
                    .url("https://api.cloudflare.com/client/v4/accounts/$resolvedAccountId/workers/scripts")
                    .header("Authorization", "Bearer $cleanToken")
                    .get()
                    .build()
                client.newCall(workersReq).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    val json = runCatching { JSONObject(raw) }.getOrNull()
                    if (resp.isSuccessful && json?.optBoolean("success", false) == true) {
                        val arr = json.optJSONArray("result") ?: JSONArray()
                        val parsed = (0 until arr.length()).mapNotNull { idx ->
                            val item = arr.optJSONObject(idx) ?: return@mapNotNull null
                            val id = item.optString("id").ifBlank { return@mapNotNull null }
                            val modified = item.optString("modified_on", "")
                            CloudflareWorkerSummary(scriptId = id, modifiedOn = modified)
                        }.sortedByDescending { it.modifiedOn }
                        workersList.addAll(parsed)
                    } else {
                        apiError = parseCloudflareError(json, raw, resp.code)
                    }
                }
            } else {
                apiError = "Account ID 未設定（Tokenに Account:Read 権限が必要です）"
            }

            val health = when {
                apiError != null -> SyncHealthState.PARTIAL
                !gatewayResult.success -> SyncHealthState.PARTIAL
                else -> SyncHealthState.CONNECTED
            }
            val msg = when {
                apiError != null -> "Token active • Workers取得注意: $apiError"
                workersList.isNotEmpty() -> "${workersList.size} Worker(s) 稼働中 • Token: $tokenStatus"
                else -> "Token $tokenStatus • デプロイ済み Worker 0件"
            }

            CloudflareSyncStatus(
                health = health,
                tokenStatus = tokenStatus,
                accountName = accountName,
                accountId = resolvedAccountId,
                workerCount = workersList.size,
                workers = workersList.take(10),
                gatewayUrl = cleanGateway,
                gatewayHttpCode = gatewayResult.statusCode,
                gatewayLatencyMs = latencyMs,
                gatewayHealthMessage = gatewayResult.message,
                summaryMessage = msg
            )
        } catch (e: Exception) {
            CloudflareSyncStatus(
                health = SyncHealthState.ERROR,
                tokenStatus = tokenStatus,
                accountName = accountName,
                accountId = resolvedAccountId,
                gatewayUrl = cleanGateway,
                gatewayHttpCode = gatewayResult.statusCode,
                gatewayLatencyMs = latencyMs,
                gatewayHealthMessage = gatewayResult.message,
                summaryMessage = e.localizedMessage ?: "Cloudflare 同期に失敗しました"
            )
        }
    }

    suspend fun fetchTermuxAgentStatus(
        agentUrl: String,
        bearerToken: String = ""
    ): TermuxAgentSyncStatus = withContext(Dispatchers.IO) {
        val cleanBase = normalizeLocalAgentUrl(agentUrl)
        val startMs = System.currentTimeMillis()
        try {
            val statusReq = buildLocalRequest("$cleanBase/status", bearerToken)
            localClient.newCall(statusReq).execute().use { resp ->
                val latency = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)
                if (resp.code == 404) {
                    // Fall back to /health if custom daemon exposes /health instead of /status
                    val healthReq = buildLocalRequest("$cleanBase/health", bearerToken)
                    localClient.newCall(healthReq).execute().use { hResp ->
                        val hLatency = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)
                        val raw = hResp.body?.string().orEmpty()
                        return@withContext parseTermuxResponse(
                            endpointUrl = cleanBase,
                            statusCode = hResp.code,
                            isSuccessful = hResp.isSuccessful,
                            rawBody = raw,
                            latencyMs = hLatency
                        )
                    }
                }
                val raw = resp.body?.string().orEmpty()
                parseTermuxResponse(
                    endpointUrl = cleanBase,
                    statusCode = resp.code,
                    isSuccessful = resp.isSuccessful,
                    rawBody = raw,
                    latencyMs = latency
                )
            }
        } catch (e: ConnectException) {
            val latency = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)
            TermuxAgentSyncStatus(
                health = SyncHealthState.OFFLINE,
                endpointUrl = cleanBase,
                daemonState = "OFFLINE",
                latencyMs = latency,
                summaryMessage = "Termux CLI 未起動 ($cleanBase) — Termux で `antigravity serve` を実行してください"
            )
        } catch (e: SocketTimeoutException) {
            val latency = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)
            TermuxAgentSyncStatus(
                health = SyncHealthState.OFFLINE,
                endpointUrl = cleanBase,
                daemonState = "TIMEOUT",
                latencyMs = latency,
                summaryMessage = "Termux CLI 応答タイムアウト ($cleanBase)"
            )
        } catch (e: Exception) {
            val latency = (System.currentTimeMillis() - startMs).coerceAtLeast(1L)
            TermuxAgentSyncStatus(
                health = SyncHealthState.ERROR,
                endpointUrl = cleanBase,
                daemonState = "ERROR",
                latencyMs = latency,
                summaryMessage = e.localizedMessage ?: "Termux ステータス取得失敗"
            )
        }
    }

    suspend fun executeTermuxSafeCommand(
        agentUrl: String,
        bearerToken: String = "",
        preset: TermuxPresetCommand,
        workdir: String = ""
    ): TermuxCommandResult = withContext(Dispatchers.IO) {
        val cleanBase = normalizeLocalAgentUrl(agentUrl)
        val execClient = localClient.newBuilder()
            .readTimeout(190, TimeUnit.SECONDS)
            .build()
        try {
            val bodyJson = JSONObject().apply {
                put("preset", preset.presetId)
                if (workdir.isNotBlank()) put("workdir", workdir.trim())
            }.toString()

            val builder = Request.Builder()
                .url("$cleanBase/run")
                .post(bodyJson.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }

            execClient.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                val json = runCatching { JSONObject(raw) }.getOrNull()
                if (!resp.isSuccessful && json == null) {
                    return@withContext TermuxCommandResult(
                        success = false,
                        presetId = preset.presetId,
                        exitCode = 1,
                        stderr = raw.take(400).ifBlank { "HTTP ${resp.code}" },
                        summaryMessage = "Termux /run HTTP ${resp.code}: ${raw.take(160)}"
                    )
                }
                val ok = json?.optBoolean("ok", resp.isSuccessful) ?: resp.isSuccessful
                val exitCode = json?.optInt("exitCode", if (ok) 0 else 1) ?: if (ok) 0 else 1
                val stdout = json?.optString("stdout").orEmpty()
                val stderr = json?.optString("stderr").orEmpty().ifBlank {
                    json?.optString("error").orEmpty()
                }
                val durMs = json?.optLong("durationMs", 0L) ?: 0L
                val summary = if (ok) {
                    "${preset.label} 完了 (exit $exitCode, ${durMs}ms)"
                } else {
                    "${preset.label} エラー (exit $exitCode): ${stderr.lineSequence().firstOrNull().orEmpty().take(120)}"
                }
                TermuxCommandResult(
                    success = ok,
                    presetId = preset.presetId,
                    exitCode = exitCode,
                    stdout = stdout,
                    stderr = stderr,
                    durationMs = durMs,
                    summaryMessage = summary
                )
            }
        } catch (e: ConnectException) {
            TermuxCommandResult(
                success = false,
                presetId = preset.presetId,
                exitCode = 1,
                stderr = "Termux 未起動 ($cleanBase)",
                summaryMessage = "Termux で `antigravity serve` を起動してください ($cleanBase)"
            )
        } catch (e: Exception) {
            TermuxCommandResult(
                success = false,
                presetId = preset.presetId,
                exitCode = 1,
                stderr = e.localizedMessage ?: "Termux /run failed",
                summaryMessage = e.localizedMessage ?: "Termux ローカル実行に失敗しました"
            )
        }
    }

    private fun buildLocalRequest(url: String, bearerToken: String): Request {
        val builder = Request.Builder().url(url).get()
        if (bearerToken.isNotBlank()) {
            builder.header("Authorization", "Bearer ${bearerToken.trim()}")
        }
        return builder.build()
    }

    private fun parseTermuxResponse(
        endpointUrl: String,
        statusCode: Int,
        isSuccessful: Boolean,
        rawBody: String,
        latencyMs: Long
    ): TermuxAgentSyncStatus {
        if (!isSuccessful) {
            return TermuxAgentSyncStatus(
                health = SyncHealthState.ERROR,
                endpointUrl = endpointUrl,
                daemonState = "HTTP_$statusCode",
                latencyMs = latencyMs,
                summaryMessage = "Termux HTTP $statusCode: ${rawBody.take(140).ifBlank { "Unauthorized or error" }}"
            )
        }
        val json = runCatching { JSONObject(rawBody) }.getOrNull()
        val agentName = json?.optString("agentName").orEmpty().ifBlank {
            json?.optString("service").orEmpty().ifBlank { "AntiGravityAgent" }
        }
        val version = json?.optString("version").orEmpty()
        val daemonState = json?.optString("state").orEmpty().ifBlank { "RUNNING" }
        val pid = json?.optInt("pid", 0) ?: 0
        val activeTask = json?.optString("activeTask").orEmpty()
        val lastCmd = json?.optString("lastCommand").orEmpty()
        val exitCode = if (json != null && json.has("lastExitCode") && !json.isNull("lastExitCode")) {
            json.optInt("lastExitCode")
        } else {
            null
        }
        val lastRunAt = json?.optString("lastRunAt").orEmpty().ifBlank {
            json?.optString("timestamp").orEmpty()
        }
        val requestsToday = json?.optInt("requestsToday", 0) ?: 0
        val uptimeSec = json?.optLong("uptimeSeconds", 0L) ?: 0L
        val customSummary = json?.optString("summary").orEmpty()

        val summary = customSummary.ifBlank {
            buildString {
                append("$agentName $daemonState (${latencyMs}ms)")
                if (lastCmd.isNotBlank()) append(" • last: $lastCmd")
            }
        }

        return TermuxAgentSyncStatus(
            health = SyncHealthState.CONNECTED,
            endpointUrl = endpointUrl,
            agentName = agentName,
            version = version,
            daemonState = daemonState,
            pid = pid,
            activeTask = activeTask,
            lastCommand = lastCmd,
            lastExitCode = exitCode,
            lastRunAt = lastRunAt,
            requestsToday = requestsToday,
            uptimeSeconds = uptimeSec,
            latencyMs = latencyMs,
            summaryMessage = summary
        )
    }

    private fun parseCloudflareError(json: JSONObject?, raw: String, statusCode: Int): String {
        val firstMsg = json?.optJSONArray("errors")?.optJSONObject(0)?.optString("message").orEmpty()
        return firstMsg.ifBlank { "HTTP $statusCode ${raw.take(140)}" }
    }

    private fun normalizeBaseUrl(raw: String): String {
        val trimmed = raw.trim().ifBlank { "https://gc2223.com" }
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
        return withScheme.trimEnd('/')
    }

    private fun normalizeLocalAgentUrl(raw: String): String {
        val trimmed = raw.trim().ifBlank { "http://127.0.0.1:8787" }
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "http://$trimmed"
        }
        return withScheme.trimEnd('/')
    }
}
