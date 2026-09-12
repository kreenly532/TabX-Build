package com.example.engine

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import java.io.File

data class CleanOptions(
    val clearCache: Boolean = true,
    val clearCookies: Boolean = true,
    val clearLocalStorage: Boolean = true,
    val clearHistory: Boolean = true,
    val clearFormData: Boolean = true
)

data class CleanResult(
    val cacheCleared: Boolean,
    val cookiesCleared: Boolean,
    val storageCleared: Boolean,
    val bytesCleanedEstimate: Long
)

object SessionCleaner {

    fun performClean(
        context: Context,
        options: CleanOptions,
        activeWebView: WebView? = null,
        onComplete: (CleanResult) -> Unit
    ) {
        var bytesCleaned = 0L

        // 1. Clear Web Cache
        if (options.clearCache) {
            activeWebView?.clearCache(true)
            try {
                val cacheDir = context.cacheDir
                bytesCleaned += getFolderSize(cacheDir)
                deleteDir(cacheDir)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Clear Cookies
        var cookiesCleared = false
        if (options.clearCookies) {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies { success ->
                cookieManager.flush()
            }
            cookieManager.removeSessionCookies(null)
            cookiesCleared = true
        }

        // 3. Clear Web Storage (LocalStorage, IndexedDB, WebSQL)
        var storageCleared = false
        if (options.clearLocalStorage) {
            WebStorage.getInstance().deleteAllData()
            try {
                val appWebDir = File(context.applicationInfo.dataDir, "app_webview")
                if (appWebDir.exists()) {
                    bytesCleaned += getFolderSize(appWebDir)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            storageCleared = true
        }

        // 4. Clear Form Data and History
        if (options.clearFormData) {
            activeWebView?.clearFormData()
        }
        if (options.clearHistory) {
            activeWebView?.clearHistory()
        }

        onComplete(
            CleanResult(
                cacheCleared = options.clearCache,
                cookiesCleared = cookiesCleared,
                storageCleared = storageCleared,
                bytesCleanedEstimate = bytesCleaned
            )
        )
    }

    private fun getFolderSize(file: File?): Long {
        if (file == null || !file.exists()) return 0L
        var size = 0L
        val children = file.listFiles() ?: return file.length()
        for (child in children) {
            size += if (child.isDirectory) getFolderSize(child) else child.length()
        }
        return size
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list() ?: return false
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) return false
            }
            return dir.delete()
        } else if (dir != null && dir.isFile) {
            return dir.delete()
        }
        return false
    }
}
