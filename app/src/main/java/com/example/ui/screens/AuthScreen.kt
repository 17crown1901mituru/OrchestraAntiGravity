package com.example.ui.screens

import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.lifecycleScope
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.onGoogleSignInClicked
import com.example.auth.resolveDefaultWebClientId
import com.example.data.CloudBridgeClient
import com.example.data.RuntimeCloudBootstrapStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    isFirebaseReady: Boolean = true,
    onFirebaseInitialized: () -> Unit = {}
) {
    val context = LocalContext.current
    val fallbackScope = rememberCoroutineScope()
    val authScope = (context as? ComponentActivity)?.lifecycleScope ?: fallbackScope
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val bootstrapStore = remember(context) { RuntimeCloudBootstrapStore(context) }
    val bridgeClient = remember { CloudBridgeClient() }

    var bootstrapConfig by remember(context) { mutableStateOf(bootstrapStore.load()) }
    val workerLabel = remember(bootstrapConfig.gatewayUrl) {
        bootstrapConfig.gatewayUrl
            .removePrefix("https://")
            .removePrefix("http://")
            .trimEnd('/')
            .ifBlank { "Cloudflare Worker" }
    }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBootstrapDialog by remember { mutableStateOf(false) }
    var bootstrapNotice by remember { mutableStateOf<String?>(null) }
    var pendingSignInAfterBootstrap by remember { mutableStateOf(false) }

    fun launchGoogleSignIn() {
        errorMessage = null
        isLoading = true
        onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                isLoading = false
                onAuthSuccess()
            },
            onAuthError = { msg ->
                isLoading = false
                errorMessage = msg
            },
            scope = authScope,
            onAuthCancelled = {
                isLoading = false
            }
        )
    }

    LaunchedEffect(isFirebaseReady) {
        // Let initial Compose frame commit cleanly
        delay(120L)
        if (isFirebaseReady && resolveDefaultWebClientId(context) != null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = onAuthSuccess,
                onUnauthenticated = {},
                scope = authScope
            )
        } else {
            // Silently attempt runtime bootstrap in background without showing error banners on launch
            val targetGateway = bootstrapConfig.gatewayUrl.ifBlank { "https://gc2223.com" }
            bridgeClient.fetchClientBootstrapConfig(targetGateway).onSuccess { cfg ->
                bootstrapStore.save(cfg.copy(gatewayUrl = targetGateway.trimEnd('/')))
                bootstrapConfig = bootstrapStore.load()
                if (RuntimeCloudBootstrapStore.ensureFirebaseInitialized(context)) {
                    onFirebaseInitialized()
                }
            }
        }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC080C16))
                            )
                        )
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
                            text = "ZERO-COST AI ORCHESTRATOR",
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

            Text(
                text = "起動時の Google アカウントログインだけで、Firebase・Cloudflare Worker・GitHub Actions・GCP・Termux を単一 SSO セッションで統合連携します。",
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "統合ワークフロー構成",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StackPill(Icons.Rounded.AutoAwesome, "Google SSO Broker")
                        StackPill(Icons.Rounded.CloudDone, workerLabel)
                        StackPill(Icons.Rounded.Code, "GitHub Actions")
                        StackPill(Icons.Rounded.Security, "GCP $0.10/d Guard")
                        StackPill(Icons.Rounded.Terminal, "Termux CLI")
                    }
                }
            }

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Button(
                onClick = {
                    val readyNow = (isFirebaseReady || RuntimeCloudBootstrapStore.ensureFirebaseInitialized(context)) &&
                        resolveDefaultWebClientId(context) != null
                    if (readyNow) {
                        onFirebaseInitialized()
                        launchGoogleSignIn()
                    } else {
                        errorMessage = null
                        isLoading = true
                        authScope.launch {
                            val targetGateway = bootstrapConfig.gatewayUrl.ifBlank { "https://gc2223.com" }
                            val res = bridgeClient.fetchClientBootstrapConfig(targetGateway)
                            res.onSuccess { cfg ->
                                bootstrapStore.save(cfg.copy(gatewayUrl = targetGateway.trimEnd('/')))
                                bootstrapConfig = bootstrapStore.load()
                                val inited = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(context)
                                if (inited && resolveDefaultWebClientId(context) != null) {
                                    onFirebaseInitialized()
                                    launchGoogleSignIn()
                                } else {
                                    isLoading = false
                                    bootstrapNotice = "Gateway から一部項目を取得しましたが Web Client ID が不足しています。google-services.json を選択または入力してください。"
                                    pendingSignInAfterBootstrap = true
                                    showBootstrapDialog = true
                                }
                            }.onFailure { err ->
                                isLoading = false
                                val rawMsg = err.localizedMessage.orEmpty()
                                bootstrapNotice = if (rawMsg.contains("404")) {
                                    "この APK は秘密情報未埋め込みビルドで、Gateway ($targetGateway/v1/client-config) もまだ更新前 (404) です。端末内の google-services.json を選択するとすぐ Google ログインを開始できます。"
                                } else {
                                    "Gateway ($targetGateway) から自動設定を取得できなかったため、端末内の google-services.json を選択するか接続先を指定してください。"
                                }
                                pendingSignInAfterBootstrap = true
                                showBootstrapDialog = true
                            }
                        }
                    }
                },
                enabled = !isLoading,
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
                if (isLoading) {
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
                    bootstrapNotice = null
                    pendingSignInAfterBootstrap = false
                    showBootstrapDialog = true
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_runtime_config_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CloudSync,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("接続先 / google-services.json 設定")
            }

            Text(
                text = "※ Google アカウントでサインインすると、Android Keystore AES-256-GCM ボルト・専用 Firestore・Cloudflare Worker 経由の GitHub / Cloudflare SSO ブローカーが自動有効化されます（APK への秘密情報埋め込み不要）。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showBootstrapDialog) {
        RuntimeBootstrapDialog(
            initialNotice = bootstrapNotice,
            onDismiss = {
                showBootstrapDialog = false
                pendingSignInAfterBootstrap = false
            },
            onBootstrapped = { updatedCfg ->
                bootstrapConfig = updatedCfg
                showBootstrapDialog = false
                onFirebaseInitialized()
                if (pendingSignInAfterBootstrap) {
                    pendingSignInAfterBootstrap = false
                    launchGoogleSignIn()
                }
            }
        )
    }
}

@Composable
private fun StackPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
