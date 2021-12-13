package com.aliucord.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.plugins.bettermediaviewer.Patches.patchControls
import com.aliucord.plugins.bettermediaviewer.Patches.patchToolbar
import com.aliucord.plugins.bettermediaviewer.Patches.patchWidget
import com.aliucord.plugins.bettermediaviewer.Patches.patchZoomLimit
import com.aliucord.plugins.bettermediaviewer.PluginSettings

@AliucordPlugin
class BetterMediaViewer : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    override fun start(context: Context) {
        patcher.run {
            patchWidget()
            patchControls()
            if (settings.getBool("bottomToolbar", false)) patchToolbar()
            if (settings.getBool("removeZoomLimit", true)) patchZoomLimit()
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}