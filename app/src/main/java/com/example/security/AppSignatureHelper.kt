package com.example.security

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

data class AppSigningDiagnostics(
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val sha1: String,
    val sha256: String,
    val matchesRepoDebugKeystore: Boolean,
    val matchesReleaseHint: String
)

object AppSignatureHelper {
    // Deterministic SHA-1 / SHA-256 of the repository's shared debug.keystore.base64
    const val REPO_DEBUG_SHA1 = "A6:B1:B9:12:4A:53:03:54:00:94:F6:6D:B4:9D:B5:60:4B:D1:79:80"
    const val REPO_DEBUG_SHA256 =
        "8F:F4:BD:D4:89:A9:71:3F:08:FC:A0:32:9B:DE:B5:24:C1:8B:62:DA:D3:93:34:07:FB:FC:1C:EE:E5:90:ED:E6"

    fun readDiagnostics(context: Context): AppSigningDiagnostics {
        val pkgName = context.packageName
        val pm = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }

        val info: PackageInfo? = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(pkgName, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(pkgName, flags)
            }
        }.getOrNull()

        val versionName = info?.versionName.orEmpty().ifBlank { "1.0" }
        val versionCode = if (info == null) {
            1L
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }

        val certBytes: ByteArray? = extractPrimarySignerBytes(info)
        val sha1 = certBytes?.let { digestColonHex("SHA-1", it) }.orEmpty()
        val sha256 = certBytes?.let { digestColonHex("SHA-256", it) }.orEmpty()
        val matchesDebug = sha256.equals(REPO_DEBUG_SHA256, ignoreCase = true)

        val hint = when {
            sha256.isBlank() -> "テスト/エミュレータ環境または署名情報未取得"
            matchesDebug -> "共通リポジトリ debug.keystore と完全一致 (上書き再インストール可能)"
            else -> "カスタム署名 (Release 鍵または別ビルド鍵) — 共通 Debug 版から乗り換える場合は一度アンインストールが必要です"
        }

        return AppSigningDiagnostics(
            packageName = pkgName,
            versionName = versionName,
            versionCode = versionCode,
            sha1 = sha1,
            sha256 = sha256,
            matchesRepoDebugKeystore = matchesDebug,
            matchesReleaseHint = hint
        )
    }

    internal fun digestColonHex(algorithm: String, bytes: ByteArray): String {
        val digest = MessageDigest.getInstance(algorithm).digest(bytes)
        return digest.joinToString(":") { b -> "%02X".format(b) }
    }

    internal fun stripColonsHex(fingerprint: String): String =
        fingerprint.replace(":", "").trim().lowercase()

    @Suppress("DEPRECATION")
    private fun extractPrimarySignerBytes(info: PackageInfo?): ByteArray? {
        if (info == null) return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signingInfo = info.signingInfo
            val signers = if (signingInfo == null) {
                null
            } else if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }
            val first = signers?.firstOrNull()
            if (first != null) return first.toByteArray()
        }
        return info.signatures?.firstOrNull()?.toByteArray()
    }
}
