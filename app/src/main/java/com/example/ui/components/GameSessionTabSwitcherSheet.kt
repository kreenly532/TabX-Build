package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.data.model.ViewLayoutMode
import com.example.engine.WebViewPool
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.SoulGoldSecondary
import com.example.ui.theme.SpiritPurpleTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSessionTabSwitcherSheet(
    tabs: List<TabItem>,
    activeTabId: String,
    layoutMode: ViewLayoutMode,
    isGlobalSync: Boolean,
    isAllMuted: Boolean,
    isMacroRunning: Boolean,
    webViewPool: WebViewPool,
    onDismiss: () -> Unit,
    onSelectSession: (String) -> Unit,
    onCloseSession: (String) -> Unit,
    onMaximizeSession: (String) -> Unit,
    onReloadSession: (String) -> Unit,
    onToggleSessionMute: (String) -> Unit,
    onAddNewSession: () -> Unit,
    onLaunch25Matrix: () -> Unit,
    onBatchReloadAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyMaster by remember { mutableStateOf(false) }

    // Trigger fresh thumbnails when sheet is opened
    LaunchedEffect(Unit) {
        tabs.forEach { tab ->
            webViewPool.captureThumbnail(tab.id) { _ -> }
        }
    }

    val filteredTabs = remember(tabs, searchQuery, filterOnlyMaster) {
        tabs.filter { tab ->
            val matchSearch = searchQuery.isBlank() ||
                tab.title.contains(searchQuery, ignoreCase = true) ||
                (tab.accountTag?.contains(searchQuery, ignoreCase = true) == true) ||
                tab.url.contains(searchQuery, ignoreCase = true)
            val matchMaster = !filterOnlyMaster || tab.isSyncMaster
            matchSearch && matchMaster
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 12.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // Header: Title, Live Stats & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gamepad,
                            contentDescription = null,
                            tint = SoulGoldSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "多开会话总览 (${tabs.size} 开)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "GM99 斗罗大陆H5 独立容器 • 动作同步群控 • 实时快照",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Quick + Add Button
                    Button(
                        onClick = {
                            onAddNewSession()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp).testTag("switcher_add_tab_btn")
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("加号", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // 25-Accounts Quick Matrix Button
                    Button(
                        onClick = {
                            onLaunch25Matrix()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoulGoldSecondary,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp).testTag("switcher_25_matrix_btn")
                    ) {
                        Icon(Icons.Default.GridView, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("25开同屏", fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar & Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("搜索角色名、区服、标签...", fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp), tint = CyberCyanPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Clear, "清除", modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = CyberCyanPrimary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                )

                // Quick Refresh All
                IconButton(
                    onClick = onBatchReloadAll,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "全部刷新",
                        tint = CyberCyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live Status Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AccentGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "运行中: ${tabs.count { !it.isLoading }} | 加载中: ${tabs.count { it.isLoading }}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = if (isGlobalSync) "⚡ 同步群控激活中" else "独立单操模式",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isGlobalSync) AccentGreen else CyberCyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid of Game Session Cards
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(filteredTabs, key = { _, tab -> tab.id }) { index, tab ->
                    val isActive = tab.id == activeTabId
                    val cachedThumbnail = webViewPool.getCachedThumbnail(tab.id)

                    GameSessionCard(
                        tab = tab,
                        index = index + 1,
                        isActive = isActive,
                        isGlobalSync = isGlobalSync,
                        isMacroRunning = isMacroRunning,
                        thumbnail = cachedThumbnail,
                        onSelect = {
                            onSelectSession(tab.id)
                            onDismiss()
                        },
                        onMaximize = {
                            onMaximizeSession(tab.id)
                            onDismiss()
                        },
                        onReload = { onReloadSession(tab.id) },
                        onToggleMute = { onToggleSessionMute(tab.id) },
                        onClose = { onCloseSession(tab.id) },
                        canClose = tabs.size > 1
                    )
                }
            }
        }
    }
}

@Composable
private fun GameSessionCard(
    tab: TabItem,
    index: Int,
    isActive: Boolean,
    isGlobalSync: Boolean,
    isMacroRunning: Boolean,
    thumbnail: Bitmap?,
    onSelect: () -> Unit,
    onMaximize: () -> Unit,
    onReload: () -> Unit,
    onToggleMute: () -> Unit,
    onClose: () -> Unit,
    canClose: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) SoulGoldSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "sessionCardBorder"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(if (isActive) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isActive) 6.dp else 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .testTag("game_session_card_${tab.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Section 1: Thumbnail Viewport Preview with Overlaid Status Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .background(Color(0xFF0F172A))
            ) {
                if (thumbnail != null && !thumbnail.isRecycled) {
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = "游戏预览截图",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Stylized Fallback Radar / Viewport Art
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF1E293B),
                                        Color(0xFF0F172A)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = null,
                                tint = if (isActive) SoulGoldSecondary else CyberCyanPrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "GM99 实时会话",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Top-Left Overlays: Status Badge (Live / Loading / Macro / Sync)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Index Badge (#01, #02...)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "#${String.format("%02d", index)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            ),
                            color = if (isActive) SoulGoldSecondary else CyberCyanPrimary
                        )
                    }

                    // Live Status Dot
                    StatusIndicatorBadge(
                        isLoading = tab.isLoading,
                        progress = tab.progress,
                        isSyncMaster = tab.isSyncMaster,
                        isGlobalSync = isGlobalSync,
                        isMacroRunning = isMacroRunning
                    )
                }

                // Top-Right Overlays: Sound status and Close Button
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (tab.isMuted) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeMute,
                                contentDescription = "静音中",
                                tint = AccentRed,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    if (canClose) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.7f))
                                .clickable { onClose() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭会话",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Bottom Overlay: Account Tag pill
                val tagText = tab.accountTag ?: "斗罗大陆H5"
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tagText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Section 2: Card Footer Info & Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = tab.url.removePrefix("https://").removePrefix("http://"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Action Bar: Focus / Maximize / Reload / Mute
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onReload,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "刷新",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = if (tab.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "单号静音",
                                tint = if (tab.isMuted) AccentRed else CyberCyanPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        IconButton(
                            onClick = onMaximize,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "单号放大全屏",
                                tint = SoulGoldSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Focus Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isActive) SoulGoldSecondary else CyberCyanPrimary.copy(alpha = 0.18f)
                            )
                            .clickable { onSelect() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isActive) "当前操控" else "切换聚焦",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isActive) Color.Black else CyberCyanPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusIndicatorBadge(
    isLoading: Boolean,
    progress: Int,
    isSyncMaster: Boolean,
    isGlobalSync: Boolean,
    isMacroRunning: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    isLoading -> SoulGoldSecondary.copy(alpha = 0.85f)
                    isMacroRunning -> SpiritPurpleTertiary.copy(alpha = 0.85f)
                    isGlobalSync -> AccentGreen.copy(alpha = 0.85f)
                    else -> Color.Black.copy(alpha = 0.75f)
                }
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    progress = { progress / 100f },
                    strokeWidth = 1.5.dp,
                    color = Color.Black,
                    modifier = Modifier.size(8.dp)
                )
                Text(
                    text = "加载中",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
            } else if (isMacroRunning) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(9.dp)
                )
                Text(
                    text = "挂机中",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            } else if (isGlobalSync) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(9.dp)
                )
                Text(
                    text = if (isSyncMaster) "主控" else "从号",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )
                Text(
                    text = "在线",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}
