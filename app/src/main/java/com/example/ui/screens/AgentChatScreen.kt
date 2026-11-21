package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AiGenerationState
import com.example.model.AttachedSource
import com.example.model.CrawlMode
import com.example.model.DeviceViewportProfile
import com.example.model.InferenceTarget
import com.example.model.SourceAttachmentKind
import com.example.model.WebCrawlPreview
import com.example.ui.components.DeviceCaptureDialog
import java.util.Locale

@Composable
fun AgentChatScreen(
    aiState: AiGenerationState,
    inferenceTarget: InferenceTarget,
    gc2223Endpoint: String,
    attachedSources: List<AttachedSource>,
    isCrawlingWeb: Boolean,
    lastCrawlPreview: WebCrawlPreview?,
    onCrawlUrlOrDomain: (String, CrawlMode, DeviceViewportProfile, Boolean, Int) -> Unit,
    onAttachDeviceScreenshot: (Bitmap, String, DeviceViewportProfile) -> Unit,
    onAddSourceUris: (List<Uri>) -> Unit,
    onRemoveSource: (String) -> Unit,
    onSelectTarget: (InferenceTarget) -> Unit,
    onSendPrompt: (String, Boolean) -> Unit,
    onSavePlanAsBlueprint: (title: String, category: String, summary: String, content: String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var promptText by rememberSaveable { mutableStateOf("") }
    var structuredMode by rememberSaveable { mutableStateOf(true) }
    var urlInput by rememberSaveable { mutableStateOf("") }
    var crawlMode by rememberSaveable { mutableStateOf(CrawlMode.DOMAIN_CRAWL) }
    var deviceProfile by rememberSaveable { mutableStateOf(DeviceViewportProfile.PIXEL_9_PRO) }
    var preferEnglish by rememberSaveable { mutableStateOf(true) }
    var recursiveMaxPages by rememberSaveable { mutableStateOf(20) }
    var showDeviceCaptureDialog by rememberSaveable { mutableStateOf(false) }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAddSourceUris(uris)
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAddSourceUris(uris)
        }
    }

    val quickPrompts = remember {
        listOf(
            "添付ソースから最適設計を組む" to "添付した Markdown / PDF / 画像 / 動画の仕様や画面構成を統合理解し、運用コストゼロを目指した具体的なアーキテクチャと実装手順・CLI スクリプトを組み立てて",
            "gc2223.com に Workers AI + Gemini ルーターを組む" to "gc2223.com の Cloudflare Worker で Workers AI 無料枠を優先し、複雑タスクだけ GCP/Gemini へ逃がすルーティング設計と wrangler コマンドを作って",
            "Termux AntiGravityAgent 拡張" to "Termux 上で antigravity コマンドを実行したとき、GitHub Actions と gc2223.com を安全に連携させる CLI フックを設計して",
            "GCP Always Free + 予算$1ガード" to "クレジットカード登録後の GCP で課金事故をゼロにする予算アラート・Cloud Run 最小設定・Workload Identity 連携手順をまとめて",
            "Cloud Docs 最適化監査" to "取得した https://cloud.google.com/docs 配下の .md ドキュメントと現在の Terraform/Worker 構成を Google Cloud Well-Architected 6本の柱、OWASP Top 10:2025、Cloud Quotas アラート、TLS 1.2+ 制約、gcloud beta terraform vet で監査し改善点を実行可能なコマンド付きで列挙して",
            "Google Dev Program 特典活用" to "Google Developer Program の特典や学習リソースを GCP / Firebase 構成のコスト最適化にどう組み合わせるか提案して"
        )
    }

    val totalAttachedBytes = remember(attachedSources) {
        attachedSources.sumOf { it.sizeBytes }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "AI オーケストレーター",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inference_target_selector")
            ) {
                SegmentedButton(
                    selected = inferenceTarget == InferenceTarget.FIREBASE_GEMINI,
                    onClick = { onSelectTarget(InferenceTarget.FIREBASE_GEMINI) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
                ) {
                    Text("Firebase Gemini", maxLines = 1)
                }
                SegmentedButton(
                    selected = inferenceTarget == InferenceTarget.GC2223_GATEWAY,
                    onClick = { onSelectTarget(InferenceTarget.GC2223_GATEWAY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = { Icon(Icons.Rounded.Cloud, contentDescription = null, modifier = Modifier.size(16.dp)) }
                ) {
                    Text("gc2223.com GW", maxLines = 1)
                }
            }

            if (inferenceTarget == InferenceTarget.FIREBASE_GEMINI) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = structuredMode,
                        onClick = { structuredMode = true },
                        label = { Text("構造化プラン (JSON)") },
                        modifier = Modifier.testTag("mode_structured_chip")
                    )
                    FilterChip(
                        selected = !structuredMode,
                        onClick = { structuredMode = false },
                        label = { Text("ストリーミング相談") },
                        modifier = Modifier.testTag("mode_stream_chip")
                    )
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Terminal,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "POST $gc2223Endpoint/v1/chat へ送信（PDF/画像/動画解析時は Firebase Gemini を選択）",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // URL / Domain Crawl & Latest Device Screen Capture Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "URL・ドメインクロール & 最新デバイス画面取得",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("URL または ドメイン (例: cloud.google.com/docs)") },
                        placeholder = { Text("https://cloud.google.com/docs / https://gc2223.com") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("url_crawl_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CrawlMode.entries.forEach { mode ->
                            FilterChip(
                                selected = crawlMode == mode,
                                onClick = { crawlMode = mode },
                                label = { Text(mode.label) },
                                modifier = Modifier.testTag("crawl_mode_${mode.name}")
                            )
                        }
                        DeviceViewportProfile.entries.forEach { profile ->
                            FilterChip(
                                selected = deviceProfile == profile,
                                onClick = { deviceProfile = profile },
                                label = { Text(profile.label) },
                                modifier = Modifier.testTag("device_profile_${profile.name}")
                            )
                        }
                    }

                    if (crawlMode == CrawlMode.PATH_RECURSIVE) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = preferEnglish,
                                onClick = { preferEnglish = !preferEnglish },
                                label = { Text(if (preferEnglish) "英語基本+固有言語" else "元言語のまま") },
                                modifier = Modifier.testTag("crawl_prefer_english_chip")
                            )
                            listOf(20, 35, 50).forEach { limit ->
                                FilterChip(
                                    selected = recursiveMaxPages == limit,
                                    onClick = { recursiveMaxPages = limit },
                                    label = { Text("最大${limit}p") },
                                    modifier = Modifier.testTag("crawl_max_pages_$limit")
                                )
                            }
                        }
                        Text(
                            text = "指定 URL の PATH 配下を英語基本（固有言語のみの場合は自動フォールバック）で再帰巡回し、構造化 .md と統合インデックスを生成します",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.testTag("path_recursive_hint")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val limit = if (crawlMode == CrawlMode.PATH_RECURSIVE) recursiveMaxPages else 4
                                onCrawlUrlOrDomain(urlInput, crawlMode, deviceProfile, preferEnglish, limit)
                            },
                            enabled = urlInput.isNotBlank() && !isCrawlingWeb,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("crawl_url_button")
                        ) {
                            if (isCrawlingWeb) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("取得中...")
                            } else {
                                Icon(Icons.Rounded.Language, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("クロール添付", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        OutlinedButton(
                            onClick = { showDeviceCaptureDialog = true },
                            enabled = urlInput.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("capture_device_screen_button")
                        ) {
                            Icon(Icons.Rounded.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("デバイス画面", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    if (lastCrawlPreview != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${lastCrawlPreview.targetUrl} (${lastCrawlPreview.pageCount}p • ${lastCrawlPreview.deviceProfileLabel})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (lastCrawlPreview.savedSessionDirName.isNotBlank()) {
                                    Text(
                                        text = "保存先: web_captures/${lastCrawlPreview.savedSessionDirName}/ (${lastCrawlPreview.attachedFileName})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (lastCrawlPreview.languageSummary.isNotBlank()) {
                                    Text(
                                        text = "取得言語: ${lastCrawlPreview.languageSummary}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.testTag("crawl_language_summary")
                                    )
                                }
                                if (lastCrawlPreview.summarySnippet.isNotBlank()) {
                                    Text(
                                        text = lastCrawlPreview.summarySnippet,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Multimodal Source Attachment Section (Markdown, PDF, Image, Video)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "関連ソース添付 (Markdown / PDF / 画像 / 動画)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (attachedSources.isNotEmpty()) {
                            Text(
                                text = "${attachedSources.size}件 • ${formatBytes(totalAttachedBytes)} / 20MB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                documentPickerLauncher.launch(
                                    arrayOf(
                                        "application/pdf",
                                        "text/markdown",
                                        "text/plain",
                                        "application/json",
                                        "text/*"
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("attach_docs_button")
                        ) {
                            Icon(Icons.Rounded.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("MD / PDF 添付", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        OutlinedButton(
                            onClick = {
                                mediaPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("attach_media_button")
                        ) {
                            Icon(Icons.Rounded.PermMedia, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("画像 / 動画 添付", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    if (attachedSources.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            attachedSources.forEach { src ->
                                val icon = when (src.kind) {
                                    SourceAttachmentKind.MARKDOWN -> Icons.Rounded.Description
                                    SourceAttachmentKind.PDF -> Icons.Rounded.PictureAsPdf
                                    SourceAttachmentKind.IMAGE -> Icons.Rounded.Image
                                    SourceAttachmentKind.VIDEO -> Icons.Rounded.VideoFile
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 10.dp, end = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val thumbnail = remember(src.uriString, src.kind) {
                                            if (src.kind == SourceAttachmentKind.IMAGE) {
                                                runCatching {
                                                    context.contentResolver.openInputStream(Uri.parse(src.uriString))?.use { stream ->
                                                        val opts = BitmapFactory.Options().apply { inSampleSize = 4 }
                                                        BitmapFactory.decodeStream(stream, null, opts)?.asImageBitmap()
                                                    }
                                                }.getOrNull()
                                            } else null
                                        }
                                        if (thumbnail != null) {
                                            Image(
                                                bitmap = thumbnail,
                                                contentDescription = src.displayName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            )
                                        } else {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = src.kind.name,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = src.displayName,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${src.kind.name} • ${formatBytes(src.sizeBytes)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemoveSource(src.uriString) },
                                            modifier = Modifier.testTag("remove_source_${src.displayName}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = "${src.displayName} を削除",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPrompts.forEachIndexed { index, (label, fullPrompt) ->
                    AssistChip(
                        onClick = { promptText = fullPrompt },
                        label = { Text(label) },
                        modifier = Modifier.testTag("quick_prompt_chip_$index")
                    )
                }
            }

            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("構築・自動化したいワークフローを入力") },
                placeholder = { Text("例: 添付した仕様書と画面スクショをもとに構築プランを作成して") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("agent_prompt_input"),
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = { onSendPrompt(promptText, structuredMode) },
                enabled = (promptText.isNotBlank() || attachedSources.isNotEmpty()) && aiState !is AiGenerationState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("send_prompt_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (aiState is AiGenerationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("生成中...")
                } else {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (inferenceTarget == InferenceTarget.FIREBASE_GEMINI) {
                            if (attachedSources.isNotEmpty()) {
                                "Gemini でソース解析して生成 (${attachedSources.size}件)"
                            } else {
                                "Gemini 3.8 Flash で生成"
                            }
                        } else {
                            "gc2223.com ゲートウェイで実行"
                        }
                    )
                }
            }

            when (aiState) {
                is AiGenerationState.Idle -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "運用コストゼロ設計モード待機中",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "上のプリセットを選ぶかプロンプトを入力すると、Firebase AI Logic またはデプロイ済みの gc2223.com Worker から応答を取得し、そのまま Firestore にブループリント保存できます。",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is AiGenerationState.Loading -> Unit

                is AiGenerationState.Streaming -> {
                    ResponseCard(
                        title = "ストリーミング応答 (Gemini 3.8 Flash)",
                        body = aiState.text,
                        onCopy = {
                            clipboard.setText(AnnotatedString(aiState.text))
                            onShowMessage("応答をコピーしました")
                        },
                        onSave = null
                    )
                }

                is AiGenerationState.Success -> {
                    val plan = aiState.structuredPlan
                    if (plan != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = plan.planTitle,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${plan.targetLayer} • ${plan.estimatedMonthlyCost}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                plan.steps.forEachIndexed { idx, step ->
                                    Text(
                                        text = "${idx + 1}. $step",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    SelectionContainer {
                                        Text(
                                            text = plan.cliOrConfigSnippet,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(plan.cliOrConfigSnippet))
                                            onShowMessage("スニペットをコピーしました")
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("copy_plan_snippet_button")
                                    ) {
                                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("コピー")
                                    }
                                    Button(
                                        onClick = {
                                            onSavePlanAsBlueprint(
                                                plan.planTitle,
                                                plan.targetLayer,
                                                "月額目安: ${plan.estimatedMonthlyCost} / ${plan.steps.firstOrNull().orEmpty()}",
                                                aiState.text
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("save_plan_blueprint_button")
                                    ) {
                                        Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("保存")
                                    }
                                }
                            }
                        }
                    } else {
                        ResponseCard(
                            title = if (inferenceTarget == InferenceTarget.FIREBASE_GEMINI) {
                                "Gemini 3.8 Flash 応答"
                            } else {
                                "gc2223.com 応答"
                            },
                            body = aiState.text,
                            onCopy = {
                                clipboard.setText(AnnotatedString(aiState.text))
                                onShowMessage("応答をコピーしました")
                            },
                            onSave = {
                                onSavePlanAsBlueprint(
                                    "AI 出力メモ",
                                    "CLI",
                                    promptText.take(120),
                                    aiState.text
                                )
                            }
                        )
                    }
                }

                is AiGenerationState.Error -> {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = aiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDeviceCaptureDialog) {
        DeviceCaptureDialog(
            initialUrlOrDomain = urlInput,
            profile = deviceProfile,
            onDismiss = { showDeviceCaptureDialog = false },
            onCaptureBitmap = { bmp ->
                onAttachDeviceScreenshot(bmp, urlInput, deviceProfile)
            }
        )
    }
}

@Composable
private fun ResponseCard(
    title: String,
    body: String,
    onCopy: () -> Unit,
    onSave: (() -> Unit)?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Row {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.testTag("copy_response_button")
                    ) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = "応答をコピー")
                    }
                    if (onSave != null) {
                        IconButton(
                            onClick = onSave,
                            modifier = Modifier.testTag("save_response_button")
                        ) {
                            Icon(Icons.Rounded.BookmarkAdd, contentDescription = "ブループリント保存")
                        }
                    }
                }
            }
            SelectionContainer {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 KB"
    val kb = bytes / 1024.0
    if (kb < 1024.0) {
        return String.format(Locale.US, "%.1f KB", kb)
    }
    val mb = kb / 1024.0
    return String.format(Locale.US, "%.1f MB", mb)
}
