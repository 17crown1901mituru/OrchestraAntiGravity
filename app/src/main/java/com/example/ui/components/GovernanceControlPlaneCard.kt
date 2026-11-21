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
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.OrchestrationHardeningState
import java.util.Locale

@Composable
fun GovernanceControlPlaneCard(
    state: OrchestrationHardeningState,
    isBusy: Boolean,
    onSyncPolicy: () -> Unit,
    onVerifyAuditChain: () -> Unit = {},
    onRunChaosDrill: () -> Unit = {}
) {
    val chainTampered = state.auditChainStatus.equals("TAMPERED", ignoreCase = true)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (state.versionDriftDetected || chainTampered) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            }
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("governance_control_plane_card")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        imageVector = Icons.Rounded.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ガバナンス・SRE / SAIF 制御プレーン",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (state.wifKeylessEnabled) "WIF Keyless • v${state.policyVersion}" else "v${state.policyVersion}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // SRE SLI/SLO & SAIF Hash Chain
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GovernanceTile(
                    icon = Icons.Rounded.Speed,
                    title = "SRE SLI / Error Budget",
                    value = "${String.format(Locale.US, "%.2f", state.sliSuccessPct)}% • Burn ${String.format(Locale.US, "%.2f", state.burnRate)}x",
                    detail = "残予算 ${String.format(Locale.US, "%.1f", state.errorBudgetRemainingPct)}% • 平均 ${state.avgLatencyMs}ms",
                    modifier = Modifier.weight(1f)
                )
                GovernanceTile(
                    icon = Icons.Rounded.FactCheck,
                    title = "SAIF / SHA-256 監査鎖",
                    value = "Chain: ${state.auditChainStatus} (${state.auditVerifiedCount})",
                    detail = "閾値 ${(state.saifMinConfidence * 100).toInt()}% • #${state.lastAuditHash.take(10).ifBlank { "genesis" }}",
                    modifier = Modifier.weight(1f)
                )
            }

            // Security Boundary (DLP & Taint) & Chaos GameDay Tile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GovernanceTile(
                    icon = Icons.Rounded.Security,
                    title = "LLM境界 (DLP / Taint)",
                    value = "DLP遮蔽 ${state.dlpRedactionsCount} • Taint ${state.taintBlocksCount}",
                    detail = "入力サンドボックス & SSRF防御 有効",
                    modifier = Modifier.weight(1f)
                )
                GovernanceTile(
                    icon = Icons.Rounded.BugReport,
                    title = "カオス自己診断 (GameDay)",
                    value = state.lastChaosScorePct?.let { "スコア ${it}%" } ?: "未実行",
                    detail = state.lastChaosDrilledAt.take(16).ifBlank { "月次Dry-Run待機" },
                    modifier = Modifier.weight(1f)
                )
            }

            // Pillar 1 & 2: Hardware Zero-Trust & GitHub Actions Monthly CI Quota
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GovernanceTile(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Hardware Zero-Trust",
                    value = if (state.hardwareKeyRegistered) "ECDSA 有効" else "鍵未登録",
                    detail = if (state.hardwareKeyRegistered) "Key: ${state.hardwareKeyId.take(12)}" else "StrongBox/TEE 署名推奨",
                    modifier = Modifier.weight(1f)
                )
                GovernanceTile(
                    icon = Icons.Rounded.Memory,
                    title = "CI 月間無料枠 / DAG",
                    value = "${state.ciMinutesUsedMonth} / ${state.ciMinutesLimit}分",
                    detail = if (state.ciOffloadActive) "Federation Offload 作動中" else "並列 DAG • チェックポイント有効",
                    modifier = Modifier.weight(1f)
                )
            }

            // System Integrity Matrix Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "System Integrity Matrix (構成整合性)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = if (state.versionDriftDetected || chainTampered) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when {
                            chainTampered -> "監査鎖異常"
                            state.versionDriftDetected -> "Drift 検知"
                            else -> "Chain: ${state.auditChainStatus}"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (state.versionDriftDetected || chainTampered) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // 2x2 Version Matrix
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MatrixCell(label = "Git SHA", value = state.gitCommitSha, modifier = Modifier.weight(1f))
                MatrixCell(label = "Worker Ver", value = state.workerVersion, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MatrixCell(label = "Terraform Rev", value = state.terraformRevision, modifier = Modifier.weight(1f))
                MatrixCell(label = "Termux CLI", value = state.termuxCliVersion, modifier = Modifier.weight(1f))
            }

            if (state.secretLastRotatedAt.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Secret Manager 最終回転: ${state.secretLastRotatedAt.take(19)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVerifyAuditChain,
                    enabled = !isBusy,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("verify_audit_chain_button")
                ) {
                    Icon(Icons.Rounded.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("監査チェーン検証", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(
                    onClick = onRunChaosDrill,
                    enabled = !isBusy,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("run_chaos_drill_button")
                ) {
                    Icon(Icons.Rounded.BugReport, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("カオス自己診断", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            OutlinedButton(
                onClick = onSyncPolicy,
                enabled = !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sync_policy_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sync,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ポリシー再同期 (Policy-as-Code -> KV)",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GovernanceTile(
    icon: ImageVector,
    title: String,
    value: String,
    detail: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MatrixCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value.ifBlank { "-" }.take(12),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
