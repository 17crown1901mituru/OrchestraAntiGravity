package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.DeviceViewportProfile

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DeviceCaptureDialog(
    initialUrlOrDomain: String,
    profile: DeviceViewportProfile,
    onDismiss: () -> Unit,
    onCaptureBitmap: (Bitmap) -> Unit
) {
    val normalizedUrl = remember(initialUrlOrDomain) {
        val trimmed = initialUrlOrDomain.trim().ifBlank { "https://google.com" }
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
    }
    var currentUrl by remember(normalizedUrl) { mutableStateOf(normalizedUrl) }
    var loadProgress by remember { mutableIntStateOf(0) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PhoneAndroid,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "${profile.label} (${profile.widthDp}x${profile.heightDp}dp)",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = currentUrl,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = { webViewRef?.reload() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "再読み込み")
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "閉じる")
                    }
                }

                if (loadProgress in 1..99) {
                    LinearProgressIndicator(
                        progress = { loadProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                val ratio = (profile.widthDp.toFloat() / profile.heightDp.toFloat()).coerceIn(0.42f, 1.0f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth(if (ratio > 0.7f) 1f else 0.78f)
                            .aspectRatio(ratio)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.useWideViewPort = true
                                    settings.loadWithOverviewMode = true
                                    settings.userAgentString = profile.userAgent
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            if (!url.isNullOrBlank()) currentUrl = url
                                        }
                                    }
                                    webChromeClient = object : WebChromeClient() {
                                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                            loadProgress = newProgress
                                        }
                                    }
                                    loadUrl(normalizedUrl)
                                    webViewRef = this
                                }
                            },
                            update = { wv ->
                                if (wv.settings.userAgentString != profile.userAgent) {
                                    wv.settings.userAgentString = profile.userAgent
                                }
                                webViewRef = wv
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Button(
                    onClick = {
                        val wv = webViewRef
                        if (wv != null && wv.width > 0 && wv.height > 0) {
                            val bmp = Bitmap.createBitmap(wv.width, wv.height, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bmp)
                            wv.draw(canvas)
                            onCaptureBitmap(bmp)
                            onDismiss()
                        }
                    },
                    enabled = webViewRef != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_device_capture_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("この最新デバイス画面をキャプチャして添付")
                }
            }
        }
    }
}
