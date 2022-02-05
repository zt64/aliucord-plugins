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

        val ctx = requireContext()

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Role Color Everywhere"
        })
        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
            text = "Note: Some of these options will require a restart to colorize already rendered elements"
        })
        addCheckedSetting(ctx, "User mentions", "Whether mentions are colored", "userMentions")
        addCheckedSetting(ctx, "Typing text", "Whether typing users are colored", "typingText")
        addCheckedSetting(ctx, "Voice users", "Whether usernames in voice channels are colored", "voiceChannel")
        addCheckedSetting(ctx, "Reaction lists", "Whether usernames on reaction lists are colored", "reactionList")
        addCheckedSetting(ctx, "Profile name", "Whether usernames on profiles are colored", "profileName")
        addCheckedSetting(ctx, "Profile name tag", "Whether tags on profiles are colored", "profileTag")
        addCheckedSetting(ctx, "Statuses", "Whether statuses on sidebars and profiles are colored", "status")
        addCheckedSetting(ctx, "Messages", "Whether messages are colored", "messages", false)
    }

    private fun BottomSheet.addCheckedSetting(ctx: Context, title: String, subtext: String, setting: String, default: Boolean = true) {
        addView(Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtext).apply {
            isChecked = settings.getBool(setting, default)
            setOnCheckedListener {
                settings.setBool(setting, it)
                PluginManager.stopPlugin("RoleColorEverywhere")
                PluginManager.startPlugin("RoleColorEverywhere")
            }
        })
    }
}