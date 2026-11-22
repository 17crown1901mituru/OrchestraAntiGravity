package com.example

import com.example.connectors.ConnectorContext
import com.example.connectors.GitHubOAuthConnector
import com.example.connectors.RemoteMcpConnector
import com.example.connectors.TermuxCliConnector
import com.example.data.GcpProvisioningClient
import com.example.model.ExternalCredentials
import com.example.model.McpEndpointEntry
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Step2ConnectorsAndDiscoveryUnitTest {

    private data class RecordedHttpRequest(
        val method: String,
        val path: String,
        val headers: Map<String, String>,
        val body: String
    )

    private fun withMockHttpServer(
        handler: (RecordedHttpRequest) -> Pair<Int, String>
    ): Pair<String, AutoCloseable> {
        val server = ServerSocket(0, 16, InetAddress.getByName("127.0.0.1"))
        val running = java.util.concurrent.atomic.AtomicBoolean(true)
        val thread = Thread {
            while (running.get() && !server.isClosed) {
                val socket = try {
                    server.accept()
                } catch (_: Exception) {
                    break
                }
                try {
                    socket.soTimeout = 3000
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                    val requestLine = reader.readLine() ?: continue
                    val parts = requestLine.split(" ")
                    val method = parts.getOrElse(0) { "GET" }
                    val path = parts.getOrElse(1) { "/" }
                    val headers = linkedMapOf<String, String>()
                    var contentLength = 0
                    while (true) {
                        val line = reader.readLine() ?: break
                        if (line.isEmpty()) break
                        val colon = line.indexOf(':')
                        if (colon > 0) {
                            val k = line.substring(0, colon).trim().lowercase()
                            val v = line.substring(colon + 1).trim()
                            headers[k] = v
                            if (k == "content-length") {
                                contentLength = v.toIntOrNull() ?: 0
                            }
                        }
                    }
                    val bodyChars = if (contentLength > 0) {
                        val buf = CharArray(contentLength)
                        var read = 0
                        while (read < contentLength) {
                            val n = reader.read(buf, read, contentLength - read)
                            if (n <= 0) break
                            read += n
                        }
                        String(buf, 0, read)
                    } else {
                        ""
                    }
                    val (status, respBody) = handler(RecordedHttpRequest(method, path, headers, bodyChars))
                    val bodyBytes = respBody.toByteArray(StandardCharsets.UTF_8)
                    val out = socket.getOutputStream()
                    val statusText = if (status in 200..299) "OK" else "ERR"
                    val headerStr = "HTTP/1.1 $status $statusText\r\n" +
                        "Content-Type: application/json; charset=utf-8\r\n" +
                        "Content-Length: ${bodyBytes.size}\r\n" +
                        "Connection: close\r\n\r\n"
                    out.write(headerStr.toByteArray(StandardCharsets.UTF_8))
                    out.write(bodyBytes)
                    out.flush()
                } finally {
                    runCatching { socket.close() }
                }
            }
        }
        thread.isDaemon = true
        thread.start()
        val baseUrl = "http://127.0.0.1:${server.localPort}"
        return baseUrl to AutoCloseable {
            running.set(false)
            runCatching { server.close() }
        }
    }

    @Test
    fun discoverAccountResources_fetchesOrganizationsProjectsBillingAndGeminiModels() = runBlocking {
        val seenPaths = CopyOnWriteArrayList<String>()
        val (baseUrl, closeable) = withMockHttpServer { req ->
            seenPaths.add("${req.method} ${req.path}")
            when {
                req.path.startsWith("/oauth2/v3/userinfo") ->
                    200 to """{"sub":"10987654321","email":"builder@example.com","name":"Builder"}"""
                req.path.startsWith("/v1/organizations:search") ->
                    200 to """{"organizations":[{"name":"organizations/998877","displayName":"acme-org.example","owner":{"directoryCustomerId":"C0123"}}]}"""
                req.path.startsWith("/v1/projects") ->
                    200 to """{"projects":[{"projectId":"acme-hub-prod","name":"Acme Hub","projectNumber":"123456","lifecycleState":"ACTIVE","parent":{"type":"organization","id":"998877"}}]}"""
                req.path.startsWith("/v1/billingAccounts") ->
                    200 to """{"billingAccounts":[{"name":"billingAccounts/01A2B3-C4D5E6","displayName":"Main Billing","open":true}]}"""
                req.path.startsWith("/v1beta/models") ->
                    200 to """{"models":[{"name":"models/gemini-2.5-flash","displayName":"Gemini 2.5 Flash","supportedGenerationMethods":["generateContent"],"inputTokenLimit":1048576}]}"""
                else -> 404 to """{"error":{"message":"unexpected path ${req.path}"}}"""
            }
        }

        closeable.use {
            val client = GcpProvisioningClient(
                client = OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build(),
                resourceManagerBaseUrl = baseUrl,
                serviceUsageBaseUrl = baseUrl,
                firebaseMgmtBaseUrl = baseUrl,
                billingBaseUrl = baseUrl,
                userInfoBaseUrl = baseUrl,
                generativeLanguageBaseUrl = baseUrl
            )
            val result = client.discoverAccountResources("ya29.fake-oauth-token").getOrThrow()
            assertEquals("10987654321", result.userInfo?.sub)
            assertEquals(1, result.organizations.size)
            assertEquals("998877", result.organizations.first().orgId)
            assertEquals(1, result.projects.size)
            assertEquals("organization", result.projects.first().parentType)
            assertEquals("998877", result.projects.first().parentId)
            assertEquals(1, result.billingAccounts.size)
            assertEquals(1, result.geminiModels.size)
            assertEquals("gemini-2.5-flash", result.geminiModels.first().modelId)
        }
    }

    @Test
    fun gitHubOAuthConnector_deviceFlowAndOrgRepoDiscovery_succeeds() = runBlocking {
        val (baseUrl, closeable) = withMockHttpServer { req ->
            when {
                req.path == "/login/device/code" ->
                    200 to """{"device_code":"dc_xyz","user_code":"ABCD-1234","verification_uri":"https://github.com/login/device","expires_in":900,"interval":5}"""
                req.path == "/login/oauth/access_token" ->
                    200 to """{"access_token":"gho_test_token_123","token_type":"bearer","scope":"repo,workflow,read:org"}"""
                req.path == "/user" ->
                    200 to """{"login":"octocat"}"""
                req.path.startsWith("/user/orgs") ->
                    200 to """[{"login":"acme-robotics","description":"Acme Org"}]"""
                req.path.startsWith("/orgs/acme-robotics/repos") ->
                    200 to """[{"full_name":"acme-robotics/antigravity-app","default_branch":"main","private":true,"open_issues_count":2}]"""
                else -> 404 to "{}"
            }
        }

        closeable.use {
            val connector = GitHubOAuthConnector(
                githubWebBaseUrl = baseUrl,
                githubApiBaseUrl = baseUrl
            )
            val challenge = connector.startDeviceFlow("Ov23liTestClient").getOrThrow()
            assertEquals("ABCD-1234", challenge.userCode)
            assertEquals("dc_xyz", challenge.deviceCode)

            val token = connector.pollDeviceFlowToken("Ov23liTestClient", challenge.deviceCode).getOrThrow()
            assertEquals("gho_test_token_123", token)

            val summary = connector.fetchUserAndOrgs(token, selectedOrg = "acme-robotics").getOrThrow()
            assertEquals("octocat", summary.login)
            assertEquals(listOf("acme-robotics"), summary.organizations.map { it.login })
            assertEquals(listOf("acme-robotics/antigravity-app"), summary.repos.map { it.fullName })

            val ctx = ConnectorContext(googleSub = "10987654321", virtualAlias = "u-aabbccddeeff@gc2223.com")
            val creds = ExternalCredentials(githubPat = token, githubSelectedOrg = "acme-robotics")
            val callRes = connector.callTool(ctx, creds, "github_list_orgs_and_repos", JSONObject()).getOrThrow()
            assertTrue(callRes.ok)
            assertTrue(callRes.output.contains("acme-robotics/antigravity-app"))
        }
    }

    @Test
    fun remoteMcpAndTermuxConnectors_propagateGoogleSubVirtualAliasHeaders() = runBlocking {
        val captured = CopyOnWriteArrayList<RecordedHttpRequest>()
        val (baseUrl, closeable) = withMockHttpServer { req ->
            captured.add(req)
            when {
                req.path == "/mcp" && req.method == "POST" -> {
                    val rpc = JSONObject(req.body)
                    val method = rpc.optString("method")
                    val id = rpc.optString("id")
                    when (method) {
                        "tools/list" -> 200 to """{"jsonrpc":"2.0","id":"$id","result":{"tools":[{"name":"mcp_inspect_session_context","description":"Inspect context","inputSchema":{"type":"object"}}]}}"""
                        "tools/call" -> 200 to """{"jsonrpc":"2.0","id":"$id","result":{"isError":false,"content":[{"type":"text","text":"{\"zeroPii\":true}"}]}}"""
                        else -> 404 to "{}"
                    }
                }
                req.path == "/run" && req.method == "POST" ->
                    200 to """{"ok":true,"stdout":"git status clean"}"""
                else -> 404 to "{}"
            }
        }

        closeable.use {
            val ctx = ConnectorContext(
                googleSub = "sub-999111",
                virtualAlias = "u-112233445566@gc2223.com",
                googleIdToken = "eyJtest.eyJsub.sig",
                gcpProjectId = "acme-hub-prod"
            )
            val creds = ExternalCredentials(
                termuxAgentUrl = baseUrl,
                mcpEndpoints = listOf(
                    McpEndpointEntry(id = "local_test", label = "Local Mock MCP", url = baseUrl, isActive = true)
                )
            )

            val remoteMcp = RemoteMcpConnector()
            val tools = remoteMcp.listTools(ctx, creds).getOrThrow()
            assertEquals(1, tools.size)
            assertEquals("mcp_inspect_session_context", tools.first().name)

            val mcpResult = remoteMcp.callTool(ctx, creds, "mcp_inspect_session_context", JSONObject()).getOrThrow()
            assertTrue(mcpResult.ok)
            assertTrue(mcpResult.output.contains("zeroPii"))

            val mcpReq = captured.first { it.path == "/mcp" }
            assertEquals("u-112233445566@gc2223.com", mcpReq.headers["x-virtual-alias"])
            assertEquals("sub-999111", mcpReq.headers["x-google-sub"])
            assertEquals("eyJtest.eyJsub.sig", mcpReq.headers["x-google-id-token"])

            val termux = TermuxCliConnector()
            val termuxRes = termux.callTool(ctx, creds, "termux_git_status", JSONObject()).getOrThrow()
            assertTrue(termuxRes.ok)
            assertFalse(termuxRes.output.isBlank())

            val termuxReq = captured.first { it.path == "/run" }
            assertEquals("sub-999111", termuxReq.headers["x-google-sub"])
            assertEquals("u-112233445566@gc2223.com", termuxReq.headers["x-virtual-alias"])
            assertEquals("acme-hub-prod", termuxReq.headers["x-gcp-project"])
        }
    }
}
