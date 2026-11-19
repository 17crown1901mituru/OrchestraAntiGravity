package com.example.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit

data class SyncWorkUiState(
    val isSyncingNow: Boolean = false,
    val isTermuxSyncingNow: Boolean = false,
    val periodicScheduled: Boolean = false,
    val periodicStateLabel: String = "未設定",
    val nextRunEpochMs: Long = 0L
)

object BackgroundSyncScheduler {
    private const val PERIODIC_WORK_PREFIX = "antigravity_periodic_sync_"
    private const val IMMEDIATE_WORK_PREFIX = "antigravity_immediate_sync_"
    private const val TERMUX_PERIODIC_WORK_PREFIX = "antigravity_termux_periodic_sync_"
    private const val TERMUX_IMMEDIATE_WORK_PREFIX = "antigravity_termux_immediate_sync_"

    fun periodicWorkName(uid: String) = "$PERIODIC_WORK_PREFIX$uid"
    fun immediateWorkName(uid: String) = "$IMMEDIATE_WORK_PREFIX$uid"
    fun termuxPeriodicWorkName(uid: String) = "$TERMUX_PERIODIC_WORK_PREFIX$uid"
    fun termuxImmediateWorkName(uid: String) = "$TERMUX_IMMEDIATE_WORK_PREFIX$uid"

