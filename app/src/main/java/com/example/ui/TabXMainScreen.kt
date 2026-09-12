package com.example.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AccountProfile
import com.example.data.model.ActionType
import com.example.data.model.MacroAction
import com.example.data.model.ViewLayoutMode
import com.example.ui.components.AccountManagerDialog
import com.example.ui.components.BrowserTopBar
import com.example.ui.components.CacheCleanerDialog
import com.example.ui.components.FloatingMacroHUD
import com.example.ui.components.GameSessionBottomBar
import com.example.ui.components.GameSessionTabSwitcherSheet
import com.example.ui.components.MultiWebViewContainer
import com.example.ui.components.SaveScriptDialog
import com.example.ui.components.ScriptManagerDialog
import com.example.ui.components.TabStrip
import com.example.ui.components.UserAgentDialog
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.SoulGoldSecondary
import com.example.ui.viewmodel.BrowserViewModel
import kotlinx.coroutines.delay

@Composable
fun TabXMainScreen(
    viewModel: BrowserViewModel = viewModel()
) {
    val context = LocalContext.current
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val gridTabIds by viewModel.gridTabIds.collectAsState()
    val layoutMode by viewModel.layoutMode.collectAsState()
    val isGlobalSync by viewModel.isGlobalSync.collectAsState()
    val isAllMuted by viewModel.isAllMuted.collectAsState()
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()

    val isRecording by viewModel.isRecording.collectAsState()
    val recordedActions by viewModel.recordedActions.collectAsState()
    val macroState by viewModel.macroState.collectAsState()

    val savedScripts by viewModel.savedScripts.collectAsState()
    val savedAccounts by viewModel.savedAccounts.collectAsState()

    val showScriptDialog by viewModel.showScriptDialog.collectAsState()
    val showAccountDialog by viewModel.showAccountDialog.collectAsState()
    val showUaDialog by viewModel.showUaDialog.collectAsState()
    val showCleanDialog by viewModel.showCleanDialog.collectAsState()
    val showSessionSwitcher by viewModel.showSessionSwitcher.collectAsState()
    val cleanNotification by viewModel.cleanNotification.collectAsState()

    var showSaveScriptDialog by remember { mutableStateOf(false) }

    val activeTab = viewModel.getActiveTab()

    // Handle Screen WakeLock
    val activity = context as? Activity
    DisposableEffect(keepScreenOn) {
        if (keepScreenOn) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Handle Back button
    BackHandler(enabled = activeTab?.canGoBack == true) {
        viewModel.goBack()
    }

    // Auto-dismiss clean notification after 3 seconds
    LaunchedEffect(cleanNotification) {
        if (cleanNotification != null) {
            delay(3000)
            viewModel.dismissCleanNotification()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            GameSessionBottomBar(
                tabs = tabs,
                activeTab = activeTab,
                layoutMode = layoutMode,
                isGlobalSync = isGlobalSync,
                isAllMuted = isAllMuted,
                isMacroRunning = macroState.isRunning,
                onOpenSessionSwitcher = { viewModel.setShowSessionSwitcher(true) },
                onLayoutModeChanged = { viewModel.setLayoutMode(it) },
                onToggleSync = { viewModel.toggleGlobalSync() },
                onToggleAllMute = { viewModel.toggleAllMute() },
                onAddNewSession = { viewModel.addNewTab() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Tab Strip
                TabStrip(
                    tabs = tabs,
                    activeTabId = activeTabId,
                    isGlobalSync = isGlobalSync,
                    onTabSelected = { viewModel.selectTab(it) },
                    onTabClosed = { viewModel.closeTab(it) },
                    onAddNewTab = { viewModel.addNewTab() }
                )

                // Omnibox Navigation & Quick Action Bar
                BrowserTopBar(
                    activeTab = activeTab,
                    layoutMode = layoutMode,
                    isGlobalSync = isGlobalSync,
                    isAllMuted = isAllMuted,
                    keepScreenOn = keepScreenOn,
                    isRecording = isRecording,
                    isMacroRunning = macroState.isRunning,
                    onNavigateUrl = { viewModel.loadUrl(it) },
                    onGoBack = { viewModel.goBack() },
                    onGoForward = { viewModel.goForward() },
                    onReload = { viewModel.reload() },
                    onLayoutModeChanged = { viewModel.setLayoutMode(it) },
                    onToggleSync = { viewModel.toggleGlobalSync() },
                    onToggleAllMute = { viewModel.toggleAllMute() },
                    onLaunch25Accounts = { viewModel.launch25AccountsMatrix() },
                    onLaunch36Accounts = { viewModel.launch36AccountsMatrix() },
                    onBatchReloadAll = { viewModel.batchReloadAll() },
                    onToggleKeepScreenOn = { viewModel.toggleKeepScreenOn() },
                    onOpenAccounts = { viewModel.setShowAccountDialog(true) },
                    onOpenScripts = { viewModel.setShowScriptDialog(true) },
                    onOpenUa = { viewModel.setShowUaDialog(true) },
                    onOpenClean = { viewModel.setShowCleanDialog(true) }
                )

                // Multi-Instance WebView Area
                MultiWebViewContainer(
                    allTabs = tabs,
                    activeTabId = activeTabId,
                    gridTabIds = gridTabIds,
                    layoutMode = layoutMode,
                    isGlobalSync = isGlobalSync,
                    webViewPool = viewModel.webViewPool,
                    onSelectTab = { viewModel.selectTab(it) },
                    onCloseTab = { viewModel.closeTab(it) },
                    onSetSingleMode = { tabId ->
                        viewModel.selectTab(tabId)
                        viewModel.setLayoutMode(ViewLayoutMode.SINGLE)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            // Quick floating button to return to 25-Grid when single view is active with 25+ tabs
            if (layoutMode == ViewLayoutMode.SINGLE && tabs.size >= 4) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val target = when {
                            tabs.size >= 36 -> ViewLayoutMode.GRID_36
                            tabs.size >= 25 -> ViewLayoutMode.GRID_25
                            tabs.size >= 16 -> ViewLayoutMode.GRID_16
                            tabs.size >= 9 -> ViewLayoutMode.GRID_9
                            else -> ViewLayoutMode.GRID_4
                        }
                        viewModel.setLayoutMode(target)
                    },
                    icon = { Icon(Icons.Default.GridView, null, tint = Color.Black) },
                    text = { Text("返回 ${tabs.size}开矩阵同屏", fontWeight = FontWeight.Bold, color = Color.Black) },
                    containerColor = SoulGoldSecondary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            // Floating Macro HUD (when recording or running macro)
            FloatingMacroHUD(
                isRecording = isRecording,
                recordedActions = recordedActions,
                macroState = macroState,
                onStopRecordingAndSave = {
                    viewModel.stopRecording()
                    showSaveScriptDialog = true
                },
                onCancelRecording = { viewModel.stopRecording() },
                onAddDelayStep = { delayMs ->
                    viewModel.addManualStep(
                        MacroAction(
                            type = ActionType.DELAY,
                            delayAfterMs = delayMs,
                            label = "等待 ${(delayMs / 1000.0)} 秒"
                        )
                    )
                },
                onPauseMacro = { viewModel.pauseScript() },
                onResumeMacro = { viewModel.resumeScript() },
                onStopMacro = { viewModel.stopScript() },
                onSetMacroSpeed = { viewModel.setMacroSpeed(it) },
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            // Clean Notification Toast Banner
            AnimatedVisibility(
                visible = cleanNotification != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 6.dp,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = cleanNotification ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.dismissCleanNotification() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Rich Game Session Tab Switcher Sheet (with live screenshot thumbnails & status indicators)
            if (showSessionSwitcher) {
                GameSessionTabSwitcherSheet(
                    tabs = tabs,
                    activeTabId = activeTabId,
                    layoutMode = layoutMode,
                    isGlobalSync = isGlobalSync,
                    isAllMuted = isAllMuted,
                    isMacroRunning = macroState.isRunning,
                    webViewPool = viewModel.webViewPool,
                    onDismiss = { viewModel.setShowSessionSwitcher(false) },
                    onSelectSession = { viewModel.selectTab(it) },
                    onCloseSession = { viewModel.closeTab(it) },
                    onMaximizeSession = { tabId ->
                        viewModel.selectTab(tabId)
                        viewModel.setLayoutMode(ViewLayoutMode.SINGLE)
                    },
                    onReloadSession = { tabId ->
                        val targetTab = tabs.find { it.id == tabId }
                        if (targetTab != null) {
                            val wv = viewModel.webViewPool.getOrCreateWebView(targetTab)
                            wv.reload()
                        }
                    },
                    onToggleSessionMute = { tabId ->
                        viewModel.toggleTabMute(tabId)
                    },
                    onAddNewSession = { viewModel.addNewTab() },
                    onLaunch25Matrix = { viewModel.launch25AccountsMatrix() },
                    onBatchReloadAll = { viewModel.batchReloadAll() }
                )
            }

            // Dialogs
            if (showScriptDialog) {
                ScriptManagerDialog(
                    savedScripts = savedScripts,
                    isRecording = isRecording,
                    onDismiss = { viewModel.setShowScriptDialog(false) },
                    onStartRecording = { viewModel.startRecording() },
                    onExecuteScript = { script, syncAll ->
                        viewModel.executeScript(script, syncAll)
                    },
                    onDeleteScript = { viewModel.deleteScript(it) }
                )
            }

            if (showSaveScriptDialog) {
                SaveScriptDialog(
                    recordedActionsCount = recordedActions.size,
                    onDismiss = { showSaveScriptDialog = false },
                    onSave = { name, desc, repeatCount, speed ->
                        viewModel.saveRecordedScript(name, desc, repeatCount, speed)
                        showSaveScriptDialog = false
                    }
                )
            }

            if (showAccountDialog) {
                AccountManagerDialog(
                    accounts = savedAccounts,
                    onDismiss = { viewModel.setShowAccountDialog(false) },
                    onLaunchAccount = { account ->
                        viewModel.launchAccountInNewTab(account)
                    },
                    onLaunchAllInGrid = { accountList ->
                        // Open 4 tabs in Quad Grid
                        accountList.take(4).forEachIndexed { idx, acc ->
                            if (idx == 0 && tabs.size == 1) {
                                viewModel.loadUrl(acc.customUrl.ifBlank { "https://m.gm99.com/dldl" })
                            } else {
                                viewModel.addNewTab(
                                    url = acc.customUrl.ifBlank { "https://m.gm99.com/dldl" },
                                    accountTag = acc.accountName,
                                    title = "斗罗-${acc.serverName}"
                                )
                            }
                        }
                        viewModel.setLayoutMode(ViewLayoutMode.GRID_4)
                    },
                    onLaunchBatchCount = { targetCount ->
                        viewModel.launchBatchAccounts(targetCount)
                    },
                    onSaveAccount = { viewModel.saveAccount(it) },
                    onDeleteAccount = { viewModel.deleteAccount(it) }
                )
            }

            if (showUaDialog) {
                UserAgentDialog(
                    currentUaString = activeTab?.customUserAgent,
                    onDismiss = { viewModel.setShowUaDialog(false) },
                    onApplyUserAgent = { preset, customUa ->
                        if (activeTab != null) {
                            viewModel.setTabUserAgent(activeTab.id, preset, customUa)
                        }
                    }
                )
            }

            if (showCleanDialog) {
                CacheCleanerDialog(
                    onDismiss = { viewModel.setShowCleanDialog(false) },
                    onExecuteClean = { options ->
                        viewModel.performClean(options)
                    }
                )
            }
        }
    }
}
