package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.auth.extractGcpAccessTokenFromIntent
import com.example.auth.requestGcpOAuthAuthorization
import com.example.security.AppSignatureHelper
import com.example.ui.viewmodel.AuthenticationUiState
import com.example.ui.viewmodel.AuthenticationViewModel
import com.example.ui.viewmodel.LoginStage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    onAuthSuccess: () -> Unit,
    isFirebaseReady: Boolean = true,
    onFirebaseInitialized: () -> Unit = {},
    authViewModel: AuthenticationViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                AuthenticationViewModel(appContext = app)
            }
        }
    )
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val signingDiag = remember(context) { runCatching { AppSignatureHelper.readDiagnostics(context) }.getOrNull() }

    val oauthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            extractGcpAccessTokenFromIntent(context, result.data)
                .onSuccess { token -> authViewModel.onStage1OAuthTokenAcquired(token) }
                .onFailure { err ->
                    authViewModel.onStage1OAuthFailed(err.localizedMessage ?: "Google OAuth トークン取得に失敗しました")
                }
        } else {
            authViewModel.onStage1OAuthFailed("Google OAuth 承認がキャンセルされました")
        }
    }

    val launchStage1OAuth: () -> Unit = remember(activity) {
        {
            if (activity == null) {
                authViewModel.onStage1OAuthFailed("Google OAuth には Activity コンテキストが必要です")
            } else {
                requestGcpOAuthAuthorization(
                    activity = activity,
                    includeUserInfoScopes = true,
                    onAccessToken = { token -> authViewModel.onStage1OAuthTokenAcquired(token) },
                    onResolutionRequired = { senderReq -> oauthLauncher.launch(senderReq) },
                    onError = { msg -> authViewModel.onStage1OAuthFailed(msg) }
                )
            }
        }
    }

    LaunchedEffect(isFirebaseReady) {
        authViewModel.refreshConfigurationState()
        authViewModel.startStartupChecks(
            credentialManager = credentialManager,
            onFirebaseInitialized = onFirebaseInitialized,
            onAuthSuccess = onAuthSuccess
        )
    }

    BackHandler(enabled = uiState.loginStage == LoginStage.SELECT_GCP_PROJECT && !uiState.isLoading) {
        authViewModel.backToStage1OAuth()
    }

    val workerLabel = remember(uiState.gatewayUrl) {
        uiState.gatewayUrl.removePrefix("https://").removePrefix("http://").trimEnd('/').ifBlank { "Cloudflare Worker" }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .safeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeroOrbitHeader()

            StepProgressCard(stage = uiState.loginStage)

            StatusBadgeSurface(uiState = uiState)

            if (!uiState.statusMessage.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_status_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(
                            text = uiState.statusMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (!uiState.errorMessage.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_error_banner")
                ) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            if (uiState.loginStage == LoginStage.GOOGLE_OAUTH) {
                Stage1GoogleOAuthSection(
                    uiState = uiState,
                    workerLabel = workerLabel,
                    onSignInClick = {
                        authViewModel.onGoogleSignInClicked(
                            activity = activity,
                            credentialManager = credentialManager,
                            onFirebaseInitialized = onFirebaseInitialized,
                            onAuthSuccess = onAuthSuccess,
                            onLaunchStage1OAuth = launchStage1OAuth
                        )
                    },
                    onConnectGateway = { gwUrl ->
                        authViewModel.connectGatewayFromAdvancedOptions(
                            gatewayUrl = gwUrl,
                            activity = activity,
                            credentialManager = credentialManager,
                            onFirebaseInitialized = onFirebaseInitialized,
                            onAuthSuccess = onAuthSuccess
                        )
                    },
                    onSubmitManualOAuthToken = { token -> authViewModel.onStage1OAuthTokenAcquired(token) }
                )
            } else {
                Stage2GcpProjectSection(
                    uiState = uiState,
                    sha1Summary = signingDiag?.sha1.orEmpty(),
                    onRefreshProjects = { authViewModel.refreshStage2Projects() },
                    onSelectProject = { authViewModel.selectStage2Project(it) },
                    onCreateProject = { id, name -> authViewModel.createStage2ProjectAndSelect(id, name) },
                    onProvisionAndContinue = {
                        authViewModel.provisionSelectedProjectAndSignIn(
                            activity = activity,
                            credentialManager = credentialManager,
                            onFirebaseInitialized = onFirebaseInitialized,
                            onAuthSuccess = onAuthSuccess
                        )
                    },
                    onBackToStage1 = { authViewModel.backToStage1OAuth() }
                )
            }
        }
    }
}

