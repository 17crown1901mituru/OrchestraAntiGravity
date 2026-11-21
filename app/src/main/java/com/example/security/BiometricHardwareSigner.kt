package com.example.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import kotlin.coroutines.resume

data class HardwareKeyExport(
    val keyId: String,
    val spkiB64: String,
    val alg: String = "ECDSA_P256_SHA256"
)

object BiometricHardwareSigner {
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    const val DEFAULT_KEY_ALIAS = "antigravity_approval_ecdsa_p256"

    fun hasHardwareKey(alias: String = DEFAULT_KEY_ALIAS): Boolean {
        return runCatching {
            val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            ks.containsAlias(alias) && ks.getCertificate(alias) != null
        }.getOrDefault(false)
    }

    fun canAuthenticateBiometric(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    fun ensureOrCreateSigningKey(
        alias: String = DEFAULT_KEY_ALIAS,
        preferStrongBox: Boolean = true
    ): Result<HardwareKeyExport> = runCatching {
        val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        val existingCert = ks.getCertificate(alias)
        val spkiBytes = if (existingCert != null) {
            existingCert.publicKey.encoded
        } else {
            generateEcKeyPair(alias, preferStrongBox).public.encoded
        }
        val spkiB64 = Base64.encodeToString(spkiBytes, Base64.NO_WRAP)
        HardwareKeyExport(
            keyId = computeKeyId(spkiBytes),
            spkiB64 = spkiB64
        )
    }

    fun exportPublicKeyIfPresent(alias: String = DEFAULT_KEY_ALIAS): HardwareKeyExport? {
        return runCatching {
            val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            val cert = ks.getCertificate(alias) ?: return null
            val spkiBytes = cert.publicKey.encoded
            HardwareKeyExport(
                keyId = computeKeyId(spkiBytes),
                spkiB64 = Base64.encodeToString(spkiBytes, Base64.NO_WRAP)
            )
        }.getOrNull()
    }

    fun computeKeyId(spkiBytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(spkiBytes)
        return digest.take(6).joinToString("") { "%02x".format(it) }
    }

    private fun generateEcKeyPair(alias: String, preferStrongBox: Boolean): java.security.KeyPair {
        val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_PROVIDER)
        val tryStrongBox = preferStrongBox && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        return try {
            kpg.initialize(buildKeyGenSpec(alias, useStrongBox = tryStrongBox))
            kpg.generateKeyPair()
        } catch (e: Exception) {
            if (tryStrongBox && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && e is StrongBoxUnavailableException || e.cause is StrongBoxUnavailableException)) {
                kpg.initialize(buildKeyGenSpec(alias, useStrongBox = false))
                kpg.generateKeyPair()
            } else if (tryStrongBox) {
                kpg.initialize(buildKeyGenSpec(alias, useStrongBox = false))
                kpg.generateKeyPair()
            } else {
                throw e
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun buildKeyGenSpec(alias: String, useStrongBox: Boolean): KeyGenParameterSpec {
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN)
            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setUserAuthenticationRequired(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
        } else {
            builder.setUserAuthenticationValidityDurationSeconds(-1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && useStrongBox) {
            builder.setIsStrongBoxBacked(true)
        }
        return builder.build()
    }

    suspend fun signChallengeWithBiometric(
        activity: FragmentActivity,
        payload: String,
        alias: String = DEFAULT_KEY_ALIAS,
        title: String = activity.getString(R.string.biometric_approval_title),
        subtitle: String = activity.getString(R.string.biometric_approval_subtitle)
    ): Result<String> {
        if (!canAuthenticateBiometric(activity)) {
            return Result.failure(IllegalStateException("生体認証 (Strong Biometric) が端末に登録されていないか利用できません。"))
        }
        val signature = runCatching {
            val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            val privateKey = ks.getKey(alias, null) as? PrivateKey
                ?: error("ハードウェア署名鍵が未登録です。先に「生体署名鍵を登録」を実行してください。")
            Signature.getInstance("SHA256withECDSA").apply {
                initSign(privateKey)
            }
        }.getOrElse { return Result.failure(it) }

        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val executor = ContextCompat.getMainExecutor(activity)
                val callback = object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        val unlockedSig = result.cryptoObject?.signature
                        if (unlockedSig == null) {
                            if (cont.isActive) cont.resume(Result.failure(IllegalStateException("CryptoObject signature missing")))
                            return
                        }
                        val signedResult = runCatching {
                            unlockedSig.update(payload.toByteArray(Charsets.UTF_8))
                            val derBytes = unlockedSig.sign()
                            val p1363 = derToP1363(derBytes, componentLen = 32)
                            Base64.encodeToString(p1363, Base64.NO_WRAP)
                        }
                        if (cont.isActive) cont.resume(signedResult)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (cont.isActive) {
                            cont.resume(Result.failure(IllegalStateException("生体認証がキャンセル/失敗しました ($errorCode): $errString")))
                        }
                    }

                    override fun onAuthenticationFailed() {
                        // BiometricPrompt handles retries internally until error/cancel
                    }
                }

                val prompt = BiometricPrompt(activity, executor, callback)
                val info = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setNegativeButtonText("キャンセル")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build()

                prompt.authenticate(info, BiometricPrompt.CryptoObject(signature))
            }
        }
    }

    /**
     * Converts an ASN.1 DER-encoded ECDSA signature (`30 len 02 rLen r 02 sLen s`)
     * into fixed-length IEEE P1363 `(r || s)` format required by Web Crypto `crypto.subtle.verify`.
     */
    fun derToP1363(der: ByteArray, componentLen: Int = 32): ByteArray {
        if (der.size == componentLen * 2 && der.firstOrNull()?.toInt() != 0x30) {
            return der
        }
        require(der.size >= 8 && der[0].toInt() == 0x30) { "Invalid DER ECDSA signature header" }
        var offset = 1
        val seqLenFirst = der[offset++].toInt() and 0xFF
        if ((seqLenFirst and 0x80) != 0) {
            val lenBytes = seqLenFirst and 0x7F
            offset += lenBytes
        }
        require(offset < der.size && der[offset++].toInt() == 0x02) { "Missing DER INTEGER tag for R" }
        val rLen = der[offset++].toInt() and 0xFF
        require(offset + rLen <= der.size) { "Invalid DER R length" }
        val rSlice = der.copyOfRange(offset, offset + rLen)
        offset += rLen

        require(offset < der.size && der[offset++].toInt() == 0x02) { "Missing DER INTEGER tag for S" }
        val sLen = der[offset++].toInt() and 0xFF
        require(offset + sLen <= der.size) { "Invalid DER S length" }
        val sSlice = der.copyOfRange(offset, offset + sLen)

        val out = ByteArray(componentLen * 2)
        copyUnsignedComponent(rSlice, out, 0, componentLen)
        copyUnsignedComponent(sSlice, out, componentLen, componentLen)
        return out
    }

    private fun copyUnsignedComponent(src: ByteArray, dst: ByteArray, dstOffset: Int, targetLen: Int) {
        var start = 0
        while (start < src.size - 1 && src[start].toInt() == 0) {
            start++
        }
        val cleanLen = src.size - start
        require(cleanLen <= targetLen) { "ECDSA component exceeds $targetLen bytes ($cleanLen)" }
        System.arraycopy(src, start, dst, dstOffset + (targetLen - cleanLen), cleanLen)
    }
}
