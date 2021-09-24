package com.aliucord.plugins.accountswitcher.switchermodal

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.facebook.drawee.view.SimpleDraweeView

class ViewHolder(private val adapter: SwitcherAdapter, layout: RelativeLayout) : RecyclerView.ViewHolder(layout), View.OnClickListener {
    private val iconId = Utils.getResId("user_profile_adapter_item_server_image", "id")
    private val iconTextId = Utils.getResId("user_profile_adapter_item_server_text", "id")
    private val serverNameId = Utils.getResId("user_profile_adapter_item_server_name", "id")
    private val serverNickId = Utils.getResId("user_profile_adapter_item_server_nick", "id")

    val icon = layout.findViewById(iconId) as SimpleDraweeView
    val name = layout.findViewById(serverNameId) as TextView

    init {
        layout.findViewById<View>(serverNickId).visibility = View.GONE
        layout.findViewById<View>(iconTextId).visibility = View.GONE
        layout.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        adapter.onClick(view.context, adapterPosition)
    }
}