package com.aliucord.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.plugins.bettermediaviewer.Patches
import com.aliucord.plugins.bettermediaviewer.PluginSettings

class BetterMediaViewer : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    companion object {
        val logger = Logger("BetterMediaViewer")
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Adds a variety of improvements to the default media viewer."
            version = "1.0.0"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        with(Patches(patcher), {
            patchMenu()
            patchControls()

            if (settings.getBool("immersiveModeState", false)) patchImmersiveMode()
            if (settings.getBool("hideBackButton", false)) patchBackButton()
            if (settings.getBool("bottomToolbar", false)) patchToolbar()
            if (settings.getBool("removeZoomLimit", true)) patchZoomLimit()
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}