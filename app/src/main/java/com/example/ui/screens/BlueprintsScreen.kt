package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.model.AgentBlueprint
import com.example.model.StreamUiState

@Composable
fun BlueprintsScreen(
    blueprintsState: StreamUiState<List<AgentBlueprint>>,
    onCreateBlueprint: (title: String, category: String, summary: String, content: String) -> Unit,
    onUpdateStatus: (blueprintId: String, newStatus: String) -> Unit,
    onDeleteBlueprint: (blueprintId: String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var statusFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "DRAFT", "READY", "DEPLOYED", "ARCHIVED")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ブループリント保管庫 (Firestore)",
                style = MaterialTheme.typography.headlineSmall
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { f ->
                    FilterChip(
                        selected = statusFilter == f,
                        onClick = { statusFilter = f },
                        label = { Text(f) },
                        modifier = Modifier.testTag("blueprint_filter_$f")
                    )
                }
            }

            when (blueprintsState) {
                is StreamUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is StreamUiState.Error -> {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = blueprintsState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is StreamUiState.Success -> {
                    val items = remember(blueprintsState.data, statusFilter) {
                        if (statusFilter == "ALL") blueprintsState.data
                        else blueprintsState.data.filter { it.status == statusFilter }
                    }
                    if (items.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.BookmarkBorder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "保存済みのブループリントはありません",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "「AI」または「キット」タブから構成を保存するか、右下の＋ボタンから新規登録してください。",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(items, key = { it.id }) { bp ->
                                BlueprintCard(
                                    blueprint = bp,
                                    onCopy = {
                                        clipboard.setText(AnnotatedString(bp.content))
                                        onShowMessage("「${bp.title}」をコピーしました")
                                    },
                                    onMarkDeployed = {
                                        onUpdateStatus(bp.id, "DEPLOYED")
                                    },
                                    onArchive = {
                                        onUpdateStatus(bp.id, "ARCHIVED")
                                    },
                                    onDelete = {
                                        onDeleteBlueprint(bp.id)
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(72.dp)) }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_blueprint_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "新規ブループリント作成")
        }
    }

    if (showCreateDialog) {
        CreateBlueprintDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, category, summary, content ->
                onCreateBlueprint(title, category, summary, content)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun BlueprintCard(
    blueprint: AgentBlueprint,
    onCopy: () -> Unit,
    onMarkDeployed: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    val isArchived = blueprint.status == "ARCHIVED"
    val badgeColor = when (blueprint.status) {
        "DEPLOYED" -> MaterialTheme.colorScheme.secondary
        "READY" -> MaterialTheme.colorScheme.primary
        "ARCHIVED" -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.tertiary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("blueprint_card_${blueprint.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = badgeColor.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${blueprint.category} • ${blueprint.status}",
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Row {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = "コピー")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Rounded.DeleteOutline,
                            contentDescription = "削除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(text = blueprint.title, style = MaterialTheme.typography.titleLarge)
            if (blueprint.summary.isNotBlank()) {
                Text(
                    text = blueprint.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        text = blueprint.content.take(450) + if (blueprint.content.length > 450) "\n..." else "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (!isArchived) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (blueprint.status != "DEPLOYED") {
                        Button(
                            onClick = onMarkDeployed,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("デプロイ済にする")
                        }
                    }
                    OutlinedButton(
                        onClick = onArchive,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Archive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("アーカイブ")
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateBlueprintDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, category: String, summary: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("CLI") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val categories = listOf("CLI", "WORKER", "TERMUX", "GITHUB", "GCP")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新規ブループリント") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("タイトル") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("概要 (任意)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("コマンド・スクリプト内容") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(title, category, summary, content) },
                enabled = title.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_blueprint_button")
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}
