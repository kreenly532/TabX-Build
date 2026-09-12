package com.example.ui.components

import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.TabItem
import com.example.data.model.ViewLayoutMode
import com.example.engine.WebViewPool
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.SoulGoldSecondary

@Composable
fun MultiWebViewContainer(
    allTabs: List<TabItem>,
    activeTabId: String,
    gridTabIds: List<String>,
    layoutMode: ViewLayoutMode,
    isGlobalSync: Boolean,
    webViewPool: WebViewPool,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onSetSingleMode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabMap = remember(allTabs) { allTabs.associateBy { it.id } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (layoutMode) {
            ViewLayoutMode.SINGLE -> {
                val activeTab = tabMap[activeTabId] ?: allTabs.firstOrNull()
                if (activeTab != null) {
                    SingleWebViewPane(
                        tab = activeTab,
                        index = 1,
                        isActive = true,
                        showMiniHeader = false,
                        isCompact = false,
                        isGlobalSync = isGlobalSync,
                        webViewPool = webViewPool,
                        onSelect = { onSelectTab(activeTab.id) },
                        onClose = { onCloseTab(activeTab.id) },
                        onMaximize = { onSetSingleMode(activeTab.id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            ViewLayoutMode.SPLIT_VERTICAL -> {
                val tabsToShow = gridTabIds.mapNotNull { tabMap[it] }.take(2)
                Column(modifier = Modifier.fillMaxSize()) {
                    tabsToShow.forEachIndexed { index, tab ->
                        SingleWebViewPane(
                            tab = tab,
                            index = index + 1,
                            isActive = tab.id == activeTabId,
                            showMiniHeader = true,
                            isCompact = false,
                            isGlobalSync = isGlobalSync,
                            webViewPool = webViewPool,
                            onSelect = { onSelectTab(tab.id) },
                            onClose = { onCloseTab(tab.id) },
                            onMaximize = { onSetSingleMode(tab.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        if (index < tabsToShow.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(CyberCyanPrimary.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }

            ViewLayoutMode.SPLIT_HORIZONTAL -> {
                val tabsToShow = gridTabIds.mapNotNull { tabMap[it] }.take(2)
                Row(modifier = Modifier.fillMaxSize()) {
                    tabsToShow.forEachIndexed { index, tab ->
                        SingleWebViewPane(
                            tab = tab,
                            index = index + 1,
                            isActive = tab.id == activeTabId,
                            showMiniHeader = true,
                            isCompact = false,
                            isGlobalSync = isGlobalSync,
                            webViewPool = webViewPool,
                            onSelect = { onSelectTab(tab.id) },
                            onClose = { onCloseTab(tab.id) },
                            onMaximize = { onSetSingleMode(tab.id) },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        )
                        if (index < tabsToShow.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(2.dp)
                                    .background(CyberCyanPrimary.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }

            // High-density multi-account matrix grids: 4-Grid, 9-Grid, 16-Grid, 25-Grid, 36-Grid, All-Grid
            ViewLayoutMode.GRID_4,
            ViewLayoutMode.GRID_9,
            ViewLayoutMode.GRID_16,
            ViewLayoutMode.GRID_25,
            ViewLayoutMode.GRID_36,
            ViewLayoutMode.MATRIX_ALL -> {
                val maxCount = layoutMode.maxVisible
                val tabsToShow = gridTabIds.mapNotNull { tabMap[it] }.take(maxCount)
                val columns = layoutMode.columns
                val isDenseMatrix = columns >= 4

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val availableWidth = maxWidth
                    val availableHeight = maxHeight
                    val rows = (tabsToShow.size + columns - 1) / columns
                    
                    // Fixed-height tile or dynamic height so all rows fit neatly
                    val itemHeight = if (rows > 0 && rows <= 6) {
                        (availableHeight / rows).coerceAtLeast(100.dp)
                    } else {
                        160.dp
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        itemsIndexed(tabsToShow, key = { _, tab -> tab.id }) { index, tab ->
                            SingleWebViewPane(
                                tab = tab,
                                index = index + 1,
                                isActive = tab.id == activeTabId,
                                showMiniHeader = true,
                                isCompact = isDenseMatrix,
                                isGlobalSync = isGlobalSync,
                                webViewPool = webViewPool,
                                onSelect = { onSelectTab(tab.id) },
                                onClose = { onCloseTab(tab.id) },
                                onMaximize = { onSetSingleMode(tab.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleWebViewPane(
    tab: TabItem,
    index: Int,
    isActive: Boolean,
    showMiniHeader: Boolean,
    isCompact: Boolean,
    isGlobalSync: Boolean,
    webViewPool: WebViewPool,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    onMaximize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val webView = remember(tab.id) { webViewPool.getOrCreateWebView(tab) }
    var isMuted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(if (showMiniHeader) 4.dp else 0.dp))
            .border(
                width = if (showMiniHeader && isActive) 2.dp else if (showMiniHeader) 0.5.dp else 0.dp,
                color = if (isActive) CyberCyanPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(if (showMiniHeader) 4.dp else 0.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (showMiniHeader) {
            val headerHeight = if (isCompact) 20.dp else 28.dp
            val fontSize = if (isCompact) 9.sp else 11.sp

            Surface(
                color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .clickable { onSelect() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Badge index: #01, #02, ... #25
                        Text(
                            text = "#${String.format("%02d", index)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = fontSize,
                                fontWeight = FontWeight.Black
                            ),
                            color = if (isActive) SoulGoldSecondary else CyberCyanPrimary
                        )
                        Spacer(modifier = Modifier.width(3.dp))

                        val displayTitle = tab.accountTag ?: tab.title
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = fontSize,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isGlobalSync) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "同步中",
                                tint = AccentGreen,
                                modifier = Modifier.size(if (isCompact) 9.dp else 11.dp)
                            )
                        }

                        if (tab.isLoading) {
                            Spacer(modifier = Modifier.width(2.dp))
                            CircularProgressIndicator(
                                progress = { tab.progress / 100f },
                                modifier = Modifier.size(if (isCompact) 8.dp else 10.dp),
                                strokeWidth = 1.5.dp,
                                color = CyberCyanPrimary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        // In dense 25-grid mode, keep controls minimal
                        if (!isCompact) {
                            IconButton(
                                onClick = { webView.reload() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "刷新",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onMaximize,
                            modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "放大此号",
                                tint = if (isActive) CyberCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(if (isCompact) 11.dp else 13.dp)
                            )
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(if (isCompact) 10.dp else 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Native Android WebView
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .clickable { onSelect() }
        ) {
            AndroidView(
                factory = {
                    (webView.parent as? ViewGroup)?.removeView(webView)
                    webView
                },
                onRelease = { view ->
                    (view.parent as? ViewGroup)?.removeView(view)
                },
                onReset = { view ->
                    (view.parent as? ViewGroup)?.removeView(view)
                },
                update = { _ -> },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("webview_pane_${tab.id}")
            )
        }
    }
}
