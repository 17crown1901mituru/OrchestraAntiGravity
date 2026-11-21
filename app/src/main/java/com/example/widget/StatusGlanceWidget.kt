package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.data.LocalSecretsStore
import com.example.data.OrchestrationHardeningClient
import com.example.model.OrchestrationHardeningState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatusGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val summary = WidgetStateStore.load(context)
        provideContent {
            GlanceTheme {
                StatusWidgetContent(summary = summary)
            }
        }
    }

    @Composable
    private fun StatusWidgetContent(summary: WidgetStatusSummary) {
        val kvRemain = (summary.kvDailyLimit - summary.kvWritesToday).coerceAtLeast(0)
        val aiRemain = (summary.aiNeuronDailyLimit - summary.aiNeuronsToday).coerceAtLeast(0)
        val ciRemain = (summary.ciMinutesLimit - summary.ciMinutesUsedMonth).coerceAtLeast(0)
        val updatedLabel = if (summary.updatedAtEpochMs > 0L) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(summary.updatedAtEpochMs))
        } else {
            "--:--"
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    if (summary.safeMode) GlanceTheme.colors.errorContainer else GlanceTheme.colors.surface
                )
                .cornerRadius(16.dp)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Vertical.Top
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "AntiGravity Hub",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = "${summary.operatingMode} • $updatedLabel",
                    style = TextStyle(
                        color = if (summary.safeMode) GlanceTheme.colors.error else GlanceTheme.colors.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = "承認待ち: ${summary.approvalsCount}件",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = if (summary.hardwareKeyRegistered) "HW鍵: 有効" else "HW鍵: 未登録",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = "残KV: $kvRemain/${summary.kvDailyLimit} • 残AI: $aiRemain • CI残: ${ciRemain}m",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Button(
                    text = "状態更新",
                    onClick = actionRunCallback<RefreshStatusAction>(),
                    modifier = GlanceModifier.defaultWeight()
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Button(
                    text = "開く",
                    onClick = actionStartActivity<MainActivity>(),
                    modifier = GlanceModifier.defaultWeight()
                )
            }
        }
    }
}

class RefreshStatusAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val currentUser = runCatching { Firebase.auth.currentUser }.getOrNull()
        val uid = currentUser?.uid.orEmpty()
        if (uid.isNotBlank()) {
            val creds = LocalSecretsStore(context).loadForUser(uid)
            val idToken = withTimeoutOrNull(4000L) {
                runCatching { currentUser?.getIdToken(false)?.await()?.token }.getOrNull()
            }
            val snap = runCatching {
                OrchestrationHardeningClient().fetchHardeningSnapshot(
                    baseUrl = creds.gc2223Endpoint,
                    bearerToken = creds.gc2223BearerToken,
                    firebaseIdToken = idToken
                )
            }.getOrNull()
            if (snap != null && snap.statusMessage.isNotBlank()) {
                WidgetStateStore.saveFromHardeningState(context, snap)
            }
        }
        StatusGlanceWidget().updateAll(context)
    }
}

suspend fun updateGlanceWidgetFromState(context: Context, state: OrchestrationHardeningState) {
    WidgetStateStore.saveFromHardeningState(context, state)
    runCatching { StatusGlanceWidget().updateAll(context) }
}
