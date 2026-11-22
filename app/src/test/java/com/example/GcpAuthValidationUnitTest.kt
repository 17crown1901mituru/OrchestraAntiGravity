package com.example

import com.example.auth.validateWebClientId
import com.example.security.AppSignatureHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class GcpAuthValidationUnitTest {

    @Test
    fun validateWebClientId_checksDomainAndPlaceholders() {
        assertNull(validateWebClientId(null))
        assertNull(validateWebClientId(""))
        assertNull(validateWebClientId("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"))
        assertNull(validateWebClientId("placeholder-id.apps.googleusercontent.com"))
        assertNull(validateWebClientId("123456.example.com"))
        assertEquals(
            "1234567890-abc.apps.googleusercontent.com",
            validateWebClientId(" 1234567890-abc.apps.googleusercontent.com ")
        )
    }

    @Test
    fun appSignatureHelper_formatsAndNormalizesFingerprints() {
        val bytes = "antigravity-release-cert".toByteArray(Charsets.UTF_8)
        val sha1 = AppSignatureHelper.digestColonHex("SHA-1", bytes)
        val sha256 = AppSignatureHelper.digestColonHex("SHA-256", bytes)

        assertEquals(59, sha1.length)
        assertEquals(95, sha256.length)
        val normalized = AppSignatureHelper.stripColonsHex(AppSignatureHelper.REPO_DEBUG_SHA256)
        assertFalse(normalized.contains(":"))
        assertEquals(64, normalized.length)
    }
}
