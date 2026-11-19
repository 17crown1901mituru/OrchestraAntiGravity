package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.ExternalCredentials
import com.example.model.VaultMetadata
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userEmail: String,
    credentials: ExternalCredentials,
    vaultMetadata: VaultMetadata,
    isBusy: Boolean,
    verificationStatus: String?,
    snackbarHostState: SnackbarHostState,
    onSaveCredentials: (ExternalCredentials) -> Unit,
    onClearVault: () -> Unit,
    onVerifyGitHub: (String) -> Unit,
    onVerifyCloudflare: (String) -> Unit,
    onVerifyGc2223: (endpoint: String, bearer: String) -> Unit,
    onVerifyTermux: (agentUrl: String, bearerToken: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    var githubPat by remember(credentials) { mutableStateOf(credentials.githubPat) }
    var githubOwnerRepo by remember(credentials) { mutableStateOf(credentials.githubOwnerRepo) }
    var githubWorkflowFile by remember(credentials) { mutableStateOf(credentials.githubWorkflowFile) }

    var cloudflareApiToken by remember(credentials) { mutableStateOf(credentials.cloudflareApiToken) }
    var cloudflareAccountId by remember(credentials) { mutableStateOf(credentials.cloudflareAccountId) }
    var gc2223Endpoint by remember(credentials) { mutableStateOf(credentials.gc2223Endpoint) }
    var gc2223BearerToken by remember(credentials) { mutableStateOf(credentials.gc2223BearerToken) }

    var gcpProjectId by remember(credentials) { mutableStateOf(credentials.gcpProjectId) }
    var gcpApiKeyOrToken by remember(credentials) { mutableStateOf(credentials.gcpApiKeyOrToken) }
    var gcpBillingAccountId by remember(credentials) { mutableStateOf(credentials.gcpBillingAccountId) }

    var termuxAgentUrl by remember(credentials) { mutableStateOf(credentials.termuxAgentUrl) }
    var termuxAgentToken by remember(credentials) { mutableStateOf(credentials.termuxAgentToken) }

    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(credentials) {
        githubPat = credentials.githubPat
        githubOwnerRepo = credentials.githubOwnerRepo
        githubWorkflowFile = credentials.githubWorkflowFile
        cloudflareApiToken = credentials.cloudflareApiToken
        cloudflareAccountId = credentials.cloudflareAccountId
        gc2223Endpoint = credentials.gc2223Endpoint
        gc2223BearerToken = credentials.gc2223BearerToken
        gcpProjectId = credentials.gcpProjectId
        gcpApiKeyOrToken = credentials.gcpApiKeyOrToken
        gcpBillingAccountId = credentials.gcpBillingAccountId
        termuxAgentUrl = credentials.termuxAgentUrl
        termuxAgentToken = credentials.termuxAgentToken
    }

    val updatedTimestampText = remember(vaultMetadata.updatedAtEpochMs) {
        if (vaultMetadata.updatedAtEpochMs <= 0L) {
            "未保存"
        } else {
            SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                .format(Date(vaultMetadata.updatedAtEpochMs))
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "セキュア設定",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "暗号化ローカルボルト (${vaultMetadata.linkedEmail.ifBlank { userEmail }})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isBusy) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                // Linked Firebase Auth & AES-256-GCM status card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
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
                                Icon(
                                    imageVector = Icons.Rounded.VerifiedUser,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Firebase Auth 連携暗号化ボルト",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Surface(
                                color = if (vaultMetadata.hasStoredSecrets) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (vaultMetadata.hasStoredSecrets) "暗号化済み" else "未保存",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (vaultMetadata.hasStoredSecrets) {
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "アカウント: ${vaultMetadata.linkedEmail.ifBlank { userEmail.ifBlank { "Google Account" } }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Firebase UID: ${vaultMetadata.linkedUid.ifBlank { "-" }}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "暗号方式: ${vaultMetadata.cipherAlgorithm} • 最終保存: $updatedTimestampText",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "すべての API キーと PAT は、Firebase UID ごとに生成された Android Keystore 鍵で AES-256-GCM 暗号化され、端末内のみに保存されます。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!verificationStatus.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_verification_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = verificationStatus,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // GitHub Credentials Card
                SettingsSectionCard(
                    title = "GitHub (Actions & Repo)",
                    icon = Icons.Rounded.Code
                ) {
                    SecretTextField(
                        value = githubPat,
                        onValueChange = { githubPat = it },
                        label = "GitHub PAT (repo, workflow)",
                        placeholder = "ghp_... / github_pat_...",
                        testTag = "settings_github_pat_input"
                    )
                    OutlinedTextField(
                        value = githubOwnerRepo,
                        onValueChange = { githubOwnerRepo = it },
                        label = { Text("GitHub リポジトリ (owner/repo)") },
                        placeholder = { Text("17crown1901mituru/OrchestraAntiGravity") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_github_repo_input")
                    )
                    OutlinedTextField(
                        value = githubWorkflowFile,
                        onValueChange = { githubWorkflowFile = it },
                        label = { Text("Actions ワークフローファイル") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_github_workflow_input")
                    )
                    OutlinedButton(
                        onClick = { onVerifyGitHub(githubPat) },
                        enabled = !isBusy && githubPat.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_verify_github_button")
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("GitHub PAT 疎通確認")
                    }
                }

                // Cloudflare & gc2223.com Credentials Card
                SettingsSectionCard(
                    title = "Cloudflare & gc2223.com",
                    icon = Icons.Rounded.Cloud
                ) {
                    SecretTextField(
                        value = cloudflareApiToken,
                        onValueChange = { cloudflareApiToken = it },
                        label = "Cloudflare API Token",
                        placeholder = "Workers Edit / AI Token",
                        testTag = "settings_cloudflare_token_input"
                    )
                    OutlinedTextField(
                        value = cloudflareAccountId,
                        onValueChange = { cloudflareAccountId = it },
                        label = { Text("Cloudflare Account ID") },
                        placeholder = { Text("32文字の Account ID") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_cloudflare_account_input")
                    )
                    OutlinedTextField(
                        value = gc2223Endpoint,
                        onValueChange = { gc2223Endpoint = it },
                        label = { Text("gc2223.com Gateway URL") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_gc2223_endpoint_input")
                    )
                    SecretTextField(
                        value = gc2223BearerToken,
                        onValueChange = { gc2223BearerToken = it },
                        label = "Worker AGENT_BEARER_TOKEN",
                        placeholder = "Worker シークレットトークン",
                        testTag = "settings_gc2223_bearer_input"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onVerifyCloudflare(cloudflareApiToken) },
                            enabled = !isBusy && cloudflareApiToken.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("settings_verify_cloudflare_button")
                        ) {
                            Text("CF Token確認")
                        }
                        OutlinedButton(
                            onClick = { onVerifyGc2223(gc2223Endpoint, gc2223BearerToken) },
                            enabled = !isBusy && gc2223Endpoint.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("settings_verify_gc2223_button")
                        ) {
                            Text("/health 疎通")
                        }
                    }
                }

                // GCP Credentials Card
                SettingsSectionCard(
                    title = "GCP (Always Free & Budget)",
                    icon = Icons.Rounded.Security
                ) {
                    OutlinedTextField(
                        value = gcpProjectId,
                        onValueChange = { gcpProjectId = it },
                        label = { Text("GCP Project ID") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_gcp_project_input")
                    )
                    SecretTextField(
                        value = gcpApiKeyOrToken,
                        onValueChange = { gcpApiKeyOrToken = it },
                        label = "GCP API Key / Service Token",
                        placeholder = "Cloud Billing / Run API 用キー",
                        testTag = "settings_gcp_token_input"
                    )
                    OutlinedTextField(
                        value = gcpBillingAccountId,
                        onValueChange = { gcpBillingAccountId = it },
                        label = { Text("GCP Billing Account ID") },
                        placeholder = { Text("XXXXXX-XXXXXX-XXXXXX") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_gcp_billing_input")
                    )
                }

                // Termux Local AntiGravityAgent CLI Card
                SettingsSectionCard(
                    title = "Termux AntiGravityAgent CLI (ローカル)",
                    icon = Icons.Rounded.Terminal
                ) {
                    OutlinedTextField(
                        value = termuxAgentUrl,
                        onValueChange = { termuxAgentUrl = it },
                        label = { Text("Termux Daemon URL (例: http://127.0.0.1:8787)") },
                        placeholder = { Text("http://127.0.0.1:8787") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_termux_url_input")
                    )
                    SecretTextField(
                        value = termuxAgentToken,
                        onValueChange = { termuxAgentToken = it },
                        label = "ローカル Bearer Token (任意)",
                        placeholder = "ANTIGRAVITY_LOCAL_TOKEN",
                        testTag = "settings_termux_token_input"
                    )
                    OutlinedButton(
                        onClick = { onVerifyTermux(termuxAgentUrl, termuxAgentToken) },
                        enabled = !isBusy && termuxAgentUrl.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_verify_termux_button")
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Termux /status 疎通確認")
                    }
                }

                // Save and Clear Actions
                Button(
                    onClick = {
                        onSaveCredentials(
                            ExternalCredentials(
                                gc2223Endpoint = gc2223Endpoint,
                                gc2223BearerToken = gc2223BearerToken,
                                cloudflareApiToken = cloudflareApiToken,
                                cloudflareAccountId = cloudflareAccountId,
                                githubPat = githubPat,
                                githubOwnerRepo = githubOwnerRepo,
                                githubWorkflowFile = githubWorkflowFile,
                                gcpProjectId = gcpProjectId,
                                gcpApiKeyOrToken = gcpApiKeyOrToken,
                                gcpBillingAccountId = gcpBillingAccountId,
                                termuxAgentUrl = termuxAgentUrl,
                                termuxAgentToken = termuxAgentToken
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_secure_settings_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AES-256-GCM で暗号化して保存")
                }

                OutlinedButton(
                    onClick = { showClearDialog = true },
                    enabled = vaultMetadata.hasStoredSecrets,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (vaultMetadata.hasStoredSecrets) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("clear_vault_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("保管キーをすべて消去")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("暗号化ボルトを消去しますか？") },
            text = {
                Text(
                    "このアカウント (${vaultMetadata.linkedEmail.ifBlank { userEmail }}) に紐づく GitHub・Cloudflare・GCP の保存済みキーと Keystore 暗号鍵を完全に削除します。"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearVault()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.testTag("confirm_clear_vault_button")
                ) {
                    Text("消去する")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            content()
        }
    }
}

@Composable
private fun SecretTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    testTag: String
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { visible = !visible },
                modifier = Modifier.testTag("${testTag}_toggle")
            ) {
                Icon(
                    imageVector = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                    contentDescription = if (visible) "非表示にする" else "表示する"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
