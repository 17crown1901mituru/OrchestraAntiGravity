package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.DataSaverOn
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.OrchestrationHardeningState
import java.util.Locale

@Composable
fun SustainabilityTelemetryCard(
    state: OrchestrationHardeningState,
    isBusy: Boolean,
    onSetOperatingMode: (mode: String) -> Unit
) {
    val isEcoSleep = state.operatingMode == "ECO_SLEEP"
    val isGreenDefer = state.operatingMode == "GREEN_DEFER"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.22f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sustainability_telemetry_card")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Eco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "サステナビリティ・テレメトリ (GreenOps)",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Surface(
                    color = when {
                        isEcoSleep -> MaterialTheme.colorScheme.surfaceVariant
                        isGreenDefer -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = state.operatingMode,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Metric Grid (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricTile(
                    icon = Icons.Rounded.AutoAwesome,
                    title = "AI 推論節約率",
                    value = "${state.aiSavingsPct}%",
                    detail = "Cache ${state.aiCachedCount} / 1B ${state.aiFrugalCount} (計${state.aiTotalCount})",
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    icon = Icons.Rounded.CloudDone,
                    title = "R2 ストレージ循環",
                    value = "監査 ${state.r2AuditCount} / ログ ${state.r2TermuxLogCount}",
                    detail = "${state.r2NextPurgeDays}日パージ • 再エネ待機 ${state.greenDeferredCount}件",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tempLabel = state.termuxTempC?.let {
                    String.format(Locale.US, "%.1f°C", it)
                } ?: "未計測"
                MetricTile(
                    icon = Icons.Rounded.Thermostat,
                    title = "端末バッテリー保護",
                    value = "$tempLabel • 保護 ${state.batteryGuardCount}回",
                    detail = "適応ポーリング ${state.recommendedPollSec}秒間隔",
                    modifier = Modifier.weight(1f)
                )
                val cfePct = (state.lastCfeScore * 100).toInt()
                val co2Text = if (state.carbonIntensityGco2 > 0) "${state.carbonIntensityGco2} gCO2/kWh" else "CFE $cfePct%"
                MetricTile(
                    icon = Icons.Rounded.Eco,
                    title = "グリッド CFE / 炭素強度",
                    value = co2Text,
                    detail = "GCP CFE $cfePct% (us-central1)",
                    modifier = Modifier.weight(1f)
                )
            }

            // Free-Tier Ceiling Guard (Cache API + Workers AI Neurons)
            Surface(
                color = if (state.frugalFallbackActive) {
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("free_tier_guard_card")
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.DataSaverOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "無料枠天井ガード (Free-Tier Guard)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (state.frugalFallbackActive) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "倹約AIモード発動中",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "KV ${state.kvWritesToday}/${state.kvDailyLimit} • Cache API | AI ${state.aiNeuronsToday}/${state.aiNeuronDailyLimit} Neurons",
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (state.lastDepAuditNote.isNotBlank()) {
                Text(
                    text = "週次依存チェック: ${state.lastDepAuditNote}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = {
                    val targetMode = if (isEcoSleep || isGreenDefer) "ACTIVE" else "ECO_SLEEP"
                    onSetOperatingMode(targetMode)
                },
                enabled = !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("toggle_eco_sleep_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bedtime,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        isEcoSleep -> "通常稼働 (ACTIVE) へ復帰"
                        isGreenDefer -> "再エネ待機を即時実行 (ACTIVE)"
                        else -> "ディープスリープ (ECO_SLEEP) へ移行"
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MetricTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    detail: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
