package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.CloudBridgeClient
import com.example.data.FirestoreRepository
import com.example.data.LocalSecretsStore
import com.example.data.RuntimeCloudBootstrapConfig
import com.example.data.RuntimeCloudBootstrapStore
import com.example.data.SyncSnapshotStore
import com.example.firebase.configureAppCheck
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.MainHubScreen
import com.example.ui.theme.AntiGravityTheme
import com.example.ui.viewmodel.HubViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // App Check MUST run before setContent, ViewModels, or any Firebase call
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            AntiGravityTheme {
                AppNavigation()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth? = null) {
    val context = LocalContext.current
    var bootstrapRevision by remember { mutableIntStateOf(0) }
    var showRuntimeConfigSheet by rememberSaveable { mutableStateOf(false) }

    val resolvedAuth = remember(auth, context, bootstrapRevision) {
        auth ?: runCatching {
            RuntimeCloudBootstrapStore.ensureFirebaseInitialized(context)
            if (FirebaseApp.getApps(context).isNotEmpty()) Firebase.auth else null
        }.getOrNull()
    }

    if (resolvedAuth == null || showRuntimeConfigSheet) {
        RuntimeCloudBootstrapScreen(
            canCancel = resolvedAuth != null,
            onConfigured = {
                showRuntimeConfigSheet = false
                bootstrapRevision++
            },
            onCancel = {
                showRuntimeConfigSheet = false
            }
        )
        return
    }

    val currentUser by resolvedAuth.authStateFlow().collectAsStateWithLifecycle(initialValue = resolvedAuth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(
            onAuthSuccess = {},
            onOpenRuntimeConfig = { showRuntimeConfigSheet = true }
        )
    } else {
        val uid = user.uid
        val email = user.email.orEmpty()
        key(uid) {
            val hubViewModel: HubViewModel = viewModel(
                key = uid,
                factory = viewModelFactory {
                    initializer {
                        val app = checkNotNull(this[APPLICATION_KEY]) {
                            "APPLICATION_KEY missing from CreationExtras"
                        }
                        val databaseId = RuntimeCloudBootstrapStore.resolveDatabaseId(app)
                        val db = FirebaseFirestore.getInstance(databaseId)
                        HubViewModel(
                            appContext = app,
                            repository = FirestoreRepository(db, resolvedAuth),
                            secretsStore = LocalSecretsStore(app),
                            syncSnapshotStore = SyncSnapshotStore(app),
                            bridgeClient = CloudBridgeClient(),
                            currentUserId = uid,
                            userEmail = email
                        )
                    }
                }
            )
            MainHubScreen(
                viewModel = hubViewModel,
                userEmail = email
            )
        }
    }
}

