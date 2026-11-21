package com.example.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.R
import com.example.data.CloudBridgeClient
import com.example.data.FirestoreRepository
import com.example.data.LocalSecretsStore
import com.example.data.OrchestrationHardeningClient
import com.example.data.SyncSnapshotStore
import com.example.model.SyncHealthState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "CloudStatusSyncWorker"

class CloudStatusSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val currentFirebaseUser = runCatching { Firebase.auth.currentUser }.getOrNull()
        val uid = inputData.getString(KEY_USER_ID).orEmpty()
            .ifBlank { currentFirebaseUser?.uid.orEmpty() }
        val triggerSource = inputData.getString(KEY_TRIGGER_SOURCE).orEmpty().ifBlank { "PERIODIC" }

        if (uid.isBlank()) {
            Log.d(TAG, "Skipping background sync because no user UID is available")
            return@withContext Result.success()
        }

        val secretsStore = LocalSecretsStore(applicationContext)
        val snapshotStore = SyncSnapshotStore(applicationContext)
        val bridgeClient = CloudBridgeClient()

        val credentials = secretsStore.loadForUser(uid)
        val idToken = if (currentFirebaseUser != null && currentFirebaseUser.uid == uid) {
            try {
                currentFirebaseUser.getIdToken(false).await()?.token
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Unable to fetch Firebase ID token during sync", e)
                null
            }
        } else {
            null
        }

        try {
            val (githubStatus, cloudflareStatus) = coroutineScope {
                val ghDeferred = async {
                    bridgeClient.fetchGitHubSyncStatus(
                        pat = credentials.githubPat,
                        ownerRepo = credentials.githubOwnerRepo,
                        workflowFile = credentials.githubWorkflowFile
                    )
                }
                val cfDeferred = async {
                    bridgeClient.fetchCloudflareSyncStatus(
                        apiToken = credentials.cloudflareApiToken,
                        accountId = credentials.cloudflareAccountId,
                        gatewayUrl = credentials.gc2223Endpoint,
                        bearerToken = credentials.gc2223BearerToken,
                        firebaseIdToken = idToken
                    )
                }
                ghDeferred.await() to cfDeferred.await()
            }

            val syncedAt = System.currentTimeMillis()
            snapshotStore.saveCloudSnapshot(
                uid = uid,
                github = githubStatus,
                cloudflare = cloudflareStatus,
                syncedAtEpochMs = syncedAt,
                triggerSource = triggerSource
            )

            // Sync node status into Firestore when signed into the matching Firebase Auth session
            if (currentFirebaseUser != null && currentFirebaseUser.uid == uid) {
                runCatching {
                    val databaseId = applicationContext.getString(R.string.firestore_database_id)
                    val db = FirebaseFirestore.getInstance(databaseId)
                    val repo = FirestoreRepository(db, Firebase.auth)

                    val ghNodeStatus = when (githubStatus.health) {
                        SyncHealthState.CONNECTED -> "ACTIVE"
                        SyncHealthState.ERROR -> "ERROR"
                        else -> "CONFIGURED"
                    }
                    repo.updateNodeStatus(
                        userId = uid,
                        nodeId = "github_actions",
                        status = ghNodeStatus,
                        endpointOrTarget = githubStatus.targetRepo.ifBlank { credentials.githubOwnerRepo.ifBlank { "github" } },
                        lastCheckedNote = githubStatus.summaryMessage
                    )

                    val cfNodeStatus = when (cloudflareStatus.health) {
                        SyncHealthState.CONNECTED -> "ACTIVE"
                        SyncHealthState.ERROR -> "ERROR"
                        else -> "CONFIGURED"
                    }
                    repo.updateNodeStatus(
                        userId = uid,
                        nodeId = "cloudflare_gc2223",
                        status = cfNodeStatus,
                        endpointOrTarget = cloudflareStatus.gatewayUrl,
                        lastCheckedNote = cloudflareStatus.summaryMessage
                    )
                }.onFailure { err ->
                    Log.w(TAG, "Optional Firestore node status sync skipped", err)
                }
            }

            // Best-effort check for SAFE_MODE / gateway errors to trigger local emergency notification
            val hardening = if (credentials.gc2223Endpoint.isNotBlank()) {
                runCatching {
                    OrchestrationHardeningClient().fetchHardeningSnapshot(
                        baseUrl = credentials.gc2223Endpoint,
                        bearerToken = credentials.gc2223BearerToken,
                        firebaseIdToken = idToken
                    )
                }.getOrNull()
            } else null

            if (hardening?.safeMode == true) {
                UrgentAlertNotifier.notifyUrgentAlert(
                    context = applicationContext,
                    title = "AntiGravity SAFE_MODE 作動中",
                    body = hardening.safeReason.ifBlank { "サーキットブレーカーにより自律タスクが一時停止されました。" },
                    notificationId = 9001
                )
            } else if (cloudflareStatus.health == SyncHealthState.ERROR) {
                UrgentAlertNotifier.notifyUrgentAlert(
                    context = applicationContext,
                    title = "AntiGravity Gateway 障害検知",
                    body = cloudflareStatus.summaryMessage.ifBlank { "gc2223.com ゲートウェイへの疎通に失敗しました。" },
                    notificationId = 9002
                )
            }

            Result.success(
                workDataOf(
                    KEY_SYNCED_AT to syncedAt,
                    KEY_GITHUB_HEALTH to githubStatus.health.name,
                    KEY_CLOUDFLARE_HEALTH to cloudflareStatus.health.name
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Background status sync failed unexpectedly", e)
            Result.retry()
        }
    }

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_TRIGGER_SOURCE = "trigger_source"
        const val KEY_SYNCED_AT = "synced_at"
        const val KEY_GITHUB_HEALTH = "github_health"
        const val KEY_CLOUDFLARE_HEALTH = "cloudflare_health"
    }
}
