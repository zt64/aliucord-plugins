package com.aliucord.plugins.accountswitcher.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.AccountSwitcher
import com.aliucord.plugins.accountswitcher.authToken
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.aliucord.views.Divider
import com.discord.stores.StoreStream

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Account Switcher")

        val ctx = requireContext()

        val recycler = RecyclerView(ctx).apply {
            adapter = AccountAdapter(this@PluginSettings, AccountSwitcher.accounts)
            layoutManager = LinearLayoutManager(ctx)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                weight = 1f
            }

            addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ShapeDrawable(RectShape()).apply {
                    intrinsicHeight = DimenUtils.getDefaultPadding()
                    setTint(Color.TRANSPARENT)
                })
            })

            linearLayout.addView(this)
        }

        addView(Divider(ctx))

        addView(Button(ctx).apply {
            text = "Add Account"
            setOnClickListener {
                AccountDialog(recycler.adapter as AccountAdapter).show(parentFragmentManager, "Add Account")
            }
        })

        if (StoreStream.getAuthentication().isAuthed) {
            val token = StoreStream.getAuthentication().authToken

            if (!AccountSwitcher.accounts.containsKey(token)) {
                addView(Button(ctx).apply {
                    text = "Add Current Account"
                    setOnClickListener {
                        AccountSwitcher.addAccount(token, StoreStream.getUsers().me.id)
                        Utils.showToast(context, "Added Current Account")
                        linearLayout.removeView(this)
                    }
                })
            }
        }
    }
}