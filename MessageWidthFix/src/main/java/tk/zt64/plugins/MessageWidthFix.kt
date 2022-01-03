package tk.zt64.plugins

import android.content.Context
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemEmbed
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemSticker
import com.discord.widgets.chat.list.adapter.WidgetChatListItem
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.list.entries.StickerEntry
import com.google.android.material.card.MaterialCardView

@AliucordPlugin
class MessageWidthFix : Plugin() {
    override fun start(context: Context) {
        val containerCardViewId = Utils.getResId("chat_list_item_embed_container_card", "id")

        patcher.after<WidgetChatListItem>("onConfigure", Int::class.java, ChatListEntry::class.java) {
            val chatListEntry = it.args[1] as ChatListEntry

            when (this) {
                is WidgetChatListAdapterItemEmbed -> {
                    (itemView as ConstraintLayout).layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                    (itemView.findViewById<MaterialCardView>(containerCardViewId).layoutParams as ConstraintLayout.LayoutParams).apply {
                        width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                        endToEnd = ConstraintLayout.LayoutParams.UNSET
                    }
                }
                is WidgetChatListAdapterItemSticker -> (itemView as FrameLayout).run {
                    layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                    // Discord devs too lazy to do it themselves
                    setOnLongClickListener {
                        val stickerEntry = chatListEntry as StickerEntry
                        adapter.eventHandler.onMessageLongClicked(stickerEntry.message, "", stickerEntry.message.hasThread())
                        true
                    }
                }
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}