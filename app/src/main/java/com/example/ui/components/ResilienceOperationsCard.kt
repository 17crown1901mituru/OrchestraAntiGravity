package com.example.ui.components

import android.Manifest
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon as AndroidIcon
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.DevicesOther
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.OrchestrationHardeningState
import com.example.sync.KillSwitchTileService
import com.example.sync.UrgentAlertNotifier

@Composable
fun ResilienceOperationsCard(
    state: OrchestrationHardeningState,
    isBusy: Boolean,
    onRegisterLocalNode: () -> Unit,
    onTriggerAutoTune: () -> Unit,
    onCreateDrSnapshot: () -> Unit,
    onRestoreDrSnapshot: () -> Unit,
    onPingDeadmanSwitch: () -> Unit = {},
    onRegisterBiometricKey: () -> Unit = {},
    onShowMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(UrgentAlertNotifier.hasNotificationPermission(context))
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
        if (granted) {
            UrgentAlertNotifier.ensureChannel(context)
        }
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.22f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("resilience_operations_card")
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
                        imageVector = Icons.Rounded.DevicesOther,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "フェデレーション・自己進化・DR 復元",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Nodes ${state.federatedNodes.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 1. Multi-Node Federation
            if (state.federatedNodes.isEmpty()) {
                Text(
                    text = "フェデレーションノード未登録（優先: ${state.preferredNodeId}）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                state.federatedNodes.take(3).forEach { node ->
                    val caps = node.capabilities.joinToString(", ").ifBlank { "default" }
                    val statusTag = if (node.online) "Online" else "Idle"
                    Text(
                        text = "• ${node.nodeId} (${node.nodeType} / $statusTag): [$caps]",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            OutlinedButton(
                onClick = onRegisterLocalNode,
                enabled = !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("register_node_button")
            ) {
                Icon(Icons.Rounded.DevicesOther, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("この端末ノードを登録 (android-termux)", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            // 2. Closed-Loop Policy Auto-Tuning PR + Golden Test Gate
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val goldenBadge = if (state.autoTuneBlockedByGolden) {
                        "Golden ${state.goldenScorePct}% (PRブロック)"
                    } else {
                        "Golden ${state.goldenScorePct}%"
                    }
                    Text(
                        text = "自己進化ポリシー PR [$goldenBadge]: ${state.lastTuningSummary.ifBlank { "週次承認履歴から閾値PRを自動生成" }}",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (state.lastTuningPrUrl.isNotBlank()) {
                        Text(
                            text = state.lastTuningPrUrl,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            OutlinedButton(
                onClick = onTriggerAutoTune,
                enabled = !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("trigger_auto_tune_button")
            ) {
                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("自己進化 PR を生成", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            // 3. Encrypted DR Snapshot & Restore
            Text(
                text = "暗号化 DR (AES-256-GCM / ${state.drKdfAlgorithm} -> R2): ${state.lastDrSnapshotKey.ifBlank { "未作成" }}${if (state.lastDrSnapshotAt.isNotBlank()) " (${state.lastDrSnapshotAt.take(16)})" else ""}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCreateDrSnapshot,
                    enabled = !isBusy,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("create_dr_snapshot_button")
                ) {
                    Icon(Icons.Rounded.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DR 退避 (.enc)", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(
                    onClick = onRestoreDrSnapshot,
                    enabled = !isBusy,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("restore_dr_snapshot_button")
                ) {
                    Icon(Icons.Rounded.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DR 復元", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // 4. External Dead Man's Switch & Local Emergency Notifications
            val deadmanLabel = if (state.deadmanConfigured) "設定済み" else "未設定"
            val pingSuffix = if (state.lastDeadmanPingAt.isNotBlank()) " (${state.lastDeadmanPingAt.take(16)})" else ""
            Text(
                text = "外部 Dead Man's Switch: $deadmanLabel$pingSuffix",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            OutlinedButton(
                onClick = onPingDeadmanSwitch,
                enabled = !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("ping_deadman_button")
            ) {
                Icon(Icons.Rounded.MonitorHeart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Dead Man Ping", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                OutlinedButton(
                    onClick = {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("enable_alert_notifications_button")
                ) {
                    Icon(Icons.Rounded.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("緊急プッシュ通知を許可", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // 5. Hardware Zero-Trust Key & Quick Settings Kill-Switch Tile
            OutlinedButton(
                onClick = onRegisterBiometricKey,
                enabled = !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("register_biometric_key_button")
            ) {
                Icon(Icons.Rounded.Fingerprint, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (state.hardwareKeyRegistered) {
                        "生体署名鍵を更新 (登録済: ${state.hardwareKeyId.take(8)})"
                    } else {
                        "生体署名鍵を登録 (StrongBox/TEE)"
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                OutlinedButton(
                    onClick = {
                        val sbm = context.getSystemService(StatusBarManager::class.java)
                        sbm?.requestAddTileService(
                            ComponentName(context, KillSwitchTileService::class.java),
                            context.getString(R.string.qs_tile_killswitch_label),
                            AndroidIcon.createWithResource(context, R.drawable.ic_launcher_foreground),
                            context.mainExecutor
                        ) { resultCode ->
                            when (resultCode) {
                                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ->
                                    onShowMessage("クイック設定に Kill-Switch タイルを追加しました")
                                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED ->
                                    onShowMessage("Kill-Switch タイルは既にクイック設定に追加済みです")
                                else ->
                                    onShowMessage("タイルの追加をスキップしました (code=$resultCode)")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_killswitch_tile_button")
                ) {
                    Icon(Icons.Rounded.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("クイック設定キルスイッチを追加", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
