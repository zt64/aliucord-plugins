import android.content.Context
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.patcher.component3
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
        ) { (_, _: Any, chatListEntry: ChatListEntry) ->
            when (this) {
                is WidgetChatListAdapterItemEmbed -> (itemView as ConstraintLayout).apply {
                    (layoutParams as RecyclerView.LayoutParams).width = RecyclerView.LayoutParams.MATCH_PARENT

                    val cardLayoutParams = findViewById<MaterialCardView>(containerCardViewId)
                        .layoutParams as ConstraintLayout.LayoutParams
                    cardLayoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    cardLayoutParams.horizontalBias = 0.0f
                    cardLayoutParams.constrainedWidth = true
                }
                is WidgetChatListAdapterItemSticker -> {
                    val itemView = itemView as FrameLayout
                    itemView.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

                    // Apply the margin to the sticker, not the whole chat item.
                    (itemView.layoutParams as RecyclerView.LayoutParams).marginStart = 0

                    val stickerLayoutParams = itemView
                        .findViewById<StickerView>("chat_list_adapter_item_sticker")
                        .layoutParams as FrameLayout.LayoutParams
                    stickerLayoutParams.marginStart = itemView.resources.getDimension(R.d.uikit_guideline_chat).toInt()

                    itemView.setOnLongClickListener {
                        val stickerEntry = chatListEntry as StickerEntry
                        adapter.eventHandler.onMessageLongClicked(
                            stickerEntry.message,
                            "",
                            stickerEntry.message.hasThread()
                        )
                        true
                    }

                    // Keep TapTap sticker handling working.
                    itemView.setOnClickListener {
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