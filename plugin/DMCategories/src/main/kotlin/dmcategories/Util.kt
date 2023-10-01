package dmcategories

import android.content.Context
import android.view.animation.RotateAnimation
import com.aliucord.Utils
import com.discord.stores.StoreStream
import com.discord.views.CheckedSetting
import com.discord.widgets.channels.list.WidgetChannelsListAdapter

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
object Util {
    val expandAnimation: RotateAnimation = WidgetChannelsListAdapter.ItemChannelCategory.Companion.`access$getAnimation`(WidgetChannelsListAdapter.ItemChannelCategory.Companion, true)
    val collapseAnimation: RotateAnimation = WidgetChannelsListAdapter.ItemChannelCategory.Companion.`access$getAnimation`(WidgetChannelsListAdapter.ItemChannelCategory.Companion, false)

    fun getCurrentId() = StoreStream.getUsers().me.id

    fun updateChannels() = StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
        StoreStream.getMessagesMostRecent().markChanged()
    }

    fun createSwitch(context: Context, text: String, subText: String) =
        Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, text, subText)
}