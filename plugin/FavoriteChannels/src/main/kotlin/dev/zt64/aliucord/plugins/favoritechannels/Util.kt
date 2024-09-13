package dev.zt64.aliucord.plugins.favoritechannels

import android.content.Context
import com.aliucord.Utils
import com.discord.stores.StoreStream
import com.discord.views.CheckedSetting

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
object Util {
    // Update the channel list causing a re-render
    fun updateChannels() {
        StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
            StoreStream.getChannels().markChanged()
        }
    }

    fun createSwitch(context: Context, text: String, subText: String): CheckedSetting {
        return Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, text, subText)
    }
}