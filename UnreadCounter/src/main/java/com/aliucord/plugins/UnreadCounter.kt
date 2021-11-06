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
import com.aliucord.patcher.Hook
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.discord.api.message.Message
import com.discord.databinding.WidgetChannelsListItemChannelBinding
import com.discord.stores.StoreStream
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemTextChannel
import com.lytefast.flexinput.R
import rx.Subscription
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicReference

@AliucordPlugin
class UnreadCounter : Plugin() {
    private var channelBinding: Field = WidgetChannelsListAdapter.ItemChannelText::class.java.getDeclaredField("binding").apply {
        isAccessible = true
    }
    private val WidgetChannelsListAdapter.ItemChannelText.binding
        get() = channelBinding[this] as WidgetChannelsListItemChannelBinding

    override fun start(context: Context) {
        val unreadCounterId = View.generateViewId()
        val mentionsCounterId = Utils.getResId("channels_item_channel_mentions", "id")

        patcher.patch(WidgetChannelsListAdapter.ItemChannelText::class.java.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, ChannelListItem::class.java), Hook {
            val textChannel = it.args[1] as ChannelListItemTextChannel
            val itemChannelText = it.thisObject as WidgetChannelsListAdapter.ItemChannelText
            val root = itemChannelText.binding.root as RelativeLayout

            val counter = root.findViewById(unreadCounterId) ?: TextView(root.context, null, 0, R.i.Icon_Mentions_Large).apply {
                id = unreadCounterId
                visibility = View.GONE
                background = ContextCompat.getDrawable(root.context, R.e.drawable_overlay_mentions)!!.mutate().apply {
                    setTint(Color.GRAY)
                }
                layoutParams = RelativeLayout.LayoutParams(20.dp, 20.dp).apply {
                    alignWithParent = true
                    minWidth = 16.dp
                    marginStart = 8.dp
                    marginEnd = 16.dp
                    addRule(RelativeLayout.START_OF, root.findViewById<TextView>(mentionsCounterId).id)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                root.addView(this)
            }

            val subscriptionReference = AtomicReference<Subscription>()
            subscriptionReference.set(StoreStream.getReadStates().getUnreadMarker(textChannel.channel.id).subscribe(createActionSubscriber({ unread ->
                subscriptionReference.get()?.unsubscribe()
                counter.visibility = if (unread.count > 0) View.VISIBLE else View.GONE
                counter.text = unread.count.toString()
            })))
        })

        patcher.patch(StoreStream::class.java.getDeclaredMethod("handleMessageCreate", Message::class.java), Hook {
            val message = com.discord.models.message.Message(it.args[0] as Message)

            if (message.guildId != StoreStream.getGuildSelected().selectedGuildId) return@Hook

            StoreStream.getChannels().markChanged()
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}