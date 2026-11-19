package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.BackgroundSyncSettings
import com.example.model.CloudflareSyncStatus
import com.example.model.GitHubSyncStatus
import com.example.model.SyncHealthState
import com.example.model.SyncStatusSnapshot
import com.example.model.TermuxAgentSyncStatus
import com.example.sync.SyncWorkUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatusDashboardScreen(
    snapshot: SyncStatusSnapshot,
    syncSettings: BackgroundSyncSettings,
    syncWorkState: SyncWorkUiState,
    onSyncNow: () -> Unit,
    onSyncTermuxNow: () -> Unit,
    onToggleBackgroundSync: (Boolean) -> Unit,
    onSelectIntervalMinutes: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    val lastSyncedLabel = remember(snapshot.syncedAtEpochMs) {
        formatEpochMs(snapshot.syncedAtEpochMs, emptyText = "まだ同期されていません")
    }
    val nextRunLabel = remember(syncWorkState.nextRunEpochMs, syncSettings.enabled) {
        if (!syncSettings.enabled) {
            "停止中"
        } else {
            formatEpochMs(syncWorkState.nextRunEpochMs, emptyText = "システム最適化中")
        }
    }
    val hasMissingCreds = snapshot.github.health == SyncHealthState.MISSING_CREDENTIALS ||
        snapshot.cloudflare.health == SyncHealthState.MISSING_CREDENTIALS ||
        snapshot.syncedAtEpochMs == 0L

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Background Sync Control Card
            item {
                BackgroundSyncControlCard(
                    syncSettings = syncSettings,
                    syncWorkState = syncWorkState,
                    lastSyncedLabel = lastSyncedLabel,
                    nextRunLabel = nextRunLabel,
                    triggerSource = snapshot.triggerSource,
                    hasMissingCreds = hasMissingCreds,
                    onSyncNow = onSyncNow,
                    onToggleBackgroundSync = onToggleBackgroundSync,
                    onSelectIntervalMinutes = onSelectIntervalMinutes,
                    onOpenSettings = onOpenSettings
                )
            }

            // 2. Termux AntiGravityAgent Local CLI Status Card
            item {
                TermuxStatusCard(
                    termux = snapshot.termux,
                    lastSyncedEpochMs = snapshot.termuxSyncedAtEpochMs,
                    triggerSource = snapshot.termuxTriggerSource,
                    isSyncingTermux = syncWorkState.isTermuxSyncingNow,
                    onSyncTermuxNow = onSyncTermuxNow,
                    onOpenSettings = onOpenSettings
                )
            }

            // 3. GitHub Repositories & Actions Status Card
            item {
                GitHubStatusCard(
                    github = snapshot.github,
                    onOpenSettings = onOpenSettings
                )
            }

            // 4. Cloudflare Workers & gc2223.com Gateway Status Card
            item {
                CloudflareStatusCard(
                    cloudflare = snapshot.cloudflare,
                    onOpenSettings = onOpenSettings
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BackgroundSyncControlCard(
    syncSettings: BackgroundSyncSettings,
    syncWorkState: SyncWorkUiState,
    lastSyncedLabel: String,
    nextRunLabel: String,
    triggerSource: String,
    hasMissingCreds: Boolean,
    onSyncNow: () -> Unit,
    onToggleBackgroundSync: (Boolean) -> Unit,
    onSelectIntervalMinutes: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    val intervals = listOf(15L to "15分", 30L to "30分", 60L to "1時間", 360L to "6時間")

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("background_sync_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CloudSync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "バックグラウンド同期",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Surface(
                    color = if (syncWorkState.isSyncingNow) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else if (syncSettings.enabled) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = syncWorkState.periodicStateLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (syncWorkState.isSyncingNow) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else if (syncSettings.enabled) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (syncWorkState.isSyncingNow) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "WorkManager 定期ステータス取得",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "GitHub・Cloudflare・Termux CLI を自動取得",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = syncSettings.enabled,
                    onCheckedChange = onToggleBackgroundSync,
                    modifier = Modifier.testTag("toggle_background_sync_switch")
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                intervals.forEach { (minutes, label) ->
                    FilterChip(
                        selected = syncSettings.intervalMinutes == minutes,
                        onClick = { onSelectIntervalMinutes(minutes) },
                        label = { Text(label) },
                        modifier = Modifier.testTag("sync_interval_$minutes")
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "最終同期: $lastSyncedLabel" +
                            if (triggerSource.isNotBlank()) " ($triggerSource)" else "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "次回予定: $nextRunLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSyncNow,
                    enabled = !syncWorkState.isSyncingNow,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("sync_now_button")
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (syncWorkState.isSyncingNow) "同期中..." else "今すぐ同期")
                }

                if (hasMissingCreds) {
                    OutlinedButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("dashboard_open_settings_button")
                    ) {
                        Icon(Icons.Rounded.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("キー設定")
                    }
                }
            }
        }
    }
}

@Composable
private fun TermuxStatusCard(
    termux: TermuxAgentSyncStatus,
    lastSyncedEpochMs: Long,
    triggerSource: String,
    isSyncingTermux: Boolean,
    onSyncTermuxNow: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val (badgeColor, badgeIcon) = healthVisuals(termux.health)
    val lastTermuxLabel = remember(lastSyncedEpochMs) {
        formatEpochMs(lastSyncedEpochMs, emptyText = "未取得")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("termux_status_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusCardHeader(
                title = "Termux AntiGravityAgent (ローカル)",
                icon = Icons.Rounded.Terminal,
                health = termux.health,
                badgeColor = badgeColor,
                badgeIcon = badgeIcon
            )

            if (isSyncingTermux) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Text(
                text = termux.summaryMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (termux.health == SyncHealthState.ERROR) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = termux.endpointUrl.ifBlank { "http://127.0.0.1:8787" },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${termux.agentName}${if (termux.version.isNotBlank()) " v${termux.version}" else ""} • 同期: $lastTermuxLabel" +
                                if (triggerSource.isNotBlank()) " ($triggerSource)" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (termux.latencyMs > 0L) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${termux.latencyMs}ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            if (termux.daemonState.isNotBlank() || termux.activeTask.isNotBlank() || termux.lastCommand.isNotBlank()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (termux.daemonState.isNotBlank()) {
                        val stateValue = buildString {
                            append(termux.daemonState)
                            if (termux.pid > 0) append(" (PID ${termux.pid})")
                            if (termux.uptimeSeconds > 0L) append(" • 稼働 ${formatUptime(termux.uptimeSeconds)}")
                        }
                        MetricRow("エージェント状態", stateValue)
                    }
                    if (termux.activeTask.isNotBlank()) {
                        MetricRow("実行中タスク", termux.activeTask)
                    }
                    if (termux.lastCommand.isNotBlank()) {
                        val cmdSummary = buildString {
                            append(termux.lastCommand)
                            if (termux.lastExitCode != null) append(" (exit ${termux.lastExitCode})")
                        }
                        MetricRow("最新 CLI コマンド", cmdSummary)
                    }
                    if (termux.requestsToday > 0) {
                        MetricRow("本日リクエスト数", "${termux.requestsToday} 件")
                    }
                    if (termux.lastRunAt.isNotBlank()) {
                        MetricRow("最終実行日時", formatIsoDateShort(termux.lastRunAt))
                    }
                }
            }

            if (termux.health == SyncHealthState.OFFLINE || termux.health == SyncHealthState.NEVER_SYNCED) {
                Text(
                    text = "ヒント: Termux で `antigravity serve` を起動すると、ローカルエージェントの稼働状況が自動反映されます。",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSyncTermuxNow,
                    enabled = !isSyncingTermux,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("sync_termux_now_button")
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSyncingTermux) "取得中..." else "Termux 更新")
                }
                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("termux_open_settings_button")
                ) {
                    Icon(Icons.Rounded.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("URL設定")
                }
            }
        }
    }
}

