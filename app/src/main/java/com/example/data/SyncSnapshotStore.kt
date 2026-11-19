package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BackgroundSyncSettings
import com.example.model.CloudflareSyncStatus
import com.example.model.CloudflareWorkerSummary
import com.example.model.GitHubActivityEvent
import com.example.model.GitHubCommitSummary
import com.example.model.GitHubRepoSummary
import com.example.model.GitHubSyncStatus
import com.example.model.SyncHealthState
import com.example.model.SyncStatusSnapshot
import com.example.model.TermuxAgentSyncStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import org.json.JSONArray
import org.json.JSONObject

class SyncSnapshotStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun observeSnapshot(uid: String): Flow<SyncStatusSnapshot> = callbackFlow {
        val watchedKeys = setOf(
            cloudKey(uid),
            termuxKey(uid)
        )
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == null || changedKey in watchedKeys) {
                trySend(loadSnapshot(uid))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.onStart {
        emit(loadSnapshot(uid))
    }

    fun observeSettings(uid: String): Flow<BackgroundSyncSettings> = callbackFlow {
        val enabledK = enabledKey(uid)
        val intervalK = intervalKey(uid)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == null || changedKey == enabledK || changedKey == intervalK) {
                trySend(loadSettings(uid))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.onStart {
        emit(loadSettings(uid))
    }

    fun saveSettings(uid: String, settings: BackgroundSyncSettings) {
        if (uid.isBlank()) return
        prefs.edit()
            .putBoolean(enabledKey(uid), settings.enabled)
            .putLong(intervalKey(uid), settings.intervalMinutes.coerceAtLeast(15L))
            .apply()
    }

    fun loadSettings(uid: String): BackgroundSyncSettings {
        if (uid.isBlank()) return BackgroundSyncSettings()
        return BackgroundSyncSettings(
            enabled = prefs.getBoolean(enabledKey(uid), true),
            intervalMinutes = prefs.getLong(intervalKey(uid), 15L).coerceAtLeast(15L)
        )
    }

    fun saveCloudSnapshot(
        uid: String,
        github: GitHubSyncStatus,
        cloudflare: CloudflareSyncStatus,
        syncedAtEpochMs: Long,
        triggerSource: String
    ) {
        if (uid.isBlank()) return
        val json = serializeCloud(uid, github, cloudflare, syncedAtEpochMs, triggerSource)
        prefs.edit().putString(cloudKey(uid), json.toString()).apply()
    }

    fun saveTermuxSnapshot(
        uid: String,
        termux: TermuxAgentSyncStatus,
        syncedAtEpochMs: Long,
        triggerSource: String
    ) {
        if (uid.isBlank()) return
        val json = serializeTermux(termux, syncedAtEpochMs, triggerSource)
        prefs.edit().putString(termuxKey(uid), json.toString()).apply()
    }

    fun saveSnapshot(uid: String, snapshot: SyncStatusSnapshot) {
        if (uid.isBlank()) return
        prefs.edit()
            .putString(
                cloudKey(uid),
                serializeCloud(
                    uid = uid,
                    github = snapshot.github,
                    cloudflare = snapshot.cloudflare,
                    syncedAtEpochMs = snapshot.syncedAtEpochMs,
                    triggerSource = snapshot.triggerSource
                ).toString()
            )
            .putString(
                termuxKey(uid),
                serializeTermux(
                    termux = snapshot.termux,
                    syncedAtEpochMs = snapshot.termuxSyncedAtEpochMs,
                    triggerSource = snapshot.termuxTriggerSource
                ).toString()
            )
            .apply()
    }

    fun loadSnapshot(uid: String): SyncStatusSnapshot {
        if (uid.isBlank()) return SyncStatusSnapshot()
        val cloudRaw = prefs.getString(cloudKey(uid), null)
        val termuxRaw = prefs.getString(termuxKey(uid), null)

        var syncedAtEpochMs = 0L
        var triggerSource = ""
        var github = GitHubSyncStatus()
        var cloudflare = CloudflareSyncStatus()

        if (!cloudRaw.isNullOrBlank()) {
            runCatching {
                val root = JSONObject(cloudRaw)
                syncedAtEpochMs = root.optLong("syncedAtEpochMs", 0L)
                triggerSource = root.optString("triggerSource")
                github = deserializeGitHub(root.optJSONObject("github") ?: JSONObject())
                cloudflare = deserializeCloudflare(root.optJSONObject("cloudflare") ?: JSONObject())
            }
        }

        var termuxSyncedAt = 0L
        var termuxTrigger = ""
        var termux = TermuxAgentSyncStatus()
        if (!termuxRaw.isNullOrBlank()) {
            runCatching {
                val tRoot = JSONObject(termuxRaw)
                termuxSyncedAt = tRoot.optLong("termuxSyncedAtEpochMs", 0L)
                termuxTrigger = tRoot.optString("termuxTriggerSource")
                termux = deserializeTermux(tRoot.optJSONObject("termux") ?: JSONObject())
            }
        }

        return SyncStatusSnapshot(
            userId = uid,
            syncedAtEpochMs = syncedAtEpochMs,
            github = github,
            cloudflare = cloudflare,
            triggerSource = triggerSource,
            termuxSyncedAtEpochMs = termuxSyncedAt,
            termux = termux,
            termuxTriggerSource = termuxTrigger
        )
    }

    fun clearForUser(uid: String) {
        if (uid.isBlank()) return
        prefs.edit()
            .remove(cloudKey(uid))
            .remove(termuxKey(uid))
            .remove(enabledKey(uid))
            .remove(intervalKey(uid))
            .apply()
    }

    private fun serializeCloud(
        uid: String,
        github: GitHubSyncStatus,
        cloudflare: CloudflareSyncStatus,
        syncedAtEpochMs: Long,
        triggerSource: String
    ): JSONObject {
        val ghRepos = JSONArray().apply {
            github.recentRepos.forEach { r ->
                put(
                    JSONObject().apply {
                        put("fullName", r.fullName)
                        put("defaultBranch", r.defaultBranch)
                        put("isPrivate", r.isPrivate)
                        put("openIssuesCount", r.openIssuesCount)
                        put("pushedAt", r.pushedAt)
                    }
                )
            }
        }
        val ghCommits = JSONArray().apply {
            github.recentCommits.forEach { c ->
                put(
                    JSONObject().apply {
                        put("sha", c.sha)
                        put("message", c.message)
                        put("author", c.author)
                        put("committedAt", c.committedAt)
                    }
                )
            }
        }
        val ghEvents = JSONArray().apply {
            github.recentEvents.forEach { ev ->
                put(
                    JSONObject().apply {
                        put("type", ev.type)
                        put("actor", ev.actor)
                        put("detail", ev.detail)
                        put("createdAt", ev.createdAt)
                    }
                )
            }
        }
        val ghJson = JSONObject().apply {
            put("health", github.health.name)
            put("accountLogin", github.accountLogin)
            put("targetRepo", github.targetRepo)
            put("defaultBranch", github.defaultBranch)
            put("isPrivate", github.isPrivate)
            put("openIssuesCount", github.openIssuesCount)
            put("openPullRequestsCount", github.openPullRequestsCount)
            put("starsCount", github.starsCount)
            put("pushedAt", github.pushedAt)
            put("workflowName", github.workflowName)
            put("workflowStatus", github.workflowStatus)
            put("workflowConclusion", github.workflowConclusion)
            put("workflowUpdatedAt", github.workflowUpdatedAt)
            put("summaryMessage", github.summaryMessage)
            put("recentRepos", ghRepos)
            put("recentCommits", ghCommits)
            put("recentEvents", ghEvents)
        }

        val cfWorkers = JSONArray().apply {
            cloudflare.workers.forEach { w ->
                put(
                    JSONObject().apply {
                        put("scriptId", w.scriptId)
                        put("modifiedOn", w.modifiedOn)
                    }
                )
            }
        }
        val cfJson = JSONObject().apply {
            put("health", cloudflare.health.name)
            put("tokenStatus", cloudflare.tokenStatus)
            put("accountName", cloudflare.accountName)
            put("accountId", cloudflare.accountId)
            put("workerCount", cloudflare.workerCount)
            put("gatewayUrl", cloudflare.gatewayUrl)
            put("gatewayHttpCode", cloudflare.gatewayHttpCode)
            put("gatewayLatencyMs", cloudflare.gatewayLatencyMs)
            put("gatewayHealthMessage", cloudflare.gatewayHealthMessage)
            put("summaryMessage", cloudflare.summaryMessage)
            put("workers", cfWorkers)
        }

        return JSONObject().apply {
            put("userId", uid)
            put("syncedAtEpochMs", syncedAtEpochMs)
            put("triggerSource", triggerSource)
            put("github", ghJson)
            put("cloudflare", cfJson)
        }
    }

    private fun serializeTermux(
        termux: TermuxAgentSyncStatus,
        syncedAtEpochMs: Long,
        triggerSource: String
    ): JSONObject {
        val tJson = JSONObject().apply {
            put("health", termux.health.name)
            put("endpointUrl", termux.endpointUrl)
            put("agentName", termux.agentName)
            put("version", termux.version)
            put("daemonState", termux.daemonState)
            put("pid", termux.pid)
            put("activeTask", termux.activeTask)
            put("lastCommand", termux.lastCommand)
            if (termux.lastExitCode != null) {
                put("lastExitCode", termux.lastExitCode)
            }
            put("lastRunAt", termux.lastRunAt)
            put("requestsToday", termux.requestsToday)
            put("uptimeSeconds", termux.uptimeSeconds)
            put("latencyMs", termux.latencyMs)
            put("summaryMessage", termux.summaryMessage)
        }
        return JSONObject().apply {
            put("termuxSyncedAtEpochMs", syncedAtEpochMs)
            put("termuxTriggerSource", triggerSource)
            put("termux", tJson)
        }
    }

    private fun deserializeGitHub(ghObj: JSONObject): GitHubSyncStatus {
        val ghArr = ghObj.optJSONArray("recentRepos") ?: JSONArray()
        val repos = (0 until ghArr.length()).mapNotNull { idx ->
            val obj = ghArr.optJSONObject(idx) ?: return@mapNotNull null
            GitHubRepoSummary(
                fullName = obj.optString("fullName"),
                defaultBranch = obj.optString("defaultBranch", "main"),
                isPrivate = obj.optBoolean("isPrivate", false),
                openIssuesCount = obj.optInt("openIssuesCount", 0),
                pushedAt = obj.optString("pushedAt")
            )
        }
        val commitArr = ghObj.optJSONArray("recentCommits") ?: JSONArray()
        val commits = (0 until commitArr.length()).mapNotNull { idx ->
            val obj = commitArr.optJSONObject(idx) ?: return@mapNotNull null
            GitHubCommitSummary(
                sha = obj.optString("sha"),
                message = obj.optString("message"),
                author = obj.optString("author"),
                committedAt = obj.optString("committedAt")
            )
        }
        val eventArr = ghObj.optJSONArray("recentEvents") ?: JSONArray()
        val events = (0 until eventArr.length()).mapNotNull { idx ->
            val obj = eventArr.optJSONObject(idx) ?: return@mapNotNull null
            GitHubActivityEvent(
                type = obj.optString("type"),
                actor = obj.optString("actor"),
                detail = obj.optString("detail"),
                createdAt = obj.optString("createdAt")
            )
        }
        return GitHubSyncStatus(
            health = parseHealth(ghObj.optString("health")),
            accountLogin = ghObj.optString("accountLogin"),
            targetRepo = ghObj.optString("targetRepo"),
            defaultBranch = ghObj.optString("defaultBranch"),
            isPrivate = ghObj.optBoolean("isPrivate", false),
            openIssuesCount = ghObj.optInt("openIssuesCount", 0),
            openPullRequestsCount = ghObj.optInt("openPullRequestsCount", 0),
            starsCount = ghObj.optInt("starsCount", 0),
            pushedAt = ghObj.optString("pushedAt"),
            workflowName = ghObj.optString("workflowName"),
            workflowStatus = ghObj.optString("workflowStatus"),
            workflowConclusion = ghObj.optString("workflowConclusion"),
            workflowUpdatedAt = ghObj.optString("workflowUpdatedAt"),
            recentRepos = repos,
            recentCommits = commits,
            recentEvents = events,
            summaryMessage = ghObj.optString("summaryMessage", "未同期")
        )
    }

    private fun deserializeCloudflare(cfObj: JSONObject): CloudflareSyncStatus {
        val cfArr = cfObj.optJSONArray("workers") ?: JSONArray()
        val workers = (0 until cfArr.length()).mapNotNull { idx ->
            val obj = cfArr.optJSONObject(idx) ?: return@mapNotNull null
            CloudflareWorkerSummary(
                scriptId = obj.optString("scriptId"),
                modifiedOn = obj.optString("modifiedOn")
            )
        }
        return CloudflareSyncStatus(
            health = parseHealth(cfObj.optString("health")),
            tokenStatus = cfObj.optString("tokenStatus"),
            accountName = cfObj.optString("accountName"),
            accountId = cfObj.optString("accountId"),
            workerCount = cfObj.optInt("workerCount", 0),
            workers = workers,
            gatewayUrl = cfObj.optString("gatewayUrl", "https://gc2223.com"),
            gatewayHttpCode = cfObj.optInt("gatewayHttpCode", 0),
            gatewayLatencyMs = cfObj.optLong("gatewayLatencyMs", 0L),
            gatewayHealthMessage = cfObj.optString("gatewayHealthMessage"),
            summaryMessage = cfObj.optString("summaryMessage", "未同期")
        )
    }

    private fun deserializeTermux(tObj: JSONObject): TermuxAgentSyncStatus {
        val exitCode = if (tObj.has("lastExitCode") && !tObj.isNull("lastExitCode")) {
            tObj.optInt("lastExitCode")
        } else {
            null
        }
        return TermuxAgentSyncStatus(
            health = parseHealth(tObj.optString("health")),
            endpointUrl = tObj.optString("endpointUrl", "http://127.0.0.1:8787"),
            agentName = tObj.optString("agentName", "AntiGravityAgent"),
            version = tObj.optString("version"),
            daemonState = tObj.optString("daemonState"),
            pid = tObj.optInt("pid", 0),
            activeTask = tObj.optString("activeTask"),
            lastCommand = tObj.optString("lastCommand"),
            lastExitCode = exitCode,
            lastRunAt = tObj.optString("lastRunAt"),
            requestsToday = tObj.optInt("requestsToday", 0),
            uptimeSeconds = tObj.optLong("uptimeSeconds", 0L),
            latencyMs = tObj.optLong("latencyMs", 0L),
            summaryMessage = tObj.optString("summaryMessage", "未同期")
        )
    }

    private fun parseHealth(raw: String): SyncHealthState =
        SyncHealthState.entries.firstOrNull { it.name == raw } ?: SyncHealthState.NEVER_SYNCED

    private fun cloudKey(uid: String) = "cloud_snapshot_$uid"
    private fun termuxKey(uid: String) = "termux_snapshot_$uid"
    private fun enabledKey(uid: String) = "sync_enabled_$uid"
    private fun intervalKey(uid: String) = "sync_interval_$uid"

    companion object {
        const val PREFS_NAME = "antigravity_sync_snapshots"
    }
}
