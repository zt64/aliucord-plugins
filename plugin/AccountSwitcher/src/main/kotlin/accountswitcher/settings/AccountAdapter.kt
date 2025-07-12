@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS", "MISSING_DEPENDENCY_SUPERCLASS_WARNING")

package accountswitcher.settings

import AccountSwitcher.Companion.accounts
import accountswitcher.Account
import accountswitcher.fetchUser
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.RxUtils.await
import com.discord.models.domain.auth.ModelLoginResult
import com.discord.models.user.CoreUser
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.rest.RestAPI
import com.discord.utilities.user.UserUtils
import com.aliucord.widgets.LinearLayout as ACLinearLayout

private const val VIEW_TYPE_ACCOUNT = 0
private const val VIEW_TYPE_EMPTY = 1

class AccountAdapter(private val fragment: SettingsPage, private val edit: Boolean = true) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun addAccount(token: String, id: Long) = accounts.put(id, Account(token, id))

    fun removeAccount(id: Long) = accounts.remove(id)

    override fun getItemCount() = if (accounts.isEmpty()) 1 else accounts.size

    override fun getItemViewType(position: Int): Int {
        return if (accounts.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ACCOUNT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val ctx = parent.context
        return when (viewType) {
            VIEW_TYPE_EMPTY -> {
                EmptyViewHolder(ctx)
            }
            else -> {
                AccountViewHolder(
                    adapter = this,
                    layout = ACLinearLayout(ctx).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    },
                    edit = edit
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AccountViewHolder -> {
                val account = accounts.values.elementAt(position)
                val isActive = account.id == StoreStream.getUsers().me.id
                val enabled = edit || !isActive

                holder.itemView.isEnabled = enabled
                holder.itemView.alpha = if (enabled) 1.0f else 0.75f

                Utils.threadPool.execute {
                    val user = StoreStream.getUsers().users[account.id] ?: RestAPI.api
                        .userGet(account.id)
                        .await()
                        .first
                        ?.let(::CoreUser)

                    val name = if (user == null) {
                        "Failed to load user"
                    } else {
                        UserUtils.INSTANCE.getUserNameWithDiscriminator(user, null, null)
                    }

                    Utils.mainThread.post {
                        holder.name.text = name
                        holder.activeIndicator.visibility = if (isActive) View.VISIBLE else View.GONE
                        holder.userId.text = "ID: ${user?.id ?: "Unknown"}"
                        IconUtils.setIcon(holder.avatar, user)
                    }
                }
            }
            is EmptyViewHolder -> {}
        }
    }

    fun onEdit(position: Int) {
        AccountDialog(this, accounts.values.elementAt(position))
            .show(fragment.parentFragmentManager, "Edit Account")
    }

    fun onRemove(position: Int) = accounts.values.elementAt(position).let { account ->
        val dialog = ConfirmDialog()
            .setIsDangerous(true)
            .setTitle("Delete ${StoreStream.getUsers().users[account.id]?.username ?: "Unknown"}")
            .setDescription("Are you sure you want to delete this account?")

        dialog.setOnOkListener {
            dialog.dismiss()
            removeAccount(account.id)
            notifyItemRemoved(position)
        }

        dialog.show(fragment.parentFragmentManager, "Confirm Account Removal")
    }

    fun onClick(ctx: Context, position: Int) = accounts.entries.elementAt(position).let { (id, it) ->
        fragment.close()
        Utils.threadPool.execute {
            if (fetchUser(it.token) == null) return@execute Utils.showToast("Invalid token")

            StoreStream.getAuthentication().handleLoginResult(
                ModelLoginResult(
                    it.token.lowercase().startsWith("mfa"),
                    null,
                    it.token,
                    null,
                    emptyList()
                )
            )

            Utils.mainThread.postDelayed({
                val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
                ctx.startActivity(Intent.makeRestartActivityTask(intent?.component))
                Runtime.getRuntime().exit(0)
            }, 250)
        }
    }
}