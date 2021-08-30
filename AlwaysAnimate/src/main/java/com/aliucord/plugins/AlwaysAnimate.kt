package com.aliucord.plugins

import android.content.Context
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePrePatchFn
import com.aliucord.plugins.alwaysanimate.PluginSettings
import com.discord.utilities.icon.IconUtils


class AlwaysAnimate : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Allows making server icons and member avatars always animate."
            version = "1.1.0"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        if (settings.getBool("guildIcons", true)) {
            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForGuild", Long::class.javaObjectType, String::class.javaObjectType, String::class.javaObjectType, Boolean::class.java, Int::class.javaObjectType), PinePrePatchFn {
                it.args[3] = true
            })
        }

//        if (settings.getBool("avatars", true)) {
//            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForUser", Long::class.javaObjectType, String::class.javaObjectType, Int::class.javaObjectType, Boolean::class.javaPrimitiveType, Int::class.javaObjectType), PinePrePatchFn {
//                it.args[3] = true
//            })
//        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}