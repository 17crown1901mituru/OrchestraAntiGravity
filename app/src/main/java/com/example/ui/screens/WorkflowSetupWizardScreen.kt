package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Api
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.auth.extractGcpAccessTokenFromIntent
import com.example.auth.requestGcpOAuthAuthorization
import com.example.model.ExternalCredentials
import com.example.model.GcpWizardState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun WorkflowSetupWizardScreen(
    userEmail: String,
    wizardState: GcpWizardState,
    credentials: ExternalCredentials,
    onOAuthTokenAcquired: (String) -> Unit,
    onOAuthError: (String) -> Unit,
    onLoadProjects: () -> Unit,
    onSelectProject: (String) -> Unit,
    onCreateProject: (projectId: String, displayName: String) -> Unit,
    onLinkBillingAccount: (String) -> Unit,
    onToggleApiSelection: (String) -> Unit,
    onRefreshApis: () -> Unit,
    onEnableSelectedApis: () -> Unit,
    onSyncFirebase: () -> Unit,
    onExportPortableBackupJson: () -> String,
    onApplyAllWorkflows: (gatewayUrl: String, githubOwnerRepo: String, termuxAgentUrl: String) -> Unit,
    onVerifyCloudflareGateway: (String) -> Unit,
    onVerifyGitHubSso: () -> Unit,
    onVerifyTermux: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    var showManualTokenFallback by rememberSaveable { mutableStateOf(false) }
    var manualTokenInput by rememberSaveable { mutableStateOf("") }
    var showCreateProjectForm by rememberSaveable { mutableStateOf(false) }
    var newProjectName by rememberSaveable { mutableStateOf("AntiGravity Hub") }
    var newProjectId by rememberSaveable {
        mutableStateOf("antigravity-hub-${(1000..9999).random()}")
    }
    var gatewayUrlInput by rememberSaveable(credentials.gc2223Endpoint) {
        mutableStateOf(credentials.gc2223Endpoint.ifBlank { "https://gc2223.com" })
    }
    var githubRepoInput by rememberSaveable(credentials.githubOwnerRepo) {
        mutableStateOf(credentials.githubOwnerRepo)
    }
    var termuxUrlInput by rememberSaveable(credentials.termuxAgentUrl) {
        mutableStateOf(credentials.termuxAgentUrl.ifBlank { "http://127.0.0.1:8765" })
    }

    val oauthResolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            extractGcpAccessTokenFromIntent(context, res.data)
                .onSuccess { token -> onOAuthTokenAcquired(token) }
                .onFailure { err ->
                    onOAuthError(err.localizedMessage ?: "GCP OAuth 結果からトークンを取得できませんでした")
                }
        } else {
            onOAuthError("GCP OAuth の同意がキャンセルされました")
        }
    }

    val backupExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val payload = onExportPortableBackupJson()
            val writeRes = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
                        it.write(payload)
                    } ?: throw IllegalStateException("出力ストリームを開けません")
                }
            }
            writeRes.onSuccess {
                onShowMessage("再インストール用バックアップ JSON を保存しました")
            }.onFailure { err ->
                onShowMessage("バックアップ保存失敗: ${err.localizedMessage ?: "IO error"}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Google → GCP → 全ワークフロー GUI ウィザード",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "OAuth 権限承認・GCP プロジェクト選択/作成・API 有効化・Firebase 署名 SHA 同期・Cloudflare/GitHub/Termux 連携をGUIで完結させます。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (wizardState.isBusy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (!wizardState.errorMessage.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("wizard_error_banner")
                ) {
                    Text(
                        text = wizardState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else if (!wizardState.statusMessage.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("wizard_status_banner")
                ) {
                    Text(
                        text = wizardState.statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Card 0: APK Signing Conflict Countermeasure & Portable Config Backup
            val diag = wizardState.signingDiagnostics
            WizardCard(
                title = "APK 署名診断 & 再インストール衝突対策",
                icon = Icons.Rounded.Shield
            ) {
                if (diag != null) {
                    Text(
                        text = "Package: ${diag.packageName} (v${diag.versionName} / code ${diag.versionCode})",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = diag.matchesReleaseHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (diag.matchesRepoDebugKeystore) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    if (diag.sha1.isNotBlank()) {
                        Text(
                            text = "SHA-1: ${diag.sha1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (diag.sha256.isNotBlank()) {
                        Text(
                            text = "SHA-256: ${diag.sha256}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shaText = buildString {
                                appendLine("Package: ${diag?.packageName ?: context.packageName}")
                                appendLine("SHA-1: ${diag?.sha1.orEmpty()}")
                                appendLine("SHA-256: ${diag?.sha256.orEmpty()}")
                            }
                            copyText(context, "APK Fingerprints", shaText)
                            onShowMessage("APK の SHA-1 / SHA-256 をコピーしました")
                        },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("wizard_copy_sha_button")
                    ) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("署名 SHA コピー", maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = { backupExportLauncher.launch("antigravity-bootstrap.json") },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("wizard_export_backup_button")
                    ) {
                        Icon(Icons.Rounded.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("再導入用バックアップ", maxLines = 1)
                    }
                }
                Text(
                    text = "※ 別鍵(Release版等)の APK へ切り替える際、既存データ保持上書きが拒否されたら「再導入用バックアップ」保存後に旧APKを削除→新APKで「google-services.json 設定」から復元し、Step 3 で Firebase に新 SHA を自動登録できます。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Step 1: Google OAuth (cloud-platform)
            WizardCard(
                title = "Step 1: Google Cloud / Firebase OAuth 承認",
                icon = Icons.Rounded.VerifiedUser,
                completed = wizardState.oauthAuthorized
            ) {
                Text(
                    text = "アカウント: ${userEmail.ifBlank { "Google Account" }}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        if (activity == null) {
                            onOAuthError("OAuth 同意画面の起動には Activity が必要です")
                        } else {
                            requestGcpOAuthAuthorization(
                                activity = activity,
                                onAccessToken = onOAuthTokenAcquired,
                                onResolutionRequired = { intentReq -> oauthResolutionLauncher.launch(intentReq) },
                                onError = onOAuthError
                            )
                        }
                    },
                    enabled = !wizardState.isBusy,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("wizard_authorize_gcp_button")
                ) {
                    Icon(Icons.Rounded.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (wizardState.oauthAuthorized) "Google OAuth を再承認 / 更新" else "Google で GCP / Firebase API 権限を承認")
                }

                TextButton(
                    onClick = { showManualTokenFallback = !showManualTokenFallback },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = "OAuth 手動トークン貼付 (gcloud auth print-access-token)",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (showManualTokenFallback) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (showManualTokenFallback) "閉じる" else "開く"
                    )
                }

                AnimatedVisibility(visible = showManualTokenFallback) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = manualTokenInput,
                            onValueChange = { manualTokenInput = it },
                            label = { Text("GCP OAuth2 Access Token (ya29....)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("wizard_manual_token_input")
                        )
                        OutlinedButton(
                            onClick = { onOAuthTokenAcquired(manualTokenInput) },
                            enabled = manualTokenInput.isNotBlank() && !wizardState.isBusy,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("トークンを適用して次へ")
                        }
                    }
                }
            }

            // Step 2: GCP Project Selection / Creation & Billing
            WizardCard(
                title = "Step 2: GCP プロジェクト選択 / 新規作成",
                icon = Icons.Rounded.Security,
                completed = wizardState.selectedProjectId.isNotBlank()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onLoadProjects,
                        enabled = wizardState.oauthAuthorized && !wizardState.isBusy,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("wizard_load_projects_button")
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("一覧を取得")
                    }
                    OutlinedButton(
                        onClick = { showCreateProjectForm = !showCreateProjectForm },
                        enabled = !wizardState.isBusy,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("wizard_toggle_create_project_button")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("新規作成")
                    }
                }

                if (wizardState.projects.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        wizardState.projects.forEach { proj ->
                            val selected = proj.projectId == wizardState.selectedProjectId
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectProject(proj.projectId) }
                                    .testTag("wizard_project_card_${proj.projectId}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selected, onClick = { onSelectProject(proj.projectId) })
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(proj.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(proj.projectId, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                } else if (wizardState.selectedProjectId.isNotBlank()) {
                    Text(
                        text = "現在の設定プロジェクト: ${wizardState.selectedProjectId}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                AnimatedVisibility(visible = showCreateProjectForm) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newProjectName,
                            onValueChange = { newProjectName = it },
                            label = { Text("表示名 (例: AntiGravity Hub)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("wizard_new_project_name_input")
                        )
                        OutlinedTextField(
                            value = newProjectId,
                            onValueChange = { newProjectId = it },
                            label = { Text("新規 GCP Project ID (英小文字・数字・ハイフン 6〜30文字)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("wizard_new_project_id_input")
                        )
                        Button(
                            onClick = { onCreateProject(newProjectId, newProjectName) },
                            enabled = wizardState.oauthAuthorized && !wizardState.isBusy,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("wizard_create_project_button")
                        ) {
                            Text("GCP プロジェクトを新規作成して選択")
                        }
                    }
                }

                if (wizardState.billingAccounts.isNotEmpty() && wizardState.selectedProjectId.isNotBlank()) {
                    Text("請求アカウント (任意リンク):", style = MaterialTheme.typography.labelMedium)
                    wizardState.billingAccounts.forEach { acc ->
                        OutlinedButton(
                            onClick = { onLinkBillingAccount(acc.name) },
                            enabled = !wizardState.isBusy,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("${acc.displayName} をリンク (${if (acc.open) "有効" else "停止"})")
                        }
                    }
                }
            }

            // Step 3: Batch Enable GCP APIs & Firebase SHA Sync
            WizardCard(
                title = "Step 3: API 承認・有効化 & Firebase + 署名 SHA 同期",
                icon = Icons.Rounded.Api,
                completed = wizardState.firebaseSynced
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    wizardState.apiItems.forEach { item ->
                        val checked = wizardState.selectedApiNamesToEnable.contains(item.serviceName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleApiSelection(item.serviceName) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = checked, onCheckedChange = { onToggleApiSelection(item.serviceName) })
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, style = MaterialTheme.typography.labelLarge)
                                Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                color = if (item.enabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (item.enabled) "ENABLED" else "未有効",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRefreshApis,
                        enabled = wizardState.oauthAuthorized && wizardState.selectedProjectId.isNotBlank() && !wizardState.isBusy,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("状態再取得")
                    }
                    Button(
                        onClick = onEnableSelectedApis,
                        enabled = wizardState.oauthAuthorized && wizardState.selectedProjectId.isNotBlank() && !wizardState.isBusy,
                        modifier = Modifier.weight(1.4f).height(48.dp).testTag("wizard_enable_apis_button")
                    ) {
                        Text("選択 API 一括有効化")
                    }
                }

                Button(
                    onClick = onSyncFirebase,
                    enabled = wizardState.oauthAuthorized && wizardState.selectedProjectId.isNotBlank() && !wizardState.isBusy,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("wizard_sync_firebase_button")
                ) {
                    Icon(Icons.Rounded.CloudDone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Firebase 有効化・APK 署名 SHA 登録・設定同期")
                }
            }

            // Step 4: Cloudflare / GitHub / Termux Linking
            WizardCard(
                title = "Step 4: 全ワークフロー連携 (Cloudflare / GitHub / Termux)",
                icon = Icons.Rounded.RocketLaunch
            ) {
                OutlinedTextField(
                    value = gatewayUrlInput,
                    onValueChange = { gatewayUrlInput = it },
                    label = { Text("Cloudflare Worker Gateway URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("wizard_gateway_url_input")
                )
                OutlinedTextField(
                    value = githubRepoInput,
                    onValueChange = { githubRepoInput = it },
                    label = { Text("GitHub Repository (owner/repo)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("wizard_github_repo_input")
                )
                OutlinedTextField(
                    value = termuxUrlInput,
                    onValueChange = { termuxUrlInput = it },
                    label = { Text("Termux AntiGravityAgent URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("wizard_termux_url_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onVerifyCloudflareGateway(gatewayUrlInput) },
                        enabled = !wizardState.isBusy,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) { Text("CF疎通", maxLines = 1) }
                    OutlinedButton(
                        onClick = onVerifyGitHubSso,
                        enabled = !wizardState.isBusy,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) { Text("GH確認", maxLines = 1) }
                    OutlinedButton(
                        onClick = { onVerifyTermux(termuxUrlInput) },
                        enabled = !wizardState.isBusy,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) { Text("Termux", maxLines = 1) }
                }

                Button(
                    onClick = {
                        onApplyAllWorkflows(gatewayUrlInput, githubRepoInput, termuxUrlInput)
                    },
                    enabled = !wizardState.isBusy,
                    modifier = Modifier.fillMaxWidth().height(54.dp).testTag("wizard_apply_all_button")
                ) {
                    Text("全ワークフローへ反映して一括同期", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun WizardCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    completed: Boolean = false,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (completed) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = "完了",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            content()
        }
    }
}

private fun copyText(context: Context, label: String, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
}
