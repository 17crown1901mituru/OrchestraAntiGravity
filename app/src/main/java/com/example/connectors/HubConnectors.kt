package com.example.connectors

import com.example.data.CloudBridgeClient
import com.example.model.ExternalCredentials
import com.example.model.GitHubRepoSummary
import com.example.model.McpEndpointEntry
import com.example.model.SyncHealthState
import com.example.model.TermuxPresetCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

enum class ConnectorKind(val label: String) {
    GITHUB_OAUTH("GitHub OAuth"),
    TERMUX_CLI("Termux Local CLI"),
    CUSTOM_MCP("MCP Server")
}

data class ConnectorContext(
    val googleSub: String,
    val virtualAlias: String,
    val oauthAccessToken: String = "",
    val gcpProjectId: String = "",
    val googleIdToken: String = ""
)

data class ConnectorStatus(
    val connectorId: String,
    val kind: ConnectorKind,
    val title: String,
    val connected: Boolean,
    val detail: String,
    val accountScope: String = ""
)

data class McpToolDefinition(
    val name: String,
    val description: String,
    val inputSchemaJson: String = "{}",
    val providerId: String = ""
)

data class McpToolExecutionResult(
    val ok: Boolean,
    val toolName: String,
    val output: String,
    val endpointUrl: String = ""
)

data class GitHubDeviceCodeChallenge(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresInSec: Int = 900,
    val intervalSec: Int = 5
)

data class GitHubOrganizationSummary(
    val login: String,
    val description: String = ""
)

data class GitHubIdentitySummary(
    val login: String,
    val organizations: List<GitHubOrganizationSummary> = emptyList(),
    val repos: List<GitHubRepoSummary> = emptyList()
)

interface HubConnector {
    val id: String
    val kind: ConnectorKind
    val displayName: String
    suspend fun checkStatus(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): ConnectorStatus
}

interface McpToolProvider : HubConnector {
    suspend fun listTools(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): Result<List<McpToolDefinition>>

    suspend fun callTool(
        context: ConnectorContext,
        credentials: ExternalCredentials,
        toolName: String,
        argumentsJson: JSONObject = JSONObject()
    ): Result<McpToolExecutionResult>
}

