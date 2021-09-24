package com.aliucord.plugins.accountswitcher.settings

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.aliucord.plugins.AccountSwitcher
import com.aliucord.plugins.accountswitcher.fetchUser
import com.lytefast.flexinput.R
import java.util.regex.Pattern

class AccountDialog(private val adapter: AccountAdapter, private val token: String? = null): InputDialog() {
    private val accounts = adapter.accounts.apply { remove(token) }

    private val buttonStates = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled) // disabled
    )

    override fun onViewBound(view: View) {
        if (token == null) {
            setTitle("Add Account")
            setDescription("Please input the account token")
        } else {
            setTitle("Edit Account")
        }

        setPlaceholderText("Token")

        setOnOkListener {
            val inputToken = input.trim()

            if (accounts.containsKey(inputToken)) {
                return@setOnOkListener Utils.showToast(context, "An account with this token already exists")
            }

            Utils.threadPool.execute {
                if (token == inputToken) return@execute dismiss()
                val id = fetchUser(inputToken)?.id ?: return@execute Utils.showToast(Utils.appContext, "Invalid Token")

                if (token != null) AccountSwitcher.removeAccount(token)

                AccountSwitcher.addAccount(inputToken, id)
                Utils.mainThread.post { adapter.notifyItemChanged(accounts.keys.indexOf(inputToken)) }

                dismiss()
            }
        }

        super.onViewBound(view)

        if (token != null) {
            inputLayout.editText?.setText(token)
            body.visibility = View.GONE
        }

        okButton.isEnabled = token != null
        okButton.backgroundTintList = ColorStateList(buttonStates, intArrayOf(
                resources.getColor(R.c.uikit_btn_bg_color_selector_brand, context?.theme), // enabled color
                resources.getColor(R.c.uikit_btn_bg_color_selector_secondary, context?.theme) // disabled color
        ))

        inputLayout.editText?.addTextChangedListener(object: TextWatcher {
            private val pattern = Pattern.compile("(mfa\\.[a-z0-9_-]{20,})|([a-z0-9_-]{23,28}\\.[a-z0-9_-]{6,7}\\.[a-z0-9_-]{27})", Pattern.CASE_INSENSITIVE)

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                okButton.isEnabled = pattern.matcher(s?.trim().toString()).matches()
            }
        })
    }
}