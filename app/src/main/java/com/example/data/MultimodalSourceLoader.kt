package com.example.data

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import com.example.model.AttachedSource
import com.example.model.SourceAttachmentKind
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.InlineDataPart
import com.google.firebase.ai.type.Part
import com.google.firebase.ai.type.TextPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale

/**
 * Loads user-selected Markdown/text, PDF, image, and video URIs through Android's
 * zero-permission ContentResolver (Photo Picker & Storage Access Framework) and packs
 * them into Firebase AI Logic multimodal Content within the 20 MB inline payload budget.
 */
class MultimodalSourceLoader(private val context: Context) {

    private val resolver: ContentResolver = context.contentResolver

    fun inspectUri(uri: Uri): AttachedSource? {
        var displayName = uri.lastPathSegment?.substringAfterLast('/')?.ifBlank { null } ?: "source"
        var sizeBytes = -1L

        if (uri.scheme == ContentResolver.SCHEME_FILE && !uri.path.isNullOrBlank()) {
            val file = File(uri.path!!)
            if (file.exists()) {
                displayName = file.name
                sizeBytes = file.length()
            }
        } else {
            runCatching {
                resolver.query(
                    uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                    null,
                    null,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIdx >= 0) {
                            cursor.getString(nameIdx)?.takeIf { it.isNotBlank() }?.let { displayName = it }
                        }
                        val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) {
                            sizeBytes = cursor.getLong(sizeIdx)
                        }
                    }
                }
            }
        }

        val resolvedMime = normalizeMimeType(
            rawMime = resolver.getType(uri).orEmpty(),
            fileName = displayName
        )
        val kind = classifyKind(resolvedMime, displayName) ?: return null

        return AttachedSource(
            uriString = uri.toString(),
            displayName = displayName,
            mimeType = resolvedMime,
            sizeBytes = sizeBytes.coerceAtLeast(0L),
            kind = kind
        )
    }

    suspend fun buildMultimodalContent(
        instructionPrompt: String,
        sources: List<AttachedSource>
    ): Result<Content> = withContext(Dispatchers.IO) {
        try {
            var inlineBytesTotal = 0L
            val markdownBlocks = mutableListOf<String>()
            val binaryParts = mutableListOf<InlineDataPart>()

            for (source in sources) {
                val uri = Uri.parse(source.uriString)
                when (source.kind) {
                    SourceAttachmentKind.MARKDOWN -> {
                        val text = readUtf8Limited(uri, MAX_TEXT_CHARS_PER_FILE)
                            ?: return@withContext Result.failure(
                                IllegalStateException("テキスト/Markdown を読み込めませんでした: ${source.displayName}")
                            )
                        markdownBlocks.add(
                            "--- Source (${source.displayName}, ${source.mimeType}) ---\n$text"
                        )
                    }

                    SourceAttachmentKind.IMAGE -> {
                        val bytes = readCompressedImageOrRaw(uri, source.mimeType)
                            ?: return@withContext Result.failure(
                                IllegalStateException("画像を読み込めませんでした: ${source.displayName}")
                            )
                        inlineBytesTotal += bytes.first.size
                        if (inlineBytesTotal > MAX_TOTAL_INLINE_BYTES) {
                            return@withContext Result.failure(
                                IllegalArgumentException("添付メディアの合計サイズが 20MB 上限を超えました。")
                            )
                        }
                        binaryParts.add(InlineDataPart(bytes.first, bytes.second, source.displayName))
                    }

                    SourceAttachmentKind.PDF,
                    SourceAttachmentKind.VIDEO -> {
                        if (source.sizeBytes > MAX_TOTAL_INLINE_BYTES) {
                            return@withContext Result.failure(
                                IllegalArgumentException("${source.displayName} が 20MB を超えています。20MB 未満のファイルを選択してください。")
                            )
                        }
                        val bytes = readBinaryLimited(uri, MAX_TOTAL_INLINE_BYTES - inlineBytesTotal)
                            ?: return@withContext Result.failure(
                                IllegalArgumentException("${source.displayName} が読み込めないか、合計 20MB 上限を超えました。")
                            )
                        inlineBytesTotal += bytes.size
                        val mime = if (source.kind == SourceAttachmentKind.PDF) {
                            "application/pdf"
                        } else {
                            source.mimeType.ifBlank { "video/mp4" }
                        }
                        binaryParts.add(InlineDataPart(bytes, mime, source.displayName))
                    }
                }
            }

            val allParts = buildList<Part> {
                if (markdownBlocks.isNotEmpty()) {
                    add(TextPart(markdownBlocks.joinToString("\n\n")))
                }
                addAll(binaryParts)
                add(TextPart(instructionPrompt))
            }
            Result.success(Content(role = "user", parts = allParts))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun extractTextContextForGateway(
        sources: List<AttachedSource>
    ): Pair<String, List<String>> = withContext(Dispatchers.IO) {
        val textParts = mutableListOf<String>()
        val skippedBinaries = mutableListOf<String>()
        for (source in sources) {
            if (source.kind == SourceAttachmentKind.MARKDOWN) {
                val uri = Uri.parse(source.uriString)
                val body = readUtf8Limited(uri, 24_000)
                if (!body.isNullOrBlank()) {
                    textParts.add("--- ${source.displayName} ---\n$body")
                }
            } else {
                skippedBinaries.add(source.displayName)
            }
        }
        textParts.joinToString("\n\n") to skippedBinaries
    }

    private fun readUtf8Limited(uri: Uri, maxChars: Int): String? = runCatching {
        resolver.openInputStream(uri)?.use { stream ->
            val raw = stream.bufferedReader(Charsets.UTF_8).readText()
            if (raw.length > maxChars) {
                raw.take(maxChars) + "\n...[truncated at $maxChars chars]"
            } else {
                raw
            }
        }
    }.getOrNull()

    private fun readBinaryLimited(uri: Uri, remainingBudget: Long): ByteArray? {
        if (remainingBudget <= 0L) return null
        return runCatching {
            resolver.openInputStream(uri)?.use { input ->
                val out = ByteArrayOutputStream()
                val buf = ByteArray(16 * 1024)
                var total = 0L
                while (true) {
                    val read = input.read(buf)
                    if (read <= 0) break
                    total += read
                    if (total > remainingBudget) return@use null
                    out.write(buf, 0, read)
                }
                out.toByteArray()
            }
        }.getOrNull()
    }

    private fun readCompressedImageOrRaw(uri: Uri, mimeType: String): Pair<ByteArray, String>? {
        val raw = readBinaryLimited(uri, MAX_RAW_IMAGE_BEFORE_SCALE)
        if (raw != null && raw.size <= 1_500_000 && mimeType.startsWith("image/")) {
            return raw to mimeType
        }
        return runCatching {
            val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, boundsOpts) }
            val maxDim = maxOf(boundsOpts.outWidth, boundsOpts.outHeight)
            var sample = 1
            while (maxDim / sample > 1600) {
                sample *= 2
            }
            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp = resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOpts)
            } ?: return@runCatching null
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 88, baos)
            bmp.recycle()
            baos.toByteArray() to "image/jpeg"
        }.getOrNull() ?: raw?.let { it to (mimeType.ifBlank { "image/jpeg" }) }
    }

    private fun normalizeMimeType(rawMime: String, fileName: String): String {
        val lowerName = fileName.lowercase(Locale.US)
        if (rawMime.isNotBlank() && rawMime != "application/octet-stream") {
            return rawMime
        }
        return when {
            lowerName.endsWith(".md") || lowerName.endsWith(".markdown") -> "text/markdown"
            lowerName.endsWith(".txt") || lowerName.endsWith(".log") -> "text/plain"
            lowerName.endsWith(".json") -> "application/json"
            lowerName.endsWith(".yaml") || lowerName.endsWith(".yml") -> "text/yaml"
            lowerName.endsWith(".pdf") -> "application/pdf"
            lowerName.endsWith(".png") -> "image/png"
            lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") -> "image/jpeg"
            lowerName.endsWith(".webp") -> "image/webp"
            lowerName.endsWith(".mp4") -> "video/mp4"
            lowerName.endsWith(".webm") -> "video/webm"
            lowerName.endsWith(".mov") || lowerName.endsWith(".mkv") -> "video/mp4"
            else -> "text/plain"
        }
    }

    private fun classifyKind(mime: String, fileName: String): SourceAttachmentKind? {
        val lowerMime = mime.lowercase(Locale.US)
        val lowerName = fileName.lowercase(Locale.US)
        return when {
            lowerMime == "application/pdf" || lowerName.endsWith(".pdf") -> SourceAttachmentKind.PDF
            lowerMime.startsWith("image/") -> SourceAttachmentKind.IMAGE
            lowerMime.startsWith("video/") -> SourceAttachmentKind.VIDEO
            lowerMime.startsWith("text/") ||
                lowerMime == "application/json" ||
                lowerMime.contains("markdown") ||
                lowerName.endsWith(".md") ||
                lowerName.endsWith(".markdown") ||
                lowerName.endsWith(".txt") ||
                lowerName.endsWith(".json") ||
                lowerName.endsWith(".yaml") ||
                lowerName.endsWith(".yml") -> SourceAttachmentKind.MARKDOWN
            else -> null
        }
    }

    companion object {
        const val MAX_TOTAL_INLINE_BYTES: Long = 19L * 1024L * 1024L
        private const val MAX_RAW_IMAGE_BEFORE_SCALE: Long = 16L * 1024L * 1024L
        private const val MAX_TEXT_CHARS_PER_FILE = 100_000
    }
}