@Composable
private fun RuntimeCloudBootstrapScreen(
    canCancel: Boolean,
    onConfigured: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val store = remember(context) { RuntimeCloudBootstrapStore(context) }
    val savedConfig = remember(context) { store.load() }
    val bridgeClient = remember { CloudBridgeClient() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (canCancel) {
        BackHandler { onCancel() }
    }

    var gatewayUrl by rememberSaveable {
        mutableStateOf(savedConfig.gatewayUrl.ifBlank { "https://gc2223.com" })
    }
    var showAdvancedManual by rememberSaveable { mutableStateOf(false) }
    var rawGoogleServicesJson by rememberSaveable { mutableStateOf("") }
    var firebaseProjectId by rememberSaveable { mutableStateOf(savedConfig.firebaseProjectId) }
    var firebaseAppId by rememberSaveable { mutableStateOf(savedConfig.firebaseAppId) }
    var firebaseApiKey by rememberSaveable { mutableStateOf(savedConfig.firebaseApiKey) }
    var firebaseWebClientId by rememberSaveable { mutableStateOf(savedConfig.firebaseWebClientId) }
    var firestoreDatabaseId by rememberSaveable {
        mutableStateOf(savedConfig.firestoreDatabaseId.ifBlank { "(default)" })
    }
    var defaultGithubRepo by rememberSaveable { mutableStateOf(savedConfig.defaultGithubRepo) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun finalizeBootstrap(config: RuntimeCloudBootstrapConfig) {
        val saveResult = store.save(config)
        if (saveResult.isFailure) {
            errorMessage = "設定の保存に失敗しました: ${saveResult.exceptionOrNull()?.localizedMessage}"
            return
        }
        val initialized = RuntimeCloudBootstrapStore.ensureFirebaseInitialized(context)
        if (!initialized) {
            errorMessage = "Firebase を初期化できませんでした。Project ID / App ID / API Key を確認してください。"
            return
        }
        configureAppCheck(context, (context as? Activity)?.intent)
        onConfigured()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (canCancel) {
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("bootstrap_back_to_signin_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "ログイン画面へ戻る"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Google ログイン画面に戻る")
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.VpnKey,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ランタイム接続設定 (ゼロ埋め込みモード)",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            text = "この APK には Firebase 設定や API トークンをハードコードしていません。Cloudflare Worker ゲートウェイから接続先を取得し、Google アカウントのシングルサインオンで Firebase・Cloudflare・GitHub・GCP を統合連携します。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (!errorMessage.isNullOrBlank()) {
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

                // Option A: Fetch from Cloudflare Worker /v1/client-config
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.CloudSync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cloudflare Worker から自動構成",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            text = "ゲートウェイ URL を指定すると /v1/client-config から Firebase Project・Web Client ID・デフォルト GitHub Repo を安全に読み込みます。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                        Button(
                            onClick = {
                                val target = gatewayUrl.trim()
                                if (target.isBlank()) {
                                    errorMessage = "Gateway URL を入力してください"
                                    return@Button
                                }
                                errorMessage = null
                                isLoading = true
                                scope.launch {
                                    val result = bridgeClient.fetchClientBootstrapConfig(target)
                                    isLoading = false
                                    result.onSuccess { cfg ->
                                        firebaseProjectId = cfg.firebaseProjectId
                                        firebaseAppId = cfg.firebaseAppId
                                        firebaseApiKey = cfg.firebaseApiKey
                                        firebaseWebClientId = cfg.firebaseWebClientId
                                        firestoreDatabaseId = cfg.firestoreDatabaseId
                                        defaultGithubRepo = cfg.defaultGithubRepo
                                        finalizeBootstrap(cfg.copy(gatewayUrl = target.trimEnd('/')))
                                    }.onFailure { err ->
                                        errorMessage = err.localizedMessage ?: "Gateway からの設定取得に失敗しました"
                                    }
                                }
                            },
                            enabled = !isLoading && gatewayUrl.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("bootstrap_fetch_gateway_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CloudSync,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gateway (/v1/client-config) から設定読込")
                        }
                    }
                }

                // Option B: Manual / google-services.json paste fallback
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
                        TextButton(
                            onClick = { showAdvancedManual = !showAdvancedManual },
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
                                text = "google-services.json 貼付 / 手動入力",
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (showAdvancedManual) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = if (showAdvancedManual) "閉じる" else "開く"
                            )
                        }

                        AnimatedVisibility(visible = showAdvancedManual) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = rawGoogleServicesJson,
                                    onValueChange = { rawGoogleServicesJson = it },
                                    label = { Text("google-services.json 内容 (任意)") },
                                    placeholder = { Text("{ \"project_info\": { ... } }") },
                                    minLines = 3,
                                    maxLines = 6,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bootstrap_json_input")
                                )
                                OutlinedButton(
                                    onClick = {
                                        errorMessage = null
                                        val parsed = RuntimeCloudBootstrapStore.parseGoogleServicesJson(
                                            rawJson = rawGoogleServicesJson,
                                            targetPackageName = context.packageName,
                                            existingGatewayUrl = gatewayUrl
                                        )
                                        parsed.onSuccess { cfg ->
                                            val merged = cfg.copy(
                                                firestoreDatabaseId = firestoreDatabaseId.ifBlank { "(default)" },
                                                defaultGithubRepo = defaultGithubRepo.trim()
                                            )
                                            firebaseProjectId = merged.firebaseProjectId
                                            firebaseAppId = merged.firebaseAppId
                                            firebaseApiKey = merged.firebaseApiKey
                                            firebaseWebClientId = merged.firebaseWebClientId
                                            finalizeBootstrap(merged)
                                        }.onFailure { err ->
                                            errorMessage = err.localizedMessage ?: "JSON の解析に失敗しました"
                                        }
                                    },
                                    enabled = !isLoading && rawGoogleServicesJson.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("bootstrap_parse_json_button")
                                ) {
                                    Text("JSON を解析して接続開始")
                                }

                                OutlinedTextField(
                                    value = firebaseProjectId,
                                    onValueChange = { firebaseProjectId = it },
                                    label = { Text("Firebase Project ID") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bootstrap_project_id_input")
                                )
                                OutlinedTextField(
                                    value = firebaseAppId,
                                    onValueChange = { firebaseAppId = it },
                                    label = { Text("Firebase App ID (1:...:android:...)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bootstrap_app_id_input")
                                )
                                OutlinedTextField(
                                    value = firebaseApiKey,
                                    onValueChange = { firebaseApiKey = it },
                                    label = { Text("Firebase Web API Key") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bootstrap_api_key_input")
                                )
                                OutlinedTextField(
                                    value = firebaseWebClientId,
                                    onValueChange = { firebaseWebClientId = it },
                                    label = { Text("OAuth Web Client ID (Google ログイン用)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bootstrap_web_client_id_input")
                                )
                                OutlinedTextField(
                                    value = firestoreDatabaseId,
                                    onValueChange = { firestoreDatabaseId = it },
                                    label = { Text("Firestore Database ID") },
                                    placeholder = { Text("(default)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bootstrap_db_id_input")
                                )
                                OutlinedTextField(
                                    value = defaultGithubRepo,
                                    onValueChange = { defaultGithubRepo = it },
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
                                            firebaseProjectId = firebaseProjectId,
                                            firebaseAppId = firebaseAppId,
                                            firebaseApiKey = firebaseApiKey,
                                            firebaseWebClientId = firebaseWebClientId,
                                            firestoreDatabaseId = firestoreDatabaseId,
                                            defaultGithubRepo = defaultGithubRepo
                                        )
                                        if (!manualCfg.hasFirebaseRuntimeParams()) {
                                            errorMessage = "Project ID / App ID / API Key を入力してください"
                                            return@Button
                                        }
                                        finalizeBootstrap(manualCfg)
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
                                    Text("手動設定を保存して Firebase 初期化")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

