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
import com.example.auth.resolveDefaultWebClientId
import com.example.auth.validateWebClientId
import com.example.data.CloudBridgeClient
import com.example.data.RuntimeCloudBootstrapConfig
import com.example.data.RuntimeCloudBootstrapStore
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

data class AuthenticationUiState(
    val isLoading: Boolean = false,
    val isFirebaseReady: Boolean = false,
    val configuredWebClientId: String? = null,
    val isWebClientIdValid: Boolean = false,
    val webClientIdStatusLabel: String = "検証中...",
    val gatewayUrl: String = "https://gc2223.com",
    val errorMessage: String? = null,
    val showBootstrapDialog: Boolean = false,
    val bootstrapNotice: String? = null,
    val pendingSignInAfterBootstrap: Boolean = false,
    val authenticatedUid: String? = null,
    val authenticatedEmail: String? = null
)

class AuthenticationViewModel(
    appContext: Context,
    private val bootstrapStore: RuntimeCloudBootstrapStore = RuntimeCloudBootstrapStore(appContext.applicationContext),
    private val bridgeClient: CloudBridgeClient = CloudBridgeClient()
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
                "Web Client ID 検証済み (${validatedClientId.take(14)}...)"
            firebaseReady && validatedClientId == null ->
                "Web Client ID 未検証 (*.apps.googleusercontent.com が必要)"
            else ->
                "ゼロ埋め込みモード (ログイン時に自動取得または google-services.json 読込)"
        }

        _uiState.update { state ->
            state.copy(
                isFirebaseReady = firebaseReady,
                configuredWebClientId = validatedClientId,
                isWebClientIdValid = validatedClientId != null,
                webClientIdStatusLabel = statusLabel,
                gatewayUrl = bootstrap.gatewayUrl.ifBlank { "https://gc2223.com" },
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
                // Silently probe Gateway /v1/client-config without surfacing startup error banners
                val targetGateway = current.gatewayUrl.ifBlank { "https://gc2223.com" }
                bridgeClient.fetchClientBootstrapConfig(targetGateway).onSuccess { cfg ->
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
        onAuthSuccess: () -> Unit
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

        // Zero-config runtime APK: attempt Gateway /v1/client-config first, then open bootstrap sheet if needed
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val targetGateway = _uiState.value.gatewayUrl.ifBlank { "https://gc2223.com" }
            val cfgResult = bridgeClient.fetchClientBootstrapConfig(targetGateway)
            cfgResult.onSuccess { cfg ->
                bootstrapStore.save(cfg.copy(gatewayUrl = targetGateway.trimEnd('/')))
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
                            bootstrapNotice = "Gateway から一部項目を取得しましたが有効な Web Client ID (*.apps.googleusercontent.com) が不足しています。google-services.json を選択または入力してください。",
                            pendingSignInAfterBootstrap = true,
                            showBootstrapDialog = true
                        )
                    }
                }
            }.onFailure { err ->
                val rawMsg = err.localizedMessage.orEmpty()
                val notice = if (rawMsg.contains("404")) {
                    "この APK は秘密情報未埋め込みビルドで、Gateway ($targetGateway/v1/client-config) もまだ更新前 (404) です。端末内の google-services.json を選択するとすぐ Google ログインを開始できます。"
                } else {
                    "Gateway ($targetGateway) から自動設定を取得できなかったため、端末内の google-services.json を選択するか接続先を指定してください。"
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        bootstrapNotice = notice,
                        pendingSignInAfterBootstrap = true,
                        showBootstrapDialog = true
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
                    errorMessage = "設定されている Web Client ID が無効です: $validatedClientId"
                )
            }
            return
        }

        if (activity == null) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "Google Sign-In には Activity コンテキストが必要です。")
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
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    onAuthSuccess()
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "想定外の認証情報タイプが返されました。")
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Google Sign-In cancelled or dismissed: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Google ログインに失敗しました"
                    )
                }
            }
        }
    }

    fun openBootstrapDialog() {
        _uiState.update {
            it.copy(
                bootstrapNotice = null,
                pendingSignInAfterBootstrap = false,
                showBootstrapDialog = true
            )
        }
    }

    fun dismissBootstrapDialog() {
        _uiState.update {
            it.copy(
                showBootstrapDialog = false,
                pendingSignInAfterBootstrap = false
            )
        }
    }

    fun onBootstrapCompleted(
        updatedConfig: RuntimeCloudBootstrapConfig,
        activity: Activity?,
        credentialManager: CredentialManager,
        onFirebaseInitialized: () -> Unit,
        onAuthSuccess: () -> Unit
    ) {
        val shouldContinueSignIn = _uiState.value.pendingSignInAfterBootstrap
        _uiState.update {
            it.copy(
                gatewayUrl = updatedConfig.gatewayUrl.ifBlank { it.gatewayUrl },
                showBootstrapDialog = false,
                pendingSignInAfterBootstrap = false
            )
        }
        onFirebaseInitialized()
        refreshConfigurationState()

        val validClientId = _uiState.value.configuredWebClientId
        if (shouldContinueSignIn && _uiState.value.isFirebaseReady && validClientId != null) {
            executeCredentialManagerSignIn(
                activity = activity,
                credentialManager = credentialManager,
                validatedClientId = validClientId,
                onAuthSuccess = onAuthSuccess
            )
        } else if (shouldContinueSignIn) {
            _uiState.update {
                it.copy(errorMessage = "有効な Web Client ID が未設定のため、ログインを開始できませんでした。")
            }
        }
    }
}
