package com.example.widget

import android.content.Context
import com.example.model.OrchestrationHardeningState

data class WidgetStatusSummary(
    val operatingMode: String = "ACTIVE",
    val safeMode: Boolean = false,
    val frugalFallbackActive: Boolean = false,
    val approvalsCount: Int = 0,
    val kvWritesToday: Int = 0,
    val kvDailyLimit: Int = 1000,
    val aiNeuronsToday: Int = 0,
    val aiNeuronDailyLimit: Int = 10000,
    val ciMinutesUsedMonth: Int = 0,
    val ciMinutesLimit: Int = 2000,
    val hardwareKeyRegistered: Boolean = false,
    val updatedAtEpochMs: Long = 0L
)

object WidgetStateStore {
    private const val PREFS_NAME = "antigravity_widget_state"
    private const val KEY_MODE = "operating_mode"
    private const val KEY_SAFE_MODE = "safe_mode"
    private const val KEY_FRUGAL = "frugal_fallback"
    private const val KEY_APPROVALS = "approvals_count"
    private const val KEY_KV_TODAY = "kv_writes_today"
    private const val KEY_KV_LIMIT = "kv_daily_limit"
    private const val KEY_AI_TODAY = "ai_neurons_today"
    private const val KEY_AI_LIMIT = "ai_neuron_daily_limit"
    private const val KEY_CI_USED = "ci_minutes_used"
    private const val KEY_CI_LIMIT = "ci_minutes_limit"
    private const val KEY_HW_REG = "hardware_key_registered"
    private const val KEY_UPDATED_AT = "updated_at_ms"

    fun saveFromHardeningState(context: Context, state: OrchestrationHardeningState) {
        val summary = WidgetStatusSummary(
            operatingMode = if (state.safeMode) "SAFE_MODE" else state.operatingMode.ifBlank { "ACTIVE" },
            safeMode = state.safeMode,
            frugalFallbackActive = state.frugalFallbackActive,
            approvalsCount = state.approvalsCount,
            kvWritesToday = state.kvWritesToday,
            kvDailyLimit = state.kvDailyLimit,
            aiNeuronsToday = state.aiNeuronsToday,
            aiNeuronDailyLimit = state.aiNeuronDailyLimit,
            ciMinutesUsedMonth = state.ciMinutesUsedMonth,
            ciMinutesLimit = state.ciMinutesLimit,
            hardwareKeyRegistered = state.hardwareKeyRegistered,
            updatedAtEpochMs = System.currentTimeMillis()
        )
        save(context, summary)
    }

    fun save(context: Context, summary: WidgetStatusSummary) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, summary.operatingMode)
            .putBoolean(KEY_SAFE_MODE, summary.safeMode)
            .putBoolean(KEY_FRUGAL, summary.frugalFallbackActive)
            .putInt(KEY_APPROVALS, summary.approvalsCount)
            .putInt(KEY_KV_TODAY, summary.kvWritesToday)
            .putInt(KEY_KV_LIMIT, summary.kvDailyLimit)
            .putInt(KEY_AI_TODAY, summary.aiNeuronsToday)
            .putInt(KEY_AI_LIMIT, summary.aiNeuronDailyLimit)
            .putInt(KEY_CI_USED, summary.ciMinutesUsedMonth)
            .putInt(KEY_CI_LIMIT, summary.ciMinutesLimit)
            .putBoolean(KEY_HW_REG, summary.hardwareKeyRegistered)
            .putLong(KEY_UPDATED_AT, summary.updatedAtEpochMs)
            .apply()
    }

    fun updateSafeModeOnly(context: Context, safeMode: Boolean) {
        val current = load(context)
        save(
            context,
            current.copy(
                safeMode = safeMode,
                operatingMode = if (safeMode) "SAFE_MODE" else "ACTIVE",
                updatedAtEpochMs = System.currentTimeMillis()
            )
        )
    }

    fun load(context: Context): WidgetStatusSummary {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return WidgetStatusSummary(
            operatingMode = prefs.getString(KEY_MODE, "ACTIVE").orEmpty().ifBlank { "ACTIVE" },
            safeMode = prefs.getBoolean(KEY_SAFE_MODE, false),
            frugalFallbackActive = prefs.getBoolean(KEY_FRUGAL, false),
            approvalsCount = prefs.getInt(KEY_APPROVALS, 0),
            kvWritesToday = prefs.getInt(KEY_KV_TODAY, 0),
            kvDailyLimit = prefs.getInt(KEY_KV_LIMIT, 1000),
            aiNeuronsToday = prefs.getInt(KEY_AI_TODAY, 0),
            aiNeuronDailyLimit = prefs.getInt(KEY_AI_LIMIT, 10000),
            ciMinutesUsedMonth = prefs.getInt(KEY_CI_USED, 0),
            ciMinutesLimit = prefs.getInt(KEY_CI_LIMIT, 2000),
            hardwareKeyRegistered = prefs.getBoolean(KEY_HW_REG, false),
            updatedAtEpochMs = prefs.getLong(KEY_UPDATED_AT, 0L)
        )
    }
}
