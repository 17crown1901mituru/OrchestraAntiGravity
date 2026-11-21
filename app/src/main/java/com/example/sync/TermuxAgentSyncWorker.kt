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
import com.example.data.SyncSnapshotStore
import com.example.model.SyncHealthState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "TermuxAgentSyncWorker"

class TermuxAgentSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val currentFirebaseUser = runCatching { Firebase.auth.currentUser }.getOrNull()
        val uid = inputData.getString(KEY_USER_ID).orEmpty()
            .ifBlank { currentFirebaseUser?.uid.orEmpty() }
        val triggerSource = inputData.getString(KEY_TRIGGER_SOURCE).orEmpty().ifBlank { "PERIODIC" }

        if (uid.isBlank()) {
            Log.d(TAG, "Skipping Termux status pull because no user UID is available")
            return@withContext Result.success()
        }

        val secretsStore = LocalSecretsStore(applicationContext)
        val snapshotStore = SyncSnapshotStore(applicationContext)
        val bridgeClient = CloudBridgeClient()

        val credentials = secretsStore.loadForUser(uid)

        try {
            val termuxStatus = bridgeClient.fetchTermuxAgentStatus(
                agentUrl = credentials.termuxAgentUrl,
                bearerToken = credentials.termuxAgentToken
            )
            val syncedAt = System.currentTimeMillis()
            snapshotStore.saveTermuxSnapshot(
                uid = uid,
                termux = termuxStatus,
                syncedAtEpochMs = syncedAt,
                triggerSource = triggerSource
            )

            // Keep Firestore's termux_antigravity node in sync when signed into the matching session
            if (currentFirebaseUser != null && currentFirebaseUser.uid == uid) {
                runCatching {
                    val databaseId = com.example.data.RuntimeCloudBootstrapStore.resolveDatabaseId(applicationContext)
                    val db = FirebaseFirestore.getInstance(databaseId)
                    val repo = FirestoreRepository(db, Firebase.auth)
                    val nodeStatus = when (termuxStatus.health) {
                        SyncHealthState.CONNECTED -> "ACTIVE"
                        SyncHealthState.ERROR -> "ERROR"
                        else -> "CONFIGURED"
                    }
                    repo.updateNodeStatus(
                        userId = uid,
                        nodeId = "termux_antigravity",
                        status = nodeStatus,
                        endpointOrTarget = "~/.local/bin/antigravity -> ${termuxStatus.endpointUrl}",
                        lastCheckedNote = termuxStatus.summaryMessage
                    )
                }.onFailure { err ->
                    Log.w(TAG, "Optional Firestore Termux node update skipped", err)
                }
            }

            Result.success(
                workDataOf(
                    KEY_TERMUX_SYNCED_AT to syncedAt,
                    KEY_TERMUX_HEALTH to termuxStatus.health.name
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Termux status pull failed unexpectedly", e)
            Result.retry()
        }
    }

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_TRIGGER_SOURCE = "trigger_source"
        const val KEY_TERMUX_SYNCED_AT = "termux_synced_at"
        const val KEY_TERMUX_HEALTH = "termux_health"
    }
}
