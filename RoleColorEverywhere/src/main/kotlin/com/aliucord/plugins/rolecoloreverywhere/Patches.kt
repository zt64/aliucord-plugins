package com.aliucord.plugins.rolecoloreverywhere

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.aliucord.Utils
import com.aliucord.api.PatcherAPI
import com.aliucord.patcher.after
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.mg_recycler.MGRecyclerDataPayload
import com.discord.utilities.textprocessing.FontColorSpan
import com.discord.utilities.textprocessing.node.UserMentionNode
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemVoiceUser
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListAdapter
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember
import com.discord.widgets.chat.input.autocomplete.AutocompleteViewModel
import com.discord.widgets.chat.input.autocomplete.InputEditTextAction
import com.discord.widgets.chat.input.autocomplete.UserAutocompletable
import com.discord.widgets.chat.input.autocomplete.adapter.AutocompleteItemViewHolder
import com.discord.widgets.chat.input.models.MentionInputModel
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.MessageEntry
import com.discord.widgets.chat.managereactions.ManageReactionsResultsAdapter
import com.discord.widgets.user.profile.UserProfileHeaderView
import com.discord.widgets.user.profile.UserProfileHeaderViewModel
import com.discord.widgets.voice.fullscreen.stage.AudienceViewHolder
import com.discord.widgets.voice.fullscreen.stage.SpeakerViewHolder
import com.discord.widgets.voice.fullscreen.stage.StageCallItem
import com.discord.widgets.voice.sheet.CallParticipantsAdapter
import com.lytefast.flexinput.R
import de.robv.android.xposed.XC_MethodHook

