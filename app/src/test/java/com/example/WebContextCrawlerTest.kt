package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.OrchestrationTemplates
import com.example.data.StackTemplates
import com.example.data.WebContextCrawler
import com.example.model.CrawlMode
import com.example.model.DeviceViewportProfile
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.net.ServerSocket
import kotlin.concurrent.thread

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WebContextCrawlerTest {

  @Test
  fun `path recursive mode prefers english falls back to native-only pages parses sitemap index and converts tables`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val server = ServerSocket(0)
    val port = server.localPort
    val acceptLanguageHeaders = mutableListOf<Pair<String, String>>()

    val t = thread(isDaemon = true) {
      while (!server.isClosed) {
        val sock = runCatching { server.accept() }.getOrNull() ?: break
        sock.use {
          val reader = it.getInputStream().bufferedReader()
          val reqLine = reader.readLine() ?: ""
          val path = reqLine.split(" ").getOrNull(1) ?: "/"
          var acceptLang = ""
          while (true) {
            val h = reader.readLine() ?: break
            if (h.isEmpty()) break
            if (h.startsWith("Accept-Language:", ignoreCase = true)) {
              acceptLang = h.substringAfter(":").trim()
            }
          }
          synchronized(acceptLanguageHeaders) {
            acceptLanguageHeaders.add(path to acceptLang)
          }

          val (statusLine, contentType, body) = when (path) {
            "/robots.txt" -> Triple(
              "200 OK",
              "text/plain; charset=utf-8",
              "User-agent: *\nSitemap: http://127.0.0.1:$port/sitemap_index.xml\n"
            )
            "/sitemap.xml" -> Triple(
              "404 Not Found",
              "text/plain; charset=utf-8",
              "Not found"
            )
            "/sitemap_index.xml" -> Triple(
              "200 OK",
              "application/xml; charset=utf-8",
              """
              <sitemapindex>
                <sitemap><loc>http://127.0.0.1:$port/sitemap_docs.xml</loc></sitemap>
              </sitemapindex>
              """.trimIndent()
            )
            "/sitemap_docs.xml" -> Triple(
              "200 OK",
              "application/xml; charset=utf-8",
              """
              <urlset>
                <url><loc>http://127.0.0.1:$port/docs/from-sitemap</loc></url>
                <url><loc>http://127.0.0.1:$port/outside-sitemap</loc></url>
              </urlset>
              """.trimIndent()
            )
            "/docs" -> Triple(
              "200 OK",
              "text/html; charset=utf-8",
              """
              <html lang="en"><head><title>Cloud Docs Root</title></head><body>
                <nav><a href="/pricing">Pricing Outside</a><a href="/blog">Blog Outside</a></nav>
                <main>
                  <h1>Documentation Portal</h1>
                  <p>Start reading <a href="/docs/security">Security</a> and <a href="/docs/security?hl=es#top">Security ES</a>.</p>
                  <p>Check <a href="/docs/quotas">Quotas Guide</a> or <a href="/docs/ja-only?hl=ja">Japan Local Guide</a>.</p>
                  <p>Also <a href="/docs/localized-hub">Localized Hub</a> and ignore <a href="/docs/diagram.png">Diagram</a>.</p>
                </main>
                <footer>Copyright Footer Noise</footer>
              </body></html>
              """.trimIndent()
            )
            "/docs/security" -> Triple(
              "200 OK",
              "text/html; charset=utf-8",
              """
              <html lang="en"><head><title>Security Best Practices</title></head><body>
                <devsite-book-nav>Sidebar Navigation Noise</devsite-book-nav>
                <devsite-toc>TOC Noise</devsite-toc>
                <article>
                  <h2>TLS Hardening</h2>
                  <ul><li>Enforce <strong>TLS 1.2</strong> or higher</li><li>Disable service account keys</li></ul>
                  <table>
                    <tr><th>Policy</th><th>Value</th></tr>
                    <tr><td>gcp.restrictTLSVersion</td><td>MODERN_TLS</td></tr>
                  </table>
                  <pre><code class="language-bash">gcloud beta terraform vet tfplan.json --policy-library=./policy-library</code></pre>
                  <a href="/docs/quotas#alerts">Quotas Fragment</a>
                  <a href="/outside/page">Outside Link</a>
                </article>
              </body></html>
              """.trimIndent()
            )
            "/docs/quotas" -> Triple(
              "200 OK",
              "text/html; charset=utf-8",
              """
              <html lang="en"><head><title>Cloud Quotas</title></head><body>
                <main>
                  <h1>Quota Monitoring</h1>
                  <p>Configure alert threshold at 80 percent allocation.</p>
                </main>
              </body></html>
              """.trimIndent()
            )
            "/docs/ja-only" -> Triple(
              "404 Not Found",
              "text/html; charset=utf-8",
              "<html><body>English version does not exist</body></html>"
            )
            "/docs/ja-only?hl=ja" -> Triple(
              "200 OK",
              "text/html; charset=utf-8",
              """
              <html lang="ja"><head><title>日本独自ガイド</title></head><body>
                <main>
                  <h1>日本国内向け固有要件</h1>
                  <p>このページは日本語のみ存在します。</p>
                </main>
              </body></html>
              """.trimIndent()
            )
            "/docs/localized-hub" -> Triple(
              "200 OK",
              "text/html; charset=utf-8",
              """
              <html lang="es">
                <head>
                  <title>Hub en Español</title>
                  <link rel="alternate" hreflang="en" href="http://127.0.0.1:$port/docs/localized-hub?hl=en" />
                </head>
                <body><main><h1>Contenido en Español</h1></main></body>
              </html>
              """.trimIndent()
            )
            "/docs/localized-hub?hl=en" -> Triple(
              "200 OK",
              "text/html; charset=utf-8",
              """
              <html lang="en"><head><title>English Localized Hub</title></head><body>
                <main><h1>Canonical English Hub</h1></main>
              </body></html>
              """.trimIndent()
            )
            "/docs/from-sitemap" -> Triple(
              "200 OK",
              "text/html; charset=utf-8",
              """
              <html lang="en"><head><title>Sitemap Index Target</title></head><body>
                <main><h1>Discovered via Child Sitemap</h1></main>
              </body></html>
              """.trimIndent()
            )
            else -> Triple("404 Not Found", "text/html; charset=utf-8", "<html><body>Not found: $path</body></html>")
          }

          val bytes = body.toByteArray(Charsets.UTF_8)
          val resp = "HTTP/1.1 $statusLine\r\n" +
            "Content-Type: $contentType\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
          it.getOutputStream().apply {
            write(resp.toByteArray(Charsets.UTF_8))
            write(bytes)
            flush()
          }
        }
      }
    }

    try {
      val crawler = WebContextCrawler(context, allowLoopbackForTest = true)
      val res = crawler.crawlAndCapture(
        rawUrlOrDomain = "http://127.0.0.1:$port/docs",
        mode = CrawlMode.PATH_RECURSIVE,
        maxPages = 20,
        profile = DeviceViewportProfile.PIXEL_9_PRO,
        preferEnglish = true
      )
      assertTrue(res.isSuccess)
      val out = res.getOrThrow()

      // /docs, /docs/security, /docs/quotas, /docs/ja-only?hl=ja (native fallback),
      // /docs/localized-hub?hl=en (via hreflang="en"), /docs/from-sitemap (via sitemap_index) -> 6 pages
      assertEquals(6, out.pageCount)
      assertEquals(6, out.perPageMarkdownFiles.size)
      assertTrue(out.sessionDirectoryName.startsWith("session_"))
      assertTrue(out.languageSummary.contains("en:5"))
      assertTrue(out.languageSummary.contains("ja:1"))
      assertTrue(out.languageSummary.contains("固有言語フォールバック:1"))

      // Structured Markdown headings, bold, lists, table, and fenced code language hint
      assertTrue(out.markdownContent.contains("# Documentation Portal"))
      assertTrue(out.markdownContent.contains("## TLS Hardening"))
      assertTrue(out.markdownContent.contains("- Enforce **TLS 1.2** or higher"))
      assertTrue(out.markdownContent.contains("| Policy | Value |"))
      assertTrue(out.markdownContent.contains("| gcp.restrictTLSVersion | MODERN_TLS |"))
      assertTrue(out.markdownContent.contains("```bash"))
      assertTrue(out.markdownContent.contains("gcloud beta terraform vet"))
      assertTrue(out.markdownContent.contains("Quota Monitoring"))

      // Native-only Japanese page recovered when English 404'd
      assertTrue(out.markdownContent.contains("日本国内向け固有要件"))
      assertTrue(out.markdownContent.contains("Language: ja (native-only fallback)"))

      // Non-English page transparently switched to hreflang="en" canonical English version
      assertTrue(out.markdownContent.contains("Canonical English Hub"))
      assertFalse(out.markdownContent.contains("Contenido en Español"))

      // Child sitemap URL discovered via robots.txt -> sitemap_index.xml -> sitemap_docs.xml
      assertTrue(out.markdownContent.contains("Discovered via Child Sitemap"))
      assertFalse(out.markdownContent.contains("URL: http://127.0.0.1:$port/outside-sitemap"))

      // Noise elements & out-of-scope pages must not be crawled
      assertFalse(out.markdownContent.contains("Sidebar Navigation Noise"))
      assertFalse(out.markdownContent.contains("TOC Noise"))
      assertFalse(out.markdownContent.contains("Copyright Footer Noise"))
      assertFalse(out.markdownContent.contains("/pricing"))
      assertFalse(out.markdownContent.contains("URL: http://127.0.0.1:$port/outside/page"))
      assertFalse(out.markdownContent.contains("URL: http://127.0.0.1:$port/docs/security?hl=es"))
      assertFalse(out.markdownContent.contains("Not found:"))
      assertFalse(out.perPageMarkdownFiles.any { it.contains("outside") || it.contains("pricing") })

      // Verify Accept-Language: en-US,en;q=0.9 was sent on English-first page fetches
      assertTrue(
        acceptLanguageHeaders.any { (reqPath, langHeader) ->
          reqPath == "/docs" && langHeader.contains("en-US")
        }
      )

      // Verify per-page .md files actually exist inside cacheDir/web_captures/<sessionDirectoryName>
      val sessionDir = File(File(context.cacheDir, "web_captures"), out.sessionDirectoryName)
      assertTrue(sessionDir.exists() && sessionDir.isDirectory)
      val mdFiles = sessionDir.listFiles { f -> f.extension == "md" }.orEmpty()
      assertEquals(6, mdFiles.size)
    } finally {
      server.close()
      t.interrupt()
    }
  }

  @Test
  fun `default crawler rejects loopback private network metadata and non-http schemes`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val crawler = WebContextCrawler(context)

    val blockedTargets = listOf(
      "http://127.0.0.1:8787/health",
      "http://localhost:8787/health",
      "http://169.254.169.254/computeMetadata/v1/",
      "http://metadata.google.internal/computeMetadata/v1/",
      "http://10.0.0.1/internal",
      "http://192.168.1.1/admin",
      "file:///etc/passwd"
    )

    blockedTargets.forEach { target ->
      val res = crawler.crawlAndCapture(
        rawUrlOrDomain = target,
        mode = CrawlMode.PATH_RECURSIVE,
        profile = DeviceViewportProfile.PIXEL_9_PRO
      )
      assertTrue("Expected failure for $target", res.isFailure)
    }
  }

  @Test
  fun `upgraded cloud docs templates include org policies quotas well-architected policy library and worker english crawler`() {
    val tf = OrchestrationTemplates.all.first { it.id == "terraform_llm_infra" }.content
    assertTrue(tf.contains("cloudquotas.googleapis.com"))
    assertTrue(tf.contains("orgpolicy.googleapis.com"))
    assertTrue(tf.contains("gcp.restrictTLSVersion"))
    assertTrue(tf.contains("iam.disableServiceAccountKeyCreation"))
    assertTrue(tf.contains("gcp.restrictEndpointUsage"))
    assertTrue(tf.contains("rotation_period"))
    assertTrue(tf.contains("quota_high_usage"))

    val tfCi = OrchestrationTemplates.all.first { it.id == "github_actions_terraform" }.content
    assertTrue(tfCi.contains("gcloud beta terraform vet tfplan.json"))

    val dagCi = OrchestrationTemplates.all.first { it.id == "github_actions_orchestrator" }.content
    assertTrue(dagCi.contains("gcloud beta terraform vet tfplan.json"))

    val policyLib = OrchestrationTemplates.all.first { it.id == "policy_library_constraints" }.content
    assertTrue(policyLib.contains("enforce-modern-tls-versions"))
    assertTrue(policyLib.contains("disable-service-account-key-creation"))

    val worker = OrchestrationTemplates.all.first { it.id == "cf_email_autofix_worker" }.content
    assertTrue(worker.contains("fetchEnglishFirstPage"))
    assertTrue(worker.contains("cleanDevsiteHtmlToText"))
    assertTrue(worker.contains("devsite-book-nav"))
    assertTrue(worker.contains("Accept-Language"))

    val zeroCost = StackTemplates.all.first { it.id == "gcp_zero_cost_guard" }.content
    assertTrue(zeroCost.contains("cloudquotas.googleapis.com"))
    assertTrue(zeroCost.contains("x-goog-api-key"))
    assertTrue(zeroCost.contains("x-goog-user-project"))

    val wellArch = StackTemplates.all.first { it.id == "gcp_docs_well_architected" }.content
    assertTrue(wellArch.contains("Well-Architected"))
    assertTrue(wellArch.contains("OWASP Top 10:2025"))
    assertTrue(wellArch.contains("gcloud beta terraform vet"))
    assertTrue(wellArch.contains("Cloudflare / Firebase / GitHub / Terraform"))
  }

  @Test
  fun `multi-platform official doc compliance across cloudflare firebase github and terraform`() {
    // 1. Cloudflare Workers & Wrangler official docs
    val stackWorker = StackTemplates.all.first { it.id == "cf_worker_gc2223" }.content
    assertTrue(stackWorker.contains("crypto.subtle.timingSafeEqual"))
    assertTrue(stackWorker.contains("timingSafeEqualStrings"))
    assertTrue(stackWorker.contains("securetoken@system.gserviceaccount.com"))
    assertTrue(stackWorker.contains("RSASSA-PKCS1-v1_5"))
    assertTrue(stackWorker.contains("X-GitHub-Api-Version"))

    val orchWorker = OrchestrationTemplates.all.first { it.id == "cf_email_autofix_worker" }.content
    assertTrue(orchWorker.contains("crypto.subtle.timingSafeEqual"))
    assertTrue(orchWorker.contains("verifyFirebaseToken"))
    assertTrue(orchWorker.contains("securetoken@system.gserviceaccount.com"))
    assertTrue(orchWorker.contains("RSASSA-PKCS1-v1_5"))
    assertTrue(orchWorker.contains("email_verified"))
    assertTrue(orchWorker.contains("2022-11-28"))

    val stackWrangler = StackTemplates.all.first { it.id == "wrangler_toml_gc2223" }.content
    assertTrue(stackWrangler.contains("workers_dev = false"))
    assertTrue(stackWrangler.contains("nodejs_compat"))

    val orchWrangler = OrchestrationTemplates.all.first { it.id == "wrangler_email_selfheal" }.content
    assertTrue(orchWrangler.contains("workers_dev = false"))

    // 2. GitHub Actions official hardening & concurrency docs
    val ghDeploy = StackTemplates.all.first { it.id == "github_actions_deploy" }.content
    assertTrue(ghDeploy.contains("permissions: {}"))
    assertTrue(ghDeploy.contains("concurrency:"))
    assertTrue(ghDeploy.contains("timeout-minutes:"))

    val ghTf = OrchestrationTemplates.all.first { it.id == "github_actions_terraform" }.content
    assertTrue(ghTf.contains("permissions: {}"))
    assertTrue(ghTf.contains("concurrency:"))
    assertTrue(ghTf.contains("timeout-minutes:"))

    val ghDag = OrchestrationTemplates.all.first { it.id == "github_actions_orchestrator" }.content
    assertTrue(ghDag.contains("permissions: {}"))
    assertTrue(ghDag.contains("concurrency:"))
    assertTrue(ghDag.contains("timeout-minutes: 20"))
    assertTrue(ghDag.contains("timeout-minutes: 10"))

    // 3. Terraform Google Provider v6.0 & state security docs
    val tf = OrchestrationTemplates.all.first { it.id == "terraform_llm_infra" }.content
    assertTrue(tf.contains("cloud_run_deletion_protection"))
    assertTrue(tf.contains("deletion_protection = var.cloud_run_deletion_protection"))
    assertTrue(tf.contains("default_labels"))
    assertTrue(tf.contains("tfstate"))
  }

  @Test
  fun `github service sends official X-GitHub-Api-Version header`() = runBlocking {
    val server = ServerSocket(0)
    val port = server.localPort
    val receivedApiVersions = mutableListOf<String>()

    val t = thread(isDaemon = true) {
      while (!server.isClosed) {
        val sock = runCatching { server.accept() }.getOrNull() ?: break
        sock.use {
          val reader = it.getInputStream().bufferedReader()
          val reqLine = reader.readLine() ?: ""
          val path = reqLine.split(" ").getOrNull(1) ?: "/"
          while (true) {
            val line = reader.readLine() ?: break
            if (line.isEmpty()) break
            if (line.startsWith("X-GitHub-Api-Version:", ignoreCase = true)) {
              synchronized(receivedApiVersions) {
                receivedApiVersions.add(line.substringAfter(":").trim())
              }
            }
          }
          val body = when {
            path.startsWith("/user/repos") -> "[]"
            path.startsWith("/user") -> """{"login":"octocat"}"""
            else -> "{}"
          }
          val resp = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: ${body.length}\r\nConnection: close\r\n\r\n$body"
          it.getOutputStream().write(resp.toByteArray(Charsets.UTF_8))
        }
      }
    }

    try {
      val service = com.example.data.GitHubActivityService(apiBaseUrl = "http://127.0.0.1:$port")
      val status = service.fetchRepositoryActivitySummary(
        pat = "ghp_test_token_1234",
        ownerRepo = "",
        workflowFile = ""
      )
      assertEquals("octocat", status.accountLogin)
      assertTrue(receivedApiVersions.isNotEmpty())
      assertTrue(receivedApiVersions.all { it == "2022-11-28" })
    } finally {
      server.close()
      t.interrupt()
    }
  }
}
