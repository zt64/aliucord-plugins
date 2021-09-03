package com.aliucord.plugins

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.utils.DimenUtils
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.discord.databinding.WidgetChannelsListItemChannelBinding
import com.discord.views.typing.TypingDots
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemTextChannel
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.HashMap

@AliucordPlugin
class TypingIndicators : Plugin() {
    private val channels = HashMap<Long, TypingDots>()

    private var channelBinding: Field? = null
    private val WidgetChannelsListAdapter.ItemChannelText.binding: WidgetChannelsListItemChannelBinding
        get() = (channelBinding ?: javaClass.getDeclaredField("binding").apply {
            isAccessible = true
            channelBinding = this
        })[this] as WidgetChannelsListItemChannelBinding

    override fun start(context: Context) {
        val typingDotsId = View.generateViewId()
        val lp = RelativeLayout.LayoutParams(DimenUtils.dpToPx(24), RelativeLayout.LayoutParams.MATCH_PARENT).apply {
            marginEnd = DimenUtils.dpToPx(16)
            addRule(RelativeLayout.ALIGN_PARENT_END)
            addRule(RelativeLayout.CENTER_VERTICAL)
        }

        patcher.patch(WidgetChannelsListAdapter.ItemChannelText::class.java.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, ChannelListItem::class.java), PinePatchFn {
            val textChannel = it.args[1] as ChannelListItemTextChannel
            val itemChannelText = it.thisObject as WidgetChannelsListAdapter.ItemChannelText
            itemChannelText.adapter.getItem(0)

            val root = itemChannelText.binding.root as RelativeLayout
            if (root.findViewById<View?>(typingDotsId) != null) return@PinePatchFn
            val typingDots = TypingDots(Utils.appActivity, null).apply {
                id = typingDotsId
                visibility = View.GONE
            }
            root.addView(typingDots, lp)
            //Observable<ChatTypingModel> observable = ChatTypingModel.Companion.access$getTypingObservableForChannel(ChatTypingModel.Companion, textChannel.getChannel());
//            val subscription = RxUtils.subscribe(observable, RxUtils.createActionSubscriber { longSet: Set<Long?> ->
//                if (longSet.isEmpty()) {
//                    typingDots.b()
//                    typingDots.visibility = View.GONE
//                } else {
//                    typingDots.a(false)
//                    typingDots.visibility = View.VISIBLE
//                }
//            })

            channels[textChannel.channel.id] = typingDots
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}