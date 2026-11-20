package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import com.example.model.CrawlMode
import com.example.model.DeviceViewportProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.concurrent.TimeUnit

data class CrawlOutput(
    val normalizedUrl: String,
    val pageCount: Int,
    val markdownContent: String,
    val summarySnippet: String,
    val markdownUri: Uri,
    val markdownFileName: String,
    val screenshotUri: Uri? = null,
    val screenshotNote: String = ""
)

/**
 * Fetches single URLs or crawls same-domain pages (with latest mobile device User-Agent
 * profiles), optionally calls a Cloudflare Browser Rendering endpoint (/v1/screenshot),
 * and persists captured Markdown & device screenshot PNGs in cacheDir for Gemini multimodal
 * analysis.
 */
class WebContextCrawler(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun normalizeUrlOrDomain(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
        return withScheme
    }

    suspend fun crawlAndCapture(
        rawUrlOrDomain: String,
        mode: CrawlMode,
        profile: DeviceViewportProfile,
        gatewayBaseUrl: String = "",
        gatewayBearerToken: String = "",
        firebaseIdToken: String? = null,
        maxPages: Int = 4
    ): Result<CrawlOutput> = withContext(Dispatchers.IO) {
        val startUrl = normalizeUrlOrDomain(rawUrlOrDomain)
        val parsedRoot = runCatching { URI(startUrl) }.getOrNull()
        val host = parsedRoot?.host?.lowercase().orEmpty()
        if (startUrl.isBlank() || host.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("有効な URL またはドメイン（例: google.com）を入力してください。")
            )
        }

        val visited = linkedSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(startUrl)

        val pageSections = mutableListOf<String>()
        val pageLimit = if (mode == CrawlMode.DOMAIN_CRAWL) maxPages.coerceIn(1, 5) else 1

        // For domain crawl, check sitemap.xml gently first for extra URLs
        if (mode == CrawlMode.DOMAIN_CRAWL) {
            discoverSitemapUrls(parsedRoot?.scheme ?: "https", host, profile.userAgent, limit = 3)
                .forEach { if (!queue.contains(it)) queue.add(it) }
        }

        var firstError: Throwable? = null
        while (queue.isNotEmpty() && visited.size < pageLimit) {
            val target = queue.removeFirst()
            if (!visited.add(target)) continue

            val fetchResult = runCatching { fetchSinglePage(target, profile.userAgent) }
            fetchResult.onSuccess { page ->
                pageSections.add(page.toMarkdownSection())
                if (mode == CrawlMode.DOMAIN_CRAWL && visited.size < pageLimit) {
                    page.sameOriginLinks
                        .filter { it !in visited && !queue.contains(it) }
                        .take(3)
                        .forEach { queue.add(it) }
                }
            }.onFailure { err ->
                if (firstError == null) firstError = err
            }
        }

        if (pageSections.isEmpty()) {
            return@withContext Result.failure(
                firstError ?: IllegalStateException("$startUrl のコンテンツを取得できませんでした。")
            )
        }

        val captureDir = File(context.cacheDir, "web_captures").apply { mkdirs() }
        val safeHost = host.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val stamp = System.currentTimeMillis()
        val mdFile = File(captureDir, "crawl_${safeHost}_$stamp.md")

        val fullMarkdown = buildString {
            appendLine("# Web Crawl Context: $startUrl")
            appendLine("- Mode: ${mode.label}")
            appendLine("- Device Profile: ${profile.label} (${profile.widthDp}x${profile.heightDp}dp)")
            appendLine("- User-Agent: `${profile.userAgent}`")
            appendLine("- Pages Fetched: ${pageSections.size}")
            appendLine()
            append(pageSections.joinToString("\n\n---\n\n"))
        }
        mdFile.writeText(fullMarkdown, Charsets.UTF_8)

        // Optional remote Browser Rendering screenshot if gc2223.com Gateway supports /v1/screenshot
        var remoteScreenshotUri: Uri? = null
        var screenshotNote = ""
        if (gatewayBaseUrl.isNotBlank()) {
            val remoteRes = tryFetchGatewayScreenshot(
                gatewayBaseUrl = gatewayBaseUrl,
                bearerToken = gatewayBearerToken,
                firebaseIdToken = firebaseIdToken,
                targetUrl = startUrl,
                profile = profile,
                outFile = File(captureDir, "screen_${safeHost}_$stamp.png")
            )
            remoteScreenshotUri = remoteRes.first
            screenshotNote = remoteRes.second
        }

        val snippet = pageSections.firstOrNull()?.lines()?.take(6)?.joinToString("\n").orEmpty()
        Result.success(
            CrawlOutput(
                normalizedUrl = startUrl,
                pageCount = pageSections.size,
                markdownContent = fullMarkdown,
                summarySnippet = snippet,
                markdownUri = Uri.fromFile(mdFile),
                markdownFileName = mdFile.name,
                screenshotUri = remoteScreenshotUri,
                screenshotNote = screenshotNote
            )
        )
    }

    suspend fun saveDeviceScreenshotBitmap(
        bitmap: Bitmap,
        rawUrlOrDomain: String,
        profile: DeviceViewportProfile
    ): Uri = withContext(Dispatchers.IO) {
        val captureDir = File(context.cacheDir, "web_captures").apply { mkdirs() }
        val host = runCatching {
            URI(normalizeUrlOrDomain(rawUrlOrDomain)).host ?: "device"
        }.getOrDefault("device").replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val file = File(captureDir, "device_${profile.name.lowercase()}_${host}_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 92, out)
        }
        Uri.fromFile(file)
    }

    private fun tryFetchGatewayScreenshot(
        gatewayBaseUrl: String,
        bearerToken: String,
        firebaseIdToken: String?,
        targetUrl: String,
        profile: DeviceViewportProfile,
        outFile: File
    ): Pair<Uri?, String> {
        val cleanBase = gatewayBaseUrl.trim().trimEnd('/')
        if (!cleanBase.startsWith("http://") && !cleanBase.startsWith("https://")) {
            return null to ""
        }
        return runCatching {
            val body = JSONObject().apply {
                put("url", targetUrl)
                put("device", profile.label)
                put("width", profile.widthDp)
                put("height", profile.heightDp)
                put("userAgent", profile.userAgent)
            }.toString()
            val builder = Request.Builder()
                .url("$cleanBase/v1/screenshot")
                .post(body.toRequestBody(jsonMediaType))
            if (bearerToken.isNotBlank()) {
                builder.header("Authorization", "Bearer ${bearerToken.trim()}")
            }
            if (!firebaseIdToken.isNullOrBlank()) {
                builder.header("X-Firebase-Token", firebaseIdToken)
            }
            client.newCall(builder.build()).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return@runCatching null to "GW /v1/screenshot HTTP ${resp.code}（端末内 WebView キャプチャを利用可能）"
                }
                val ctype = resp.header("Content-Type").orEmpty()
                if (ctype.startsWith("image/")) {
                    val bytes = resp.body?.bytes() ?: return@runCatching null to "Empty image"
                    outFile.writeBytes(bytes)
                    return@runCatching Uri.fromFile(outFile) to "Cloudflare Browser Rendering で画面取得済み"
                }
                val raw = resp.body?.string().orEmpty()
                val json = JSONObject(raw)
                val b64 = json.optString("screenshotBase64")
                if (b64.isNotBlank()) {
                    val decoded = Base64.decode(b64, Base64.DEFAULT)
                    outFile.writeBytes(decoded)
                    Uri.fromFile(outFile) to "Cloudflare Browser Rendering で画面取得済み"
                } else {
                    null to "スクリーンショットデータ未返却"
                }
            }
        }.getOrElse { null to "" }
    }

    private fun discoverSitemapUrls(scheme: String, host: String, userAgent: String, limit: Int): List<String> {
        return runCatching {
            val req = Request.Builder()
                .url("$scheme://$host/sitemap.xml")
                .header("User-Agent", userAgent)
                .get()
                .build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return emptyList()
                val xml = resp.body?.string().orEmpty().take(120_000)
                Regex("<loc>([^<]+)</loc>")
                    .findAll(xml)
                    .map { it.groupValues[1].trim() }
                    .filter { it.startsWith("http") && !it.endsWith(".xml") }
                    .take(limit)
                    .toList()
            }
        }.getOrDefault(emptyList())
    }

    private fun fetchSinglePage(url: String, userAgent: String): ParsedPage {
        val req = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "text/html,application/xhtml+xml,text/plain;q=0.9,*/*;q=0.8")
            .get()
            .build()

        client.newCall(req).execute().use { resp ->
            val raw = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw IllegalStateException("HTTP ${resp.code} ($url)")
            }
            val contentType = resp.header("Content-Type").orEmpty()
            if (!contentType.contains("html", ignoreCase = true) && !raw.contains("<html", ignoreCase = true)) {
                return ParsedPage(
                    url = url,
                    title = url,
                    description = "",
                    headings = emptyList(),
                    bodyText = raw.take(8_000),
                    sameOriginLinks = emptyList()
                )
            }

            val title = Regex("(?is)<title[^>]*>(.*?)</title>")
                .find(raw)?.groupValues?.getOrNull(1)
                ?.let(::cleanHtmlText)
                .orEmpty()
            val description = Regex("(?is)<meta[^>]+name=[\"']description[\"'][^>]+content=[\"'](.*?)[\"']")
                .find(raw)?.groupValues?.getOrNull(1)
                ?.let(::cleanHtmlText)
                .orEmpty()
            val headings = Regex("(?is)<h[1-3][^>]*>(.*?)</h[1-3]>")
                .findAll(raw)
                .map { cleanHtmlText(it.groupValues[1]) }
                .filter { it.isNotBlank() }
                .distinct()
                .take(15)
                .toList()

            val baseUri = runCatching { URI(url) }.getOrNull()
            val links = if (baseUri != null) {
                Regex("(?is)<a[^>]+href=[\"']([^\"'#]+)[\"']")
                    .findAll(raw)
                    .mapNotNull { match ->
                        val href = match.groupValues[1].trim()
                        if (href.startsWith("javascript:") || href.startsWith("mailto:")) return@mapNotNull null
                        runCatching { baseUri.resolve(href).toString() }.getOrNull()
                    }
                    .filter { resolved ->
                        val u = runCatching { URI(resolved) }.getOrNull()
                        u?.host.equals(baseUri.host, ignoreCase = true) &&
                            (u?.scheme == "http" || u?.scheme == "https")
                    }
                    .distinct()
                    .take(12)
                    .toList()
            } else {
                emptyList()
            }

            val stripped = raw
                .replace(Regex("(?is)<script[^>]*>.*?</script>"), " ")
                .replace(Regex("(?is)<style[^>]*>.*?</style>"), " ")
                .replace(Regex("(?is)<noscript[^>]*>.*?</noscript>"), " ")
                .replace(Regex("(?is)<svg[^>]*>.*?</svg>"), " ")
            val bodyText = cleanHtmlText(stripped).take(8_000)

            return ParsedPage(
                url = url,
                title = title.ifBlank { url },
                description = description,
                headings = headings,
                bodyText = bodyText,
                sameOriginLinks = links
            )
        }
    }

    private fun cleanHtmlText(html: String): String {
        return html
            .replace(Regex("<[^>]+>"), " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private data class ParsedPage(
        val url: String,
        val title: String,
        val description: String,
        val headings: List<String>,
        val bodyText: String,
        val sameOriginLinks: List<String>
    ) {
        fun toMarkdownSection(): String = buildString {
            appendLine("## $title")
            appendLine("URL: $url")
            if (description.isNotBlank()) {
                appendLine("Meta Description: $description")
            }
            if (headings.isNotEmpty()) {
                appendLine("Headings:")
                headings.forEach { appendLine("- $it") }
            }
            appendLine()
            appendLine(bodyText)
        }
    }
}
