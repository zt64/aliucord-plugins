package dev.zt64.aliucord.plugins.favoritechannels.items

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aliucord.Utils
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem

object ChannelListItemFavoriteCategory : ChannelListItem {
    override fun getKey(): String = ""

    override fun getType(): Int = 500
}

class ItemFavoriteCategory(adapter: WidgetChannelsListAdapter) :
    WidgetChannelsListAdapter.Item(Utils.getResId("widget_channels_list_item_category", "layout"), adapter) {
    private val nameView = itemView.findViewById<TextView>(
        Utils.getResId("channels_item_category_name", "id")
    )

    init {
        itemView
            .findViewById<ImageView>(Utils.getResId("channels_item_category_add", "id"))
            .visibility = View.GONE

        itemView
            .findViewById<ImageView>(Utils.getResId("channels_item_category_arrow", "id"))
            .visibility = View.INVISIBLE

        nameView.text = "Favorites"
    }

    override fun onConfigure(i: Int, channelListItem: ChannelListItem) {
        super.onConfigure(i, channelListItem)

        channelListItem as ChannelListItemFavoriteCategory
    }
}