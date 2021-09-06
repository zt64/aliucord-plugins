package com.aliucord.plugins.alwaysanimate

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

        val ctx = requireContext()

        addView(createCheckedSetting(ctx, "Server icons", "guildIcons"))
        addView(createCheckedSetting(ctx, "User avatars", "avatars"))
        addView(createCheckedSetting(ctx, "Round Animated Avatars", "roundedAvatars"))
        addView(createCheckedSetting(ctx, "Disable animations when battery saver is on", "batterySaver", false))
    }

    private fun createCheckedSetting(ctx: Context, title: String, setting: String, checked: Boolean = true): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, null).apply {
            isChecked = settings.getBool(setting, checked)
            setOnCheckedListener {
                settings.setBool(setting, it)
                PluginManager.stopPlugin("AlwaysAnimate")
                PluginManager.startPlugin("AlwaysAnimate")
            }
        }
    }
}