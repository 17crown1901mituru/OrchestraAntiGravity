package com.example.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "GoogleAuthHelper"
const val GCP_CLOUD_PLATFORM_SCOPE = "https://www.googleapis.com/auth/cloud-platform"

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    val auth = runCatching { Firebase.auth }.getOrNull()
    if (auth == null) {
        onUnauthenticated()
        return
    }
    if (auth.currentUser != null) {
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
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
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
        onAuthError("有効な Web Client ID (default_web_client_id) が設定されていません。接続設定または google-services.json を確認してください。")
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
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                val auth = runCatching { Firebase.auth }.getOrNull()
                    ?: throw IllegalStateException("FirebaseAuth is not initialized.")
                auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
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
                runCatching { Firebase.auth.signOut() }
                onSignOutComplete()
            }
        }
    }
}

fun requestGcpOAuthAuthorization(
    activity: Activity,
    onAccessToken: (String) -> Unit,
    onResolutionRequired: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    val authRequest = AuthorizationRequest.builder()
        .setRequestedScopes(listOf(Scope(GCP_CLOUD_PLATFORM_SCOPE)))
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
