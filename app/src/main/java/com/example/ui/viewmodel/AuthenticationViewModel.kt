package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.InMemoryOAuthSessionStore
import com.example.auth.resolveDefaultWebClientId
import com.example.auth.validateWebClientId
import com.example.data.CloudBridgeClient
import com.example.data.GcpProjectSummary
import com.example.data.GcpProvisioningClient
import com.example.data.RuntimeCloudBootstrapStore
import com.example.security.AppSignatureHelper
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthenticationViewModel"

enum class LoginStage {
    GOOGLE_OAUTH,
    SELECT_GCP_PROJECT
}

data class AuthenticationUiState(
    val loginStage: LoginStage = LoginStage.GOOGLE_OAUTH,
    val isLoading: Boolean = false,
    val isFirebaseReady: Boolean = false,
    val configuredWebClientId: String? = null,
    val isWebClientIdValid: Boolean = false,
    val webClientIdStatusLabel: String = "検証中...",
    val gatewayUrl: String = "https://gc2223.com",
    val oauthAccessToken: String = "",
    val oauthUserEmail: String = "",
    val gcpProjects: List<GcpProjectSummary> = emptyList(),
    val selectedGcpProjectId: String = "",
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val authenticatedUid: String? = null,
    val authenticatedEmail: String? = null
)

