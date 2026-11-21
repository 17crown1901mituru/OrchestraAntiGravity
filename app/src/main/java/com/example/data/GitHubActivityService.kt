package com.example.data

import com.example.model.GitHubActivityEvent
import com.example.model.GitHubCommitSummary
import com.example.model.GitHubRepoSummary
import com.example.model.GitHubSyncStatus
import com.example.model.SyncHealthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service responsible for fetching GitHub repository activity summaries
 * (authenticated user profile, recent repositories, commits, open pull requests,
 * Actions workflow runs, and repository activity events) using the saved GitHub PAT.
 */
class GitHubActivityService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
        .build(),
    private val apiBaseUrl: String = "https://api.github.com"
) {
    private val cleanBaseUrl = apiBaseUrl.trimEnd('/')

    suspend fun fetchRepositoryActivitySummary(
        pat: String,
        ownerRepo: String,
        workflowFile: String
    ): GitHubSyncStatus = withContext(Dispatchers.IO) {
        val cleanPat = pat.trim()
        val cleanRepo = normalizeRepoSlug(ownerRepo)
        if (cleanPat.isBlank() && !cleanRepo.contains("/")) {
            return@withContext GitHubSyncStatus(
                health = SyncHealthState.MISSING_CREDENTIALS,
                summaryMessage = "セキュア設定で GitHub PAT とリポジトリを設定してください。"
            )
        }

        var login = ""
        val recentRepos = mutableListOf<GitHubRepoSummary>()
        var repoInfoError: String? = null

        try {
            if (cleanPat.isNotBlank()) {
                val userReq = githubGet("$cleanBaseUrl/user", cleanPat)
                client.newCall(userReq).execute().use { resp ->
                    val raw = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        return@withContext GitHubSyncStatus(
                            health = SyncHealthState.ERROR,
                            targetRepo = cleanRepo,
                            summaryMessage = "GitHub 認証エラー (HTTP ${resp.code}): ${parseMessage(raw)}"
                        )
                    }
                    login = JSONObject(raw).optString("login", "")
                }

                val reposReq = githubGet("$cleanBaseUrl/user/repos?sort=updated&per_page=3", cleanPat)
                client.newCall(reposReq).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val arr = JSONArray(resp.body?.string().orEmpty().ifBlank { "[]" })
                        for (i in 0 until arr.length()) {
                            val obj = arr.optJSONObject(i) ?: continue
                            recentRepos.add(
                                GitHubRepoSummary(
                                    fullName = obj.optString("full_name"),
                                    defaultBranch = obj.optString("default_branch", "main"),
                                    isPrivate = obj.optBoolean("private", false),
                                    openIssuesCount = obj.optInt("open_issues_count", 0),
                                    pushedAt = obj.optString("pushed_at")
                                )
                            )
                        }
                    }
                }
            }

            val resolvedRepo = when {
                cleanRepo.contains("/") -> cleanRepo
                recentRepos.isNotEmpty() -> recentRepos.first().fullName
                else -> ""
            }

            if (resolvedRepo.isBlank()) {
                return@withContext GitHubSyncStatus(
                    health = if (login.isNotBlank()) SyncHealthState.PARTIAL else SyncHealthState.MISSING_CREDENTIALS,
                    accountLogin = login,
                    recentRepos = recentRepos,
                    summaryMessage = if (login.isNotBlank()) {
                        "@$login で認証済み（リポジトリが見つかりません）"
                    } else {
                        "GitHub リポジトリ (owner/repo) を設定してください。"
                    }
                )
            }

            var defaultBranch = "main"
            var isPrivate = false
            var openIssuesAndPrs = 0
            var starsCount = 0
            var pushedAt = ""
            var finalRepoName = resolvedRepo

            client.newCall(githubGet("$cleanBaseUrl/repos/$resolvedRepo", cleanPat)).execute().use { resp ->
                val raw = resp.body?.string().orEmpty()
                if (resp.isSuccessful) {
                    val obj = JSONObject(raw)
                    finalRepoName = obj.optString("full_name", resolvedRepo)
                    defaultBranch = obj.optString("default_branch", "main")
                    isPrivate = obj.optBoolean("private", false)
                    openIssuesAndPrs = obj.optInt("open_issues_count", 0)
                    starsCount = obj.optInt("stargazers_count", 0)
                    pushedAt = obj.optString("pushed_at", "")
                } else {
                    repoInfoError = "Repo HTTP ${resp.code}: ${parseMessage(raw)}"
                }
            }

            var wfName = workflowFile.trim()
            var wfStatus = ""
            var wfConclusion = ""
            var wfUpdatedAt = ""
            var wfRunId = 0L
            var wfRunUrl = ""
            var openPrCount = 0
            val recentCommits = mutableListOf<GitHubCommitSummary>()
            val recentEvents = mutableListOf<GitHubActivityEvent>()

            if (repoInfoError == null) {
                // 1. Latest Actions run
                runCatching {
                    client.newCall(
                        githubGet("$cleanBaseUrl/repos/$resolvedRepo/actions/runs?per_page=1", cleanPat)
                    ).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val root = JSONObject(resp.body?.string().orEmpty().ifBlank { "{}" })
                            val latest = root.optJSONArray("workflow_runs")?.optJSONObject(0)
                            if (latest != null) {
                                wfName = latest.optString("name", wfName)
                                wfStatus = latest.optString("status", "")
                                wfConclusion = latest.optString("conclusion", "")
                                wfUpdatedAt = latest.optString("updated_at", "")
                                wfRunId = latest.optLong("id", 0L)
                                wfRunUrl = latest.optString("html_url", "")
                            }
                        }
                    }
                }

                // 2. Recent commits summary
                runCatching {
                    client.newCall(
                        githubGet("$cleanBaseUrl/repos/$resolvedRepo/commits?per_page=4", cleanPat)
                    ).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val arr = JSONArray(resp.body?.string().orEmpty().ifBlank { "[]" })
                            for (i in 0 until arr.length()) {
                                val item = arr.optJSONObject(i) ?: continue
                                val sha = item.optString("sha", "").take(7)
                                val commitObj = item.optJSONObject("commit")
                                val fullMsg = commitObj?.optString("message", "").orEmpty()
                                val headline = fullMsg.lineSequence().firstOrNull().orEmpty().take(90)
                                val authorObj = commitObj?.optJSONObject("author")
                                val authorName = authorObj?.optString("name").orEmpty()
                                    .ifBlank { item.optJSONObject("author")?.optString("login").orEmpty() }
                                val committedAt = authorObj?.optString("date").orEmpty()
                                if (sha.isNotBlank() || headline.isNotBlank()) {
                                    recentCommits.add(
                                        GitHubCommitSummary(
                                            sha = sha,
                                            message = headline.ifBlank { "(no commit message)" },
                                            author = authorName.ifBlank { "unknown" },
                                            committedAt = committedAt
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Open Pull Requests count (GitHub's open_issues_count includes open PRs)
                runCatching {
                    client.newCall(
                        githubGet("$cleanBaseUrl/repos/$resolvedRepo/pulls?state=open&per_page=20", cleanPat)
                    ).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val arr = JSONArray(resp.body?.string().orEmpty().ifBlank { "[]" })
                            openPrCount = arr.length()
                        }
                    }
                }

                // 4. Recent repository activity events
                runCatching {
                    client.newCall(
                        githubGet("$cleanBaseUrl/repos/$resolvedRepo/events?per_page=4", cleanPat)
                    ).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val arr = JSONArray(resp.body?.string().orEmpty().ifBlank { "[]" })
                            for (i in 0 until arr.length()) {
                                val ev = arr.optJSONObject(i) ?: continue
                                val type = ev.optString("type", "Event")
                                val actor = ev.optJSONObject("actor")?.optString("login").orEmpty()
                                val payload = ev.optJSONObject("payload")
                                val detail = describeEventDetail(type, payload)
                                recentEvents.add(
                                    GitHubActivityEvent(
                                        type = type,
                                        actor = actor.ifBlank { "github" },
                                        detail = detail,
                                        createdAt = ev.optString("created_at")
                                    )
                                )
                            }
                        }
                    }
                }
            }

            val pureOpenIssuesCount = (openIssuesAndPrs - openPrCount).coerceAtLeast(0)
            val health = when {
                repoInfoError != null && login.isNotBlank() -> SyncHealthState.PARTIAL
                repoInfoError != null -> SyncHealthState.ERROR
                else -> SyncHealthState.CONNECTED
            }
            val msg = when {
                repoInfoError != null -> repoInfoError!!
                else -> buildSummaryMessage(
                    repoName = finalRepoName,
                    branch = defaultBranch,
                    commitsCount = recentCommits.size,
                    openPrs = openPrCount,
                    openIssues = pureOpenIssuesCount,
                    wfStatus = wfStatus,
                    wfConclusion = wfConclusion
                )
            }

            GitHubSyncStatus(
                health = health,
                accountLogin = login,
                targetRepo = finalRepoName,
                defaultBranch = defaultBranch,
                isPrivate = isPrivate,
                openIssuesCount = pureOpenIssuesCount,
                openPullRequestsCount = openPrCount,
                starsCount = starsCount,
                pushedAt = pushedAt,
                workflowName = wfName,
                workflowStatus = wfStatus,
                workflowConclusion = wfConclusion,
                workflowUpdatedAt = wfUpdatedAt,
                workflowRunId = wfRunId,
                workflowRunUrl = wfRunUrl,
                recentRepos = recentRepos,
                recentCommits = recentCommits,
                recentEvents = recentEvents,
                summaryMessage = msg
            )
        } catch (e: Exception) {
            GitHubSyncStatus(
                health = SyncHealthState.ERROR,
                accountLogin = login,
                targetRepo = cleanRepo,
                recentRepos = recentRepos,
                summaryMessage = e.localizedMessage ?: "GitHub アクティビティ取得に失敗しました"
            )
        }
    }

    private fun githubGet(url: String, pat: String): Request {
        val builder = Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .get()
        if (pat.isNotBlank()) {
            builder.header("Authorization", "Bearer $pat")
        }
        return builder.build()
    }

    private fun describeEventDetail(type: String, payload: JSONObject?): String {
        if (payload == null) return type
        return when (type) {
            "PushEvent" -> {
                val ref = payload.optString("ref").removePrefix("refs/heads/")
                val size = payload.optInt("size", payload.optJSONArray("commits")?.length() ?: 0)
                if (ref.isNotBlank()) "pushed $size commit(s) to $ref" else "pushed $size commit(s)"
            }
            "PullRequestEvent" -> {
                val action = payload.optString("action", "updated")
                val num = payload.optInt("number", 0)
                if (num > 0) "PR #$num $action" else "Pull Request $action"
            }
            "IssuesEvent" -> {
                val action = payload.optString("action", "updated")
                val num = payload.optJSONObject("issue")?.optInt("number", 0) ?: 0
                if (num > 0) "Issue #$num $action" else "Issue $action"
            }
            "ReleaseEvent" -> {
                val tag = payload.optJSONObject("release")?.optString("tag_name").orEmpty()
                "released ${tag.ifBlank { "new version" }}"
            }
            else -> payload.optString("action").ifBlank { type.removeSuffix("Event") }
        }
    }

    private fun buildSummaryMessage(
        repoName: String,
        branch: String,
        commitsCount: Int,
        openPrs: Int,
        openIssues: Int,
        wfStatus: String,
        wfConclusion: String
    ): String {
        val parts = mutableListOf("$repoName ($branch)")
        if (commitsCount > 0) parts.add("直近Commit ${commitsCount}件")
        parts.add("PR $openPrs / Issue $openIssues")
        if (wfStatus.isNotBlank()) {
            parts.add("Actions: $wfStatus/${wfConclusion.ifBlank { "-" }}")
        }
        return parts.joinToString(" • ")
    }

    private fun normalizeRepoSlug(raw: String): String = raw.trim()
        .removePrefix("https://github.com/")
        .removePrefix("http://github.com/")
        .removeSuffix(".git")
        .trim('/')

    private fun parseMessage(raw: String): String = runCatching {
        JSONObject(raw).optString("message").ifBlank { raw.take(160) }
    }.getOrElse { raw.take(160) }
}