@Composable
private fun GitHubStatusCard(
    github: GitHubSyncStatus,
    onOpenSettings: () -> Unit
) {
    val (badgeColor, badgeIcon) = healthVisuals(github.health)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("github_status_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusCardHeader(
                title = "GitHub リポジトリ & アクティビティ",
                icon = Icons.Rounded.Code,
                health = github.health,
                badgeColor = badgeColor,
                badgeIcon = badgeIcon
            )

            Text(
                text = github.summaryMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (github.health == SyncHealthState.ERROR) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            if (github.targetRepo.isNotBlank() || github.accountLogin.isNotBlank()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (github.accountLogin.isNotBlank()) {
                        MetricRow("アカウント", "@${github.accountLogin}")
                    }
                    if (github.targetRepo.isNotBlank()) {
                        val visibility = if (github.isPrivate) "Private" else "Public"
                        MetricRow(
                            "対象リポジトリ",
                            "${github.targetRepo} ($visibility • ${github.defaultBranch.ifBlank { "main" }})"
                        )
                        MetricRow(
                            "PR / Issue / Stars",
                            "PR ${github.openPullRequestsCount} 件 • Issue ${github.openIssuesCount} 件 • ★ ${github.starsCount}"
                        )
                        if (github.pushedAt.isNotBlank()) {
                            MetricRow("最終 Push", formatIsoDateShort(github.pushedAt))
                        }
                    }
                    if (github.workflowStatus.isNotBlank()) {
                        MetricRow(
                            "最新 Actions",
                            "${github.workflowName.ifBlank { "Workflow" }}: ${github.workflowStatus} / ${github.workflowConclusion.ifBlank { "running" }}"
                        )
                    }
                }
            }

            if (github.recentCommits.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("github_recent_commits_section")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "直近のコミットサマリー",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        github.recentCommits.forEach { commit ->
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "${commit.sha} • ${commit.message}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${commit.author} • ${formatIsoDateShort(commit.committedAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (github.recentEvents.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("github_recent_events_section")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "アクティビティイベント",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        github.recentEvents.forEach { ev ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${ev.actor}: ${ev.detail}",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatIsoDateShort(ev.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (github.recentRepos.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "最近更新されたリポジトリ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        github.recentRepos.forEach { repo ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = repo.fullName,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${repo.defaultBranch} • ${formatIsoDateShort(repo.pushedAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (github.health == SyncHealthState.MISSING_CREDENTIALS) {
                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("セキュア設定で GitHub PAT を登録")
                }
            }
        }
    }
}

