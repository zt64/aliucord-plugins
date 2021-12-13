package tk.zt64.plugins.pindms.items

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.Divider
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem

class ChannelListItemDivider : ChannelListItem {
    override fun getKey(): String = ""
    override fun getType(): Int = 401
}

class ItemDivider(@LayoutRes i: Int, adapter: WidgetChannelsListAdapter) : WidgetChannelsListAdapter.Item(i, adapter) {
    init {
        itemView.findViewById<TextView>(Utils.getResId("channels_list_item_header", "id")).visibility = View.GONE
        itemView.findViewById<ImageView>(Utils.getResId("channels_list_new", "id")).visibility = View.GONE

        (itemView as RelativeLayout).addView(Divider(itemView.context).apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 2.dp).apply {
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
        })
    }
}