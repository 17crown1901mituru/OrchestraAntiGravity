package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class WorkflowNode(
    @DocumentId val id: String = "",
    val userId: String = "",
    val provider: String = "CLOUDFLARE",
    val title: String = "",
    val endpointOrTarget: String = "",
    val accountHint: String = "",
    val status: String = "CONFIGURED",
    val monthlyFreeQuota: String = "",
    val lastCheckedNote: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AgentBlueprint(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "CLI",
    val summary: String = "",
    val content: String = "",
    val targetHost: String = "gc2223.com",
    val status: String = "DRAFT",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CostConfig(
    @DocumentId val id: String = "",
    val userId: String = "",
    val alertEmail: String = "17crown2223@gmail.com",
    val dailyThresholdUsd: Double = 0.10,
    val autoEmailEnabled: Boolean = true,
    val webhookPath: String = "/v1/cost-alert",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CostEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val provider: String = "GCP",
    val dateKey: String = "",
    val amountUsd: Double = 0.0,
    val usageSummary: String = "",
    val source: String = "MANUAL",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CostAlert(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dateKey: String = "",
    val totalUsd: Double = 0.0,
    val thresholdUsd: Double = 0.10,
    val recipientEmail: String = "17crown2223@gmail.com",
    val deliveryStatus: String = "QUEUED_LOCAL",
    val deliveryDetail: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

sealed interface StreamUiState<out T> {
    data object Loading : StreamUiState<Nothing>
    data class Success<T>(val data: T) : StreamUiState<T>
    data class Error(val message: String) : StreamUiState<Nothing>
}

sealed interface AiGenerationState {
    data object Idle : AiGenerationState
    data object Loading : AiGenerationState
    data class Streaming(val text: String) : AiGenerationState
    data class Success(val text: String, val structuredPlan: WorkflowPlanResult? = null) : AiGenerationState
    data class Error(val message: String) : AiGenerationState
}

data class WorkflowPlanResult(
    val planTitle: String,
    val targetLayer: String,
    val estimatedMonthlyCost: String,
    val steps: List<String>,
    val cliOrConfigSnippet: String
)

enum class InferenceTarget {
    FIREBASE_GEMINI,
    GC2223_GATEWAY
}

enum class SourceAttachmentKind {
    MARKDOWN,
    PDF,
    IMAGE,
    VIDEO
}

data class AttachedSource(
    val uriString: String,
    val displayName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val kind: SourceAttachmentKind
)

data class ExternalCredentials(
    val gc2223Endpoint: String = "https://gc2223.com",
    val gc2223BearerToken: String = "",
    val cloudflareApiToken: String = "",
    val cloudflareAccountId: String = "",
    val githubPat: String = "",
    val githubOwnerRepo: String = "",
    val githubWorkflowFile: String = "antigravity-deploy.yml",
    val gcpProjectId: String = "gen-lang-client-0439741065",
    val gcpApiKeyOrToken: String = "",
    val gcpBillingAccountId: String = "",
    val termuxAgentUrl: String = "http://127.0.0.1:8787",
    val termuxAgentToken: String = ""
)

data class VaultMetadata(
    val linkedUid: String = "",
    val linkedEmail: String = "",
    val cipherAlgorithm: String = "AES-256-GCM (AndroidKeyStore)",
    val updatedAtEpochMs: Long = 0L,
    val hasStoredSecrets: Boolean = false
)

data class StackTemplate(
    val id: String,
    val title: String,
    val category: String,
    val targetHost: String,
    val subtitle: String,
    val freeTierSummary: String,
    val content: String
)

enum class SyncHealthState {
    NEVER_SYNCED,
    CONNECTED,
    PARTIAL,
    OFFLINE,
    ERROR,
    MISSING_CREDENTIALS
}

data class GitHubRepoSummary(
    val fullName: String,
    val defaultBranch: String,
    val isPrivate: Boolean,
    val openIssuesCount: Int,
    val pushedAt: String
)

data class GitHubCommitSummary(
    val sha: String,
    val message: String,
    val author: String,
    val committedAt: String
)

data class GitHubActivityEvent(
    val type: String,
    val actor: String,
    val detail: String,
    val createdAt: String
)

data class GitHubSyncStatus(
    val health: SyncHealthState = SyncHealthState.NEVER_SYNCED,
    val accountLogin: String = "",
    val targetRepo: String = "",
    val defaultBranch: String = "",
    val isPrivate: Boolean = false,
    val openIssuesCount: Int = 0,
    val openPullRequestsCount: Int = 0,
    val starsCount: Int = 0,
    val pushedAt: String = "",
    val workflowName: String = "",
    val workflowStatus: String = "",
    val workflowConclusion: String = "",
    val workflowUpdatedAt: String = "",
    val workflowRunId: Long = 0L,
    val workflowRunUrl: String = "",
    val recentRepos: List<GitHubRepoSummary> = emptyList(),
    val recentCommits: List<GitHubCommitSummary> = emptyList(),
    val recentEvents: List<GitHubActivityEvent> = emptyList(),
    val summaryMessage: String = "未同期"
)

data class CloudflareWorkerSummary(
    val scriptId: String,
    val modifiedOn: String
)

data class CloudflareSyncStatus(
    val health: SyncHealthState = SyncHealthState.NEVER_SYNCED,
    val tokenStatus: String = "",
    val accountName: String = "",
    val accountId: String = "",
    val workerCount: Int = 0,
    val workers: List<CloudflareWorkerSummary> = emptyList(),
    val gatewayUrl: String = "https://gc2223.com",
    val gatewayHttpCode: Int = 0,
    val gatewayLatencyMs: Long = 0L,
    val gatewayHealthMessage: String = "",
    val summaryMessage: String = "未同期"
)

data class TermuxAgentSyncStatus(
    val health: SyncHealthState = SyncHealthState.NEVER_SYNCED,
    val endpointUrl: String = "http://127.0.0.1:8787",
    val agentName: String = "AntiGravityAgent",
    val version: String = "",
    val daemonState: String = "",
    val pid: Int = 0,
    val activeTask: String = "",
    val lastCommand: String = "",
    val lastExitCode: Int? = null,
    val lastRunAt: String = "",
    val requestsToday: Int = 0,
    val uptimeSeconds: Long = 0L,
    val latencyMs: Long = 0L,
    val summaryMessage: String = "未同期"
)

data class SyncStatusSnapshot(
    val userId: String = "",
    val syncedAtEpochMs: Long = 0L,
    val github: GitHubSyncStatus = GitHubSyncStatus(),
    val cloudflare: CloudflareSyncStatus = CloudflareSyncStatus(),
    val triggerSource: String = "",
    val termuxSyncedAtEpochMs: Long = 0L,
    val termux: TermuxAgentSyncStatus = TermuxAgentSyncStatus(),
    val termuxTriggerSource: String = ""
)

data class BackgroundSyncSettings(
    val enabled: Boolean = true,
    val intervalMinutes: Long = 15L
)

enum class DeviceViewportProfile(
    val label: String,
    val widthDp: Int,
    val heightDp: Int,
    val userAgent: String
) {
    PIXEL_9_PRO(
        label = "Pixel 9 Pro",
        widthDp = 412,
        heightDp = 915,
        userAgent = "Mozilla/5.0 (Linux; Android 15; Pixel 9 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36"
    ),
    GALAXY_S25_ULTRA(
        label = "Galaxy S25 Ultra",
        widthDp = 412,
        heightDp = 915,
        userAgent = "Mozilla/5.0 (Linux; Android 15; SM-S938B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36"
    ),
    IPHONE_16_PRO(
        label = "iPhone 16 Pro",
        widthDp = 393,
        heightDp = 852,
        userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 18_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.3 Mobile/15E148 Safari/604.1"
    ),
    FOLDABLE_UNFOLDED(
        label = "Foldable 展開",
        widthDp = 840,
        heightDp = 900,
        userAgent = "Mozilla/5.0 (Linux; Android 15; Pixel 9 Pro Fold) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
    )
}

enum class CrawlMode(val label: String) {
    SINGLE_URL("単一URL"),
    DOMAIN_CRAWL("ドメイン巡回")
}

data class WebCrawlPreview(
    val targetUrl: String,
    val pageCount: Int,
    val deviceProfileLabel: String,
    val summarySnippet: String,
    val attachedFileName: String,
    val hasScreenshot: Boolean = false
)

enum class TerraformAction(val cliValue: String, val label: String) {
    PLAN("plan", "terraform plan"),
    APPLY("apply", "terraform apply"),
    DESTROY("destroy", "terraform destroy")
}

enum class TermuxPresetCommand(val presetId: String, val label: String) {
    ANTIGRAVITY_HEALTH("antigravity_health", "GW 疎通"),
    GIT_PULL("git_pull", "git pull"),
    TF_PLAN("tf_plan", "TF Plan"),
    TF_APPLY("tf_apply", "TF Apply"),
    WRANGLER_DEPLOY("wrangler_deploy", "Wrangler")
}

data class TermuxCommandResult(
    val success: Boolean,
    val presetId: String,
    val exitCode: Int,
    val stdout: String = "",
    val stderr: String = "",
    val durationMs: Long = 0L,
    val summaryMessage: String = ""
)

enum class EmailRouteTarget(val localPart: String, val address: String, val label: String) {
    TASK("task", "task@gc2223.com", "AIタスク実行 (task@)"),
    TERRAFORM("tf", "tf@gc2223.com", "Terraform実行 (tf@)"),
    CI_HEAL("ci-heal", "ci-heal@gc2223.com", "自己修復 (ci-heal@)"),
    CRAWL("crawl", "crawl@gc2223.com", "URL調査 (crawl@)")
}

enum class OrchestrationTaskType(val taskId: String, val label: String) {
    AI_TASK_ROUTER("ai_task_router", "AI自律ルーティング"),
    TF_PLAN("tf_plan", "Terraform Plan"),
    TF_APPLY("tf_apply", "Terraform Apply"),
    DISPATCH_GITHUB("dispatch_github", "GitHub Actions起動"),
    QUEUE_TERMUX("queue_termux", "Termux Pull キュー投入"),
    CRAWL_ANALYZE("crawl_analyze", "URLクロール解析")
}

data class OrchestrationRunResult(
    val success: Boolean,
    val routeAddress: String,
    val taskId: String,
    val statusCode: Int,
    val summary: String,
    val detail: String = "",
    val traceId: String = ""
)

data class PendingApprovalItem(
    val approvalId: String,
    val action: String,
    val repo: String = "",
    val targetEnv: String = "dev",
    val summary: String = "",
    val traceId: String = "",
    val expiresAtMs: Long = 0L
)

data class AuditTraceEntry(
    val traceId: String,
    val source: String,
    val taskType: String,
    val ok: Boolean,
    val summary: String,
    val timestamp: String
)

data class OrchestrationHardeningState(
    val safeMode: Boolean = false,
    val safeReason: String = "",
    val aiHourCount: Int = 0,
    val ghHourCount: Int = 0,
    val dlqCount: Int = 0,
    val approvalsCount: Int = 0,
    val termuxBatteryPct: Int? = null,
    val termuxCharging: Boolean? = null,
    val termuxNetwork: String = "",
    val termuxLastSeen: String = "",
    val cronLastAt: String = "",
    val cronLastConclusion: String = "",
    val patExpiryWarning: String = "",
    val approvals: List<PendingApprovalItem> = emptyList(),
    val recentAuditLogs: List<AuditTraceEntry> = emptyList(),
    val statusMessage: String = ""
)

