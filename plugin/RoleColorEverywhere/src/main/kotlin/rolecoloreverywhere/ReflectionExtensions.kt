package rolecoloreverywhere

import com.discord.databinding.VoiceUserListItemUserBinding
import com.discord.databinding.WidgetChannelMembersListItemUserBinding
import com.discord.databinding.WidgetChannelsListItemVoiceUserBinding
import com.discord.databinding.WidgetChatInputAutocompleteItemBinding
import com.discord.databinding.WidgetChatOverlayBinding
import com.discord.databinding.WidgetManageReactionsResultUserBinding
import com.discord.databinding.WidgetStageChannelAudienceBinding
import com.discord.databinding.WidgetStageChannelSpeakerBinding
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember
import com.discord.widgets.chat.input.autocomplete.adapter.AutocompleteItemViewHolder
import com.discord.widgets.chat.managereactions.ManageReactionsResultsAdapter
import com.discord.widgets.chat.overlay.WidgetChatOverlay
import com.discord.widgets.voice.fullscreen.stage.AudienceViewHolder
import com.discord.widgets.voice.fullscreen.stage.SpeakerViewHolder
import com.discord.widgets.voice.sheet.CallParticipantsAdapter
import com.facebook.drawee.span.DraweeSpanStringBuilder

private val typingIndicatorBinding = WidgetChatOverlay.TypingIndicatorViewHolder::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val itemVoiceBinding = WidgetChannelsListAdapter.ItemVoiceUser::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val autoCompleteBinding = AutocompleteItemViewHolder::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val memberListBinding = ChannelMembersListViewHolderMember::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val voiceListBinding = CallParticipantsAdapter.ViewHolderUser::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val audienceListBinding = AudienceViewHolder::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val speakerListBinding = SpeakerViewHolder::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val reactionListBinding = ManageReactionsResultsAdapter.ReactionUserViewHolder::class.java.getDeclaredField("binding")
    .apply { isAccessible = true }
private val mDraweeStringBuilderField = SimpleDraweeSpanTextView::class.java.getDeclaredField("mDraweeStringBuilder")
    .apply { isAccessible = true }

val WidgetChatOverlay.TypingIndicatorViewHolder.binding
    get() = typingIndicatorBinding[this] as WidgetChatOverlayBinding

val WidgetChannelsListAdapter.ItemVoiceUser.binding
    get() = itemVoiceBinding[this] as WidgetChannelsListItemVoiceUserBinding

val AutocompleteItemViewHolder.binding
    get() = autoCompleteBinding[this] as WidgetChatInputAutocompleteItemBinding

val ChannelMembersListViewHolderMember.binding
    get() = memberListBinding[this] as WidgetChannelMembersListItemUserBinding

val CallParticipantsAdapter.ViewHolderUser.binding
    get() = voiceListBinding[this] as VoiceUserListItemUserBinding

val AudienceViewHolder.binding
    get() = audienceListBinding[this] as WidgetStageChannelAudienceBinding

val SpeakerViewHolder.binding
    get() = speakerListBinding[this] as WidgetStageChannelSpeakerBinding

val ManageReactionsResultsAdapter.ReactionUserViewHolder.binding
    get() = reactionListBinding[this] as WidgetManageReactionsResultUserBinding

val SimpleDraweeSpanTextView.mDraweeStringBuilder
    get() = mDraweeStringBuilderField[this] as DraweeSpanStringBuilder?