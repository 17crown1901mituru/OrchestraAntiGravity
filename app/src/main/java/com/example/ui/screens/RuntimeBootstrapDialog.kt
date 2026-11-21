package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CloudBridgeClient
import com.example.data.RuntimeCloudBootstrapConfig
import com.example.data.RuntimeCloudBootstrapStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RuntimeBootstrapDialog(
    initialNotice: String? = null,
    onDismiss: () -> Unit,
    onBootstrapped: (RuntimeCloudBootstrapConfig) -> Unit
) {
    val context = LocalContext.current
    val store = remember(context) { RuntimeCloudBootstrapStore(context) }
    val saved = remember(context) { store.load() }
    val bridgeClient = remember { CloudBridgeClient() }
    val scope = rememberCoroutineScope()

    var gatewayUrl by rememberSaveable { mutableStateOf(saved.gatewayUrl.ifBlank { "https://gc2223.com" }) }
    var showManualSection by rememberSaveable { mutableStateOf(false) }
    var rawJson by rememberSaveable { mutableStateOf("") }
    var projectId by rememberSaveable { mutableStateOf(saved.firebaseProjectId) }
    var appId by rememberSaveable { mutableStateOf(saved.firebaseAppId) }
    var apiKey by rememberSaveable { mutableStateOf(saved.firebaseApiKey) }
    var webClientId by rememberSaveable { mutableStateOf(saved.firebaseWebClientId) }
    var dbId by rememberSaveable { mutableStateOf(saved.firestoreDatabaseId.ifBlank { "(default)" }) }
    var defaultRepo by rememberSaveable { mutableStateOf(saved.defaultGithubRepo) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember(initialNotice) { mutableStateOf(initialNotice) }

    fun saveAndFinish(config: RuntimeCloudBootstrapConfig) {
        val saveRes = store.save(config)
        if (saveRes.isFailure) {
            errorMessage = "設定の保存に失敗しました: ${saveRes.exceptionOrNull()?.localizedMessage}"
            return
        }
        val normalized = saveRes.getOrThrow()
        val initialized = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(context)
        if (!initialized) {
            errorMessage = "Firebase を初期化できませんでした。Project ID / App ID / API Key を確認してください。"
            return
        }
        if (normalized.firebaseWebClientId.isBlank()) {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            val hasResClient = resId != 0 && runCatching { context.getString(resId).isNotBlank() }.getOrDefault(false)
            if (!hasResClient) {
                errorMessage = "OAuth Web Client ID が未入力です。Google ログイン用に Web Client ID (client_type: 3) を設定してください。"
                return
            }
        }
        onBootstrapped(normalized)
    }

    fun applyParsedJson(jsonText: String) {
        RuntimeCloudBootstrapStore.parseGoogleServicesJson(
            rawJson = jsonText,
            targetPackageName = context.packageName,
            existingGatewayUrl = gatewayUrl
        ).onSuccess { parsed ->
            val merged = parsed.copy(
                firestoreDatabaseId = dbId.ifBlank { "(default)" },
                defaultGithubRepo = defaultRepo.trim()
            )
            projectId = merged.firebaseProjectId
            appId = merged.firebaseAppId
            apiKey = merged.firebaseApiKey
            webClientId = merged.firebaseWebClientId
            saveAndFinish(merged)
        }.onFailure { err ->
            errorMessage = err.localizedMessage ?: "google-services.json の解析に失敗しました"
        }
    }

    val openJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        errorMessage = null
        isLoading = true
        scope.launch {
            val readRes = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: throw IllegalStateException("ファイルを開けませんでした")
                }
            }
            isLoading = false
            readRes.onSuccess { content ->
                rawJson = content
                applyParsedJson(content)
            }.onFailure { err ->
                errorMessage = err.localizedMessage ?: "ファイル読み込みに失敗しました"
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .widthIn(max = 560.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "初期接続セットアップ",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("bootstrap_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "閉じる"
                        )
                    }
                }

                Text(
                    text = "APK ソースに秘密情報・Firebase 設定を埋め込まないため、初回のみ端末上の google-services.json を読み込むか、`/v1/client-config` 対応の Cloudflare Worker URL を指定してください。保存後すぐに Google ログインへ進みます。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // 1. Pick google-services.json directly via Android SAF (zero storage permissions)
                Button(
                    onClick = { openJsonLauncher.launch(arrayOf("application/json", "*/*")) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("bootstrap_pick_json_file_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("google-services.json ファイルを選択して Google ログイン")
                }

                // 2. Fetch from Gateway /v1/client-config
                OutlinedTextField(
                    value = gatewayUrl,
                    onValueChange = { gatewayUrl = it },
                    label = { Text("Cloudflare Worker Gateway URL") },
                    placeholder = { Text("https://gc2223.com") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bootstrap_gateway_url_input")
                )

                OutlinedButton(
                    onClick = {
                        val target = gatewayUrl.trim()
                        if (target.isBlank()) {
                            errorMessage = "Gateway URL を入力してください"
                            return@OutlinedButton
                        }
                        errorMessage = null
                        isLoading = true
                        scope.launch {
                            val res = bridgeClient.fetchClientBootstrapConfig(target)
                            isLoading = false
                            res.onSuccess { cfg ->
                                saveAndFinish(cfg.copy(gatewayUrl = target.trimEnd('/')))
                            }.onFailure { err ->
                                val rawMsg = err.localizedMessage.orEmpty()
                                errorMessage = if (rawMsg.contains("404")) {
                                    "Gateway の /v1/client-config が 404 です（最新の Worker スクリプト未デプロイ）。上のボタンから google-services.json を選ぶとすぐ Google ログインできます。"
                                } else {
                                    rawMsg.ifBlank { "Gateway からの取得に失敗しました" }
                                }
                            }
                        }
                    },
                    enabled = !isLoading && gatewayUrl.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("bootstrap_fetch_gateway_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CloudSync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gateway (/v1/client-config) から取得")
                }

                // 3. Expandable paste/manual fallback
                TextButton(
                    onClick = { showManualSection = !showManualSection },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("bootstrap_toggle_manual_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "JSON テキスト貼付 / 手動入力",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (showManualSection) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (showManualSection) "閉じる" else "開く"
                    )
                }

                AnimatedVisibility(visible = showManualSection) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = rawJson,
                            onValueChange = { rawJson = it },
                            label = { Text("google-services.json テキスト") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bootstrap_json_input")
                        )
                        OutlinedButton(
                            onClick = {
                                errorMessage = null
                                applyParsedJson(rawJson)
                            },
                            enabled = !isLoading && rawJson.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("bootstrap_parse_json_button")
                        ) {
                            Text("貼付 JSON を解析して接続")
                        }

                        OutlinedTextField(
                            value = projectId,
                            onValueChange = { projectId = it },
                            label = { Text("Firebase Project ID") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bootstrap_project_id_input")
                        )
                        OutlinedTextField(
                            value = appId,
                            onValueChange = { appId = it },
                            label = { Text("Firebase App ID") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bootstrap_app_id_input")
                        )
                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            label = { Text("Firebase Web API Key") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bootstrap_api_key_input")
                        )
                        OutlinedTextField(
                            value = webClientId,
                            onValueChange = { webClientId = it },
                            label = { Text("OAuth Web Client ID") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bootstrap_web_client_id_input")
                        )
                        OutlinedTextField(
                            value = dbId,
                            onValueChange = { dbId = it },
                            label = { Text("Firestore Database ID") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bootstrap_db_id_input")
                        )
                        OutlinedTextField(
                            value = defaultRepo,
                            onValueChange = { defaultRepo = it },
                            label = { Text("デフォルト GitHub Repo (owner/repo)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bootstrap_github_repo_input")
                        )
                        Button(
                            onClick = {
                                errorMessage = null
                                val manualCfg = RuntimeCloudBootstrapConfig(
                                    gatewayUrl = gatewayUrl,
                                    firebaseProjectId = projectId,
                                    firebaseAppId = appId,
                                    firebaseApiKey = apiKey,
                                    firebaseWebClientId = webClientId,
                                    firestoreDatabaseId = dbId,
                                    defaultGithubRepo = defaultRepo
                                )
                                if (!manualCfg.hasFirebaseRuntimeParams()) {
                                    errorMessage = "Project ID / App ID / API Key を入力してください"
                                    return@Button
                                }
                                saveAndFinish(manualCfg)
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("bootstrap_save_manual_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("手動設定を保存して接続")
                        }
                    }
                }
            }
        }
    }
}
