package com.aliucord.plugins.accountswitcher

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.aliucord.views.Divider
import com.lytefast.flexinput.R
import java.util.*

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Account Switcher")

        val ctx = requireContext()

        // Map<String, String> accounts = settings.getObject("accounts", null);
        val accounts = HashMap<String, String>()
        accounts["user1"] = "token1"
        accounts["user2"] = "token2"
        val addAccountBtn = Button(ctx).apply {
            text = "Add Account"
            setOnClickListener { Utils.showToast(ctx, "Add account") }
        }
        addView(addAccountBtn)
        addView(Divider(ctx))
        val accountsHeader = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Header).apply { text = "Accounts" }

        val accountsList = RecyclerView(ctx).apply {
            layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        }
        accounts.forEach { (name: String?, token: String?) -> accountsList.addView(makeAccountItem(ctx, name, token)) }
        addView(accountsHeader)
        addView(accountsList)
    }

    private fun makeAccountItem(ctx: Context?, name: String?, token: String?): ConstraintLayout {
        val accountItem = ConstraintLayout(ctx!!)
        val nameText = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Label)
        nameText.text = name
        nameText.id = View.generateViewId()
        val tokenText = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_SubText)
        tokenText.text = token
        tokenText.id = View.generateViewId()
        val editButton = Button(ctx)
        editButton.id = View.generateViewId()
        accountItem.addView(editButton)
        val deleteButton = Button(ctx)
        deleteButton.id = View.generateViewId()
        accountItem.addView(deleteButton)
        accountItem.addView(nameText)
        accountItem.addView(tokenText)
        val constraintSet = ConstraintSet()
        constraintSet.connect(nameText.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        //        constraintSet.connect(nameText.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
//        constraintSet.connect(nameText.getId(), ConstraintSet.BOTTOM, tokenText.getId(), ConstraintSet.TOP);
        constraintSet.applyTo(accountItem)
        return accountItem
    }
}