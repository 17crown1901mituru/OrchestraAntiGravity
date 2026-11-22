package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.InMemoryOAuthSessionStore
import com.example.connectors.ConnectorContext
import com.example.connectors.ConnectorStatus
import com.example.connectors.GitHubDeviceCodeChallenge
import com.example.connectors.GitHubOrganizationSummary
import com.example.connectors.HubConnectorRegistry
import com.example.connectors.McpToolDefinition
import com.example.connectors.McpToolExecutionResult
import com.example.data.BridgeCallResult
import com.example.data.CloudBridgeClient
import com.example.data.FirestoreRepository
import com.example.data.GcpProvisioningClient
import com.example.data.LocalSecretsStore
import com.example.data.MultimodalSourceLoader
import com.example.data.OrchestrationExtendedClient
import com.example.data.OrchestrationHardeningClient
import com.example.data.RuntimeCloudBootstrapStore
import com.example.data.StackTemplates
import com.example.data.SyncSnapshotStore
import com.example.data.WebContextCrawler
import com.example.firebase.configureAppCheck
import com.example.model.AgentBlueprint
import com.example.model.AiGenerationState
import com.example.model.AskRagResult
import com.example.model.AttachedSource
import com.example.model.BackgroundSyncSettings
import com.example.model.CostAlert
import com.example.model.CostConfig
import com.example.model.CostEntry
import com.example.model.CrawlMode
import com.example.model.DeviceViewportProfile
import com.example.model.EmailRouteTarget
import com.example.model.ExternalCredentials
import com.example.model.GcpWizardState
import com.example.model.InferenceTarget
import com.example.model.OrchestrationHardeningState
import com.example.model.OrchestrationRunResult
import com.example.model.OrchestrationTaskType
import com.example.model.StreamUiState
import com.example.model.SyncStatusSnapshot
import com.example.model.TerraformAction
import com.example.model.TermuxCommandResult
import com.example.model.TermuxPresetCommand
import com.example.model.VaultMetadata
import com.example.model.WebCrawlPreview
import com.example.model.WorkflowNode
import com.example.model.WorkflowPlanResult
import com.example.security.AppSignatureHelper
import com.example.security.BiometricHardwareSigner
import com.example.sync.BackgroundSyncScheduler
import com.example.sync.SyncWorkUiState
import com.example.widget.updateGlanceWidgetFromState
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.UUID

private const val TAG = "HubViewModel"
private const val STOP_TIMEOUT_MILLIS = 5000L

