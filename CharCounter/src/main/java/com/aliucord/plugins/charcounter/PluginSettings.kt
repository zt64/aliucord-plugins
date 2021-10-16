package com.aliucord.plugins.charcounter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Always Visible", "Whether the counter should be visible even when there is no text").apply {
            isChecked = settings.getBool("alwaysVisible", false)
            setOnCheckedListener { settings.setBool("alwaysVisible", it) }
        })
    }
}