package tk.zt64.plugins.pindms.items

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.aliucord.Utils
import com.discord.stores.StoreStream
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.lytefast.flexinput.R
import tk.zt64.plugins.PinDMs
import tk.zt64.plugins.pindms.DMGroup
import tk.zt64.plugins.pindms.sheets.GroupSheet

class ChannelListItemDMGroup(val group: DMGroup) : ChannelListItem {
    override fun getKey(): String = ""
    override fun getType(): Int = 400
}

class ItemDMGroup(@LayoutRes i: Int, adapter: WidgetChannelsListAdapter) : WidgetChannelsListAdapter.Item(i, adapter) {
    private val nameView = itemView.findViewById<TextView>(Utils.getResId("channels_item_category_name", "id"))
    private val arrowView = itemView.findViewById<ImageView>(Utils.getResId("channels_item_category_arrow", "id"))
    private val collapsedDrawable = AppCompatResources.getDrawable(adapter.context, R.e.ic_chevron_right_grey_12dp)
    private val openedDrawable = AppCompatResources.getDrawable(adapter.context, R.e.ic_chevron_down_grey_12dp)
    private var isCollapsed = false

    init {
        itemView.findViewById<ImageView>(Utils.getResId("channels_item_category_add", "id")).visibility = View.GONE
    }

    override fun onConfigure(i: Int, channelListItem: ChannelListItem?) {
        super.onConfigure(i, channelListItem)

        channelListItem as ChannelListItemDMGroup

        val group = channelListItem.group

        nameView?.text = group.name

        arrowView.setImageDrawable(if (group.collapsed) collapsedDrawable else openedDrawable)

        if (isCollapsed != group.collapsed) {
            isCollapsed = group.collapsed
            arrowView.startAnimation(
                if (group.collapsed) WidgetChannelsListAdapter.ItemChannelCategory.Companion.`access$getAnimation`(WidgetChannelsListAdapter.ItemChannelCategory.Companion, false)
                else WidgetChannelsListAdapter.ItemChannelCategory.Companion.`access$getAnimation`(WidgetChannelsListAdapter.ItemChannelCategory.Companion, true)
            )
        }

        itemView.setOnClickListener {
            group.collapsed = !group.collapsed

            PinDMs.groups[PinDMs.groups.indexOf(PinDMs.getGroup(group.name))] = group

            StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
                StoreStream.getChannels().markChanged()
            }

            PinDMs.saveGroups()
        }
        itemView.setOnLongClickListener {
            GroupSheet(group).show((itemView.context as AppCompatActivity).supportFragmentManager, "GroupSheet")
            true
        }
    }
}