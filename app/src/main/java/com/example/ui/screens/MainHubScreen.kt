package com.example.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.auth.signOutUser
import com.example.ui.components.SelfHealDialog
import com.example.ui.viewmodel.HubViewModel
import kotlinx.coroutines.launch
import java.util.Locale

enum class HubTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    AGENT("AI", Icons.Rounded.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_agent"),
    NODES("状態", Icons.Rounded.Hub, Icons.Outlined.Hub, "tab_nodes"),
    COST("コスト", Icons.Rounded.MonitorHeart, Icons.Outlined.MonitorHeart, "tab_cost"),
    DEPLOY("キット", Icons.Rounded.Terminal, Icons.Outlined.Terminal, "tab_deploy"),
    VAULT("保存", Icons.Rounded.Bookmarks, Icons.Outlined.Bookmarks, "tab_vault")
}

private enum class StatusSubTab(val label: String, val testTag: String) {
    DASHBOARD("同期ダッシュボード", "subtab_status_dashboard"),
    NODES("連携ノード", "subtab_nodes_list")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHubScreen(
    viewModel: HubViewModel,
    userEmail: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val signOutScope = (context as? ComponentActivity)?.lifecycleScope ?: scope
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    var currentTab by rememberSaveable { mutableStateOf(HubTab.AGENT) }
    var statusSubTab by rememberSaveable { mutableStateOf(StatusSubTab.DASHBOARD) }
    var showSettingsScreen by rememberSaveable { mutableStateOf(false) }
    var showSelfHealDialog by rememberSaveable { mutableStateOf(false) }
    var selfHealPrefillLog by rememberSaveable { mutableStateOf<String?>(null) }

    val nodesState by viewModel.nodesState.collectAsStateWithLifecycle()
    val blueprintsState by viewModel.blueprintsState.collectAsStateWithLifecycle()
    val costConfig by viewModel.costConfigState.collectAsStateWithLifecycle()
    val costEntriesState by viewModel.costEntriesState.collectAsStateWithLifecycle()
    val costAlertsState by viewModel.costAlertsState.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val inferenceTarget by viewModel.inferenceTarget.collectAsStateWithLifecycle()
    val attachedSources by viewModel.attachedSources.collectAsStateWithLifecycle()
    val isCrawlingWeb by viewModel.isCrawlingWeb.collectAsStateWithLifecycle()
    val lastCrawlPreview by viewModel.lastCrawlPreview.collectAsStateWithLifecycle()
    val isSelfHealing by viewModel.isSelfHealing.collectAsStateWithLifecycle()
    val selfHealReport by viewModel.selfHealReport.collectAsStateWithLifecycle()
    val isRunningTermuxCommand by viewModel.isRunningTermuxCommand.collectAsStateWithLifecycle()
    val lastTermuxCommandResult by viewModel.lastTermuxCommandResult.collectAsStateWithLifecycle()
    val isOrchestrating by viewModel.isOrchestrating.collectAsStateWithLifecycle()
    val lastOrchestrationResult by viewModel.lastOrchestrationResult.collectAsStateWithLifecycle()
    val hardeningState by viewModel.hardeningState.collectAsStateWithLifecycle()
    val isHardeningBusy by viewModel.isHardeningBusy.collectAsStateWithLifecycle()
    val credentials by viewModel.credentials.collectAsStateWithLifecycle()
    val vaultMetadata by viewModel.vaultMetadata.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val isBusyNodeAction by viewModel.isBusyNodeAction.collectAsStateWithLifecycle()
    val verificationStatus by viewModel.settingsVerificationStatus.collectAsStateWithLifecycle()
    val syncSnapshot by viewModel.syncSnapshotState.collectAsStateWithLifecycle()
    val syncSettings by viewModel.syncSettingsState.collectAsStateWithLifecycle()
    val syncWorkState by viewModel.syncWorkState.collectAsStateWithLifecycle()

    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBanner()
        }
    }

    if (showSettingsScreen) {
        SettingsScreen(
            userEmail = userEmail,
            credentials = credentials,
            vaultMetadata = vaultMetadata,
            isBusy = isBusyNodeAction,
            verificationStatus = verificationStatus,
            snackbarHostState = snackbarHostState,
            onSaveCredentials = viewModel::saveCredentials,
            onClearVault = viewModel::clearEncryptedVault,
            onVerifyGitHub = viewModel::verifyGitHubFromSettings,
            onVerifyCloudflare = viewModel::verifyCloudflareFromSettings,
            onVerifyGc2223 = viewModel::verifyGc2223FromSettings,
            onVerifyTermux = viewModel::verifyTermuxFromSettings,
            onNavigateBack = {
                viewModel.clearVerificationStatus()
                showSettingsScreen = false
            }
        )
        return
    }

    if (currentTab != HubTab.AGENT) {
        BackHandler {
            currentTab = HubTab.AGENT
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "AntiGravity Hub",
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = userEmail.ifBlank { "Google Account Connected" },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", costConfig.dailyThresholdUsd)}/d Guard",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                statusSubTab = StatusSubTab.DASHBOARD
                                currentTab = HubTab.NODES
                            },
                            modifier = Modifier.testTag("open_status_dashboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CloudSync,
                                contentDescription = "ステータスダッシュボード"
                            )
                        }
                        IconButton(
                            onClick = { showSettingsScreen = true },
                            modifier = Modifier.testTag("open_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "セキュア設定"
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.onUserSignedOut()
                                signOutUser(credentialManager, signOutScope)
                            },
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = "サインアウト"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        HubTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = { Text(tab.label, maxLines = 1) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                        HubTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (currentTab) {
                        HubTab.AGENT -> AgentChatScreen(
                            aiState = aiState,
                            inferenceTarget = inferenceTarget,
                            gc2223Endpoint = credentials.gc2223Endpoint,
                            attachedSources = attachedSources,
                            isCrawlingWeb = isCrawlingWeb,
                            lastCrawlPreview = lastCrawlPreview,
                            onCrawlUrlOrDomain = viewModel::crawlDomainOrUrl,
                            onAttachDeviceScreenshot = viewModel::attachDeviceScreenshotBitmap,
                            onAddSourceUris = viewModel::addAttachedUris,
                            onRemoveSource = viewModel::removeAttachedSource,
                            onSelectTarget = viewModel::setInferenceTarget,
                            onSendPrompt = viewModel::runPrompt,
                            onSavePlanAsBlueprint = { title, cat, summary, content ->
                                viewModel.saveBlueprint(title, cat, summary, content)
                            },
                            onShowMessage = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )

                        HubTab.NODES -> Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .widthIn(max = 760.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                StatusSubTab.entries.forEachIndexed { index, subTab ->
                                    SegmentedButton(
                                        selected = statusSubTab == subTab,
                                        onClick = { statusSubTab = subTab },
                                        shape = SegmentedButtonDefaults.itemShape(
                                            index = index,
                                            count = StatusSubTab.entries.size
                                        ),
                                        modifier = Modifier.testTag(subTab.testTag)
                                    ) {
                                        Text(subTab.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                            when (statusSubTab) {
                                StatusSubTab.DASHBOARD -> StatusDashboardScreen(
                                    snapshot = syncSnapshot,
                                    syncSettings = syncSettings,
                                    syncWorkState = syncWorkState,
                                    isRunningTermuxCommand = isRunningTermuxCommand,
                                    lastTermuxCommandResult = lastTermuxCommandResult,
                                    onRunTermuxPreset = viewModel::runTermuxPresetCommand,
                                    onEscalateTermuxErrorToSelfHeal = { errLog ->
                                        viewModel.clearSelfHealReport()
                                        selfHealPrefillLog = errLog
                                        showSelfHealDialog = true
                                    },
                                    onSyncNow = viewModel::triggerBackgroundSyncNow,
                                    onSyncTermuxNow = viewModel::triggerTermuxSyncNow,
                                    onToggleBackgroundSync = viewModel::setBackgroundSyncEnabled,
                                    onSelectIntervalMinutes = viewModel::setBackgroundSyncInterval,
                                    onOpenSelfHeal = {
                                        viewModel.clearSelfHealReport()
                                        selfHealPrefillLog = null
                                        showSelfHealDialog = true
                                    },
                                    onOpenSettings = { showSettingsScreen = true }
                                )

                                StatusSubTab.NODES -> NodesScreen(
                                    nodesState = nodesState,
                                    isBusy = isBusyNodeAction,
                                    onOpenSettings = { showSettingsScreen = true },
                                    onTestGc2223Health = viewModel::testGc2223Health,
                                    onVerifyCloudflare = viewModel::verifyCloudflareNode,
                                    onVerifyGitHub = { node -> viewModel.verifyOrDispatchGitHub(node, dispatch = false) },
                                    onDispatchGitHub = { node -> viewModel.verifyOrDispatchGitHub(node, dispatch = true) },
                                    onOpenSelfHeal = {
                                        viewModel.clearSelfHealReport()
                                        selfHealPrefillLog = null
                                        showSelfHealDialog = true
                                    },
                                    onMarkNodeActive = viewModel::updateGcpOrTermuxStatus
                                )
                            }
                        }

                        HubTab.COST -> CostMonitorScreen(
                            userEmail = userEmail,
                            credentials = credentials,
                            costConfig = costConfig,
                            costEntriesState = costEntriesState,
                            costAlertsState = costAlertsState,
                            onSaveCostConfig = viewModel::saveCostConfig,
                            onAddExpense = viewModel::addExpenseAndCheckThreshold,
                            onDeleteEntry = viewModel::deleteCostEntry,
                            onTriggerAlertTest = viewModel::triggerManualAlertTest,
                            onFetchSsoToken = viewModel::copyableFirebaseSsoToken,
                            onOpenSettings = { showSettingsScreen = true },
                            onShowMessage = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )

                        HubTab.DEPLOY -> DeployKitScreen(
                            isBusy = isBusyNodeAction,
                            isOrchestrating = isOrchestrating,
                            isHardeningBusy = isHardeningBusy,
                            lastOrchestrationResult = lastOrchestrationResult,
                            hardeningState = hardeningState,
                            githubRepoConfigured = credentials.githubPat.isNotBlank() && credentials.githubOwnerRepo.contains("/"),
                            onPushTemplateToGitHub = viewModel::pushTemplateToGitHub,
                            onPushAllOrchestrationStack = viewModel::pushAllOrchestrationStackToGitHub,
                            onDispatchTerraform = { action, env ->
                                viewModel.dispatchTerraformRun(action = action, targetEnv = env)
                            },
                            onRunOrchestrationTask = viewModel::runOrchestrationTask,
                            onRefreshHardening = viewModel::refreshHardeningState,
                            onDecideApproval = viewModel::decideApproval,
                            onToggleSafeMode = viewModel::setCircuitBreakerSafeMode,
                            onSetOperatingMode = viewModel::setSustainabilityMode,
                            onSyncPolicy = viewModel::syncGovernancePolicy,
                            onVerifyAuditChain = viewModel::verifyAuditChain,
                            onRunChaosDrill = viewModel::runChaosDrill,
                            onRegisterLocalNode = { viewModel.registerLocalNode() },
                            onTriggerAutoTune = viewModel::triggerPolicyAutoTune,
                            onCreateDrSnapshot = viewModel::createDrSnapshot,
                            onRestoreDrSnapshot = viewModel::restoreLatestDrSnapshot,
                            onPingDeadmanSwitch = viewModel::pingDeadmanSwitch,
                            onSaveTemplate = { tpl ->
                                viewModel.saveBlueprint(
                                    title = tpl.title,
                                    category = tpl.category,
                                    summary = tpl.subtitle,
                                    content = tpl.content,
                                    targetHost = tpl.targetHost,
                                    status = "READY"
                                )
                            },
                            onShowMessage = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )

                        HubTab.VAULT -> BlueprintsScreen(
                            blueprintsState = blueprintsState,
                            onCreateBlueprint = { title, cat, summary, content ->
                                viewModel.saveBlueprint(title, cat, summary, content)
                            },
                            onUpdateStatus = viewModel::updateBlueprintStatus,
                            onDeleteBlueprint = viewModel::deleteBlueprint,
                            onShowMessage = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSelfHealDialog) {
        SelfHealDialog(
            defaultRepo = credentials.githubOwnerRepo.ifBlank { syncSnapshot.github.targetRepo },
            defaultWorkflow = credentials.githubWorkflowFile,
            defaultRunId = syncSnapshot.github.workflowRunId,
            initialErrorLog = selfHealPrefillLog,
            isSelfHealing = isSelfHealing,
            selfHealReport = selfHealReport,
            onDismiss = { showSelfHealDialog = false },
            onRunSelfHeal = { errorLog, repo, workflow, runId, sendToEmail, rerun ->
                viewModel.diagnoseErrorAndSelfHeal(
                    errorLog = errorLog,
                    repoOverride = repo,
                    workflowFile = workflow,
                    runId = runId,
                    sendToEmailWorker = sendToEmail,
                    rerunWorkflow = rerun
                )
            },
            onQuickRerunOnly = { runId ->
                viewModel.rerunFailedGitHubRun(runId)
            }
        )
    }
}
