package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.viewmodel.AuthenticationUiState
import com.example.ui.viewmodel.AuthenticationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    onAuthSuccess: () -> Unit,
    isFirebaseReady: Boolean = false,
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

    val oauthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            extractGcpAccessTokenFromIntent(context, result.data)
                .onSuccess { token ->
                    authViewModel.onStage1OAuthTokenAcquired(token, onAuthSuccess)
                }
                .onFailure { err ->
                    authViewModel.onStage1OAuthFailed(err.localizedMessage ?: "Google OAuth トークン取得に失敗しました")
                }
        } else {
            authViewModel.onStage1OAuthFailed(
                "Google OAuth 承認が中断されました（GCP Console 側の Android OAuth クライアント / SHA-1 未登録時は「ハブを開始」からそのまま MCP・Termux・GitHub 連携を利用できます）"
            )
        }
    }

    val launchStage1OAuth: () -> Unit = remember(activity, onAuthSuccess) {
        {
            if (activity == null) {
                authViewModel.onStage1OAuthFailed("Google OAuth には Activity コンテキストが必要です")
            } else {
                requestGcpOAuthAuthorization(
                    activity = activity,
                    includeUserInfoScopes = true,
                    onAccessToken = { token ->
                        authViewModel.onStage1OAuthTokenAcquired(token, onAuthSuccess)
                    },
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

    val workerLabel = remember(uiState.gatewayUrl) {
        uiState.gatewayUrl.removePrefix("https://").removePrefix("http://").trimEnd('/').ifBlank { "Cloudflare MCP" }
    }

    var advancedExpanded by rememberSaveable { mutableStateOf(false) }
    var gatewayInput by rememberSaveable(uiState.gatewayUrl) { mutableStateOf(uiState.gatewayUrl) }
    var manualOAuthInput by rememberSaveable { mutableStateOf("") }

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

            Text(
                text = "事前 Firebase 設定や google-services.json は不要です。Google アカウント OAuth でそのままログインし、端末内の暗号化ストレージ・ステートレス MCP 連携・オンデマンド GCP / Firebase 構成を利用できます。",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "統合ワークフロー構成", style = MaterialTheme.typography.titleMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StackPill(Icons.Rounded.AutoAwesome, "Google OAuth (subベース)")
                        StackPill(Icons.Rounded.Security, "オンデマンド GCP / Firebase")
                        StackPill(Icons.Rounded.CloudDone, "$workerLabel MCP")
                        StackPill(Icons.Rounded.Code, "GitHub Actions")
                        StackPill(Icons.Rounded.Terminal, "Termux CLI")
                    }
                }
            }

            Button(
                onClick = {
                    authViewModel.onGoogleSignInClicked(
                        activity = activity,
                        credentialManager = credentialManager,
                        onFirebaseInitialized = onFirebaseInitialized,
                        onAuthSuccess = onAuthSuccess,
                        onLaunchStage1OAuth = launchStage1OAuth
                    )
                },
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Google アカウントでログイン",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    authViewModel.enterHubInStatelessMode(
                        accountHint = uiState.oauthUserEmail,
                        onAuthSuccess = onAuthSuccess
                    )
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("enter_hub_stateless_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "ハブを開始 (GCP OAuth なしで続行)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
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
                Text("詳細オプション (MCP Gateway / 手動 OAuth トークン)")
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
                        Text("MCP ゲートウェイ / 手動 OAuth 連携", style = MaterialTheme.typography.titleSmall)
                        OutlinedTextField(
                            value = gatewayInput,
                            onValueChange = { gatewayInput = it },
                            label = { Text("Stateless MCP Gateway URL") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("login_gateway_url_input")
                        )
                        OutlinedButton(
                            onClick = {
                                authViewModel.connectGatewayFromAdvancedOptions(
                                    gatewayUrl = gatewayInput,
                                    activity = activity,
                                    credentialManager = credentialManager,
                                    onFirebaseInitialized = onFirebaseInitialized,
                                    onAuthSuccess = onAuthSuccess
                                )
                            },
                            enabled = !uiState.isLoading && gatewayInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_gateway_connect_button")
                        ) {
                            Text("Gateway 経由で SSO ログイン")
                        }
                        OutlinedTextField(
                            value = manualOAuthInput,
                            onValueChange = { manualOAuthInput = it },
                            label = { Text("手動 Google OAuth Token (任意: ya29...)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("login_manual_oauth_input")
                        )
                        OutlinedButton(
                            onClick = { authViewModel.onStage1OAuthTokenAcquired(manualOAuthInput, onAuthSuccess) },
                            enabled = !uiState.isLoading && manualOAuthInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_manual_oauth_submit")
                        ) {
                            Text("OAuth トークンでログイン")
                        }
                    }
                }
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
                    text = "STATELESS OAUTH + ON-DEMAND CLOUD",
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
private fun StatusBadgeSurface(uiState: AuthenticationUiState) {
    val ready = uiState.authenticatedUid != null || (uiState.isFirebaseReady && uiState.isWebClientIdValid)
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
