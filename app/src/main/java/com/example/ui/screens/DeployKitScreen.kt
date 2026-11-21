package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Terrain
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.StackTemplates
import com.example.model.AskRagResult
import com.example.model.EmailRouteTarget
import com.example.model.OrchestrationHardeningState
import com.example.model.OrchestrationRunResult
import com.example.model.OrchestrationTaskType
import com.example.model.StackTemplate
import com.example.model.TerraformAction
import com.example.ui.components.OrchestrationHardeningPanel

@Composable
fun DeployKitScreen(
    isBusy: Boolean = false,
    isOrchestrating: Boolean = false,
    isHardeningBusy: Boolean = false,
    lastOrchestrationResult: OrchestrationRunResult? = null,
    hardeningState: OrchestrationHardeningState = OrchestrationHardeningState(),
    lastAskResult: AskRagResult? = null,
    isAskingRag: Boolean = false,
    githubRepoConfigured: Boolean = false,
    onPushTemplateToGitHub: (filePath: String, content: String, commitMessage: String) -> Unit = { _, _, _ -> },
    onPushAllOrchestrationStack: () -> Unit = {},
    onDispatchTerraform: (action: TerraformAction, targetEnv: String) -> Unit = { _, _ -> },
    onRunOrchestrationTask: (EmailRouteTarget, OrchestrationTaskType, String, String) -> Unit = { _, _, _, _ -> },
    onRefreshHardening: () -> Unit = {},
    onDecideApproval: (approvalId: String, approve: Boolean) -> Unit = { _, _ -> },
    onToggleSafeMode: (enable: Boolean, reason: String) -> Unit = { _, _ -> },
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
    onAskAuditRag: (String) -> Unit = {},
    onSaveTemplate: (StackTemplate) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val categories = listOf("ALL", "TERRAFORM", "SELF_HEAL", "WORKER", "TERMUX", "GITHUB", "GCP")
    var selectedCategory by remember { mutableStateOf("ALL") }
    var targetEnv by remember { mutableStateOf("dev") }
    var selectedRoute by remember { mutableStateOf(EmailRouteTarget.TASK) }
    var selectedTaskType by remember { mutableStateOf(OrchestrationTaskType.AI_TASK_ROUTER) }
    var orchestrationInput by remember { mutableStateOf("Terraform dev検証とWorkerヘルス同期を実行") }

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == "ALL") StackTemplates.all
        else StackTemplates.all.filter { it.category == selectedCategory }
    }

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
            Text(
                text = "gc2223.com・Terraform・Email修復キット",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Cloudflare Worker・Terraform IaC・Email Worker 自己修復・Termux CLI の実用コード集",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isBusy || isOrchestrating || isHardeningBusy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        modifier = Modifier.testTag("template_filter_$cat")
                    )
                }
            }

            if (selectedCategory == "TERRAFORM") {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("terraform_runner_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Terrain,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Terraform リモート実行 (GitHub Actions)",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            text = if (githubRepoConfigured) {
                                "下のテンプレートを GitHub へ Push し、terraform plan / apply を直接起動できます。"
                            } else {
                                "セキュア設定で GitHub PAT と owner/repo を保存すると、Push と plan/apply を直接起動できます。"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("dev", "prod").forEach { env ->
                                FilterChip(
                                    selected = targetEnv == env,
                                    onClick = { targetEnv = env },
                                    label = { Text("env: $env") },
                                    modifier = Modifier.testTag("tf_env_$env")
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onDispatchTerraform(TerraformAction.PLAN, targetEnv) },
                                enabled = !isBusy,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("dispatch_terraform_plan_button")
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("TF Plan", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Button(
                                onClick = { onDispatchTerraform(TerraformAction.APPLY, targetEnv) },
                                enabled = !isBusy,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("dispatch_terraform_apply_button")
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("TF Apply", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedCategory in listOf("ALL", "SELF_HEAL", "WORKER", "GITHUB", "TERRAFORM")) {
                    item(key = "orchestration_automation_console") {
                        OrchestrationAutomationCard(
                            isBusy = isBusy,
                            isOrchestrating = isOrchestrating,
                            githubRepoConfigured = githubRepoConfigured,
                            targetEnv = targetEnv,
                            selectedRoute = selectedRoute,
                            selectedTaskType = selectedTaskType,
                            inputValue = orchestrationInput,
                            lastResult = lastOrchestrationResult,
                            onSelectRoute = { route ->
                                selectedRoute = route
                                selectedTaskType = when (route) {
                                    EmailRouteTarget.TERRAFORM -> OrchestrationTaskType.TF_PLAN
                                    EmailRouteTarget.CRAWL -> OrchestrationTaskType.CRAWL_ANALYZE
                                    EmailRouteTarget.CI_HEAL -> OrchestrationTaskType.DISPATCH_GITHUB
                                    EmailRouteTarget.TASK -> OrchestrationTaskType.AI_TASK_ROUTER
                                }
                            },
                            onSelectTaskType = { selectedTaskType = it },
                            onInputChange = { orchestrationInput = it },
                            onPushAllStack = onPushAllOrchestrationStack,
                            onRunOrchestration = {
                                onRunOrchestrationTask(selectedRoute, selectedTaskType, orchestrationInput, targetEnv)
                            },
                            onDispatchTerraformPlan = {
                                onDispatchTerraform(TerraformAction.PLAN, targetEnv)
                            }
                        )
                    }
                    item(key = "orchestration_hardening_panel") {
                        OrchestrationHardeningPanel(
                            state = hardeningState,
                            isBusy = isHardeningBusy,
                            onRefresh = onRefreshHardening,
                            onDecideApproval = onDecideApproval,
                            onToggleSafeMode = onToggleSafeMode,
                            onSetOperatingMode = onSetOperatingMode,
                            onSyncPolicy = onSyncPolicy,
                            onVerifyAuditChain = onVerifyAuditChain,
                            onRunChaosDrill = onRunChaosDrill,
                            onRegisterLocalNode = onRegisterLocalNode,
                            onTriggerAutoTune = onTriggerAutoTune,
                            onCreateDrSnapshot = onCreateDrSnapshot,
                            onRestoreDrSnapshot = onRestoreDrSnapshot,
                            onPingDeadmanSwitch = onPingDeadmanSwitch,
                            onRegisterBiometricKey = onRegisterBiometricKey,
                            lastAskResult = lastAskResult,
                            isAskingRag = isAskingRag,
                            onAskAuditRag = onAskAuditRag,
                            onShowMessage = onShowMessage
                        )
                    }
                }
                items(filteredTemplates, key = { it.id }) { template ->
                    val gitPath = resolveGitHubFilePath(template)
                    TemplateCard(
                        template = template,
                        gitHubPushPath = gitPath,
                        isBusy = isBusy,
                        onCopy = {
                            clipboard.setText(AnnotatedString(template.content))
                            onShowMessage("${template.title} をコピーしました")
                        },
                        onShare = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, template.title)
                                putExtra(Intent.EXTRA_TEXT, template.content)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, template.title))
                        },
                        onSave = { onSaveTemplate(template) },
                        onPushGitHub = gitPath?.let { path ->
                            {
                                onPushTemplateToGitHub(
                                    path,
                                    template.content,
                                    "chore(antigravity): update $path"
                                )
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun OrchestrationAutomationCard(
    isBusy: Boolean,
    isOrchestrating: Boolean,
    githubRepoConfigured: Boolean,
    targetEnv: String,
    selectedRoute: EmailRouteTarget,
    selectedTaskType: OrchestrationTaskType,
    inputValue: String,
    lastResult: OrchestrationRunResult?,
    onSelectRoute: (EmailRouteTarget) -> Unit,
    onSelectTaskType: (OrchestrationTaskType) -> Unit,
    onInputChange: (String) -> Unit,
    onPushAllStack: () -> Unit,
    onRunOrchestration: () -> Unit,
    onDispatchTerraformPlan: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("orchestration_automation_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.AutoMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "全体オーケストレーション & Email ルーター",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = "Terraform (Email Routing/KV/GCP)・統合 Worker・GitHub Orchestrator を一括 Push し、宛先別タスク実行 (/v1/orchestrate) を起動します。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPushAllStack,
                    enabled = !isBusy && githubRepoConfigured,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("push_all_orchestration_stack_button")
                ) {
                    Icon(Icons.Rounded.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stack一式 GitHub Push", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(
                    onClick = onDispatchTerraformPlan,
                    enabled = !isBusy,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("quick_tf_plan_button")
                ) {
                    Icon(Icons.Rounded.Terrain, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TF Plan ($targetEnv)", maxLines = 1)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EmailRouteTarget.entries.forEach { route ->
                    FilterChip(
                        selected = selectedRoute == route,
                        onClick = { onSelectRoute(route) },
                        label = { Text(route.label, maxLines = 1) },
                        modifier = Modifier.testTag("email_route_chip_${route.localPart}")
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OrchestrationTaskType.entries.forEach { task ->
                    FilterChip(
                        selected = selectedTaskType == task,
                        onClick = { onSelectTaskType(task) },
                        label = { Text(task.label, maxLines = 1) },
                        modifier = Modifier.testTag("orchestration_task_chip_${task.taskId}")
                    )
                }
            }

            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                label = { Text("タスク内容 / 対象URL / 指示") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("orchestration_input_field")
            )

            Button(
                onClick = onRunOrchestration,
                enabled = !isOrchestrating && inputValue.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_orchestration_task_button")
            ) {
                Icon(Icons.Rounded.MarkEmailRead, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOrchestrating) "オーケストレーション実行中..." else "${selectedRoute.address} でタスク実行",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (lastResult != null) {
                Surface(
                    color = if (lastResult.success) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = lastResult.summary,
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (lastResult.traceId.isNotBlank() || lastResult.traceparent.isNotBlank()) {
                            val tpShort = if (lastResult.traceparent.isNotBlank()) " • tp: ${lastResult.traceparent.take(22)}..." else ""
                            Text(
                                text = "Trace ID: ${lastResult.traceId}$tpShort",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (lastResult.aiConfidence != null || lastResult.aiReasoning.isNotBlank()) {
                            val pct = lastResult.aiConfidence?.let { "${(it * 100).toInt()}%" } ?: "-"
                            Text(
                                text = "SAIF 信頼度: $pct • ${lastResult.aiReasoning.take(120)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (lastResult.detail.isNotBlank()) {
                            SelectionContainer {
                                Text(
                                    text = lastResult.detail.take(360),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun resolveGitHubFilePath(template: StackTemplate): String? = when (template.id) {
    "terraform_llm_infra" -> "infra/main.tf"
    "github_actions_terraform" -> ".github/workflows/antigravity-terraform.yml"
    "github_actions_orchestrator" -> ".github/workflows/antigravity-orchestrator.yml"
    "github_actions_deploy" -> ".github/workflows/antigravity-deploy.yml"
    "governance_policy_json" -> "governance-policy.json"
    "cf_email_autofix_worker" -> "worker/src/worker.js"
    "wrangler_email_selfheal" -> "worker/wrangler.toml"
    "dr_bootstrap_script" -> "scripts/bootstrap-dr.sh"
    "cf_worker_gc2223" -> "src/worker.js"
    "wrangler_toml_gc2223" -> "wrangler.toml"
    else -> null
}

@Composable
private fun TemplateCard(
    template: StackTemplate,
    gitHubPushPath: String?,
    isBusy: Boolean,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onPushGitHub: (() -> Unit)?
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("template_card_${template.id}")
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
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${template.category} • ${template.targetHost}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Rounded.Share, contentDescription = "コードを共有")
                }
            }

            Text(text = template.title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = template.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = template.freeTierSummary,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                SelectionContainer {
                    Text(
                        text = if (expanded) template.content else template.content.lines().take(10).joinToString("\n") + "\n...",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (expanded) "閉じる" else "全文", maxLines = 1)
                }
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_template_${template.id}")
                ) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("コピー", maxLines = 1)
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_template_${template.id}")
                ) {
                    Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("保存", maxLines = 1)
                }
            }

            if (onPushGitHub != null && gitHubPushPath != null) {
                OutlinedButton(
                    onClick = onPushGitHub,
                    enabled = !isBusy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("push_github_${template.id}")
                ) {
                    Icon(Icons.Rounded.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GitHub へ Push ($gitHubPushPath)", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