class GitHubOAuthConnector(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build(),
    private val githubWebBaseUrl: String = "https://github.com",
    private val githubApiBaseUrl: String = "https://api.github.com",
    private val bridgeClient: CloudBridgeClient = CloudBridgeClient(githubApiBaseUrl = githubApiBaseUrl)
) : McpToolProvider {

    override val id: String = "github_oauth"
    override val kind: ConnectorKind = ConnectorKind.GITHUB_OAUTH
    override val displayName: String = "GitHub (OAuth Device Flow / Org)"

    private val cleanWebBase = githubWebBaseUrl.trimEnd('/')
    private val cleanApiBase = githubApiBaseUrl.trimEnd('/')
    private val formMediaType = "application/x-www-form-urlencoded; charset=utf-8".toMediaType()

    suspend fun startDeviceFlow(
        clientId: String,
        scopes: String = "repo workflow read:org"
    ): Result<GitHubDeviceCodeChallenge> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanClientId = clientId.trim()
            require(cleanClientId.isNotBlank()) {
                "GitHub OAuth App Client ID を入力してください（Client Secret 不要の Device Flow 対応 App）"
            }
            val formBody = "client_id=${URLEncoder.encode(cleanClientId, "UTF-8")}" +
                "&scope=${URLEncoder.encode(scopes.trim(), "UTF-8")}"
            val req = Request.Builder()
                .url("$cleanWebBase/login/device/code")
                .header("Accept", "application/json")
                .post(formBody.toRequestBody(formMediaType))
                .build()
            client.newCall(req).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw IllegalStateException("GitHub Device Flow HTTP ${resp.code}: ${raw.take(200)}")
                }
                val json = JSONObject(raw)
                val err = json.optString("error", "")
                if (err.isNotBlank()) {
                    throw IllegalStateException(json.optString("error_description", err))
                }
                val deviceCode = json.optString("device_code", "").trim()
                val userCode = json.optString("user_code", "").trim()
                val verificationUri = json.optString("verification_uri", "https://github.com/login/device").trim()
                require(deviceCode.isNotBlank() && userCode.isNotBlank()) {
                    "GitHub Device Flow のレスポンスに device_code / user_code がありません"
                }
                GitHubDeviceCodeChallenge(
                    deviceCode = deviceCode,
                    userCode = userCode,
                    verificationUri = verificationUri,
                    expiresInSec = json.optInt("expires_in", 900),
                    intervalSec = json.optInt("interval", 5).coerceAtLeast(1)
                )
            }
        }
    }

    suspend fun pollDeviceFlowToken(
        clientId: String,
        deviceCode: String,
        intervalSec: Int = 5,
        maxAttempts: Int = 24
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanClientId = clientId.trim()
            val cleanDeviceCode = deviceCode.trim()
            require(cleanClientId.isNotBlank() && cleanDeviceCode.isNotBlank()) {
                "clientId と deviceCode が必要です"
            }
            var waitMs = (intervalSec.coerceAtLeast(1) * 1000L)
            repeat(maxAttempts) { attempt ->
                if (attempt > 0) {
                    delay(waitMs)
                }
                val formBody = "client_id=${URLEncoder.encode(cleanClientId, "UTF-8")}" +
                    "&device_code=${URLEncoder.encode(cleanDeviceCode, "UTF-8")}" +
                    "&grant_type=${URLEncoder.encode("urn:ietf:params:oauth:grant-type:device_code", "UTF-8")}"
                val req = Request.Builder()
                    .url("$cleanWebBase/login/oauth/access_token")
                    .header("Accept", "application/json")
                    .post(formBody.toRequestBody(formMediaType))
                    .build()
                client.newCall(req).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException("GitHub OAuth Token HTTP ${resp.code}: ${raw.take(200)}")
                    }
                    val json = JSONObject(raw)
                    val accessToken = json.optString("access_token", "").trim()
                    if (accessToken.isNotBlank()) {
                        return@runCatching accessToken
                    }
                    val error = json.optString("error", "").trim()
                    when (error) {
                        "authorization_pending" -> Unit
                        "slow_down" -> waitMs += 5000L
                        else -> throw IllegalStateException(
                            json.optString("error_description", error.ifBlank { "GitHub OAuth 承認に失敗しました" })
                        )
                    }
                }
            }
            throw IllegalStateException("GitHub Device Flow のブラウザ承認がタイムアウトしました。再度「承認完了を確認」を押してください。")
        }
    }

    suspend fun fetchUserAndOrgs(
        accessToken: String,
        selectedOrg: String = ""
    ): Result<GitHubIdentitySummary> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanToken = accessToken.trim()
            require(cleanToken.isNotBlank()) { "GitHub トークンが未設定です" }

            val login = executeGitHubJsonGet("/user", cleanToken)
                ?.let { JSONObject(it).optString("login", "").trim() }
                .orEmpty()
            require(login.isNotBlank()) { "GitHub アカウント情報を取得できませんでした" }

            val orgsRaw = runCatching { executeGitHubJsonGet("/user/orgs?per_page=30", cleanToken) }.getOrNull()
            val orgs = if (!orgsRaw.isNullOrBlank()) {
                val arr = JSONArray(orgsRaw)
                (0 until arr.length()).mapNotNull { idx ->
                    val obj = arr.optJSONObject(idx) ?: return@mapNotNull null
                    val orgLogin = obj.optString("login", "").trim()
                    if (orgLogin.isBlank()) return@mapNotNull null
                    GitHubOrganizationSummary(
                        login = orgLogin,
                        description = obj.optString("description", "")
                    )
                }
            } else {
                emptyList()
            }

            val reposPath = if (selectedOrg.isNotBlank()) {
                "/orgs/${URLEncoder.encode(selectedOrg.trim(), "UTF-8")}/repos?per_page=30&sort=updated"
            } else {
                "/user/repos?per_page=30&sort=updated&affiliation=owner,organization_member"
            }
            val reposRaw = runCatching { executeGitHubJsonGet(reposPath, cleanToken) }.getOrNull()
            val repos = if (!reposRaw.isNullOrBlank()) {
                val arr = JSONArray(reposRaw)
                (0 until arr.length()).mapNotNull { idx ->
                    val obj = arr.optJSONObject(idx) ?: return@mapNotNull null
                    val fullName = obj.optString("full_name", "").trim()
                    if (fullName.isBlank()) return@mapNotNull null
                    GitHubRepoSummary(
                        fullName = fullName,
                        defaultBranch = obj.optString("default_branch", "main"),
                        isPrivate = obj.optBoolean("private", false),
                        openIssuesCount = obj.optInt("open_issues_count", 0),
                        pushedAt = obj.optString("pushed_at", "")
                    )
                }
            } else {
                emptyList()
            }

            GitHubIdentitySummary(
                login = login,
                organizations = orgs,
                repos = repos
            )
        }
    }

    private fun executeGitHubJsonGet(path: String, token: String): String? {
        val req = Request.Builder()
            .url("$cleanApiBase$path")
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/vnd.github+json")
            .get()
            .build()
        client.newCall(req).execute().use { resp ->
            val raw = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw IllegalStateException("GitHub API $path HTTP ${resp.code}: ${raw.take(160)}")
            }
            return raw
        }
    }

    override suspend fun checkStatus(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): ConnectorStatus {
        val token = credentials.githubPat.trim()
        if (token.isBlank()) {
            return ConnectorStatus(
                connectorId = id,
                kind = kind,
                title = displayName,
                connected = false,
                detail = "GitHub OAuth Device Flow または PAT 未連携 (sub=${context.googleSub.take(8)}…)",
                accountScope = context.virtualAlias
            )
        }
        return fetchUserAndOrgs(token, credentials.githubSelectedOrg).fold(
            onSuccess = { summary ->
                val orgNote = if (credentials.githubSelectedOrg.isNotBlank()) {
                    "Org:${credentials.githubSelectedOrg}"
                } else {
                    "Orgs:${summary.organizations.size}"
                }
                ConnectorStatus(
                    connectorId = id,
                    kind = kind,
                    title = displayName,
                    connected = true,
                    detail = "@${summary.login} ($orgNote, ${summary.repos.size} repos)",
                    accountScope = summary.login
                )
            },
            onFailure = { err ->
                ConnectorStatus(
                    connectorId = id,
                    kind = kind,
                    title = displayName,
                    connected = false,
                    detail = err.localizedMessage ?: "GitHub verification failed",
                    accountScope = context.virtualAlias
                )
            }
        )
    }

    override suspend fun listTools(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): Result<List<McpToolDefinition>> = Result.success(
        listOf(
            McpToolDefinition(
                name = "github_list_orgs_and_repos",
                description = "連携中アカウント/所属 GitHub Organization のリポジトリ一覧を取得",
                inputSchemaJson = """{"type":"object","properties":{"org":{"type":"string"}}}""",
                providerId = id
            ),
            McpToolDefinition(
                name = "github_dispatch_workflow",
                description = "指定リポジトリの GitHub Actions workflow_dispatch を実行",
                inputSchemaJson = """{"type":"object","properties":{"repo":{"type":"string"},"workflow":{"type":"string"},"ref":{"type":"string"}},"required":["repo","workflow"]}""",
                providerId = id
            )
        )
    )

    override suspend fun callTool(
        context: ConnectorContext,
        credentials: ExternalCredentials,
        toolName: String,
        argumentsJson: JSONObject
    ): Result<McpToolExecutionResult> = runCatching {
        val token = credentials.githubPat.trim()
        require(token.isNotBlank()) { "GitHub トークンが sub=${context.googleSub.take(8)}… のローカルボルトにありません" }
        when (toolName) {
            "github_list_orgs_and_repos" -> {
                val org = argumentsJson.optString("org", credentials.githubSelectedOrg).trim()
                val summary = fetchUserAndOrgs(token, org).getOrThrow()
                val out = JSONObject().apply {
                    put("login", summary.login)
                    put("organizations", JSONArray(summary.organizations.map { it.login }))
                    put("repos", JSONArray(summary.repos.map { it.fullName }))
                }.toString(2)
                McpToolExecutionResult(ok = true, toolName = toolName, output = out, endpointUrl = cleanApiBase)
            }
            "github_dispatch_workflow" -> {
                val repo = argumentsJson.optString("repo", credentials.githubOwnerRepo).trim()
                val workflow = argumentsJson.optString("workflow", credentials.githubWorkflowFile).trim()
                val ref = argumentsJson.optString("ref", "main").trim().ifBlank { "main" }
                require(repo.contains("/")) { "owner/repo が未設定です" }
                val res = bridgeClient.dispatchGitHubWorkflow(
                    pat = token,
                    ownerRepo = repo,
                    workflowFile = workflow,
                    ref = ref
                )
                McpToolExecutionResult(ok = res.success, toolName = toolName, output = res.message, endpointUrl = cleanApiBase)
            }
            else -> throw IllegalArgumentException("Unsupported GitHub connector tool: $toolName")
        }
    }
}

