package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CloudBridgeClient
import com.example.data.FirestoreRepository
import com.example.data.LocalSecretsStore
import com.example.data.MultimodalSourceLoader
import com.example.data.SyncSnapshotStore
import com.example.model.AgentBlueprint
import com.example.model.AiGenerationState
import com.example.model.AttachedSource
import com.example.model.BackgroundSyncSettings
import com.example.model.CostAlert
import com.example.model.CostConfig
import com.example.model.CostEntry
import com.example.model.ExternalCredentials
import com.example.model.InferenceTarget
import com.example.model.StreamUiState
import com.example.model.SyncStatusSnapshot
import com.example.model.VaultMetadata
import com.example.model.WorkflowNode
import com.example.model.WorkflowPlanResult
import com.example.sync.BackgroundSyncScheduler
import com.example.sync.SyncWorkUiState
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "HubViewModel"
private const val STOP_TIMEOUT_MILLIS = 5000L

class HubViewModel(
    appContext: Context,
    private val repository: FirestoreRepository,
    private val secretsStore: LocalSecretsStore,
    private val syncSnapshotStore: SyncSnapshotStore,
    private val bridgeClient: CloudBridgeClient,
    private val currentUserId: String,
    private val userEmail: String
) : ViewModel() {

    private val applicationContext = appContext.applicationContext

    private val chatModel by lazy {
        Firebase.ai.generativeModel(modelName = "gemini-3.8-flash")
    }

    private val plannerModel by lazy {
        val planSchema = Schema.obj(
            properties = mapOf(
                "planTitle" to Schema.string(description = "Concise title of the automation workflow"),
                "targetLayer" to Schema.enumeration(
                    values = listOf("WORKER", "TERMUX", "GITHUB", "GCP", "CLI"),
                    description = "Primary layer targeted by this plan"
                ),
                "estimatedMonthlyCost" to Schema.string(description = "Estimated monthly operating cost under free tier"),
                "steps" to Schema.array(
                    items = Schema.string(),
                    description = "Sequential execution steps"
                ),
                "cliOrConfigSnippet" to Schema.string(description = "Ready-to-run shell, YAML, or JS snippet")
            )
        )
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = planSchema
            }
        )
    }

    val nodesState: StateFlow<StreamUiState<List<WorkflowNode>>> = repository.observeNodes(currentUserId)
        .map<List<WorkflowNode>, StreamUiState<List<WorkflowNode>>> { StreamUiState.Success(it) }
        .catch { error ->
            emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load workflow nodes")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Loading
        )

    val blueprintsState: StateFlow<StreamUiState<List<AgentBlueprint>>> = repository.observeBlueprints(currentUserId)
        .map<List<AgentBlueprint>, StreamUiState<List<AgentBlueprint>>> { list ->
            val sorted = list.sortedByDescending { it.updatedAt?.seconds ?: it.createdAt?.seconds ?: 0L }
            StreamUiState.Success(sorted)
        }
        .catch { error ->
            emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load blueprints")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Loading
        )

    val costConfigState: StateFlow<CostConfig> = repository.observeCostConfig(currentUserId)
        .catch { error ->
            unwrapFirestoreError(error, "Failed to observe cost config")
            emit(CostConfig(userId = currentUserId, alertEmail = userEmail.ifBlank { "17crown2223@gmail.com" }))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = CostConfig(userId = currentUserId, alertEmail = userEmail.ifBlank { "17crown2223@gmail.com" })
        )

    val costEntriesState: StateFlow<StreamUiState<List<CostEntry>>> = repository.observeCostEntries(currentUserId)
        .map<List<CostEntry>, StreamUiState<List<CostEntry>>> { list ->
            val sorted = list.sortedByDescending { it.createdAt?.seconds ?: 0L }
            StreamUiState.Success(sorted)
        }
        .catch { error ->
            emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load cost entries")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Loading
        )

    val costAlertsState: StateFlow<StreamUiState<List<CostAlert>>> = repository.observeCostAlerts(currentUserId)
        .map<List<CostAlert>, StreamUiState<List<CostAlert>>> { list ->
            val sorted = list.sortedByDescending { it.createdAt?.seconds ?: 0L }
            StreamUiState.Success(sorted)
        }
        .catch { error ->
            emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load cost alerts")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Loading
        )

    private val _aiState = MutableStateFlow<AiGenerationState>(AiGenerationState.Idle)
    val aiState: StateFlow<AiGenerationState> = _aiState.asStateFlow()

    private val _inferenceTarget = MutableStateFlow(InferenceTarget.FIREBASE_GEMINI)
    val inferenceTarget: StateFlow<InferenceTarget> = _inferenceTarget.asStateFlow()

    private val _attachedSources = MutableStateFlow<List<AttachedSource>>(emptyList())
    val attachedSources: StateFlow<List<AttachedSource>> = _attachedSources.asStateFlow()

    private val sourceLoader by lazy { MultimodalSourceLoader(applicationContext) }

    private val _credentials = MutableStateFlow(secretsStore.loadForUser(currentUserId))
    val credentials: StateFlow<ExternalCredentials> = _credentials.asStateFlow()

    private val _vaultMetadata = MutableStateFlow(secretsStore.getVaultMetadata(currentUserId, userEmail))
    val vaultMetadata: StateFlow<VaultMetadata> = _vaultMetadata.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _isBusyNodeAction = MutableStateFlow(false)
    val isBusyNodeAction: StateFlow<Boolean> = _isBusyNodeAction.asStateFlow()

    private val _settingsVerificationStatus = MutableStateFlow<String?>(null)
    val settingsVerificationStatus: StateFlow<String?> = _settingsVerificationStatus.asStateFlow()

    val syncSnapshotState: StateFlow<SyncStatusSnapshot> = syncSnapshotStore.observeSnapshot(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = syncSnapshotStore.loadSnapshot(currentUserId)
        )

    val syncSettingsState: StateFlow<BackgroundSyncSettings> = syncSnapshotStore.observeSettings(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = syncSnapshotStore.loadSettings(currentUserId)
        )

    val syncWorkState: StateFlow<SyncWorkUiState> = BackgroundSyncScheduler.observeWorkState(applicationContext, currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = SyncWorkUiState()
        )

    init {
        viewModelScope.launch {
            repository.seedDefaultNodesIfEmpty(currentUserId, userEmail)
                .onFailure { err -> Log.w(TAG, "Initial node seed failed", err) }
            repository.seedDefaultCostConfigIfMissing(currentUserId, userEmail)
                .onFailure { err -> Log.w(TAG, "Initial cost config seed failed", err) }
        }
        val syncCfg = syncSnapshotStore.loadSettings(currentUserId)
        if (syncCfg.enabled) {
            BackgroundSyncScheduler.ensurePeriodicSyncScheduled(
                context = applicationContext,
                uid = currentUserId,
                intervalMinutes = syncCfg.intervalMinutes,
                replaceExisting = false
            )
            val savedCreds = _credentials.value
            val snap = syncSnapshotStore.loadSnapshot(currentUserId)
            val hasCloudKeys = savedCreds.githubPat.isNotBlank() ||
                savedCreds.githubOwnerRepo.isNotBlank() ||
                savedCreds.cloudflareApiToken.isNotBlank()
            if (hasCloudKeys && snap.syncedAtEpochMs == 0L) {
                BackgroundSyncScheduler.triggerImmediateSync(applicationContext, currentUserId)
            } else if (snap.termuxSyncedAtEpochMs == 0L) {
                BackgroundSyncScheduler.triggerImmediateTermuxSync(applicationContext, currentUserId)
            }
        }
    }

    private fun unwrapFirestoreError(error: Throwable, fallback: String): String {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, fallback, rootError)
        return rootError.message ?: fallback
    }

    private suspend fun fetchFirebaseIdToken(): String? {
        return try {
            Firebase.auth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Failed to obtain Firebase ID token", e)
            null
        }
    }

    fun copyableFirebaseSsoToken(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            onResult(fetchFirebaseIdToken())
        }
    }

    fun setInferenceTarget(target: InferenceTarget) {
        _inferenceTarget.value = target
    }

    fun addAttachedUris(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val existing = _attachedSources.value.associateBy { it.uriString }.toMutableMap()
        var addedCount = 0
        var skippedCount = 0
        uris.forEach { uri ->
            val inspected = sourceLoader.inspectUri(uri)
            if (inspected != null) {
                existing[inspected.uriString] = inspected
                addedCount++
            } else {
                skippedCount++
            }
        }
        _attachedSources.value = existing.values.toList()
        if (addedCount > 0) {
            _statusBanner.value = "${addedCount}件の関連ソースを添付しました"
        } else if (skippedCount > 0) {
            _statusBanner.value = "未対応のファイル形式がスキップされました"
        }
    }

    fun removeAttachedSource(uriString: String) {
        _attachedSources.value = _attachedSources.value.filterNot { it.uriString == uriString }
    }

    fun clearAttachedSources() {
        _attachedSources.value = emptyList()
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun clearVerificationStatus() {
        _settingsVerificationStatus.value = null
    }

    fun triggerBackgroundSyncNow() {
        BackgroundSyncScheduler.triggerImmediateSync(applicationContext, currentUserId)
        _statusBanner.value = "GitHub・Cloudflare・Termux の同期を開始しました"
    }

    fun triggerTermuxSyncNow() {
        BackgroundSyncScheduler.triggerImmediateTermuxSync(applicationContext, currentUserId)
        _statusBanner.value = "Termux AntiGravityAgent のステータスを取得中..."
    }

    fun setBackgroundSyncEnabled(enabled: Boolean) {
        val current = syncSnapshotStore.loadSettings(currentUserId)
        syncSnapshotStore.saveSettings(currentUserId, current.copy(enabled = enabled))
        if (enabled) {
            BackgroundSyncScheduler.ensurePeriodicSyncScheduled(
                context = applicationContext,
                uid = currentUserId,
                intervalMinutes = current.intervalMinutes,
                replaceExisting = true
            )
            _statusBanner.value = "定期バックグラウンド同期を有効化しました (${current.intervalMinutes}分間隔)"
        } else {
            BackgroundSyncScheduler.cancelUserSync(applicationContext, currentUserId)
            _statusBanner.value = "定期バックグラウンド同期を停止しました"
        }
    }

    fun setBackgroundSyncInterval(intervalMinutes: Long) {
        val safeMinutes = intervalMinutes.coerceAtLeast(15L)
        val current = syncSnapshotStore.loadSettings(currentUserId)
        syncSnapshotStore.saveSettings(currentUserId, current.copy(intervalMinutes = safeMinutes))
        if (current.enabled) {
            BackgroundSyncScheduler.ensurePeriodicSyncScheduled(
                context = applicationContext,
                uid = currentUserId,
                intervalMinutes = safeMinutes,
                replaceExisting = true
            )
        }
        _statusBanner.value = "同期間隔を ${safeMinutes}分 に設定しました"
    }

    fun onUserSignedOut() {
        BackgroundSyncScheduler.cancelUserSync(applicationContext, currentUserId)
    }

    fun saveCredentials(updated: ExternalCredentials) {
        secretsStore.saveForUser(currentUserId, userEmail, updated)
            .onSuccess { meta ->
                _credentials.value = updated
                _vaultMetadata.value = meta
                _statusBanner.value = "AES-256-GCM 暗号化ボルトに保存しました (${meta.linkedEmail.ifBlank { currentUserId }})"
                val hasCloudTargets = updated.githubPat.isNotBlank() ||
                    updated.githubOwnerRepo.isNotBlank() ||
                    updated.cloudflareApiToken.isNotBlank()
                if (syncSettingsState.value.enabled) {
                    if (hasCloudTargets) {
                        BackgroundSyncScheduler.triggerImmediateSync(applicationContext, currentUserId)
                    } else {
                        BackgroundSyncScheduler.triggerImmediateTermuxSync(applicationContext, currentUserId)
                    }
                }
            }
            .onFailure { err ->
                _statusBanner.value = "暗号化保存に失敗しました: ${err.localizedMessage ?: "Keystore error"}"
            }
    }

    fun clearEncryptedVault() {
        secretsStore.clearForUser(currentUserId, userEmail)
            .onSuccess { meta ->
                _credentials.value = ExternalCredentials()
                _vaultMetadata.value = meta
                _settingsVerificationStatus.value = null
                syncSnapshotStore.saveSnapshot(currentUserId, SyncStatusSnapshot(userId = currentUserId))
                _statusBanner.value = "暗号化ボルトの保存キーをすべて消去しました"
            }
            .onFailure { err ->
                _statusBanner.value = "消去に失敗しました: ${err.localizedMessage ?: "Keystore error"}"
            }
    }

    fun verifyGitHubFromSettings(pat: String) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val res = bridgeClient.verifyGitHubPat(pat)
                _settingsVerificationStatus.value = res.message
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun verifyCloudflareFromSettings(token: String) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val res = bridgeClient.verifyCloudflareToken(token)
                _settingsVerificationStatus.value = res.message
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun verifyGc2223FromSettings(endpoint: String, bearerToken: String) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val res = bridgeClient.pingGc2223Gateway(
                    baseUrl = endpoint,
                    bearerToken = bearerToken,
                    firebaseIdToken = fetchFirebaseIdToken()
                )
                _settingsVerificationStatus.value = res.message
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun verifyTermuxFromSettings(agentUrl: String, bearerToken: String) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val status = bridgeClient.fetchTermuxAgentStatus(agentUrl, bearerToken)
                _settingsVerificationStatus.value = status.summaryMessage
                _statusBanner.value = status.summaryMessage
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun runPrompt(userPrompt: String, structuredPlanMode: Boolean) {
        val cleanPrompt = userPrompt.trim()
        val sources = _attachedSources.value
        if (cleanPrompt.isEmpty() && sources.isEmpty()) return
        val effectivePrompt = cleanPrompt.ifEmpty {
            "添付した関連ソース（Markdown / PDF / 画像 / 動画）の内容を統合的に解析し、運用コストゼロを目指した具体的なソリューションと構築手順を組み立ててください。"
        }

        if (_inferenceTarget.value == InferenceTarget.GC2223_GATEWAY) {
            queryGc2223Gateway(effectivePrompt, sources)
            return
        }

        if (structuredPlanMode) {
            generateStructuredPlan(effectivePrompt, sources)
        } else {
            generateStreamingReply(effectivePrompt, sources)
        }
    }

    private fun generateStreamingReply(userPrompt: String, sources: List<AttachedSource>) {
        viewModelScope.launch {
            _aiState.value = AiGenerationState.Loading
            val accumulated = StringBuilder()
            val contextualPrompt = buildString {
                appendLine("You are AntiGravityAgent Architect, helping the user ($userEmail) build a near-zero-cost LLM workflow.")
                appendLine("Stack: Firebase (Auth/Firestore/AI Logic), Cloudflare Workers on domain gc2223.com (${_credentials.value.gc2223Endpoint}), GitHub Actions, GCP Always-Free + $0.10/day Budget Alert, and Termux AntiGravityAgent CLI.")
                if (sources.isNotEmpty()) {
                    appendLine("Carefully inspect ALL attached multimodal sources (Markdown specs, PDFs, images, videos) and synthesize a unified, actionable solution.")
                }
                appendLine("Respond concisely in Japanese with practical commands or configs.")
                appendLine()
                append("User request: ")
                append(userPrompt)
            }
            try {
                if (sources.isEmpty()) {
                    withTimeout(60_000L) {
                        chatModel.generateContentStream(contextualPrompt).collect { chunk ->
                            val part = chunk.text.orEmpty()
                            accumulated.append(part)
                            _aiState.value = AiGenerationState.Streaming(accumulated.toString())
                        }
                    }
                } else {
                    val contentResult = sourceLoader.buildMultimodalContent(contextualPrompt, sources)
                    val multimodalContent = contentResult.getOrElse { err ->
                        _aiState.value = AiGenerationState.Error(err.localizedMessage ?: "添付ソースの読み込みに失敗しました。")
                        return@launch
                    }
                    withTimeout(60_000L) {
                        chatModel.generateContentStream(multimodalContent).collect { chunk ->
                            val part = chunk.text.orEmpty()
                            accumulated.append(part)
                            _aiState.value = AiGenerationState.Streaming(accumulated.toString())
                        }
                    }
                }
                if (accumulated.isNotBlank()) {
                    _aiState.value = AiGenerationState.Success(accumulated.toString())
                    val srcLabel = if (sources.isNotEmpty()) " + ${sources.size} sources" else ""
                    repository.addCostEntry(
                        userId = currentUserId,
                        provider = "FIREBASE",
                        amountUsd = 0.0,
                        usageSummary = "Gemini 3.8 Flash stream$srcLabel (${accumulated.length} chars, Spark Free)",
                        source = "AUTO_TELEMETRY"
                    )
                } else {
                    _aiState.value = AiGenerationState.Error("モデルから空の応答が返されました。")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Streaming timed out", e)
                _aiState.value = AiGenerationState.Error("60秒でタイムアウトしました。メディアサイズを小さくして再度お試しください。")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI streaming failed", e)
                _aiState.value = AiGenerationState.Error(e.localizedMessage ?: "AI 生成に失敗しました。")
            }
        }
    }

    private fun generateStructuredPlan(userPrompt: String, sources: List<AttachedSource>) {
        viewModelScope.launch {
            _aiState.value = AiGenerationState.Loading
            val prompt = buildString {
                appendLine("Design a concrete zero-cost automation solution for:")
                appendLine("Account: $userEmail")
                appendLine("Cloudflare domain: ${_credentials.value.gc2223Endpoint}")
                appendLine("GitHub repo: ${_credentials.value.githubOwnerRepo.ifBlank { "17crown1901mituru/OrchestraAntiGravity" }}")
                appendLine("GCP project: ${_credentials.value.gcpProjectId.ifBlank { "gen-lang-client-0439741065" }}")
                appendLine("Target CLI: Termux AntiGravityAgent")
                if (sources.isNotEmpty()) {
                    appendLine("Synthesize the attached Markdown, PDF, image, and/or video sources into the steps and cliOrConfigSnippet.")
                }
                appendLine("Respond in Japanese for descriptions and steps, with a valid copy-pasteable snippet in cliOrConfigSnippet.")
                appendLine()
                append("Request: ")
                append(userPrompt)
            }
            try {
                val response = if (sources.isEmpty()) {
                    withTimeout(60_000L) {
                        plannerModel.generateContent(prompt)
                    }
                } else {
                    val contentResult = sourceLoader.buildMultimodalContent(prompt, sources)
                    val multimodalContent = contentResult.getOrElse { err ->
                        _aiState.value = AiGenerationState.Error(err.localizedMessage ?: "添付ソースの読み込みに失敗しました。")
                        return@launch
                    }
                    withTimeout(60_000L) {
                        plannerModel.generateContent(multimodalContent)
                    }
                }
                val rawJson = response.text
                    ?: throw IllegalStateException("Empty structured response.")
                val json = JSONObject(rawJson)
                val stepsJson = json.getJSONArray("steps")
                val stepsList = (0 until stepsJson.length()).map { stepsJson.getString(it) }
                val plan = WorkflowPlanResult(
                    planTitle = json.getString("planTitle"),
                    targetLayer = json.getString("targetLayer"),
                    estimatedMonthlyCost = json.getString("estimatedMonthlyCost"),
                    steps = stepsList,
                    cliOrConfigSnippet = json.getString("cliOrConfigSnippet")
                )
                val formatted = buildString {
                    appendLine("### ${plan.planTitle}")
                    appendLine("対象レイヤー: ${plan.targetLayer} / 月額目安: ${plan.estimatedMonthlyCost}")
                    appendLine()
                    plan.steps.forEachIndexed { idx, step ->
                        appendLine("${idx + 1}. $step")
                    }
                    appendLine()
                    appendLine("```")
                    appendLine(plan.cliOrConfigSnippet)
                    append("```")
                }
                _aiState.value = AiGenerationState.Success(formatted, structuredPlan = plan)
                val srcLabel = if (sources.isNotEmpty()) " (${sources.size} sources)" else ""
                repository.addCostEntry(
                    userId = currentUserId,
                    provider = "FIREBASE",
                    amountUsd = 0.0,
                    usageSummary = "Gemini 3.8 Flash JSON plan$srcLabel: ${plan.planTitle.take(50)}",
                    source = "AUTO_TELEMETRY"
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Structured plan timed out", e)
                _aiState.value = AiGenerationState.Error("設計プラン生成がタイムアウトしました。")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Structured plan failed", e)
                _aiState.value = AiGenerationState.Error(e.localizedMessage ?: "プラン生成に失敗しました。")
            }
        }
    }

    private fun queryGc2223Gateway(userPrompt: String, sources: List<AttachedSource>) {
        viewModelScope.launch {
            _aiState.value = AiGenerationState.Loading
            val (textContext, skippedBinaries) = if (sources.isNotEmpty()) {
                sourceLoader.extractTextContextForGateway(sources)
            } else {
                "" to emptyList()
            }
            if (skippedBinaries.isNotEmpty()) {
                _aiState.value = AiGenerationState.Error(
                    "PDF / 画像 / 動画 (${skippedBinaries.joinToString(", ")}) の解析には「Firebase Gemini」を選択してください。"
                )
                return@launch
            }
            val combinedPrompt = if (textContext.isNotBlank()) {
                "$textContext\n\n---\n$userPrompt"
            } else {
                userPrompt
            }
            val creds = _credentials.value
            val idToken = fetchFirebaseIdToken()
            val result = bridgeClient.queryGc2223Gateway(
                baseUrl = creds.gc2223Endpoint,
                bearerToken = creds.gc2223BearerToken,
                prompt = combinedPrompt,
                firebaseIdToken = idToken
            )
            if (result.success) {
                _aiState.value = AiGenerationState.Success(result.message)
                repository.addCostEntry(
                    userId = currentUserId,
                    provider = "CLOUDFLARE",
                    amountUsd = 0.0,
                    usageSummary = "gc2223.com /v1/chat (Workers AI Free)",
                    source = "AUTO_TELEMETRY"
                )
            } else {
                _aiState.value = AiGenerationState.Error(
                    "gc2223.com ゲートウェイ呼び出し失敗: ${result.message}"
                )
            }
        }
    }

    fun testGc2223Health(node: WorkflowNode) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val res = bridgeClient.pingGc2223Gateway(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                val newStatus = if (res.success) "ACTIVE" else "ERROR"
                repository.updateNodeStatus(
                    userId = currentUserId,
                    nodeId = node.id,
                    status = newStatus,
                    endpointOrTarget = creds.gc2223Endpoint,
                    lastCheckedNote = res.message
                )
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun verifyCloudflareNode(node: WorkflowNode) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val creds = _credentials.value
                val res = bridgeClient.verifyCloudflareToken(creds.cloudflareApiToken)
                val newStatus = if (res.success) "ACTIVE" else "ERROR"
                repository.updateNodeStatus(
                    userId = currentUserId,
                    nodeId = node.id,
                    status = newStatus,
                    endpointOrTarget = creds.gc2223Endpoint,
                    lastCheckedNote = res.message
                )
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun verifyOrDispatchGitHub(node: WorkflowNode, dispatch: Boolean) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val creds = _credentials.value
                val res = if (dispatch) {
                    bridgeClient.dispatchGitHubWorkflow(
                        pat = creds.githubPat,
                        ownerRepo = creds.githubOwnerRepo,
                        workflowFile = creds.githubWorkflowFile
                    )
                } else {
                    bridgeClient.verifyGitHubPat(creds.githubPat)
                }
                val newStatus = if (res.success) "ACTIVE" else "ERROR"
                val target = creds.githubOwnerRepo.ifBlank { node.endpointOrTarget }
                repository.updateNodeStatus(
                    userId = currentUserId,
                    nodeId = node.id,
                    status = newStatus,
                    endpointOrTarget = target,
                    lastCheckedNote = res.message
                )
                if (dispatch && res.success) {
                    repository.addCostEntry(
                        userId = currentUserId,
                        provider = "GITHUB",
                        amountUsd = 0.0,
                        usageSummary = "workflow_dispatch: ${creds.githubOwnerRepo}",
                        source = "AUTO_TELEMETRY"
                    )
                }
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun updateGcpOrTermuxStatus(node: WorkflowNode, newStatus: String, note: String) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val target = if (node.provider == "GCP") {
                    _credentials.value.gcpProjectId.ifBlank { node.endpointOrTarget }
                } else {
                    node.endpointOrTarget
                }
                repository.updateNodeStatus(
                    userId = currentUserId,
                    nodeId = node.id,
                    status = newStatus,
                    endpointOrTarget = target,
                    lastCheckedNote = note
                ).onSuccess {
                    _statusBanner.value = "${node.title} を $newStatus に更新しました"
                }.onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "更新に失敗しました"
                }
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun saveCostConfig(
        alertEmail: String,
        dailyThresholdUsd: Double,
        autoEmailEnabled: Boolean,
        webhookPath: String
    ) {
        viewModelScope.launch {
            repository.updateCostConfig(
                userId = currentUserId,
                alertEmail = alertEmail,
                dailyThresholdUsd = dailyThresholdUsd,
                autoEmailEnabled = autoEmailEnabled,
                webhookPath = webhookPath
            ).onSuccess {
                _statusBanner.value = "コスト監視設定を保存しました ($${String.format("%.2f", dailyThresholdUsd)}/日)"
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "設定の保存に失敗しました"
            }
        }
    }

    fun addExpenseAndCheckThreshold(
        provider: String,
        amountUsd: Double,
        usageSummary: String
    ) {
        viewModelScope.launch {
            val today = FirestoreRepository.todayDateKey()
            val result = repository.addCostEntry(
                userId = currentUserId,
                provider = provider,
                amountUsd = amountUsd,
                usageSummary = usageSummary,
                source = "MANUAL",
                dateKey = today
            )
            if (result.isFailure) {
                _statusBanner.value = result.exceptionOrNull()?.localizedMessage ?: "記録に失敗しました"
                return@launch
            }

            val currentEntries = (costEntriesState.value as? StreamUiState.Success)?.data.orEmpty()
            val todayEntries = currentEntries.filter { it.dateKey == today }
            val newTotal = todayEntries.sumOf { it.amountUsd } + amountUsd
            val breakdown = todayEntries.groupBy { it.provider }
                .mapValues { (_, list) -> list.sumOf { it.amountUsd } }
                .toMutableMap()
            breakdown[provider] = (breakdown[provider] ?: 0.0) + amountUsd

            val cfg = costConfigState.value
            if (newTotal > cfg.dailyThresholdUsd) {
                triggerThresholdAlertInternal(
                    dateKey = today,
                    totalUsd = newTotal,
                    config = cfg,
                    breakdown = breakdown
                )
            } else {
                _statusBanner.value = "$provider に $${String.format("%.4f", amountUsd)} を記録しました"
            }
        }
    }

    fun triggerManualAlertTest(todayTotalUsd: Double, breakdown: Map<String, Double>) {
        viewModelScope.launch {
            triggerThresholdAlertInternal(
                dateKey = FirestoreRepository.todayDateKey(),
                totalUsd = todayTotalUsd,
                config = costConfigState.value,
                breakdown = breakdown
            )
        }
    }

    private suspend fun triggerThresholdAlertInternal(
        dateKey: String,
        totalUsd: Double,
        config: CostConfig,
        breakdown: Map<String, Double>
    ) {
        if (!config.autoEmailEnabled) {
            repository.recordCostAlert(
                userId = currentUserId,
                dateKey = dateKey,
                totalUsd = totalUsd,
                thresholdUsd = config.dailyThresholdUsd,
                recipientEmail = config.alertEmail,
                deliveryStatus = "QUEUED_LOCAL",
                deliveryDetail = "Threshold exceeded while auto-email webhook is disabled"
            )
            _statusBanner.value = "日次しきい値 ($${String.format("%.2f", config.dailyThresholdUsd)}) を超過しました"
            return
        }

        val creds = _credentials.value
        val idToken = fetchFirebaseIdToken()
        val webhookRes = bridgeClient.sendCostAlertWebhook(
            baseUrl = creds.gc2223Endpoint,
            webhookPath = config.webhookPath,
            bearerToken = creds.gc2223BearerToken,
            firebaseIdToken = idToken,
            recipientEmail = config.alertEmail,
            dateKey = dateKey,
            totalUsd = totalUsd,
            thresholdUsd = config.dailyThresholdUsd,
            breakdownByProvider = breakdown
        )

        val status = if (webhookRes.success) "SENT" else "FAILED"
        repository.recordCostAlert(
            userId = currentUserId,
            dateKey = dateKey,
            totalUsd = totalUsd,
            thresholdUsd = config.dailyThresholdUsd,
            recipientEmail = config.alertEmail,
            deliveryStatus = status,
            deliveryDetail = webhookRes.message
        )
        _statusBanner.value = if (webhookRes.success) {
            "${config.alertEmail} 宛の超過アラートを送信しました"
        } else {
            "しきい値超過を記録 (Webhook: ${webhookRes.message})"
        }
    }

    fun deleteCostEntry(entryId: String) {
        viewModelScope.launch {
            repository.deleteCostEntry(currentUserId, entryId)
                .onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "削除に失敗しました"
                }
        }
    }

    fun saveBlueprint(
        title: String,
        category: String,
        summary: String,
        content: String,
        targetHost: String = "gc2223.com",
        status: String = "READY"
    ) {
        viewModelScope.launch {
            repository.saveBlueprint(
                userId = currentUserId,
                title = title,
                category = category,
                summary = summary,
                content = content,
                targetHost = targetHost,
                status = status
            ).onSuccess {
                _statusBanner.value = "ブループリント「$title」を Firestore に保存しました"
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "保存に失敗しました"
            }
        }
    }

    fun updateBlueprintStatus(blueprintId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateBlueprintStatus(currentUserId, blueprintId, newStatus)
                .onSuccess {
                    _statusBanner.value = "ステータスを $newStatus に更新しました"
                }
                .onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "更新に失敗しました"
                }
        }
    }

    fun deleteBlueprint(blueprintId: String) {
        viewModelScope.launch {
            repository.deleteBlueprint(currentUserId, blueprintId)
                .onSuccess {
                    _statusBanner.value = "ブループリントを削除しました"
                }
                .onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "削除に失敗しました"
                }
        }
    }
}
