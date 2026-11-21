package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import com.example.model.CrawlMode
import com.example.model.DeviceViewportProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.InetAddress
import java.net.URI
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

data class CrawlOutput(
    val normalizedUrl: String,
    val pageCount: Int,
    val markdownContent: String,
    val summarySnippet: String,
    val markdownUri: Uri,
    val markdownFileName: String,
    val screenshotUri: Uri? = null,
    val screenshotNote: String = "",
    val perPageMarkdownFiles: List<String> = emptyList(),
    val sessionDirectoryName: String = "",
    val languageSummary: String = ""
)

/**
 * Fetches single URLs, domain pages, or recursively crawls pages under a strict URL path prefix
 * (e.g. https://cloud.google.com/docs -> https://docs.cloud.google.com/docs/...) as structured
 * Markdown files. Defaults to English-first (`Accept-Language: en-US,en;q=0.9` + `hreflang="en"`)
 * and automatically falls back to locale-specific pages when only a non-English page exists.
 */
class WebContextCrawler(
    private val context: Context,
    private val allowLoopbackForTest: Boolean = false,
    private val client: OkHttpClient = buildDefaultClient(allowLoopbackForTest)
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    companion object {
        private fun isRestrictedAddress(addr: InetAddress): Boolean {
            if (addr.isLoopbackAddress ||
                addr.isSiteLocalAddress ||
                addr.isLinkLocalAddress ||
                addr.isAnyLocalAddress ||
                addr.isMulticastAddress
            ) {
                return true
            }
            val raw = addr.address
            // IPv4 100.64.0.0/10 (Carrier-Grade NAT)
            if (raw.size == 4 && (raw[0].toInt() and 0xFF) == 100 && (raw[1].toInt() and 0xC0) == 64) {
                return true
            }
            // IPv6 fc00::/7 (Unique Local Address)
            if (raw.size == 16 && (raw[0].toInt() and 0xFE) == 0xFC) {
                return true
            }
            return false
        }

        private fun buildDefaultClient(allowLoopbackForTest: Boolean): OkHttpClient {
            val guardedDns = object : Dns {
                override fun lookup(hostname: String): List<InetAddress> {
                    val resolved = Dns.SYSTEM.lookup(hostname)
                    if (allowLoopbackForTest) return resolved
                    val publicOnly = resolved.filterNot(::isRestrictedAddress)
                    if (publicOnly.isEmpty()) {
                        throw UnknownHostException("非公開またはループバック IP への名前解決をブロックしました: $hostname")
                    }
                    return publicOnly
                }
            }
            return OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .followRedirects(true)
                .dns(guardedDns)
                .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
                .build()
        }
    }

    fun normalizeUrlOrDomain(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    internal fun validateSafeExternalUrl(rawUrl: String): Result<String> {
        val parsed = runCatching { URI(rawUrl) }.getOrNull()
            ?: return Result.failure(IllegalArgumentException("無効な URL 形式です: $rawUrl"))
        val scheme = parsed.scheme?.lowercase().orEmpty()
        if (scheme != "http" && scheme != "https") {
            return Result.failure(IllegalArgumentException("HTTP/HTTPS スキームのみ許可されています ($scheme)"))
        }
        val host = parsed.host?.lowercase()?.trim('.').orEmpty()
        if (host.isBlank()) {
            return Result.failure(IllegalArgumentException("ホスト名が空です"))
        }
        if (host == "metadata.google.internal" || host.endsWith(".internal") || host.endsWith(".local")) {
            return Result.failure(IllegalArgumentException("内部メタデータ/ローカルホストへのアクセスはブロックされました: $host"))
        }
        val isLoopback = host == "localhost" || host == "::1" || host == "[::1]" || host.startsWith("127.")
        if (isLoopback && !allowLoopbackForTest) {
            return Result.failure(IllegalArgumentException("ループバックアドレスへのアクセスはブロックされました: $host"))
        }
        if (host == "0.0.0.0" ||
            host.startsWith("169.254.") ||
            host.startsWith("10.") ||
            host.startsWith("192.168.") ||
            Regex("^172\\.(1[6-9]|2\\d|3[0-1])\\.").containsMatchIn(host)
        ) {
            return Result.failure(IllegalArgumentException("プライベート / リンクローカル IP へのアクセスはブロックされました: $host"))
        }
        if (!isLoopback && Regex("^\\d+\\.\\d+\\.\\d+\\.\\d+$").matches(host)) {
            val addr = runCatching { InetAddress.getByName(host) }.getOrNull()
            if (addr != null && isRestrictedAddress(addr)) {
                return Result.failure(IllegalArgumentException("非公開 IP へのアクセスはブロックされました: $host"))
            }
        }
        return Result.success(rawUrl)
    }

    internal fun canonicalizeUrl(rawUrl: String, keepHlParam: String? = null): String {
        val httpUrl = rawUrl.toHttpUrlOrNull() ?: return rawUrl.substringBefore('#').trimEnd('/').ifEmpty { rawUrl }
        val builder = httpUrl.newBuilder().fragment(null)
        val paramNames = httpUrl.queryParameterNames.toList()
        if (paramNames.isNotEmpty()) {
            paramNames.forEach { name ->
                val lower = name.lowercase()
                val shouldStrip = lower.startsWith("utm_") ||
                    (lower == "hl" && keepHlParam == null)
                if (shouldStrip) {
                    builder.removeAllQueryParameters(name)
                }
            }
        }
        val built = builder.build().toString()
        val path = httpUrl.encodedPath
        return if (path.length > 1 && built.endsWith("/") && httpUrl.query == null) {
            built.dropLast(1)
        } else {
            built
        }
    }

    internal fun extractPathPrefix(uri: URI): String {
        val rawPath = uri.path.orEmpty().trim()
        if (rawPath.isBlank() || rawPath == "/") return "/"
        return "/" + rawPath.trim('/').lowercase()
    }

    internal fun isWithinPathPrefix(candidate: URI, canonicalHost: String, canonicalPrefix: String): Boolean {
        val candHost = candidate.host?.lowercase().orEmpty()
        val candScheme = candidate.scheme?.lowercase().orEmpty()
        if (candHost != canonicalHost || (candScheme != "http" && candScheme != "https")) return false
        val candPath = candidate.path.orEmpty().ifBlank { "/" }.let {
            if (it == "/") "/" else "/" + it.trim('/').lowercase()
        }
        if (isBinaryOrAssetPath(candPath)) return false
        if (canonicalPrefix == "/") return true
        return candPath == canonicalPrefix || candPath.startsWith("$canonicalPrefix/")
    }

    private fun isBinaryOrAssetPath(path: String): Boolean {
        val lower = path.lowercase()
        val assetExts = listOf(
            ".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg", ".ico",
            ".css", ".js", ".woff", ".woff2", ".ttf", ".zip", ".tar",
            ".gz", ".pdf", ".xml", ".mp4", ".webm", ".json"
        )
        return assetExts.any { lower.endsWith(it) }
    }

    private fun isEnglishLang(lang: String): Boolean {
        val clean = lang.trim().lowercase()
        return clean == "en" || clean.startsWith("en-") || clean.startsWith("en_")
    }

    private fun recordLanguageFallbackCandidate(
        rawUrl: String,
        canonicalUrl: String,
        fallbackMap: MutableMap<String, String>
    ) {
        val httpUrl = rawUrl.toHttpUrlOrNull() ?: return
        val hl = httpUrl.queryParameter("hl")?.trim()?.lowercase().orEmpty()
        if (hl.isNotBlank() && !isEnglishLang(hl) && !fallbackMap.containsKey(canonicalUrl)) {
            fallbackMap[canonicalUrl] = httpUrl.newBuilder().fragment(null).build().toString()
        }
    }

    suspend fun crawlAndCapture(
        rawUrlOrDomain: String,
        mode: CrawlMode,
        profile: DeviceViewportProfile,
        gatewayBaseUrl: String = "",
        gatewayBearerToken: String = "",
        firebaseIdToken: String? = null,
        maxPages: Int = if (mode == CrawlMode.PATH_RECURSIVE) 20 else 4,
        preferEnglish: Boolean = true
    ): Result<CrawlOutput> = withContext(Dispatchers.IO) {
        val normalizedSeed = normalizeUrlOrDomain(rawUrlOrDomain)
        val safetyCheck = validateSafeExternalUrl(normalizedSeed)
        if (safetyCheck.isFailure) {
            return@withContext Result.failure(
                safetyCheck.exceptionOrNull() ?: IllegalArgumentException("不正な URL 形式です")
            )
        }
        val parsedSeed = runCatching { URI(normalizedSeed) }.getOrNull()
        val seedHost = parsedSeed?.host?.lowercase().orEmpty()
        if (normalizedSeed.isBlank() || seedHost.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("有効な URL またはドメイン（例: cloud.google.com/docs）を入力してください。")
            )
        }

        val rawSeedHl = normalizedSeed.toHttpUrlOrNull()?.queryParameter("hl")
        val keepHlParam = if (preferEnglish) null else rawSeedHl
        val initialCanonical = canonicalizeUrl(normalizedSeed, keepHlParam = keepHlParam)
        val fallbackLangUrls = linkedMapOf<String, String>()
        if (preferEnglish && !rawSeedHl.isNullOrBlank() && !isEnglishLang(rawSeedHl)) {
            recordLanguageFallbackCandidate(normalizedSeed, initialCanonical, fallbackLangUrls)
        }

        val pageLimit = when (mode) {
            CrawlMode.SINGLE_URL -> 1
            CrawlMode.DOMAIN_CRAWL -> maxPages.coerceIn(1, 5)
            CrawlMode.PATH_RECURSIVE -> maxPages.coerceIn(1, 50)
        }

        val visited = linkedSetOf<String>()
        val queue = ArrayDeque<String>()
        val pages = mutableListOf<ParsedPage>()
        var canonicalHost = seedHost
        var canonicalAuthority = parsedSeed?.let { if (it.port > 0) "${it.host.lowercase()}:${it.port}" else it.host.lowercase() } ?: seedHost
        var canonicalScheme = parsedSeed?.scheme?.lowercase() ?: "https"
        var canonicalPrefix = parsedSeed?.let(::extractPathPrefix) ?: "/"

        // Fetch root page first (preferring English; falling back to native-only ?hl= if English 404s)
        var rootFetch = runCatching {
            fetchPageWithEnglishPreference(
                url = initialCanonical,
                userAgent = profile.userAgent,
                restrictPathPrefix = null,
                expectedHost = null,
                keepHlParam = keepHlParam,
                preferEnglish = preferEnglish,
                fallbackMap = fallbackLangUrls
            )
        }
        if (rootFetch.isFailure) {
            val nativeSeed = fallbackLangUrls[initialCanonical]
            if (nativeSeed != null && nativeSeed != initialCanonical) {
                rootFetch = runCatching {
                    fetchSinglePage(
                        url = nativeSeed,
                        userAgent = profile.userAgent,
                        restrictPathPrefix = null,
                        keepHlParam = "native",
                        preferEnglishHeader = false,
                        fallbackMap = fallbackLangUrls,
                        isNativeOnlyFallback = true
                    )
                }
            }
        }

        var firstError: Throwable? = rootFetch.exceptionOrNull()
        rootFetch.onSuccess { rootPage ->
            visited.add(initialCanonical)
            visited.add(canonicalizeUrl(rootPage.url, keepHlParam = keepHlParam))
            visited.add(rootPage.url)
            pages.add(rootPage)

            val resolvedRootUri = runCatching { URI(rootPage.url) }.getOrNull()
            if (resolvedRootUri != null && !resolvedRootUri.host.isNullOrBlank()) {
                val postRedirectSafe = validateSafeExternalUrl(rootPage.url)
                if (postRedirectSafe.isFailure) {
                    return@withContext Result.failure(postRedirectSafe.exceptionOrNull()!!)
                }
                canonicalHost = resolvedRootUri.host.lowercase()
                canonicalAuthority = if (resolvedRootUri.port > 0) "${canonicalHost}:${resolvedRootUri.port}" else canonicalHost
                canonicalScheme = resolvedRootUri.scheme?.lowercase() ?: canonicalScheme
                if (mode == CrawlMode.PATH_RECURSIVE) {
                    canonicalPrefix = extractPathPrefix(resolvedRootUri)
                }
            }

            if (mode != CrawlMode.SINGLE_URL) {
                val linkScopePrefix = if (mode == CrawlMode.PATH_RECURSIVE) canonicalPrefix else null
                rootPage.sameOriginLinks
                    .filter { link ->
                        val u = runCatching { URI(link) }.getOrNull() ?: return@filter false
                        if (linkScopePrefix != null) {
                            isWithinPathPrefix(u, canonicalHost, linkScopePrefix)
                        } else {
                            u.host.equals(canonicalHost, ignoreCase = true)
                        }
                    }
                    .forEach { link ->
                        if (link !in visited && !queue.contains(link)) {
                            queue.add(link)
                        }
                    }

                val sitemapLimit = if (mode == CrawlMode.PATH_RECURSIVE) pageLimit else 3
                discoverSitemapUrls(
                    scheme = canonicalScheme,
                    authority = canonicalAuthority,
                    host = canonicalHost,
                    userAgent = profile.userAgent,
                    limit = sitemapLimit,
                    pathPrefix = linkScopePrefix,
                    keepHlParam = keepHlParam,
                    fallbackMap = fallbackLangUrls
                ).forEach { smUrl ->
                    if (smUrl !in visited && !queue.contains(smUrl)) {
                        queue.add(smUrl)
                    }
                }
            }
        }

        val linkScopePrefix = if (mode == CrawlMode.PATH_RECURSIVE) canonicalPrefix else null
        while (mode != CrawlMode.SINGLE_URL && queue.isNotEmpty() && pages.size < pageLimit) {
            val target = queue.removeFirst()
            val canonicalTarget = canonicalizeUrl(target, keepHlParam = keepHlParam)
            if (!visited.add(canonicalTarget)) continue
            if (validateSafeExternalUrl(canonicalTarget).isFailure) continue

            var fetchResult = runCatching {
                fetchPageWithEnglishPreference(
                    url = canonicalTarget,
                    userAgent = profile.userAgent,
                    restrictPathPrefix = linkScopePrefix,
                    expectedHost = canonicalHost,
                    keepHlParam = keepHlParam,
                    preferEnglish = preferEnglish,
                    fallbackMap = fallbackLangUrls
                )
            }

            // If English canonical returns 404/410/error and a locale-specific candidate exists, fetch it
            if (fetchResult.isFailure && preferEnglish) {
                val nativeUrl = fallbackLangUrls[canonicalTarget]
                if (nativeUrl != null && nativeUrl != canonicalTarget && validateSafeExternalUrl(nativeUrl).isSuccess) {
                    fetchResult = runCatching {
                        fetchSinglePage(
                            url = nativeUrl,
                            userAgent = profile.userAgent,
                            restrictPathPrefix = linkScopePrefix,
                            expectedHost = canonicalHost,
                            keepHlParam = "native",
                            preferEnglishHeader = false,
                            fallbackMap = fallbackLangUrls,
                            isNativeOnlyFallback = true
                        )
                    }
                }
            }

            fetchResult.onSuccess { page ->
                visited.add(page.url)
                pages.add(page)
                if (pages.size < pageLimit) {
                    page.sameOriginLinks
                        .filter { it !in visited && !queue.contains(it) }
                        .take(if (mode == CrawlMode.PATH_RECURSIVE) 60 else 5)
                        .forEach { queue.add(it) }
                }
            }.onFailure { err ->
                if (firstError == null) firstError = err
            }
        }

        if (pages.isEmpty()) {
            return@withContext Result.failure(
                firstError ?: IllegalStateException("$normalizedSeed のコンテンツを取得できませんでした。")
            )
        }

        val captureDir = File(context.cacheDir, "web_captures").apply { mkdirs() }
        val safeHost = canonicalHost.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val stamp = System.currentTimeMillis()
        val sessionDirName = "session_${safeHost}_$stamp"
        val sessionDir = File(captureDir, sessionDirName).apply { mkdirs() }

        val perPageFiles = mutableListOf<String>()
        pages.forEachIndexed { idx, page ->
            val slug = buildSafeMarkdownFilename(page.url, idx + 1)
            val perPageFile = File(sessionDir, slug)
            perPageFile.writeText(page.toStandaloneMarkdown(), Charsets.UTF_8)
            perPageFiles.add(slug)
        }

        val langCounts = pages.groupingBy { it.language.ifBlank { "unknown" } }.eachCount()
        val nativeFallbackCount = pages.count { it.isNativeOnlyFallback }
        val langSummary = langCounts.entries.joinToString(", ") { "${it.key}:${it.value}" } +
            if (nativeFallbackCount > 0) " (固有言語フォールバック:$nativeFallbackCount)" else ""

        val mdFile = File(captureDir, "crawl_${safeHost}_$stamp.md")
        val finalSeedDisplayUrl = pages.first().url
        val fullMarkdown = buildString {
            appendLine("# Web Crawl Context: $finalSeedDisplayUrl")
            appendLine("- Mode: ${mode.label}")
            appendLine("- Language Policy: ${if (preferEnglish) "English-first (native fallback when no EN exists)" else "Source/Seed locale"}")
            appendLine("- Languages Captured: $langSummary")
            if (mode == CrawlMode.PATH_RECURSIVE) {
                appendLine("- Scope Prefix: `$canonicalHost$canonicalPrefix`")
            }
            appendLine("- Device Profile: ${profile.label} (${profile.widthDp}x${profile.heightDp}dp)")
            appendLine("- User-Agent: `${profile.userAgent}`")
            appendLine("- Pages Fetched: ${pages.size}")
            appendLine("- Individual .md Files (`$sessionDirName/`):")
            pages.forEachIndexed { idx, page ->
                val langBadge = if (page.isNativeOnlyFallback) "[${page.language}:固有言語]" else "[${page.language}]"
                appendLine("  ${idx + 1}. `${perPageFiles[idx]}` $langBadge — ${page.title} (${page.url})")
            }
            appendLine()
            append(pages.joinToString("\n\n---\n\n") { it.toMarkdownSection() })
        }
        mdFile.writeText(fullMarkdown, Charsets.UTF_8)

        var remoteScreenshotUri: Uri? = null
        var screenshotNote = ""
        if (gatewayBaseUrl.isNotBlank()) {
            val remoteRes = tryFetchGatewayScreenshot(
                gatewayBaseUrl = gatewayBaseUrl,
                bearerToken = gatewayBearerToken,
                firebaseIdToken = firebaseIdToken,
                targetUrl = finalSeedDisplayUrl,
                profile = profile,
                outFile = File(captureDir, "screen_${safeHost}_$stamp.png")
            )
            remoteScreenshotUri = remoteRes.first
            screenshotNote = remoteRes.second
        }

        val snippet = pages.firstOrNull()?.toMarkdownSection()?.lines()?.take(6)?.joinToString("\n").orEmpty()
        Result.success(
            CrawlOutput(
                normalizedUrl = finalSeedDisplayUrl,
                pageCount = pages.size,
                markdownContent = fullMarkdown,
                summarySnippet = snippet,
                markdownUri = Uri.fromFile(mdFile),
                markdownFileName = mdFile.name,
                screenshotUri = remoteScreenshotUri,
                screenshotNote = screenshotNote,
                perPageMarkdownFiles = perPageFiles,
                sessionDirectoryName = sessionDirName,
                languageSummary = langSummary
            )
        )
    }

    private fun fetchPageWithEnglishPreference(
        url: String,
        userAgent: String,
        restrictPathPrefix: String?,
        expectedHost: String?,
        keepHlParam: String?,
        preferEnglish: Boolean,
        fallbackMap: MutableMap<String, String>
    ): ParsedPage {
        val initialPage = fetchSinglePage(
            url = url,
            userAgent = userAgent,
            restrictPathPrefix = restrictPathPrefix,
            expectedHost = expectedHost,
            keepHlParam = keepHlParam,
            preferEnglishHeader = preferEnglish,
            fallbackMap = fallbackMap,
            isNativeOnlyFallback = false
        )
        if (!preferEnglish || isEnglishLang(initialPage.language)) {
            return initialPage
        }

        // Page returned non-English HTML; check explicit hreflang="en" alternate first
        val candidateEnUrl = initialPage.englishAlternateUrl?.takeIf { enAlt ->
            val u = runCatching { URI(enAlt) }.getOrNull() ?: return@takeIf false
            val host = (expectedHost ?: runCatching { URI(initialPage.url).host }.getOrNull()).orEmpty()
            val prefixOk = restrictPathPrefix == null || isWithinPathPrefix(u, host.lowercase(), restrictPathPrefix)
            validateSafeExternalUrl(enAlt).isSuccess && prefixOk && enAlt != initialPage.url
        }

        if (candidateEnUrl != null) {
            val enTry = runCatching {
                fetchSinglePage(
                    url = candidateEnUrl,
                    userAgent = userAgent,
                    restrictPathPrefix = restrictPathPrefix,
                    expectedHost = expectedHost,
                    keepHlParam = "en",
                    preferEnglishHeader = true,
                    fallbackMap = fallbackMap,
                    isNativeOnlyFallback = false
                )
            }.getOrNull()
            if (enTry != null && isEnglishLang(enTry.language)) {
                return enTry
            }
        }

        // No English alternate succeeded -> keep native-language page and tag as native-only
        return initialPage.copy(isNativeOnlyFallback = true)
    }

    private fun buildSafeMarkdownFilename(url: String, index: Int): String {
        val rawPath = runCatching { URI(url).path.orEmpty() }.getOrDefault("")
            .trim('/')
            .ifBlank { "index" }
        val sanitized = rawPath
            .replace('/', '_')
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .take(80)
            .trim('_')
            .ifBlank { "page" }
        return "${String.format("%02d", index)}_${sanitized}.md"
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

    private fun discoverSitemapUrls(
        scheme: String,
        authority: String,
        host: String,
        userAgent: String,
        limit: Int,
        pathPrefix: String? = null,
        keepHlParam: String? = null,
        fallbackMap: MutableMap<String, String> = mutableMapOf()
    ): List<String> {
        val sitemapQueue = ArrayDeque<String>()
        sitemapQueue.add("$scheme://$authority/sitemap.xml")

        // Also parse Sitemap: directives from robots.txt
        runCatching {
            val robotsReq = Request.Builder()
                .url("$scheme://$authority/robots.txt")
                .header("User-Agent", userAgent)
                .get()
                .build()
            client.newCall(robotsReq).execute().use { resp ->
                if (resp.isSuccessful) {
                    val txt = resp.body?.string().orEmpty().take(64_000)
                    Regex("(?im)^\\s*Sitemap:\\s*(https?://\\S+)")
                        .findAll(txt)
                        .map { it.groupValues[1].trim() }
                        .filter { sm ->
                            val u = runCatching { URI(sm) }.getOrNull()
                            u != null && u.host.equals(host, ignoreCase = true) && validateSafeExternalUrl(sm).isSuccess
                        }
                        .take(4)
                        .forEach { sm ->
                            if (!sitemapQueue.contains(sm)) sitemapQueue.add(sm)
                        }
                }
            }
        }

        val discovered = linkedSetOf<String>()
        val visitedSitemaps = mutableSetOf<String>()
        var childSitemapsFetched = 0

        while (sitemapQueue.isNotEmpty() && discovered.size < limit && childSitemapsFetched < 5) {
            val smUrl = sitemapQueue.removeFirst()
            if (!visitedSitemaps.add(smUrl)) continue
            childSitemapsFetched++

            val xml = runCatching {
                val req = Request.Builder()
                    .url(smUrl)
                    .header("User-Agent", userAgent)
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) "" else resp.body?.string().orEmpty().take(500_000)
                }
            }.getOrDefault("")

            if (xml.isBlank()) continue

            // Recurse into child <sitemap><loc>...</loc></sitemap> inside sitemapindex
            Regex("(?is)<sitemap[^>]*>\\s*<loc>([^<]+)</loc>")
                .findAll(xml)
                .map { it.groupValues[1].trim() }
                .filter { child ->
                    val u = runCatching { URI(child) }.getOrNull()
                    u != null && u.host.equals(host, ignoreCase = true) && validateSafeExternalUrl(child).isSuccess
                }
                .take(4)
                .forEach { child ->
                    if (child !in visitedSitemaps && !sitemapQueue.contains(child)) {
                        sitemapQueue.add(child)
                    }
                }

            Regex("(?is)<loc>([^<]+)</loc>")
                .findAll(xml)
                .map { it.groupValues[1].trim() }
                .filter { it.startsWith("http") && !it.endsWith(".xml") }
                .forEach { rawLoc ->
                    val canonical = canonicalizeUrl(rawLoc, keepHlParam = keepHlParam)
                    val u = runCatching { URI(canonical) }.getOrNull() ?: return@forEach
                    val inScope = if (pathPrefix != null) {
                        isWithinPathPrefix(u, host, pathPrefix)
                    } else {
                        u.host.equals(host, ignoreCase = true) && !isBinaryOrAssetPath(u.path.orEmpty())
                    }
                    if (inScope) {
                        recordLanguageFallbackCandidate(rawLoc, canonical, fallbackMap)
                        discovered.add(canonical)
                    }
                }
        }
        return discovered.take(limit)
    }

    private fun fetchSinglePage(
        url: String,
        userAgent: String,
        restrictPathPrefix: String? = null,
        expectedHost: String? = null,
        keepHlParam: String? = null,
        preferEnglishHeader: Boolean = true,
        fallbackMap: MutableMap<String, String> = mutableMapOf(),
        isNativeOnlyFallback: Boolean = false
    ): ParsedPage {
        val reqBuilder = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "text/html,application/xhtml+xml,text/markdown,text/plain;q=0.9,*/*;q=0.8")
        if (preferEnglishHeader) {
            reqBuilder.header("Accept-Language", "en-US,en;q=0.9")
        }
        val req = reqBuilder.get().build()

        client.newCall(req).execute().use { resp ->
            val finalUrl = canonicalizeUrl(resp.request.url.toString(), keepHlParam = keepHlParam)
            val raw = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw IllegalStateException("HTTP ${resp.code} ($finalUrl)")
            }
            val contentType = resp.header("Content-Type").orEmpty()
            if (!contentType.contains("html", ignoreCase = true) && !raw.contains("<html", ignoreCase = true)) {
                return ParsedPage(
                    url = finalUrl,
                    title = finalUrl,
                    description = "",
                    headings = emptyList(),
                    bodyMarkdown = raw.take(12_000),
                    sameOriginLinks = emptyList(),
                    language = resp.header("Content-Language").orEmpty().ifBlank { "en" },
                    englishAlternateUrl = null,
                    isNativeOnlyFallback = isNativeOnlyFallback
                )
            }

            val htmlLang = Regex("(?is)<html[^>]+lang=[\"']([^\"']+)[\"']")
                .find(raw)?.groupValues?.getOrNull(1)?.trim()?.lowercase()
                .orEmpty()
                .ifBlank {
                    resp.request.url.queryParameter("hl")?.trim()?.lowercase()
                        ?: resp.header("Content-Language")?.trim()?.lowercase()
                        ?: "en"
                }

            val baseUri = runCatching { URI(finalUrl) }.getOrNull()
            val enAlternateUrl = extractEnglishAlternateUrl(raw, baseUri)

            val title = Regex("(?is)<title[^>]*>(.*?)</title>")
                .find(raw)?.groupValues?.getOrNull(1)
                ?.let(::cleanHtmlText)
                .orEmpty()
            val description = Regex("(?is)<meta[^>]+name=[\"']description[\"'][^>]+content=[\"'](.*?)[\"']")
                .find(raw)?.groupValues?.getOrNull(1)
                ?.let(::cleanHtmlText)
                .orEmpty()

            val mainHtml = extractPrimaryHtmlRegion(raw)
            val headings = Regex("(?is)<h[1-4][^>]*>(.*?)</h[1-4]>")
                .findAll(mainHtml)
                .map { cleanHtmlText(it.groupValues[1]) }
                .filter { it.isNotBlank() }
                .distinct()
                .take(25)
                .toList()

            val scopeHost = (expectedHost ?: baseUri?.host)?.lowercase().orEmpty()
            val links = if (baseUri != null && scopeHost.isNotBlank()) {
                // Prioritize links inside main content before any remaining links in the document
                val orderedAnchors = (
                    extractHrefCandidates(mainHtml) + extractHrefCandidates(raw)
                    ).distinct()

                orderedAnchors
                    .mapNotNull { href ->
                        val resolvedRaw = runCatching { baseUri.resolve(href).toString() }.getOrNull()
                            ?: return@mapNotNull null
                        val canonical = canonicalizeUrl(resolvedRaw, keepHlParam = keepHlParam)
                        val u = runCatching { URI(canonical) }.getOrNull() ?: return@mapNotNull null
                        val inScope = if (restrictPathPrefix != null) {
                            isWithinPathPrefix(u, scopeHost, restrictPathPrefix)
                        } else {
                            u.host.equals(scopeHost, ignoreCase = true) &&
                                (u.scheme == "http" || u.scheme == "https") &&
                                !isBinaryOrAssetPath(u.path.orEmpty())
                        }
                        if (!inScope) return@mapNotNull null
                        recordLanguageFallbackCandidate(resolvedRaw, canonical, fallbackMap)
                        canonical
                    }
                    .distinct()
                    .take(100)
                    .toList()
            } else {
                emptyList()
            }

            val bodyMarkdown = htmlToStructuredMarkdown(mainHtml, baseUri).take(14_000)

            return ParsedPage(
                url = finalUrl,
                title = title.ifBlank { finalUrl },
                description = description,
                headings = headings,
                bodyMarkdown = bodyMarkdown,
                sameOriginLinks = links,
                language = htmlLang,
                englishAlternateUrl = enAlternateUrl,
                isNativeOnlyFallback = isNativeOnlyFallback
            )
        }
    }

    private fun extractEnglishAlternateUrl(rawHtml: String, baseUri: URI?): String? {
        val altLinks = Regex("(?is)<link[^>]+rel=[\"']alternate[\"'][^>]*>")
            .findAll(rawHtml)
            .map { it.value }
            .toList()
        for (tag in altLinks) {
            val hreflang = Regex("(?is)hreflang=[\"']([^\"']+)[\"']").find(tag)?.groupValues?.getOrNull(1)?.lowercase()
            if (hreflang == "en" || hreflang == "en-us") {
                val href = Regex("(?is)href=[\"']([^\"']+)[\"']").find(tag)?.groupValues?.getOrNull(1)?.trim()
                if (!href.isNullOrBlank()) {
                    return runCatching { baseUri?.resolve(href)?.toString() ?: href }.getOrNull()
                }
            }
        }
        return null
    }

    private fun extractHrefCandidates(html: String): List<String> {
        return Regex("(?is)<a[^>]+href=[\"']([^\"'#]+)[\"']")
            .findAll(html)
            .mapNotNull { match ->
                val href = match.groupValues[1].trim()
                if (href.isBlank() ||
                    href.startsWith("javascript:") ||
                    href.startsWith("mailto:") ||
                    href.startsWith("tel:")
                ) {
                    null
                } else {
                    href
                }
            }
            .toList()
    }

    internal fun extractPrimaryHtmlRegion(rawHtml: String): String {
        val strippedChrome = rawHtml
            .replace(Regex("(?is)<script[^>]*>.*?</script>"), " ")
            .replace(Regex("(?is)<style[^>]*>.*?</style>"), " ")
            .replace(Regex("(?is)<noscript[^>]*>.*?</noscript>"), " ")
            .replace(Regex("(?is)<svg[^>]*>.*?</svg>"), " ")
            .replace(Regex("(?is)<nav[^>]*>.*?</nav>"), " ")
            .replace(Regex("(?is)<aside[^>]*>.*?</aside>"), " ")
            .replace(Regex("(?is)<footer[^>]*>.*?</footer>"), " ")
            .replace(Regex("(?is)<devsite-book-nav[^>]*>.*?</devsite-book-nav>"), " ")
            .replace(Regex("(?is)<devsite-header[^>]*>.*?</devsite-header>"), " ")
            .replace(Regex("(?is)<devsite-footer[^>]*>.*?</devsite-footer>"), " ")
            .replace(Regex("(?is)<devsite-toc[^>]*>.*?</devsite-toc>"), " ")
            .replace(Regex("(?is)<devsite-actions[^>]*>.*?</devsite-actions>"), " ")
            .replace(Regex("(?is)<devsite-feedback[^>]*>.*?</devsite-feedback>"), " ")
            .replace(Regex("(?is)<devsite-thumbs-rating[^>]*>.*?</devsite-thumbs-rating>"), " ")

        val article = Regex("(?is)<article[^>]*>(.*?)</article>").find(strippedChrome)?.groupValues?.getOrNull(1)
        if (!article.isNullOrBlank() && cleanHtmlText(article).length > 80) return article

        val main = Regex("(?is)<main[^>]*>(.*?)</main>").find(strippedChrome)?.groupValues?.getOrNull(1)
        if (!main.isNullOrBlank() && cleanHtmlText(main).length > 80) return main

        return strippedChrome
    }

    internal fun htmlToStructuredMarkdown(html: String, baseUri: URI? = null): String {
        var md = html
        // Preserve fenced code blocks with optional language hint
        md = Regex("(?is)<pre([^>]*)>(.*?)</pre>").replace(md) { match ->
            val preAttrs = match.groupValues[1]
            val inner = match.groupValues[2]
            val langHint = Regex("(?i)(?:language-|lang-)([a-zA-Z0-9_+-]+)")
                .find("$preAttrs $inner")
                ?.groupValues?.getOrNull(1)
                ?.lowercase()
                .orEmpty()
            val codeText = decodeHtmlEntities(inner.replace(Regex("<[^>]+>"), ""))
                .trim('\r', '\n')
            "\n```$langHint\n$codeText\n```\n"
        }
        // Preserve inline code
        md = Regex("(?is)<code[^>]*>(.*?)</code>").replace(md) { match ->
            val codeText = cleanHtmlText(match.groupValues[1])
            if (codeText.isBlank()) "" else "`$codeText`"
        }
        // Preserve Markdown tables
        md = Regex("(?is)<table[^>]*>(.*?)</table>").replace(md) { tableMatch ->
            val tableHtml = tableMatch.groupValues[1]
            val rows = Regex("(?is)<tr[^>]*>(.*?)</tr>")
                .findAll(tableHtml)
                .map { rowMatch ->
                    Regex("(?is)<(?:th|td)[^>]*>(.*?)</(?:th|td)>")
                        .findAll(rowMatch.groupValues[1])
                        .map { cellMatch -> cleanHtmlText(cellMatch.groupValues[1]).replace("|", "\\|") }
                        .toList()
                }
                .filter { it.isNotEmpty() }
                .toList()
            if (rows.isEmpty()) return@replace ""
            val colCount = rows.maxOf { it.size }
            buildString {
                append("\n\n")
                rows.forEachIndexed { rowIdx, cells ->
                    val padded = cells + List((colCount - cells.size).coerceAtLeast(0)) { "" }
                    append("| ").append(padded.joinToString(" | ")).append(" |\n")
                    if (rowIdx == 0) {
                        append("| ").append(List(colCount) { "---" }.joinToString(" | ")).append(" |\n")
                    }
                }
                append("\n")
            }
        }
        // Preserve headings
        for (level in 1..4) {
            val prefix = "#".repeat(level)
            md = Regex("(?is)<h$level[^>]*>(.*?)</h$level>").replace(md) { match ->
                val text = cleanHtmlText(match.groupValues[1])
                if (text.isBlank()) "\n" else "\n\n$prefix $text\n\n"
            }
        }
        // Preserve links
        md = Regex("(?is)<a[^>]+href=[\"']([^\"']+)[\"'][^>]*>(.*?)</a>").replace(md) { match ->
            val href = match.groupValues[1].trim()
            val label = cleanHtmlText(match.groupValues[2])
            if (label.isBlank() || href.startsWith("javascript:")) {
                label
            } else {
                val resolved = runCatching { baseUri?.resolve(href)?.toString() ?: href }.getOrDefault(href)
                "[$label]($resolved)"
            }
        }
        // Preserve bold emphasis
        md = Regex("(?is)<(?:strong|b)[^>]*>(.*?)</(?:strong|b)>").replace(md) { match ->
            val inner = cleanHtmlText(match.groupValues[1])
            if (inner.isBlank()) "" else "**$inner**"
        }
        // Preserve list items and block breaks
        md = Regex("(?is)<li[^>]*>(.*?)</li>").replace(md) { match ->
            val item = cleanHtmlText(match.groupValues[1])
            if (item.isBlank()) "" else "\n- $item"
        }
        md = md
            .replace(Regex("(?is)<br\\s*/?>"), "\n")
            .replace(Regex("(?is)</(p|div|section|blockquote)>"), "\n\n")
            .replace(Regex("(?is)<[^>]+>"), " ")

        return decodeHtmlEntities(md)
            .lines()
            .map { it.replace(Regex("[ \\t]+"), " ").trimEnd() }
            .joinToString("\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    private fun decodeHtmlEntities(text: String): String {
        return text
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&nbsp;", " ")
    }

    private fun cleanHtmlText(html: String): String {
        return decodeHtmlEntities(html.replace(Regex("<[^>]+>"), " "))
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private data class ParsedPage(
        val url: String,
        val title: String,
        val description: String,
        val headings: List<String>,
        val bodyMarkdown: String,
        val sameOriginLinks: List<String>,
        val language: String = "en",
        val englishAlternateUrl: String? = null,
        val isNativeOnlyFallback: Boolean = false
    ) {
        fun toStandaloneMarkdown(): String = buildString {
            appendLine("# $title")
            appendLine("URL: $url")
            appendLine("Language: $language" + if (isNativeOnlyFallback) " (native-only fallback)" else "")
            if (description.isNotBlank()) {
                appendLine()
                appendLine("> $description")
            }
            appendLine()
            appendLine(bodyMarkdown)
        }

        fun toMarkdownSection(): String = buildString {
            appendLine("## $title")
            appendLine("URL: $url")
            appendLine("Language: $language" + if (isNativeOnlyFallback) " (native-only fallback)" else "")
            if (description.isNotBlank()) {
                appendLine("Meta Description: $description")
            }
            if (headings.isNotEmpty()) {
                appendLine("Headings:")
                headings.forEach { appendLine("- $it") }
            }
            appendLine()
            appendLine(bodyMarkdown)
        }
    }
}
