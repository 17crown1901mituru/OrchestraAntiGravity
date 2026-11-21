package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
import com.example.data.CloudBridgeClient
import com.example.data.FirestoreRepository
import com.example.data.LocalSecretsStore
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
import kotlinx.coroutines.flow.flowOf

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // App Check MUST run before setContent, ViewModels, or any Firebase call when configured
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

    val resolvedAuth = remember(auth, context, bootstrapRevision) {
        auth ?: runCatching {
            RuntimeCloudBootstrapStore.ensureFirebaseInitialized(context)
            if (FirebaseApp.getApps(context).isNotEmpty()) Firebase.auth else null
        }.getOrNull()
    }

    val currentUserFlow = remember(resolvedAuth) {
        resolvedAuth?.authStateFlow() ?: flowOf(null)
    }
    val user by currentUserFlow.collectAsStateWithLifecycle(initialValue = resolvedAuth?.currentUser)

    val activeUser = user
    if (activeUser == null || resolvedAuth == null) {
        AuthScreen(
            onAuthSuccess = {},
            isFirebaseReady = resolvedAuth != null,
            onFirebaseInitialized = {
                configureAppCheck(context, (context as? Activity)?.intent)
                bootstrapRevision++
            }
        )
    } else {
        val uid = activeUser.uid
        val email = activeUser.email.orEmpty()
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
