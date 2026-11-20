package com.example

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.example.data.CloudBridgeClient
import com.example.data.GitHubActivityService
import com.example.data.LocalSecretsStore
import com.example.data.MultimodalSourceLoader
import com.example.data.OrchestrationHardeningClient
import com.example.data.StackTemplates
import com.example.data.SyncSnapshotStore
import com.example.data.WebContextCrawler
import com.example.model.BackgroundSyncSettings
import com.example.model.CloudflareSyncStatus
import com.example.model.CloudflareWorkerSummary
import com.example.model.CrawlMode
import com.example.model.DeviceViewportProfile
import com.example.model.EmailRouteTarget
import com.example.model.ExternalCredentials
import com.example.model.GitHubActivityEvent
import com.example.model.GitHubCommitSummary
import com.example.model.GitHubRepoSummary
import com.example.model.GitHubSyncStatus
import com.example.model.OrchestrationTaskType
import com.example.model.SourceAttachmentKind
import com.example.model.SyncHealthState
import com.example.model.SyncStatusSnapshot
import com.example.model.TerraformAction
import com.example.model.TermuxAgentSyncStatus
import com.example.model.TermuxPresetCommand
import com.example.sync.CloudStatusSyncWorker
import com.example.sync.TermuxAgentSyncWorker
import com.google.firebase.ai.type.InlineDataPart
import com.google.firebase.ai.type.TextPart
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AntiGravity Hub", appName)
  }

  @Test
  fun `local secrets store requires firebase uid and reports metadata`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = LocalSecretsStore(context)

    val saveBlank = store.saveForUser("", "17crown2223@gmail.com", ExternalCredentials())
    assertTrue(saveBlank.isFailure)

    val meta = store.getVaultMetadata("uid_1234", "17crown2223@gmail.com")
    assertEquals("uid_1234", meta.linkedUid)
    assertEquals("17crown2223@gmail.com", meta.linkedEmail)
    assertEquals("AES-256-GCM (AndroidKeyStore)", meta.cipherAlgorithm)
    assertFalse(meta.hasStoredSecrets)

    val loaded = store.loadForUser("uid_1234")
    assertEquals("https://gc2223.com", loaded.gc2223Endpoint)
    assertEquals("gen-lang-client-0439741065", loaded.gcpProjectId)
  }

  @Test
  fun `sync snapshot store persists github and cloudflare statuses per user`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = SyncSnapshotStore(context)

    val snapshot = SyncStatusSnapshot(
      userId = "uid_alpha",
      syncedAtEpochMs = 1_700_000_000_000L,
      triggerSource = "MANUAL",
      github = GitHubSyncStatus(
        health = SyncHealthState.CONNECTED,
        accountLogin = "17crown2223",
        targetRepo = "17crown1901mituru/OrchestraAntiGravity",
        defaultBranch = "main",
        openIssuesCount = 1,
        openPullRequestsCount = 2,
        starsCount = 14,
        workflowName = "deploy-gc2223.yml",
        workflowStatus = "completed",
        workflowConclusion = "success",
        recentRepos = listOf(
          GitHubRepoSummary("17crown1901mituru/OrchestraAntiGravity", "main", true, 2, "2026-11-18T10:00:00Z")
        ),
        recentCommits = listOf(
          GitHubCommitSummary("abc1234", "Add activity summary service", "17crown1901mituru", "2026-11-18T09:55:00Z")
        ),
        recentEvents = listOf(
          GitHubActivityEvent("PushEvent", "17crown1901mituru", "pushed 2 commit(s) to main", "2026-11-18T09:56:00Z")
        ),
        summaryMessage = "Synced cleanly"
      ),
      cloudflare = CloudflareSyncStatus(
        health = SyncHealthState.CONNECTED,
        tokenStatus = "active",
        accountName = "gc2223",
        accountId = "acc_999",
        workerCount = 1,
        workers = listOf(CloudflareWorkerSummary("antigravity-edge", "2026-11-18T09:00:00Z")),
        gatewayUrl = "https://gc2223.com",
        gatewayHttpCode = 200,
        gatewayLatencyMs = 42L,
        gatewayHealthMessage = "HTTP 200: ok",
        summaryMessage = "1 Worker running"
      )
    )

    store.saveSnapshot("uid_alpha", snapshot)
    store.saveSettings("uid_alpha", BackgroundSyncSettings(enabled = true, intervalMinutes = 30L))

    val loaded = store.loadSnapshot("uid_alpha")
    assertEquals(SyncHealthState.CONNECTED, loaded.github.health)
    assertEquals("17crown1901mituru/OrchestraAntiGravity", loaded.github.targetRepo)
    assertEquals(2, loaded.github.openPullRequestsCount)
    assertEquals(14, loaded.github.starsCount)
    assertEquals(1, loaded.github.recentRepos.size)
    assertEquals("abc1234", loaded.github.recentCommits.first().sha)
    assertEquals("PushEvent", loaded.github.recentEvents.first().type)
    assertEquals(1, loaded.cloudflare.workerCount)
    assertEquals("antigravity-edge", loaded.cloudflare.workers.first().scriptId)
    assertEquals(42L, loaded.cloudflare.gatewayLatencyMs)

    val otherUser = store.loadSnapshot("uid_beta")
    assertEquals(SyncHealthState.NEVER_SYNCED, otherUser.github.health)

    val settings = store.loadSettings("uid_alpha")
    assertTrue(settings.enabled)
    assertEquals(30L, settings.intervalMinutes)

    store.clearForUser("uid_alpha")
    assertEquals(0L, store.loadSnapshot("uid_alpha").syncedAtEpochMs)
  }

  @Test
  fun `cloud status sync worker skips cleanly without uid`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val worker = TestListenableWorkerBuilder<CloudStatusSyncWorker>(context)
      .setInputData(workDataOf(CloudStatusSyncWorker.KEY_USER_ID to ""))
      .build()

    val result = worker.doWork()
    assertEquals(ListenableWorker.Result.success(), result)
  }

  @Test
  fun `sync snapshot store preserves cloud and termux snapshots independently`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = SyncSnapshotStore(context)

    store.saveCloudSnapshot(
      uid = "uid_gamma",
      github = GitHubSyncStatus(
        health = SyncHealthState.CONNECTED,
        targetRepo = "17crown2223/antigravity",
        summaryMessage = "Cloud OK"
      ),
      cloudflare = CloudflareSyncStatus(
        health = SyncHealthState.CONNECTED,
        workerCount = 2,
        summaryMessage = "2 Workers"
      ),
      syncedAtEpochMs = 1_700_000_001_000L,
      triggerSource = "PERIODIC"
    )

    store.saveTermuxSnapshot(
      uid = "uid_gamma",
      termux = TermuxAgentSyncStatus(
        health = SyncHealthState.CONNECTED,
        endpointUrl = "http://127.0.0.1:8787",
        daemonState = "RUNNING",
        pid = 4242,
        lastCommand = "antigravity health",
        requestsToday = 7,
        summaryMessage = "AntiGravityAgent RUNNING"
      ),
      syncedAtEpochMs = 1_700_000_002_000L,
      triggerSource = "MANUAL"
    )

    val merged = store.loadSnapshot("uid_gamma")
    assertEquals(1_700_000_001_000L, merged.syncedAtEpochMs)
    assertEquals("Cloud OK", merged.github.summaryMessage)
    assertEquals(2, merged.cloudflare.workerCount)
    assertEquals(1_700_000_002_000L, merged.termuxSyncedAtEpochMs)
    assertEquals("RUNNING", merged.termux.daemonState)
    assertEquals(4242, merged.termux.pid)
    assertEquals("antigravity health", merged.termux.lastCommand)

    store.clearForUser("uid_gamma")
    assertEquals(0L, store.loadSnapshot("uid_gamma").termuxSyncedAtEpochMs)
  }

  @Test
  fun `termux agent sync worker handles blank uid and pulls status cleanly`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val skipWorker = TestListenableWorkerBuilder<TermuxAgentSyncWorker>(context)
      .setInputData(workDataOf(TermuxAgentSyncWorker.KEY_USER_ID to ""))
      .build()
    assertEquals(ListenableWorker.Result.success(), skipWorker.doWork())

    val payload = """
      {
        "agentName": "AntiGravityAgent",
        "version": "1.2.0",
        "state": "RUNNING",
        "pid": 7788,
        "activeTask": "worker-sync",
        "lastCommand": "antigravity health",
        "lastExitCode": 0,
        "lastRunAt": "2026-11-18T12:34:56Z",
        "requestsToday": 5,
        "uptimeSeconds": 120
      }
    """.trimIndent()

    val serverSocket = runCatching {
      java.net.ServerSocket(8787, 1, java.net.InetAddress.getByName("127.0.0.1"))
    }.getOrNull()

    val serverThread = serverSocket?.let { ss ->
      Thread {
        runCatching {
          ss.soTimeout = 4000
          ss.accept().use { sock ->
            val reader = sock.getInputStream().bufferedReader()
            while (true) {
              val line = reader.readLine() ?: break
              if (line.isEmpty()) break
            }
            val bodyBytes = payload.toByteArray(Charsets.UTF_8)
            val headers = "HTTP/1.1 200 OK\r\n" +
              "Content-Type: application/json\r\n" +
              "Content-Length: ${bodyBytes.size}\r\n" +
              "Connection: close\r\n\r\n"
            sock.getOutputStream().apply {
              write(headers.toByteArray(Charsets.UTF_8))
              write(bodyBytes)
              flush()
            }
          }
        }
      }.apply { start() }
    }

    try {
      val worker = TestListenableWorkerBuilder<TermuxAgentSyncWorker>(context)
        .setInputData(
          workDataOf(
            TermuxAgentSyncWorker.KEY_USER_ID to "uid_termux_test",
            TermuxAgentSyncWorker.KEY_TRIGGER_SOURCE to "MANUAL"
          )
        )
        .build()

      val result = worker.doWork()
      assertTrue(result is ListenableWorker.Result.Success)

      val snapshot = SyncSnapshotStore(context).loadSnapshot("uid_termux_test")
      assertTrue(snapshot.termuxSyncedAtEpochMs > 0L)
      assertEquals("http://127.0.0.1:8787", snapshot.termux.endpointUrl)
      if (serverSocket != null) {
        assertEquals(SyncHealthState.CONNECTED, snapshot.termux.health)
        assertEquals("RUNNING", snapshot.termux.daemonState)
        assertEquals(7788, snapshot.termux.pid)
        assertEquals("antigravity health", snapshot.termux.lastCommand)
        assertEquals(5, snapshot.termux.requestsToday)
      } else {
        assertTrue(
          snapshot.termux.health == SyncHealthState.OFFLINE ||
            snapshot.termux.health == SyncHealthState.ERROR
        )
      }
    } finally {
      runCatching { serverSocket?.close() }
      runCatching { serverThread?.join(1000) }
    }
  }

  @Test
  fun `github activity service fetches commits open prs events and workflow summary`() = runTest {
    val ss = java.net.ServerSocket(0, 10, java.net.InetAddress.getByName("127.0.0.1"))
    val port = ss.localPort
    val serverThread = Thread {
      runCatching {
        ss.soTimeout = 5000
        while (!ss.isClosed) {
          val sock = runCatching { ss.accept() }.getOrNull() ?: break
          sock.use {
            val reader = it.getInputStream().bufferedReader()
            val requestLine = reader.readLine().orEmpty()
            while (true) {
              val headerLine = reader.readLine() ?: break
              if (headerLine.isEmpty()) break
            }
            val path = requestLine.split(" ").getOrNull(1).orEmpty()
            val body = when {
              path.startsWith("/user/repos") -> """
                [{"full_name":"17crown1901mituru/OrchestraAntiGravity","default_branch":"main","private":false,"open_issues_count":3,"pushed_at":"2026-11-18T12:00:00Z"}]
              """.trimIndent()
              path.startsWith("/user") -> """{"login":"17crown1901mituru"}"""
              path.contains("/actions/runs") -> """
                {"workflow_runs":[{"name":"Deploy","status":"completed","conclusion":"success","updated_at":"2026-11-18T11:50:00Z"}]}
              """.trimIndent()
              path.contains("/commits") -> """
                [{"sha":"1234567890abcdef","commit":{"message":"Wire GitHub activity summary service\n\nDetails","author":{"name":"Mitsuru","date":"2026-11-18T11:40:00Z"}}}]
              """.trimIndent()
              path.contains("/pulls") -> """
                [{"id":101,"number":7,"title":"Add Termux sync"}]
              """.trimIndent()
              path.contains("/events") -> """
                [{"type":"PushEvent","actor":{"login":"17crown1901mituru"},"payload":{"ref":"refs/heads/main","size":2},"created_at":"2026-11-18T11:45:00Z"}]
              """.trimIndent()
              else -> """
                {"full_name":"17crown1901mituru/OrchestraAntiGravity","default_branch":"main","private":false,"open_issues_count":3,"stargazers_count":9,"pushed_at":"2026-11-18T12:00:00Z"}
              """.trimIndent()
            }
            val bytes = body.toByteArray(Charsets.UTF_8)
            val resp = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
            it.getOutputStream().apply {
              write(resp.toByteArray(Charsets.UTF_8))
              write(bytes)
              flush()
            }
          }
        }
      }
    }.apply { start() }

    try {
      val service = GitHubActivityService(apiBaseUrl = "http://127.0.0.1:$port")
      val summary = service.fetchRepositoryActivitySummary(
        pat = "test_pat_token",
        ownerRepo = "17crown1901mituru/OrchestraAntiGravity",
        workflowFile = "deploy.yml"
      )
      assertEquals(SyncHealthState.CONNECTED, summary.health)
      assertEquals("17crown1901mituru", summary.accountLogin)
      assertEquals("17crown1901mituru/OrchestraAntiGravity", summary.targetRepo)
      assertEquals(1, summary.openPullRequestsCount)
      assertEquals(2, summary.openIssuesCount)
      assertEquals(9, summary.starsCount)
      assertEquals("1234567", summary.recentCommits.first().sha)
      assertEquals("Wire GitHub activity summary service", summary.recentCommits.first().message)
      assertEquals("pushed 2 commit(s) to main", summary.recentEvents.first().detail)
      assertEquals("success", summary.workflowConclusion)
    } finally {
      runCatching { ss.close() }
      runCatching { serverThread.join(1000) }
    }
  }

  @Test
  fun `multimodal source loader inspects markdown pdf video and builds inline content`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val loader = MultimodalSourceLoader(context)

    val tempDir = context.cacheDir
    val mdFile = java.io.File(tempDir, "architecture.md").apply {
      writeText("# Architecture\nUse Cloudflare Workers + Termux CLI.")
    }
    val pdfFile = java.io.File(tempDir, "spec.pdf").apply {
      writeBytes("%PDF-1.4\n1 0 obj\n<<>>\nendobj\n".toByteArray())
    }
    val mp4File = java.io.File(tempDir, "demo.mp4").apply {
      writeBytes(byteArrayOf(0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70))
    }

    try {
      val mdSource = loader.inspectUri(Uri.fromFile(mdFile))!!
      val pdfSource = loader.inspectUri(Uri.fromFile(pdfFile))!!
      val videoSource = loader.inspectUri(Uri.fromFile(mp4File))!!

      assertEquals(SourceAttachmentKind.MARKDOWN, mdSource.kind)
      assertEquals(SourceAttachmentKind.PDF, pdfSource.kind)
      assertEquals("application/pdf", pdfSource.mimeType)
      assertEquals(SourceAttachmentKind.VIDEO, videoSource.kind)

      val contentResult = loader.buildMultimodalContent(
        instructionPrompt = "Assemble solution",
        sources = listOf(mdSource, pdfSource, videoSource)
      )
      assertTrue(contentResult.isSuccess)
      val parts = contentResult.getOrThrow().parts
      val textParts = parts.filterIsInstance<TextPart>()
      val inlineParts = parts.filterIsInstance<InlineDataPart>()

      assertTrue(textParts.any { it.text.contains("Use Cloudflare Workers + Termux CLI.") })
      assertTrue(textParts.any { it.text == "Assemble solution" })
      assertEquals(2, inlineParts.size)
      assertEquals("application/pdf", inlineParts[0].mimeType)
      assertEquals("video/mp4", inlineParts[1].mimeType)

      val oversized = pdfSource.copy(sizeBytes = MultimodalSourceLoader.MAX_TOTAL_INLINE_BYTES + 1024L)
      val rejectResult = loader.buildMultimodalContent("Check size", listOf(oversized))
      assertTrue(rejectResult.isFailure)
    } finally {
      mdFile.delete()
      pdfFile.delete()
      mp4File.delete()
    }
  }

  @Test
  fun `web context crawler crawls domain pages saves markdown and device bitmap png`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val ss = java.net.ServerSocket(0, 10, java.net.InetAddress.getByName("127.0.0.1"))
    val port = ss.localPort
    val serverThread = Thread {
      runCatching {
        ss.soTimeout = 5000
        while (!ss.isClosed) {
          val sock = runCatching { ss.accept() }.getOrNull() ?: break
          sock.use {
            val reader = it.getInputStream().bufferedReader()
            val reqLine = reader.readLine().orEmpty()
            while (true) {
              val h = reader.readLine() ?: break
              if (h.isEmpty()) break
            }
            val path = reqLine.split(" ").getOrNull(1).orEmpty()
            val html = when {
              path.startsWith("/docs") -> "<html><head><title>Docs Page</title></head><body><h1>API Docs</h1><p>Terraform and Worker guide.</p></body></html>"
              else -> "<html><head><title>Root Portal</title><meta name=\"description\" content=\"AntiGravity Portal\"></head><body><h1>Welcome</h1><a href=\"/docs\">Docs</a><p>Main page body</p></body></html>"
            }
            val bytes = html.toByteArray(Charsets.UTF_8)
            val resp = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
            it.getOutputStream().apply {
              write(resp.toByteArray(Charsets.UTF_8))
              write(bytes)
              flush()
            }
          }
        }
      }
    }.apply { start() }

    try {
      val crawler = WebContextCrawler(context)
      assertEquals("https://google.com", crawler.normalizeUrlOrDomain("google.com"))

      val res = crawler.crawlAndCapture(
        rawUrlOrDomain = "http://127.0.0.1:$port",
        mode = CrawlMode.DOMAIN_CRAWL,
        profile = DeviceViewportProfile.PIXEL_9_PRO
      )
      assertTrue(res.isSuccess)
      val out = res.getOrThrow()
      assertEquals(2, out.pageCount)
      assertTrue(out.markdownContent.contains("Root Portal"))
      assertTrue(out.markdownContent.contains("API Docs"))

      val bmp = Bitmap.createBitmap(120, 240, Bitmap.Config.ARGB_8888)
      val pngUri = crawler.saveDeviceScreenshotBitmap(bmp, "http://127.0.0.1:$port", DeviceViewportProfile.IPHONE_16_PRO)
      bmp.recycle()

      val loader = MultimodalSourceLoader(context)
      val mdSrc = loader.inspectUri(out.markdownUri)!!
      val pngSrc = loader.inspectUri(pngUri)!!
      assertEquals(SourceAttachmentKind.MARKDOWN, mdSrc.kind)
      assertEquals(SourceAttachmentKind.IMAGE, pngSrc.kind)
    } finally {
      runCatching { ss.close() }
      runCatching { serverThread.join(1000) }
    }
  }

  @Test
  fun `cloud bridge client commits terraform dispatches plan rerun and triggers email worker`() = runTest {
    val ss = java.net.ServerSocket(0, 10, java.net.InetAddress.getByName("127.0.0.1"))
    val port = ss.localPort
    val recordedRequests = java.util.Collections.synchronizedList(mutableListOf<String>())
    val serverThread = Thread {
      runCatching {
        ss.soTimeout = 5000
        while (!ss.isClosed) {
          val sock = runCatching { ss.accept() }.getOrNull() ?: break
          sock.use {
            val reader = it.getInputStream().bufferedReader()
            val reqLine = reader.readLine().orEmpty()
            recordedRequests.add(reqLine)
            var contentLen = 0
            while (true) {
              val h = reader.readLine() ?: break
              if (h.isEmpty()) break
              if (h.startsWith("Content-Length:", ignoreCase = true)) {
                contentLen = h.substringAfter(":").trim().toIntOrNull() ?: 0
              }
            }
            if (contentLen > 0) {
              val buf = CharArray(contentLen)
              reader.read(buf, 0, contentLen)
            }
            val (code, body) = when {
              reqLine.contains("/contents/") && reqLine.startsWith("GET") -> 200 to """{"sha":"oldsha123"}"""
              reqLine.contains("/contents/") && reqLine.startsWith("PUT") -> 200 to """{"commit":{"sha":"newsha999"}}"""
              reqLine.contains("/dispatches") -> 204 to ""
              reqLine.contains("/rerun-failed-jobs") -> 201 to ""
              reqLine.contains("/v1/workflow-error") -> 200 to """{"ok":true,"rerunStatus":"RERUN_TRIGGERED"}"""
              else -> 200 to "{}"
            }
            val bytes = body.toByteArray(Charsets.UTF_8)
            val statusText = when (code) {
              204 -> "No Content"
              201 -> "Created"
              else -> "OK"
            }
            val resp = "HTTP/1.1 $code $statusText\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
            it.getOutputStream().apply {
              write(resp.toByteArray(Charsets.UTF_8))
              write(bytes)
              flush()
            }
          }
        }
      }
    }.apply { start() }

    try {
      val bridge = CloudBridgeClient(githubApiBaseUrl = "http://127.0.0.1:$port")

      val pushRes = bridge.commitFileToGitHub(
        pat = "pat_123",
        ownerRepo = "user/repo",
        filePath = "infra/main.tf",
        content = "terraform {}",
        commitMessage = "Add tf"
      )
      assertTrue(pushRes.success)
      assertTrue(pushRes.message.contains("newsha9"))

      val tfRes = bridge.dispatchTerraformWorkflow(
        pat = "pat_123",
        ownerRepo = "user/repo",
        action = TerraformAction.PLAN,
        targetEnv = "dev"
      )
      assertTrue(tfRes.success)

      val rerunRes = bridge.rerunFailedGitHubWorkflow("pat_123", "user/repo", 98765L)
      assertTrue(rerunRes.success)

      val emailRes = bridge.triggerEmailSelfHealWorker(
        baseUrl = "http://127.0.0.1:$port",
        bearerToken = "tok",
        firebaseIdToken = null,
        recipientEmail = "17crown2223@gmail.com",
        ownerRepo = "user/repo",
        workflowFile = "antigravity-terraform.yml",
        runId = 98765L,
        errorLog = "403 API disabled",
        suggestedPatch = "enable service",
        retryCount = 0
      )
      assertTrue(emailRes.success)
      assertTrue(recordedRequests.any { it.contains("/v1/workflow-error") })
      assertTrue(StackTemplates.all.any { it.category == "TERRAFORM" })
      assertTrue(StackTemplates.all.any { it.category == "SELF_HEAL" })
    } finally {
      runCatching { ss.close() }
      runCatching { serverThread.join(1000) }
    }
  }

  @Test
  fun `cloud bridge client executes whitelisted termux preset via POST run`() = runTest {
    val ss = java.net.ServerSocket(0, 10, java.net.InetAddress.getByName("127.0.0.1"))
    val port = ss.localPort
    var capturedBody = ""
    var capturedAuth = ""
    val serverThread = Thread {
      runCatching {
        ss.soTimeout = 5000
        ss.accept().use { sock ->
          val reader = sock.getInputStream().bufferedReader()
          val reqLine = reader.readLine().orEmpty()
          var contentLen = 0
          while (true) {
            val h = reader.readLine() ?: break
            if (h.isEmpty()) break
            if (h.startsWith("Authorization:", ignoreCase = true)) {
              capturedAuth = h.substringAfter(":").trim()
            }
            if (h.startsWith("Content-Length:", ignoreCase = true)) {
              contentLen = h.substringAfter(":").trim().toIntOrNull() ?: 0
            }
          }
          if (contentLen > 0) {
            val buf = CharArray(contentLen)
            reader.read(buf, 0, contentLen)
            capturedBody = String(buf)
          }
          val body = if (reqLine.startsWith("POST /run")) {
            """{"ok":true,"preset":"tf_plan","exitCode":0,"stdout":"No changes. Infrastructure is up-to-date.","stderr":"","durationMs":412}"""
          } else {
            """{"error":"not found"}"""
          }
          val bytes = body.toByteArray(Charsets.UTF_8)
          val resp = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
          sock.getOutputStream().apply {
            write(resp.toByteArray(Charsets.UTF_8))
            write(bytes)
            flush()
          }
        }
      }
    }.apply { start() }

    try {
      val bridge = CloudBridgeClient()
      val res = bridge.executeTermuxSafeCommand(
        agentUrl = "http://127.0.0.1:$port",
        bearerToken = "local_secret_123",
        preset = TermuxPresetCommand.TF_PLAN,
        workdir = "~/OrchestraAntiGravity"
      )
      assertTrue(res.success)
      assertEquals(0, res.exitCode)
      assertEquals("tf_plan", res.presetId)
      assertTrue(res.stdout.contains("Infrastructure is up-to-date"))
      assertEquals("Bearer local_secret_123", capturedAuth)
      val parsedReq = org.json.JSONObject(capturedBody)
      assertEquals("tf_plan", parsedReq.getString("preset"))
      assertEquals("~/OrchestraAntiGravity", parsedReq.getString("workdir"))

      val termuxTpl = StackTemplates.all.first { it.id == "termux_antigravity_cli" }.content
      assertTrue(termuxTpl.contains("ALLOWED_PRESETS"))
      assertTrue(termuxTpl.contains("antigravity heal"))
      assertTrue(termuxTpl.contains("pull-task)"))
    } finally {
      runCatching { ss.close() }
      runCatching { serverThread.join(1000) }
    }
  }

  @Test
  fun `cloud bridge client triggers unified orchestration and validates terraform email router templates`() = runTest {
    val ss = java.net.ServerSocket(0, 10, java.net.InetAddress.getByName("127.0.0.1"))
    val port = ss.localPort
    var capturedPostBody = ""
    var capturedFirebaseHdr = ""
    val serverThread = Thread {
      runCatching {
        ss.soTimeout = 5000
        repeat(2) {
          ss.accept().use { sock ->
            val reader = sock.getInputStream().bufferedReader()
            val reqLine = reader.readLine().orEmpty()
            var contentLen = 0
            while (true) {
              val h = reader.readLine() ?: break
              if (h.isEmpty()) break
              if (h.startsWith("X-Firebase-Token:", ignoreCase = true)) {
                capturedFirebaseHdr = h.substringAfter(":").trim()
              }
              if (h.startsWith("Content-Length:", ignoreCase = true)) {
                contentLen = h.substringAfter(":").trim().toIntOrNull() ?: 0
              }
            }
            if (contentLen > 0) {
              val buf = CharArray(contentLen)
              reader.read(buf, 0, contentLen)
              capturedPostBody = String(buf)
            }
            val respBody = when {
              reqLine.startsWith("POST /v1/orchestrate") ->
                """{"ok":true,"taskType":"tf_plan","detail":"OK_DISPATCHED_antigravity-terraform.yml"}"""
              reqLine.startsWith("GET /v1/termux/next-task") ->
                """{"ok":true,"hasTask":true,"task":{"preset":"git_pull","workdir":"~/OrchestraAntiGravity"}}"""
              else -> """{"error":"unexpected"}"""
            }
            val bytes = respBody.toByteArray(Charsets.UTF_8)
            val hdr = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
            sock.getOutputStream().apply {
              write(hdr.toByteArray(Charsets.UTF_8))
              write(bytes)
              flush()
            }
          }
        }
      }
    }.apply { start() }

    try {
      val bridge = CloudBridgeClient()
      val orchRes = bridge.triggerOrchestrationTask(
        baseUrl = "http://127.0.0.1:$port",
        bearerToken = "gw_token_abc",
        firebaseIdToken = "fb_jwt_xyz",
        routeTarget = EmailRouteTarget.TERRAFORM,
        taskType = OrchestrationTaskType.TF_PLAN,
        ownerRepo = "17crown1901mituru/OrchestraAntiGravity",
        promptOrInput = "plan prod",
        targetEnv = "prod"
      )
      assertTrue(orchRes.success)
      assertEquals(200, orchRes.statusCode)
      assertEquals("tf@gc2223.com", orchRes.routeAddress)
      assertTrue(orchRes.detail.contains("OK_DISPATCHED_antigravity-terraform.yml"))
      assertEquals("fb_jwt_xyz", capturedFirebaseHdr)

      val reqJson = org.json.JSONObject(capturedPostBody)
      assertEquals("tf_plan", reqJson.getString("taskType"))
      assertEquals("tf@gc2223.com", reqJson.getString("routeAddress"))
      assertEquals("prod", reqJson.getJSONObject("payload").getString("targetEnv"))
      assertTrue(reqJson.getString("traceId").isNotBlank())
      assertTrue(orchRes.traceId.isNotBlank())

      val queueRes = bridge.fetchPendingTermuxQueueTask(
        baseUrl = "http://127.0.0.1:$port",
        bearerToken = "gw_token_abc",
        firebaseIdToken = "fb_jwt_xyz"
      )
      assertTrue(queueRes.success)
      assertTrue(queueRes.message.contains("git_pull"))

      val tfContent = StackTemplates.all.first { it.id == "terraform_llm_infra" }.content
      assertTrue(tfContent.contains("cloudflare_email_routing_rule"))
      assertTrue(tfContent.contains("cloudflare_workers_kv_namespace"))
      assertTrue(tfContent.contains("google_pubsub_topic"))

      val workerContent = StackTemplates.all.first { it.id == "cf_email_autofix_worker" }.content
      assertTrue(workerContent.contains("localPart === \"tf\""))
      assertTrue(workerContent.contains("localPart === \"crawl\""))
      assertTrue(workerContent.contains("async scheduled(event, env, ctx)"))
      assertTrue(workerContent.contains("APPROVE APPLY"))
      assertTrue(workerContent.contains("LEASED"))
      assertTrue(workerContent.contains("dlq:"))
      assertTrue(workerContent.contains("SAFE_MODE"))
      assertTrue(workerContent.contains("In-Reply-To"))
      assertTrue(workerContent.contains("loadCiKnowledge"))
      assertTrue(workerContent.contains("github-authentication-token-expiration"))

      val termuxContent = StackTemplates.all.first { it.id == "termux_antigravity_cli" }.content
      assertTrue(termuxContent.contains("X-Device-Battery"))

      assertTrue(StackTemplates.all.any { it.id == "github_actions_orchestrator" })
    } finally {
      runCatching { ss.close() }
      runCatching { serverThread.join(1000) }
    }
  }

  @Test
  fun `orchestration hardening client fetches state approvals audit and toggles safe mode`() = runTest {
    val ss = java.net.ServerSocket(0, 10, java.net.InetAddress.getByName("127.0.0.1"))
    val port = ss.localPort
    val recordedBodies = java.util.Collections.synchronizedList(mutableListOf<String>())
    val serverThread = Thread {
      runCatching {
        ss.soTimeout = 5000
        repeat(5) {
          val sock = runCatching { ss.accept() }.getOrNull() ?: return@repeat
          sock.use { s ->
            val reader = s.getInputStream().bufferedReader()
            val reqLine = reader.readLine().orEmpty()
            var contentLen = 0
            while (true) {
              val h = reader.readLine() ?: break
              if (h.isEmpty()) break
              if (h.startsWith("Content-Length:", ignoreCase = true)) {
                contentLen = h.substringAfter(":").trim().toIntOrNull() ?: 0
              }
            }
            if (contentLen > 0) {
              val buf = CharArray(contentLen)
              reader.read(buf, 0, contentLen)
              recordedBodies.add(String(buf))
            }
            val respBody = when {
              reqLine.startsWith("GET /v1/orchestrate/state") ->
                """{"ok":true,"safeMode":false,"safeReason":"","aiHour":4,"ghHour":2,"dlqCount":1,"approvalsCount":1,"heartbeat":{"battery":82,"charging":true,"network":"wifi","seenAt":"2026-11-20T06:00:00Z"},"cron":{"at":"2026-11-20T06:00:00Z","conclusion":"success","patExpiry":""}}"""
              reqLine.startsWith("GET /v1/approvals") ->
                """{"ok":true,"approvals":[{"approvalId":"appr7788","action":"tf_apply","repo":"user/repo","targetEnv":"prod","summary":"Terraform apply (prod)","traceId":"trc123456","expiresAtMs":1900000000000}]}"""
              reqLine.startsWith("GET /v1/audit") ->
                """{"ok":true,"logs":[{"traceId":"trc123456","source":"email:tf@","taskType":"tf","ok":true,"summary":"Plan dispatched","timestamp":"2026-11-20T05:59:00Z"}]}"""
              reqLine.startsWith("POST /v1/approvals/decide") ->
                """{"ok":true,"approvalId":"appr7788","approved":true,"detail":"OK_DISPATCHED_antigravity-terraform.yml","traceId":"trc123456"}"""
              reqLine.startsWith("POST /v1/circuit-breaker") ->
                """{"ok":true,"safeMode":true,"traceId":"trc999"}"""
              else -> """{"error":"not found"}"""
            }
            val bytes = respBody.toByteArray(Charsets.UTF_8)
            val hdr = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
            s.getOutputStream().apply {
              write(hdr.toByteArray(Charsets.UTF_8))
              write(bytes)
              flush()
            }
          }
        }
      }
    }.apply { start() }

    try {
      val hardeningClient = OrchestrationHardeningClient()
      val snapshot = hardeningClient.fetchHardeningSnapshot(
        baseUrl = "http://127.0.0.1:$port",
        bearerToken = "gw_tok",
        firebaseIdToken = "fb_tok"
      )
      assertFalse(snapshot.safeMode)
      assertEquals(4, snapshot.aiHourCount)
      assertEquals(2, snapshot.ghHourCount)
      assertEquals(1, snapshot.dlqCount)
      assertEquals(82, snapshot.termuxBatteryPct)
      assertEquals(true, snapshot.termuxCharging)
      assertEquals("wifi", snapshot.termuxNetwork)
      assertEquals("success", snapshot.cronLastConclusion)
      assertEquals(1, snapshot.approvals.size)
      assertEquals("appr7788", snapshot.approvals.first().approvalId)
      assertEquals(1, snapshot.recentAuditLogs.size)
      assertEquals("trc123456", snapshot.recentAuditLogs.first().traceId)

      val decideRes = hardeningClient.decideApproval(
        baseUrl = "http://127.0.0.1:$port",
        bearerToken = "gw_tok",
        firebaseIdToken = "fb_tok",
        approvalId = "appr7788",
        approve = true
      )
      assertTrue(decideRes.success)
      assertTrue(decideRes.message.contains("OK_DISPATCHED_antigravity-terraform.yml"))
      assertTrue(recordedBodies.any { it.contains("\"approvalId\":\"appr7788\"") && it.contains("\"approve\":true") })

      val cbRes = hardeningClient.toggleCircuitBreakerSafeMode(
        baseUrl = "http://127.0.0.1:$port",
        bearerToken = "gw_tok",
        firebaseIdToken = "fb_tok",
        enableSafeMode = true,
        reason = "Test trigger"
      )
      assertTrue(cbRes.success)
      assertTrue(recordedBodies.any { it.contains("\"safeMode\":true") })
    } finally {
      runCatching { ss.close() }
      runCatching { serverThread.join(1000) }
    }
  }
}
