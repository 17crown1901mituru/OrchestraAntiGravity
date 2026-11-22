package com.example.data

import android.content.Context
import com.example.R
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.AgentBlueprint
import com.example.model.CostAlert
import com.example.model.CostConfig
import com.example.model.CostEntry
import com.example.model.WorkflowNode
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            RuntimeCloudBootstrapStore.resolveDatabaseId(context)
        )
    )

    private fun requireMatchedUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
        check(uid == expectedUserId) { "Signed-in user changed; refusing cross-user write." }
        return uid
    }

    fun observeNodes(userId: String): Flow<List<WorkflowNode>> {
        val path = "users/$userId/nodes"
        return db.collection("users").document(userId).collection("nodes")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    WorkflowNode::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                unwrapAndLog(error, userId, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun seedDefaultNodesIfEmpty(userId: String, accountEmail: String): Result<Unit> {
        val collectionPath = "users/$userId/nodes"
        return try {
            val uid = requireMatchedUserId(userId)
            val colRef = db.collection("users").document(uid).collection("nodes")
            val existing = colRef.limit(1).get().await()
            if (!existing.isEmpty) return Result.success(Unit)

            val hint = accountEmail.ifBlank { "google-account" }.take(120)
            val defaults = listOf(
                "cloudflare_gc2223" to mapOf(
                    "userId" to uid,
                    "provider" to "CLOUDFLARE",
                    "title" to "gc2223.com Edge LLM Gateway",
                    "endpointOrTarget" to "https://gc2223.com",
                    "accountHint" to hint,
                    "status" to "CONFIGURED",
                    "monthlyFreeQuota" to "Workers 100k req/day + Workers AI Free",
                    "lastCheckedNote" to "Ready for Wrangler deploy to gc2223.com",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                "termux_antigravity" to mapOf(
                    "userId" to uid,
                    "provider" to "TERMUX",
                    "title" to "Termux AntiGravityAgent CLI",
                    "endpointOrTarget" to "~/.local/bin/antigravity -> gc2223.com",
                    "accountHint" to "Android Termux Shell",
                    "status" to "CONFIGURED",
                    "monthlyFreeQuota" to "Local CLI (0 cost)",
                    "lastCheckedNote" to "Installer ready in Deploy Kit tab",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                "github_actions" to mapOf(
                    "userId" to uid,
                    "provider" to "GITHUB",
                    "title" to "GitHub Actions CI/CD Runner",
                    "endpointOrTarget" to "workflow_dispatch -> deploy-gc2223.yml",
                    "accountHint" to hint,
                    "status" to "CONFIGURED",
                    "monthlyFreeQuota" to "Public Free / Private 2,000 min/mo",
                    "lastCheckedNote" to "Set GitHub PAT & repo to dispatch",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                "gcp_free_tier" to mapOf(
                    "userId" to uid,
                    "provider" to "GCP",
                    "title" to "GCP Always Free + Budget Guard",
                    "endpointOrTarget" to "Cloud Run (2M req) + Workflows (5k steps)",
                    "accountHint" to hint,
                    "status" to "CONFIGURED",
                    "monthlyFreeQuota" to "Always Free + $0.10/day Guard",
                    "lastCheckedNote" to "Set $0.10/day budget alert via gcloud",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                "firebase_ai_logic" to mapOf(
                    "userId" to uid,
                    "provider" to "FIREBASE",
                    "title" to "Firebase AI Logic + Firestore",
                    "endpointOrTarget" to "gemini-3.8-flash (Google SSO active)",
                    "accountHint" to hint,
                    "status" to "ACTIVE",
                    "monthlyFreeQuota" to "Spark Free / App Check active",
                    "lastCheckedNote" to "Authenticated via Google SSO",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )

            val batch = db.batch()
            for ((docId, payload) in defaults) {
                batch.set(colRef.document(docId), payload)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, collectionPath)
            Result.failure(e)
        }
    }

    suspend fun updateNodeStatus(
        userId: String,
        nodeId: String,
        status: String,
        endpointOrTarget: String,
        lastCheckedNote: String
    ): Result<Unit> {
        val docPath = "users/$userId/nodes/$nodeId"
        return try {
            val uid = requireMatchedUserId(userId)
            require(nodeId.isNotBlank() && nodeId.matches(ID_REGEX)) { "Invalid node ID" }
            require(status in VALID_NODE_STATUSES) { "Invalid node status" }

            db.collection("users").document(uid).collection("nodes").document(nodeId)
                .update(
                    mapOf(
                        "status" to status,
                        "endpointOrTarget" to endpointOrTarget.take(200),
                        "lastCheckedNote" to lastCheckedNote.take(300),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    fun observeBlueprints(userId: String): Flow<List<AgentBlueprint>> {
        val path = "users/$userId/blueprints"
        return db.collection("users").document(userId).collection("blueprints")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    AgentBlueprint::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                unwrapAndLog(error, userId, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun saveBlueprint(
        userId: String,
        title: String,
        category: String,
        summary: String,
        content: String,
        targetHost: String = "gc2223.com",
        status: String = "READY"
    ): Result<String> {
        val docId = "bp_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docPath = "users/$userId/blueprints/$docId"
        return try {
            val uid = requireMatchedUserId(userId)
            val safeTitle = title.trim().ifEmpty { "Untitled Blueprint" }.take(100)
            val safeCategory = if (category in VALID_BLUEPRINT_CATEGORIES) category else "CLI"
            val safeContent = content.trim().ifEmpty { "# empty" }.take(20_000)
            val safeStatus = if (status in VALID_BLUEPRINT_STATUSES) status else "DRAFT"

            val payload = mapOf(
                "userId" to uid,
                "title" to safeTitle,
                "category" to safeCategory,
                "summary" to summary.trim().take(300),
                "content" to safeContent,
                "targetHost" to targetHost.trim().take(120),
                "status" to safeStatus,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).collection("blueprints").document(docId)
                .set(payload)
                .await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateBlueprintStatus(
        userId: String,
        blueprintId: String,
        newStatus: String
    ): Result<Unit> {
        val docPath = "users/$userId/blueprints/$blueprintId"
        return try {
            val uid = requireMatchedUserId(userId)
            require(blueprintId.isNotBlank() && blueprintId.matches(ID_REGEX)) { "Invalid blueprint ID" }
            require(newStatus in VALID_BLUEPRINT_STATUSES) { "Invalid blueprint status" }

            db.collection("users").document(uid).collection("blueprints").document(blueprintId)
                .update(
                    mapOf(
                        "status" to newStatus,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteBlueprint(userId: String, blueprintId: String): Result<Unit> {
        val docPath = "users/$userId/blueprints/$blueprintId"
        return try {
            val uid = requireMatchedUserId(userId)
            require(blueprintId.isNotBlank() && blueprintId.matches(ID_REGEX)) { "Invalid blueprint ID" }

            db.collection("users").document(uid).collection("blueprints").document(blueprintId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    // --- Cost Monitoring & Threshold Alerts ---

    fun observeCostConfig(userId: String): Flow<CostConfig> {
        val path = "users/$userId/cost_config/daily_budget"
        return db.collection("users").document(userId).collection("cost_config")
            .document("daily_budget")
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(
                    CostConfig::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ) ?: CostConfig(userId = userId)
            }
            .catch { error ->
                unwrapAndLog(error, userId, OperationType.GET, path)
                throw error
            }
    }

    suspend fun seedDefaultCostConfigIfMissing(userId: String, userEmail: String): Result<Unit> {
        val docPath = "users/$userId/cost_config/daily_budget"
        return try {
            val uid = requireMatchedUserId(userId)
            val docRef = db.collection("users").document(uid)
                .collection("cost_config").document("daily_budget")
            val snap = docRef.get().await()
            if (snap.exists()) return Result.success(Unit)

            val defaultEmail = userEmail.ifBlank { "17crown2223@gmail.com" }.take(120)
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "alertEmail" to defaultEmail,
                    "dailyThresholdUsd" to 0.10,
                    "autoEmailEnabled" to true,
                    "webhookPath" to "/v1/cost-alert",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateCostConfig(
        userId: String,
        alertEmail: String,
        dailyThresholdUsd: Double,
        autoEmailEnabled: Boolean,
        webhookPath: String
    ): Result<Unit> {
        val docPath = "users/$userId/cost_config/daily_budget"
        return try {
            val uid = requireMatchedUserId(userId)
            val cleanEmail = alertEmail.trim().ifBlank { "17crown2223@gmail.com" }.take(120)
            require(cleanEmail.length >= 3) { "Alert email must be at least 3 characters." }
            require(dailyThresholdUsd > 0.0 && dailyThresholdUsd <= 1000.0) {
                "Daily threshold must be between $0.001 and $1000."
            }

            db.collection("users").document(uid)
                .collection("cost_config").document("daily_budget")
                .update(
                    mapOf(
                        "alertEmail" to cleanEmail,
                        "dailyThresholdUsd" to dailyThresholdUsd,
                        "autoEmailEnabled" to autoEmailEnabled,
                        "webhookPath" to webhookPath.trim().take(120),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    fun observeCostEntries(userId: String): Flow<List<CostEntry>> {
        val path = "users/$userId/cost_entries"
        return db.collection("users").document(userId).collection("cost_entries")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    CostEntry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                unwrapAndLog(error, userId, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun addCostEntry(
        userId: String,
        provider: String,
        amountUsd: Double,
        usageSummary: String,
        source: String,
        dateKey: String = todayDateKey()
    ): Result<String> {
        val entryId = "ce_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docPath = "users/$userId/cost_entries/$entryId"
        return try {
            val uid = requireMatchedUserId(userId)
            val safeProvider = if (provider in VALID_COST_PROVIDERS) provider else "GCP"
            val safeSource = if (source in VALID_COST_SOURCES) source else "MANUAL"
            require(dateKey.length == 10) { "dateKey must be YYYY-MM-DD" }
            require(amountUsd >= 0.0 && amountUsd <= 10000.0) { "Invalid cost amount" }
            val safeSummary = usageSummary.trim().ifBlank { "$safeProvider usage" }.take(200)

            db.collection("users").document(uid)
                .collection("cost_entries").document(entryId)
                .set(
                    mapOf(
                        "userId" to uid,
                        "provider" to safeProvider,
                        "dateKey" to dateKey,
                        "amountUsd" to amountUsd,
                        "usageSummary" to safeSummary,
                        "source" to safeSource,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(entryId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteCostEntry(userId: String, entryId: String): Result<Unit> {
        val docPath = "users/$userId/cost_entries/$entryId"
        return try {
            val uid = requireMatchedUserId(userId)
            require(entryId.isNotBlank() && entryId.matches(ID_REGEX)) { "Invalid entry ID" }
            db.collection("users").document(uid)
                .collection("cost_entries").document(entryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    fun observeCostAlerts(userId: String): Flow<List<CostAlert>> {
        val path = "users/$userId/cost_alerts"
        return db.collection("users").document(userId).collection("cost_alerts")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    CostAlert::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                unwrapAndLog(error, userId, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun recordCostAlert(
        userId: String,
        dateKey: String,
        totalUsd: Double,
        thresholdUsd: Double,
        recipientEmail: String,
        deliveryStatus: String,
        deliveryDetail: String
    ): Result<Unit> {
        val alertId = "al_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docPath = "users/$userId/cost_alerts/$alertId"
        return try {
            val uid = requireMatchedUserId(userId)
            val safeStatus = if (deliveryStatus in VALID_ALERT_STATUSES) deliveryStatus else "QUEUED_LOCAL"
            db.collection("users").document(uid)
                .collection("cost_alerts").document(alertId)
                .set(
                    mapOf(
                        "userId" to uid,
                        "dateKey" to dateKey.take(10),
                        "totalUsd" to totalUsd.coerceAtLeast(0.0),
                        "thresholdUsd" to thresholdUsd.coerceAtLeast(0.001),
                        "recipientEmail" to recipientEmail.trim().ifBlank { "17crown2223@gmail.com" }.take(120),
                        "deliveryStatus" to safeStatus,
                        "deliveryDetail" to deliveryDetail.take(300),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    private fun unwrapAndLog(
        error: Throwable,
        userId: String,
        op: OperationType,
        path: String
    ) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(exceptionToLog, op, path)
        }
    }

    companion object {
        val ID_REGEX = Regex("^[a-zA-Z0-9_\\-]+$")
        val VALID_NODE_STATUSES = setOf("ACTIVE", "CONFIGURED", "ERROR", "PAUSED")
        val VALID_BLUEPRINT_CATEGORIES = setOf("WORKER", "TERMUX", "GITHUB", "GCP", "CLI")
        val VALID_BLUEPRINT_STATUSES = setOf("DRAFT", "READY", "DEPLOYED", "ARCHIVED")
        val VALID_COST_PROVIDERS = setOf("GCP", "CLOUDFLARE", "FIREBASE", "GITHUB")
        val VALID_COST_SOURCES = setOf("AUTO_TELEMETRY", "API_SYNC", "MANUAL")
        val VALID_ALERT_STATUSES = setOf("SENT", "QUEUED_LOCAL", "FAILED")

        fun todayDateKey(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}
