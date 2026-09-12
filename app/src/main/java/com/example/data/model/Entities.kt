package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "macro_scripts")
data class MacroScript(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: String = "GM99斗罗H5",
    val actionsJson: String = "[]",
    val repeatCount: Int = 0, // 0 = infinite loop
    val speedMultiplier: Float = 1.0f,
    val randomJitterPx: Int = 6, // Anti-ban random coordinate jitter
    val intervalBetweenLoopsMs: Long = 1000L,
    val createdAt: Long = System.currentTimeMillis()
)

data class MacroAction(
    val type: ActionType = ActionType.CLICK,
    val xPercent: Float = 0.5f, // 0.0 to 1.0 relative screen coordinate
    val yPercent: Float = 0.5f, // 0.0 to 1.0 relative screen coordinate
    val delayAfterMs: Long = 1000L,
    val textParam: String = "",
    val label: String = ""
)

enum class ActionType {
    CLICK,
    DOUBLE_CLICK,
    LONG_PRESS,
    DELAY,
    SCROLL_DOWN,
    SCROLL_UP,
    RELOAD_PAGE
}

@Entity(tableName = "account_profiles")
data class AccountProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountName: String,
    val serverName: String = "斗罗1服",
    val username: String = "",
    val notes: String = "",
    val customUa: String? = null,
    val customUrl: String = "https://m.gm99.com/dldl",
    val colorTagHex: String = "#3B82F6",
    val lastUsedTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "browser_bookmarks")
data class BrowserBookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val isGamePreset: Boolean = false,
    val iconCategory: String = "GAME"
)
