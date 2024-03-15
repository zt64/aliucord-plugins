package dmcategories.items

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.utils.DimenUtils.dp
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem

object ChannelListItemDivider : ChannelListItem {
    const val TYPE = 401

    override fun getKey(): String = ""

    override fun getType(): Int = TYPE
}

class ItemDivider(
    @LayoutRes i: Int,
    adapter: WidgetChannelsListAdapter
) : WidgetChannelsListAdapter.Item(i, adapter) {
    init {
        (itemView.layoutParams as RecyclerView.LayoutParams).height = 4.dp
    }
}