@OptIn(ExperimentalCoroutinesApi::class)
class HubViewModel(
    appContext: Context,
    repository: FirestoreRepository? = null,
    private val secretsStore: LocalSecretsStore,
    private val syncSnapshotStore: SyncSnapshotStore,
    private val bridgeClient: CloudBridgeClient,
    private val currentUserId: String,
    private val userEmail: String,
    private val gcpClient: GcpProvisioningClient = GcpProvisioningClient(),
    private val connectorRegistry: HubConnectorRegistry = HubConnectorRegistry(),
    private val onFirebaseProvisioned: () -> Unit = {}
) : ViewModel() {

    private val applicationContext = appContext.applicationContext
    private val _repositoryFlow = MutableStateFlow(repository)

    private fun isFirebaseInitialized(): Boolean =
        FirebaseApp.getApps(applicationContext).isNotEmpty()

    private fun cloudRepoAndUid(): Pair<FirestoreRepository, String>? {
        val repo = _repositoryFlow.value ?: return null
        if (!isFirebaseInitialized()) return null
        val cloudUid = runCatching { Firebase.auth.currentUser?.uid }.getOrNull() ?: return null
        return repo to cloudUid
    }

    private val _localNodes = MutableStateFlow(buildDefaultLocalNodes(currentUserId, userEmail))
    private val _localBlueprints = MutableStateFlow<List<AgentBlueprint>>(emptyList())
    private val _localCostConfig = MutableStateFlow(
        CostConfig(userId = currentUserId, alertEmail = userEmail)
    )
    private val _localCostEntries = MutableStateFlow<List<CostEntry>>(emptyList())
    private val _localCostAlerts = MutableStateFlow<List<CostAlert>>(emptyList())

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

    val nodesState: StateFlow<StreamUiState<List<WorkflowNode>>> = _repositoryFlow
        .flatMapLatest { repo ->
            val cloudUid = if (repo != null && isFirebaseInitialized()) {
                runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
            } else null
            if (repo != null && cloudUid != null) {
                repo.observeNodes(cloudUid)
                    .map<List<WorkflowNode>, StreamUiState<List<WorkflowNode>>> { StreamUiState.Success(it) }
                    .catch { error ->
                        emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load workflow nodes")))
                    }
            } else {
                _localNodes.map { StreamUiState.Success(it) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Success(_localNodes.value)
        )

    val blueprintsState: StateFlow<StreamUiState<List<AgentBlueprint>>> = _repositoryFlow
        .flatMapLatest { repo ->
            val cloudUid = if (repo != null && isFirebaseInitialized()) {
                runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
            } else null
            if (repo != null && cloudUid != null) {
                repo.observeBlueprints(cloudUid)
                    .map<List<AgentBlueprint>, StreamUiState<List<AgentBlueprint>>> { list ->
                        val sorted = list.sortedByDescending { it.updatedAt?.seconds ?: it.createdAt?.seconds ?: 0L }
                        StreamUiState.Success(sorted)
                    }
                    .catch { error ->
                        emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load blueprints")))
                    }
            } else {
                _localBlueprints.map { StreamUiState.Success(it) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Success(emptyList())
        )

    val costConfigState: StateFlow<CostConfig> = _repositoryFlow
        .flatMapLatest { repo ->
            val cloudUid = if (repo != null && isFirebaseInitialized()) {
                runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
            } else null
            if (repo != null && cloudUid != null) {
                repo.observeCostConfig(cloudUid)
                    .catch { error ->
                        unwrapFirestoreError(error, "Failed to observe cost config")
                        emit(CostConfig(userId = cloudUid, alertEmail = userEmail))
                    }
            } else {
                _localCostConfig
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = CostConfig(userId = currentUserId, alertEmail = userEmail)
        )

    val costEntriesState: StateFlow<StreamUiState<List<CostEntry>>> = _repositoryFlow
        .flatMapLatest { repo ->
            val cloudUid = if (repo != null && isFirebaseInitialized()) {
                runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
            } else null
            if (repo != null && cloudUid != null) {
                repo.observeCostEntries(cloudUid)
                    .map<List<CostEntry>, StreamUiState<List<CostEntry>>> { list ->
                        val sorted = list.sortedByDescending { it.createdAt?.seconds ?: 0L }
                        StreamUiState.Success(sorted)
                    }
                    .catch { error ->
                        emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load cost entries")))
                    }
            } else {
                _localCostEntries.map { StreamUiState.Success(it) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Success(emptyList())
        )

    val costAlertsState: StateFlow<StreamUiState<List<CostAlert>>> = _repositoryFlow
        .flatMapLatest { repo ->
            val cloudUid = if (repo != null && isFirebaseInitialized()) {
                runCatching { Firebase.auth.currentUser?.uid }.getOrNull()
            } else null
            if (repo != null && cloudUid != null) {
                repo.observeCostAlerts(cloudUid)
                    .map<List<CostAlert>, StreamUiState<List<CostAlert>>> { list ->
                        val sorted = list.sortedByDescending { it.createdAt?.seconds ?: 0L }
                        StreamUiState.Success(sorted)
                    }
                    .catch { error ->
                        emit(StreamUiState.Error(unwrapFirestoreError(error, "Failed to load cost alerts")))
                    }
            } else {
                _localCostAlerts.map { StreamUiState.Success(it) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StreamUiState.Success(emptyList())
        )

    private val _aiState = MutableStateFlow<AiGenerationState>(AiGenerationState.Idle)
    val aiState: StateFlow<AiGenerationState> = _aiState.asStateFlow()

    private val _inferenceTarget = MutableStateFlow(
        if (isFirebaseInitialized()) InferenceTarget.FIREBASE_GEMINI else InferenceTarget.GC2223_GATEWAY
    )
    val inferenceTarget: StateFlow<InferenceTarget> = _inferenceTarget.asStateFlow()

    private val _attachedSources = MutableStateFlow<List<AttachedSource>>(emptyList())
    val attachedSources: StateFlow<List<AttachedSource>> = _attachedSources.asStateFlow()

    private val sourceLoader by lazy { MultimodalSourceLoader(applicationContext) }
    private val webCrawler by lazy { WebContextCrawler(applicationContext) }

    private val _isCrawlingWeb = MutableStateFlow(false)
    val isCrawlingWeb: StateFlow<Boolean> = _isCrawlingWeb.asStateFlow()

    private val _lastCrawlPreview = MutableStateFlow<WebCrawlPreview?>(null)
    val lastCrawlPreview: StateFlow<WebCrawlPreview?> = _lastCrawlPreview.asStateFlow()

    private val _isSelfHealing = MutableStateFlow(false)
    val isSelfHealing: StateFlow<Boolean> = _isSelfHealing.asStateFlow()

    private val _selfHealReport = MutableStateFlow<String?>(null)
    val selfHealReport: StateFlow<String?> = _selfHealReport.asStateFlow()

    private val _isRunningTermuxCommand = MutableStateFlow(false)
    val isRunningTermuxCommand: StateFlow<Boolean> = _isRunningTermuxCommand.asStateFlow()

    private val _lastTermuxCommandResult = MutableStateFlow<TermuxCommandResult?>(null)
    val lastTermuxCommandResult: StateFlow<TermuxCommandResult?> = _lastTermuxCommandResult.asStateFlow()

    private val _isOrchestrating = MutableStateFlow(false)
    val isOrchestrating: StateFlow<Boolean> = _isOrchestrating.asStateFlow()

    private val _lastOrchestrationResult = MutableStateFlow<OrchestrationRunResult?>(null)
    val lastOrchestrationResult: StateFlow<OrchestrationRunResult?> = _lastOrchestrationResult.asStateFlow()

    private val hardeningClient by lazy { OrchestrationHardeningClient() }
    private val extendedClient by lazy { OrchestrationExtendedClient() }

    private val _hardeningState = MutableStateFlow(OrchestrationHardeningState())
    val hardeningState: StateFlow<OrchestrationHardeningState> = _hardeningState.asStateFlow()

    private val _isHardeningBusy = MutableStateFlow(false)
    val isHardeningBusy: StateFlow<Boolean> = _isHardeningBusy.asStateFlow()

    private val _lastAskResult = MutableStateFlow<AskRagResult?>(null)
    val lastAskResult: StateFlow<AskRagResult?> = _lastAskResult.asStateFlow()

    private val _isAskingRag = MutableStateFlow(false)
    val isAskingRag: StateFlow<Boolean> = _isAskingRag.asStateFlow()

    private val _vaultMetadata = MutableStateFlow(secretsStore.getVaultMetadata(currentUserId, userEmail))

    private val _credentials = MutableStateFlow(enrichFromRuntimeBootstrap(secretsStore.loadForUser(currentUserId)))
    val credentials: StateFlow<ExternalCredentials> = _credentials.asStateFlow()

    val vaultMetadata: StateFlow<VaultMetadata> = _vaultMetadata.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _isBusyNodeAction = MutableStateFlow(false)
    val isBusyNodeAction: StateFlow<Boolean> = _isBusyNodeAction.asStateFlow()

    private val _settingsVerificationStatus = MutableStateFlow<String?>(null)
    val settingsVerificationStatus: StateFlow<String?> = _settingsVerificationStatus.asStateFlow()

    private val _gcpWizardState = MutableStateFlow(
        GcpWizardState(
            accessToken = InMemoryOAuthSessionStore.accessToken,
            oauthAuthorized = InMemoryOAuthSessionStore.accessToken.isNotBlank(),
            selectedProjectId = InMemoryOAuthSessionStore.selectedGcpProjectId.ifBlank { _credentials.value.gcpProjectId },
            signingDiagnostics = runCatching { AppSignatureHelper.readDiagnostics(applicationContext) }.getOrNull(),
            statusMessage = if (InMemoryOAuthSessionStore.accessToken.isNotBlank()) {
                "ログイン時の Google OAuth (cloud-platform) を引き継ぎました"
            } else null
        )
    )
    val gcpWizardState: StateFlow<GcpWizardState> = _gcpWizardState.asStateFlow()

    private val _connectorStatuses = MutableStateFlow<List<ConnectorStatus>>(emptyList())
    val connectorStatuses: StateFlow<List<ConnectorStatus>> = _connectorStatuses.asStateFlow()

    private val _mcpTools = MutableStateFlow<List<McpToolDefinition>>(emptyList())
    val mcpTools: StateFlow<List<McpToolDefinition>> = _mcpTools.asStateFlow()

    private val _gitHubDeviceChallenge = MutableStateFlow<GitHubDeviceCodeChallenge?>(null)
    val gitHubDeviceChallenge: StateFlow<GitHubDeviceCodeChallenge?> = _gitHubDeviceChallenge.asStateFlow()

    private val _gitHubOrganizations = MutableStateFlow<List<GitHubOrganizationSummary>>(emptyList())
    val gitHubOrganizations: StateFlow<List<GitHubOrganizationSummary>> = _gitHubOrganizations.asStateFlow()

    private val _gitHubDiscoveredRepos = MutableStateFlow<List<String>>(emptyList())
    val gitHubDiscoveredRepos: StateFlow<List<String>> = _gitHubDiscoveredRepos.asStateFlow()

    private val _lastMcpToolResult = MutableStateFlow<McpToolExecutionResult?>(null)
    val lastMcpToolResult: StateFlow<McpToolExecutionResult?> = _lastMcpToolResult.asStateFlow()

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
            cloudRepoAndUid()?.let { (repo, cloudUid) ->
                repo.seedDefaultNodesIfEmpty(cloudUid, userEmail)
                    .onFailure { err -> Log.w(TAG, "Initial node seed failed", err) }
                repo.seedDefaultCostConfigIfMissing(cloudUid, userEmail)
                    .onFailure { err -> Log.w(TAG, "Initial cost config seed failed", err) }
            }
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
            val hasCloudTargets = savedCreds.githubPat.isNotBlank() ||
                savedCreds.githubOwnerRepo.isNotBlank() ||
                savedCreds.cloudflareApiToken.isNotBlank() ||
                savedCreds.gc2223Endpoint.isNotBlank()
            if (hasCloudTargets && snap.syncedAtEpochMs == 0L) {
                BackgroundSyncScheduler.triggerImmediateSync(applicationContext, currentUserId)
            } else if (snap.termuxSyncedAtEpochMs == 0L) {
                BackgroundSyncScheduler.triggerImmediateTermuxSync(applicationContext, currentUserId)
            }
        }
        if (InMemoryOAuthSessionStore.accessToken.isNotBlank()) {
            loadGcpProjectsAndBilling()
        }
    }

    private fun enrichFromRuntimeBootstrap(loaded: ExternalCredentials): ExternalCredentials {
        val bootstrap = runCatching { RuntimeCloudBootstrapStore(applicationContext).load() }.getOrNull()
            ?: return loaded
        val hasVault = _vaultMetadata.value.hasStoredSecrets
        val effectiveGateway = when {
            bootstrap.gatewayUrl.isNotBlank() && (!hasVault || loaded.gc2223Endpoint == "https://gc2223.com") ->
                bootstrap.gatewayUrl
            else -> loaded.gc2223Endpoint
        }
        val effectiveRepo = loaded.githubOwnerRepo.ifBlank { bootstrap.defaultGithubRepo }
        val effectiveProject = when {
            bootstrap.firebaseProjectId.isNotBlank() && !hasVault -> bootstrap.firebaseProjectId
            else -> loaded.gcpProjectId
        }
        return loaded.copy(
            gc2223Endpoint = effectiveGateway,
            githubOwnerRepo = effectiveRepo,
            gcpProjectId = effectiveProject
        )
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

    fun attachCloudRepository(cloudRepo: FirestoreRepository) {
        _repositoryFlow.value = cloudRepo
        viewModelScope.launch {
            cloudRepoAndUid()?.let { (repo, cloudUid) ->
                repo.seedDefaultNodesIfEmpty(cloudUid, userEmail)
                    .onFailure { err -> Log.w(TAG, "On-demand node seed failed", err) }
                repo.seedDefaultCostConfigIfMissing(cloudUid, userEmail)
                    .onFailure { err -> Log.w(TAG, "On-demand cost config seed failed", err) }
            }
        }
    }

    private suspend fun persistNodeStatus(
        nodeId: String,
        status: String,
        endpointOrTarget: String,
        lastCheckedNote: String
    ): Result<Unit> {
        _localNodes.update { list ->
            list.map { node ->
                if (node.id == nodeId) {
                    node.copy(
                        status = status,
                        endpointOrTarget = endpointOrTarget.take(200),
                        lastCheckedNote = lastCheckedNote.take(300)
                    )
                } else node
            }
        }
        val (repo, cloudUid) = cloudRepoAndUid() ?: return Result.success(Unit)
        return repo.updateNodeStatus(cloudUid, nodeId, status, endpointOrTarget, lastCheckedNote)
    }

    private suspend fun persistCostEntry(
        provider: String,
        amountUsd: Double,
        usageSummary: String,
        source: String,
        dateKey: String = FirestoreRepository.todayDateKey()
    ): Result<String> {
        val localEntry = CostEntry(
            id = UUID.randomUUID().toString(),
            userId = currentUserId,
            provider = provider,
            dateKey = dateKey,
            amountUsd = amountUsd,
            usageSummary = usageSummary,
            source = source
        )
        _localCostEntries.update { listOf(localEntry) + it }
        val (repo, cloudUid) = cloudRepoAndUid() ?: return Result.success(localEntry.id)
        return repo.addCostEntry(cloudUid, provider, amountUsd, usageSummary, source, dateKey)
    }

    private suspend fun persistCostConfig(
        alertEmail: String,
        dailyThresholdUsd: Double,
        autoEmailEnabled: Boolean,
        webhookPath: String
    ): Result<Unit> {
        _localCostConfig.update {
            it.copy(
                alertEmail = alertEmail,
                dailyThresholdUsd = dailyThresholdUsd,
                autoEmailEnabled = autoEmailEnabled,
                webhookPath = webhookPath
            )
        }
        val (repo, cloudUid) = cloudRepoAndUid() ?: return Result.success(Unit)
        return repo.updateCostConfig(cloudUid, alertEmail, dailyThresholdUsd, autoEmailEnabled, webhookPath)
    }

    private suspend fun persistCostAlert(
        dateKey: String,
        totalUsd: Double,
        thresholdUsd: Double,
        recipientEmail: String,
        deliveryStatus: String,
        deliveryDetail: String
    ): Result<Unit> {
        val localAlert = CostAlert(
            id = UUID.randomUUID().toString(),
            userId = currentUserId,
            dateKey = dateKey,
            totalUsd = totalUsd,
            thresholdUsd = thresholdUsd,
            recipientEmail = recipientEmail,
            deliveryStatus = deliveryStatus,
            deliveryDetail = deliveryDetail
        )
        _localCostAlerts.update { listOf(localAlert) + it }
        val (repo, cloudUid) = cloudRepoAndUid() ?: return Result.success(Unit)
        return repo.recordCostAlert(cloudUid, dateKey, totalUsd, thresholdUsd, recipientEmail, deliveryStatus, deliveryDetail)
    }

    private suspend fun fetchFirebaseIdToken(): String? {
        if (!isFirebaseInitialized()) return null
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

    fun crawlDomainOrUrl(
        rawUrlOrDomain: String,
        mode: CrawlMode,
        profile: DeviceViewportProfile,
        preferEnglish: Boolean = true,
        maxPages: Int = if (mode == CrawlMode.PATH_RECURSIVE) 20 else 4
    ) {
        if (rawUrlOrDomain.isBlank() || _isCrawlingWeb.value) return
        viewModelScope.launch {
            _isCrawlingWeb.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val result = webCrawler.crawlAndCapture(
                    rawUrlOrDomain = rawUrlOrDomain,
                    mode = mode,
                    profile = profile,
                    gatewayBaseUrl = creds.gc2223Endpoint,
                    gatewayBearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken,
                    maxPages = maxPages,
                    preferEnglish = preferEnglish
                )
                result.onSuccess { output ->
                    val newUris = buildList {
                        add(output.markdownUri)
                        output.screenshotUri?.let { add(it) }
                    }
                    addAttachedUris(newUris)
                    _lastCrawlPreview.value = WebCrawlPreview(
                        targetUrl = output.normalizedUrl,
                        pageCount = output.pageCount,
                        deviceProfileLabel = profile.label,
                        summarySnippet = output.summarySnippet,
                        attachedFileName = output.markdownFileName,
                        hasScreenshot = output.screenshotUri != null,
                        savedSessionDirName = output.sessionDirectoryName,
                        languageSummary = output.languageSummary
                    )
                    val screenSuffix = if (output.screenshotUri != null) " + 画面PNG" else ""
                    val mdSuffix = if (output.perPageMarkdownFiles.size > 1) {
                        " (.md ${output.perPageMarkdownFiles.size}件)"
                    } else ""
                    val langSuffix = if (output.languageSummary.isNotBlank()) {
                        " [${output.languageSummary}]"
                    } else ""
                    _statusBanner.value = "${output.pageCount}ページ取得し添付しました$mdSuffix$langSuffix$screenSuffix".trim()
                }.onFailure { err ->
                    _statusBanner.value = "URL取得失敗: ${err.localizedMessage ?: "Network error"}"
                }
            } finally {
                _isCrawlingWeb.value = false
            }
        }
    }

    fun attachDeviceScreenshotBitmap(
        bitmap: Bitmap,
        rawUrlOrDomain: String,
        profile: DeviceViewportProfile
    ) {
        viewModelScope.launch {
            runCatching {
                val uri = webCrawler.saveDeviceScreenshotBitmap(bitmap, rawUrlOrDomain, profile)
                addAttachedUris(listOf(uri))
                val current = _lastCrawlPreview.value
                _lastCrawlPreview.value = current?.copy(hasScreenshot = true) ?: WebCrawlPreview(
                    targetUrl = webCrawler.normalizeUrlOrDomain(rawUrlOrDomain),
                    pageCount = 1,
                    deviceProfileLabel = profile.label,
                    summarySnippet = "${profile.label} (${profile.widthDp}x${profile.heightDp}dp) 画面キャプチャ",
                    attachedFileName = uri.lastPathSegment.orEmpty(),
                    hasScreenshot = true
                )
                _statusBanner.value = "${profile.label} の最新画面を添付しました"
            }.onFailure { err ->
                _statusBanner.value = "画面キャプチャ保存に失敗: ${err.localizedMessage ?: "IO error"}"
            }
        }
    }

    fun pushTemplateToGitHub(
        filePath: String,
        content: String,
        commitMessage: String
    ) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val res = bridgeClient.commitFileToGitHub(
                    pat = creds.githubPat,
                    ownerRepo = creds.githubOwnerRepo,
                    filePath = filePath,
                    content = content,
                    commitMessage = commitMessage,
                    gatewayUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun dispatchTerraformRun(
        action: TerraformAction,
        targetEnv: String = "dev",
        workflowFile: String = "antigravity-terraform.yml"
    ) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val res = bridgeClient.dispatchTerraformWorkflow(
                    pat = creds.githubPat,
                    ownerRepo = creds.githubOwnerRepo,
                    workflowFile = workflowFile,
                    action = action,
                    targetEnv = targetEnv,
                    gatewayUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                if (res.success) {
                    persistCostEntry(
                        provider = "GITHUB",
                        amountUsd = 0.0,
                        usageSummary = "Terraform ${action.cliValue} ($targetEnv) on ${creds.githubOwnerRepo.ifBlank { "sso-default" }}",
                        source = "AUTO_TELEMETRY"
                    )
                }
                _statusBanner.value = res.message
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun pushAllOrchestrationStackToGitHub() {
        if (_isBusyNodeAction.value) return
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val targets = listOf(
                    "terraform_llm_infra" to "infra/main.tf",
                    "governance_policy_json" to "governance-policy.json",
                    "cf_email_autofix_worker" to "worker/src/worker.js",
                    "wrangler_email_selfheal" to "worker/wrangler.toml",
                    "github_actions_terraform" to ".github/workflows/antigravity-terraform.yml",
                    "github_actions_orchestrator" to ".github/workflows/antigravity-orchestrator.yml",
                    "dr_bootstrap_script" to "scripts/bootstrap-dr.sh"
                )
                var successCount = 0
                var lastErr = ""
                for ((tplId, path) in targets) {
                    val tpl = StackTemplates.all.firstOrNull { it.id == tplId } ?: continue
                    val res = bridgeClient.commitFileToGitHub(
                        pat = creds.githubPat,
                        ownerRepo = creds.githubOwnerRepo,
                        filePath = path,
                        content = tpl.content,
                        commitMessage = "chore(antigravity): bootstrap orchestration stack ($path)",
                        gatewayUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = idToken
                    )
                    if (res.success) {
                        successCount++
                    } else {
                        lastErr = res.message
                        break
                    }
                }
                _statusBanner.value = if (successCount == targets.size) {
                    "オーケストレーション構成 (${successCount}ファイル) を GitHub へ一括 Push しました"
                } else {
                    "一括 Push 中断 (${successCount}/${targets.size}): $lastErr"
                }
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun runOrchestrationTask(
        routeTarget: EmailRouteTarget,
        taskType: OrchestrationTaskType,
        promptOrInput: String,
        targetEnv: String = "dev"
    ) {
        if (_isOrchestrating.value) return
        viewModelScope.launch {
            _isOrchestrating.value = true
            try {
                val creds = _credentials.value
                val res = bridgeClient.triggerOrchestrationTask(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = fetchFirebaseIdToken(),
                    routeTarget = routeTarget,
                    taskType = taskType,
                    ownerRepo = creds.githubOwnerRepo,
                    promptOrInput = promptOrInput,
                    targetEnv = targetEnv
                )
                _lastOrchestrationResult.value = res
                _statusBanner.value = res.summary
                if (res.success) {
                    val tidSuffix = if (res.traceId.isNotBlank()) " [tid:${res.traceId}]" else ""
                    persistCostEntry(
                        provider = "CLOUDFLARE",
                        amountUsd = 0.0,
                        usageSummary = "Orchestrate ${routeTarget.address} (${taskType.taskId})$tidSuffix".take(195),
                        source = "AUTO_TELEMETRY"
                    )
                    refreshHardeningState()
                }
            } finally {
                _isOrchestrating.value = false
            }
        }
    }

    fun refreshHardeningState() {
        if (_isHardeningBusy.value) return
        viewModelScope.launch {
            _isHardeningBusy.value = true
            try {
                val creds = _credentials.value
                val snapshot = hardeningClient.fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = fetchFirebaseIdToken()
                )
                _hardeningState.value = snapshot
                updateGlanceWidgetFromState(applicationContext, snapshot)
            } finally {
                _isHardeningBusy.value = false
            }
        }
    }

    fun registerBiometricHardwareKey() {
        if (_isHardeningBusy.value) return
        viewModelScope.launch {
            _isHardeningBusy.value = true
            try {
                if (!BiometricHardwareSigner.canAuthenticateBiometric(applicationContext)) {
                    _statusBanner.value = "端末に生体認証 (指紋/顔 Strong Biometric) が設定されていません"
                    return@launch
                }
                val exported = BiometricHardwareSigner.ensureOrCreateSigningKey()
                    .getOrElse { err ->
                        _statusBanner.value = "Keystore 鍵生成失敗: ${err.localizedMessage ?: "Error"}"
                        return@launch
                    }
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val res = extendedClient.registerHardwarePublicKey(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken,
                    keyId = exported.keyId,
                    spkiB64 = exported.spkiB64
                )
                _statusBanner.value = res.message
                val snapshot = hardeningClient.fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                _hardeningState.value = snapshot
                updateGlanceWidgetFromState(applicationContext, snapshot)
            } finally {
                _isHardeningBusy.value = false
            }
        }
    }

    fun decideApprovalZeroTrust(activity: FragmentActivity?, approvalId: String, approve: Boolean) {
        if (_isHardeningBusy.value || approvalId.isBlank()) return
        viewModelScope.launch {
            _isHardeningBusy.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                var challengeStr: String? = null
                var actionHashStr: String? = null
                var sigB64: String? = null
                var keyIdStr: String? = null

                if (approve && _hardeningState.value.hardwareKeyRegistered) {
                    val chRes = extendedClient.requestApprovalChallenge(
                        baseUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = idToken,
                        approvalId = approvalId
                    )
                    if (!chRes.success) {
                        _statusBanner.value = chRes.message
                        return@launch
                    }
                    if (chRes.requiresHardwareSignature) {
                        if (activity == null) {
                            _statusBanner.value = "生体認証プロンプトの起動にはアクティブな画面コンテキストが必要です"
                            return@launch
                        }
                        if (!BiometricHardwareSigner.hasHardwareKey()) {
                            _statusBanner.value = "この端末に Keystore 秘密鍵が存在しません。先に「生体署名鍵を登録」を実行してください"
                            return@launch
                        }
                        val payloadToSign = "${approvalId}|${chRes.actionHash}|${chRes.challenge}"
                        val signed = BiometricHardwareSigner.signChallengeWithBiometric(
                            activity = activity,
                            payload = payloadToSign
                        )
                        if (signed.isFailure) {
                            _statusBanner.value = signed.exceptionOrNull()?.localizedMessage ?: "生体署名がキャンセルされました"
                            return@launch
                        }
                        challengeStr = chRes.challenge
                        actionHashStr = chRes.actionHash
                        sigB64 = signed.getOrThrow()
                        keyIdStr = chRes.keyId.ifBlank { BiometricHardwareSigner.exportPublicKeyIfPresent()?.keyId }
                    }
                }

                val res = hardeningClient.decideApproval(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken,
                    approvalId = approvalId,
                    approve = approve,
                    challenge = challengeStr,
                    actionHash = actionHashStr,
                    signatureB64 = sigB64,
                    keyId = keyIdStr
                )
                _statusBanner.value = res.message
                if (res.success) {
                    persistCostEntry(
                        provider = "CLOUDFLARE",
                        amountUsd = 0.0,
                        usageSummary = res.message.take(195),
                        source = "AUTO_TELEMETRY"
                    )
                }
                val snapshot = hardeningClient.fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                _hardeningState.value = snapshot
                updateGlanceWidgetFromState(applicationContext, snapshot)
            } finally {
                _isHardeningBusy.value = false
            }
        }
    }

    fun decideApproval(approvalId: String, approve: Boolean) {
        decideApprovalZeroTrust(activity = null, approvalId = approvalId, approve = approve)
    }

    fun askAuditKnowledge(question: String) {
        if (_isAskingRag.value || question.isBlank()) return
        viewModelScope.launch {
            _isAskingRag.value = true
            try {
                val creds = _credentials.value
                val res = extendedClient.askAuditKnowledge(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = fetchFirebaseIdToken(),
                    question = question
                )
                _lastAskResult.value = res
                _statusBanner.value = res.statusMessage
            } finally {
                _isAskingRag.value = false
            }
        }
    }

    fun setCircuitBreakerSafeMode(enable: Boolean, reason: String) {
        if (_isHardeningBusy.value) return
        viewModelScope.launch {
            _isHardeningBusy.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val res = hardeningClient.toggleCircuitBreakerSafeMode(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken,
                    enableSafeMode = enable,
                    reason = reason
                )
                _statusBanner.value = res.message
                val snapshot = hardeningClient.fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                _hardeningState.value = snapshot
                updateGlanceWidgetFromState(applicationContext, snapshot)
            } finally {
                _isHardeningBusy.value = false
            }
        }
    }

    fun setSustainabilityMode(mode: String) {
        if (_isHardeningBusy.value) return
        viewModelScope.launch {
            _isHardeningBusy.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val res = hardeningClient.setOperatingMode(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken,
                    mode = mode
                )
                _statusBanner.value = res.message
                val snapshot = hardeningClient.fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                _hardeningState.value = snapshot
                updateGlanceWidgetFromState(applicationContext, snapshot)
            } finally {
                _isHardeningBusy.value = false
            }
        }
    }

    fun syncGovernancePolicy() {
        if (_isHardeningBusy.value) return
        viewModelScope.launch {
            _isHardeningBusy.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val policyTpl = StackTemplates.all.firstOrNull { it.id == "governance_policy_json" }?.content
                    ?: "{}"
                val res = hardeningClient.syncGovernancePolicy(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken,
                    policyJsonRaw = policyTpl
                )
                _statusBanner.value = res.message
                val snapshot = hardeningClient.fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                _hardeningState.value = snapshot
                updateGlanceWidgetFromState(applicationContext, snapshot)
            } finally {
                _isHardeningBusy.value = false
            }
        }
    }

    fun verifyAuditChain() {
        runHardeningAction { creds, idToken ->
            hardeningClient.verifyAuditChain(creds.gc2223Endpoint, creds.gc2223BearerToken, idToken)
        }
    }

    fun runChaosDrill() {
        runHardeningAction { creds, idToken ->
            hardeningClient.runChaosDrill(creds.gc2223Endpoint, creds.gc2223BearerToken, idToken)
        }
    }

    fun registerLocalNode(
        nodeId: String = "android-termux-primary",
        nodeType: String = "termux",
        capabilities: List<String> = listOf("termux", "android_sms")
    ) {
        runHardeningAction { creds, idToken ->
            hardeningClient.registerFederatedNode(
                baseUrl = creds.gc2223Endpoint,
                bearerToken = creds.gc2223BearerToken,
                firebaseIdToken = idToken,
                nodeId = nodeId,
                nodeType = nodeType,
                capabilities = capabilities
            )
        }
    }

    fun triggerPolicyAutoTune() {
        runHardeningAction { creds, idToken ->
            hardeningClient.triggerPolicyAutoTune(
                baseUrl = creds.gc2223Endpoint,
                bearerToken = creds.gc2223BearerToken,
                firebaseIdToken = idToken,
                ownerRepo = creds.githubOwnerRepo
            )
        }
    }

    fun createDrSnapshot() {
        runHardeningAction { creds, idToken ->
            hardeningClient.createDrSnapshot(creds.gc2223Endpoint, creds.gc2223BearerToken, idToken)
        }
    }

    fun restoreLatestDrSnapshot() {
        runHardeningAction { creds, idToken ->
            hardeningClient.restoreDrSnapshot(creds.gc2223Endpoint, creds.gc2223BearerToken, idToken)
        }
    }

    fun pingDeadmanSwitch() {
        runHardeningAction { creds, idToken ->
            hardeningClient.pingDeadmanSwitch(creds.gc2223Endpoint, creds.gc2223BearerToken, idToken)
        }
    }

    private fun runHardeningAction(
        block: suspend (ExternalCredentials, String?) -> BridgeCallResult
    ) {
        if (_isHardeningBusy.value) return
        viewModelScope.launch {
            _isHardeningBusy.value = true
            try {
                val creds = _credentials.value
                val idToken = fetchFirebaseIdToken()
                val res = block(creds, idToken)
                _statusBanner.value = res.message
                val snapshot = hardeningClient.fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
                _hardeningState.value = snapshot
                updateGlanceWidgetFromState(applicationContext, snapshot)
            } finally {
                _isHardeningBusy.value = false
            }
        }
    }

    fun clearSelfHealReport() {
        _selfHealReport.value = null
    }

    fun runTermuxPresetCommand(
        preset: TermuxPresetCommand,
        workdir: String = ""
    ) {
        if (_isRunningTermuxCommand.value) return
        viewModelScope.launch {
            _isRunningTermuxCommand.value = true
            try {
                val creds = _credentials.value
                val res = bridgeClient.executeTermuxSafeCommand(
                    agentUrl = creds.termuxAgentUrl,
                    bearerToken = creds.termuxAgentToken,
                    preset = preset,
                    workdir = workdir
                )
                _lastTermuxCommandResult.value = res
                _statusBanner.value = res.summaryMessage
                BackgroundSyncScheduler.triggerImmediateTermuxSync(applicationContext, currentUserId)
            } finally {
                _isRunningTermuxCommand.value = false
            }
        }
    }

    fun rerunFailedGitHubRun(runId: Long) {
        viewModelScope.launch {
            _isSelfHealing.value = true
            try {
                val creds = _credentials.value
                val res = bridgeClient.rerunFailedGitHubWorkflow(
                    pat = creds.githubPat,
                    ownerRepo = creds.githubOwnerRepo,
                    runId = runId,
                    gatewayUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = fetchFirebaseIdToken()
                )
                _selfHealReport.value = res.message
                _statusBanner.value = res.message
            } finally {
                _isSelfHealing.value = false
            }
        }
    }

    fun diagnoseErrorAndSelfHeal(
        errorLog: String,
        repoOverride: String,
        workflowFile: String,
        runId: Long,
        sendToEmailWorker: Boolean,
        rerunWorkflow: Boolean,
        retryCount: Int = 0
    ) {
        if (errorLog.isBlank() || _isSelfHealing.value) return
        viewModelScope.launch {
            _isSelfHealing.value = true
            try {
                val creds = _credentials.value
                val effectiveRepo = repoOverride.ifBlank { creds.githubOwnerRepo }
                val effectiveWorkflow = workflowFile.ifBlank { creds.githubWorkflowFile }
                val idToken = fetchFirebaseIdToken()

                val prompt = buildString {
                    appendLine("You are AntiGravity Self-Heal Engineer.")
                    appendLine("Diagnose this Terraform / GitHub Actions / Cloud error concisely in Japanese.")
                    appendLine("Output: 1) 原因要約, 2) 修正パッチ(HCL/YAML/CLI), 3) 再実行の注意点。")
                    appendLine("Repo: $effectiveRepo | Workflow: $effectiveWorkflow | RunId: $runId")
                    appendLine()
                    append(errorLog.take(6000))
                }

                val aiDiagnosis = if (!isFirebaseInitialized() && !RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)) {
                    "Firebase 未初期化のため、ローカル / MCP ステータスのみ解析します。\n「状態 → GCP連携」からプロジェクトを連携すると Gemini 自己修復解析が有効になります。"
                } else {
                    runCatching {
                        withTimeout(45_000L) {
                            chatModel.generateContent(prompt).text.orEmpty()
                        }
                    }.getOrElse { err ->
                        "Gemini 診断エラー: ${err.localizedMessage ?: "Timeout"}"
                    }
                }

                val reportLines = mutableListOf<String>()
                reportLines.add("### Gemini エラー診断・修正案")
                reportLines.add(aiDiagnosis.ifBlank { "診断出力が空でした。" })

                if (sendToEmailWorker) {
                    val emailRes = bridgeClient.triggerEmailSelfHealWorker(
                        baseUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = idToken,
                        recipientEmail = costConfigState.value.alertEmail.ifBlank { userEmail },
                        ownerRepo = effectiveRepo,
                        workflowFile = effectiveWorkflow,
                        runId = runId,
                        errorLog = errorLog,
                        suggestedPatch = aiDiagnosis,
                        retryCount = retryCount
                    )
                    reportLines.add("\n### Email Worker 連携")
                    reportLines.add(emailRes.message)
                }

                if (rerunWorkflow) {
                    val rerunRes = if (runId > 0L) {
                        bridgeClient.rerunFailedGitHubWorkflow(
                            pat = creds.githubPat,
                            ownerRepo = effectiveRepo,
                            runId = runId,
                            gatewayUrl = creds.gc2223Endpoint,
                            bearerToken = creds.gc2223BearerToken,
                            firebaseIdToken = idToken
                        )
                    } else {
                        bridgeClient.dispatchGitHubWorkflow(
                            pat = creds.githubPat,
                            ownerRepo = effectiveRepo,
                            workflowFile = effectiveWorkflow,
                            inputs = mapOf(
                                "triggered_by" to "antigravity-self-heal",
                                "retry_count" to (retryCount + 1).toString()
                            ),
                            gatewayUrl = creds.gc2223Endpoint,
                            bearerToken = creds.gc2223BearerToken,
                            firebaseIdToken = idToken
                        )
                    }
                    reportLines.add("\n### ワークフロー再実行")
                    reportLines.add(rerunRes.message)
                }

                _selfHealReport.value = reportLines.joinToString("\n")
                _statusBanner.value = "エラー診断と自己修復フローを実行しました"
            } finally {
                _isSelfHealing.value = false
            }
        }
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
        InMemoryOAuthSessionStore.clear()
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
                    updated.cloudflareApiToken.isNotBlank() ||
                    updated.gc2223Endpoint.isNotBlank()
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
                _vaultMetadata.value = meta
                _credentials.value = enrichFromRuntimeBootstrap(ExternalCredentials())
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
                val creds = _credentials.value
                val msg = if (pat.isNotBlank()) {
                    bridgeClient.verifyGitHubPat(pat).message
                } else {
                    val status = bridgeClient.fetchGitHubSyncStatus(
                        pat = "",
                        ownerRepo = creds.githubOwnerRepo,
                        workflowFile = creds.githubWorkflowFile,
                        gatewayUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = fetchFirebaseIdToken()
                    )
                    status.summaryMessage
                }
                _settingsVerificationStatus.value = msg
                _statusBanner.value = msg
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun verifyCloudflareFromSettings(token: String) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val creds = _credentials.value
                val msg = if (token.isNotBlank()) {
                    bridgeClient.verifyCloudflareToken(token).message
                } else {
                    val status = bridgeClient.fetchCloudflareSyncStatus(
                        apiToken = "",
                        accountId = creds.cloudflareAccountId,
                        gatewayUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = fetchFirebaseIdToken()
                    )
                    status.summaryMessage
                }
                _settingsVerificationStatus.value = msg
                _statusBanner.value = msg
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
            if (!isFirebaseInitialized() && !RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)) {
                _aiState.value = AiGenerationState.Error(
                    "Firebase はまだオンデマンド初期化されていません。「状態 → GCP連携」でプロジェクトを選択するか、推論先を「gc2223.com MCP」に切り替えてください。"
                )
                return@launch
            }
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
                    persistCostEntry(
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
            if (!isFirebaseInitialized() && !RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)) {
                _aiState.value = AiGenerationState.Error(
                    "Firebase はまだオンデマンド初期化されていません。「状態 → GCP連携」でプロジェクトを選択するか、推論先を「gc2223.com MCP」に切り替えてください。"
                )
                return@launch
            }
            _aiState.value = AiGenerationState.Loading
            val prompt = buildString {
                appendLine("Design a concrete zero-cost automation solution for:")
                appendLine("Account: $userEmail")
                appendLine("Cloudflare domain: ${_credentials.value.gc2223Endpoint}")
                appendLine("GitHub repo: ${_credentials.value.githubOwnerRepo.ifBlank { "(not configured)" }}")
                appendLine("GCP project: ${_credentials.value.gcpProjectId.ifBlank { "(not configured)" }}")
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
                persistCostEntry(
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
                persistCostEntry(
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
                persistNodeStatus(
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
                val idToken = fetchFirebaseIdToken()
                val (newStatus, note) = if (creds.cloudflareApiToken.isNotBlank()) {
                    val res = bridgeClient.verifyCloudflareToken(creds.cloudflareApiToken)
                    (if (res.success) "ACTIVE" else "ERROR") to res.message
                } else {
                    val cfStatus = bridgeClient.fetchCloudflareSyncStatus(
                        apiToken = "",
                        accountId = creds.cloudflareAccountId,
                        gatewayUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = idToken
                    )
                    val st = when (cfStatus.health) {
                        com.example.model.SyncHealthState.CONNECTED -> "ACTIVE"
                        com.example.model.SyncHealthState.ERROR -> "ERROR"
                        else -> "CONFIGURED"
                    }
                    st to cfStatus.summaryMessage
                }
                persistNodeStatus(
                    nodeId = node.id,
                    status = newStatus,
                    endpointOrTarget = creds.gc2223Endpoint,
                    lastCheckedNote = note
                )
                _statusBanner.value = note
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
                val idToken = fetchFirebaseIdToken()
                var targetRepoResolved = creds.githubOwnerRepo.ifBlank { node.endpointOrTarget }
                val (ok, note) = if (dispatch) {
                    val res = bridgeClient.dispatchGitHubWorkflow(
                        pat = creds.githubPat,
                        ownerRepo = creds.githubOwnerRepo,
                        workflowFile = creds.githubWorkflowFile,
                        gatewayUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = idToken
                    )
                    res.success to res.message
                } else if (creds.githubPat.isNotBlank()) {
                    val res = bridgeClient.verifyGitHubPat(creds.githubPat)
                    res.success to res.message
                } else {
                    val ghStatus = bridgeClient.fetchGitHubSyncStatus(
                        pat = "",
                        ownerRepo = creds.githubOwnerRepo,
                        workflowFile = creds.githubWorkflowFile,
                        gatewayUrl = creds.gc2223Endpoint,
                        bearerToken = creds.gc2223BearerToken,
                        firebaseIdToken = idToken
                    )
                    if (ghStatus.targetRepo.isNotBlank()) targetRepoResolved = ghStatus.targetRepo
                    (ghStatus.health == com.example.model.SyncHealthState.CONNECTED) to ghStatus.summaryMessage
                }
                val newStatus = if (ok) "ACTIVE" else "ERROR"
                persistNodeStatus(
                    nodeId = node.id,
                    status = newStatus,
                    endpointOrTarget = targetRepoResolved,
                    lastCheckedNote = note
                )
                if (dispatch && ok) {
                    persistCostEntry(
                        provider = "GITHUB",
                        amountUsd = 0.0,
                        usageSummary = "workflow_dispatch: ${targetRepoResolved}",
                        source = "AUTO_TELEMETRY"
                    )
                }
                _statusBanner.value = note
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
                persistNodeStatus(
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
            persistCostConfig(
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
            val result = persistCostEntry(
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
            persistCostAlert(
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
        persistCostAlert(
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
            _localCostEntries.update { list -> list.filterNot { it.id == entryId } }
            val (repo, cloudUid) = cloudRepoAndUid() ?: return@launch
            repo.deleteCostEntry(cloudUid, entryId)
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
            val localBp = AgentBlueprint(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = title,
                category = category,
                summary = summary,
                content = content,
                targetHost = targetHost,
                status = status
            )
            _localBlueprints.update { listOf(localBp) + it }
            val cloud = cloudRepoAndUid()
            if (cloud == null) {
                _statusBanner.value = "ブループリント「$title」を端末内に保存しました"
                return@launch
            }
            cloud.first.saveBlueprint(
                userId = cloud.second,
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
            _localBlueprints.update { list ->
                list.map { if (it.id == blueprintId) it.copy(status = newStatus) else it }
            }
            val (repo, cloudUid) = cloudRepoAndUid() ?: run {
                _statusBanner.value = "ステータスを $newStatus に更新しました"
                return@launch
            }
            repo.updateBlueprintStatus(cloudUid, blueprintId, newStatus)
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
            _localBlueprints.update { list -> list.filterNot { it.id == blueprintId } }
            val (repo, cloudUid) = cloudRepoAndUid() ?: run {
                _statusBanner.value = "ブループリントを削除しました"
                return@launch
            }
            repo.deleteBlueprint(cloudUid, blueprintId)
                .onSuccess {
                    _statusBanner.value = "ブループリントを削除しました"
                }
                .onFailure { err ->
                    _statusBanner.value = err.localizedMessage ?: "削除に失敗しました"
                }
        }
    }

    private fun currentConnectorContext(): ConnectorContext {
        val oauthSession = InMemoryOAuthSessionStore.currentSession
        return ConnectorContext(
            googleSub = currentUserId,
            virtualAlias = userEmail,
            oauthAccessToken = _gcpWizardState.value.accessToken
                .ifBlank { oauthSession?.accessToken.orEmpty() },
            gcpProjectId = _gcpWizardState.value.selectedProjectId
                .ifBlank { _credentials.value.gcpProjectId },
            googleIdToken = oauthSession?.idToken.orEmpty()
        )
    }

    fun onGcpOAuthTokenAcquired(token: String) {
        val cleanToken = token.trim()
        if (cleanToken.isBlank()) {
            onGcpOAuthFailed("GCP OAuth Access Token が空です")
            return
        }
        InMemoryOAuthSessionStore.accessToken = cleanToken
        _gcpWizardState.update {
            it.copy(
                accessToken = cleanToken,
                oauthAuthorized = true,
                errorMessage = null,
                statusMessage = "Google OAuth を承認しました。アカウント・所属組織リソースを自動スキャンしています..."
            )
        }
        discoverAccountResources()
    }

    fun onGcpOAuthFailed(message: String) {
        _gcpWizardState.update {
            it.copy(
                isBusy = false,
                errorMessage = message
            )
        }
        _statusBanner.value = message
    }

    fun loadGcpProjectsAndBilling() {
        discoverAccountResources()
    }

    fun discoverAccountResources() {
        val token = _gcpWizardState.value.accessToken
            .ifBlank { InMemoryOAuthSessionStore.accessToken }
        if (token.isBlank()) {
            _gcpWizardState.update {
                it.copy(errorMessage = "先に Step 1 で Google OAuth (cloud-platform) を承認してください")
            }
            return
        }
        viewModelScope.launch {
            _gcpWizardState.update {
                it.copy(
                    isBusy = true,
                    oauthAuthorized = true,
                    accessToken = token,
                    errorMessage = null,
                    statusMessage = "アカウント・所属組織・GCP・Gemini モデルを自動ディスカバリー中..."
                )
            }
            try {
                val currentSelection = _gcpWizardState.value.selectedProjectId
                    .ifBlank { _credentials.value.gcpProjectId }
                val res = gcpClient.discoverAccountResources(
                    accessToken = token,
                    preferredProjectId = currentSelection,
                    apiKey = _credentials.value.gcpApiKeyOrToken
                )
                res.onFailure { err ->
                    _gcpWizardState.update {
                        it.copy(
                            isBusy = false,
                            errorMessage = err.localizedMessage ?: "アカウントリソースの自動ディスカバリーに失敗しました"
                        )
                    }
                    return@launch
                }

                val discovery = res.getOrThrow()
                val resolvedSelection = when {
                    discovery.projects.any { it.projectId == currentSelection } -> currentSelection
                    currentSelection.isNotBlank() && discovery.projects.isEmpty() -> currentSelection
                    discovery.projects.isNotEmpty() -> discovery.projects.first().projectId
                    else -> currentSelection
                }
                val detectedModel = discovery.geminiModels.firstOrNull()?.modelId
                    ?: _gcpWizardState.value.selectedGeminiModel

                _gcpWizardState.update {
                    it.copy(
                        isBusy = false,
                        organizations = discovery.organizations,
                        projects = discovery.projects,
                        billingAccounts = discovery.billingAccounts,
                        geminiModels = discovery.geminiModels,
                        selectedProjectId = resolvedSelection,
                        selectedGeminiModel = detectedModel,
                        discoveryCompleted = true,
                        discoverySummary = discovery.discoveryNote,
                        statusMessage = "自動検出完了: ${discovery.discoveryNote}",
                        errorMessage = null
                    )
                }

                if (resolvedSelection.isNotBlank()) {
                    refreshEnabledGcpApisInternal(token, resolvedSelection)
                }
                refreshAllConnectors()
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    fun selectOrganizationFilter(orgId: String) {
        val cleanOrg = orgId.trim()
        _gcpWizardState.update { state ->
            val filtered = if (cleanOrg.isBlank()) {
                state.projects
            } else {
                state.projects.filter { it.parentType == "organization" && it.parentId == cleanOrg }
            }
            val nextProj = if (filtered.isNotEmpty() && filtered.none { it.projectId == state.selectedProjectId }) {
                filtered.first().projectId
            } else {
                state.selectedProjectId
            }
            state.copy(
                selectedOrganizationId = cleanOrg,
                selectedProjectId = nextProj,
                statusMessage = if (cleanOrg.isBlank()) {
                    "全組織・個人プロジェクト (${state.projects.size}件)"
                } else {
                    "組織 $cleanOrg でフィルタ (${filtered.size}件)"
                }
            )
        }
    }

    fun selectGeminiModel(modelId: String) {
        val cleanModel = modelId.trim()
        if (cleanModel.isBlank()) return
        _gcpWizardState.update {
            it.copy(
                selectedGeminiModel = cleanModel,
                statusMessage = "使用 Gemini モデルを $cleanModel に設定しました"
            )
        }
        _statusBanner.value = "Gemini モデル設定: $cleanModel"
    }

    fun selectGcpProject(projectId: String) {
        val cleanId = projectId.trim()
        if (cleanId.isBlank()) return
        InMemoryOAuthSessionStore.selectedGcpProjectId = cleanId
        val updatedCreds = _credentials.value.copy(gcpProjectId = cleanId)
        _credentials.value = updatedCreds
        secretsStore.saveForUser(currentUserId, userEmail, updatedCreds)
        _vaultMetadata.value = secretsStore.getVaultMetadata(currentUserId, userEmail)

        _gcpWizardState.update {
            it.copy(
                selectedProjectId = cleanId,
                errorMessage = null,
                statusMessage = "選択中 GCP プロジェクト: $cleanId"
            )
        }
        val token = _gcpWizardState.value.accessToken
            .ifBlank { InMemoryOAuthSessionStore.accessToken }
        if (token.isNotBlank()) {
            viewModelScope.launch {
                refreshEnabledGcpApisInternal(token, cleanId)
                gcpClient.listAvailableGeminiModels(token, cleanId, updatedCreds.gcpApiKeyOrToken)
                    .onSuccess { models ->
                        if (models.isNotEmpty()) {
                            _gcpWizardState.update {
                                it.copy(
                                    geminiModels = models,
                                    selectedGeminiModel = models.first().modelId
                                )
                            }
                        }
                    }
            }
        }
    }

    fun refreshAllConnectors() {
        viewModelScope.launch {
            val ctx = currentConnectorContext()
            val creds = _credentials.value
            val statuses = connectorRegistry.refreshAllStatuses(ctx, creds)
            val tools = connectorRegistry.collectAllTools(ctx, creds)
            _connectorStatuses.value = statuses
            _mcpTools.value = tools
        }
    }

    fun startGitHubDeviceFlow(clientId: String) {
        val cleanClientId = clientId.trim()
        val updatedCreds = _credentials.value.copy(githubOAuthClientId = cleanClientId)
        _credentials.value = updatedCreds
        secretsStore.saveForUser(currentUserId, userEmail, updatedCreds)
        _vaultMetadata.value = secretsStore.getVaultMetadata(currentUserId, userEmail)

        viewModelScope.launch {
            _gcpWizardState.update { it.copy(isBusy = true, errorMessage = null) }
            connectorRegistry.gitHubConnector.startDeviceFlow(cleanClientId)
                .onSuccess { challenge ->
                    _gitHubDeviceChallenge.value = challenge
                    _gcpWizardState.update {
                        it.copy(
                            isBusy = false,
                            statusMessage = "GitHub コード ${challenge.userCode} を ${challenge.verificationUri} で承認してください"
                        )
                    }
                    _statusBanner.value = "GitHub デバイスコード発行: ${challenge.userCode}"
                }
                .onFailure { err ->
                    _gcpWizardState.update {
                        it.copy(isBusy = false, errorMessage = err.localizedMessage ?: "GitHub Device Flow 開始に失敗しました")
                    }
                }
        }
    }

    fun completeGitHubDeviceFlowPolling() {
        val challenge = _gitHubDeviceChallenge.value ?: return
        val clientId = _credentials.value.githubOAuthClientId.trim()
        if (clientId.isBlank()) return

        viewModelScope.launch {
            _gcpWizardState.update {
                it.copy(isBusy = true, errorMessage = null, statusMessage = "GitHub ブラウザ承認を確認中...")
            }
            try {
                connectorRegistry.gitHubConnector.pollDeviceFlowToken(
                    clientId = clientId,
                    deviceCode = challenge.deviceCode,
                    intervalSec = challenge.intervalSec,
                    maxAttempts = 12
                ).onSuccess { oauthToken ->
                    val summary = connectorRegistry.gitHubConnector
                        .fetchUserAndOrgs(oauthToken, _credentials.value.githubSelectedOrg)
                        .getOrNull()
                    val firstRepo = summary?.repos?.firstOrNull()?.fullName.orEmpty()
                    val nextCreds = _credentials.value.copy(
                        githubPat = oauthToken,
                        githubAccountLogin = summary?.login.orEmpty(),
                        githubOwnerRepo = _credentials.value.githubOwnerRepo.ifBlank { firstRepo }
                    )
                    saveCredentials(nextCreds)
                    _gitHubDeviceChallenge.value = null
                    _gitHubOrganizations.value = summary?.organizations.orEmpty()
                    _gitHubDiscoveredRepos.value = summary?.repos?.map { it.fullName }.orEmpty()
                    _gcpWizardState.update {
                        it.copy(
                            isBusy = false,
                            statusMessage = "GitHub (@${summary?.login ?: "OAuth"}) 連携完了: Org ${summary?.organizations?.size ?: 0}件 / Repo ${summary?.repos?.size ?: 0}件"
                        )
                    }
                    refreshAllConnectors()
                }.onFailure { err ->
                    _gcpWizardState.update {
                        it.copy(isBusy = false, errorMessage = err.localizedMessage ?: "GitHub 承認確認に失敗しました")
                    }
                }
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    fun selectGitHubOrganization(orgLogin: String) {
        val cleanOrg = orgLogin.trim()
        val updated = _credentials.value.copy(githubSelectedOrg = cleanOrg)
        saveCredentials(updated)
        val token = updated.githubPat.trim()
        if (token.isNotBlank()) {
            viewModelScope.launch {
                connectorRegistry.gitHubConnector.fetchUserAndOrgs(token, cleanOrg)
                    .onSuccess { summary ->
                        _gitHubOrganizations.value = summary.organizations
                        _gitHubDiscoveredRepos.value = summary.repos.map { it.fullName }
                        if (summary.repos.isNotEmpty() && updated.githubOwnerRepo.isBlank()) {
                            saveCredentials(updated.copy(githubOwnerRepo = summary.repos.first().fullName))
                        }
                        _statusBanner.value = if (cleanOrg.isBlank()) {
                            "GitHub 全リポジトリ (${summary.repos.size}件)"
                        } else {
                            "GitHub Org '$cleanOrg' (${summary.repos.size} repos)"
                        }
                    }
            }
        }
    }

    fun selectGitHubRepository(ownerRepo: String) {
        val cleanRepo = ownerRepo.trim()
        if (cleanRepo.isBlank()) return
        saveCredentials(_credentials.value.copy(githubOwnerRepo = cleanRepo))
        _statusBanner.value = "GitHub 連携リポジトリを選択: $cleanRepo"
    }

    fun addCustomMcpEndpoint(label: String, url: String, bearerToken: String = "") {
        val cleanUrl = url.trim().trimEnd('/')
        if (cleanUrl.isBlank()) return
        val cleanLabel = label.trim().ifBlank { cleanUrl.removePrefix("https://").removePrefix("http://") }
        val newEntry = com.example.model.McpEndpointEntry(
            id = "mcp_${System.currentTimeMillis()}",
            label = cleanLabel,
            url = cleanUrl,
            bearerToken = bearerToken.trim(),
            isActive = true
        )
        val updatedList = _credentials.value.mcpEndpoints
            .filterNot { it.url == cleanUrl }
            .map { it.copy(isActive = false) } + newEntry
        val updatedCreds = _credentials.value.copy(
            gc2223Endpoint = cleanUrl,
            gc2223BearerToken = bearerToken.trim().ifBlank { _credentials.value.gc2223BearerToken },
            mcpEndpoints = updatedList
        )
        saveCredentials(updatedCreds)
        refreshAllConnectors()
    }

    fun switchActiveMcpEndpoint(endpointId: String) {
        val currentList = _credentials.value.mcpEndpoints
        val target = currentList.firstOrNull { it.id == endpointId } ?: return
        val updatedList = currentList.map { it.copy(isActive = it.id == endpointId) }
        val updatedCreds = _credentials.value.copy(
            gc2223Endpoint = target.url,
            gc2223BearerToken = target.bearerToken.ifBlank { _credentials.value.gc2223BearerToken },
            mcpEndpoints = updatedList
        )
        saveCredentials(updatedCreds)
        refreshAllConnectors()
    }

    fun removeCustomMcpEndpoint(endpointId: String) {
        val currentList = _credentials.value.mcpEndpoints
        if (currentList.size <= 1) return
        val filtered = currentList.filterNot { it.id == endpointId }
        val normalized = if (filtered.none { it.isActive }) {
            filtered.mapIndexed { idx, item -> item.copy(isActive = idx == 0) }
        } else {
            filtered
        }
        val active = normalized.first { it.isActive }
        saveCredentials(
            _credentials.value.copy(
                gc2223Endpoint = active.url,
                mcpEndpoints = normalized
            )
        )
        refreshAllConnectors()
    }

    fun executeMcpTool(toolName: String, argumentsJson: JSONObject = JSONObject()) {
        viewModelScope.launch {
            _isBusyNodeAction.value = true
            try {
                val ctx = currentConnectorContext()
                val creds = _credentials.value
                val provider = when {
                    toolName.startsWith("github_") -> connectorRegistry.gitHubConnector
                    toolName.startsWith("termux_") -> connectorRegistry.termuxConnector
                    else -> connectorRegistry.remoteMcpConnector
                }
                provider.callTool(ctx, creds, toolName, argumentsJson)
                    .onSuccess { res ->
                        _lastMcpToolResult.value = res
                        _statusBanner.value = "MCP ツール '$toolName': ${if (res.ok) "成功" else "エラー"}"
                    }
                    .onFailure { err ->
                        _lastMcpToolResult.value = McpToolExecutionResult(
                            ok = false,
                            toolName = toolName,
                            output = err.localizedMessage ?: "MCP 実行失敗"
                        )
                        _statusBanner.value = err.localizedMessage ?: "MCP 実行に失敗しました"
                    }
            } finally {
                _isBusyNodeAction.value = false
            }
        }
    }

    fun createGcpProjectAndSelect(projectId: String, displayName: String) {
        val token = _gcpWizardState.value.accessToken
        if (token.isBlank()) {
            _gcpWizardState.update {
                it.copy(errorMessage = "先に Step 1 で Google OAuth (cloud-platform) を承認してください")
            }
            return
        }
        viewModelScope.launch {
            _gcpWizardState.update { it.copy(isBusy = true, errorMessage = null, statusMessage = "GCP プロジェクト作成中...") }
            try {
                val res = gcpClient.createProject(token, projectId, displayName)
                res.onSuccess { created ->
                    val updatedList = (_gcpWizardState.value.projects.filterNot { it.projectId == created.projectId } + created)
                        .sortedBy { it.projectId }
                    val updatedCreds = _credentials.value.copy(gcpProjectId = created.projectId)
                    _credentials.value = updatedCreds
                    if (_vaultMetadata.value.hasStoredSecrets) {
                        secretsStore.saveForUser(currentUserId, userEmail, updatedCreds)
                    }
                    _gcpWizardState.update {
                        it.copy(
                            isBusy = false,
                            projects = updatedList,
                            selectedProjectId = created.projectId,
                            statusMessage = "GCP プロジェクト '${created.projectId}' を作成・選択しました",
                            errorMessage = null
                        )
                    }
                    refreshEnabledGcpApisInternal(token, created.projectId)
                }.onFailure { err ->
                    _gcpWizardState.update {
                        it.copy(
                            isBusy = false,
                            errorMessage = err.localizedMessage ?: "GCP プロジェクト作成に失敗しました"
                        )
                    }
                }
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    fun linkGcpBillingAccount(billingAccountName: String) {
        val token = _gcpWizardState.value.accessToken
        val projectId = _gcpWizardState.value.selectedProjectId
        if (token.isBlank() || projectId.isBlank()) {
            _gcpWizardState.update {
                it.copy(errorMessage = "OAuth 承認とプロジェクト選択を先に完了してください")
            }
            return
        }
        viewModelScope.launch {
            _gcpWizardState.update { it.copy(isBusy = true, errorMessage = null) }
            try {
                gcpClient.linkBillingAccount(token, projectId, billingAccountName)
                    .onSuccess { linked ->
                        _gcpWizardState.update {
                            it.copy(statusMessage = "請求アカウント ($linked) を $projectId にリンクしました")
                        }
                        _statusBanner.value = "請求アカウントをリンクしました ($projectId)"
                    }
                    .onFailure { err ->
                        _gcpWizardState.update {
                            it.copy(errorMessage = err.localizedMessage ?: "請求リンクに失敗しました")
                        }
                    }
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    fun toggleGcpApiSelection(serviceName: String) {
        _gcpWizardState.update { state ->
            val next = if (state.selectedApiNamesToEnable.contains(serviceName)) {
                state.selectedApiNamesToEnable - serviceName
            } else {
                state.selectedApiNamesToEnable + serviceName
            }
            state.copy(selectedApiNamesToEnable = next)
        }
    }

    fun refreshEnabledGcpApis() {
        val token = _gcpWizardState.value.accessToken
        val projectId = _gcpWizardState.value.selectedProjectId
        if (token.isBlank() || projectId.isBlank()) return
        viewModelScope.launch {
            _gcpWizardState.update { it.copy(isBusy = true, errorMessage = null) }
            try {
                refreshEnabledGcpApisInternal(token, projectId)
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    private suspend fun refreshEnabledGcpApisInternal(token: String, projectId: String) {
        gcpClient.listEnabledServices(token, projectId)
            .onSuccess { enabledSet ->
                val updatedItems = GcpProvisioningClient.recommendedApis.map { item ->
                    item.copy(enabled = enabledSet.contains(item.serviceName))
                }
                _gcpWizardState.update {
                    it.copy(
                        apiItems = updatedItems,
                        statusMessage = "$projectId: ${updatedItems.count { s -> s.enabled }}/${updatedItems.size} 個の推奨 API が有効です"
                    )
                }
            }
            .onFailure { err ->
                _gcpWizardState.update {
                    it.copy(errorMessage = err.localizedMessage ?: "API 有効状態の取得に失敗しました")
                }
            }
    }

    fun enableSelectedGcpApis() {
        val state = _gcpWizardState.value
        val token = state.accessToken
        val projectId = state.selectedProjectId
        val selected = state.selectedApiNamesToEnable.toList()
        if (token.isBlank() || projectId.isBlank()) {
            _gcpWizardState.update {
                it.copy(errorMessage = "OAuth 承認と GCP プロジェクト選択を先に完了してください")
            }
            return
        }
        viewModelScope.launch {
            _gcpWizardState.update { it.copy(isBusy = true, errorMessage = null, statusMessage = "選択 API を一括有効化中...") }
            try {
                gcpClient.batchEnableServices(token, projectId, selected)
                    .onSuccess { enabled ->
                        refreshEnabledGcpApisInternal(token, projectId)
                        _gcpWizardState.update {
                            it.copy(
                                statusMessage = "${enabled.size} 件の API を承認・有効化しました",
                                errorMessage = null
                            )
                        }
                        _statusBanner.value = "${enabled.size} 件の GCP API を有効化しました"
                    }
                    .onFailure { err ->
                        _gcpWizardState.update {
                            it.copy(errorMessage = err.localizedMessage ?: "API 一括有効化に失敗しました")
                        }
                    }
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    fun syncFirebaseForSelectedProject() {
        val state = _gcpWizardState.value
        val token = state.accessToken
        val projectId = state.selectedProjectId
        if (token.isBlank() || projectId.isBlank()) {
            _gcpWizardState.update {
                it.copy(errorMessage = "OAuth 承認と GCP プロジェクト選択を先に完了してください")
            }
            return
        }
        viewModelScope.launch {
            _gcpWizardState.update {
                it.copy(isBusy = true, errorMessage = null, statusMessage = "Firebase 有効化・APK SHA 登録・Android 設定同期中...")
            }
            try {
                val diag = AppSignatureHelper.readDiagnostics(applicationContext)
                val creds = _credentials.value
                gcpClient.ensureFirebaseAndFetchAndroidConfig(
                    accessToken = token,
                    projectId = projectId,
                    packageName = applicationContext.packageName,
                    existingGatewayUrl = creds.gc2223Endpoint,
                    sha1 = diag.sha1,
                    sha256 = diag.sha256
                ).onSuccess { syncRes ->
                    val bootstrapStore = RuntimeCloudBootstrapStore(applicationContext)
                    val mergedBootstrap = syncRes.bootstrapConfig.copy(
                        defaultGithubRepo = creds.githubOwnerRepo.ifBlank { syncRes.bootstrapConfig.defaultGithubRepo }
                    )
                    bootstrapStore.save(mergedBootstrap)
                    val updatedCreds = creds.copy(gcpProjectId = syncRes.projectId)
                    saveCredentials(updatedCreds)

                    // On-demand initialization of Firebase, App Check & Firestore after user selects GCP project
                    val fbInitialized = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)
                    if (fbInitialized) {
                        configureAppCheck(applicationContext, null)
                        val fbAuth = runCatching { Firebase.auth }.getOrNull()
                        val sessionIdToken = InMemoryOAuthSessionStore.currentSession?.idToken.orEmpty()
                        if (fbAuth != null && fbAuth.currentUser == null && sessionIdToken.isNotBlank()) {
                            runCatching {
                                fbAuth.signInWithCredential(GoogleAuthProvider.getCredential(sessionIdToken, null)).await()
                            }.onFailure { Log.w(TAG, "Optional Firebase Auth sign-in skipped", it) }
                        }
                        if (fbAuth?.currentUser != null) {
                            runCatching {
                                val dbId = RuntimeCloudBootstrapStore.resolveDatabaseId(applicationContext)
                                val db = FirebaseFirestore.getInstance(dbId)
                                attachCloudRepository(FirestoreRepository(db, fbAuth))
                            }.onFailure { Log.w(TAG, "Optional Firestore attach skipped", it) }
                        }
                        onFirebaseProvisioned()
                    }

                    _gcpWizardState.update {
                        it.copy(
                            isBusy = false,
                            firebaseSynced = true,
                            lastFirebaseAppId = syncRes.androidAppId,
                            registeredShaCount = syncRes.registeredShaCount,
                            signingDiagnostics = diag,
                            statusMessage = "Firebase (${syncRes.androidAppId}) と App Check をオンデマンド初期化し、署名 SHA (${syncRes.registeredShaCount}件) を同期しました",
                            errorMessage = null
                        )
                    }
                    _statusBanner.value = "Firebase / Firestore / App Check をオンデマンド初期化しました"
                }.onFailure { err ->
                    _gcpWizardState.update {
                        it.copy(
                            isBusy = false,
                            errorMessage = err.localizedMessage ?: "Firebase 同期に失敗しました"
                        )
                    }
                }
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    fun exportPortableBootstrapJson(): String {
        val bootstrap = RuntimeCloudBootstrapStore(applicationContext).load()
        val creds = _credentials.value
        val merged = bootstrap.copy(
            gatewayUrl = creds.gc2223Endpoint.ifBlank { bootstrap.gatewayUrl },
            firebaseProjectId = creds.gcpProjectId.ifBlank { bootstrap.firebaseProjectId },
            defaultGithubRepo = creds.githubOwnerRepo.ifBlank { bootstrap.defaultGithubRepo }
        )
        return RuntimeCloudBootstrapStore.exportPortableJson(merged)
    }

    fun applyWizardToWorkflowNodesAndSync(
        gatewayUrl: String,
        githubOwnerRepo: String,
        termuxAgentUrl: String
    ) {
        val wiz = _gcpWizardState.value
        val currentCreds = _credentials.value
        val effectiveProject = wiz.selectedProjectId.ifBlank { currentCreds.gcpProjectId }
        val updatedCreds = currentCreds.copy(
            gcpProjectId = effectiveProject,
            gc2223Endpoint = gatewayUrl.trim().trimEnd('/').ifBlank { currentCreds.gc2223Endpoint },
            githubOwnerRepo = githubOwnerRepo.trim().ifBlank { currentCreds.githubOwnerRepo },
            termuxAgentUrl = termuxAgentUrl.trim().trimEnd('/').ifBlank { currentCreds.termuxAgentUrl }
        )
        saveCredentials(updatedCreds)

        val bootstrapStore = RuntimeCloudBootstrapStore(applicationContext)
        val existingBootstrap = bootstrapStore.load()
        bootstrapStore.save(
            existingBootstrap.copy(
                gatewayUrl = updatedCreds.gc2223Endpoint,
                firebaseProjectId = effectiveProject.ifBlank { existingBootstrap.firebaseProjectId },
                defaultGithubRepo = updatedCreds.githubOwnerRepo
            )
        )

        viewModelScope.launch {
            _gcpWizardState.update { it.copy(isBusy = true, errorMessage = null) }
            try {
                val idToken = fetchFirebaseIdToken()
                val currentNodes = (nodesState.value as? StreamUiState.Success)?.data.orEmpty()
                val enabledCount = wiz.apiItems.count { it.enabled }

                for (node in currentNodes) {
                    when (node.provider) {
                        "GCP" -> {
                            val st = if (wiz.oauthAuthorized && effectiveProject.isNotBlank()) "ACTIVE" else "CONFIGURED"
                            val note = if (wiz.oauthAuthorized) {
                                "Project: $effectiveProject (有効API ${enabledCount}/${wiz.apiItems.size})"
                            } else {
                                "Project: $effectiveProject"
                            }
                            persistNodeStatus(node.id, st, effectiveProject, note)
                        }
                        "FIREBASE" -> {
                            val st = if (wiz.firebaseSynced) "ACTIVE" else node.status
                            val note = if (wiz.firebaseSynced) {
                                "App: ${wiz.lastFirebaseAppId} / SHA同期済み (${wiz.registeredShaCount}件)"
                            } else {
                                node.lastCheckedNote
                            }
                            persistNodeStatus(node.id, st, effectiveProject, note)
                        }
                        "CLOUDFLARE" -> {
                            val gwRes = bridgeClient.pingGc2223Gateway(
                                baseUrl = updatedCreds.gc2223Endpoint,
                                bearerToken = updatedCreds.gc2223BearerToken,
                                firebaseIdToken = idToken
                            )
                            persistNodeStatus(
                                node.id,
                                if (gwRes.success) "ACTIVE" else "CONFIGURED",
                                updatedCreds.gc2223Endpoint,
                                gwRes.message
                            )
                        }
                        "GITHUB" -> {
                            val ghStatus = bridgeClient.fetchGitHubSyncStatus(
                                pat = updatedCreds.githubPat,
                                ownerRepo = updatedCreds.githubOwnerRepo,
                                workflowFile = updatedCreds.githubWorkflowFile,
                                gatewayUrl = updatedCreds.gc2223Endpoint,
                                bearerToken = updatedCreds.gc2223BearerToken,
                                firebaseIdToken = idToken
                            )
                            val st = when (ghStatus.health) {
                                com.example.model.SyncHealthState.CONNECTED -> "ACTIVE"
                                com.example.model.SyncHealthState.ERROR -> "ERROR"
                                else -> "CONFIGURED"
                            }
                            persistNodeStatus(
                                node.id,
                                st,
                                updatedCreds.githubOwnerRepo.ifBlank { node.endpointOrTarget },
                                ghStatus.summaryMessage
                            )
                        }
                        "TERMUX" -> {
                            val tmStatus = bridgeClient.fetchTermuxAgentStatus(
                                updatedCreds.termuxAgentUrl,
                                updatedCreds.termuxAgentToken
                            )
                            val st = if (tmStatus.health == com.example.model.SyncHealthState.CONNECTED) "ACTIVE" else "CONFIGURED"
                            persistNodeStatus(
                                node.id,
                                st,
                                updatedCreds.termuxAgentUrl,
                                tmStatus.summaryMessage
                            )
                        }
                    }
                }

                triggerBackgroundSyncNow()
                triggerTermuxSyncNow()
                _gcpWizardState.update {
                    it.copy(
                        isBusy = false,
                        statusMessage = "全ワークフローノード (GCP / Firebase / Cloudflare / GitHub / Termux) へ反映・同期しました",
                        errorMessage = null
                    )
                }
                _statusBanner.value = "全ワークフロー連携を反映・同期しました"
            } finally {
                _gcpWizardState.update { it.copy(isBusy = false) }
            }
        }
    }

    private companion object {
        fun buildDefaultLocalNodes(userId: String, accountHint: String): List<WorkflowNode> {
            val hint = accountHint.ifBlank { "google-oauth" }.take(120)
            return listOf(
                WorkflowNode(
                    id = "cloudflare_gc2223",
                    userId = userId,
                    provider = "CLOUDFLARE",
                    title = "gc2223.com Stateless MCP Gateway",
                    endpointOrTarget = "https://gc2223.com",
                    accountHint = hint,
                    status = "CONFIGURED",
                    monthlyFreeQuota = "Workers 100k req/day + Workers AI Free",
                    lastCheckedNote = "Stateless MCP mode ready (zero PII on edge)"
                ),
                WorkflowNode(
                    id = "termux_antigravity",
                    userId = userId,
                    provider = "TERMUX",
                    title = "Termux AntiGravityAgent CLI",
                    endpointOrTarget = "http://127.0.0.1:8787",
                    accountHint = "Android Termux Shell",
                    status = "CONFIGURED",
                    monthlyFreeQuota = "Local CLI (0 cost)",
                    lastCheckedNote = "Local shell & MCP bridge ready"
                ),
                WorkflowNode(
                    id = "github_actions",
                    userId = userId,
                    provider = "GITHUB",
                    title = "GitHub Actions CI/CD Runner",
                    endpointOrTarget = "workflow_dispatch -> deploy-gc2223.yml",
                    accountHint = hint,
                    status = "CONFIGURED",
                    monthlyFreeQuota = "Public Free / Private 2,000 min/mo",
                    lastCheckedNote = "Set GitHub PAT & repo in local AES vault"
                ),
                WorkflowNode(
                    id = "gcp_free_tier",
                    userId = userId,
                    provider = "GCP",
                    title = "GCP Always Free + Budget Guard",
                    endpointOrTarget = "(Select in GCP Wizard)",
                    accountHint = hint,
                    status = "CONFIGURED",
                    monthlyFreeQuota = "Always Free + $0.10/day Guard",
                    lastCheckedNote = "Auto-discover projects via Google OAuth"
                ),
                WorkflowNode(
                    id = "firebase_ai_logic",
                    userId = userId,
                    provider = "FIREBASE",
                    title = "Firebase AI Logic + Firestore (On-Demand)",
                    endpointOrTarget = "gemini-3.8-flash",
                    accountHint = hint,
                    status = "CONFIGURED",
                    monthlyFreeQuota = "Spark Free / On-Demand Init",
                    lastCheckedNote = "Initialized when GCP project is linked"
                )
            )
        }
    }
}