@Composable
private fun CloudflareStatusCard(
    cloudflare: CloudflareSyncStatus,
    onOpenSettings: () -> Unit
) {
    val (badgeColor, badgeIcon) = healthVisuals(cloudflare.health)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cloudflare_status_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusCardHeader(
                title = "Cloudflare Workers & Gateway",
                icon = Icons.Rounded.Cloud,
                health = cloudflare.health,
                badgeColor = badgeColor,
                badgeIcon = badgeIcon
            )

            Text(
                text = cloudflare.summaryMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (cloudflare.health == SyncHealthState.ERROR) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            if (cloudflare.gatewayHttpCode > 0 || cloudflare.gatewayHealthMessage.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cloudflare.gatewayUrl,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = cloudflare.gatewayHealthMessage.ifBlank { "/health" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (cloudflare.gatewayHttpCode > 0) {
                                    "HTTP ${cloudflare.gatewayHttpCode} (${cloudflare.gatewayLatencyMs}ms)"
                                } else {
                                    "${cloudflare.gatewayLatencyMs}ms"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            if (cloudflare.tokenStatus.isNotBlank() || cloudflare.accountId.isNotBlank()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (cloudflare.tokenStatus.isNotBlank()) {
                        MetricRow("API Token 状態", cloudflare.tokenStatus)
                    }
                    if (cloudflare.accountName.isNotBlank() || cloudflare.accountId.isNotBlank()) {
                        val accountDisplay = buildString {
                            if (cloudflare.accountName.isNotBlank()) append(cloudflare.accountName)
                            if (cloudflare.accountId.isNotBlank()) {
                                if (isNotEmpty()) append(" • ")
                                append(cloudflare.accountId.take(12))
                                if (cloudflare.accountId.length > 12) append("...")
                            }
                        }
                        MetricRow("アカウント", accountDisplay)
                    }
                    MetricRow("デプロイ済み Workers", "${cloudflare.workerCount} 件")
                }
            }

            if (cloudflare.workers.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Workers スクリプト一覧",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        cloudflare.workers.forEach { worker ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = worker.scriptId,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatIsoDateShort(worker.modifiedOn),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (cloudflare.health == SyncHealthState.MISSING_CREDENTIALS) {
                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("セキュア設定で Cloudflare Token を登録")
                }
            }
        }
    }
}

@Composable
private fun StatusCardHeader(
    title: String,
    icon: ImageVector,
    health: SyncHealthState,
    badgeColor: Color,
    badgeIcon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }
        Surface(
            color = badgeColor.copy(alpha = 0.16f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = healthLabel(health),
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor
                )
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun healthVisuals(health: SyncHealthState): Pair<Color, ImageVector> = when (health) {
    SyncHealthState.CONNECTED -> MaterialTheme.colorScheme.secondary to Icons.Rounded.CheckCircle
    SyncHealthState.ERROR -> MaterialTheme.colorScheme.error to Icons.Rounded.ErrorOutline
    SyncHealthState.PARTIAL -> MaterialTheme.colorScheme.primary to Icons.Rounded.WarningAmber
    SyncHealthState.OFFLINE -> MaterialTheme.colorScheme.tertiary to Icons.Rounded.CloudOff
    SyncHealthState.MISSING_CREDENTIALS -> MaterialTheme.colorScheme.tertiary to Icons.Rounded.Key
    SyncHealthState.NEVER_SYNCED -> MaterialTheme.colorScheme.onSurfaceVariant to Icons.Rounded.Schedule
}

private fun healthLabel(health: SyncHealthState): String = when (health) {
    SyncHealthState.CONNECTED -> "OK"
    SyncHealthState.ERROR -> "ERROR"
    SyncHealthState.PARTIAL -> "PARTIAL"
    SyncHealthState.OFFLINE -> "OFFLINE"
    SyncHealthState.MISSING_CREDENTIALS -> "要設定"
    SyncHealthState.NEVER_SYNCED -> "未同期"
}

private fun formatEpochMs(epochMs: Long, emptyText: String): String {
    if (epochMs <= 0L) return emptyText
    return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date(epochMs))
}

private fun formatIsoDateShort(iso: String): String {
    if (iso.isBlank()) return "-"
    return iso.take(16).replace("T", " ")
}

private fun formatUptime(seconds: Long): String {
    if (seconds < 60L) return "${seconds}s"
    val mins = seconds / 60L
    if (mins < 60L) return "${mins}m"
    val hours = mins / 60L
    val remMins = mins % 60L
    return "${hours}h ${remMins}m"
}