fun PatcherAPI.patchMentions() {
    val guildStore = StoreStream.getGuilds()

    patch(UserMentionNode::class.java.getDeclaredMethod("renderUserMention", SpannableStringBuilder::class.java, UserMentionNode.RenderContext::class.java), object : XC_MethodHook() {
        private var mentionLength: Int = 0

        override fun beforeHookedMethod(param: MethodHookParam) {
            mentionLength = (param.args[0] as SpannableStringBuilder).length
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            @Suppress("UNCHECKED_CAST")
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


    after<AutocompleteViewModel>("generateSpanUpdates", MentionInputModel::class.java) {
        val res = it.result as InputEditTextAction.ReplaceCharacterStyleSpans
        val mentionInputModel = it.args[0] as MentionInputModel

        mentionInputModel.inputMentionsMap.forEach { (k, v) ->
            if (v !is UserAutocompletable) return@after

            val color = v.guildMember?.color ?: return@after
            if (color != Color.BLACK) res.spans[k] = listOf(FontColorSpan(color), StyleSpan(1))
        }
    }
}

fun PatcherAPI.patchVoiceChannels() {
    val voiceUserNameId = Utils.getResId("channels_item_voice_user_name", "id")
    val voiceUserListId = Utils.getResId("voice_user_list_item_user_name", "id")
    val stageAudienceNameId = Utils.getResId("voice_user_list_item_user_name", "id")
    val stageSpeakerNameId = Utils.getResId("stage_channel_audience_member_name", "id")

    after<WidgetChannelsListAdapter.ItemVoiceUser>("onConfigure", Int::class.java, ChannelListItem::class.java) {
        val channelListItemVoiceUser = it.args[1] as ChannelListItemVoiceUser
        val color = channelListItemVoiceUser.computed.color
        val ctx = binding.root.context

        binding.root.findViewById<TextView>(voiceUserNameId).setTextColor(
            if (color == Color.BLACK)
                ColorCompat.getThemedColor(ctx, R.b.colorChannelDefault)
            else color
        )
    }

    after<CallParticipantsAdapter.ViewHolderUser>("onConfigure", Int::class.java, MGRecyclerDataPayload::class.java) {
        val callParticipant = it.args[1] as CallParticipantsAdapter.ListItem.VoiceUser
        val color = callParticipant.participant.guildMember.color

        if (color != Color.BLACK) binding.root.findViewById<TextView>(voiceUserListId).setTextColor(color)
    }

    after<AudienceViewHolder>("onConfigure", Int::class.java, StageCallItem::class.java) {
        val stageCallItem = it.args[1] as StageCallItem.AudienceItem
        val color = stageCallItem.voiceUser.guildMember.color

        if (color != Color.BLACK) binding.root.findViewById<TextView>(stageAudienceNameId).setTextColor(color)
    }

    after<SpeakerViewHolder>("onConfigure", Int::class.java, StageCallItem::class.java) {
        val stageCallItem = it.args[1] as StageCallItem.SpeakerItem
        val color = stageCallItem.voiceUser.guildMember.color

        if (color != Color.BLACK) binding.root.findViewById<TextView>(stageSpeakerNameId).setTextColor(color)
    }
}

fun PatcherAPI.patchMentionsList() {
    val mentionNameId = Utils.getResId("chat_input_item_name", "id")

    after<AutocompleteItemViewHolder>("bindUser", UserAutocompletable::class.java) {
        val userAutocompletable = it.args[0] as UserAutocompletable
        val color = userAutocompletable.guildMember?.color ?: return@after

        if (color != Color.BLACK) binding.root.findViewById<TextView>(mentionNameId).setTextColor(color)
    }
}

fun PatcherAPI.patchProfileName(profileTag: Boolean) {
    val usernameTextId = Utils.getResId("username_text", "id")

    after<UserProfileHeaderView>("configurePrimaryName", UserProfileHeaderViewModel.ViewState.Loaded::class.java) {
        val loaded = it.args[0] as UserProfileHeaderViewModel.ViewState.Loaded
        val guildMember = loaded.guildMember ?: return@after

        if (guildMember.color == Color.BLACK) return@after

        val textView = UserProfileHeaderView.`access$getBinding$p`(this).root
            .findViewById<com.facebook.drawee.span.SimpleDraweeSpanTextView>(usernameTextId)

        textView.apply {
            val end = if (guildMember.nick == null && !profileTag) loaded.user.username.length
            else j.length

            j.setSpan(ForegroundColorSpan(guildMember.color), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setDraweeSpanStringBuilder(j)
        }
    }
}

fun PatcherAPI.patchMessages() {
    after<WidgetChatListAdapterItemMessage>("processMessageText", SimpleDraweeSpanTextView::class.java, MessageEntry::class.java) {
        val member = (it.args[1] as MessageEntry).author ?: return@after

        if (member.color == Color.BLACK) return@after

        val textView = it.args[0] as SimpleDraweeSpanTextView

        val stringBuilder = textView.mDraweeStringBuilder?.apply {
            setSpan(ForegroundColorSpan(member.color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.setDraweeSpanStringBuilder(stringBuilder)
    }
}

fun PatcherAPI.patchMemberStatuses() {
    val statusGameId = Utils.getResId("channel_members_list_item_game", "id")
    val customStatusId = Utils.getResId("user_profile_header_custom_status", "id")

    after<ChannelMembersListViewHolderMember>("bind", ChannelMembersListAdapter.Item.Member::class.java, Function0::class.java) {
        val member = it.args[0] as ChannelMembersListAdapter.Item.Member
        val color = member.color ?: return@after

        if (color != Color.BLACK) {
            val textView = binding.root.findViewById<SimpleDraweeSpanTextView>(statusGameId)

            textView.mDraweeStringBuilder?.apply {
                setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.setDraweeSpanStringBuilder(this)
            }
        }
    }

    after<UserProfileHeaderView>("updateViewState", UserProfileHeaderViewModel.ViewState.Loaded::class.java) {
        val guildMember = (it.args[0] as UserProfileHeaderViewModel.ViewState.Loaded).guildMember ?: return@after

        if (guildMember.color != Color.BLACK) {
            val textView = UserProfileHeaderView.`access$getBinding$p`(this).root.findViewById<SimpleDraweeSpanTextView>(customStatusId)

            textView.mDraweeStringBuilder?.apply {
                setSpan(ForegroundColorSpan(guildMember.color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.setDraweeSpanStringBuilder(this)
            }
        }
    }
}

fun PatcherAPI.patchReactionsList() {
    val reactionUsersTextViewId = Utils.getResId("manage_reactions_result_user_name", "id")

    after<ManageReactionsResultsAdapter.ReactionUserViewHolder>("onConfigure", Int::class.java, MGRecyclerDataPayload::class.java) {
        if (it.args[1] !is ManageReactionsResultsAdapter.ReactionUserItem) return@after

        val guildMember = (it.args[1] as ManageReactionsResultsAdapter.ReactionUserItem).guildMember
                ?: return@after

        if (guildMember.color != Color.BLACK) binding.root.findViewById<TextView>(reactionUsersTextViewId).setTextColor(guildMember.color)
    }
}