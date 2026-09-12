package com.example

import com.example.data.model.ActionType
import com.example.data.model.MacroAction
import com.example.data.model.MacroHelper
import com.example.data.model.TabItem
import com.example.data.model.UserAgentPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testMacroSerialization() {
        val actions = listOf(
            MacroAction(type = ActionType.CLICK, xPercent = 0.5f, yPercent = 0.6f, delayAfterMs = 1200L, label = "点击进入"),
            MacroAction(type = ActionType.DELAY, delayAfterMs = 3000L, label = "等待结算")
        )
        val json = MacroHelper.toJson(actions)
        val parsed = MacroHelper.parseActions(json)

        assertEquals(2, parsed.size)
        assertEquals(ActionType.CLICK, parsed[0].type)
        assertEquals(0.5f, parsed[0].xPercent, 0.001f)
        assertEquals(1200L, parsed[0].delayAfterMs)
        assertEquals("点击进入", parsed[0].label)
    }

    @Test
    fun testPresetScripts() {
        val presets = MacroHelper.getPresetScripts()
        assertTrue(presets.isNotEmpty())
        val gm99Script = presets.firstOrNull { it.name.contains("斗罗大陆H5") }
        assertNotNull(gm99Script)
    }

    @Test
    fun testTabItemInitialization() {
        val tab = TabItem(
            title = "GM99 斗罗大陆H5",
            url = "https://m.gm99.com/dldl",
            accountTag = "大号"
        )
        assertEquals("https://m.gm99.com/dldl", tab.url)
        assertEquals("大号", tab.accountTag)
        assertTrue(tab.isSyncEnabled)
    }

    @Test
    fun testUserAgentPresets() {
        val desktopUa = UserAgentPreset.DESKTOP_CHROME.uaString
        assertTrue(desktopUa.contains("Windows NT"))

        val gm99Ua = UserAgentPreset.GM99_SPECIAL.uaString
        assertTrue(gm99Ua.contains("GM99GameClient"))
    }
}
