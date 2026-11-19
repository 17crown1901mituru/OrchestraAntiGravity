package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.FirestoreRepository
import com.example.model.CostAlert
import com.example.model.CostConfig
import com.example.model.CostEntry
import com.example.model.ExternalCredentials
import com.example.model.StreamUiState
import java.util.Locale

@Composable
fun CostMonitorScreen(
    userEmail: String,
    credentials: ExternalCredentials,
    costConfig: CostConfig,
    costEntriesState: StreamUiState<List<CostEntry>>,
    costAlertsState: StreamUiState<List<CostAlert>>,
    onSaveCostConfig: (alertEmail: String, thresholdUsd: Double, autoEmail: Boolean, webhookPath: String) -> Unit,
    onAddExpense: (provider: String, amountUsd: Double, summary: String) -> Unit,
    onDeleteEntry: (entryId: String) -> Unit,
    onTriggerAlertTest: (todayTotal: Double, breakdown: Map<String, Double>) -> Unit,
    onFetchSsoToken: ((String?) -> Unit) -> Unit,
    onOpenSettings: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val todayKey = remember { FirestoreRepository.todayDateKey() }

    val entries = (costEntriesState as? StreamUiState.Success)?.data.orEmpty()
    val todayEntries = remember(entries, todayKey) { entries.filter { it.dateKey == todayKey } }
    val todayTotal = remember(todayEntries) { todayEntries.sumOf { it.amountUsd } }
    val breakdown = remember(todayEntries) {
        val map = linkedMapOf("GCP" to 0.0, "CLOUDFLARE" to 0.0, "FIREBASE" to 0.0, "GITHUB" to 0.0)
        todayEntries.forEach { e ->
            map[e.provider] = (map[e.provider] ?: 0.0) + e.amountUsd
        }
        map
    }

    val alerts = (costAlertsState as? StreamUiState.Success)?.data.orEmpty()
    val exceeded = todayTotal > costConfig.dailyThresholdUsd
    val ratio = (todayTotal / costConfig.dailyThresholdUsd.coerceAtLeast(0.001)).toFloat().coerceIn(0f, 1f)

    var showLogDialog by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "コスト監視 & SSO",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "日次しきい値 $${String.format(Locale.US, "%.2f", costConfig.dailyThresholdUsd)}/日 (${costConfig.alertEmail})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { showConfigDialog = true },
                        modifier = Modifier.testTag("open_cost_config_button")
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = "アラート設定")
                    }
                }
            }

            // Daily Budget Gauge Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (exceeded) {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (exceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (exceeded) Icons.Rounded.WarningAmber else Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = if (exceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "本日 ($todayKey) の累計コスト",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = "$${String.format(Locale.US, "%.4f", todayTotal)} / $${String.format(Locale.US, "%.2f", costConfig.dailyThresholdUsd)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (exceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = if (exceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            breakdown.forEach { (provider, amount) ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = provider,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$${String.format(Locale.US, "%.3f", amount)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showLogDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_cost_entry_button")
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("費用を記録")
                            }
                            OutlinedButton(
                                onClick = { onTriggerAlertTest(todayTotal, breakdown) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("trigger_cost_alert_button")
                            ) {
                                Icon(Icons.Rounded.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("メール通知テスト")
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val subject = Uri.encode("[AntiGravity Cost Alert] $todayKey: $${String.format(Locale.US, "%.4f", todayTotal)}")
                                val body = Uri.encode("Today ($todayKey) spend: $${String.format(Locale.US, "%.4f", todayTotal)} (threshold: $${String.format(Locale.US, "%.2f", costConfig.dailyThresholdUsd)}/day)\nBreakdown: $breakdown")
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${costConfig.alertEmail}?subject=$subject&body=$body")
                                }
                                context.startActivity(Intent.createChooser(intent, "メールアプリで通知"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mailto_fallback_button")
                        ) {
                            Icon(Icons.Rounded.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${costConfig.alertEmail} へメールアプリで送信")
                        }
                    }
                }
            }

            // Unified Google SSO & Cross-Service Auth Status Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.VerifiedUser,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "シングルサインオン (Google SSO) 状態",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            text = "ログイン中: ${userEmail.ifBlank { "Google Account" }} (Credential Manager 自動サインイン有効)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        SsoRow("Firebase & GCP", "Google SSO 共有済み (ID Token 発行中)", true)
                        SsoRow(
                            "Cloudflare (gc2223.com)",
                            if (credentials.cloudflareApiToken.isNotBlank() || credentials.gc2223BearerToken.isNotBlank()) {
                                "API Token + Firebase JWT ブリッジ設定済み"
                            } else {
                                "Firebase JWT 対応可 / API Token 未入力 (セキュア設定で暗号化保存)"
                            },
                            credentials.cloudflareApiToken.isNotBlank()
                        )
                        SsoRow(
                            "GitHub",
                            if (credentials.githubPat.isNotBlank()) {
                                "PAT 連携済み (${credentials.githubOwnerRepo.ifBlank { "repo未指定" }})"
                            } else {
                                "GitHub PAT 未入力 (セキュア設定で暗号化保存)"
                            },
                            credentials.githubPat.isNotBlank()
                        )
                        OutlinedButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cost_open_settings_button")
                        ) {
                            Icon(Icons.Rounded.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("API キー・暗号化ボルト設定を開く")
                        }
                        OutlinedButton(
                            onClick = {
                                onFetchSsoToken { token ->
                                    if (!token.isNullOrBlank()) {
                                        clipboard.setText(AnnotatedString(token))
                                        onShowMessage("Firebase SSO ID Token をコピーしました (gc2223.com X-Firebase-Token用)")
                                    } else {
                                        onShowMessage("ID Token の取得に失敗しました")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("copy_sso_jwt_button")
                        ) {
                            Icon(Icons.Rounded.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("gc2223.com / Termux 用 SSO JWT をコピー")
                        }
                    }
                }
            }

            // Recent Alert History
            item {
                Text(
                    text = "アラート送信履歴 (${alerts.size}件)",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (alerts.isEmpty()) {
                item {
                    Text(
                        text = "しきい値 ($${String.format(Locale.US, "%.2f", costConfig.dailyThresholdUsd)}/日) 超過アラートはまだ発生していません。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(alerts.take(5), key = { it.id }) { alert ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${alert.dateKey} • $${String.format(Locale.US, "%.4f", alert.totalUsd)} -> ${alert.recipientEmail} [${alert.deliveryStatus}]",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = alert.deliveryDetail,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Cost Entries Log
            item {
                Text(
                    text = "記録済みコスト・テレメトリ (${entries.size}件)",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(entries.take(20), key = { it.id }) { entry ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${entry.provider} • ${entry.dateKey} • $${String.format(Locale.US, "%.4f", entry.amountUsd)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = entry.usageSummary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(onClick = { onDeleteEntry(entry.id) }) {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = "記録を削除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showLogDialog) {
        AddExpenseDialog(
            onDismiss = { showLogDialog = false },
            onSave = { provider, amount, summary ->
                onAddExpense(provider, amount, summary)
                showLogDialog = false
            }
        )
    }

    if (showConfigDialog) {
        EditCostConfigDialog(
            initial = costConfig,
            onDismiss = { showConfigDialog = false },
            onSave = { email, threshold, autoEmail, webhookPath ->
                onSaveCostConfig(email, threshold, autoEmail, webhookPath)
                showConfigDialog = false
            }
        )
    }
}

@Composable
private fun SsoRow(service: String, detail: String, connected: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = service, style = MaterialTheme.typography.labelLarge)
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            color = if (connected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (connected) "OK" else "要設定",
                style = MaterialTheme.typography.labelSmall,
                color = if (connected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (provider: String, amountUsd: Double, summary: String) -> Unit
) {
    val providers = listOf("GCP", "CLOUDFLARE", "FIREBASE", "GITHUB")
    var selectedProvider by remember { mutableStateOf("GCP") }
    var amountText by remember { mutableStateOf("0.02") }
    var summary by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("日次コストを記録") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    providers.forEach { p ->
                        FilterChip(
                            selected = selectedProvider == p,
                            onClick = { selectedProvider = p },
                            label = { Text(p) }
                        )
                    }
                }
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("金額 (USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("内訳メモ (例: Cloud Run / Gemini)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            val parsed = amountText.toDoubleOrNull()
            Button(
                onClick = {
                    if (parsed != null && parsed >= 0.0) {
                        onSave(selectedProvider, parsed, summary.ifBlank { "$selectedProvider daily usage" })
                    }
                },
                enabled = parsed != null && parsed >= 0.0,
                modifier = Modifier.testTag("confirm_add_expense_button")
            ) {
                Text("記録")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}

@Composable
private fun EditCostConfigDialog(
    initial: CostConfig,
    onDismiss: () -> Unit,
    onSave: (email: String, thresholdUsd: Double, autoEmail: Boolean, webhookPath: String) -> Unit
) {
    var email by remember { mutableStateOf(initial.alertEmail) }
    var thresholdText by remember { mutableStateOf(initial.dailyThresholdUsd.toString()) }
    var autoEmail by remember { mutableStateOf(initial.autoEmailEnabled) }
    var webhookPath by remember { mutableStateOf(initial.webhookPath) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("コスト監視しきい値設定") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("通知先メールアドレス") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = thresholdText,
                    onValueChange = { thresholdText = it },
                    label = { Text("日次しきい値 (USD / day)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = webhookPath,
                    onValueChange = { webhookPath = it },
                    label = { Text("gc2223.com Webhook パス") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("超過時に自動で Webhook 通知", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = autoEmail, onCheckedChange = { autoEmail = it })
                }
            }
        },
        confirmButton = {
            val parsed = thresholdText.toDoubleOrNull()
            Button(
                onClick = {
                    if (parsed != null && parsed > 0.0) {
                        onSave(email, parsed, autoEmail, webhookPath)
                    }
                },
                enabled = email.isNotBlank() && parsed != null && parsed > 0.0,
                modifier = Modifier.testTag("save_cost_config_button")
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}
