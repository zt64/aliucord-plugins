package com.aliucord.plugins

import android.content.Context
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.accountswitcher.PluginSettings
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.settings.WidgetSettings
import com.lytefast.flexinput.R

@AliucordPlugin
class AccountSwitcher : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    override fun start(context: Context) {
        val icon = ContextCompat.getDrawable(context, R.d.ic_my_account_24dp)

        patcher.patch(WidgetSettings::class.java.getDeclaredMethod("onTabSelected"), PinePatchFn {
            val ctx = (it.thisObject as WidgetSettings).requireContext()
            icon?.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
            Utils.appActivity.t.menu.add("Account Switcher")
                .setIcon(icon)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setOnMenuItemClickListener {
                    Utils.showToast(context, "Account Switcher")
                    false
                }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}