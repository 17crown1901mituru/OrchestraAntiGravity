package com.example.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.example.model.ExternalCredentials
import com.example.model.VaultMetadata
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
        val encoded = prefs.getString(vaultKey(uid), null) ?: return ExternalCredentials()
        return try {
            val plaintext = decrypt(uid, encoded)
            val json = JSONObject(plaintext)
            ExternalCredentials(
                gc2223Endpoint = json.optString("gc2223Endpoint", "https://gc2223.com")
                    .ifBlank { "https://gc2223.com" },
                gc2223BearerToken = json.optString("gc2223BearerToken", ""),
                cloudflareApiToken = json.optString("cloudflareApiToken", ""),
                cloudflareAccountId = json.optString("cloudflareAccountId", ""),
                githubPat = json.optString("githubPat", ""),
                githubOwnerRepo = json.optString("githubOwnerRepo", ""),
                githubWorkflowFile = json.optString("githubWorkflowFile", "antigravity-deploy.yml")
                    .ifBlank { "antigravity-deploy.yml" },
                gcpProjectId = json.optString("gcpProjectId", "gen-lang-client-0439741065")
                    .ifBlank { "gen-lang-client-0439741065" },
                gcpApiKeyOrToken = json.optString("gcpApiKeyOrToken", ""),
                gcpBillingAccountId = json.optString("gcpBillingAccountId", ""),
                termuxAgentUrl = json.optString("termuxAgentUrl", "http://127.0.0.1:8787")
                    .ifBlank { "http://127.0.0.1:8787" },
                termuxAgentToken = json.optString("termuxAgentToken", "")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt local vault for user $uid", e)
            ExternalCredentials()
        }
    }

    fun saveForUser(
        uid: String,
        email: String,
        credentials: ExternalCredentials
    ): Result<VaultMetadata> {
        if (uid.isBlank()) {
            return Result.failure(IllegalStateException("Authenticated Firebase UID is required."))
        }
        return try {
            val payload = JSONObject().apply {
                put("gc2223Endpoint", credentials.gc2223Endpoint.trim())
                put("gc2223BearerToken", credentials.gc2223BearerToken.trim())
                put("cloudflareApiToken", credentials.cloudflareApiToken.trim())
                put("cloudflareAccountId", credentials.cloudflareAccountId.trim())
                put("githubPat", credentials.githubPat.trim())
                put("githubOwnerRepo", credentials.githubOwnerRepo.trim())
                put("githubWorkflowFile", credentials.githubWorkflowFile.trim())
                put("gcpProjectId", credentials.gcpProjectId.trim())
                put("gcpApiKeyOrToken", credentials.gcpApiKeyOrToken.trim())
                put("gcpBillingAccountId", credentials.gcpBillingAccountId.trim())
                put("termuxAgentUrl", credentials.termuxAgentUrl.trim().ifBlank { "http://127.0.0.1:8787" })
                put("termuxAgentToken", credentials.termuxAgentToken.trim())
            }.toString()

            val encrypted = encrypt(uid, payload)
            val now = System.currentTimeMillis()
            prefs.edit()
                .putString(vaultKey(uid), encrypted)
                .putLong(updatedKey(uid), now)
                .putString(emailKey(uid), email)
                .apply()

            Result.success(getVaultMetadata(uid, email))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt and save local vault", e)
            Result.failure(e)
        }
    }

    fun clearForUser(uid: String, email: String): Result<VaultMetadata> {
        if (uid.isBlank()) {
            return Result.failure(IllegalStateException("Authenticated Firebase UID is required."))
        }
        return try {
            prefs.edit()
                .remove(vaultKey(uid))
                .remove(updatedKey(uid))
                .remove(emailKey(uid))
                .apply()
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            val alias = keyAlias(uid)
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias)
            }
            Result.success(getVaultMetadata(uid, email))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear encrypted vault", e)
            Result.failure(e)
        }
    }

    fun getVaultMetadata(uid: String, email: String): VaultMetadata {
        val hasSecrets = uid.isNotBlank() && prefs.contains(vaultKey(uid))
        val updatedAt = if (uid.isNotBlank()) prefs.getLong(updatedKey(uid), 0L) else 0L
        val storedEmail = if (uid.isNotBlank()) prefs.getString(emailKey(uid), email).orEmpty() else email
        return VaultMetadata(
            linkedUid = uid,
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

    private fun keyAlias(uid: String) = "$KEY_ALIAS_PREFIX$uid"
    private fun vaultKey(uid: String) = "vault_$uid"
    private fun updatedKey(uid: String) = "vault_updated_$uid"
    private fun emailKey(uid: String) = "vault_email_$uid"
}
