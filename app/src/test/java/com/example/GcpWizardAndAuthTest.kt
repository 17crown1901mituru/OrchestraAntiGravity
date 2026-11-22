package com.example

import android.content.Context
import android.util.Base64
import androidx.test.core.app.ApplicationProvider
import com.example.auth.validateWebClientId
import com.example.data.GcpProvisioningClient
import com.example.data.RuntimeCloudBootstrapConfig
import com.example.data.RuntimeCloudBootstrapStore
import com.example.security.AppSignatureHelper
import com.example.ui.viewmodel.AuthenticationViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.InetAddress
import java.net.ServerSocket
import java.util.Collections

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GcpWizardAndAuthTest {

    @Test
    fun `validateWebClientId rejects blanks placeholders and invalid domains`() {
        assertNull(validateWebClientId(null))
        assertNull(validateWebClientId(""))
        assertNull(validateWebClientId("   "))
        assertNull(validateWebClientId("YOUR_CLIENT_ID.apps.googleusercontent.com"))
        assertNull(validateWebClientId("placeholder-123.apps.googleusercontent.com"))
        assertNull(validateWebClientId("abc.apps.googleusercontent.com"))
        assertNull(validateWebClientId("1234567890-valid.example.com"))

        assertEquals(
            "1234567890-abcdef.apps.googleusercontent.com",
            validateWebClientId(" 1234567890-abcdef.apps.googleusercontent.com ")
        )
    }

    @Test
    fun `authentication viewmodel validates configured web client id and dialog transitions`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = RuntimeCloudBootstrapStore(context)
        store.clear()

        store.save(
            RuntimeCloudBootstrapConfig(
                gatewayUrl = "https://gc2223.com",
                firebaseProjectId = "test-proj-01",
                firebaseAppId = "1:111:android:222",
                firebaseApiKey = "AIzaTestKey",
                firebaseWebClientId = "9876543210-validclient.apps.googleusercontent.com"
            )
        )

        val vm = AuthenticationViewModel(appContext = context, bootstrapStore = store)
        vm.refreshConfigurationState()

        val state = vm.uiState.value
        assertEquals("https://gc2223.com", state.gatewayUrl)
        assertTrue(state.isWebClientIdValid)
        assertEquals("9876543210-validclient.apps.googleusercontent.com", state.configuredWebClientId)

        vm.openBootstrapDialog()
        assertTrue(vm.uiState.value.showBootstrapDialog)
        vm.dismissBootstrapDialog()
        assertFalse(vm.uiState.value.showBootstrapDialog)

        store.clear()
    }

    @Test
    fun `portable backup export and import preserves runtime bootstrap config across uninstall`() {
        val original = RuntimeCloudBootstrapConfig(
            gatewayUrl = "https://gc2223.com/",
            firebaseProjectId = "proj-backup-99",
            firebaseAppId = "1:99:android:abc",
            firebaseApiKey = "AIzaBackupKey",
            firebaseWebClientId = "999999-backup.apps.googleusercontent.com",
            firestoreDatabaseId = "prod-db",
            storageBucket = "proj-backup-99.firebasestorage.app",
            defaultGithubRepo = "owner/antigravity"
        )

        val exported = RuntimeCloudBootstrapStore.exportPortableJson(original)
        assertTrue(exported.contains("antigravity-bootstrap-v1"))

        val imported = RuntimeCloudBootstrapStore.importPortableOrGoogleServicesJson(
            rawJson = exported,
            targetPackageName = "com.gc2223.antigravity"
        )
        assertTrue(imported.isSuccess)
        val restored = imported.getOrThrow()
        assertEquals("https://gc2223.com", restored.gatewayUrl)
        assertEquals("proj-backup-99", restored.firebaseProjectId)
        assertEquals("999999-backup.apps.googleusercontent.com", restored.firebaseWebClientId)
        assertEquals("prod-db", restored.firestoreDatabaseId)
        assertEquals("owner/antigravity", restored.defaultGithubRepo)
    }

    @Test
    fun `app signature helper computes deterministic colon-hex digests`() {
        val sampleCert = "antigravity-cert".toByteArray(Charsets.UTF_8)
        val sha1 = AppSignatureHelper.digestColonHex("SHA-1", sampleCert)
        val sha256 = AppSignatureHelper.digestColonHex("SHA-256", sampleCert)

        assertEquals(20 * 2 + 19, sha1.length)
        assertEquals(32 * 2 + 31, sha256.length)
        assertFalse(AppSignatureHelper.stripColonsHex(AppSignatureHelper.REPO_DEBUG_SHA256).contains(":"))
        assertEquals(64, AppSignatureHelper.stripColonsHex(AppSignatureHelper.REPO_DEBUG_SHA256).length)
    }

    @Test
    fun `gcp provisioning client creates projects enables apis registers sha and fetches firebase config`() = runTest {
        val ss = ServerSocket(0, 30, InetAddress.getByName("127.0.0.1"))
        val port = ss.localPort
        val capturedShaPosts = Collections.synchronizedList(mutableListOf<String>())

        val googleServicesPayload = """
            {
              "project_info": {
                "project_number": "777888999",
                "project_id": "ag-gui-proj",
                "storage_bucket": "ag-gui-proj.firebasestorage.app"
              },
              "client": [
                {
                  "client_info": {
                    "mobilesdk_app_id": "1:777888999:android:555aaa",
                    "android_client_info": { "package_name": "com.gc2223.antigravity" }
                  },
                  "oauth_client": [
                    { "client_id": "777888999-gui.apps.googleusercontent.com", "client_type": 3 }
                  ],
                  "api_key": [{ "current_key": "AIzaGuiSyncedKey" }]
                }
              ]
            }
        """.trimIndent()
        val encodedGs = Base64.encodeToString(googleServicesPayload.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

        val serverThread = Thread {
            runCatching {
                ss.soTimeout = 5000
                repeat(12) {
                    val sock = runCatching { ss.accept() }.getOrNull() ?: return@repeat
                    sock.use { s ->
                        val reader = s.getInputStream().bufferedReader()
                        val reqLine = reader.readLine().orEmpty()
                        var contentLen = 0
                        while (true) {
                            val line = reader.readLine() ?: break
                            if (line.isEmpty()) break
                            if (line.startsWith("Content-Length:", ignoreCase = true)) {
                                contentLen = line.substringAfter(":").trim().toIntOrNull() ?: 0
                            }
                        }
                        val reqBody = if (contentLen > 0) {
                            val buf = CharArray(contentLen)
                            reader.read(buf, 0, contentLen)
                            String(buf)
                        } else ""

                        val respJson = when {
                            reqLine.startsWith("GET /v1/projects?") -> """
                                {"projects":[{"projectId":"existing-proj","name":"Existing Proj","projectNumber":"1001","lifecycleState":"ACTIVE"}]}
                            """.trimIndent()
                            reqLine.startsWith("POST /v1/projects ") -> """{"name":"operations/cp-1","done":false}"""
                            reqLine.startsWith("GET /v1/operations/cp-1") -> """{"name":"operations/cp-1","done":true}"""
                            reqLine.startsWith("GET /v1/projects/ag-gui-proj") -> """
                                {"projectId":"ag-gui-proj","name":"AG GUI Proj","projectNumber":"777888999","lifecycleState":"ACTIVE"}
                            """.trimIndent()
                            reqLine.startsWith("GET /v1/projects/ag-gui-proj/services") -> """
                                {"services":[{"name":"projects/777888999/services/firebase.googleapis.com"}]}
                            """.trimIndent()
                            reqLine.startsWith("POST /v1/projects/ag-gui-proj/services:batchEnable") -> """{"name":"operations/be-1","done":true}"""
                            reqLine.startsWith("POST /v1beta1/projects/ag-gui-proj:addFirebase") -> """{"name":"operations/fb-1","done":true}"""
                            reqLine.startsWith("GET /v1beta1/projects/ag-gui-proj/androidApps ") -> """
                                {"apps":[{"appId":"1:777888999:android:555aaa","packageName":"com.gc2223.antigravity"}]}
                            """.trimIndent()
                            reqLine.startsWith("GET /v1beta1/projects/ag-gui-proj/androidApps/1:777888999:android:555aaa/sha") -> """
                                {"certificates":[]}
                            """.trimIndent()
                            reqLine.startsWith("POST /v1beta1/projects/ag-gui-proj/androidApps/1:777888999:android:555aaa/sha") -> {
                                capturedShaPosts.add(reqBody)
                                """{"name":"sha-entry","shaHash":"ok"}"""
                            }
                            reqLine.startsWith("GET /v1beta1/projects/ag-gui-proj/androidApps/1:777888999:android:555aaa/config") -> """
                                {"configFilename":"google-services.json","configFileContents":"$encodedGs"}
                            """.trimIndent()
                            else -> """{}"""
                        }

                        val bytes = respJson.toByteArray(Charsets.UTF_8)
                        val headers = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
                        s.getOutputStream().apply {
                            write(headers.toByteArray(Charsets.UTF_8))
                            write(bytes)
                            flush()
                        }
                    }
                }
            }
        }.apply { start() }

        try {
            val base = "http://127.0.0.1:$port"
            val client = GcpProvisioningClient(
                resourceManagerBaseUrl = base,
                serviceUsageBaseUrl = base,
                firebaseMgmtBaseUrl = base,
                billingBaseUrl = base
            )

            val listRes = client.listProjects("ya29.test-token")
            assertTrue(listRes.isSuccess)
            assertEquals("existing-proj", listRes.getOrThrow().first().projectId)

            val createRes = client.createProject("ya29.test-token", "ag-gui-proj", "AG GUI Proj")
            assertTrue(createRes.isSuccess)
            assertEquals("ag-gui-proj", createRes.getOrThrow().projectId)

            val svcRes = client.listEnabledServices("ya29.test-token", "ag-gui-proj")
            assertTrue(svcRes.isSuccess)
            assertTrue(svcRes.getOrThrow().contains("firebase.googleapis.com"))

            val batchRes = client.batchEnableServices(
                "ya29.test-token",
                "ag-gui-proj",
                listOf("firebase.googleapis.com", "firestore.googleapis.com")
            )
            assertTrue(batchRes.isSuccess)
            assertEquals(2, batchRes.getOrThrow().size)

            val fbRes = client.ensureFirebaseAndFetchAndroidConfig(
                accessToken = "ya29.test-token",
                projectId = "ag-gui-proj",
                packageName = "com.gc2223.antigravity",
                existingGatewayUrl = "https://gc2223.com",
                sha1 = AppSignatureHelper.REPO_DEBUG_SHA1,
                sha256 = AppSignatureHelper.REPO_DEBUG_SHA256
            )
            assertTrue(fbRes.isSuccess)
            val sync = fbRes.getOrThrow()
            assertEquals("1:777888999:android:555aaa", sync.androidAppId)
            assertEquals(2, sync.registeredShaCount)
            assertEquals(2, capturedShaPosts.size)
            assertEquals("777888999-gui.apps.googleusercontent.com", sync.bootstrapConfig.firebaseWebClientId)
        } finally {
            runCatching { ss.close() }
            runCatching { serverThread.join(1000) }
        }
    }
}
