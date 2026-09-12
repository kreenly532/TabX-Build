package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.SoulGoldSecondary

@Composable
fun TabStrip(
    tabs: List<TabItem>,
    activeTabId: String,
    isGlobalSync: Boolean,
    onTabSelected: (String) -> Unit,
    onTabClosed: (String) -> Unit,
    onAddNewTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabs.forEach { tab ->
                val isActive = tab.id == activeTabId
                TabChip(
                    tab = tab,
                    isActive = isActive,
                    isGlobalSync = isGlobalSync,
                    onSelect = { onTabSelected(tab.id) },
                    onClose = { onTabClosed(tab.id) },
                    canClose = tabs.size > 1
                )
            }

            // Add Tab Button
            IconButton(
                onClick = onAddNewTab,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                    .testTag("add_new_tab_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新建标签页",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TabChip(
    tab: TabItem,
    isActive: Boolean,
    isGlobalSync: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    canClose: Boolean
) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        },
        label = "tabBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isActive) CyberCyanPrimary else Color.Transparent,
        label = "tabBorder"
    )

    Box(
        modifier = Modifier
            .widthIn(min = 110.dp, max = 180.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onSelect() }
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (tab.isLoading) {
                    CircularProgressIndicator(
                        progress = { tab.progress / 100f },
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 2.dp,
                        color = CyberCyanPrimary
                    )
                } else if (tab.url.contains("gm99.com") || tab.url.contains("dldl")) {
                    Icon(
                        imageVector = Icons.Default.Gamepad,
                        contentDescription = null,
                        tint = if (isActive) SoulGoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Web,
                        contentDescription = null,
                        tint = if (isActive) CyberCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = tab.accountTag?.let { "[$it] ${tab.title}" } ?: tab.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Sync badge if global sync active
            if (isGlobalSync) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "同步中",
                    tint = AccentGreen,
                    modifier = Modifier
                        .size(12.dp)
                        .padding(horizontal = 2.dp)
                )
            }

            if (canClose) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { onClose() }
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭标签",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
