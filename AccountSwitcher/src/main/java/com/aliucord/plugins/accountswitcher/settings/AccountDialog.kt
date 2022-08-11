package com.aliucord.plugins.accountswitcher.settings

import android.content.res.ColorStateList
import android.text.*
import android.view.View
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.plugins.accountswitcher.Account
import com.aliucord.plugins.accountswitcher.fetchUser
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R
import java.util.regex.Pattern

class AccountDialog(private val adapter: AccountAdapter, private val account: Account? = null) : InputDialog() {
    private val token = account?.token

    private val buttonStates = arrayOf(
        intArrayOf(android.R.attr.state_enabled), // enabled
        intArrayOf(-android.R.attr.state_enabled) // disabled
    )

    override fun onViewBound(view: View) {
        if (token == null) {
            setTitle("Add Account")
            setDescription("Please input the account token")
        } else setTitle("Edit Account")

        setPlaceholderText("Token")

        setOnOkListener {
            val inputToken = input.trim()

            if (adapter.accounts.any { it != account && it.token == inputToken }) return@setOnOkListener Utils.showToast("An account with this token already exists")

            if (account?.token == inputToken) return@setOnOkListener dismiss()

            Utils.threadPool.execute {
                val userId = fetchUser(inputToken)?.id ?: return@execute Utils.showToast("Invalid token")

                if (account?.token != null) adapter.removeAccount(account.token)

                adapter.addAccount(inputToken, userId)
                StoreStream.getUsers().fetchUsers(listOf(userId))

                Utils.mainThread.post { adapter.notifyItemChanged(adapter.accounts.indexOfFirst { it.token == inputToken }) }

                dismiss()
            }
        }

        super.onViewBound(view)

        if (token != null) {
            inputLayout.editText?.setText(token)
            body.visibility = View.GONE
        }

        okButton.isEnabled = true
        okButton.backgroundTintList = ColorStateList(
            buttonStates, intArrayOf(
                resources.getColor(R.c.uikit_btn_bg_color_selector_brand, context?.theme), // enabled color
                resources.getColor(R.c.uikit_btn_bg_color_selector_secondary, context?.theme) // disabled color
            )
        )

        /*inputLayout.editText?.addTextChangedListener(object : TextWatcher {
            private val pattern = Pattern.compile("(mfa\\.[a-z0-9_-]{20,})|([a-z0-9_-]{23,28}\\.[a-z0-9_-]{6,7}\\.[a-z0-9_-]{27})", Pattern.CASE_INSENSITIVE)

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                okButton.isEnabled = pattern.matcher(s?.trim().toString()).matches()
            }
        })*/
        inputLayout.editText?.inputType = InputType.TYPE_CLASS_TEXT
    }
}
