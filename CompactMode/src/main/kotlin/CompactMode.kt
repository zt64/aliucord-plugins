import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.settings.delegate
import com.aliucord.utils.DimenUtils.dp
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemBotComponentRow
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemEmbed
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemEphemeralMessage
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemGameInvite
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemGift
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemInvite
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemReactions
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemSpotifyListenTogether
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemStageInvite
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemSticker
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemUploadProgress
import com.discord.widgets.chat.list.adapter.WidgetChatListItem
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.card.MaterialCardView
import compactmode.PluginSettings

@AliucordPlugin
class CompactMode : Plugin() {
    private val itemAvatarField = WidgetChatListAdapterItemMessage::class.java
        .getDeclaredField("itemAvatar")
        .apply { isAccessible = true }

    private val WidgetChatListAdapterItemMessage.avatarView
        get() = itemAvatarField[this] as ImageView?
    private val WidgetChatListAdapterItemMessage.messageTextView
        get() = WidgetChatListAdapterItemMessage.`access$getItemText$p`(this)

    private val SettingsAPI.contentMargin by settings.delegate(8)
    private val SettingsAPI.avatarScale by settings.delegate(28)
    private val SettingsAPI.headerMargin by settings.delegate(8)
    private val SettingsAPI.messagePadding by settings.delegate(10)
    private val SettingsAPI.hideReplyIcon by settings.delegate(true)
    private val SettingsAPI.hideAvatar by settings.delegate(false)

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val reactionsFlexBoxId = Utils.getResId("chat_list_item_reactions", "id")
        val headerLayoutId = Utils.getResId("chat_list_adapter_item_text_header", "id")
        val guidelineId = Utils.getResId("uikit_chat_guideline", "id")
        val embedContainerCardId = Utils.getResId("chat_list_item_embed_container_card", "id")
        val replyIconViewId = Utils.getResId("chat_list_adapter_item_text_decorator_reply_link_icon", "id")
        val itemTextViewId = Utils.getResId("chat_list_adapter_item_text", "id")
        val componentRowId = Utils.getResId("chat_list_adapter_item_component_root", "id")
        val messageRootId = Utils.getResId("widget_chat_list_adapter_item_text_root", "id")
        val failedMessageRootId = Utils.getResId("chat_list_adapter_item_failed", "id")
        val usernameViewId = Utils.getResId("chat_list_adapter_item_text_name", "id")
        val loadingTextId = Utils.getResId("chat_list_adapter_item_text_loading", "id")

