package com.example.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.json.JSONObject
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val TAG = "RuntimeCloudBootstrap"
private const val PREFS_NAME = "antigravity_runtime_bootstrap"
private const val KEY_PAYLOAD = "bootstrap_payload_enc"
private const val KEY_ALIAS = "antigravity_bootstrap_aes"
private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val TRANSFORMATION = "AES/GCM/NoPadding"

data class RuntimeCloudBootstrapConfig(
    val gatewayUrl: String = "",
    val firebaseProjectId: String = "",
    val firebaseAppId: String = "",
    val firebaseApiKey: String = "",
    val firebaseWebClientId: String = "",
    val firestoreDatabaseId: String = "(default)",
    val storageBucket: String = "",
    val defaultGithubRepo: String = ""
) {
    fun hasFirebaseRuntimeParams(): Boolean =
        firebaseProjectId.isNotBlank() && firebaseAppId.isNotBlank() && firebaseApiKey.isNotBlank()
}

class RuntimeCloudBootstrapStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): RuntimeCloudBootstrapConfig {
        val raw = prefs.getString(KEY_PAYLOAD, null) ?: return RuntimeCloudBootstrapConfig()
        val jsonText = runCatching {
            if (raw.startsWith("plain:")) {
                raw.removePrefix("plain:")
            } else {
                decrypt(raw)
            }
        }.getOrElse { err ->
            Log.w(TAG, "Failed to read runtime bootstrap config", err)
            return RuntimeCloudBootstrapConfig()
        }

