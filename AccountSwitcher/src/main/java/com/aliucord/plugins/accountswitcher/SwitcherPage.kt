package com.aliucord.plugins.accountswitcher

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
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.accountswitcher.settings.AccountAdapter
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R

class SwitcherPage(private val accounts: ArrayList<Account>): SettingsPage() {
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Account Switcher")
        setActionBarSubtitle(Utils.pluralise(accounts.size, "account"))

        val ctx = requireContext()

        RecyclerView(ctx).apply {
            adapter = AccountAdapter(this@SwitcherPage, accounts, false)
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

        if (StoreStream.getAuthentication().isAuthed) {
            addView(Button(ctx).apply {
                text = "Log Out"
                setBackgroundColor(view.resources.getColor(R.c.uikit_btn_bg_color_selector_red, view.context.theme))
                setOnClickListener { StoreStream.getAuthentication().logout() }
            })
        }
    }
}