class TermuxCliConnector(
    private val localClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(4, TimeUnit.SECONDS)
        .build(),
    private val bridgeClient: CloudBridgeClient = CloudBridgeClient()
) : McpToolProvider {

    override val id: String = "termux_cli"
    override val kind: ConnectorKind = ConnectorKind.TERMUX_CLI
    override val displayName: String = "Termux / Local CLI Daemon"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun checkStatus(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): ConnectorStatus = withContext(Dispatchers.IO) {
        val cleanUrl = credentials.termuxAgentUrl.trim().trimEnd('/').ifBlank { "http://127.0.0.1:8787" }
        val reqBuilder = Request.Builder()
            .url("$cleanUrl/status")
            .header("X-Google-Sub", context.googleSub)
            .header("X-Virtual-Alias", context.virtualAlias)
            .apply {
                if (context.gcpProjectId.isNotBlank()) header("X-GCP-Project", context.gcpProjectId)
                if (credentials.termuxAgentToken.isNotBlank()) {
                    header("Authorization", "Bearer ${credentials.termuxAgentToken.trim()}")
                }
            }
            .get()
        runCatching {
            localClient.newCall(reqBuilder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty().take(240)
                if (resp.isSuccessful) {
                    ConnectorStatus(
                        connectorId = id,
                        kind = kind,
                        title = displayName,
                        connected = true,
                        detail = "Online ($cleanUrl) HTTP ${resp.code}: ${raw.ifBlank { "OK" }}",
                        accountScope = context.virtualAlias
                    )
                } else {
                    ConnectorStatus(
                        connectorId = id,
                        kind = kind,
                        title = displayName,
                        connected = false,
                        detail = "HTTP ${resp.code} ($cleanUrl)",
                        accountScope = context.virtualAlias
                    )
                }
            }
        }.getOrElse { err ->
            val sync = bridgeClient.fetchTermuxAgentStatus(cleanUrl, credentials.termuxAgentToken)
            ConnectorStatus(
                connectorId = id,
                kind = kind,
                title = displayName,
                connected = sync.health == SyncHealthState.CONNECTED,
                detail = sync.summaryMessage.ifBlank { err.localizedMessage ?: "Termux offline" },
                accountScope = context.virtualAlias
            )
        }
    }

    override suspend fun listTools(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): Result<List<McpToolDefinition>> = Result.success(
        TermuxPresetCommand.entries.map { preset ->
            McpToolDefinition(
                name = "termux_${preset.presetId}",
                description = "ローカル Termux CLI プリセット '${preset.label}' をセッション付きで実行",
                inputSchemaJson = """{"type":"object","properties":{"workdir":{"type":"string"}}}""",
                providerId = id
            )
        }
    )

    override suspend fun callTool(
        context: ConnectorContext,
        credentials: ExternalCredentials,
        toolName: String,
        argumentsJson: JSONObject
    ): Result<McpToolExecutionResult> = withContext(Dispatchers.IO) {
        runCatching {
            val presetId = toolName.removePrefix("termux_")
            val preset = TermuxPresetCommand.entries.firstOrNull { it.presetId == presetId }
                ?: throw IllegalArgumentException("許可されていない Termux プリセットです: $toolName")
            val cleanUrl = credentials.termuxAgentUrl.trim().trimEnd('/').ifBlank { "http://127.0.0.1:8787" }
            val workdir = argumentsJson.optString("workdir", "~/OrchestraAntiGravity")
            val bodyJson = JSONObject().apply {
                put("preset", preset.presetId)
                put("workdir", workdir)
                put("googleSub", context.googleSub)
                put("virtualAlias", context.virtualAlias)
                if (context.gcpProjectId.isNotBlank()) put("gcpProjectId", context.gcpProjectId)
            }.toString()
            val reqBuilder = Request.Builder()
                .url("$cleanUrl/run")
                .header("X-Google-Sub", context.googleSub)
                .header("X-Virtual-Alias", context.virtualAlias)
                .apply {
                    if (context.gcpProjectId.isNotBlank()) header("X-GCP-Project", context.gcpProjectId)
                    if (credentials.termuxAgentToken.isNotBlank()) {
                        header("Authorization", "Bearer ${credentials.termuxAgentToken.trim()}")
                    }
                }
                .post(bodyJson.toRequestBody(jsonMediaType))
            localClient.newCall(reqBuilder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw IllegalStateException("Termux /run HTTP ${resp.code}: ${raw.take(240)}")
                }
                val parsed = runCatching { JSONObject(raw) }.getOrNull()
                val ok = parsed?.optBoolean("ok", true) ?: true
                val outText = parsed?.optString("stdout", raw).orEmpty().ifBlank { raw }
                McpToolExecutionResult(ok = ok, toolName = toolName, output = outText, endpointUrl = cleanUrl)
            }
        }
    }
}

