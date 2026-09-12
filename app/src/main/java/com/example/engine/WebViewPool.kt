package com.example.engine

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.model.TabItem
import java.util.concurrent.ConcurrentHashMap

class WebViewPool(
    private val context: Context,
    private val onPageTitleChanged: (tabId: String, title: String) -> Unit,
    private val onPageUrlChanged: (tabId: String, url: String, canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    private val onLoadingProgressChanged: (tabId: String, progress: Int, isLoading: Boolean) -> Unit,
    private val onRecordClickReceived: (tabId: String, x: Float, y: Float) -> Unit,
    private val onMasterSyncTouch: (x: Float, y: Float) -> Unit
) {
    private val webViewMap = ConcurrentHashMap<String, WebView>()
    private val thumbnailCache = ConcurrentHashMap<String, Bitmap>()

    @SuppressLint("SetJavaScriptEnabled")
    fun getOrCreateWebView(tab: TabItem): WebView {
        return webViewMap.getOrPut(tab.id) {
            WebView(context).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
                isFocusable = true
                isFocusableInTouchMode = true
                scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    mediaPlaybackRequiresUserGesture = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    cacheMode = WebSettings.LOAD_DEFAULT

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        safeBrowsingEnabled = false
                    }

                    // Apply custom User Agent if set
                    if (!tab.customUserAgent.isNullOrBlank()) {
                        userAgentString = tab.customUserAgent
                    }
                }

                // Enable multi-instance cookie support
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                // Inject TabX Bridge for macro recording and automation
                addJavascriptInterface(
                    TabXBridge(
                        tabId = tab.id,
                        onPageClickRecorded = onRecordClickReceived,
                        onMasterTouchDispatched = onMasterSyncTouch
                    ),
                    TabXBridge.JS_BRIDGE_NAME
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onLoadingProgressChanged(tab.id, 10, true)
                        url?.let {
                            onPageUrlChanged(tab.id, it, canGoBack(), canGoForward())
                        }
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onLoadingProgressChanged(tab.id, 100, false)
                        url?.let {
                            onPageUrlChanged(tab.id, it, canGoBack(), canGoForward())
                        }
                        // Inject touch listener script for macro recording
                        view?.evaluateJavascript(TabXBridge.RECORDING_INJECTION_SCRIPT, null)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val requestUrl = request?.url?.toString() ?: return false
                        if (requestUrl.startsWith("http://") || requestUrl.startsWith("https://")) {
                            return false
                        }
                        // Allow custom schemes (e.g. GM99 pay, alipay, weixin)
                        return try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, request.url)
                            context.startActivity(intent)
                            true
                        } catch (e: Exception) {
                            true
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        onLoadingProgressChanged(tab.id, newProgress, newProgress < 100)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        if (!title.isNullOrBlank()) {
                            onPageTitleChanged(tab.id, title)
                        }
                    }
                }

                // Initial URL load
                loadUrl(tab.url)
            }
        }
    }

    fun updateTabUserAgent(tabId: String, newUserAgent: String) {
        val webView = webViewMap[tabId] ?: return
        if (newUserAgent.isBlank()) {
            webView.settings.userAgentString = null // Reset to default
        } else {
            webView.settings.userAgentString = newUserAgent
        }
        webView.reload()
    }

    fun applyRecordingState(active: Boolean) {
        val script = TabXBridge.setRecordingActiveScript(active)
        webViewMap.values.forEach { webView ->
            webView.evaluateJavascript(script, null)
        }
    }

    fun executeSimulatedClickOnTab(tabId: String, xPercent: Float, yPercent: Float, jitterPx: Int = 0) {
        val webView = webViewMap[tabId] ?: return
        val script = TabXBridge.createSimulatedClickScript(xPercent, yPercent, jitterPx)
        webView.post {
            webView.evaluateJavascript(script, null)
        }
    }

    fun executeSimulatedClickOnAllTabs(xPercent: Float, yPercent: Float, excludeTabId: String? = null, jitterPx: Int = 0) {
        val script = TabXBridge.createSimulatedClickScript(xPercent, yPercent, jitterPx)
        webViewMap.forEach { (id, webView) ->
            if (excludeTabId == null || id != excludeTabId) {
                webView.post {
                    webView.evaluateJavascript(script, null)
                }
            }
        }
    }

    fun syncNavigateAllTabs(url: String) {
        webViewMap.values.forEach { webView ->
            webView.post {
                webView.loadUrl(url)
            }
        }
    }

    fun syncReloadAllTabs() {
        webViewMap.values.forEach { webView ->
            webView.post {
                webView.reload()
            }
        }
    }

    fun setAllMuted(muted: Boolean) {
        val script = if (muted) TabXBridge.MUTE_SCRIPT else TabXBridge.UNMUTE_SCRIPT
        webViewMap.values.forEach { webView ->
            webView.post {
                webView.evaluateJavascript(script, null)
            }
        }
    }

    fun setTabMuted(tabId: String, muted: Boolean) {
        val webView = webViewMap[tabId] ?: return
        val script = if (muted) TabXBridge.MUTE_SCRIPT else TabXBridge.UNMUTE_SCRIPT
        webView.post {
            webView.evaluateJavascript(script, null)
        }
    }

    fun getCachedThumbnail(tabId: String): Bitmap? {
        return thumbnailCache[tabId]
    }

    fun captureThumbnail(tabId: String, width: Int = 300, height: Int = 180, onCaptured: (Bitmap?) -> Unit) {
        val webView = webViewMap[tabId]
        if (webView == null || webView.width <= 0 || webView.height <= 0) {
            onCaptured(thumbnailCache[tabId])
            return
        }
        webView.post {
            try {
                val w = webView.width
                val h = webView.height
                if (w > 0 && h > 0) {
                    val fullBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(fullBitmap)
                    webView.draw(canvas)
                    val targetHeight = (height.coerceAtLeast((width * h) / w)).coerceAtMost(400)
                    val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, width, targetHeight, true)
                    fullBitmap.recycle()
                    thumbnailCache[tabId] = scaledBitmap
                    onCaptured(scaledBitmap)
                } else {
                    onCaptured(thumbnailCache[tabId])
                }
            } catch (e: Exception) {
                onCaptured(thumbnailCache[tabId])
            }
        }
    }

    fun releaseTab(tabId: String) {
        thumbnailCache.remove(tabId)?.recycle()
        webViewMap.remove(tabId)?.apply {
            stopLoading()
            loadUrl("about:blank")
            clearHistory()
            removeAllViews()
            destroy()
        }
    }

    fun releaseAll() {
        thumbnailCache.values.forEach { it.recycle() }
        thumbnailCache.clear()
        webViewMap.forEach { (_, webView) ->
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        }
        webViewMap.clear()
    }
}
