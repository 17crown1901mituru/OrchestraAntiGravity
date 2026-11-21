package com.example.sync

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.R
import com.example.data.LocalSecretsStore
import com.example.data.OrchestrationHardeningClient
import com.example.widget.StatusGlanceWidget
import com.example.widget.WidgetStateStore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "KillSwitchTileService"

class KillSwitchTileService : TileService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartListening() {
        super.onStartListening()
        val summary = WidgetStateStore.load(applicationContext)
        applyTileUi(safeMode = summary.safeMode, busy = false)
    }

    override fun onClick() {
        super.onClick()
        if (isLocked && isSecure) {
            unlockAndRun { performSafeModeToggle() }
        } else {
            performSafeModeToggle()
        }
    }

    private fun performSafeModeToggle() {
        val current = WidgetStateStore.load(applicationContext)
        val targetSafeMode = !current.safeMode
        applyTileUi(safeMode = current.safeMode, busy = true)

        serviceScope.launch {
            val user = runCatching { Firebase.auth.currentUser }.getOrNull()
            val uid = user?.uid.orEmpty()
            if (uid.isBlank()) {
                withContext(Dispatchers.Main) {
                    applyTileUi(safeMode = current.safeMode, busy = false, overrideSub = "要Googleログイン")
                }
                return@launch
            }

            val creds = LocalSecretsStore(applicationContext).loadForUser(uid)
            val idToken = withTimeoutOrNull(4000L) {
                runCatching { user?.getIdToken(false)?.await()?.token }.getOrNull()
            }
            val reason = if (targetSafeMode) {
                "Android Quick Settings Kill-Switch engaged"
            } else {
                "Android Quick Settings Kill-Switch cleared"
            }
            val res = runCatching {
                OrchestrationHardeningClient().toggleCircuitBreakerSafeMode(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken,
                    enableSafeMode = targetSafeMode,
                    reason = reason
                )
            }.getOrNull()

            if (res?.success == true) {
                WidgetStateStore.updateSafeModeOnly(applicationContext, targetSafeMode)
                runCatching { StatusGlanceWidget().updateAll(applicationContext) }
                withContext(Dispatchers.Main) {
                    applyTileUi(safeMode = targetSafeMode, busy = false)
                }
            } else {
                Log.w(TAG, "KillSwitch toggle failed: ${res?.message}")
                withContext(Dispatchers.Main) {
                    applyTileUi(safeMode = current.safeMode, busy = false, overrideSub = "通信失敗")
                }
            }
        }
    }

    private fun applyTileUi(safeMode: Boolean, busy: Boolean, overrideSub: String? = null) {
        val tile = qsTile ?: return
        tile.label = getString(R.string.qs_tile_killswitch_label)
        tile.state = when {
            busy -> Tile.STATE_UNAVAILABLE
            safeMode -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        val statusText = overrideSub ?: when {
            busy -> "切替中..."
            safeMode -> "SAFE_MODE 遮断中"
            else -> "通常稼働 (ACTIVE)"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = statusText
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tile.stateDescription = statusText
        }
        tile.contentDescription = "${tile.label}: $statusText"
        runCatching { tile.updateTile() }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
