package tk.zt64.plugins.dmcategories

import android.view.animation.RotateAnimation
import com.discord.stores.StoreStream
import com.discord.widgets.channels.list.WidgetChannelsListAdapter

object Util {
    fun getCurrentId() = StoreStream.getUsers().me.id

    fun updateChannels() = StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
        StoreStream.getMessagesMostRecent().markChanged()
    }

    val expandAnimation: RotateAnimation = WidgetChannelsListAdapter.ItemChannelCategory.Companion.`access$getAnimation`(WidgetChannelsListAdapter.ItemChannelCategory.Companion, true)
    val collapseAnimation: RotateAnimation = WidgetChannelsListAdapter.ItemChannelCategory.Companion.`access$getAnimation`(WidgetChannelsListAdapter.ItemChannelCategory.Companion, false)
}