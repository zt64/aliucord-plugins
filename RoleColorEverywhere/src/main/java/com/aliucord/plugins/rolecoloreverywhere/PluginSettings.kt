package com.aliucord.plugins.rolecoloreverywhere

import android.content.Context
import android.os.Bundle
import android.view.View
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        with(requireContext()) {
            addView(createCheckedSetting(this, "User mentions", "Whether mentions are colored", "userMentions"))
            addView(createCheckedSetting(this, "Typing text", "Whether typing users are colored", "typingText"))
            addView(createCheckedSetting(this, "Voice users", "Whether usernames in voice channels are colored", "voiceChannel"))
        }
    }

    private fun createCheckedSetting(ctx: Context, title: String, subtext: String, setting: String): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtext).apply {
            isChecked = settings.getBool(setting, true)
            setOnCheckedListener {
                settings.setBool(setting, it)
                PluginManager.stopPlugin("RoleColorEverywhere")
                PluginManager.startPlugin("RoleColorEverywhere")
            }
        }
    }
}