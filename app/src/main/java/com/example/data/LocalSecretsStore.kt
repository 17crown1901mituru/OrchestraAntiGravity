package com.example.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.example.model.ExternalCredentials
import com.example.model.McpEndpointEntry
import com.example.model.VaultMetadata
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val TAG = "LocalSecretsStore"
private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val KEY_ALIAS_PREFIX = "antigravity_vault_aes_"
private const val TRANSFORMATION = "AES/GCM/NoPadding"
private const val GCM_IV_LENGTH_BYTES = 12
private const val GCM_TAG_LENGTH_BITS = 128

class LocalSecretsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "antigravity_encrypted_vault",
        Context.MODE_PRIVATE
    )

    fun loadForUser(uid: String): ExternalCredentials {
        if (uid.isBlank()) return ExternalCredentials()
        val sub = uid.trim()
        val encoded = prefs.getString(vaultKey(sub), null) ?: return ExternalCredentials()
        return try {
            val plaintext = decrypt(sub, encoded)
            val json = JSONObject(plaintext)
            val endpoint = json.optString("gc2223Endpoint", "https://gc2223.com")
                .ifBlank { "https://gc2223.com" }
            val parsedMcp = parseMcpEndpoints(json.optJSONArray("mcpEndpoints"), endpoint)
            ExternalCredentials(
                gc2223Endpoint = endpoint,
                gc2223BearerToken = json.optString("gc2223BearerToken", ""),
                cloudflareApiToken = json.optString("cloudflareApiToken", ""),
                cloudflareAccountId = json.optString("cloudflareAccountId", ""),
                githubPat = json.optString("githubPat", ""),
                githubOwnerRepo = json.optString("githubOwnerRepo", ""),
                githubWorkflowFile = json.optString("githubWorkflowFile", "antigravity-deploy.yml")
                    .ifBlank { "antigravity-deploy.yml" },
                githubOAuthClientId = json.optString("githubOAuthClientId", ""),
                githubAccountLogin = json.optString("githubAccountLogin", ""),
                githubSelectedOrg = json.optString("githubSelectedOrg", ""),
                gcpProjectId = json.optString("gcpProjectId", ""),
                gcpApiKeyOrToken = json.optString("gcpApiKeyOrToken", ""),
                gcpBillingAccountId = json.optString("gcpBillingAccountId", ""),
                termuxAgentUrl = json.optString("termuxAgentUrl", "http://127.0.0.1:8787")
                    .ifBlank { "http://127.0.0.1:8787" },
                termuxAgentToken = json.optString("termuxAgentToken", ""),
                mcpEndpoints = parsedMcp
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt local vault for subject $sub", e)
            ExternalCredentials()
        }
    }

    fun saveForUser(
        uid: String,
        email: String,
        credentials: ExternalCredentials
    ): Result<VaultMetadata> {
        val sub = uid.trim()
        if (sub.isBlank()) {
            return Result.failure(IllegalStateException("Authenticated Google sub / UID is required."))
        }
        return try {
            val mcpArr = JSONArray().apply {
                credentials.mcpEndpoints.forEach { entry ->
                    if (entry.url.isNotBlank()) {
                        put(JSONObject().apply {
                            put("id", entry.id.trim())
                            put("label", entry.label.trim())
                            put("url", entry.url.trim().trimEnd('/'))
                            put("bearerToken", entry.bearerToken.trim())
                            put("isActive", entry.isActive)
                        })
                    }
                }
            }
            val payload = JSONObject().apply {
                put("gc2223Endpoint", credentials.gc2223Endpoint.trim())
                put("gc2223BearerToken", credentials.gc2223BearerToken.trim())
                put("cloudflareApiToken", credentials.cloudflareApiToken.trim())
                put("cloudflareAccountId", credentials.cloudflareAccountId.trim())
                put("githubPat", credentials.githubPat.trim())
                put("githubOwnerRepo", credentials.githubOwnerRepo.trim())
                put("githubWorkflowFile", credentials.githubWorkflowFile.trim())
                put("githubOAuthClientId", credentials.githubOAuthClientId.trim())
                put("githubAccountLogin", credentials.githubAccountLogin.trim())
                put("githubSelectedOrg", credentials.githubSelectedOrg.trim())
                put("gcpProjectId", credentials.gcpProjectId.trim())
                put("gcpApiKeyOrToken", credentials.gcpApiKeyOrToken.trim())
                put("gcpBillingAccountId", credentials.gcpBillingAccountId.trim())
                put("termuxAgentUrl", credentials.termuxAgentUrl.trim().ifBlank { "http://127.0.0.1:8787" })
                put("termuxAgentToken", credentials.termuxAgentToken.trim())
                put("mcpEndpoints", mcpArr)
            }.toString()

            val encrypted = encrypt(sub, payload)
            val now = System.currentTimeMillis()
            prefs.edit()
                .putString(vaultKey(sub), encrypted)
                .putLong(updatedKey(sub), now)
                .putString(emailKey(sub), email)
                .apply()

            Result.success(getVaultMetadata(sub, email))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt and save local vault", e)
            Result.failure(e)
        }
    }

    fun clearForUser(uid: String, email: String): Result<VaultMetadata> {
        val sub = uid.trim()
        if (sub.isBlank()) {
            return Result.failure(IllegalStateException("Authenticated Google sub / UID is required."))
        }
        return try {
            prefs.edit()
                .remove(vaultKey(sub))
                .remove(updatedKey(sub))
                .remove(emailKey(sub))
                .apply()
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            val alias = keyAlias(sub)
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias)
            }
            Result.success(getVaultMetadata(sub, email))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear encrypted vault", e)
            Result.failure(e)
        }
    }

    fun getVaultMetadata(uid: String, email: String): VaultMetadata {
        val sub = uid.trim()
        val hasSecrets = sub.isNotBlank() && prefs.contains(vaultKey(sub))
        val updatedAt = if (sub.isNotBlank()) prefs.getLong(updatedKey(sub), 0L) else 0L
        val storedEmail = if (sub.isNotBlank()) prefs.getString(emailKey(sub), email).orEmpty() else email
        return VaultMetadata(
            linkedUid = sub,
            linkedEmail = storedEmail.ifBlank { email },
            cipherAlgorithm = "AES-256-GCM (AndroidKeyStore)",
            updatedAtEpochMs = updatedAt,
            hasStoredSecrets = hasSecrets
        )
    }

    private fun getOrCreateKey(uid: String): SecretKey {
        val alias = keyAlias(uid)
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        val existingEntry = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        if (existingEntry != null) {
            return existingEntry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun encrypt(uid: String, plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey(uid))
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val combined = ByteBuffer.allocate(4 + iv.size + ciphertext.size)
            .putInt(iv.size)
            .put(iv)
            .put(ciphertext)
            .array()
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(uid: String, encoded: String): String {
        val combined = Base64.decode(encoded, Base64.NO_WRAP)
        val buffer = ByteBuffer.wrap(combined)
        val ivLen = buffer.int
        require(ivLen in 12..16) { "Invalid GCM IV length" }
        val iv = ByteArray(ivLen)
        buffer.get(iv)
        val ciphertext = ByteArray(buffer.remaining())
        buffer.get(ciphertext)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(uid), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charsets.UTF_8)
    }

    private fun parseMcpEndpoints(
        arr: JSONArray?,
        fallbackEndpoint: String
    ): List<McpEndpointEntry> {
        val list = mutableListOf<McpEndpointEntry>()
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val url = obj.optString("url", "").trim().trimEnd('/')
                if (url.isBlank()) continue
                list.add(
                    McpEndpointEntry(
                        id = obj.optString("id", "mcp_$i").ifBlank { "mcp_$i" },
                        label = obj.optString("label", url).ifBlank { url },
                        url = url,
                        bearerToken = obj.optString("bearerToken", ""),
                        isActive = obj.optBoolean("isActive", false)
                    )
                )
            }
        }
        if (list.isEmpty()) {
            val clean = fallbackEndpoint.trim().trimEnd('/').ifBlank { "https://gc2223.com" }
            list.add(
                McpEndpointEntry(
                    id = "default_gc2223",
                    label = "gc2223.com Stateless MCP",
                    url = clean,
                    isActive = true
                )
            )
        } else if (list.none { it.isActive }) {
            list[0] = list[0].copy(isActive = true)
        }
        return list
    }

    private fun safeSubKey(sub: String): String =
        sub.trim().replace(Regex("[^A-Za-z0-9_-]"), "_")

    private fun keyAlias(uid: String) = "$KEY_ALIAS_PREFIX${safeSubKey(uid)}"
    private fun vaultKey(uid: String) = "vault_${safeSubKey(uid)}"
    private fun updatedKey(uid: String) = "vault_updated_${safeSubKey(uid)}"
    private fun emailKey(uid: String) = "vault_email_${safeSubKey(uid)}"
}
