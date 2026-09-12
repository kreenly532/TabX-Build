package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

/**
 * High-performance Bottom Navigation Bar for TabX Game Sessions
 * Follows Material Design 3 and Android Safe Inset Guidelines (navigationBarsPadding)
 */
@Composable
fun GameSessionBottomBar(
    tabs: List<TabItem>,
    activeTab: TabItem?,
    layoutMode: ViewLayoutMode,
    isGlobalSync: Boolean,
    isAllMuted: Boolean,
    isMacroRunning: Boolean,
    onOpenSessionSwitcher: () -> Unit,
    onLayoutModeChanged: (ViewLayoutMode) -> Unit,
    onToggleSync: () -> Unit,
    onToggleAllMute: () -> Unit,
    onAddNewSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Ensures no overlap with Android bottom navigation gestures
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Subtle top highlight border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CyberCyanPrimary.copy(alpha = 0.6f),
                                SoulGoldSecondary.copy(alpha = 0.6f),
                                SpiritPurpleTertiary.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Section 1: Active Session Switcher Pill / Trigger
                ActiveSessionPill(
                    tabsCount = tabs.size,
                    activeTab = activeTab,
                    isGlobalSync = isGlobalSync,
                    isMacroRunning = isMacroRunning,
                    pulseScale = pulseScale,
                    onClick = onOpenSessionSwitcher,
                    modifier = Modifier.weight(1.3f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Section 2: Quick Multi-Open Layout Matrix Selectors
                Row(
                    modifier = Modifier
                        .weight(1.8f)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LayoutModePill(
                        label = "1开",
                        isSelected = layoutMode == ViewLayoutMode.SINGLE,
                        icon = Icons.Default.Dashboard,
                        onClick = { onLayoutModeChanged(ViewLayoutMode.SINGLE) },
                        testTag = "bottom_nav_mode_1"
                    )
                    LayoutModePill(
                        label = "4开",
                        isSelected = layoutMode == ViewLayoutMode.GRID_4,
                        icon = Icons.Default.ViewCompact,
                        onClick = { onLayoutModeChanged(ViewLayoutMode.GRID_4) },
                        testTag = "bottom_nav_mode_4"
                    )
                    LayoutModePill(
                        label = "9开",
                        isSelected = layoutMode == ViewLayoutMode.GRID_9,
                        icon = Icons.Default.GridView,
                        onClick = { onLayoutModeChanged(ViewLayoutMode.GRID_9) },
                        testTag = "bottom_nav_mode_9"
                    )
                    LayoutModePill(
                        label = "🔥25开",
                        isSelected = layoutMode == ViewLayoutMode.GRID_25,
                        icon = Icons.Default.Gamepad,
                        accentColor = SoulGoldSecondary,
                        onClick = { onLayoutModeChanged(ViewLayoutMode.GRID_25) },
                        testTag = "bottom_nav_mode_25"
                    )
                    LayoutModePill(
                        label = "36开",
                        isSelected = layoutMode == ViewLayoutMode.GRID_36,
                        icon = Icons.Default.ViewCompact,
                        accentColor = CyberCyanPrimary,
                        onClick = { onLayoutModeChanged(ViewLayoutMode.GRID_36) },
                        testTag = "bottom_nav_mode_36"
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Section 3: Quick Tool Actions (Sync, Mute, Add, Switcher Drawer)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Sync Group Control Toggle
                    IconButton(
                        onClick = onToggleSync,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isGlobalSync) AccentGreen.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isGlobalSync) AccentGreen else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("bottom_bar_sync_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "同步群控",
                            tint = if (isGlobalSync) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // All Mute Toggle
                    IconButton(
                        onClick = onToggleAllMute,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isAllMuted) AccentRed.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isAllMuted) AccentRed else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("bottom_bar_mute_btn")
                    ) {
                        Icon(
                            imageVector = if (isAllMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "全体静音",
                            tint = if (isAllMuted) AccentRed else CyberCyanPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // New Session (+)
                    IconButton(
                        onClick = onAddNewSession,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .testTag("bottom_bar_add_tab_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "新建游戏会话",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Visual Tab Switcher / Gallery Trigger
                    IconButton(
                        onClick = onOpenSessionSwitcher,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SoulGoldSecondary.copy(alpha = 0.18f))
                            .border(1.dp, SoulGoldSecondary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .testTag("bottom_bar_tab_switcher_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = SoulGoldSecondary,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = "${tabs.size}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "多开会话管理器",
                                tint = SoulGoldSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveSessionPill(
    tabsCount: Int,
    activeTab: TabItem?,
    isGlobalSync: Boolean,
    isMacroRunning: Boolean,
    pulseScale: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTag = activeTab?.accountTag ?: activeTab?.title ?: "斗罗H5"

    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f))
            .border(1.dp, CyberCyanPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("active_session_pill"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Live Status Indicator Dot
            Box(
                modifier = Modifier.size(14.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing outer aura
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            when {
                                isMacroRunning -> SpiritPurpleTertiary.copy(alpha = 0.4f)
                                isGlobalSync -> AccentGreen.copy(alpha = 0.4f)
                                activeTab?.isLoading == true -> SoulGoldSecondary.copy(alpha = 0.4f)
                                else -> AccentGreen.copy(alpha = 0.4f)
                            }
                        )
                )
                // Inner solid dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isMacroRunning -> SpiritPurpleTertiary
                                isGlobalSync -> AccentGreen
                                activeTab?.isLoading == true -> SoulGoldSecondary
                                else -> AccentGreen
                            }
                        )
                )
            }

            // Session Name & Tag
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTag,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$tabsCount 个活跃会话",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = CyberCyanPrimary
                )
            }

            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = CyberCyanPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun LayoutModePill(
    label: String,
    isSelected: Boolean,
    icon: ImageVector,
    accentColor: Color = CyberCyanPrimary,
    onClick: () -> Unit,
    testTag: String
) {
    val bgAnim by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        label = "layoutModeBg"
    )
    val borderAnim by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.Transparent,
        label = "layoutModeBorder"
    )

    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgAnim)
            .border(1.dp, borderAnim, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
