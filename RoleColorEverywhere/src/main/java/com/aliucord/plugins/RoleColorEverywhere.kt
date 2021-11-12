package com.aliucord.plugins

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.plugins.rolecoloreverywhere.PluginSettings
import com.aliucord.plugins.rolecoloreverywhere.ReflectionExtensions
import com.aliucord.plugins.rolecoloreverywhere.ReflectionExtensions.binding
import com.aliucord.plugins.rolecoloreverywhere.ReflectionExtensions.mDraweeStringBuilder
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.discord.models.member.GuildMember
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.mg_recycler.MGRecyclerDataPayload
import com.discord.utilities.textprocessing.FontColorSpan
import com.discord.utilities.textprocessing.node.UserMentionNode
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemVoiceUser
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListAdapter
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember
import com.discord.widgets.chat.input.autocomplete.*
import com.discord.widgets.chat.input.autocomplete.adapter.AutocompleteItemViewHolder
import com.discord.widgets.chat.input.models.MentionInputModel
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.MessageEntry
import com.discord.widgets.chat.managereactions.ManageReactionsResultsAdapter
import com.discord.widgets.chat.overlay.ChatTypingModel
import com.discord.widgets.chat.overlay.WidgetChatOverlay
import com.discord.widgets.chat.overlay.`ChatTypingModel$Companion$getTypingUsers$1$1`
import com.discord.widgets.user.profile.UserProfileHeaderView
import com.discord.widgets.user.profile.UserProfileHeaderViewModel
import com.discord.widgets.voice.fullscreen.stage.AudienceViewHolder
import com.discord.widgets.voice.fullscreen.stage.SpeakerViewHolder
import com.discord.widgets.voice.fullscreen.stage.StageCallItem
import com.discord.widgets.voice.sheet.CallParticipantsAdapter
import de.robv.android.xposed.XC_MethodHook
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@AliucordPlugin
class RoleColorEverywhere : Plugin() {
    private val typingUsers = HashMap<String, Int>()

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    @Suppress("UNCHECKED_CAST")
    override fun start(context: Context) {
        ReflectionExtensions.init()

        val guildStore = StoreStream.getGuilds()

        if (settings.getBool("typingText", true)) {
            val typingUsersTextViewId = Utils.getResId("chat_typing_users_typing", "id")

            patcher.patch(`ChatTypingModel$Companion$getTypingUsers$1$1`::class.java.getDeclaredMethod("call", Map::class.java, Map::class.java), Hook {
                typingUsers.clear()

                if (StoreStream.getChannelsSelected().selectedChannel.isDM()) return@Hook

                val users = it.args[0] as Map<Long, User>
                val members = it.args[1] as Map<Long, GuildMember>

                members.forEach { (id, member) ->
                    val color = member.color
                    if (color != Color.BLACK) {
                        typingUsers[GuildMember.getNickOrUsername(member, users[id])] = color
                    }
                }
            })

            patcher.patch(WidgetChatOverlay.TypingIndicatorViewHolder::class.java.getDeclaredMethod("configureTyping", ChatTypingModel.Typing::class.java), Hook {
                val binding = (it.thisObject as WidgetChatOverlay.TypingIndicatorViewHolder).binding
                val textView = binding.root.findViewById<TextView>(typingUsersTextViewId)

                textView.apply {
                    text = SpannableString(text).apply {
                        typingUsers.forEach { (username, color) ->
                            val start = text.indexOf(username)
                            if (start != -1) setSpan(ForegroundColorSpan(color), start, start + username.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            })
        }

        if (settings.getBool("userMentions", true)) {
            patcher.patch(UserMentionNode::class.java.getDeclaredMethod("renderUserMention", SpannableStringBuilder::class.java, UserMentionNode.RenderContext::class.java), object : XC_MethodHook() {
                private var mentionLength: Int = 0

                override fun beforeHookedMethod(param: MethodHookParam) {
                    mentionLength = (param.args[0] as SpannableStringBuilder).length
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    val userMentionNode = param.thisObject as UserMentionNode<UserMentionNode.RenderContext>
                    val guild = guildStore.getGuild(StoreStream.getGuildSelected().selectedGuildId)
                    val member = guildStore.getMember(guild.id, userMentionNode.userId) ?: return

                    val foregroundColor = if (member.color == Color.BLACK) Color.WHITE else member.color
                    val backgroundColor = ColorUtils.setAlphaComponent(ColorUtils.blendARGB(foregroundColor, Color.BLACK, 0.65f), 70)

                    with(param.args[0] as SpannableStringBuilder) {
                        setSpan(ForegroundColorSpan(foregroundColor), mentionLength, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(BackgroundColorSpan(backgroundColor), mentionLength, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            })

            patcher.patch(AutocompleteViewModel::class.java.getDeclaredMethod("generateSpanUpdates", MentionInputModel::class.java), Hook {
                val res = it.result as InputEditTextAction.ReplaceCharacterStyleSpans
                val mentionInputModel = it.args[0] as MentionInputModel

                mentionInputModel.inputMentionsMap.forEach { (k, v) ->
                    if (v !is UserAutocompletable) return@Hook

                    val color = v.guildMember?.color ?: return@Hook
                    if (color != Color.BLACK) res.spans[k] = listOf(FontColorSpan(color), StyleSpan(1))
                }
            })
        }

        if (settings.getBool("voiceChannel", true)) {
            val voiceUserNameId = Utils.getResId("channels_item_voice_user_name", "id")
            val voiceUserListId = Utils.getResId("voice_user_list_item_user_name", "id")
            val stageAudienceNameId = Utils.getResId("voice_user_list_item_user_name", "id")
            val stageSpeakerNameId = Utils.getResId("stage_channel_audience_member_name", "id")

            patcher.patch(WidgetChannelsListAdapter.ItemVoiceUser::class.java.getDeclaredMethod("onConfigure", Int::class.java, ChannelListItem::class.java), Hook {
                val channelListItemVoiceUser = it.args[1] as ChannelListItemVoiceUser
                val color = channelListItemVoiceUser.computed.color

                if (color != Color.BLACK) {
                    val root = (it.thisObject as WidgetChannelsListAdapter.ItemVoiceUser).binding.root
                    root.findViewById<TextView>(voiceUserNameId).setTextColor(color)
                }
            })

            patcher.patch(CallParticipantsAdapter.ViewHolderUser::class.java.getDeclaredMethod("onConfigure", Int::class.java, MGRecyclerDataPayload::class.java), Hook {
                val callParticipant = it.args[1] as CallParticipantsAdapter.ListItem.VoiceUser
                val color = callParticipant.participant.guildMember.color

                if (color != Color.BLACK) {
                    val root = (it.thisObject as CallParticipantsAdapter.ViewHolderUser).binding.root
                    root.findViewById<TextView>(voiceUserListId).setTextColor(color)
                }
            })

            patcher.patch(AudienceViewHolder::class.java.getDeclaredMethod("onConfigure", Int::class.java, StageCallItem::class.java), Hook {
                val stageCallItem = it.args[1] as StageCallItem.AudienceItem
                val color = stageCallItem.voiceUser.guildMember.color

                if (color != Color.BLACK) {
                    val root = (it.thisObject as AudienceViewHolder).binding.root
                    root.findViewById<TextView>(stageAudienceNameId).setTextColor(color)
                }
            })

            patcher.patch(SpeakerViewHolder::class.java.getDeclaredMethod("onConfigure", Int::class.java, StageCallItem::class.java), Hook {
                val stageCallItem = it.args[1] as StageCallItem.SpeakerItem
                val color = stageCallItem.voiceUser.guildMember.color

                if (color != Color.BLACK) {
                    val root = (it.thisObject as SpeakerViewHolder).binding.root
                    root.findViewById<TextView>(stageSpeakerNameId).setTextColor(color)
                }
            })
        }

        if (settings.getBool("userMentionList", true)) {
            val mentionNameId = Utils.getResId("chat_input_item_name", "id")

            patcher.patch(AutocompleteItemViewHolder::class.java.getDeclaredMethod("bindUser", UserAutocompletable::class.java), Hook {
                val userAutocompletable = it.args[0] as UserAutocompletable
                val color = userAutocompletable.guildMember?.color ?: return@Hook

                if (color != Color.BLACK) {
                    val root = (it.thisObject as AutocompleteItemViewHolder).binding.root
                    root.findViewById<TextView>(mentionNameId).setTextColor(color)
                }
            })
        }

        if (settings.getBool("profileName", true)) {
            val usernameTextId = Utils.getResId("username_text", "id")
            patcher.patch(UserProfileHeaderView::class.java.getDeclaredMethod("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded::class.java), Hook {
                val loaded = it.args[0] as UserProfileHeaderViewModel.ViewState.Loaded
                val guildMember = loaded.guildMember ?: return@Hook

                if (guildMember.color != Color.BLACK) {
                    val textView = UserProfileHeaderView.`access$getBinding$p`(it.thisObject as UserProfileHeaderView).root
                            .findViewById<com.facebook.drawee.span.SimpleDraweeSpanTextView>(usernameTextId)

                    textView.apply {
                        val end = if (guildMember.nick == null && !settings.getBool("profileTag", true))
                            loaded.user.username.length
                        else
                            i.length

                        i.setSpan(ForegroundColorSpan(guildMember.color), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setDraweeSpanStringBuilder(i)
                    }
                }
            })
        }

        if (settings.getBool("messages", false)) {
            patcher.patch(WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("processMessageText", SimpleDraweeSpanTextView::class.java, MessageEntry::class.java), Hook {
                val messageEntry = it.args[1] as MessageEntry
                val member = messageEntry.author ?: return@Hook

                if (member.color != Color.BLACK) {
                    val textView = it.args[0] as SimpleDraweeSpanTextView
                    textView.mDraweeStringBuilder?.apply {
                        setSpan(ForegroundColorSpan(member.color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        textView.setDraweeSpanStringBuilder(this)
                    }
                }
            })
        }

        if (settings.getBool("status", true)) {
            val statusGameId = Utils.getResId("channel_members_list_item_game", "id")
            val customStatusId = Utils.getResId("user_profile_header_custom_status", "id")

            patcher.patch(ChannelMembersListViewHolderMember::class.java.getDeclaredMethod("bind", ChannelMembersListAdapter.Item.Member::class.java, Function0::class.java), Hook {
                val member = it.args[0] as ChannelMembersListAdapter.Item.Member
                val color = member.color ?: return@Hook

                if (color != Color.BLACK) {
                    val root = (it.thisObject as ChannelMembersListViewHolderMember).binding.root
                    val textView = root.findViewById<SimpleDraweeSpanTextView>(statusGameId)

                    textView.mDraweeStringBuilder?.apply {
                        setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        textView.setDraweeSpanStringBuilder(this)
                    }
                }
            })

            patcher.patch(UserProfileHeaderView::class.java.getDeclaredMethod("updateViewState", UserProfileHeaderViewModel.ViewState.Loaded::class.java), Hook {
                val guildMember = (it.args[0] as UserProfileHeaderViewModel.ViewState.Loaded).guildMember ?: return@Hook

                if (guildMember.color != Color.BLACK) {
                    val textView = UserProfileHeaderView.`access$getBinding$p`(it.thisObject as UserProfileHeaderView).root
                            .findViewById<SimpleDraweeSpanTextView>(customStatusId)

                    textView.mDraweeStringBuilder?.apply {
                        setSpan(ForegroundColorSpan(guildMember.color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        textView.setDraweeSpanStringBuilder(this)
                    }
                }
            })
        }

        if (settings.getBool("reactionList", true)) {
            val reactionUsersTextViewId = Utils.getResId("manage_reactions_result_user_name", "id")

            patcher.patch(ManageReactionsResultsAdapter.ReactionUserViewHolder::class.java.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, MGRecyclerDataPayload::class.java), Hook {
                if (it.args[1] !is ManageReactionsResultsAdapter.ReactionUserItem) return@Hook

                val reactionItem = it.args[1] as ManageReactionsResultsAdapter.ReactionUserItem
                val color = reactionItem.guildMember.color

                if (color != Color.BLACK) {
                    val root = (it.thisObject as ManageReactionsResultsAdapter.ReactionUserViewHolder).binding.root
                    root.findViewById<TextView>(reactionUsersTextViewId).setTextColor(color)
                }
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}