class AuthenticationViewModel(
    appContext: Context,
    private val bootstrapStore: RuntimeCloudBootstrapStore = RuntimeCloudBootstrapStore(appContext.applicationContext),
    private val bridgeClient: CloudBridgeClient = CloudBridgeClient(),
    private val gcpClient: GcpProvisioningClient = GcpProvisioningClient()
) : ViewModel() {

    private val applicationContext = appContext.applicationContext
    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    private var initialSilentCheckStarted = false

    init {
        refreshConfigurationState()
    }

    fun refreshConfigurationState(explicitAuth: FirebaseAuth? = null) {
        val bootstrap = bootstrapStore.load()
        val firebaseReady = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext) ||
            FirebaseApp.getApps(applicationContext).isNotEmpty()
        val rawResolvedId = resolveDefaultWebClientId(applicationContext)
        val validatedClientId = validateWebClientId(rawResolvedId)
        val authInstance = explicitAuth ?: if (firebaseReady) runCatching { Firebase.auth }.getOrNull() else null
        val user = authInstance?.currentUser

        val statusLabel = when {
            firebaseReady && validatedClientId != null ->
                "Firebase & Web Client ID 準備完了 (${validatedClientId.take(14)}...)"
            firebaseReady && validatedClientId == null ->
                "Web Client ID 未取得 (Stage 2 の GCP 選択で自動取得)"
            else ->
                "ゼロ埋め込みモード (1. Google OAuth → 2. GCP プロジェクト選択で自動構成)"
        }

        _uiState.update { state ->
            val sessionToken = InMemoryOAuthSessionStore.accessToken
            val effectiveToken = state.oauthAccessToken.ifBlank { sessionToken }
            val effectiveEmail = state.oauthUserEmail.ifBlank { InMemoryOAuthSessionStore.userEmail }
            val effectiveProject = state.selectedGcpProjectId.ifBlank {
                InMemoryOAuthSessionStore.selectedGcpProjectId.ifBlank { bootstrap.firebaseProjectId }
            }
            state.copy(
                isFirebaseReady = firebaseReady,
                configuredWebClientId = validatedClientId,
                isWebClientIdValid = validatedClientId != null,
                webClientIdStatusLabel = statusLabel,
                gatewayUrl = bootstrap.gatewayUrl.ifBlank { "https://gc2223.com" },
                oauthAccessToken = effectiveToken,
                oauthUserEmail = effectiveEmail,
                selectedGcpProjectId = effectiveProject,
                authenticatedUid = user?.uid,
                authenticatedEmail = user?.email
            )
        }
    }

    fun startStartupChecks(
        credentialManager: CredentialManager,
        onFirebaseInitialized: () -> Unit,
        onAuthSuccess: () -> Unit
    ) {
        if (initialSilentCheckStarted) return
        initialSilentCheckStarted = true

        viewModelScope.launch {
            delay(120L)
            refreshConfigurationState()
            val current = _uiState.value
            if (current.isFirebaseReady && current.isWebClientIdValid) {
                attemptSilentSignInInternal(credentialManager, onAuthSuccess)
            } else {
                // Silently probe Gateway /v1/client-config; if unconfigured/404, stay cleanly at Stage 1 with Firebase uninitialized
                val targetGateway = current.gatewayUrl.ifBlank { "https://gc2223.com" }
                bridgeClient.fetchClientBootstrapConfig(targetGateway).onSuccess { cfg ->
                    if (cfg.hasFirebaseRuntimeParams()) {
                        bootstrapStore.save(cfg.copy(gatewayUrl = targetGateway.trimEnd('/')))
                        if (RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)) {
                            onFirebaseInitialized()
                            refreshConfigurationState()
                            if (_uiState.value.isWebClientIdValid) {
                                attemptSilentSignInInternal(credentialManager, onAuthSuccess)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun attemptSilentSignInInternal(
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit
    ) {
        val auth = runCatching { Firebase.auth }.getOrNull() ?: return
        if (auth.currentUser != null) {
            refreshConfigurationState(auth)
            onAuthSuccess()
            return
        }
        val clientId = validateWebClientId(resolveDefaultWebClientId(applicationContext)) ?: return

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val cred = credentialManager.getCredential(applicationContext, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val idToken = GoogleIdTokenCredential.createFrom(cred.data).idToken
                auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
                refreshConfigurationState(auth)
                onAuthSuccess()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Normal on clean installs with no prior authorized credential
        }
    }

    fun onGoogleSignInClicked(
        activity: Activity?,
        credentialManager: CredentialManager,
        onFirebaseInitialized: () -> Unit,
        onAuthSuccess: () -> Unit,
        onLaunchStage1OAuth: () -> Unit
    ) {
        if (_uiState.value.isLoading) return
        refreshConfigurationState()
        val current = _uiState.value

        if (current.isFirebaseReady && current.isWebClientIdValid && current.configuredWebClientId != null) {
            executeCredentialManagerSignIn(
                activity = activity,
                credentialManager = credentialManager,
                validatedClientId = current.configuredWebClientId,
                onAuthSuccess = onAuthSuccess
            )
            return
        }

        // Clean runtime APK: probe Gateway /v1/client-config quietly; otherwise proceed straight to Stage 1 Google OAuth (never prompt for .json)
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                statusMessage = "接続設定を確認中..."
            )
        }
        viewModelScope.launch {
            val targetGateway = _uiState.value.gatewayUrl.ifBlank { "https://gc2223.com" }
            val cfgResult = bridgeClient.fetchClientBootstrapConfig(targetGateway)
            val resolvedCfg = cfgResult.getOrNull()
            if (resolvedCfg != null && resolvedCfg.hasFirebaseRuntimeParams()) {
                bootstrapStore.save(resolvedCfg.copy(gatewayUrl = targetGateway.trimEnd('/')))
                val initialized = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)
                refreshConfigurationState()
                val validId = _uiState.value.configuredWebClientId
                if (initialized && validId != null) {
                    onFirebaseInitialized()
                    executeCredentialManagerSignIn(
                        activity = activity,
                        credentialManager = credentialManager,
                        validatedClientId = validId,
                        onAuthSuccess = onAuthSuccess
                    )
                    return@launch
                }
            }

            // Transition immediately into Stage 1 Google OAuth authorization
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = null,
                    statusMessage = "Google アカウントの OAuth 認証を開始しています..."
                )
            }
            onLaunchStage1OAuth()
        }
    }

    fun onStage1OAuthTokenAcquired(accessToken: String) {
        val cleanToken = accessToken.trim()
        if (cleanToken.isBlank()) {
            onStage1OAuthFailed("Google OAuth Access Token が空でした")
            return
        }
        InMemoryOAuthSessionStore.accessToken = cleanToken
        _uiState.update {
            it.copy(
                isLoading = true,
                oauthAccessToken = cleanToken,
                errorMessage = null,
                statusMessage = "Google アカウント情報と GCP プロジェクト一覧を取得中..."
            )
        }
        viewModelScope.launch {
            val userInfoRes = gcpClient.fetchGoogleUserInfo(cleanToken)
            val resolvedEmail = userInfoRes.getOrNull()?.email.orEmpty()
            if (resolvedEmail.isNotBlank()) {
                InMemoryOAuthSessionStore.userEmail = resolvedEmail
            }

            val projectsRes = gcpClient.listProjects(cleanToken)
            val projects = projectsRes.getOrDefault(emptyList())
            val previousSelected = _uiState.value.selectedGcpProjectId.ifBlank {
                InMemoryOAuthSessionStore.selectedGcpProjectId
            }
            val selectedId = when {
                projects.any { it.projectId == previousSelected } -> previousSelected
                projects.isNotEmpty() -> projects.first().projectId
                else -> previousSelected
            }
            if (selectedId.isNotBlank()) {
                InMemoryOAuthSessionStore.selectedGcpProjectId = selectedId
            }

            val errMessage = projectsRes.exceptionOrNull()?.localizedMessage
            _uiState.update {
                it.copy(
                    loginStage = LoginStage.SELECT_GCP_PROJECT,
                    isLoading = false,
                    oauthAccessToken = cleanToken,
                    oauthUserEmail = resolvedEmail.ifBlank { it.oauthUserEmail },
                    gcpProjects = projects,
                    selectedGcpProjectId = selectedId,
                    statusMessage = if (projectsRes.isSuccess) {
                        "Google 認証完了 (${projects.size} 件の GCP プロジェクトを検出)"
                    } else null,
                    errorMessage = errMessage
                )
            }
        }
    }

    fun onStage1OAuthFailed(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                statusMessage = null,
                errorMessage = message
            )
        }
    }

    fun backToStage1OAuth() {
        _uiState.update {
            it.copy(
                loginStage = LoginStage.GOOGLE_OAUTH,
                isLoading = false,
                statusMessage = null,
                errorMessage = null
            )
        }
    }

    fun refreshStage2Projects() {
        val token = _uiState.value.oauthAccessToken
        if (token.isBlank()) {
            onStage1OAuthFailed("先に Google OAuth 認証を完了してください")
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, statusMessage = "GCP プロジェクト一覧を再取得中...") }
        viewModelScope.launch {
            val res = gcpClient.listProjects(token)
            res.onSuccess { projects ->
                val current = _uiState.value.selectedGcpProjectId
                val resolved = when {
                    projects.any { it.projectId == current } -> current
                    projects.isNotEmpty() -> projects.first().projectId
                    else -> current
                }
                if (resolved.isNotBlank()) InMemoryOAuthSessionStore.selectedGcpProjectId = resolved
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        gcpProjects = projects,
                        selectedGcpProjectId = resolved,
                        statusMessage = "${projects.size} 件の GCP プロジェクトを取得しました",
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        errorMessage = err.localizedMessage ?: "GCP プロジェクト一覧の再取得に失敗しました"
                    )
                }
            }
        }
    }

    fun selectStage2Project(projectId: String) {
        val cleanId = projectId.trim()
        if (cleanId.isBlank()) return
        InMemoryOAuthSessionStore.selectedGcpProjectId = cleanId
        _uiState.update {
            it.copy(
                selectedGcpProjectId = cleanId,
                errorMessage = null,
                statusMessage = "選択中プロジェクト: $cleanId"
            )
        }
    }

    fun createStage2ProjectAndSelect(projectId: String, displayName: String) {
        val token = _uiState.value.oauthAccessToken
        if (token.isBlank()) {
            onStage1OAuthFailed("先に Google OAuth 認証を完了してください")
            return
        }
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                statusMessage = "新規 GCP プロジェクト '${projectId.trim().lowercase()}' を作成中..."
            )
        }
        viewModelScope.launch {
            gcpClient.createProject(token, projectId, displayName)
                .onSuccess { created ->
                    InMemoryOAuthSessionStore.selectedGcpProjectId = created.projectId
                    val updatedList = (_uiState.value.gcpProjects.filterNot { it.projectId == created.projectId } + created)
                        .sortedBy { it.projectId }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            gcpProjects = updatedList,
                            selectedGcpProjectId = created.projectId,
                            statusMessage = "GCP プロジェクト '${created.projectId}' を作成・選択しました",
                            errorMessage = null
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = null,
                            errorMessage = err.localizedMessage ?: "GCP プロジェクトの作成に失敗しました"
                        )
                    }
                }
        }
    }

    fun provisionSelectedProjectAndSignIn(
        activity: Activity?,
        credentialManager: CredentialManager,
        onFirebaseInitialized: () -> Unit,
        onAuthSuccess: () -> Unit
    ) {
        val current = _uiState.value
        val token = current.oauthAccessToken.trim()
        val projectId = current.selectedGcpProjectId.trim()
        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Google OAuth トークンがありません。Stage 1 からやり直してください。") }
            return
        }
        if (projectId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "GCP プロジェクトを選択または作成してください。") }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                statusMessage = "必須 Google Cloud API (Firebase / Firestore / Auth) を承認・有効化中..."
            )
        }

        viewModelScope.launch {
            val coreServices = listOf(
                "firebase.googleapis.com",
                "firestore.googleapis.com",
                "identitytoolkit.googleapis.com",
                "firebaseappcheck.googleapis.com",
                "cloudresourcemanager.googleapis.com",
                "serviceusage.googleapis.com"
            )
            gcpClient.batchEnableServices(token, projectId, coreServices)
                .onFailure { err ->
                    Log.w(TAG, "Best-effort batchEnableServices notice for $projectId: ${err.message}")
                }

            _uiState.update {
                it.copy(statusMessage = "Firebase Management API から設定・署名 SHA・Web Client ID を自動取得中...")
            }

            val diag = runCatching { AppSignatureHelper.readDiagnostics(applicationContext) }.getOrNull()
            val syncResult = gcpClient.ensureFirebaseAndFetchAndroidConfig(
                accessToken = token,
                projectId = projectId,
                packageName = applicationContext.packageName,
                existingGatewayUrl = current.gatewayUrl,
                sha1 = diag?.sha1.orEmpty(),
                sha256 = diag?.sha256.orEmpty()
            )

            syncResult.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        errorMessage = err.localizedMessage ?: "Firebase Management API からの設定取得に失敗しました"
                    )
                }
                return@launch
            }

            val synced = syncResult.getOrThrow()
            val existingCfg = bootstrapStore.load()
            val mergedCfg = synced.bootstrapConfig.copy(
                gatewayUrl = current.gatewayUrl.trim().trimEnd('/').ifBlank { existingCfg.gatewayUrl },
                defaultGithubRepo = existingCfg.defaultGithubRepo.ifBlank { synced.bootstrapConfig.defaultGithubRepo }
            )
            bootstrapStore.save(mergedCfg)

            InMemoryOAuthSessionStore.accessToken = token
            InMemoryOAuthSessionStore.selectedGcpProjectId = projectId
            if (current.oauthUserEmail.isNotBlank()) {
                InMemoryOAuthSessionStore.userEmail = current.oauthUserEmail
            }

            val initialized = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)
            if (initialized) {
                onFirebaseInitialized()
            }
            refreshConfigurationState()

            val validClientId = _uiState.value.configuredWebClientId
            if (initialized && validClientId != null) {
                _uiState.update {
                    it.copy(
                        statusMessage = "Firebase 初期化完了 (${synced.androidAppId})。Google アカウントで認証を確定中..."
                    )
                }
                executeCredentialManagerSignIn(
                    activity = activity,
                    credentialManager = credentialManager,
                    validatedClientId = validClientId,
                    onAuthSuccess = onAuthSuccess
                )
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        errorMessage = "Firebase 設定は保存されましたが Web Client ID (*.apps.googleusercontent.com) が未反映です。Google Auth プロバイダまたは OAuth 同意画面を確認してもう一度お試しください。"
                    )
                }
            }
        }
    }

    fun updateGatewayUrl(url: String) {
        val clean = url.trim().trimEnd('/')
        val currentCfg = bootstrapStore.load()
        bootstrapStore.save(currentCfg.copy(gatewayUrl = clean))
        _uiState.update { it.copy(gatewayUrl = clean.ifBlank { "https://gc2223.com" }) }
    }

    fun connectGatewayFromAdvancedOptions(
        gatewayUrl: String,
        activity: Activity?,
        credentialManager: CredentialManager,
        onFirebaseInitialized: () -> Unit,
        onAuthSuccess: () -> Unit
    ) {
        val target = gatewayUrl.trim().trimEnd('/').ifBlank { "https://gc2223.com" }
        updateGatewayUrl(target)
        _uiState.update { it.copy(isLoading = true, errorMessage = null, statusMessage = "$target/v1/client-config を取得中...") }
        viewModelScope.launch {
            bridgeClient.fetchClientBootstrapConfig(target).onSuccess { cfg ->
                if (!cfg.hasFirebaseRuntimeParams()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = null,
                            errorMessage = "Gateway に有効な Firebase 設定が返却されませんでした"
                        )
                    }
                    return@onSuccess
                }
                bootstrapStore.save(cfg.copy(gatewayUrl = target))
                val initialized = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)
                refreshConfigurationState()
                val validId = _uiState.value.configuredWebClientId
                if (initialized && validId != null) {
                    onFirebaseInitialized()
                    executeCredentialManagerSignIn(
                        activity = activity,
                        credentialManager = credentialManager,
                        validatedClientId = validId,
                        onAuthSuccess = onAuthSuccess
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = null,
                            errorMessage = "Web Client ID (*.apps.googleusercontent.com) が Gateway 応答に含まれていません"
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        errorMessage = err.localizedMessage ?: "Gateway からの接続設定取得に失敗しました"
                    )
                }
            }
        }
    }

    private fun executeCredentialManagerSignIn(
        activity: Activity?,
        credentialManager: CredentialManager,
        validatedClientId: String,
        onAuthSuccess: () -> Unit
    ) {
        if (validateWebClientId(validatedClientId) == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    statusMessage = null,
                    errorMessage = "設定されている Web Client ID が無効です: $validatedClientId"
                )
            }
            return
        }

        if (activity == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    statusMessage = null,
                    errorMessage = "Google Sign-In には Activity コンテキストが必要です。"
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = validatedClientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        viewModelScope.launch {
            try {
                val cred = credentialManager.getCredential(activity, request).credential
                if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                    val auth = runCatching { Firebase.auth }.getOrNull()
                        ?: throw IllegalStateException("FirebaseAuth is not initialized.")
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                    refreshConfigurationState(auth)
                    _uiState.update { it.copy(isLoading = false, errorMessage = null, statusMessage = null) }
                    onAuthSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = null,
                            errorMessage = "想定外の認証情報タイプが返されました。"
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Google Sign-In cancelled or dismissed: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, statusMessage = null) }
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = null,
                        errorMessage = e.localizedMessage ?: "Google ログインに失敗しました"
                    )
                }
            }
        }
    }
}
