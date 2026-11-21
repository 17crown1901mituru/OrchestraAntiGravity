package com.example

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.service.quicksettings.Tile
import androidx.test.core.app.ApplicationProvider
import com.example.data.OrchestrationExtendedClient
import com.example.data.OrchestrationHardeningClient
import com.example.data.StackTemplates
import com.example.model.OrchestrationHardeningState
import com.example.security.BiometricHardwareSigner
import com.example.sync.KillSwitchTileService
import com.example.widget.StatusGlanceWidgetReceiver
import com.example.widget.WidgetStateStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.CopyOnWriteArrayList

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExtendedOrchestrationTest {

  @Test
  fun `derToP1363 normalizes ASN1 DER signatures with leading zero and short ints to 64 bytes`() {
    // r = 32 bytes starting with 0x80 (DER encodes as 33 bytes with 0x00 prefix)
    val rRaw = ByteArray(32) { idx -> if (idx == 0) 0x80.toByte() else (idx + 1).toByte() }
    // s = 31 bytes (short integer -> left-padded with 0x00 in 32-byte field)
    val sRaw = ByteArray(31) { idx -> (idx + 0x10).toByte() }

    val der = ByteArray(2 + (2 + 33) + (2 + 31))
    var pos = 0
    der[pos++] = 0x30
    der[pos++] = (der.size - 2).toByte()
    der[pos++] = 0x02
    der[pos++] = 33.toByte()
    der[pos++] = 0x00
    System.arraycopy(rRaw, 0, der, pos, 32)
    pos += 32
    der[pos++] = 0x02
    der[pos++] = 31.toByte()
    System.arraycopy(sRaw, 0, der, pos, 31)

    val p1363 = BiometricHardwareSigner.derToP1363(der, componentLen = 32)
    assertEquals(64, p1363.size)
    assertArrayEquals(rRaw, p1363.copyOfRange(0, 32))
    assertEquals(0.toByte(), p1363[32])
    assertArrayEquals(sRaw, p1363.copyOfRange(33, 64))
  }

  @Test
  fun `computeKeyId produces stable 12-char hex fingerprint`() {
    val sampleSpki = "sample-spki-public-key-bytes".toByteArray()
    val id1 = BiometricHardwareSigner.computeKeyId(sampleSpki)
    val id2 = BiometricHardwareSigner.computeKeyId(sampleSpki)
    assertEquals(12, id1.length)
    assertEquals(id1, id2)
  }

  @Test
  fun `widget state store saves hardening summary and toggles safeMode cleanly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val state = OrchestrationHardeningState(
      safeMode = false,
      operatingMode = "GREEN_DEFER",
      frugalFallbackActive = true,
      approvalsCount = 3,
      kvWritesToday = 420,
      kvDailyLimit = 1000,
      aiNeuronsToday = 3500,
      aiNeuronDailyLimit = 10000,
      ciMinutesUsedMonth = 980,
      ciMinutesLimit = 2000,
      hardwareKeyRegistered = true
    )

    WidgetStateStore.saveFromHardeningState(context, state)
    val loaded = WidgetStateStore.load(context)
    assertEquals("GREEN_DEFER", loaded.operatingMode)
    assertFalse(loaded.safeMode)
    assertTrue(loaded.frugalFallbackActive)
    assertEquals(3, loaded.approvalsCount)
    assertEquals(420, loaded.kvWritesToday)
    assertEquals(3500, loaded.aiNeuronsToday)
    assertEquals(980, loaded.ciMinutesUsedMonth)
    assertTrue(loaded.hardwareKeyRegistered)

    WidgetStateStore.updateSafeModeOnly(context, safeMode = true)
    val toggled = WidgetStateStore.load(context)
    assertTrue(toggled.safeMode)
    assertEquals("SAFE_MODE", toggled.operatingMode)
  }

  @Test
  fun `hardening and extended clients exchange zero-trust hardware auth, telemetry, and RAG citations`() = runTest {
    val server = ServerSocket(0, 10, InetAddress.getByName("127.0.0.1"))
    val recordedBodies = CopyOnWriteArrayList<Pair<String, String>>()

    val workerThread = Thread {
      repeat(7) {
        runCatching {
          server.soTimeout = 5000
          server.accept().use { sock ->
            val reader = sock.getInputStream().bufferedReader()
            val reqLine = reader.readLine().orEmpty()
            val path = reqLine.split(" ").getOrNull(1).orEmpty()
            var contentLength = 0
            while (true) {
              val line = reader.readLine() ?: break
              if (line.isEmpty()) break
              if (line.lowercase().startsWith("content-length:")) {
                contentLength = line.substringAfter(":").trim().toIntOrNull() ?: 0
              }
            }
            val bodyChars = if (contentLength > 0) {
              CharArray(contentLength).also { buf ->
                var read = 0
                while (read < contentLength) {
                  val r = reader.read(buf, read, contentLength - read)
                  if (r < 0) break
                  read += r
                }
              }.concatToString()
            } else ""
            recordedBodies.add(path to bodyChars)

            val responseJson = when {
              path.startsWith("/v1/orchestrate/state") -> """
                {
                  "safeMode": false,
                  "approvalsCount": 1,
                  "hardwareAuth": { "registeredKeyId": "hwkey123abc", "required": true },
                  "ciQuota": { "minutesUsedMonth": 1540, "minutesLimit": 2000, "offloadActive": true },
                  "platformTelemetry": { "actualKvWrites": 310, "actualR2Ops": 48, "actualCpuMs": 125, "deltaKvWrites": 12, "syncedAt": "2026-11-20T12:00:00Z" },
                  "finopsBreakdown": { "ci-heal": 4, "tf": 3, "crawl": 2, "dr": 1, "control": 5 }
                }
              """.trimIndent()
              path.startsWith("/v1/approvals/register-key") -> """{"ok":true,"keyId":"hwkey123abc"}"""
              path.startsWith("/v1/approvals/challenge") -> """{"ok":true,"approvalId":"apr-99","actionHash":"hash-abc","challenge":"nonce-xyz","keyId":"hwkey123abc","requiresHardwareSignature":true}"""
              path.startsWith("/v1/approvals/decide") -> """{"ok":true,"detail":"Terraform apply dispatched with ECDSA verification","traceId":"tr_009"}"""
              path.startsWith("/v1/approvals") -> """
                {
                  "approvals": [
                    {
                      "approvalId": "apr-99",
                      "action": "tf_apply",
                      "repo": "17crown1901mituru/OrchestraAntiGravity",
                      "targetEnv": "prod",
                      "summary": "Apply production KV update",
                      "traceId": "tr_008",
                      "confidence": 0.91,
                      "reasoning": "Low blast radius",
                      "actionHash": "hash-abc",
                      "criticRiskLevel": "HIGH",
                      "criticWarnings": ["本番環境(prod)に対する Terraform apply 操作が含まれています"]
                    }
                  ]
                }
              """.trimIndent()
              path.startsWith("/v1/audit") -> """{"logs":[]}"""
              path.startsWith("/v1/ask") -> """
                {
                  "ok": true,
                  "answer": "Terraform apply failed due to missing state lock [tid:tr_008].",
                  "citations": [
                    {"traceId": "tr_008", "source": "audit/2026-11-20/tr_008.json", "summary": "tf_apply prod lock error", "score": 0.94}
                  ],
                  "usedEmbeddings": true,
                  "traceId": "tr_ask1"
                }
              """.trimIndent()
              else -> """{"ok":true}"""
            }

            val bytes = responseJson.toByteArray(Charsets.UTF_8)
            val headers = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
            sock.getOutputStream().use { out ->
              out.write(headers.toByteArray(Charsets.UTF_8))
              out.write(bytes)
              out.flush()
            }
          }
        }
      }
    }.apply { start() }

    val baseUrl = "http://127.0.0.1:${server.localPort}"
    val hardeningClient = OrchestrationHardeningClient()
    val extendedClient = OrchestrationExtendedClient()

    val snap = hardeningClient.fetchHardeningSnapshot(baseUrl, "tok", null)
    assertTrue(snap.hardwareKeyRegistered)
    assertEquals("hwkey123abc", snap.hardwareKeyId)
    assertEquals(1540, snap.ciMinutesUsedMonth)
    assertTrue(snap.ciOffloadActive)
    assertEquals(310, snap.actualKvWrites)
    assertEquals(48, snap.actualR2Ops)
    assertEquals(125, snap.actualWorkerCpuMs)
    assertEquals(12, snap.deltaKvWrites)
    assertEquals(4, snap.finopsBreakdown["ci-heal"])
    assertEquals(1, snap.approvals.size)
    assertEquals("HIGH", snap.approvals.first().criticRiskLevel)
    assertEquals(1, snap.approvals.first().criticWarnings.size)

    val regRes = extendedClient.registerHardwarePublicKey(baseUrl, "tok", null, "hwkey123abc", "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEtest")
    assertTrue(regRes.success)

    val chalRes = extendedClient.requestApprovalChallenge(baseUrl, "tok", null, "apr-99")
    assertTrue(chalRes.success)
    assertTrue(chalRes.requiresHardwareSignature)
    assertEquals("nonce-xyz", chalRes.challenge)
    assertEquals("hash-abc", chalRes.actionHash)

    val decideRes = hardeningClient.decideApproval(
      baseUrl = baseUrl,
      bearerToken = "tok",
      firebaseIdToken = null,
      approvalId = "apr-99",
      approve = true,
      challenge = "nonce-xyz",
      actionHash = "hash-abc",
      signatureB64 = "c2lnbmF0dXJl",
      keyId = "hwkey123abc"
    )
    assertTrue(decideRes.success)
    assertTrue(recordedBodies.any { it.first == "/v1/approvals/decide" && it.second.contains("\"signatureB64\":\"c2lnbmF0dXJl\"") })

    val ragRes = extendedClient.askAuditKnowledge(baseUrl, "tok", null, "Why did terraform fail?")
    assertTrue(ragRes.ok)
    assertTrue(ragRes.usedEmbeddings)
    assertEquals(1, ragRes.citations.size)
    assertEquals("tr_008", ragRes.citations.first().traceId)

    workerThread.join(4000)
    server.close()
  }

  @Test
  fun `orchestration templates include all 5 enterprise pillar endpoints and parallel DAG`() {
    val worker = StackTemplates.all.first { it.id == "cf_email_autofix_worker" }.content
    assertTrue(worker.contains("/v1/approvals/register-key"))
    assertTrue(worker.contains("/v1/approvals/challenge"))
    assertTrue(worker.contains("/v1/dag/checkpoint"))
    assertTrue(worker.contains("/v1/ask"))
    assertTrue(worker.contains("runAdversarialCritic"))
    assertTrue(worker.contains("syncCloudflareGraphqlTelemetry"))
    assertTrue(worker.contains("checkGithubActionsQuota"))
    assertTrue(worker.contains("json_schema"))

    val dagYaml = StackTemplates.all.first { it.id == "github_actions_orchestrator" }.content
    assertTrue(dagYaml.contains("dag-init"))
    assertTrue(dagYaml.contains("deploy-gateway-worker"))
    assertTrue(dagYaml.contains("terraform-verify"))
    assertTrue(dagYaml.contains("health-audit"))
    assertTrue(dagYaml.contains("/v1/dag/status/"))
  }

  @Test
  fun `kill switch tile service updates tile state from WidgetStateStore and manifest components exist`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    WidgetStateStore.updateSafeModeOnly(context, safeMode = true)

    val service = Robolectric.setupService(KillSwitchTileService::class.java)
    service.onStartListening()
    val tile = service.qsTile
    assertEquals(Tile.STATE_ACTIVE, tile.state)
    assertEquals(context.getString(R.string.qs_tile_killswitch_label), tile.label)

    WidgetStateStore.updateSafeModeOnly(context, safeMode = false)
    service.onStartListening()
    assertEquals(Tile.STATE_INACTIVE, tile.state)

    val pm = context.packageManager
    val serviceInfo = pm.getServiceInfo(
      ComponentName(context, KillSwitchTileService::class.java),
      PackageManager.GET_META_DATA
    )
    assertEquals("android.permission.BIND_QUICK_SETTINGS_TILE", serviceInfo.permission)
    assertTrue(serviceInfo.exported)

    val receiverInfo = pm.getReceiverInfo(
      ComponentName(context, StatusGlanceWidgetReceiver::class.java),
      PackageManager.GET_META_DATA
    )
    assertTrue(receiverInfo.exported)
    assertTrue(receiverInfo.metaData?.containsKey("android.appwidget.provider") == true)
  }
}
