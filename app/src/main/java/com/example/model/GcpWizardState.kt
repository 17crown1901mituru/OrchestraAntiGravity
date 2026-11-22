package com.example.model

import com.example.data.GcpApiServiceItem
import com.example.data.GcpBillingAccountSummary
import com.example.data.GcpOrganizationSummary
import com.example.data.GcpProjectSummary
import com.example.data.GcpProvisioningClient
import com.example.data.GeminiModelSummary
import com.example.security.AppSigningDiagnostics

data class GcpWizardState(
    val accessToken: String = "",
    val oauthAuthorized: Boolean = false,
    val isBusy: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val organizations: List<GcpOrganizationSummary> = emptyList(),
    val selectedOrganizationId: String = "",
    val projects: List<GcpProjectSummary> = emptyList(),
    val selectedProjectId: String = "",
    val billingAccounts: List<GcpBillingAccountSummary> = emptyList(),
    val geminiModels: List<GeminiModelSummary> = emptyList(),
    val selectedGeminiModel: String = "gemini-3.8-flash",
    val discoveryCompleted: Boolean = false,
    val discoverySummary: String = "",
    val apiItems: List<GcpApiServiceItem> = GcpProvisioningClient.recommendedApis,
    val selectedApiNamesToEnable: Set<String> = GcpProvisioningClient.recommendedApis
        .filter { it.requiredForFreeTier }
        .map { it.serviceName }
        .toSet(),
    val firebaseSynced: Boolean = false,
    val lastFirebaseAppId: String = "",
    val registeredShaCount: Int = 0,
    val signingDiagnostics: AppSigningDiagnostics? = null
)
