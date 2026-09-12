package com.example.engine

import com.example.data.model.ActionType
import com.example.data.model.MacroAction
import com.example.data.model.MacroScript
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MacroState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val scriptName: String = "",
    val currentLoop: Int = 0,
    val maxLoops: Int = 0, // 0 = infinite
    val currentStepIndex: Int = 0,
    val totalSteps: Int = 0,
    val currentActionLabel: String = "",
    val speedMultiplier: Float = 1.0f,
    val syncToAllTabs: Boolean = false
)

class MacroExecutionEngine(
    private val webViewPool: WebViewPool,
    private val coroutineScope: CoroutineScope
) {
    private var executionJob: Job? = null
    private val _macroState = MutableStateFlow(MacroState())
    val macroState: StateFlow<MacroState> = _macroState.asStateFlow()

    fun startScript(
        script: MacroScript,
        actions: List<MacroAction>,
        targetTabId: String,
        syncToAllTabs: Boolean = false,
        overrideSpeed: Float? = null
    ) {
        stopScript()
        if (actions.isEmpty()) return

        val speed = overrideSpeed ?: script.speedMultiplier.coerceAtLeast(0.1f)
        val maxLoops = script.repeatCount
        val jitter = script.randomJitterPx

        _macroState.value = MacroState(
            isRunning = true,
            isPaused = false,
            scriptName = script.name,
            currentLoop = 1,
            maxLoops = maxLoops,
            currentStepIndex = 0,
            totalSteps = actions.size,
            currentActionLabel = "准备开始",
            speedMultiplier = speed,
            syncToAllTabs = syncToAllTabs
        )

        executionJob = coroutineScope.launch(Dispatchers.Default) {
            var loopCount = 0
            while (_macroState.value.isRunning && (maxLoops == 0 || loopCount < maxLoops)) {
                loopCount++
                _macroState.value = _macroState.value.copy(currentLoop = loopCount)

                for (index in actions.indices) {
                    if (!_macroState.value.isRunning) break

                    // Handle paused state
                    while (_macroState.value.isPaused && _macroState.value.isRunning) {
                        delay(200)
                    }

                    val action = actions[index]
                    _macroState.value = _macroState.value.copy(
                        currentStepIndex = index + 1,
                        currentActionLabel = action.label.ifBlank { "步骤 ${index + 1}: ${action.type.name}" }
                    )

                    // Execute action
                    when (action.type) {
                        ActionType.CLICK -> {
                            if (syncToAllTabs) {
                                webViewPool.executeSimulatedClickOnAllTabs(
                                    action.xPercent,
                                    action.yPercent,
                                    jitterPx = jitter
                                )
                            } else {
                                webViewPool.executeSimulatedClickOnTab(
                                    targetTabId,
                                    action.xPercent,
                                    action.yPercent,
                                    jitterPx = jitter
                                )
                            }
                        }
                        ActionType.DOUBLE_CLICK -> {
                            if (syncToAllTabs) {
                                webViewPool.executeSimulatedClickOnAllTabs(action.xPercent, action.yPercent, jitterPx = jitter)
                                delay(120)
                                webViewPool.executeSimulatedClickOnAllTabs(action.xPercent, action.yPercent, jitterPx = jitter)
                            } else {
                                webViewPool.executeSimulatedClickOnTab(targetTabId, action.xPercent, action.yPercent, jitterPx = jitter)
                                delay(120)
                                webViewPool.executeSimulatedClickOnTab(targetTabId, action.xPercent, action.yPercent, jitterPx = jitter)
                            }
                        }
                        ActionType.LONG_PRESS -> {
                            // Executed with jitter
                            if (syncToAllTabs) {
                                webViewPool.executeSimulatedClickOnAllTabs(action.xPercent, action.yPercent, jitterPx = jitter)
                            } else {
                                webViewPool.executeSimulatedClickOnTab(targetTabId, action.xPercent, action.yPercent, jitterPx = jitter)
                            }
                        }
                        ActionType.DELAY -> {
                            // Delay only
                        }
                        ActionType.RELOAD_PAGE -> {
                            if (syncToAllTabs) {
                                webViewPool.syncReloadAllTabs()
                            } else {
                                webViewPool.executeSimulatedClickOnTab(targetTabId, 0.5f, 0.5f)
                            }
                        }
                        else -> {}
                    }

                    // Calculate delay with speed multiplier
                    val delayMs = ((action.delayAfterMs.coerceAtLeast(100L)) / speed).toLong()
                    delay(delayMs)
                }

                // Interval between loops
                if (script.intervalBetweenLoopsMs > 0 && _macroState.value.isRunning) {
                    val loopDelay = (script.intervalBetweenLoopsMs / speed).toLong()
                    delay(loopDelay)
                }
            }

            _macroState.value = _macroState.value.copy(
                isRunning = false,
                isPaused = false,
                currentActionLabel = "执行完毕"
            )
        }
    }

    fun pauseScript() {
        if (_macroState.value.isRunning) {
            _macroState.value = _macroState.value.copy(isPaused = true)
        }
    }

    fun resumeScript() {
        if (_macroState.value.isRunning) {
            _macroState.value = _macroState.value.copy(isPaused = false)
        }
    }

    fun stopScript() {
        executionJob?.cancel()
        executionJob = null
        _macroState.value = _macroState.value.copy(
            isRunning = false,
            isPaused = false,
            currentActionLabel = "已停止"
        )
    }

    fun setSpeed(multiplier: Float) {
        _macroState.value = _macroState.value.copy(speedMultiplier = multiplier)
    }

    fun setSyncMode(sync: Boolean) {
        _macroState.value = _macroState.value.copy(syncToAllTabs = sync)
    }
}
