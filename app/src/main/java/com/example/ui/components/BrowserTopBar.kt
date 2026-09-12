package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.data.model.ViewLayoutMode
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.SoulGoldSecondary
import com.example.ui.theme.SpiritPurpleTertiary

@Composable
fun BrowserTopBar(
    activeTab: TabItem?,
    layoutMode: ViewLayoutMode,
    isGlobalSync: Boolean,
    isAllMuted: Boolean,
    keepScreenOn: Boolean,
    isRecording: Boolean,
    isMacroRunning: Boolean,
    onNavigateUrl: (String) -> Unit,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit,
    onReload: () -> Unit,
    onLayoutModeChanged: (ViewLayoutMode) -> Unit,
    onToggleSync: () -> Unit,
    onToggleAllMute: () -> Unit,
    onLaunch25Accounts: () -> Unit,
    onLaunch36Accounts: () -> Unit,
    onBatchReloadAll: () -> Unit,
    onToggleKeepScreenOn: () -> Unit,
    onOpenAccounts: () -> Unit,
    onOpenScripts: () -> Unit,
    onOpenUa: () -> Unit,
    onOpenClean: () -> Unit,
    modifier: Modifier = Modifier
) {
    var urlInputText by remember { mutableStateOf(activeTab?.url ?: "") }
    var showLayoutMenu by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(activeTab?.url) {
        if (activeTab != null) {
            urlInputText = activeTab.url
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: URL Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation controls
                IconButton(
                    onClick = onGoBack,
                    enabled = activeTab?.canGoBack == true,
                    modifier = Modifier.size(36.dp).testTag("nav_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "后退",
                        tint = if (activeTab?.canGoBack == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onGoForward,
                    enabled = activeTab?.canGoForward == true,
                    modifier = Modifier.size(36.dp).testTag("nav_forward_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "前进",
                        tint = if (activeTab?.canGoForward == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onReload,
                    modifier = Modifier.size(36.dp).testTag("nav_reload_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "刷新",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Omnibox URL TextField
                OutlinedTextField(
                    value = urlInputText,
                    onValueChange = { urlInputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("url_omnibox_input"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    placeholder = {
                        Text(
                            "输入网址或搜索 (如 gm99.com)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "安全链接",
                            tint = if (urlInputText.startsWith("https")) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    trailingIcon = {
                        if (urlInputText.isNotEmpty()) {
                            IconButton(
                                onClick = { urlInputText = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "清除",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            focusManager.clearFocus()
                            onNavigateUrl(urlInputText)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        focusedBorderColor = CyberCyanPrimary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Layout Switcher Button
                Box {
                    IconButton(
                        onClick = { showLayoutMenu = true },
                        modifier = Modifier.size(36.dp).testTag("layout_mode_btn")
                    ) {
                        val icon = when (layoutMode) {
                            ViewLayoutMode.SINGLE -> Icons.Default.Dashboard
                            ViewLayoutMode.SPLIT_HORIZONTAL -> Icons.Default.ViewColumn
                            ViewLayoutMode.SPLIT_VERTICAL -> Icons.Default.ViewStream
                            ViewLayoutMode.GRID_4,
                            ViewLayoutMode.GRID_9,
                            ViewLayoutMode.GRID_16,
                            ViewLayoutMode.GRID_25,
                            ViewLayoutMode.GRID_36,
                            ViewLayoutMode.MATRIX_ALL -> Icons.Default.ViewCompact
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = "分屏多开模式",
                            tint = if (layoutMode.maxVisible >= 25) SoulGoldSecondary else CyberCyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showLayoutMenu,
                        onDismissRequest = { showLayoutMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("单页全屏 (1开)") },
                            leadingIcon = { Icon(Icons.Default.Dashboard, null) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.SINGLE)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("双屏上下 (2开)") },
                            leadingIcon = { Icon(Icons.Default.ViewStream, null) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.SPLIT_VERTICAL)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("双屏左右 (2开)") },
                            leadingIcon = { Icon(Icons.Default.ViewColumn, null) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.SPLIT_HORIZONTAL)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("四屏矩阵 (4开 2x2)") },
                            leadingIcon = { Icon(Icons.Default.ViewCompact, null) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.GRID_4)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("九屏矩阵 (9开 3x3)") },
                            leadingIcon = { Icon(Icons.Default.ViewCompact, null) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.GRID_9)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("十六屏矩阵 (16开 4x4)") },
                            leadingIcon = { Icon(Icons.Default.ViewCompact, null) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.GRID_16)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🔥 二十五屏千军同屏 (25开 5x5)", fontWeight = FontWeight.Bold, color = SoulGoldSecondary) },
                            leadingIcon = { Icon(Icons.Default.Gamepad, null, tint = SoulGoldSecondary) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.GRID_25)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("⚡ 三十六屏同屏阵列 (36开 6x6)", fontWeight = FontWeight.Bold, color = CyberCyanPrimary) },
                            leadingIcon = { Icon(Icons.Default.ViewCompact, null, tint = CyberCyanPrimary) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.GRID_36)
                                showLayoutMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🌐 百开矩阵监视墙 (全部同屏)") },
                            leadingIcon = { Icon(Icons.Default.Dashboard, null) },
                            onClick = {
                                onLayoutModeChanged(ViewLayoutMode.MATRIX_ALL)
                                showLayoutMenu = false
                            }
                        )
                    }
                }
            }

            // Progress bar
            if (activeTab?.isLoading == true) {
                LinearProgressIndicator(
                    progress = { activeTab.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp),
                    color = CyberCyanPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Row 2: Action Chips & Quick Toolbelt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 25-Accounts Quick Launcher
                ToolChip(
                    icon = Icons.Default.Gamepad,
                    label = "🚀 一键25开同屏",
                    highlightColor = SoulGoldSecondary,
                    isActive = layoutMode == ViewLayoutMode.GRID_25,
                    onClick = onLaunch25Accounts,
                    testTag = "launch_25_accounts_btn"
                )

                // 36-Accounts Quick Launcher
                ToolChip(
                    icon = Icons.Default.ViewCompact,
                    label = "⚡ 36开同屏",
                    highlightColor = CyberCyanPrimary,
                    isActive = layoutMode == ViewLayoutMode.GRID_36,
                    onClick = onLaunch36Accounts,
                    testTag = "launch_36_accounts_btn"
                )

                // GM99 Account Manager Dialog
                ToolChip(
                    icon = Icons.Default.Gamepad,
                    label = "斗罗号库",
                    highlightColor = SoulGoldSecondary,
                    isActive = true,
                    onClick = onOpenAccounts,
                    testTag = "open_gm99_accounts_btn"
                )

                // Sync Mode Toggle
                ToolChip(
                    icon = Icons.Default.Sync,
                    label = if (isGlobalSync) "同步群控: 开 (25+号同操)" else "同步群控: 关",
                    highlightColor = AccentGreen,
                    isActive = isGlobalSync,
                    onClick = onToggleSync,
                    testTag = "toggle_sync_btn"
                )

                // Batch Mute Toggle (avoids 25 simultaneous game audio)
                ToolChip(
                    icon = if (isAllMuted) Icons.Default.CleaningServices else Icons.Default.Gamepad,
                    label = if (isAllMuted) "🔇 全体静音: 开" else "🔊 全体静音: 关",
                    highlightColor = if (isAllMuted) AccentRed else CyberCyanPrimary,
                    isActive = isAllMuted,
                    onClick = onToggleAllMute,
                    testTag = "toggle_all_mute_btn"
                )

                // Batch Reload 25 tabs
                ToolChip(
                    icon = Icons.Default.Refresh,
                    label = "🔄 25号全量刷新",
                    highlightColor = CyberCyanPrimary,
                    isActive = false,
                    onClick = onBatchReloadAll,
                    testTag = "batch_reload_all_btn"
                )

                // Macro Script & Auto-AFK
                ToolChip(
                    icon = Icons.Default.SmartToy,
                    label = when {
                        isRecording -> "录制中 [REC]"
                        isMacroRunning -> "脚本挂机中..."
                        else -> "脚本挂机/录制"
                    },
                    highlightColor = if (isRecording) AccentRed else SpiritPurpleTertiary,
                    isActive = isRecording || isMacroRunning,
                    onClick = onOpenScripts,
                    testTag = "open_scripts_btn"
                )

                // Custom User-Agent
                ToolChip(
                    icon = Icons.Default.Devices,
                    label = "自定义UA",
                    highlightColor = CyberCyanPrimary,
                    isActive = !activeTab?.customUserAgent.isNullOrBlank(),
                    onClick = onOpenUa,
                    testTag = "open_ua_btn"
                )

                // One-Click Clean
                ToolChip(
                    icon = Icons.Default.DeleteSweep,
                    label = "深度清理缓存",
                    highlightColor = CyberCyanPrimary,
                    isActive = false,
                    onClick = onOpenClean,
                    testTag = "open_clean_btn"
                )

                // Screen Wake Lock Toggle
                ToolChip(
                    icon = Icons.Default.WbSunny,
                    label = if (keepScreenOn) "屏幕常亮: 开" else "屏幕常亮: 关",
                    highlightColor = SoulGoldSecondary,
                    isActive = keepScreenOn,
                    onClick = onToggleKeepScreenOn,
                    testTag = "toggle_screen_on_btn"
                )
            }
        }
    }
}

@Composable
private fun ToolChip(
    icon: ImageVector,
    label: String,
    highlightColor: Color,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isActive) highlightColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = 1.dp,
                color = if (isActive) highlightColor else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) highlightColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
