package com.aliucord.plugins.accountswitcher.switchermodal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.plugins.accountswitcher.fetchUser
import com.discord.models.domain.auth.ModelLoginResult
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils

class SwitcherAdapter(private val accounts: LinkedHashMap<String, Long>) : RecyclerView.Adapter<ViewHolder>() {
    private val layoutId = Utils.getResId("widget_user_profile_adapter_item_server", "layout")

    override fun getItemCount() = accounts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(this, layout as RelativeLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = accounts.values.elementAt(position).let {
        val user = StoreStream.getUsers().users[it]
        holder.name.text = "${user?.username ?: "Unknown"}#${user?.discriminator ?: "0000"}"
        IconUtils.setIcon(holder.icon, user)
    }

    fun onClick(ctx: Context, position: Int) = accounts.keys.elementAt(position).let {
        Utils.threadPool.execute {
            fetchUser(it) ?: return@execute Utils.showToast(ctx, "Invalid token")

            StoreStream.getAuthentication().handleLoginResult(ModelLoginResult(false, null, it, null))
            Utils.mainThread.postDelayed({
                val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
                ctx.startActivity(Intent.makeRestartActivityTask(intent?.component))
                Runtime.getRuntime().exit(0)
            }, 250)
        }

    }
}