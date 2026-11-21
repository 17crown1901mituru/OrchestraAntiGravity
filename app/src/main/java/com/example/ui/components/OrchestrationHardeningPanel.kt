package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AskRagResult
import com.example.model.OrchestrationHardeningState

@Composable
fun OrchestrationHardeningPanel(
    state: OrchestrationHardeningState,
    isBusy: Boolean,
    onRefresh: () -> Unit,
    onDecideApproval: (approvalId: String, approve: Boolean) -> Unit,
    onToggleSafeMode: (enable: Boolean, reason: String) -> Unit,
    onSetOperatingMode: (mode: String) -> Unit = {},
    onSyncPolicy: () -> Unit = {},
    onVerifyAuditChain: () -> Unit = {},
    onRunChaosDrill: () -> Unit = {},
    onRegisterLocalNode: () -> Unit = {},
    onTriggerAutoTune: () -> Unit = {},
    onCreateDrSnapshot: () -> Unit = {},
    onRestoreDrSnapshot: () -> Unit = {},
    onPingDeadmanSwitch: () -> Unit = {},
    onRegisterBiometricKey: () -> Unit = {},
    lastAskResult: AskRagResult? = null,
    isAskingRag: Boolean = false,
    onAskAuditRag: (String) -> Unit = {},
    onShowMessage: (String) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (state.safeMode) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.28f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (state.safeMode) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("orchestration_hardening_panel")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
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
                        imageVector = if (state.safeMode) Icons.Rounded.WarningAmber else Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = if (state.safeMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "自律ハードニング・承認・監査パネル",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onRefresh,
                    enabled = !isBusy,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("refresh_hardening_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "ハードニング状態を取得"
                    )
                }
            }

            if (state.statusMessage.isNotBlank()) {
                Text(
                    text = state.statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 1. SAFE_MODE Circuit Breaker & Rate-Limit Quota
            Surface(
                color = if (state.safeMode) {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.safeMode) "Circuit Breaker: SAFE_MODE 遮断中" else "Circuit Breaker: 正常稼働中",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (state.safeMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "AI ${state.aiHourCount}/30 • GH ${state.ghHourCount}/15 • DLQ ${state.dlqCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (state.safeMode && state.safeReason.isNotBlank()) {
                        Text(
                            text = "停止理由: ${state.safeReason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            val nextEnable = !state.safeMode
                            val reason = if (nextEnable) "Manual Safe Mode from AntiGravity Hub" else "Manual reset from AntiGravity Hub"
                            onToggleSafeMode(nextEnable, reason)
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("toggle_safe_mode_button")
                    ) {
                        Icon(
                            imageVector = if (state.safeMode) Icons.Rounded.GppGood else Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.safeMode) "SAFE_MODE を解除して復旧" else "緊急停止 (SAFE_MODE 有効化)",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2. Termux Heartbeat, Battery Guard & Cron Watchdog Status
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.BatteryChargingFull,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val batteryStr = state.termuxBatteryPct?.let { "${it}%" } ?: "未計測"
                        val chargeStr = when (state.termuxCharging) {
                            true -> "充電中"
                            false -> "バッテリー駆動"
                            null -> "-"
                        }
                        val netStr = state.termuxNetwork.ifBlank { "unknown" }
                        Text(
                            text = "Termux Heartbeat: 電池 $batteryStr ($chargeStr) • 通信 $netStr",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    val cronLabel = state.cronLastConclusion.ifBlank { "未取得" }
                    val patWarn = if (state.patExpiryWarning.isNotBlank()) " • PAT期限: ${state.patExpiryWarning}" else ""
                    Text(
                        text = "6h Cron 監視: $cronLabel${if (state.cronLastAt.isNotBlank()) " (${state.cronLastAt.take(16)})" else ""}$patWarn",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SustainabilityTelemetryCard(
                state = state,
                isBusy = isBusy,
                onSetOperatingMode = onSetOperatingMode
            )

            GovernanceControlPlaneCard(
                state = state,
                isBusy = isBusy,
                onSyncPolicy = onSyncPolicy,
                onVerifyAuditChain = onVerifyAuditChain,
                onRunChaosDrill = onRunChaosDrill
            )

            ResilienceOperationsCard(
                state = state,
                isBusy = isBusy,
                onRegisterLocalNode = onRegisterLocalNode,
                onTriggerAutoTune = onTriggerAutoTune,
                onCreateDrSnapshot = onCreateDrSnapshot,
                onRestoreDrSnapshot = onRestoreDrSnapshot,
                onPingDeadmanSwitch = onPingDeadmanSwitch,
                onRegisterBiometricKey = onRegisterBiometricKey,
                onShowMessage = onShowMessage
            )

            AuditRagQueryCard(
                lastResult = lastAskResult,
                isBusy = isAskingRag,
                onAskQuestion = onAskAuditRag
            )

            // 3. Pending One-Time Approvals (30m TTL)
            Text(
                text = "ワンタイム承認待ち (${state.approvals.size}件 / TTL 30分)",
                style = MaterialTheme.typography.titleSmall
            )
            if (state.approvals.isEmpty()) {
                Text(
                    text = "現在、承認待ちの Terraform Apply / CI 再実行はありません（更新ボタンで最新化）。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                state.approvals.forEach { item ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pending_approval_${item.approvalId}")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "[${item.approvalId}] ${item.summary.ifBlank { item.action }} (${item.targetEnv})",
                                style = MaterialTheme.typography.labelLarge
                            )
                            val confSuffix = item.aiConfidence?.let { " • SAIF ${(it * 100).toInt()}%" } ?: ""
                            Text(
                                text = "Repo: ${item.repo.ifBlank { "-" }} • trace: ${item.traceId.ifBlank { "-" }}$confSuffix",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (item.aiReasoning.isNotBlank()) {
                                Text(
                                    text = "根拠: ${item.aiReasoning.take(120)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (item.criticRiskLevel.isNotBlank() || item.criticWarnings.isNotEmpty()) {
                                val risk = item.criticRiskLevel.ifBlank { "WARN" }
                                val isHigh = risk.equals("HIGH", ignoreCase = true)
                                Surface(
                                    color = if (isHigh) {
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                                    } else {
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Critic [$risk]: ${item.criticWarnings.joinToString(" / ").ifBlank { "安全レビュー済み" }}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isHigh) {
                                            MaterialTheme.colorScheme.onErrorContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSecondaryContainer
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onDecideApproval(item.approvalId, true) },
                                    enabled = !isBusy,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("approve_item_${item.approvalId}")
                                ) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("承認実行", maxLines = 1)
                                }
                                OutlinedButton(
                                    onClick = { onDecideApproval(item.approvalId, false) },
                                    enabled = !isBusy,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("reject_item_${item.approvalId}")
                                ) {
                                    Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("却下", maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Trace ID Audit Timeline
            if (state.recentAuditLogs.isNotEmpty()) {
                HorizontalDivider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Timeline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "最新監査タイムライン (R2 / KV Trace)",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                state.recentAuditLogs.take(5).forEach { log ->
                    val hashBadge = if (log.entryHash.isNotBlank()) " #${log.entryHash.take(8)}" else ""
                    val confBadge = log.aiConfidence?.let { " (${(it * 100).toInt()}%)" } ?: ""
                    Text(
                        text = "• [${log.traceId.ifBlank { "-" }}$hashBadge] ${log.source} (${log.taskType}$confBadge): ${log.summary.take(90)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (log.ok) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
