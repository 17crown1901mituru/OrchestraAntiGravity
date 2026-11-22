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
import com.example.auth.GoogleAccountSession
import com.example.auth.InMemoryOAuthSessionStore
import com.example.auth.extractGoogleJwtClaims
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
        val firebaseReady = FirebaseApp.getApps(applicationContext).isNotEmpty() ||
            bootstrap.hasFirebaseRuntimeParams()
        val rawResolvedId = resolveDefaultWebClientId(applicationContext)
        val validatedClientId = validateWebClientId(rawResolvedId)
        val authInstance = explicitAuth ?: if (FirebaseApp.getApps(applicationContext).isNotEmpty()) {
            runCatching { Firebase.auth }.getOrNull()
        } else null
        val user = authInstance?.currentUser
        val session = InMemoryOAuthSessionStore.currentSession

        val statusLabel = when {
            session != null ->
                "Google OAuth 認証済み (${session.virtualAlias})"
            firebaseReady && validatedClientId != null ->
                "Credential Manager + Firebase 準備完了 (${validatedClientId.take(14)}...)"
            validatedClientId != null ->
                "Credential Manager 利用可能 / Firebase はログイン後にオンデマンド初期化"
            else ->
                "ゼロ設定モード (Google OAuth ログイン後に GCP・MCP 連携を自動検出)"
        }

        _uiState.update { state ->
            val sessionToken = InMemoryOAuthSessionStore.accessToken
            val effectiveToken = state.oauthAccessToken.ifBlank { sessionToken }
            val effectiveEmail = state.oauthUserEmail.ifBlank { InMemoryOAuthSessionStore.userEmail }
            val effectiveProject = state.selectedGcpProjectId.ifBlank {
                InMemoryOAuthSessionStore.selectedGcpProjectId.ifBlank { bootstrap.firebaseProjectId }
            }
            state.copy(
                isFirebaseReady = FirebaseApp.getApps(applicationContext).isNotEmpty(),
                configuredWebClientId = validatedClientId,
                isWebClientIdValid = validatedClientId != null,
                webClientIdStatusLabel = statusLabel,
                gatewayUrl = bootstrap.gatewayUrl.ifBlank { "https://gc2223.com" },
                oauthAccessToken = effectiveToken,
                oauthUserEmail = effectiveEmail,
                selectedGcpProjectId = effectiveProject,
                authenticatedUid = session?.sub ?: user?.uid,
                authenticatedEmail = session?.virtualAlias ?: user?.email
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
            if (InMemoryOAuthSessionStore.currentSession?.isValid == true) {
                onAuthSuccess()
                return@launch
            }
            val current = _uiState.value
            if (current.isWebClientIdValid) {
                attemptSilentSignInInternal(credentialManager, onAuthSuccess)
            }
        }
    }

    private suspend fun attemptSilentSignInInternal(
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit
    ) {
        val auth = if (FirebaseApp.getApps(applicationContext).isNotEmpty()) {
            runCatching { Firebase.auth }.getOrNull()
        } else null
        if (auth?.currentUser != null) {
            val fbUser = auth.currentUser!!
            InMemoryOAuthSessionStore.setSession(
                GoogleAccountSession.create(
                    sub = fbUser.uid,
                    accessToken = InMemoryOAuthSessionStore.accessToken,
                    email = fbUser.email.orEmpty()
                )
            )
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
                val claims = extractGoogleJwtClaims(idToken)
                auth?.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))?.await()
                val sub = claims?.first ?: auth?.currentUser?.uid ?: GoogleAccountSession.deriveFallbackSub(idToken)
                val email = claims?.second.orEmpty().ifBlank { auth?.currentUser?.email.orEmpty() }
                InMemoryOAuthSessionStore.setSession(
                    GoogleAccountSession.create(
                        sub = sub,
                        accessToken = InMemoryOAuthSessionStore.accessToken,
                        email = email,
                        idToken = idToken
                    )
                )
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

        // If a valid Web Client ID is already configured on-device, try Credential Manager first, else launch pure Google OAuth
        if (current.isWebClientIdValid && current.configuredWebClientId != null) {
            executeCredentialManagerSignIn(
                activity = activity,
                credentialManager = credentialManager,
                validatedClientId = current.configuredWebClientId,
                onAuthSuccess = onAuthSuccess
            )
            return
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null,
                statusMessage = "Google OAuth 認証を開始しています..."
            )
        }
        onLaunchStage1OAuth()
    }

    fun onStage1OAuthTokenAcquired(
        accessToken: String,
        onAuthSuccess: (() -> Unit)? = null
    ) {
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
                statusMessage = "Google アカウント識別子 (sub) を取得しています..."
            )
        }
        viewModelScope.launch {
            val userInfo = gcpClient.fetchGoogleUserInfo(cleanToken).getOrNull()
            val resolvedEmail = userInfo?.email.orEmpty()
            val resolvedSub = userInfo?.sub?.takeIf { it.isNotBlank() }
                ?: GoogleAccountSession.deriveFallbackSub(resolvedEmail.ifBlank { cleanToken })

            val session = GoogleAccountSession.create(
                sub = resolvedSub,
                accessToken = cleanToken,
                email = resolvedEmail
            )
            InMemoryOAuthSessionStore.setSession(session)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    oauthAccessToken = cleanToken,
                    oauthUserEmail = session.virtualAlias,
                    authenticatedUid = session.sub,
                    authenticatedEmail = session.virtualAlias,
                    statusMessage = "Google OAuth ログイン完了 (${session.virtualAlias})",
                    errorMessage = null
                )
            }
            onAuthSuccess?.invoke()
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
                bootstrapStore.save(cfg.copy(gatewayUrl = target))
                val initialized = if (cfg.hasFirebaseRuntimeParams()) {
                    RuntimeCloudBootstrapStore.ensureFirebaseInitialized(applicationContext)
                } else false
                if (initialized) onFirebaseInitialized()
                refreshConfigurationState()
                val validId = _uiState.value.configuredWebClientId
                if (validId != null) {
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
                            errorMessage = "Gateway に有効な Web Client ID がありません。上の Google OAuth ボタンからログインしてください。"
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
                    val claims = extractGoogleJwtClaims(token)
                    val auth = if (FirebaseApp.getApps(applicationContext).isNotEmpty()) {
                        runCatching { Firebase.auth }.getOrNull()
                    } else null
                    auth?.signInWithCredential(GoogleAuthProvider.getCredential(token, null))?.await()

                    val sub = claims?.first ?: auth?.currentUser?.uid ?: GoogleAccountSession.deriveFallbackSub(token)
                    val email = claims?.second.orEmpty().ifBlank { auth?.currentUser?.email.orEmpty() }
                    InMemoryOAuthSessionStore.setSession(
                        GoogleAccountSession.create(
                            sub = sub,
                            accessToken = InMemoryOAuthSessionStore.accessToken,
                            email = email,
                            idToken = token
                        )
                    )
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
