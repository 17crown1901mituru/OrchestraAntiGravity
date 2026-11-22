package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.StreamUiState
import com.example.model.WorkflowNode

@Composable
fun NodesScreen(
    nodesState: StreamUiState<List<WorkflowNode>>,
    isBusy: Boolean,
    onOpenSettings: () -> Unit,
    onOpenSetupWizard: () -> Unit = {},
    onTestGc2223Health: (WorkflowNode) -> Unit,
    onVerifyCloudflare: (WorkflowNode) -> Unit,
    onVerifyGitHub: (WorkflowNode) -> Unit,
    onDispatchGitHub: (WorkflowNode) -> Unit,
    onOpenSelfHeal: () -> Unit = {},
    onMarkNodeActive: (WorkflowNode, String, String) -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "連携ノード & 無料枠",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "クレカ併用時の¥0維持ガードと疎通テスト",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("open_credentials_vault_button")
                ) {
                    Icon(Icons.Rounded.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("暗号化設定")
                }
            }

            Button(
                onClick = onOpenSetupWizard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("open_gcp_setup_wizard_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Google → GCP プロジェクト / API / 全ワークフロー連携ウィザード")
            }

            if (isBusy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            when (nodesState) {
                is StreamUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is StreamUiState.Error -> {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = nodesState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is StreamUiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(nodesState.data, key = { it.id }) { node ->
                            WorkflowNodeCard(
                                node = node,
                                onTestGc2223 = { onTestGc2223Health(node) },
                                onVerifyCloudflare = { onVerifyCloudflare(node) },
                                onVerifyGitHub = { onVerifyGitHub(node) },
                                onDispatchGitHub = { onDispatchGitHub(node) },
                                onOpenSelfHeal = onOpenSelfHeal,
                                onToggleStatus = { status, note ->
                                    onMarkNodeActive(node, status, note)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkflowNodeCard(
    node: WorkflowNode,
    onTestGc2223: () -> Unit,
    onVerifyCloudflare: () -> Unit,
    onVerifyGitHub: () -> Unit,
    onDispatchGitHub: () -> Unit,
    onOpenSelfHeal: () -> Unit,
    onToggleStatus: (String, String) -> Unit
) {
    val icon = when (node.provider) {
        "CLOUDFLARE" -> Icons.Rounded.Cloud
        "GITHUB" -> Icons.Rounded.Code
        "GCP" -> Icons.Rounded.Security
        "TERMUX" -> Icons.Rounded.Terminal
        else -> Icons.Rounded.Verified
    }
    val statusColor = when (node.status) {
        "ACTIVE" -> MaterialTheme.colorScheme.secondary
        "ERROR" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("node_card_${node.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
                    Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = node.title, style = MaterialTheme.typography.titleMedium)
                }
                Surface(
                    color = statusColor.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = node.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = node.endpointOrTarget,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "無料枠: ${node.monthlyFreeQuota}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (node.lastCheckedNote.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (node.status == "ERROR") Icons.Rounded.ErrorOutline else Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = node.lastCheckedNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (node.provider) {
                    "CLOUDFLARE" -> {
                        OutlinedButton(
                            onClick = onTestGc2223,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ping_gc2223_button")
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("/health 疎通")
                        }
                        Button(
                            onClick = onVerifyCloudflare,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("verify_cloudflare_button")
                        ) {
                            Text("CF Token確認")
                        }
                    }

                    "GITHUB" -> {
                        OutlinedButton(
                            onClick = onVerifyGitHub,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("verify_github_button")
                        ) {
                            Text("PAT確認", maxLines = 1)
                        }
                        Button(
                            onClick = onDispatchGitHub,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dispatch_github_button")
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Actions", maxLines = 1)
                        }
                        OutlinedButton(
                            onClick = onOpenSelfHeal,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("node_self_heal_button")
                        ) {
                            Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("修復", maxLines = 1)
                        }
                    }

                    "GCP" -> {
                        OutlinedButton(
                            onClick = {
                                onToggleStatus(
                                    "ACTIVE",
                                    "GCP $1予算アラート・Always Free構成を確認済み"
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_gcp_guard_button")
                        ) {
                            Text("予算ガード確認済にする")
                        }
                    }

                    "TERMUX" -> {
                        OutlinedButton(
                            onClick = {
                                onToggleStatus(
                                    "ACTIVE",
                                    "Termux antigravity CLI 導入済み"
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_termux_ready_button")
                        ) {
                            Text("Termux 導入済みにする")
                        }
                    }
                }
            }
        }
    }
}
