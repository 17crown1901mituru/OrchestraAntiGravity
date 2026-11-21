package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CloudBridgeClient
import com.example.data.RuntimeCloudBootstrapConfig
import com.example.data.RuntimeCloudBootstrapStore
import com.example.data.StackTemplates
import com.example.model.SyncHealthState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.InetAddress
import java.net.ServerSocket

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RuntimeBootstrapSsoTest {

    @Test
    fun `runtime bootstrap store persists config parses google-services json and resolves database id`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = RuntimeCloudBootstrapStore(context)
        store.clear()

        val sampleJson = """
            {
              "project_info": {
                "project_number": "1234567890",
                "project_id": "antigravity-runtime-proj",
                "storage_bucket": "antigravity-runtime-proj.firebasestorage.app"
              },
              "client": [
                {
                  "client_info": {
                    "mobilesdk_app_id": "1:1234567890:android:other",
                    "android_client_info": { "package_name": "com.other.app" }
                  },
                  "oauth_client": [
                    { "client_id": "wrong-web.apps.googleusercontent.com", "client_type": 3 }
                  ],
                  "api_key": [{ "current_key": "wrong-key" }]
                },
                {
                  "client_info": {
                    "mobilesdk_app_id": "1:1234567890:android:abcdef123456",
                    "android_client_info": { "package_name": "com.example.target" }
                  },
                  "oauth_client": [
                    { "client_id": "android-client-ignored", "client_type": 1 },
                    { "client_id": "1234567890-webclient.apps.googleusercontent.com", "client_type": 3 }
                  ],
                  "api_key": [{ "current_key": "AIzaRuntimeTestKey" }]
                }
              ]
            }
        """.trimIndent()

        val parsed = RuntimeCloudBootstrapStore.parseGoogleServicesJson(
            rawJson = sampleJson,
            targetPackageName = "com.example.target",
            existingGatewayUrl = "https://edge.example.com/"
        )
        assertTrue(parsed.isSuccess)
        val cfg = parsed.getOrThrow().copy(
            firestoreDatabaseId = "custom-db",
            defaultGithubRepo = "owner/my-repo"
        )
        assertEquals("antigravity-runtime-proj", cfg.firebaseProjectId)
        assertEquals("1:1234567890:android:abcdef123456", cfg.firebaseAppId)
        assertEquals("AIzaRuntimeTestKey", cfg.firebaseApiKey)
        assertEquals("1234567890-webclient.apps.googleusercontent.com", cfg.firebaseWebClientId)
        assertTrue(cfg.hasFirebaseRuntimeParams())

        val saveRes = store.save(cfg)
        assertTrue(saveRes.isSuccess)

        val reloaded = store.load()
        assertEquals("https://edge.example.com", reloaded.gatewayUrl)
        assertEquals("antigravity-runtime-proj", reloaded.firebaseProjectId)
        assertEquals("custom-db", reloaded.firestoreDatabaseId)
        assertEquals("owner/my-repo", reloaded.defaultGithubRepo)
        assertEquals("custom-db", RuntimeCloudBootstrapStore.resolveDatabaseId(context))

        store.clear()
        assertFalse(store.load().hasFirebaseRuntimeParams())
    }

    @Test
    fun `cloud bridge client fetches client config and routes sso broker endpoints with X-Firebase-Token`() = runTest {
        val ss = ServerSocket(0, 20, InetAddress.getByName("127.0.0.1"))
        val port = ss.localPort
        val capturedRequests = java.util.Collections.synchronizedList(mutableListOf<Pair<String, String>>())

        val serverThread = Thread {
            runCatching {
                ss.soTimeout = 5000
                repeat(7) {
                    val sock = runCatching { ss.accept() }.getOrNull() ?: return@repeat
                    sock.use { s ->
                        val reader = s.getInputStream().bufferedReader()
                        val reqLine = reader.readLine().orEmpty()
                        var firebaseToken = ""
                        var contentLen = 0
                        while (true) {
                            val h = reader.readLine() ?: break
                            if (h.isEmpty()) break
                            if (h.startsWith("X-Firebase-Token:", ignoreCase = true)) {
                                firebaseToken = h.substringAfter(":").trim()
                            }
                            if (h.startsWith("Content-Length:", ignoreCase = true)) {
                                contentLen = h.substringAfter(":").trim().toIntOrNull() ?: 0
                            }
                        }
                        if (contentLen > 0) {
                            val buf = CharArray(contentLen)
                            reader.read(buf, 0, contentLen)
                        }
                        capturedRequests.add(reqLine to firebaseToken)

                        val body = when {
                            reqLine.startsWith("GET /v1/client-config") -> """
                                {
                                  "gatewayUrl": "http://127.0.0.1:$port",
                                  "firebaseProjectId": "sso-proj-01",
                                  "firebaseAppId": "1:999:android:888",
                                  "firebaseApiKey": "AIzaSsoBrokerKey",
                                  "firebaseWebClientId": "999-sso.apps.googleusercontent.com",
                                  "firestoreDatabaseId": "(default)",
                                  "storageBucket": "sso-proj-01.firebasestorage.app",
                                  "defaultGithubRepo": "octocat/orchestra"
                                }
                            """.trimIndent()
                            reqLine.startsWith("GET /health") -> """{"ok":true,"status":"healthy"}"""
                            reqLine.startsWith("GET /v1/sso/status") -> """
                                {
                                  "ok": true,
                                  "userEmail": "user@example.com",
                                  "github": {
                                    "configured": true,
                                    "accountLogin": "octocat",
                                    "targetRepo": "octocat/orchestra",
                                    "defaultBranch": "main",
                                    "openIssuesCount": 2,
                                    "openPullRequestsCount": 1,
                                    "starsCount": 42,
                                    "workflowName": "antigravity-deploy.yml",
                                    "workflowStatus": "completed",
                                    "workflowConclusion": "success",
                                    "workflowRunId": 556677,
                                    "recentCommits": [{"sha":"abc1234","message":"SSO sync","author":"octocat","committedAt":"2026-11-21T00:00:00Z"}],
                                    "recentEvents": [{"type":"PushEvent","actor":"octocat","detail":"pushed to main","createdAt":"2026-11-21T00:00:00Z"}]
                                  },
                                  "cloudflare": {
                                    "tokenConfigured": true,
                                    "tokenStatus": "active",
                                    "accountId": "cf-acc-123",
                                    "accountName": "EdgeAccount",
                                    "workerCount": 2,
                                    "workers": [
                                      {"scriptId": "gw-worker", "modifiedOn": "2026-11-21T00:00:00Z"},
                                      {"scriptId": "autofix-worker", "modifiedOn": "2026-11-21T00:00:00Z"}
                                    ]
                                  }
                                }
                            """.trimIndent()
                            reqLine.startsWith("POST /v1/github/commit-file") ->
                                """{"ok":true,"commitSha":"deadbeef1234"}"""
                            reqLine.startsWith("POST /v1/dispatch-github") ->
                                """{"ok":true,"dispatched":true,"status":204}"""
                            reqLine.startsWith("POST /v1/github/rerun") ->
                                """{"ok":true,"status":201}"""
                            else -> """{"error":"not found"}"""
                        }

                        val bytes = body.toByteArray(Charsets.UTF_8)
                        val resp = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
                        s.getOutputStream().apply {
                            write(resp.toByteArray(Charsets.UTF_8))
                            write(bytes)
                            flush()
                        }
                    }
                }
            }
        }.apply { start() }

        try {
            val client = CloudBridgeClient()
            val baseUrl = "http://127.0.0.1:$port"

            // 1. Runtime client-config bootstrap
            val cfgRes = client.fetchClientBootstrapConfig(baseUrl)
            assertTrue(cfgRes.isSuccess)
            val cfg = cfgRes.getOrThrow()
            assertEquals("sso-proj-01", cfg.firebaseProjectId)
            assertEquals("999-sso.apps.googleusercontent.com", cfg.firebaseWebClientId)
            assertEquals("octocat/orchestra", cfg.defaultGithubRepo)

            // 2. GitHub sync via Google SSO Broker (pat is blank)
            val ghSync = client.fetchGitHubSyncStatus(
                pat = "",
                ownerRepo = "octocat/orchestra",
                workflowFile = "antigravity-deploy.yml",
                gatewayUrl = baseUrl,
                bearerToken = "",
                firebaseIdToken = "fb_sso_jwt_123"
            )
            assertEquals(SyncHealthState.CONNECTED, ghSync.health)
            assertEquals("octocat", ghSync.accountLogin)
            assertEquals(556677L, ghSync.workflowRunId)
            assertEquals("abc1234", ghSync.recentCommits.first().sha)

            // 3. Cloudflare sync via Google SSO Broker (apiToken is blank)
            val cfSync = client.fetchCloudflareSyncStatus(
                apiToken = "",
                accountId = "",
                gatewayUrl = baseUrl,
                bearerToken = "",
                firebaseIdToken = "fb_sso_jwt_123"
            )
            assertEquals(SyncHealthState.CONNECTED, cfSync.health)
            assertEquals(2, cfSync.workerCount)
            assertEquals("gw-worker", cfSync.workers.first().scriptId)

            // 4. Commit file via Google SSO Broker
            val commitRes = client.commitFileToGitHub(
                pat = "",
                ownerRepo = "octocat/orchestra",
                filePath = "worker/src/worker.js",
                content = "export default {}",
                commitMessage = "Update worker",
                gatewayUrl = baseUrl,
                bearerToken = "",
                firebaseIdToken = "fb_sso_jwt_123"
            )
            assertTrue(commitRes.success)
            assertTrue(commitRes.message.contains("deadbee"))

            // 5. Dispatch workflow via Google SSO Broker
            val dispatchRes = client.dispatchGitHubWorkflow(
                pat = "",
                ownerRepo = "octocat/orchestra",
                workflowFile = "antigravity-deploy.yml",
                gatewayUrl = baseUrl,
                bearerToken = "",
                firebaseIdToken = "fb_sso_jwt_123"
            )
            assertTrue(dispatchRes.success)

            // 6. Rerun failed jobs via Google SSO Broker
            val rerunRes = client.rerunFailedGitHubWorkflow(
                pat = "",
                ownerRepo = "octocat/orchestra",
                runId = 556677L,
                gatewayUrl = baseUrl,
                bearerToken = "",
                firebaseIdToken = "fb_sso_jwt_123"
            )
            assertTrue(rerunRes.success)

            // Verify every authenticated SSO call carried X-Firebase-Token
            val ssoCalls = capturedRequests.filterNot { it.first.startsWith("GET /v1/client-config") }
            assertTrue(ssoCalls.isNotEmpty())
            assertTrue(ssoCalls.all { it.second == "fb_sso_jwt_123" })
        } finally {
            runCatching { ss.close() }
            runCatching { serverThread.join(1000) }
        }
    }

    @Test
    fun `worker templates expose sso broker and client bootstrap endpoints`() {
        val gwWorker = StackTemplates.all.first { it.id == "cf_worker_gc2223" }.content
        val autoFixWorker = StackTemplates.all.first { it.id == "cf_email_autofix_worker" }.content

        listOf(gwWorker, autoFixWorker).forEach { workerCode ->
            assertTrue(workerCode.contains("/v1/client-config"))
            assertTrue(workerCode.contains("/v1/sso/status"))
            assertTrue(workerCode.contains("/v1/github/commit-file"))
            assertTrue(workerCode.contains("/v1/github/rerun"))
            assertTrue(workerCode.contains("/v1/dispatch-github"))
        }
    }
}
