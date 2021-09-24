package com.aliucord.plugins.accountswitcher.settings

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.AccountSwitcher
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils

class AccountAdapter(private val fragment: SettingsPage, val accounts: LinkedHashMap<String, Long>) : RecyclerView.Adapter<AccountViewHolder>() {
    override fun getItemCount() = accounts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AccountViewHolder(this, com.aliucord.widgets.LinearLayout(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) = accounts.values.elementAt(position).let {
        val user = StoreStream.getUsers().users[it]
        holder.name.text = "${user?.username ?: "Unknown"}#${user?.discriminator ?: "0000"}"
        IconUtils.setIcon(holder.avatar, user)
    }

    fun onEdit(position: Int) {
        Utils.showToast(Utils.appContext, AccountSwitcher.accounts.keys.toString())
        AccountDialog(this, AccountSwitcher.accounts.keys.elementAtOrNull(position)).show(fragment.parentFragmentManager, "Edit Account")
    }

    fun onRemove(position: Int) = AccountSwitcher.accounts.entries.elementAt(position).let { (token, id) ->
        val dialog = ConfirmDialog().setIsDangerous(true)
                .setTitle("Delete ${StoreStream.getUsers().users[id]?.username ?: "Unknown"}")
                .setDescription("Are you sure you want to delete this account?")

        dialog.setOnOkListener {
            AccountSwitcher.removeAccount(token)
            notifyItemRemoved(position)
            dialog.dismiss()
        }

        dialog.show(fragment.parentFragmentManager, "Confirm Account Removal")
    }
}