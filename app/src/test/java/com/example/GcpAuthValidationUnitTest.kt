package com.example

import com.example.auth.GoogleAccountSession
import com.example.auth.validateWebClientId
import com.example.security.AppSignatureHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun googleAccountSession_buildsDeterministicVirtualAliasFromSub() {
        val session = GoogleAccountSession.create(
            sub = "108239481029384019283",
            accessToken = "ya29.test-oauth-token"
        )
        assertTrue(session.isValid)
        assertEquals("108239481029384019283", session.sub)
        assertTrue(Regex("^u-[0-9a-f]{12}@gc2223\\.com$").matches(session.virtualAlias))
        assertEquals(
            session.virtualAlias,
            GoogleAccountSession.buildVirtualAlias(" 108239481029384019283 ")
        )

        val invalidSession = GoogleAccountSession.create(sub = "108239481029384019283")
        assertFalse(invalidSession.isValid)
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

