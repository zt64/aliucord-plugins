package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.InsteadHook
import com.aliucord.plugins.accountswitcher.SwitcherPage
import com.aliucord.plugins.accountswitcher.authToken
import com.aliucord.plugins.accountswitcher.getAccounts
import com.aliucord.plugins.accountswitcher.settings.PluginSettings
import com.discord.stores.StoreStream
import com.discord.widgets.settings.WidgetSettings

@AliucordPlugin
class AccountSwitcher : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    companion object {
        lateinit var mSettings: SettingsAPI
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        mSettings = settings

        StoreStream.getUsers().fetchUsers(getAccounts().map { it.id })

        patcher.patch(WidgetSettings::class.java.getDeclaredMethod("showLogoutDialog", Context::class.java), InsteadHook {
            Utils.openPageWithProxy(Utils.appActivity, SwitcherPage(getAccounts().apply {
                removeIf { it.token == StoreStream.getAuthentication().authToken }
            }))
        })

        // Eventually add a switcher button to the login page
        //        patcher.patch(WidgetAuthLanding::class.java.getDeclaredMethod("onViewBound", View::class.java), Hook {
        //            val ctx = (it.thisObject as WidgetAuthLanding).requireContext()
        //            val view = it.args[0] as RelativeLayouts
        //            val v = view.getChildAt(1) as LinearLayout
        //
        //            val padding = DimenUtils.dpToPx(18)
        //            Button(ctx).apply {
        //                text = "Open Account Switcher"
        //                textSize = 16.0f
        //                setPadding(0, padding, 0, padding)
        //                setOnClickListener { Utils.openPageWithProxy(Utils.appActivity, SwitcherPage(getAccounts())) }
        //
        //                if (StoreStream.getUserSettingsSystem().theme == "light")
        //                    setBackgroundColor(ctx.resources.getColor(R.c.uikit_btn_bg_color_selector_secondary_light, null))
        //                else
        //                    setBackgroundColor(ctx.resources.getColor(R.c.uikit_btn_bg_color_selector_secondary_dark, null))
        //
        //                v.addView(this)
        //            }
        //        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}