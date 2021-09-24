package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PineInsteadFn
import com.aliucord.plugins.accountswitcher.authToken
import com.aliucord.plugins.accountswitcher.settings.PluginSettings
import com.aliucord.plugins.accountswitcher.switchermodal.SwitcherModal
import com.discord.stores.StoreStream
import com.discord.widgets.settings.WidgetSettings
import com.google.gson.reflect.TypeToken

@AliucordPlugin
class AccountSwitcher : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    companion object {
        lateinit var mSettings: SettingsAPI

        private val accountsType = TypeToken.getParameterized(LinkedHashMap::class.javaObjectType, String::class.javaObjectType, Long::class.javaObjectType).getType()

        val logger = Logger("AccountSwitcher")
        var accounts: LinkedHashMap<String, Long>
            get() = mSettings.getObject("accounts", LinkedHashMap(), accountsType)
            set(v) = mSettings.setObject("accounts", v)

        fun addAccount(token: String, id: Long) {
            accounts = accounts.apply { put(token, id) }
        }

        fun removeAccount(token: String) {
            accounts = accounts.apply { accounts.remove(token) }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        mSettings = settings

        StoreStream.getUsers().fetchUsers(accounts.values.toList())

        patcher.patch(WidgetSettings::class.java.getDeclaredMethod("showLogoutDialog", Context::class.java), PineInsteadFn {
            Utils.openPageWithProxy(Utils.appActivity, SwitcherModal(accounts.apply { remove(StoreStream.getAuthentication().authToken) }))
        })

//        patcher.patch(WidgetAuthLanding::class.java.getDeclaredMethod("onViewBound", View::class.java), PinePatchFn {
//            val ctx = (it.thisObject as WidgetAuthLanding).requireContext()
//            val view = it.args[0] as RelativeLayout
//            val v = view.getChildAt(1) as LinearLayout
//
//            val padding = DimenUtils.dpToPx(18)
//            Button(ctx).apply {
//                text = "Open Account Switcher"
//                textSize = 16.0f
//                setPadding(0, padding, 0, padding)
//                setOnClickListener { Utils.openPageWithProxy(Utils.appActivity, Modal(accounts)) }
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