        patcher.after<WidgetChatListItem>("onConfigure", Int::class.java, ChatListEntry::class.java) {
            val contentMargin = settings.contentMargin.dp

            when (this) {
                is WidgetChatListAdapterItemAttachment,
                is WidgetChatListAdapterItemEphemeralMessage -> itemView
                    .findViewById<Guideline>(guidelineId)
                    .setGuidelineBegin(contentMargin)

                is WidgetChatListAdapterItemInvite,
                is WidgetChatListAdapterItemStageInvite,
                is WidgetChatListAdapterItemSticker,
                is WidgetChatListAdapterItemUploadProgress,
                is WidgetChatListAdapterItemGift,
                is WidgetChatListAdapterItemGameInvite,
                is WidgetChatListAdapterItemSpotifyListenTogether -> (itemView.layoutParams as RecyclerView.LayoutParams).marginStart =
                    contentMargin

                is WidgetChatListAdapterItemBotComponentRow -> (itemView.findViewById<LinearLayout>(componentRowId).layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    contentMargin

                is WidgetChatListAdapterItemReactions -> (itemView.findViewById<FlexboxLayout>(reactionsFlexBoxId).layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    contentMargin

                is WidgetChatListAdapterItemEmbed -> (itemView.findViewById<MaterialCardView>(embedContainerCardId).layoutParams as ConstraintLayout.LayoutParams).marginStart =
                    contentMargin
            }
        }

        patcher.after<WidgetChatListAdapterItemMessage>("onConfigure", Int::class.java, ChatListEntry::class.java) {
            val contentMargin = settings.contentMargin.dp

            when (itemView.id) {
                // Regular message
                messageRootId -> {
                    (itemView.findViewById<TextView>(loadingTextId).layoutParams as ConstraintLayout.LayoutParams).apply {
                        marginStart = 0
                    }

                    val headerView = itemView.findViewById<ConstraintLayout>(headerLayoutId)

                    itemView.findViewById<Guideline>(guidelineId).setGuidelineBegin(contentMargin)
                    itemView.setPadding(0, settings.messagePadding.dp, 0, 0)

                    if (settings.hideReplyIcon) {
                        itemView.findViewById<FrameLayout>(replyIconViewId).visibility = View.GONE
                    }
                    if (settings.hideAvatar) {
                        avatarView!!.visibility = View.GONE

                        (headerView.layoutParams as ConstraintLayout.LayoutParams).marginStart = contentMargin

                        return@after
                    }

                    2.dp.let { dp -> avatarView!!.setPadding(dp, dp, dp, dp) }

                    val constraintLayout = itemView as ConstraintLayout
                    val constraintSet = ConstraintSet().apply {
                        clone(constraintLayout)

                        avatarView!!.id.let { id ->
                            clear(id, ConstraintSet.END)

                            settings.avatarScale.dp.let { dp ->
                                constrainWidth(id, dp)
                                constrainHeight(id, dp)
                            }

                            setMargin(id, ConstraintSet.START, settings.headerMargin.dp)

                            connect(id, ConstraintSet.BOTTOM, itemTextViewId, ConstraintSet.TOP)
                            connect(itemTextViewId, ConstraintSet.TOP, id, ConstraintSet.BOTTOM)

                            centerVertically(id, headerLayoutId)
                        }

                        headerView.id.let { id ->
                            setMargin(id, ConstraintSet.START, 4.dp)

                            connect(id, ConstraintSet.START, avatarView!!.id, ConstraintSet.END)
                            connect(id, ConstraintSet.BOTTOM, itemTextViewId, ConstraintSet.TOP)
                        }
                    }

                    constraintSet.applyTo(constraintLayout)
                }
                // Failed message
                failedMessageRootId -> {
                    val root = itemView as RelativeLayout
                    val contentView = root.getChildAt(1) as LinearLayout
                    val nameView = contentView.findViewById<TextView>(usernameViewId)
                    val contentViewLayoutParams = contentView.layoutParams as RelativeLayout.LayoutParams

                    itemView.setPadding(0, settings.messagePadding.dp, 0, 0)
                    (itemView.layoutParams as RecyclerView.LayoutParams).setMargins(0, 0, 0, 0)
                    contentViewLayoutParams.marginStart = contentMargin

                    if (settings.hideAvatar) return@after avatarView!!.setVisibility(View.GONE)

                    root.removeView(avatarView)
                    contentView.removeView(nameView)

                    val headerLayout = LinearLayout(root.context).apply {
                        id = View.generateViewId()
                        orientation = LinearLayout.HORIZONTAL
                        addView(avatarView)
                        addView(nameView)
                    }

                    root.addView(headerLayout, 0)

                    (nameView.layoutParams as LinearLayout.LayoutParams).apply {
                        gravity = Gravity.CENTER_VERTICAL
                        setMargins(4.dp, 0, 0, 0)
                    }

                    contentViewLayoutParams.apply {
                        setMargins(0, 2.dp, 8.dp, 0)
                        removeRule(RelativeLayout.END_OF)
                        addRule(RelativeLayout.BELOW, headerLayout.id)
                    }

                    avatarView!!.apply {
                        2.dp.let { dp -> setPadding(dp, dp, dp, dp) }

                        (layoutParams as LinearLayout.LayoutParams).apply {
                            marginStart = settings.headerMargin.dp
                            marginEnd = 0
                            width = settings.avatarScale.dp
                            height = settings.avatarScale.dp
                        }
                    }
                }
                // Minimal message
                else -> {
                    (messageTextView.layoutParams as ConstraintLayout.LayoutParams).marginStart = contentMargin
                }
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}