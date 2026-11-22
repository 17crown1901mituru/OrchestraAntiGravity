package com.example.data

import android.util.Base64
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

data class GcpOrganizationSummary(
    val name: String,
    val displayName: String,
    val directoryCustomerId: String = ""
) {
    val orgId: String
        get() = name.substringAfterLast("/").trim()
}

data class GcpProjectSummary(
    val projectId: String,
    val name: String,
    val projectNumber: String,
    val lifecycleState: String,
    val parentType: String = "",
    val parentId: String = ""
)

data class GeminiModelSummary(
    val modelId: String,
    val displayName: String,
    val description: String = "",
    val supportedMethods: List<String> = emptyList(),
    val inputTokenLimit: Int = 0
)

data class AccountDiscoveryResult(
    val userInfo: GoogleUserInfo?,
    val organizations: List<GcpOrganizationSummary>,
    val projects: List<GcpProjectSummary>,
    val billingAccounts: List<GcpBillingAccountSummary>,
    val geminiModels: List<GeminiModelSummary>,
    val discoveryNote: String = ""
)

data class GcpBillingAccountSummary(
    val name: String,
    val displayName: String,
    val open: Boolean
)

data class GcpApiServiceItem(
    val serviceName: String,
    val title: String,
    val description: String,
    val requiredForFreeTier: Boolean,
    val enabled: Boolean = false
)

data class FirebaseAndroidSyncResult(
    val projectId: String,
    val androidAppId: String,
    val bootstrapConfig: RuntimeCloudBootstrapConfig,
    val rawGoogleServicesJson: String,
    val registeredShaCount: Int = 0
)

data class GoogleUserInfo(
    val email: String,
    val name: String,
    val sub: String
)

class GcpProvisioningClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build(),
    private val resourceManagerBaseUrl: String = "https://cloudresourcemanager.googleapis.com",
    private val serviceUsageBaseUrl: String = "https://serviceusage.googleapis.com",
    private val firebaseMgmtBaseUrl: String = "https://firebase.googleapis.com",
    private val billingBaseUrl: String = "https://cloudbilling.googleapis.com",
    private val userInfoBaseUrl: String = "https://www.googleapis.com",
    private val generativeLanguageBaseUrl: String = "https://generativelanguage.googleapis.com"
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val cleanRmBase = resourceManagerBaseUrl.trimEnd('/')
    private val cleanSuBase = serviceUsageBaseUrl.trimEnd('/')
    private val cleanFbBase = firebaseMgmtBaseUrl.trimEnd('/')
    private val cleanBillBase = billingBaseUrl.trimEnd('/')
    private val cleanUserInfoBase = userInfoBaseUrl.trimEnd('/')
    private val cleanGenAiBase = generativeLanguageBaseUrl.trimEnd('/')

    suspend fun fetchGoogleUserInfo(accessToken: String): Result<GoogleUserInfo> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
                val req = Request.Builder()
                    .url("$cleanUserInfoBase/oauth2/v3/userinfo")
                    .header("Authorization", "Bearer ${accessToken.trim()}")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "Google ユーザー情報取得"))
                    }
                    val json = JSONObject(raw)
                    GoogleUserInfo(
                        email = json.optString("email", "").trim(),
                        name = json.optString("name", "").trim(),
                        sub = json.optString("sub", "").trim()
                    )
                }
            }
        }

    suspend fun listOrganizations(accessToken: String): Result<List<GcpOrganizationSummary>> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
                val req = Request.Builder()
                    .url("$cleanRmBase/v1/organizations:search")
                    .header("Authorization", "Bearer ${accessToken.trim()}")
                    .post("{}".toRequestBody(jsonMediaType))
                    .build()
                client.newCall(req).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "GCP 組織一覧取得"))
                    }
                    val arr = JSONObject(raw).optJSONArray("organizations") ?: JSONArray()
                    (0 until arr.length()).mapNotNull { idx ->
                        val item = arr.optJSONObject(idx) ?: return@mapNotNull null
                        val name = item.optString("name", "").trim()
                        if (name.isBlank()) return@mapNotNull null
                        val ownerCustomer = item.optJSONObject("owner")?.optString("directoryCustomerId", "").orEmpty()
                        GcpOrganizationSummary(
                            name = name,
                            displayName = item.optString("displayName", name).ifBlank { name },
                            directoryCustomerId = ownerCustomer
                        )
                    }.sortedBy { it.displayName.lowercase() }
                }
            }
        }

    suspend fun listProjects(accessToken: String): Result<List<GcpProjectSummary>> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
                val filter = URLEncoder.encode("lifecycleState:ACTIVE", "UTF-8")
                val req = Request.Builder()
                    .url("$cleanRmBase/v1/projects?filter=$filter&pageSize=50")
                    .header("Authorization", "Bearer ${accessToken.trim()}")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "GCP プロジェクト一覧取得"))
                    }
                    val root = JSONObject(raw)
                    val arr = root.optJSONArray("projects") ?: JSONArray()
                    (0 until arr.length()).mapNotNull { idx ->
                        val item = arr.optJSONObject(idx) ?: return@mapNotNull null
                        val id = item.optString("projectId", "").trim()
                        if (id.isBlank()) return@mapNotNull null
                        val parent = item.optJSONObject("parent")
                        GcpProjectSummary(
                            projectId = id,
                            name = item.optString("name", id).ifBlank { id },
                            projectNumber = item.optString("projectNumber", ""),
                            lifecycleState = item.optString("lifecycleState", "ACTIVE"),
                            parentType = parent?.optString("type", "").orEmpty(),
                            parentId = parent?.optString("id", "").orEmpty()
                        )
                    }.sortedBy { it.projectId }
                }
            }
        }

    suspend fun listAvailableGeminiModels(
        accessToken: String,
        projectId: String = "",
        apiKey: String = ""
    ): Result<List<GeminiModelSummary>> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanToken = accessToken.trim()
            val cleanApiKey = apiKey.trim()
            require(cleanToken.isNotBlank() || cleanApiKey.isNotBlank()) {
                "Gemini モデル検出には OAuth トークンまたは API Key が必要です"
            }
            val query = buildString {
                append("pageSize=50")
                if (cleanApiKey.isNotBlank()) {
                    append("&key=").append(URLEncoder.encode(cleanApiKey, "UTF-8"))
                }
            }
            val builder = Request.Builder()
                .url("$cleanGenAiBase/v1beta/models?$query")
                .get()
            if (cleanToken.isNotBlank()) {
                builder.header("Authorization", "Bearer $cleanToken")
            }
            if (projectId.isNotBlank()) {
                builder.header("X-Goog-User-Project", projectId.trim())
            }
            client.newCall(builder.build()).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw IllegalStateException(parseGoogleApiError(raw, resp.code, "Gemini モデル検出"))
                }
                val arr = JSONObject(raw).optJSONArray("models") ?: JSONArray()
                (0 until arr.length()).mapNotNull { idx ->
                    val item = arr.optJSONObject(idx) ?: return@mapNotNull null
                    val rawName = item.optString("name", "").trim()
                    val modelId = rawName.removePrefix("models/").trim()
                    if (!modelId.contains("gemini", ignoreCase = true)) return@mapNotNull null
                    val methodsArr = item.optJSONArray("supportedGenerationMethods") ?: JSONArray()
                    val methods = (0 until methodsArr.length()).mapNotNull { mIdx ->
                        methodsArr.optString(mIdx, "").trim().takeIf { it.isNotBlank() }
                    }
                    GeminiModelSummary(
                        modelId = modelId,
                        displayName = item.optString("displayName", modelId).ifBlank { modelId },
                        description = item.optString("description", "").take(160),
                        supportedMethods = methods,
                        inputTokenLimit = item.optInt("inputTokenLimit", 0)
                    )
                }.sortedByDescending { it.modelId.contains("flash", ignoreCase = true) || it.modelId.contains("3.") }
            }
        }
    }

    suspend fun discoverAccountResources(
        accessToken: String,
        preferredProjectId: String = "",
        apiKey: String = ""
    ): Result<AccountDiscoveryResult> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanToken = accessToken.trim()
            require(cleanToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }

            val userInfo = fetchGoogleUserInfo(cleanToken).getOrNull()
            val projectsRes = listProjects(cleanToken)
            val projects = projectsRes.getOrElse { throw it }
            val orgs = listOrganizations(cleanToken).getOrDefault(emptyList())
            val billing = listBillingAccounts(cleanToken).getOrDefault(emptyList())
            val effectiveProject = preferredProjectId.trim().ifBlank {
                projects.firstOrNull()?.projectId.orEmpty()
            }
            val models = listAvailableGeminiModels(cleanToken, effectiveProject, apiKey)
                .getOrDefault(emptyList())

            val summaryParts = buildList {
                add("Org ${orgs.size}件")
                add("GCP Project ${projects.size}件")
                add("請求 ${billing.size}件")
                add("Gemini ${models.size}モデル")
            }

            AccountDiscoveryResult(
                userInfo = userInfo,
                organizations = orgs,
                projects = projects,
                billingAccounts = billing,
                geminiModels = models,
                discoveryNote = summaryParts.joinToString(" • ")
            )
        }
    }

    suspend fun createProject(
        accessToken: String,
        projectId: String,
        displayName: String
    ): Result<GcpProjectSummary> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
            val cleanId = projectId.trim().lowercase()
            require(Regex("^[a-z][a-z0-9-]{4,28}[a-z0-9]$").matches(cleanId)) {
                "Project ID は 6〜30文字の英小文字・数字・ハイフン（先頭は英小文字）で指定してください"
            }
            val cleanName = displayName.trim().ifBlank { cleanId }

            val payload = JSONObject().apply {
                put("projectId", cleanId)
                put("name", cleanName)
            }.toString()

            val req = Request.Builder()
                .url("$cleanRmBase/v1/projects")
                .header("Authorization", "Bearer ${accessToken.trim()}")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            var operationName = ""
            client.newCall(req).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw IllegalStateException(parseGoogleApiError(raw, resp.code, "GCP プロジェクト作成"))
                }
                val json = JSONObject(raw)
                operationName = json.optString("name", "")
            }

            if (operationName.isNotBlank()) {
                pollGoogleOperation(
                    accessToken = accessToken,
                    baseUrl = cleanRmBase,
                    versionPrefix = "v1",
                    operationName = operationName
                )
            }

            // Fetch full created project record
            val getReq = Request.Builder()
                .url("$cleanRmBase/v1/projects/$cleanId")
                .header("Authorization", "Bearer ${accessToken.trim()}")
                .get()
                .build()
            client.newCall(getReq).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (resp.isSuccessful) {
                    val json = JSONObject(raw)
                    GcpProjectSummary(
                        projectId = json.optString("projectId", cleanId),
                        name = json.optString("name", cleanName),
                        projectNumber = json.optString("projectNumber", ""),
                        lifecycleState = json.optString("lifecycleState", "ACTIVE")
                    )
                } else {
                    GcpProjectSummary(cleanId, cleanName, "", "ACTIVE")
                }
            }
        }
    }

    suspend fun listBillingAccounts(accessToken: String): Result<List<GcpBillingAccountSummary>> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
                val req = Request.Builder()
                    .url("$cleanBillBase/v1/billingAccounts")
                    .header("Authorization", "Bearer ${accessToken.trim()}")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "請求アカウント取得"))
                    }
                    val arr = JSONObject(raw).optJSONArray("billingAccounts") ?: JSONArray()
                    (0 until arr.length()).mapNotNull { idx ->
                        val item = arr.optJSONObject(idx) ?: return@mapNotNull null
                        val name = item.optString("name", "").trim()
                        if (name.isBlank()) return@mapNotNull null
                        GcpBillingAccountSummary(
                            name = name,
                            displayName = item.optString("displayName", name),
                            open = item.optBoolean("open", false)
                        )
                    }
                }
            }
        }

    suspend fun linkBillingAccount(
        accessToken: String,
        projectId: String,
        billingAccountName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
            val normalizedBilling = if (billingAccountName.startsWith("billingAccounts/")) {
                billingAccountName
            } else {
                "billingAccounts/${billingAccountName.trim()}"
            }
            val payload = JSONObject().apply {
                put("billingAccountName", normalizedBilling)
                put("billingEnabled", true)
            }.toString()
            val req = Request.Builder()
                .url("$cleanBillBase/v1/projects/${projectId.trim()}/billingInfo")
                .header("Authorization", "Bearer ${accessToken.trim()}")
                .put(payload.toRequestBody(jsonMediaType))
                .build()
            client.newCall(req).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw IllegalStateException(parseGoogleApiError(raw, resp.code, "請求リンク設定"))
                }
                normalizedBilling
            }
        }
    }

    suspend fun listEnabledServices(
        accessToken: String,
        projectId: String
    ): Result<Set<String>> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
            require(projectId.isNotBlank()) { "GCP Project ID を選択してください" }
            val enabled = linkedSetOf<String>()
            var pageToken = ""
            var pages = 0
            while (pages < 4) {
                pages++
                val tokenQuery = if (pageToken.isNotBlank()) {
                    "&pageToken=${URLEncoder.encode(pageToken, "UTF-8")}"
                } else ""
                val url = "$cleanSuBase/v1/projects/${projectId.trim()}/services?filter=state:ENABLED&pageSize=200$tokenQuery"
                val req = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer ${accessToken.trim()}")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "API 有効状態取得"))
                    }
                    val root = JSONObject(raw)
                    val services = root.optJSONArray("services") ?: JSONArray()
                    for (i in 0 until services.length()) {
                        val svc = services.optJSONObject(i) ?: continue
                        val fullName = svc.optString("name", "")
                        val configName = svc.optJSONObject("config")?.optString("name", "").orEmpty()
                        val shortName = configName.ifBlank { fullName.substringAfterLast("/") }.trim()
                        if (shortName.isNotBlank()) enabled.add(shortName)
                    }
                    pageToken = root.optString("nextPageToken", "").trim()
                }
                if (pageToken.isBlank()) break
            }
            enabled
        }
    }

    suspend fun batchEnableServices(
        accessToken: String,
        projectId: String,
        serviceNames: List<String>
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
            require(projectId.isNotBlank()) { "GCP Project ID を選択してください" }
            val cleanServices = serviceNames.map { it.trim() }.filter { it.isNotBlank() }.distinct()
            require(cleanServices.isNotEmpty()) { "有効化する API を 1 つ以上選択してください" }

            cleanServices.chunked(20).forEach { chunk ->
                val payload = JSONObject().apply {
                    put("serviceIds", JSONArray(chunk))
                }.toString()
                val req = Request.Builder()
                    .url("$cleanSuBase/v1/projects/${projectId.trim()}/services:batchEnable")
                    .header("Authorization", "Bearer ${accessToken.trim()}")
                    .post(payload.toRequestBody(jsonMediaType))
                    .build()
                var opName = ""
                client.newCall(req).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "GCP API 一括有効化"))
                    }
                    val json = runCatching { JSONObject(raw) }.getOrNull()
                    if (json != null && !json.optBoolean("done", false)) {
                        opName = json.optString("name", "")
                    }
                }
                if (opName.isNotBlank()) {
                    pollGoogleOperation(
                        accessToken = accessToken,
                        baseUrl = cleanSuBase,
                        versionPrefix = "v1",
                        operationName = opName
                    )
                }
            }
            cleanServices
        }
    }

    suspend fun ensureFirebaseAndFetchAndroidConfig(
        accessToken: String,
        projectId: String,
        packageName: String,
        appDisplayName: String = "AntiGravity Hub",
        existingGatewayUrl: String = "",
        sha1: String = "",
        sha256: String = ""
    ): Result<FirebaseAndroidSyncResult> = withContext(Dispatchers.IO) {
        runCatching {
            require(accessToken.isNotBlank()) { "GCP OAuth Access Token が未取得です" }
            val cleanProject = projectId.trim()
            require(cleanProject.isNotBlank()) { "GCP Project ID を選択してください" }

            // 1. Add Firebase to GCP project (idempotent; 409 means already enabled)
            val addFbReq = Request.Builder()
                .url("$cleanFbBase/v1beta1/projects/$cleanProject:addFirebase")
                .header("Authorization", "Bearer ${accessToken.trim()}")
                .post("{}".toRequestBody(jsonMediaType))
                .build()
            var addOp = ""
            client.newCall(addFbReq).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (resp.code == 409) {
                    // Firebase already added to this GCP project
                } else if (!resp.isSuccessful) {
                    throw IllegalStateException(parseGoogleApiError(raw, resp.code, "Firebase 追加"))
                } else {
                    val json = runCatching { JSONObject(raw) }.getOrNull()
                    if (json != null && !json.optBoolean("done", false)) {
                        addOp = json.optString("name", "")
                    }
                }
            }
            if (addOp.isNotBlank()) {
                pollGoogleOperation(
                    accessToken = accessToken,
                    baseUrl = cleanFbBase,
                    versionPrefix = "v1beta1",
                    operationName = addOp
                )
            }

            // 2. Find existing Android app matching packageName or create one
            var matchedAppId = ""
            val listAppsReq = Request.Builder()
                .url("$cleanFbBase/v1beta1/projects/$cleanProject/androidApps")
                .header("Authorization", "Bearer ${accessToken.trim()}")
                .get()
                .build()
            client.newCall(listAppsReq).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw IllegalStateException(parseGoogleApiError(raw, resp.code, "Firebase Androidアプリ一覧取得"))
                }
                val appsArr = JSONObject(raw).optJSONArray("apps") ?: JSONArray()
                for (i in 0 until appsArr.length()) {
                    val app = appsArr.optJSONObject(i) ?: continue
                    if (app.optString("packageName") == packageName) {
                        matchedAppId = app.optString("appId", "").trim()
                        break
                    }
                }
            }

            if (matchedAppId.isBlank()) {
                val createPayload = JSONObject().apply {
                    put("packageName", packageName)
                    put("displayName", appDisplayName)
                }.toString()
                val createAppReq = Request.Builder()
                    .url("$cleanFbBase/v1beta1/projects/$cleanProject/androidApps")
                    .header("Authorization", "Bearer ${accessToken.trim()}")
                    .post(createPayload.toRequestBody(jsonMediaType))
                    .build()
                var createOp = ""
                client.newCall(createAppReq).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "Firebase Androidアプリ登録"))
                    }
                    val json = JSONObject(raw)
                    if (json.optBoolean("done", false)) {
                        matchedAppId = json.optJSONObject("response")?.optString("appId", "").orEmpty()
                    } else {
                        createOp = json.optString("name", "")
                    }
                }
                if (matchedAppId.isBlank() && createOp.isNotBlank()) {
                    val opResult = pollGoogleOperation(
                        accessToken = accessToken,
                        baseUrl = cleanFbBase,
                        versionPrefix = "v1beta1",
                        operationName = createOp
                    )
                    matchedAppId = opResult?.optJSONObject("response")?.optString("appId", "").orEmpty()
                }
                if (matchedAppId.isBlank()) {
                    client.newCall(listAppsReq).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val appsArr = JSONObject(resp.body?.string().orEmpty()).optJSONArray("apps") ?: JSONArray()
                            for (i in 0 until appsArr.length()) {
                                val app = appsArr.optJSONObject(i) ?: continue
                                if (app.optString("packageName") == packageName) {
                                    matchedAppId = app.optString("appId", "").trim()
                                    break
                                }
                            }
                        }
                    }
                }
            }

            require(matchedAppId.isNotBlank()) { "Firebase Android App ID を取得できませんでした" }

            // 2.5 Register live APK SHA-1 / SHA-256 certificates so re-signed APKs never hit DEVELOPER_ERROR
            val shaCount = registerAndroidAppShaCertificates(
                accessToken = accessToken,
                projectId = cleanProject,
                androidAppId = matchedAppId,
                sha1 = sha1,
                sha256 = sha256
            ).getOrDefault(0)

            // 3. Download google-services.json config via Firebase Management API (retry once if Web Client ID was still propagating)
            val cfgReq = Request.Builder()
                .url("$cleanFbBase/v1beta1/projects/$cleanProject/androidApps/$matchedAppId/config")
                .header("Authorization", "Bearer ${accessToken.trim()}")
                .get()
                .build()
            var decodedJson = ""
            var parsedCfg: RuntimeCloudBootstrapConfig? = null
            repeat(2) { attempt ->
                decodedJson = client.newCall(cfgReq).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "google-services.json 取得"))
                    }
                    val b64 = JSONObject(raw).optString("configFileContents", "")
                    require(b64.isNotBlank()) { "configFileContents が空でした" }
                    String(Base64.decode(b64, Base64.DEFAULT), Charsets.UTF_8)
                }
                val cfg = RuntimeCloudBootstrapStore.parseGoogleServicesJson(
                    rawJson = decodedJson,
                    targetPackageName = packageName,
                    existingGatewayUrl = existingGatewayUrl
                ).getOrThrow()
                parsedCfg = cfg
                if (cfg.firebaseWebClientId.isNotBlank() || attempt == 1) {
                    return@repeat
                }
                delay(800L)
            }

            FirebaseAndroidSyncResult(
                projectId = cleanProject,
                androidAppId = matchedAppId,
                bootstrapConfig = checkNotNull(parsedCfg),
                rawGoogleServicesJson = decodedJson,
                registeredShaCount = shaCount
            )
        }
    }

    suspend fun registerAndroidAppShaCertificates(
        accessToken: String,
        projectId: String,
        androidAppId: String,
        sha1: String,
        sha256: String
    ): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanToken = accessToken.trim()
            val cleanProject = projectId.trim()
            val cleanAppId = androidAppId.trim()
            if (cleanToken.isBlank() || cleanProject.isBlank() || cleanAppId.isBlank()) return@runCatching 0

            val candidates = listOf(
                sha1.replace(":", "").trim().lowercase() to "SHA_1",
                sha256.replace(":", "").trim().lowercase() to "SHA_256"
            ).filter { (hex, _) -> hex.isNotBlank() }

            if (candidates.isEmpty()) return@runCatching 0

            val existingReq = Request.Builder()
                .url("$cleanFbBase/v1beta1/projects/$cleanProject/androidApps/$cleanAppId/sha")
                .header("Authorization", "Bearer $cleanToken")
                .get()
                .build()
            val existingHashes = hashSetOf<String>()
            client.newCall(existingReq).execute().use { resp ->
                if (resp.isSuccessful) {
                    val arr = JSONObject(resp.body?.string().orEmpty()).optJSONArray("certificates") ?: JSONArray()
                    for (i in 0 until arr.length()) {
                        val h = arr.optJSONObject(i)?.optString("shaHash", "").orEmpty()
                            .replace(":", "")
                            .trim()
                            .lowercase()
                        if (h.isNotBlank()) existingHashes.add(h)
                    }
                }
            }

            var addedOrPresent = 0
            for ((hexHash, certType) in candidates) {
                if (existingHashes.contains(hexHash)) {
                    addedOrPresent++
                    continue
                }
                val body = JSONObject().apply {
                    put("shaHash", hexHash)
                    put("certType", certType)
                }.toString()
                val postReq = Request.Builder()
                    .url("$cleanFbBase/v1beta1/projects/$cleanProject/androidApps/$cleanAppId/sha")
                    .header("Authorization", "Bearer $cleanToken")
                    .post(body.toRequestBody(jsonMediaType))
                    .build()
                client.newCall(postReq).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (resp.isSuccessful || resp.code == 409) {
                        addedOrPresent++
                    } else {
                        throw IllegalStateException(parseGoogleApiError(raw, resp.code, "Firebase SHA登録($certType)"))
                    }
                }
            }
            addedOrPresent
        }
    }

    private suspend fun pollGoogleOperation(
        accessToken: String,
        baseUrl: String,
        versionPrefix: String,
        operationName: String,
        maxAttempts: Int = 12
    ): JSONObject? {
        val cleanOp = operationName.trimStart('/')
        val opUrl = if (cleanOp.startsWith("http")) {
            cleanOp
        } else {
            "$baseUrl/$versionPrefix/$cleanOp"
        }
        repeat(maxAttempts) {
            val req = Request.Builder()
                .url(opUrl)
                .header("Authorization", "Bearer ${accessToken.trim()}")
                .get()
                .build()
            client.newCall(req).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    throw IllegalStateException(parseGoogleApiError(raw, resp.code, "GCP オペレーション確認"))
                }
                val json = JSONObject(raw)
                val err = json.optJSONObject("error")
                if (err != null) {
                    throw IllegalStateException(err.optString("message", "GCP operation failed"))
                }
                if (json.optBoolean("done", false)) {
                    return json
                }
            }
            delay(500L)
        }
        return null
    }

    private fun parseGoogleApiError(raw: String, code: Int, actionLabel: String): String {
        val msg = runCatching {
            JSONObject(raw).optJSONObject("error")?.optString("message").orEmpty()
        }.getOrDefault("")
        return "$actionLabel HTTP $code: ${msg.ifBlank { raw.take(220).ifBlank { "Error" } }}"
    }

    companion object {
        val recommendedApis: List<GcpApiServiceItem> = listOf(
            GcpApiServiceItem(
                serviceName = "firebase.googleapis.com",
                title = "Firebase Management API",
                description = "Firebase 有効化・Android 設定自動取得",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "firestore.googleapis.com",
                title = "Cloud Firestore API",
                description = "ノード状態・ブループリント・予算監視 DB",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "firebaseappcheck.googleapis.com",
                title = "Firebase App Check API",
                description = "API 不正利用・Gemini AI Logic 保護",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "firebasevertexai.googleapis.com",
                title = "Firebase AI Logic API",
                description = "Firebase 経由の Gemini 推論実行",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "generativelanguage.googleapis.com",
                title = "Generative Language API (AI Studio)",
                description = "利用可能 Gemini モデル自動検出・推論",
                requiredForFreeTier = false
            ),
            GcpApiServiceItem(
                serviceName = "identitytoolkit.googleapis.com",
                title = "Identity Toolkit (Firebase Auth)",
                description = "Google SSO 認証・JWT 発行",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "cloudresourcemanager.googleapis.com",
                title = "Cloud Resource Manager API",
                description = "GCP プロジェクト一覧・新規作成",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "serviceusage.googleapis.com",
                title = "Service Usage API",
                description = "API の承認・有効状態チェック",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "cloudbilling.googleapis.com",
                title = "Cloud Billing API",
                description = "$0.10/日 超過防止・課金リンク確認",
                requiredForFreeTier = true
            ),
            GcpApiServiceItem(
                serviceName = "secretmanager.googleapis.com",
                title = "Secret Manager API (任意)",
                description = "Bearer トークン等の KMS/Secret 管理",
                requiredForFreeTier = false
            ),
            GcpApiServiceItem(
                serviceName = "run.googleapis.com",
                title = "Cloud Run API (任意)",
                description = "Terraform でのサーバレス拡張",
                requiredForFreeTier = false
            )
        )
    }
}
