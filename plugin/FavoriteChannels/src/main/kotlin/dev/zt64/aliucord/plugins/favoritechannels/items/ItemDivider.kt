package dev.zt64.aliucord.plugins.favoritechannels.items

import com.aliucord.Utils
import com.aliucord.utils.DimenUtils.dp
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem

object ChannelListItemDivider : ChannelListItem {
    override fun getKey(): String = ""

    override fun getType(): Int = 501
}

class ItemDivider(adapter: WidgetChannelsListAdapter) :
    WidgetChannelsListAdapter.Item(Utils.getResId("widget_channels_list_item_stage_events_separator", "layout"), adapter) {
        init {
            itemView.layoutParams.height = 2.dp
        }
    }