package com.aliucord.plugins

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.discord.api.utcdatetime.UtcDateTime
import com.discord.databinding.WidgetChannelsListItemChannelBinding
import com.discord.stores.StoreStream
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemTextChannel
import com.lytefast.flexinput.R
import rx.Subscriber
import java.lang.reflect.Field

@AliucordPlugin
class UnreadCounter : Plugin() {
    private var channelBinding: Field = WidgetChannelsListAdapter.ItemChannelText::class.java
        .getDeclaredField("binding")
        .apply { isAccessible = true }
    private val WidgetChannelsListAdapter.ItemChannelText.binding
        get() = channelBinding[this] as WidgetChannelsListItemChannelBinding

    override fun start(context: Context) {
        val unreadCounterId = View.generateViewId()
        val mentionsCounterId = Utils.getResId("channels_item_channel_mentions", "id")

        patcher.after<WidgetChannelsListAdapter.ItemChannelText>(
            "onConfigure",
            Int::class.javaPrimitiveType!!,
            ChannelListItem::class.java
        ) {
            val textChannel = it.args[1] as ChannelListItemTextChannel
            val itemChannelText = it.thisObject as WidgetChannelsListAdapter.ItemChannelText
            val root = itemChannelText.binding.root as RelativeLayout

            val counter = root.findViewById(unreadCounterId) ?: TextView(
                root.context,
                null,
                0,
                R.i.Icon_Mentions_Large
            ).apply {
                id = unreadCounterId
                visibility = View.GONE
                background =
                    ContextCompat.getDrawable(root.context, R.e.drawable_overlay_mentions)!!
                        .mutate().apply {
                            setTint(Color.GRAY)
                        }
                layoutParams = RelativeLayout.LayoutParams(20.dp, 20.dp).apply {
                    alignWithParent = true
                    minWidth = 16.dp
                    marginStart = 8.dp
                    marginEnd = 16.dp
                    addRule(
                        RelativeLayout.START_OF,
                        root.findViewById<TextView>(mentionsCounterId).id
                    )
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                root.addView(this)
            }

//            counter.visibility = if (this > 0) View.VISIBLE else View.GONE
//            counter.text = this.toString()
            StoreStream.getReadStates()
                .observeUnreadCountForChannel(
                    textChannel.channel.id,
                    UtcDateTime(System.currentTimeMillis())
                )
                .subscribe(object : Subscriber<Int>() {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {}

                    override fun onNext(count: Int) {
                        unsubscribe()
                        Utils.showToast("${textChannel.channel.name} ${textChannel.isUnread} $count")
                        counter.visibility = if (count > 0) View.VISIBLE else View.GONE
                        counter.text = count.toString()
                    }
                })
        }

//        patcher.after<StoreStream>("handleMessageCreate", Message::class.java) {
//            val message = com.discord.models.message.Message(it.args[0] as Message)
//
//            if (message.guildId != StoreStream.getGuildSelected().selectedGuildId) return@patch
//
//            StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
//                StoreStream.getChannels().markChanged()
//            }
//        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}