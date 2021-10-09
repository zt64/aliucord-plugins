package com.aliucord.plugins.accountswitcher.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.AccountSwitcher
import com.aliucord.plugins.accountswitcher.Account
import com.aliucord.plugins.accountswitcher.fetchUser
import com.discord.models.domain.auth.ModelLoginResult
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.user.UserUtils

class AccountAdapter(private val fragment: SettingsPage, val accounts: ArrayList<Account>, private val isSettings: Boolean = true) : RecyclerView.Adapter<AccountViewHolder>() {
    private fun saveAccounts() = AccountSwitcher.mSettings.setObject("accounts", accounts)
    fun addAccount(token: String, id: Long): Boolean = accounts.add(Account(token, id)).also { if (it) saveAccounts() }
    fun removeAccount(token: String): Boolean = accounts.removeIf { it.token == token }.also { if (it) saveAccounts() }

    override fun getItemCount() = accounts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AccountViewHolder(this, com.aliucord.widgets.LinearLayout(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }, isSettings
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) = accounts[position].let {
        val user = StoreStream.getUsers().users[it.id]
        holder.name.text = if (user == null) "Failed to load user" else {
            UserUtils.INSTANCE.getUserNameWithDiscriminator(user, null, null)
        }
        IconUtils.setIcon(holder.avatar, user)
    }

    fun onEdit(position: Int) {
        AccountDialog(this, accounts[position]).show(fragment.parentFragmentManager, "Edit Account")
    }

    fun onRemove(position: Int) = accounts[position].let { account ->
        val dialog = ConfirmDialog().setIsDangerous(true)
                .setTitle("Delete ${StoreStream.getUsers().users[account.id]?.username ?: "Unknown"}")
                .setDescription("Are you sure you want to delete this account?")

        dialog.setOnOkListener {
            dialog.dismiss()
            removeAccount(account.token)
            notifyItemRemoved(position)
        }

        dialog.show(fragment.parentFragmentManager, "Confirm Account Removal")
    }

    fun onClick(ctx: Context, position: Int) = accounts[position].let {
        fragment.close()
        Utils.threadPool.execute {
            fetchUser(it.token) ?: return@execute Utils.showToast("Invalid token")

            StoreStream.getAuthentication().handleLoginResult(ModelLoginResult(false, null, it.token, null))
            Utils.mainThread.postDelayed({
                val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
                ctx.startActivity(Intent.makeRestartActivityTask(intent?.component))
                Runtime.getRuntime().exit(0)
            }, 250)
        }
    }
}