package tk.zt64.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils.dp
import com.discord.widgets.chat.list.adapter.*
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.card.MaterialCardView
import tk.zt64.plugins.compactmode.PluginSettings

@AliucordPlugin
class CompactMode : Plugin() {
    private val itemAvatarField = WidgetChatListAdapterItemMessage::class.java.getDeclaredField("itemAvatar").apply { isAccessible = true }

    private val WidgetChatListAdapterItemMessage.itemAvatar
        get() = itemAvatarField[this] as? ImageView
    private val WidgetChatListAdapterItemMessage.itemText
        get() = WidgetChatListAdapterItemMessage.`access$getItemText$p`(this)

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val reactionsFlexBoxId = Utils.getResId("chat_list_item_reactions", "id")
        val headerId = Utils.getResId("chat_list_adapter_item_text_header", "id")
        val guidelineId = Utils.getResId("uikit_chat_guideline", "id")
        val embedContainerCardId = Utils.getResId("chat_list_item_embed_container_card", "id")
        val replyIconId = Utils.getResId("chat_list_adapter_item_text_decorator_reply_link_icon", "id")
        val itemTextId = Utils.getResId("chat_list_adapter_item_text", "id")
        val componentRowId = Utils.getResId("chat_list_adapter_item_component_root", "id")

        patcher.after<WidgetChatListItem>("onConfigure", Int::class.java, ChatListEntry::class.java) {
            val contentMargin = settings.getInt("contentMargin", 8).dp

            when (this) {
                is WidgetChatListAdapterItemAttachment -> itemView.findViewById<Guideline>(guidelineId).setGuidelineBegin(contentMargin)
                is WidgetChatListAdapterItemEphemeralMessage -> itemView.findViewById<Guideline>(guidelineId).setGuidelineBegin(contentMargin)
                is WidgetChatListAdapterItemInvite -> (itemView.layoutParams as RecyclerView.LayoutParams).marginStart = contentMargin
                is WidgetChatListAdapterItemStageInvite -> (itemView.layoutParams as RecyclerView.LayoutParams).marginStart = contentMargin
                is WidgetChatListAdapterItemSticker -> (itemView.layoutParams as RecyclerView.LayoutParams).marginStart = contentMargin
                is WidgetChatListAdapterItemUploadProgress -> (itemView.layoutParams as RecyclerView.LayoutParams).marginStart = contentMargin
                is WidgetChatListAdapterItemSpotifyListenTogether -> (itemView.layoutParams as RecyclerView.LayoutParams).marginStart = contentMargin
                is WidgetChatListAdapterItemBotComponentRow -> (itemView.findViewById<LinearLayout>(componentRowId).layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    contentMargin
                is WidgetChatListAdapterItemReactions -> (itemView.findViewById<FlexboxLayout>(reactionsFlexBoxId).layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    contentMargin
                is WidgetChatListAdapterItemEmbed -> (itemView.findViewById<MaterialCardView>(embedContainerCardId).layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    contentMargin
                is WidgetChatListAdapterItemMessage -> {
                    itemView.findViewById<Guideline>(guidelineId)?.setGuidelineBegin(contentMargin) ?: run {
                        return@after (itemText.layoutParams as ConstraintLayout.LayoutParams).run {
                            marginStart = contentMargin
                        }
                    }

                    if (settings.getBool("hideReplyIcon", true)) itemView.findViewById<FrameLayout>(replyIconId).visibility = View.GONE

                    if (settings.getBool("hideAvatar", false)) {
                        itemAvatar?.visibility = View.GONE

                        return@after (itemView.findViewById<ConstraintLayout>(headerId).layoutParams as ConstraintLayout.LayoutParams).run {
                            marginStart = contentMargin
                        }
                    }

                    itemView.setPadding(0, settings.getInt("messagePadding", 10).dp, 0, 0)

                    2.dp.let { dp -> itemAvatar?.setPadding(dp, dp, dp, dp) }

                    val constraintLayout = itemView as ConstraintLayout
                    val constraintSet = ConstraintSet().apply {
                        clone(constraintLayout)

                        itemAvatar?.id?.let { id ->
                            clear(id, ConstraintSet.END)

                            settings.getInt("avatarScale", 28).dp.let { dp ->
                                constrainWidth(id, dp)
                                constrainHeight(id, dp)
                            }

                            setMargin(id, ConstraintSet.START, settings.getInt("headerMargin", 8).dp)

                            connect(id, ConstraintSet.BOTTOM, itemTextId, ConstraintSet.TOP)
                            connect(itemTextId, ConstraintSet.TOP, id, ConstraintSet.BOTTOM)

                            centerVertically(id, headerId)
                        }

                        itemView.findViewById<ConstraintLayout>(headerId)?.id?.let { id ->
                            setMargin(id, ConstraintSet.START, 2.dp)

                            connect(id, ConstraintSet.START, itemAvatar?.id!!, ConstraintSet.END)
                            connect(id, ConstraintSet.BOTTOM, itemTextId, ConstraintSet.TOP)
                        }
                    }

                    constraintSet.applyTo(constraintLayout)
                }
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}