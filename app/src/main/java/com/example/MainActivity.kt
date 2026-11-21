package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.CloudBridgeClient
import com.example.data.FirestoreRepository
import com.example.data.LocalSecretsStore
import com.example.data.SyncSnapshotStore
import com.example.firebase.configureAppCheck
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.MainHubScreen
import com.example.ui.theme.AntiGravityTheme
import com.example.ui.viewmodel.HubViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = {})
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
                        val databaseId = app.getString(R.string.firestore_database_id)
                        val db = FirebaseFirestore.getInstance(databaseId)
                        HubViewModel(
                            appContext = app,
                            repository = FirestoreRepository(db, auth),
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
