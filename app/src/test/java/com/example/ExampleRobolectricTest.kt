package com.example

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.example.data.GitHubActivityService
import com.example.data.LocalSecretsStore
import com.example.data.MultimodalSourceLoader
import com.example.data.SyncSnapshotStore
import com.example.model.BackgroundSyncSettings
import com.example.model.CloudflareSyncStatus
import com.example.model.CloudflareWorkerSummary
import com.example.model.ExternalCredentials
import com.example.model.GitHubActivityEvent
import com.example.model.GitHubCommitSummary
import com.example.model.GitHubRepoSummary
import com.example.model.GitHubSyncStatus
import com.example.model.SourceAttachmentKind
import com.example.model.SyncHealthState
import com.example.model.SyncStatusSnapshot
import com.example.model.TermuxAgentSyncStatus
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
}
