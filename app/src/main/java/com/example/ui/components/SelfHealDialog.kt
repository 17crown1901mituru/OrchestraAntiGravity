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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SelfHealDialog(
    defaultRepo: String,
    defaultWorkflow: String,
    defaultRunId: Long,
    initialErrorLog: String? = null,
    isSelfHealing: Boolean,
    selfHealReport: String?,
    onDismiss: () -> Unit,
    onRunSelfHeal: (
        errorLog: String,
        repo: String,
        workflow: String,
        runId: Long,
        sendToEmailWorker: Boolean,
        rerunWorkflow: Boolean
    ) -> Unit,
    onQuickRerunOnly: (Long) -> Unit
) {
    var errorLog by remember(initialErrorLog) {
        mutableStateOf(
            initialErrorLog?.takeIf { it.isNotBlank() }
                ?: "Error: Error creating CloudRun Service: googleapi: Error 403: Cloud Run Admin API has not been used in project before or it is disabled."
        )
    }
    var repoText by remember(defaultRepo) { mutableStateOf(defaultRepo) }
    var workflowText by remember(defaultWorkflow) { mutableStateOf(defaultWorkflow.ifBlank { "antigravity-terraform.yml" }) }
    var runIdText by remember(defaultRunId) { mutableStateOf(if (defaultRunId > 0L) defaultRunId.toString() else "") }
    var sendToEmailWorker by remember { mutableStateOf(true) }
    var rerunWorkflow by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .widthIn(max = 580.dp)
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            imageVector = Icons.Rounded.AutoFixHigh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Email Worker 自己修復 & 再実行",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "閉じる")
                    }
                }

                Text(
                    text = "失敗ログを Gemini で診断して修正パッチを生成し、gc2223.com Email Worker 通知 + GitHub Actions 再実行を行います（リトライ上限ガード付き）。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = repoText,
                    onValueChange = { repoText = it },
                    label = { Text("対象リポジトリ (owner/repo)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = workflowText,
                        onValueChange = { workflowText = it },
                        label = { Text("Workflow ファイル") },
                        singleLine = true,
                        modifier = Modifier.weight(1.4f)
                    )
                    OutlinedTextField(
                        value = runIdText,
                        onValueChange = { runIdText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Run ID (任意)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = errorLog,
                    onValueChange = { errorLog = it },
                    label = { Text("Terraform / Actions エラーログ") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("self_heal_error_log_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("gc2223.com Email Worker へ送信", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = sendToEmailWorker,
                        onCheckedChange = { sendToEmailWorker = it },
                        modifier = Modifier.testTag("self_heal_email_switch")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("修正後に GitHub ワークフロー再実行", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = rerunWorkflow,
                        onCheckedChange = { rerunWorkflow = it },
                        modifier = Modifier.testTag("self_heal_rerun_switch")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val parsedRunId = runIdText.toLongOrNull() ?: 0L
                    if (parsedRunId > 0L) {
                        OutlinedButton(
                            onClick = { onQuickRerunOnly(parsedRunId) },
                            enabled = !isSelfHealing,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("quick_rerun_only_button")
                        ) {
                            Text("即再実行")
                        }
                    }
                    Button(
                        onClick = {
                            onRunSelfHeal(
                                errorLog,
                                repoText,
                                workflowText,
                                parsedRunId,
                                sendToEmailWorker,
                                rerunWorkflow
                            )
                        },
                        enabled = !isSelfHealing && errorLog.isNotBlank(),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("execute_self_heal_button")
                    ) {
                        if (isSelfHealing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("修復実行中...")
                        } else {
                            Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("診断・修復・再実行")
                        }
                    }
                }

                if (!selfHealReport.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("self_heal_report_box")
                    ) {
                        SelectionContainer {
                            Text(
                                text = selfHealReport,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
