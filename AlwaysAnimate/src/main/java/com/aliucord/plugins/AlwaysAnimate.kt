package com.aliucord.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePrePatchFn
import com.aliucord.plugins.alwaysanimate.PluginSettings
import com.discord.utilities.icon.IconUtils

@AliucordPlugin
class AlwaysAnimate : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        if (settings.getBool("guildIcons", true)) {
            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForGuild", Long::class.javaObjectType, String::class.javaObjectType, String::class.javaObjectType, Boolean::class.java, Int::class.javaObjectType), PinePrePatchFn {
                it.args[3] = true
            })
        }

        if (settings.getBool("messageAvatar", false)) {
            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForUser", Long::class.javaObjectType, String::class.javaObjectType, Int::class.javaObjectType, Boolean::class.javaPrimitiveType, Int::class.javaObjectType), PinePrePatchFn {
                it.args[3] = true
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}