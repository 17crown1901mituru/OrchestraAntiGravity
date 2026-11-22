package com.example.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.BuildConfig
import com.example.data.RuntimeCloudBootstrapStore
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.MessageDigest

private const val TAG = "GoogleAuthHelper"
const val GCP_CLOUD_PLATFORM_SCOPE = "https://www.googleapis.com/auth/cloud-platform"
private const val SCOPE_OPENID = "openid"
private const val SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email"
private const val SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile"

data class GoogleAccountSession(
    val sub: String,
    val virtualAlias: String,
    val accessToken: String = "",
    val email: String = "",
    val idToken: String = "",
    val isStatelessLocal: Boolean = false
) {
    val isValid: Boolean
        get() = sub.isNotBlank() && (isStatelessLocal || accessToken.isNotBlank() || idToken.isNotBlank())

    companion object {
        fun buildVirtualAlias(sub: String): String {
            val hexPrefix = sha256Hex(sub.trim()).take(12)
            return "u-$hexPrefix@gc2223.com"
        }

        fun deriveFallbackSub(seed: String): String {
            return "oauth-${sha256Hex(seed.trim()).take(20)}"
        }

        fun create(
            sub: String,
            accessToken: String = "",
            email: String = "",
            idToken: String = ""
        ): GoogleAccountSession {
            val cleanSub = sub.trim()
            return GoogleAccountSession(
                sub = cleanSub,
                virtualAlias = buildVirtualAlias(cleanSub),
                accessToken = accessToken.trim(),
                email = email.trim(),
                idToken = idToken.trim()
            )
        }

        private fun sha256Hex(input: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
                .digest(input.toByteArray(Charsets.UTF_8))
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}

object InMemoryOAuthSessionStore {
    private val _sessionFlow = MutableStateFlow<GoogleAccountSession?>(null)
    val sessionFlow: StateFlow<GoogleAccountSession?> = _sessionFlow.asStateFlow()

    val currentSession: GoogleAccountSession?
        get() = _sessionFlow.value

    @Volatile
    var accessToken: String = ""
    @Volatile
    var userEmail: String = ""
    @Volatile
    var selectedGcpProjectId: String = ""

    fun setSession(session: GoogleAccountSession) {
        _sessionFlow.value = session
        if (session.accessToken.isNotBlank()) {
            accessToken = session.accessToken
        }
        userEmail = session.email.ifBlank { session.virtualAlias }
    }

    fun clear() {
        _sessionFlow.value = null
        accessToken = ""
        userEmail = ""
        selectedGcpProjectId = ""
    }
}

internal fun extractGoogleJwtClaims(idToken: String): Pair<String, String>? {
    return runCatching {
        val parts = idToken.trim().split('.')
        if (parts.size < 2) return@runCatching null
        val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val json = JSONObject(String(payloadBytes, Charsets.UTF_8))
        val sub = json.optString("sub", "").trim()
        val email = json.optString("email", "").trim()
        if (sub.isBlank()) null else (sub to email)
    }.getOrNull()
}

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    val existingSession = InMemoryOAuthSessionStore.currentSession
    if (existingSession?.isValid == true) {
        onAuthSuccess()
        return
    }
    val firebaseReady = FirebaseApp.getApps(context).isNotEmpty()
    val auth = if (firebaseReady) runCatching { Firebase.auth }.getOrNull() else null
    if (auth?.currentUser != null) {
        val fbUser = auth.currentUser!!
        InMemoryOAuthSessionStore.setSession(
            GoogleAccountSession.create(
                sub = fbUser.uid,
                accessToken = InMemoryOAuthSessionStore.accessToken,
                email = fbUser.email.orEmpty()
            )
        )
        onAuthSuccess()
        return
    }
    val clientId = resolveDefaultWebClientId(context)
    if (clientId == null) {
        onUnauthenticated()
        return
    }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val cred = credentialManager.getCredential(context, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val idToken = GoogleIdTokenCredential.createFrom(cred.data).idToken
                val claims = extractGoogleJwtClaims(idToken)
                if (auth != null) {
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
                }
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
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = resolveDefaultWebClientId(context)
    if (clientId == null) {
        onAuthError("有効な Web Client ID が未設定です。Google OAuth フローでログインしてください。")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    scope.launch {
        try {
            val activity = context as? Activity
                ?: throw IllegalStateException("Google Sign-In requires an Activity context.")
            val cred = credentialManager.getCredential(activity, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val idToken = GoogleIdTokenCredential.createFrom(cred.data).idToken
                val claims = extractGoogleJwtClaims(idToken)
                val auth = if (FirebaseApp.getApps(context).isNotEmpty()) {
                    runCatching { Firebase.auth }.getOrNull()
                } else null
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
                onAuthSuccess()
            } else {
                onAuthError("Unexpected credential type received.")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In cancelled or dismissed: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            onAuthError(e.localizedMessage ?: "Sign in failed")
        }
    }
}

fun signOutUser(
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit = {}
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(TAG, "Failed to clear credential state: ${e.message}")
            } finally {
                InMemoryOAuthSessionStore.clear()
                runCatching { Firebase.auth.signOut() }
                onSignOutComplete()
            }
        }
    }
}

fun requestGcpOAuthAuthorization(
    activity: Activity,
    includeUserInfoScopes: Boolean = false,
    onAccessToken: (String) -> Unit,
    onResolutionRequired: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    val scopes = buildList {
        add(Scope(GCP_CLOUD_PLATFORM_SCOPE))
        if (includeUserInfoScopes) {
            add(Scope(SCOPE_OPENID))
            add(Scope(SCOPE_EMAIL))
            add(Scope(SCOPE_PROFILE))
        }
    }
    val authRequest = AuthorizationRequest.builder()
        .setRequestedScopes(scopes)
        .build()

    Identity.getAuthorizationClient(activity)
        .authorize(authRequest)
        .addOnSuccessListener { result ->
            if (result.hasResolution()) {
                val pendingIntent = result.pendingIntent
                if (pendingIntent != null) {
                    onResolutionRequired(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
                } else {
                    onError("Google OAuth 承認画面を起動できませんでした (PendingIntent null)")
                }
            } else {
                val token = result.accessToken
                if (!token.isNullOrBlank()) {
                    onAccessToken(token)
                } else {
                    onError("GCP OAuth Access Token を取得できませんでした")
                }
            }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "GCP OAuth authorization request failed", e)
            onError(e.localizedMessage ?: "GCP OAuth 承認に失敗しました")
        }
}

fun extractGcpAccessTokenFromIntent(context: Context, data: Intent?): Result<String> = runCatching {
    val result = Identity.getAuthorizationClient(context).getAuthorizationResultFromIntent(data)
    val token = result.accessToken
    require(!token.isNullOrBlank()) { "承認結果に GCP Access Token が含まれていません" }
    token
}

internal fun validateWebClientId(rawClientId: String?): String? {
    val candidate = rawClientId?.trim().orEmpty()
    if (candidate.isBlank()) return null
    if (!candidate.endsWith(".apps.googleusercontent.com")) return null
    val prefix = candidate.removeSuffix(".apps.googleusercontent.com")
    if (prefix.length < 6 || prefix.contains("YOUR_", ignoreCase = true) || prefix.contains("PLACEHOLDER", ignoreCase = true)) {
        return null
    }
    return candidate
}

internal fun resolveDefaultWebClientId(context: Context): String? {
    val runtimeClientId = runCatching {
        validateWebClientId(RuntimeCloudBootstrapStore(context).load().firebaseWebClientId)
    }.getOrNull()
    if (runtimeClientId != null) return runtimeClientId

    val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    val fromGoogleServices = if (resId != 0) {
        runCatching { validateWebClientId(context.getString(resId)) }.getOrNull()
    } else {
        null
    }
    if (fromGoogleServices != null) return fromGoogleServices

    return validateWebClientId(BuildConfig.DEFAULT_WEB_CLIENT_ID)
}
