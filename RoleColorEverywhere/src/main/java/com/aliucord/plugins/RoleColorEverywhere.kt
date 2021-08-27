package com.aliucord.plugins

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.rolecoloreverywhere.PluginSettings
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.discord.databinding.WidgetChannelsListItemVoiceUserBinding
import com.discord.databinding.WidgetChatInputAutocompleteItemBinding
import com.discord.databinding.WidgetChatOverlayBinding
import com.discord.models.member.GuildMember
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.textprocessing.node.UserMentionNode
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemVoiceUser
import com.discord.widgets.chat.input.autocomplete.UserAutocompletable
import com.discord.widgets.chat.input.autocomplete.adapter.AutocompleteItemViewHolder
import com.discord.widgets.chat.overlay.ChatTypingModel
import com.discord.widgets.chat.overlay.WidgetChatOverlay
import com.discord.widgets.chat.overlay.`ChatTypingModel$Companion$getTypingUsers$1$1`
import com.discord.widgets.user.profile.UserProfileHeaderView
import com.discord.widgets.user.profile.UserProfileHeaderViewModel
import com.facebook.drawee.span.SimpleDraweeSpanTextView
import top.canyie.pine.Pine
import top.canyie.pine.callback.MethodHook
import java.lang.reflect.Field

class RoleColorEverywhere : Plugin() {
    private val logger = Logger("RoleColorEverywhere")

    private val typingUsers = HashMap<String, Int>()

    private var typingIndicatorBinding: Field? = null
    private val WidgetChatOverlay.TypingIndicatorViewHolder.binding: WidgetChatOverlayBinding
        get() = (typingIndicatorBinding ?: javaClass.getDeclaredField("binding").apply {
            isAccessible = true
            typingIndicatorBinding = this
        })[this] as WidgetChatOverlayBinding

    private var itemVoiceBinding: Field? = null
    private val WidgetChannelsListAdapter.ItemVoiceUser.binding: WidgetChannelsListItemVoiceUserBinding
        get() = (itemVoiceBinding ?: javaClass.getDeclaredField("binding").apply {
            isAccessible = true
            itemVoiceBinding = this
        })[this] as WidgetChannelsListItemVoiceUserBinding

    private var autoCompleteBinding: Field? = null
    private val AutocompleteItemViewHolder.binding: WidgetChatInputAutocompleteItemBinding
        get() = (autoCompleteBinding ?: javaClass.getDeclaredField("binding").apply {
            isAccessible = true
            autoCompleteBinding = this
        })[this] as WidgetChatInputAutocompleteItemBinding

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Displays the highest role color in more places like mentions and typing text"
            version = "1.2.0"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun start(context: Context) {
        val guildStore = StoreStream.getGuilds()

        if (settings.getBool("typingText", true)) {
            patcher.patch(`ChatTypingModel$Companion$getTypingUsers$1$1`::class.java.getDeclaredMethod("call", Map::class.java, Map::class.java), PinePatchFn {
                typingUsers.clear()

                val channel = StoreStream.getChannelsSelected().selectedChannel

                if (channel.isDM()) return@PinePatchFn

                val users = it.args[0] as Map<Long, User>
                val members = it.args[1] as Map<Long, GuildMember>

                members.forEach { (id, member) ->
                    val color = member.color
                    if (color != Color.BLACK) {
                        typingUsers[GuildMember.getNickOrUsername(member, users[id])] = color
                    }
                }
            })

            patcher.patch(WidgetChatOverlay.TypingIndicatorViewHolder::class.java.getDeclaredMethod("configureTyping", ChatTypingModel.Typing::class.java), PinePatchFn {
                val binding = (it.thisObject as WidgetChatOverlay.TypingIndicatorViewHolder).binding
                val textView = binding.root.findViewById<TextView>(Utils.getResId("chat_typing_users_typing", "id"))

                textView.apply {
                    text = SpannableString(text).apply {
                        typingUsers.forEach { (username, color) ->
                            val start = text.indexOf(username)
                            setSpan(ForegroundColorSpan(color), start, start + username.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            })
        }

        if (settings.getBool("userMentions", true)) {
            patcher.patch(UserMentionNode::class.java.getDeclaredMethod("renderUserMention", SpannableStringBuilder::class.java, UserMentionNode.RenderContext::class.java), object : MethodHook() {
                private var length: Int = 0

                override fun beforeCall(callFrame: Pine.CallFrame) {
                    length = (callFrame.args[0] as SpannableStringBuilder).length
                }

                override fun afterCall(callFrame: Pine.CallFrame) {
                    val userMentionNode = callFrame.thisObject as UserMentionNode<UserMentionNode.RenderContext>
                    val guild = guildStore.getGuild(StoreStream.getGuildSelected().selectedGuildId)
                    val member = guildStore.getMember(guild.id, userMentionNode.userId)

                    var foregroundColor = member.color
                    if (foregroundColor == Color.BLACK) foregroundColor = Color.WHITE

                    val backgroundColor = ColorUtils.setAlphaComponent(ColorUtils.blendARGB(foregroundColor, Color.BLACK, 0.65f), 70)

                    val spannableStringBuilder = callFrame.args[0] as SpannableStringBuilder
                    spannableStringBuilder.setSpan(ForegroundColorSpan(foregroundColor), length, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableStringBuilder.setSpan(BackgroundColorSpan(backgroundColor), length, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            })
        }

        if (settings.getBool("voiceChannel", true)) {
            patcher.patch(WidgetChannelsListAdapter.ItemVoiceUser::class.java.getDeclaredMethod("onConfigure", Int::class.java, ChannelListItem::class.java), PinePatchFn {
                val channelListItemVoiceUser = it.args[1] as ChannelListItemVoiceUser
                val binding = (it.thisObject as WidgetChannelsListAdapter.ItemVoiceUser).binding
                val textView = binding.root.findViewById<TextView>(Utils.getResId("channels_item_voice_user_name", "id"))
                val color = channelListItemVoiceUser.computed.color

                if (color != Color.BLACK) textView.setTextColor(color)
            })
        }

        if (settings.getBool("userMentionList", true)) {
            patcher.patch(AutocompleteItemViewHolder::class.java.getDeclaredMethod("bindUser", UserAutocompletable::class.java), PinePatchFn { callFrame ->
                val userAutocompletable = callFrame.args[0] as UserAutocompletable
                val binding = (callFrame.thisObject as AutocompleteItemViewHolder).binding

                val itemName = binding.root.findViewById<TextView>(Utils.getResId("chat_input_item_name", "id"))
                val color = userAutocompletable.guildMember.color
                if (color != Color.BLACK) itemName.setTextColor(color)
            })
        }

        if (settings.getBool("profileName", true)) {
            patcher.patch(UserProfileHeaderView::class.java.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded::class.java), PinePatchFn { callFrame ->
                val loaded = callFrame.args[0] as UserProfileHeaderViewModel.ViewState.Loaded

                loaded.guildMember?.let {
                    val textView = UserProfileHeaderView.`access$getBinding$p`(callFrame.thisObject as UserProfileHeaderView).root
                            .findViewById<SimpleDraweeSpanTextView>(Utils.getResId("username_text", "id"))

                    textView.apply {
                        if (it.color == Color.BLACK) return@PinePatchFn

                        val end = if (it.nick == null && !settings.getBool("profileTag", true))
                            loaded.user.username.length
                        else
                            i.length

                        i.setSpan(ForegroundColorSpan(it.color), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setDraweeSpanStringBuilder(i)
                    }
                }
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}