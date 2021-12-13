package com.aliucord.plugins.accountswitcher.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.accountswitcher.SwitcherPage
import com.aliucord.plugins.accountswitcher.authToken
import com.aliucord.plugins.accountswitcher.getAccounts
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.aliucord.views.Divider
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        val accounts = getAccounts()
        val ctx = requireContext()
        val accountAdapter = AccountAdapter(this@PluginSettings, accounts)

        setActionBarTitle("Account Switcher")

        headerBar.menu.add("Switcher")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setIcon(Utils.tintToTheme(ContextCompat.getDrawable(ctx, R.e.ic_my_account_24dp)!!.mutate()))
            .setOnMenuItemClickListener {
                Utils.openPageWithProxy(ctx, SwitcherPage(getAccounts().apply {
                    removeIf { it.token == StoreStream.getAuthentication().authToken }
                }))
                false
            }

        RecyclerView(ctx).apply {
            adapter = accountAdapter
            layoutManager = LinearLayoutManager(ctx)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                weight = 1f
            }

            addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ShapeDrawable(RectShape()).apply {
                    intrinsicHeight = DimenUtils.defaultPadding
                    setTint(Color.TRANSPARENT)
                })
            })

            linearLayout.addView(this)
        }

        addView(Divider(ctx))

        addView(Button(ctx).apply {
            text = "Add Account"
            setOnClickListener {
                AccountDialog(accountAdapter).show(parentFragmentManager, "Add Account")
            }
        })

        if (StoreStream.getAuthentication().isAuthed) {
            val token = StoreStream.getAuthentication().authToken

            if (getAccounts().none { it.token == token }) {
                addView(Button(ctx).apply {
                    text = "Add Current Account"
                    setOnClickListener {
                        when {
                            getAccounts().any { it.token == token } -> Utils.showToast("Account already added")
                            token != null -> {
                                accountAdapter.addAccount(token, StoreStream.getUsers().me.id)
                                Utils.showToast("Added current account")
                            }
                            else -> Utils.showToast("Failed to fetch token")
                        }

                        linearLayout.removeView(this)
                    }
                })
            }
        }
    }
}