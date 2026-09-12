package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionType
import com.example.data.model.MacroAction
import com.example.engine.MacroState
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.SoulGoldSecondary
import com.example.ui.theme.SpiritPurpleTertiary

@Composable
fun FloatingMacroHUD(
    isRecording: Boolean,
    recordedActions: List<MacroAction>,
    macroState: MacroState,
    onStopRecordingAndSave: () -> Unit,
    onCancelRecording: () -> Unit,
    onAddDelayStep: (Long) -> Unit,
    onPauseMacro: () -> Unit,
    onResumeMacro: () -> Unit,
    onStopMacro: () -> Unit,
    onSetMacroSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isRecording && !macroState.isRunning) return

    var isCollapsed by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "recBlink")
    val recAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recAlpha"
    )

    Box(
        modifier = modifier
            .padding(12.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp))
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isRecording) AccentRed else SpiritPurpleTertiary
            ),
            modifier = Modifier.width(if (isCollapsed) 130.dp else 290.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isRecording) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .alpha(recAlpha)
                                    .clip(CircleShape)
                                    .background(AccentRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "正在录制手势",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentRed
                                )
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = SpiritPurpleTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "挂机执行中",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SpiritPurpleTertiary
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { isCollapsed = !isCollapsed },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = if (isCollapsed) "展开" else "收起",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (!isCollapsed) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isRecording) {
                        // Recording Details
                        Text(
                            text = "已录制步骤: ${recordedActions.size} 个点击",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "请在网页上直接点击游戏按钮，自动捕获坐标",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Add Delay Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onAddDelayStep(1500L) },
                                modifier = Modifier.weight(1f).height(32.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text("+1.5s 延时", fontSize = 10.5.sp)
                            }
                            OutlinedButton(
                                onClick = { onAddDelayStep(3000L) },
                                modifier = Modifier.weight(1f).height(32.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text("+3s 延时", fontSize = 10.5.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Finish / Cancel Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = onStopRecordingAndSave,
                                enabled = recordedActions.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.Save, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("保存脚本", fontSize = 11.5.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = onCancelRecording,
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text("取消录制", fontSize = 11.5.sp, color = AccentRed)
                            }
                        }
                    } else if (macroState.isRunning) {
                        // Playback Details
                        Text(
                            text = macroState.scriptName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        Text(
                            text = "循环: ${macroState.currentLoop}${if (macroState.maxLoops > 0) "/${macroState.maxLoops}" else " (无限)"} | 步骤: ${macroState.currentStepIndex}/${macroState.totalSteps}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Progress Bar
                        if (macroState.totalSteps > 0) {
                            LinearProgressIndicator(
                                progress = { macroState.currentStepIndex.toFloat() / macroState.totalSteps.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = SpiritPurpleTertiary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Speed selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("倍速:", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(1.0f, 1.5f, 2.0f, 3.0f).forEach { spd ->
                                    val isSelected = macroState.speedMultiplier == spd
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isSelected) SpiritPurpleTertiary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { onSetMacroSpeed(spd) }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${spd}x",
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls: Pause / Stop
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (macroState.isPaused) {
                                Button(
                                    onClick = onResumeMacro,
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("继续", fontSize = 11.5.sp, color = Color.White)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = onPauseMacro,
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.Pause, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("暂停", fontSize = 11.5.sp)
                                }
                            }

                            Button(
                                onClick = onStopMacro,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.Stop, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("停止", fontSize = 11.5.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
