package com.aliucord.plugins.accountswitcher.switchermodal

import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.AccountSwitcher
import com.aliucord.plugins.accountswitcher.settings.PluginSettings
import com.aliucord.views.Button
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R

class SwitcherModal(private val accounts: LinkedHashMap<String, Long>) : SettingsPage() {
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Account Switcher")
        setActionBarSubtitle("${accounts.size} account(s)")

        val ctx = view.context

        headerBar.menu.add("Settings")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setIcon(Utils.tintToTheme(ContextCompat.getDrawable(ctx, R.d.ic_settings_24dp)!!.mutate()))
                .setOnMenuItemClickListener {
                    Utils.openPageWithProxy(ctx, PluginSettings(AccountSwitcher.mSettings))
                    false
                }

        addView(RecyclerView(ctx).apply {
            layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
            adapter = SwitcherAdapter(accounts)
        })

        if (StoreStream.getAuthentication().isAuthed) {
            addView(Button(ctx).apply {
                text = "Log Out"
                setBackgroundColor(view.resources.getColor(R.c.uikit_btn_bg_color_selector_red, view.context.theme))
                setOnClickListener { StoreStream.getAuthentication().logout() }
            })
        }
    }
}