@Composable
private fun HeroOrbitHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_hero_orbit_1795052714347),
            contentDescription = "AntiGravity cloud workflow constellation",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(0xCC080C16))))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ZERO-JSON DYNAMIC SSO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "AntiGravity Hub",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StepProgressCard(stage: LoginStage) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepChip("1. Google OAuth", active = stage == LoginStage.GOOGLE_OAUTH, completed = stage == LoginStage.SELECT_GCP_PROJECT)
            Text("→", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            StepChip("2. GCP 選択 & Firebase 自動初期化", active = stage == LoginStage.SELECT_GCP_PROJECT, completed = false)
        }
    }
}

@Composable
private fun StepChip(label: String, active: Boolean, completed: Boolean) {
    val bg = when {
        completed -> MaterialTheme.colorScheme.secondaryContainer
        active -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val fg = when {
        completed -> MaterialTheme.colorScheme.onSecondaryContainer
        active -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = if (completed) "✓ $label" else label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun StatusBadgeSurface(uiState: AuthenticationUiState) {
    val ready = uiState.isFirebaseReady && uiState.isWebClientIdValid
    Surface(
        color = if (ready) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_web_client_status")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (ready) Icons.Rounded.CheckCircle else Icons.Rounded.VerifiedUser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = uiState.webClientIdStatusLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Stage1GoogleOAuthSection(
    uiState: AuthenticationUiState,
    workerLabel: String,
    onSignInClick: () -> Unit,
    onConnectGateway: (String) -> Unit,
    onSubmitManualOAuthToken: (String) -> Unit
) {
    var advancedExpanded by rememberSaveable { mutableStateOf(false) }
    var gatewayInput by rememberSaveable(uiState.gatewayUrl) { mutableStateOf(uiState.gatewayUrl) }
    var manualOAuthInput by rememberSaveable { mutableStateOf("") }

    Text(
        text = "google-services.json は不要です。最初に Google アカウントで OAuth 認証を行い、次に GCP プロジェクトを選択すると Firebase 設定を自動取得してアプリを初期化します。",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "統合ワークフロー構成", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StackPill(Icons.Rounded.AutoAwesome, "Google OAuth & SSO")
                StackPill(Icons.Rounded.Security, "GCP & Firebase Management API")
                StackPill(Icons.Rounded.CloudDone, workerLabel)
                StackPill(Icons.Rounded.Code, "GitHub Actions")
                StackPill(Icons.Rounded.Terminal, "Termux CLI")
            }
        }
    }

    Button(
        onClick = onSignInClick,
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("google_sign_in_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.5.dp)
        } else {
            Text(
                text = "Google アカウントでログイン",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }

    OutlinedButton(
        onClick = { advancedExpanded = !advancedExpanded },
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("auth_advanced_options_button"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(Icons.Rounded.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("詳細接続オプション (Gateway / 手動 OAuth トークン)")
        Spacer(modifier = Modifier.width(6.dp))
        Icon(if (advancedExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, contentDescription = null)
    }

    AnimatedVisibility(visible = advancedExpanded) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Cloudflare Gateway / 手動 OAuth 連携", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = gatewayInput,
                    onValueChange = { gatewayInput = it },
                    label = { Text("Gateway Base URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("login_gateway_url_input")
                )
                OutlinedButton(
                    onClick = { onConnectGateway(gatewayInput) },
                    enabled = !uiState.isLoading && gatewayInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_gateway_connect_button")
                ) {
                    Text("Gateway (/v1/client-config) 経由で初期化")
                }
                OutlinedTextField(
                    value = manualOAuthInput,
                    onValueChange = { manualOAuthInput = it },
                    label = { Text("手動 Google OAuth Token (任意: ya29...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("login_manual_oauth_input")
                )
                OutlinedButton(
                    onClick = { onSubmitManualOAuthToken(manualOAuthInput) },
                    enabled = !uiState.isLoading && manualOAuthInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_manual_oauth_submit")
                ) {
                    Text("このトークンで Stage 2 (GCP プロジェクト選択) へ")
                }
            }
        }
    }
}

@Composable
private fun Stage2GcpProjectSection(
    uiState: AuthenticationUiState,
    sha1Summary: String,
    onRefreshProjects: () -> Unit,
    onSelectProject: (String) -> Unit,
    onCreateProject: (String, String) -> Unit,
    onProvisionAndContinue: () -> Unit,
    onBackToStage1: () -> Unit
) {
    var showCreateForm by rememberSaveable { mutableStateOf(false) }
    var newProjectId by rememberSaveable { mutableStateOf("") }
    var newProjectName by rememberSaveable { mutableStateOf("AntiGravity Hub") }
    var manualProjectId by rememberSaveable(uiState.selectedGcpProjectId) { mutableStateOf(uiState.selectedGcpProjectId) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.oauthUserEmail.ifBlank { "Google OAuth (cloud-platform) 承認済み" },
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onRefreshProjects,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.minimumInteractiveComponentSize().testTag("login_refresh_projects_button")
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "GCP プロジェクト一覧を再取得")
                }
            }
            if (sha1Summary.isNotBlank()) {
                Text(
                    text = "自動登録される APK SHA-1: ${sha1Summary.take(26)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("接続する GCP プロジェクトを選択", style = MaterialTheme.typography.titleMedium)
            }

            if (uiState.gcpProjects.isEmpty()) {
                Text(
                    text = "アクティブなプロジェクトが見つからない場合は、下から新規作成するか Project ID を直接指定できます。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = manualProjectId,
                    onValueChange = {
                        manualProjectId = it
                        onSelectProject(it)
                    },
                    label = { Text("GCP Project ID (直接入力)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("login_manual_project_id_input")
                )
            } else {
                uiState.gcpProjects.forEach { proj ->
                    val selected = uiState.selectedGcpProjectId == proj.projectId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !uiState.isLoading) { onSelectProject(proj.projectId) }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("login_gcp_project_${proj.projectId}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selected, onClick = { onSelectProject(proj.projectId) })
                        Column(modifier = Modifier.weight(1f)) {
                            Text(proj.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(proj.projectId, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            TextButton(
                onClick = { showCreateForm = !showCreateForm },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("login_toggle_create_project")
            ) {
                Icon(Icons.Rounded.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (showCreateForm) "新規プロジェクト作成を閉じる" else "新規 GCP プロジェクトを作成 (Spark 無料枠)")
            }

            AnimatedVisibility(visible = showCreateForm) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        label = { Text("表示名 (例: AntiGravity Hub)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("login_new_project_name_input")
                    )
                    OutlinedTextField(
                        value = newProjectId,
                        onValueChange = { newProjectId = it.lowercase() },
                        label = { Text("Project ID (英小文字・数字・ハイフン 6〜30文字)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("login_new_project_id_input")
                    )
                    OutlinedButton(
                        onClick = { onCreateProject(newProjectId, newProjectName) },
                        enabled = !uiState.isLoading && newProjectId.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_create_project_button")
                    ) {
                        Text("GCP プロジェクトを作成して選択")
                    }
                }
            }
        }
    }

    Button(
        onClick = onProvisionAndContinue,
        enabled = !uiState.isLoading && uiState.selectedGcpProjectId.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("login_provision_firebase_button"),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.5.dp)
        } else {
            Text(
                text = "選択プロジェクトで Firebase を自動設定して続行",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }

    OutlinedButton(
        onClick = onBackToStage1,
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("login_back_to_oauth_button"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("別の Google アカウントに戻す")
    }
}

@Composable
private fun StackPill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
