import android.content.Context
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.ViewUtils.findViewById
import com.discord.views.sticker.StickerView
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemEmbed
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemSticker
import com.discord.widgets.chat.list.adapter.WidgetChatListItem
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.list.entries.StickerEntry
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R

@AliucordPlugin
class MessageWidthFix : Plugin() {
    override fun start(context: Context) {
        val containerCardViewId = Utils.getResId("chat_list_item_embed_container_card", "id")

        patcher.after<WidgetChatListItem>(
            "onConfigure",
            Int::class.java,
            ChatListEntry::class.java
        ) {
            val chatListEntry = it.args[1] as ChatListEntry

            when (this) {
                is WidgetChatListAdapterItemEmbed -> (itemView as ConstraintLayout).run {
                    (layoutParams as RecyclerView.LayoutParams).apply {
                        width = RecyclerView.LayoutParams.MATCH_PARENT
                    }

                    (
                        findViewById<MaterialCardView>(
                            containerCardViewId
                        ).layoutParams as ConstraintLayout.LayoutParams
                    ).apply {
                        width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                        horizontalBias = 0.0f
                        constrainedWidth = true
                    }
                }

                is WidgetChatListAdapterItemSticker -> (itemView as FrameLayout).run {
                    layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                    // apply margin to sticker view, not the whole chat item view
                    (layoutParams as RecyclerView.LayoutParams).apply { marginStart = 0 }
                    val stickerView = itemView.findViewById<StickerView>("chat_list_adapter_item_sticker")
                    val chatDimension = itemView.resources.getDimension(R.d.uikit_guideline_chat).toInt()
                    (stickerView.layoutParams as FrameLayout.LayoutParams).apply { marginStart = chatDimension }

                    // Discord devs too lazy to do it themselves
                    setOnLongClickListener {
                        val stickerEntry = chatListEntry as StickerEntry
                        adapter.eventHandler.onMessageLongClicked(
                            stickerEntry.message,
                            "",
                            stickerEntry.message.hasThread()
                        )
                        true
                    }
                    // for TapTap to work with stickers
                    setOnClickListener {
                        val stickerEntry = chatListEntry as StickerEntry
                        adapter.eventHandler.onMessageClicked(
                            stickerEntry.message,
                            stickerEntry.message.hasThread()
                        )
                    }
                }
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}