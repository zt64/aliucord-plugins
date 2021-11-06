package com.aliucord.plugins.rolecoloreverywhere

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        with(requireContext()) {
            addView(TextView(this, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
                text = "Note: Some of these options will require a restart to colorize already rendered elements"
            })
            addView(createCheckedSetting(this, "User mentions", "Whether mentions are colored", "userMentions"))
            addView(createCheckedSetting(this, "Typing text", "Whether typing users are colored", "typingText"))
            addView(createCheckedSetting(this, "Voice users", "Whether usernames in voice channels are colored", "voiceChannel"))
            addView(createCheckedSetting(this, "Profile name", "Whether usernames on profiles are colored", "profileName"))
            addView(createCheckedSetting(this, "Profile name tag", "Whether tags on profiles are colored", "profileTag"))
            addView(createCheckedSetting(this, "Statuses", "Whether statuses on sidebars and profiles are colored", "status"))
            addView(createCheckedSetting(this, "Messages", "Whether messages are colored", "messages", false))
        }
    }

    private fun createCheckedSetting(ctx: Context, title: String, subtext: String, setting: String, default: Boolean = true): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtext).apply {
            isChecked = settings.getBool(setting, default)
            setOnCheckedListener {
                settings.setBool(setting, it)
                PluginManager.stopPlugin("RoleColorEverywhere")
                PluginManager.startPlugin("RoleColorEverywhere")
            }
        }
    }
}