        return runCatching {
            val json = JSONObject(jsonText)
            RuntimeCloudBootstrapConfig(
                gatewayUrl = json.optString("gatewayUrl", ""),
                firebaseProjectId = json.optString("firebaseProjectId", ""),
                firebaseAppId = json.optString("firebaseAppId", ""),
                firebaseApiKey = json.optString("firebaseApiKey", ""),
                firebaseWebClientId = json.optString("firebaseWebClientId", ""),
                firestoreDatabaseId = json.optString("firestoreDatabaseId", "(default)").ifBlank { "(default)" },
                storageBucket = json.optString("storageBucket", ""),
                defaultGithubRepo = json.optString("defaultGithubRepo", "")
            )
        }.getOrDefault(RuntimeCloudBootstrapConfig())
    }

    fun save(config: RuntimeCloudBootstrapConfig): Result<RuntimeCloudBootstrapConfig> = runCatching {
        val normalized = config.copy(
            gatewayUrl = config.gatewayUrl.trim().trimEnd('/'),
            firebaseProjectId = config.firebaseProjectId.trim(),
            firebaseAppId = config.firebaseAppId.trim(),
            firebaseApiKey = config.firebaseApiKey.trim(),
            firebaseWebClientId = config.firebaseWebClientId.trim(),
            firestoreDatabaseId = config.firestoreDatabaseId.trim().ifBlank { "(default)" },
            storageBucket = config.storageBucket.trim(),
            defaultGithubRepo = config.defaultGithubRepo.trim()
        )
        val payload = JSONObject().apply {
            put("gatewayUrl", normalized.gatewayUrl)
            put("firebaseProjectId", normalized.firebaseProjectId)
            put("firebaseAppId", normalized.firebaseAppId)
            put("firebaseApiKey", normalized.firebaseApiKey)
            put("firebaseWebClientId", normalized.firebaseWebClientId)
            put("firestoreDatabaseId", normalized.firestoreDatabaseId)
            put("storageBucket", normalized.storageBucket)
            put("defaultGithubRepo", normalized.defaultGithubRepo)
        }.toString()

        val encoded = runCatching { encrypt(payload) }.getOrElse { "plain:$payload" }
        prefs.edit().putString(KEY_PAYLOAD, encoded).apply()
        normalized
    }

    fun clear() {
        prefs.edit().remove(KEY_PAYLOAD).apply()
    }

    private fun getOrCreateKey(): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        val existing = ks.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) return existing.secretKey
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        generator.init(spec)
        return generator.generateKey()
    }

    private fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val combined = ByteBuffer.allocate(4 + iv.size + ciphertext.size)
            .putInt(iv.size)
            .put(iv)
            .put(ciphertext)
            .array()
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(encoded: String): String {
        val combined = Base64.decode(encoded, Base64.NO_WRAP)
        val buffer = ByteBuffer.wrap(combined)
        val ivLen = buffer.int
        require(ivLen in 12..16) { "Invalid GCM IV length" }
        val iv = ByteArray(ivLen)
        buffer.get(iv)
        val ciphertext = ByteArray(buffer.remaining())
        buffer.get(ciphertext)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }

    companion object {
        fun ensureFirebaseInitialized(context: Context): Boolean {
            val appContext = context.applicationContext
            if (FirebaseApp.getApps(appContext).isNotEmpty()) return true

            // 1. Try built-in resources from google-services.json if present
            val defaultInit = runCatching { FirebaseApp.initializeApp(appContext) }.getOrNull()
            if (defaultInit != null || FirebaseApp.getApps(appContext).isNotEmpty()) return true

            // 2. Fallback to runtime-saved FirebaseOptions (for zero-config APK builds)
            val saved = RuntimeCloudBootstrapStore(appContext).load()
            if (!saved.hasFirebaseRuntimeParams()) return false

            return runCatching {
                val options = FirebaseOptions.Builder()
                    .setProjectId(saved.firebaseProjectId)
                    .setApplicationId(saved.firebaseAppId)
                    .setApiKey(saved.firebaseApiKey)
                    .apply {
                        if (saved.storageBucket.isNotBlank()) setStorageBucket(saved.storageBucket)
                    }
                    .build()
                FirebaseApp.initializeApp(appContext, options)
                FirebaseApp.getApps(appContext).isNotEmpty()
            }.getOrDefault(false)
        }

        fun resolveDatabaseId(context: Context): String {
            val appContext = context.applicationContext
            val runtimeDb = RuntimeCloudBootstrapStore(appContext).load().firestoreDatabaseId.trim()
            if (runtimeDb.isNotBlank() && runtimeDb != "(default)") {
                return runtimeDb
            }
            val resId = appContext.resources.getIdentifier("firestore_database_id", "string", appContext.packageName)
            val resDb = if (resId != 0) {
                runCatching { appContext.getString(resId).trim() }.getOrNull().orEmpty()
            } else {
                ""
            }
            return resDb.ifBlank { runtimeDb.ifBlank { "(default)" } }
        }

        fun parseGoogleServicesJson(
            rawJson: String,
            targetPackageName: String,
            existingGatewayUrl: String = ""
        ): Result<RuntimeCloudBootstrapConfig> = runCatching {
            val root = JSONObject(rawJson.trim())
            val projectInfo = root.optJSONObject("project_info") ?: JSONObject()
            val projectId = projectInfo.optString("project_id", "").trim()
            val storageBucket = projectInfo.optString("storage_bucket", "").trim()
            val clients = root.optJSONArray("client")
                ?: throw IllegalArgumentException("google-services.json に client 配列が見つかりません")

            var matchedClient: JSONObject? = null
            for (i in 0 until clients.length()) {
                val item = clients.optJSONObject(i) ?: continue
                val pkg = item.optJSONObject("client_info")
                    ?.optJSONObject("android_client_info")
                    ?.optString("package_name")
                if (pkg == targetPackageName) {
                    matchedClient = item
                    break
                }
            }
            val chosen = matchedClient ?: clients.optJSONObject(0)
                ?: throw IllegalArgumentException("有効な client エントリがありません")

            val appId = chosen.optJSONObject("client_info")?.optString("mobilesdk_app_id", "").orEmpty().trim()
            val apiKeys = chosen.optJSONArray("api_key")
            val apiKey = apiKeys?.optJSONObject(0)?.optString("current_key", "").orEmpty().trim()

            var webClientId = ""
            val oauthClients = chosen.optJSONArray("oauth_client")
            if (oauthClients != null) {
                for (i in 0 until oauthClients.length()) {
                    val oauth = oauthClients.optJSONObject(i) ?: continue
                    if (oauth.optInt("client_type", 0) == 3) {
                        webClientId = oauth.optString("client_id", "").trim()
                        if (webClientId.isNotBlank()) break
                    }
                }
            }

            require(projectId.isNotBlank() && appId.isNotBlank() && apiKey.isNotBlank()) {
                "project_id / mobilesdk_app_id / current_key を読み取れませんでした"
            }

            RuntimeCloudBootstrapConfig(
                gatewayUrl = existingGatewayUrl.trim(),
                firebaseProjectId = projectId,
                firebaseAppId = appId,
                firebaseApiKey = apiKey,
                firebaseWebClientId = webClientId,
                storageBucket = storageBucket
            )
        }
    }
}