class RemoteMcpConnector(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
) : McpToolProvider {

    override val id: String = "remote_mcp"
    override val kind: ConnectorKind = ConnectorKind.CUSTOM_MCP
    override val displayName: String = "Stateless / Custom MCP Server"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun resolveActiveEndpoint(credentials: ExternalCredentials): McpEndpointEntry {
        val active = credentials.mcpEndpoints.firstOrNull { it.isActive }
            ?: credentials.mcpEndpoints.firstOrNull()
        return active ?: McpEndpointEntry(
            id = "default_gc2223",
            label = "gc2223.com Stateless MCP",
            url = credentials.gc2223Endpoint.trim().trimEnd('/').ifBlank { "https://gc2223.com" },
            bearerToken = credentials.gc2223BearerToken.trim(),
            isActive = true
        )
    }

    override suspend fun checkStatus(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): ConnectorStatus = withContext(Dispatchers.IO) {
        val endpoint = resolveActiveEndpoint(credentials)
        val cleanUrl = endpoint.url.trim().trimEnd('/')
        if (cleanUrl.isBlank()) {
            return@withContext ConnectorStatus(id, kind, displayName, false, "MCP エンドポイント URL 未設定", context.virtualAlias)
        }
        val req = Request.Builder()
            .url("$cleanUrl/health")
            .header("X-Virtual-Alias", context.virtualAlias)
            .header("X-Google-Sub", context.googleSub)
            .apply {
                if (context.googleIdToken.isNotBlank()) header("X-Google-Id-Token", context.googleIdToken)
                val tok = endpoint.bearerToken.ifBlank { credentials.gc2223BearerToken }.trim()
                if (tok.isNotBlank()) header("Authorization", "Bearer $tok")
            }
            .get()
            .build()
        runCatching {
            client.newCall(req).execute().use { resp ->
                val raw = resp.body?.string().orEmpty().take(200)
                ConnectorStatus(
                    connectorId = id,
                    kind = kind,
                    title = "${endpoint.label} (${cleanUrl})",
                    connected = resp.isSuccessful,
                    detail = if (resp.isSuccessful) "MCP HTTP ${resp.code}: $raw" else "HTTP ${resp.code}: $raw",
                    accountScope = context.virtualAlias
                )
            }
        }.getOrElse { err ->
            ConnectorStatus(
                connectorId = id,
                kind = kind,
                title = endpoint.label,
                connected = false,
                detail = err.localizedMessage ?: "MCP unreachable",
                accountScope = context.virtualAlias
            )
        }
    }

    override suspend fun listTools(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): Result<List<McpToolDefinition>> = withContext(Dispatchers.IO) {
        runCatching {
            val endpoint = resolveActiveEndpoint(credentials)
            val resultJson = executeMcpRpc(endpoint, credentials, context, "tools/list", JSONObject())
            val toolsArr = resultJson.optJSONArray("tools") ?: JSONArray()
            (0 until toolsArr.length()).mapNotNull { idx ->
                val item = toolsArr.optJSONObject(idx) ?: return@mapNotNull null
                val name = item.optString("name", "").trim()
                if (name.isBlank()) return@mapNotNull null
                McpToolDefinition(
                    name = name,
                    description = item.optString("description", ""),
                    inputSchemaJson = item.optJSONObject("inputSchema")?.toString() ?: "{}",
                    providerId = endpoint.id
                )
            }
        }
    }

    override suspend fun callTool(
        context: ConnectorContext,
        credentials: ExternalCredentials,
        toolName: String,
        argumentsJson: JSONObject
    ): Result<McpToolExecutionResult> = withContext(Dispatchers.IO) {
        runCatching {
            val endpoint = resolveActiveEndpoint(credentials)
            val params = JSONObject().apply {
                put("name", toolName)
                put("arguments", argumentsJson)
            }
            val resultJson = executeMcpRpc(endpoint, credentials, context, "tools/call", params)
            val isError = resultJson.optBoolean("isError", false)
            val contentArr = resultJson.optJSONArray("content") ?: JSONArray()
            val textOutput = buildString {
                for (i in 0 until contentArr.length()) {
                    val part = contentArr.optJSONObject(i) ?: continue
                    val t = part.optString("text", "").trim()
                    if (t.isNotBlank()) {
                        if (isNotEmpty()) appendLine()
                        append(t)
                    }
                }
            }.ifBlank { resultJson.toString(2) }
            McpToolExecutionResult(
                ok = !isError,
                toolName = toolName,
                output = textOutput,
                endpointUrl = endpoint.url
            )
        }
    }

    private fun executeMcpRpc(
        endpoint: McpEndpointEntry,
        credentials: ExternalCredentials,
        context: ConnectorContext,
        method: String,
        params: JSONObject
    ): JSONObject {
        val cleanUrl = endpoint.url.trim().trimEnd('/')
        require(cleanUrl.startsWith("https://") || cleanUrl.startsWith("http://127.0.0.1") || cleanUrl.startsWith("http://localhost")) {
            "MCP URL は https:// またはローカル (127.0.0.1) を指定してください"
        }
        val rpcBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", "mcp-${System.currentTimeMillis()}")
            put("method", method)
            put("params", params)
        }.toString()

        val bearer = endpoint.bearerToken.ifBlank { credentials.gc2223BearerToken }.trim()
        val req = Request.Builder()
            .url("$cleanUrl/mcp")
            .header("Content-Type", "application/json")
            .header("X-Virtual-Alias", context.virtualAlias)
            .header("X-Google-Sub", context.googleSub)
            .apply {
                if (context.googleIdToken.isNotBlank()) header("X-Google-Id-Token", context.googleIdToken)
                if (bearer.isNotBlank()) header("Authorization", "Bearer $bearer")
            }
            .post(rpcBody.toRequestBody(jsonMediaType))
            .build()

        client.newCall(req).execute().use { resp ->
            val raw = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw IllegalStateException("MCP $method HTTP ${resp.code}: ${raw.take(240)}")
            }
            val root = JSONObject(raw)
            val errObj = root.optJSONObject("error")
            if (errObj != null) {
                throw IllegalStateException("MCP Error ${errObj.optInt("code", -32000)}: ${errObj.optString("message", raw)}")
            }
            return root.optJSONObject("result") ?: root
        }
    }
}

class HubConnectorRegistry(
    val gitHubConnector: GitHubOAuthConnector = GitHubOAuthConnector(),
    val termuxConnector: TermuxCliConnector = TermuxCliConnector(),
    val remoteMcpConnector: RemoteMcpConnector = RemoteMcpConnector()
) {
    val allConnectors: List<McpToolProvider> = listOf(
        gitHubConnector,
        termuxConnector,
        remoteMcpConnector
    )

    suspend fun refreshAllStatuses(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): List<ConnectorStatus> = allConnectors.map { connector ->
        connector.checkStatus(context, credentials)
    }

    suspend fun collectAllTools(
        context: ConnectorContext,
        credentials: ExternalCredentials
    ): List<McpToolDefinition> = buildList {
        allConnectors.forEach { provider ->
            provider.listTools(context, credentials).onSuccess { addAll(it) }
        }
    }
}
