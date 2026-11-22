package com.example

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.GoogleAccountSession
import com.example.auth.InMemoryOAuthSessionStore
import com.example.data.CloudBridgeClient
import com.example.data.FirestoreRepository
import com.example.data.LocalSecretsStore
import com.example.data.RuntimeCloudBootstrapStore
import com.example.data.SyncSnapshotStore
import com.example.firebase.configureAppCheck
import com.example.ui.screens.LoginScreen
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
import kotlinx.coroutines.flow.flowOf

const val ROUTE_LOGIN = "login"
const val ROUTE_MAIN_HUB = "main_hub"

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // App Check / Firebase MUST NOT run eagerly on startup before Google OAuth + GCP discovery
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
    val navController = rememberNavController()
    var bootstrapRevision by remember { mutableIntStateOf(0) }

    val oauthSession by InMemoryOAuthSessionStore.sessionFlow.collectAsStateWithLifecycle()

    // Inspect Firebase only if already initialized on-device; never require it at boot
    val resolvedAuth = remember(auth, context, bootstrapRevision) {
        auth ?: runCatching {
            if (FirebaseApp.getApps(context).isNotEmpty()) Firebase.auth else null
        }.getOrNull()
    }

    val currentUserFlow = remember(resolvedAuth) {
        resolvedAuth?.authStateFlow() ?: flowOf(null)
    }
    val firebaseUser by currentUserFlow.collectAsStateWithLifecycle(initialValue = resolvedAuth?.currentUser)

    // Primary session gate: GoogleAccountSession (immutable sub + virtualAlias + OAuth token)
    val effectiveSession: GoogleAccountSession? = remember(oauthSession, firebaseUser) {
        oauthSession ?: firebaseUser?.let { fbUser ->
            GoogleAccountSession.create(
                sub = fbUser.uid,
                accessToken = InMemoryOAuthSessionStore.accessToken,
                email = fbUser.email.orEmpty()
            )
        }
    }

    val isAuthenticated = effectiveSession != null
    val initialDestination = remember {
        if (isAuthenticated) ROUTE_MAIN_HUB else ROUTE_LOGIN
    }

    LaunchedEffect(isAuthenticated) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (isAuthenticated && currentRoute != ROUTE_MAIN_HUB) {
            navController.navigate(ROUTE_MAIN_HUB) {
                popUpTo(ROUTE_LOGIN) { inclusive = true }
                launchSingleTop = true
            }
        } else if (!isAuthenticated && currentRoute != ROUTE_LOGIN) {
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(ROUTE_MAIN_HUB) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onAuthSuccess = {
                    bootstrapRevision++
                    if (navController.currentBackStackEntry?.destination?.route != ROUTE_MAIN_HUB) {
                        navController.navigate(ROUTE_MAIN_HUB) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                isFirebaseReady = resolvedAuth != null,
                onFirebaseInitialized = {
                    configureAppCheck(context, context.findHostActivity()?.intent)
                    bootstrapRevision++
                }
            )
        }

        composable(ROUTE_MAIN_HUB) {
            val session = effectiveSession
            if (session != null) {
                val sub = session.sub
                val accountDisplay = session.virtualAlias
                val optionalFirebaseRepo = remember(sub, resolvedAuth, firebaseUser, bootstrapRevision) {
                    val activeAuth = resolvedAuth
                    val activeFbUser = firebaseUser
                    if (activeAuth != null && activeFbUser != null && FirebaseApp.getApps(context).isNotEmpty()) {
                        runCatching {
                            val databaseId = RuntimeCloudBootstrapStore.resolveDatabaseId(context)
                            val db = FirebaseFirestore.getInstance(databaseId)
                            FirestoreRepository(db, activeAuth)
                        }.getOrNull()
                    } else {
                        null
                    }
                }

                key(sub) {
                    val hubViewModel: HubViewModel = viewModel(
                        key = sub,
                        factory = viewModelFactory {
                            initializer {
                                val app = checkNotNull(this[APPLICATION_KEY]) {
                                    "APPLICATION_KEY missing from CreationExtras"
                                }
                                HubViewModel(
                                    appContext = app,
                                    repository = optionalFirebaseRepo,
                                    secretsStore = LocalSecretsStore(app),
                                    syncSnapshotStore = SyncSnapshotStore(app),
                                    bridgeClient = CloudBridgeClient(),
                                    currentUserId = sub,
                                    userEmail = accountDisplay,
                                    onFirebaseProvisioned = {
                                        configureAppCheck(app, context.findHostActivity()?.intent)
                                        bootstrapRevision++
                                    }
                                )
                            }
                        }
                    )

                    LaunchedEffect(optionalFirebaseRepo) {
                        if (optionalFirebaseRepo != null) {
                            hubViewModel.attachCloudRepository(optionalFirebaseRepo)
                        }
                    }

                    MainHubScreen(
                        viewModel = hubViewModel,
                        userEmail = accountDisplay
                    )
                }
            }
        }
    }
}

private tailrec fun Context.findHostActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findHostActivity()
    else -> null
}