    fun ensurePeriodicSyncScheduled(
        context: Context,
        uid: String,
        intervalMinutes: Long,
        replaceExisting: Boolean = false
    ) {
        if (uid.isBlank()) return
        val wm = WorkManager.getInstance(context.applicationContext)
        val safeInterval = intervalMinutes.coerceAtLeast(15L)

        val cloudConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val cloudRequest = PeriodicWorkRequestBuilder<CloudStatusSyncWorker>(
            safeInterval,
            TimeUnit.MINUTES
        )
            .setConstraints(cloudConstraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInputData(
                workDataOf(
                    CloudStatusSyncWorker.KEY_USER_ID to uid,
                    CloudStatusSyncWorker.KEY_TRIGGER_SOURCE to "PERIODIC"
                )
            )
            .addTag(userTag(uid))
            .build()

        // Local Termux loopback polling does not require external internet connectivity
        val termuxRequest = PeriodicWorkRequestBuilder<TermuxAgentSyncWorker>(
            safeInterval,
            TimeUnit.MINUTES
        )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInputData(
                workDataOf(
                    TermuxAgentSyncWorker.KEY_USER_ID to uid,
                    TermuxAgentSyncWorker.KEY_TRIGGER_SOURCE to "PERIODIC"
                )
            )
            .addTag(userTag(uid))
            .build()

        val policy = if (replaceExisting) {
            ExistingPeriodicWorkPolicy.UPDATE
        } else {
            ExistingPeriodicWorkPolicy.KEEP
        }

        wm.enqueueUniquePeriodicWork(periodicWorkName(uid), policy, cloudRequest)
        wm.enqueueUniquePeriodicWork(termuxPeriodicWorkName(uid), policy, termuxRequest)
    }

    fun triggerImmediateSync(context: Context, uid: String) {
        if (uid.isBlank()) return
        val wm = WorkManager.getInstance(context.applicationContext)
        val cloudConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val cloudRequest = OneTimeWorkRequestBuilder<CloudStatusSyncWorker>()
            .setConstraints(cloudConstraints)
            .setInputData(
                workDataOf(
                    CloudStatusSyncWorker.KEY_USER_ID to uid,
                    CloudStatusSyncWorker.KEY_TRIGGER_SOURCE to "MANUAL"
                )
            )
            .addTag(userTag(uid))
            .build()

        wm.enqueueUniqueWork(immediateWorkName(uid), ExistingWorkPolicy.REPLACE, cloudRequest)
        triggerImmediateTermuxSync(context, uid)
    }

    fun triggerImmediateTermuxSync(context: Context, uid: String) {
        if (uid.isBlank()) return
        val wm = WorkManager.getInstance(context.applicationContext)
        val termuxRequest = OneTimeWorkRequestBuilder<TermuxAgentSyncWorker>()
            .setInputData(
                workDataOf(
                    TermuxAgentSyncWorker.KEY_USER_ID to uid,
                    TermuxAgentSyncWorker.KEY_TRIGGER_SOURCE to "MANUAL"
                )
            )
            .addTag(userTag(uid))
            .build()

        wm.enqueueUniqueWork(termuxImmediateWorkName(uid), ExistingWorkPolicy.REPLACE, termuxRequest)
    }

    fun cancelUserSync(context: Context, uid: String) {
        if (uid.isBlank()) return
        val wm = WorkManager.getInstance(context.applicationContext)
        wm.cancelUniqueWork(periodicWorkName(uid))
        wm.cancelUniqueWork(immediateWorkName(uid))
        wm.cancelUniqueWork(termuxPeriodicWorkName(uid))
        wm.cancelUniqueWork(termuxImmediateWorkName(uid))
    }

    fun observeWorkState(context: Context, uid: String): Flow<SyncWorkUiState> {
        val wm = WorkManager.getInstance(context.applicationContext)
        val cloudPeriodicFlow = wm.getWorkInfosForUniqueWorkFlow(periodicWorkName(uid))
        val cloudImmediateFlow = wm.getWorkInfosForUniqueWorkFlow(immediateWorkName(uid))
        val termuxPeriodicFlow = wm.getWorkInfosForUniqueWorkFlow(termuxPeriodicWorkName(uid))
        val termuxImmediateFlow = wm.getWorkInfosForUniqueWorkFlow(termuxImmediateWorkName(uid))

        return combine(
            cloudPeriodicFlow,
            cloudImmediateFlow,
            termuxPeriodicFlow,
            termuxImmediateFlow
        ) { cloudPeriodicList, cloudImmediateList, termuxPeriodicList, termuxImmediateList ->
            val cloudPeriodic = cloudPeriodicList.firstOrNull()
            val cloudImmediate = cloudImmediateList.firstOrNull()
            val termuxPeriodic = termuxPeriodicList.firstOrNull()
            val termuxImmediate = termuxImmediateList.firstOrNull()

            val isTermuxRunning = termuxImmediate?.state == WorkInfo.State.RUNNING ||
                termuxImmediate?.state == WorkInfo.State.ENQUEUED ||
                termuxPeriodic?.state == WorkInfo.State.RUNNING

            val isRunningNow = cloudImmediate?.state == WorkInfo.State.RUNNING ||
                cloudImmediate?.state == WorkInfo.State.ENQUEUED ||
                cloudPeriodic?.state == WorkInfo.State.RUNNING ||
                isTermuxRunning

            val scheduled = (cloudPeriodic != null &&
                cloudPeriodic.state != WorkInfo.State.CANCELLED &&
                cloudPeriodic.state != WorkInfo.State.FAILED) ||
                (termuxPeriodic != null &&
                    termuxPeriodic.state != WorkInfo.State.CANCELLED &&
                    termuxPeriodic.state != WorkInfo.State.FAILED)

            val label = when {
                cloudImmediate?.state == WorkInfo.State.RUNNING || termuxImmediate?.state == WorkInfo.State.RUNNING ->
                    "今すぐ同期中..."
                cloudImmediate?.state == WorkInfo.State.ENQUEUED || termuxImmediate?.state == WorkInfo.State.ENQUEUED ->
                    "同期キュー待機中..."
                cloudPeriodic?.state == WorkInfo.State.RUNNING || termuxPeriodic?.state == WorkInfo.State.RUNNING ->
                    "定期同期実行中..."
                cloudPeriodic?.state == WorkInfo.State.ENQUEUED || termuxPeriodic?.state == WorkInfo.State.ENQUEUED ->
                    "定期同期待機中"
                cloudPeriodic?.state == WorkInfo.State.BLOCKED ->
                    "ネットワーク接続待ち"
                cloudPeriodic?.state == WorkInfo.State.CANCELLED && termuxPeriodic?.state == WorkInfo.State.CANCELLED ->
                    "自動同期オフ"
                else -> "未スケジュール"
            }

            SyncWorkUiState(
                isSyncingNow = isRunningNow,
                isTermuxSyncingNow = isTermuxRunning,
                periodicScheduled = scheduled,
                periodicStateLabel = label,
                nextRunEpochMs = cloudPeriodic?.nextScheduleTimeMillis?.takeIf { it > 0L && it != Long.MAX_VALUE }
                    ?: termuxPeriodic?.nextScheduleTimeMillis?.takeIf { it > 0L && it != Long.MAX_VALUE }
                    ?: 0L
            )
        }
    }

    private fun userTag(uid: String) = "sync_user_$uid"
}
