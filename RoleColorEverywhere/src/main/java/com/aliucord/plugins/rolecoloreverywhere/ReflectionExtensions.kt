package com.aliucord.plugins.rolecoloreverywhere

import com.discord.databinding.*
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember
import com.discord.widgets.chat.input.autocomplete.adapter.AutocompleteItemViewHolder
import com.discord.widgets.chat.overlay.WidgetChatOverlay
import com.discord.widgets.voice.fullscreen.stage.AudienceViewHolder
import com.discord.widgets.voice.fullscreen.stage.SpeakerViewHolder
import com.discord.widgets.voice.sheet.CallParticipantsAdapter
import com.facebook.drawee.span.DraweeSpanStringBuilder
import java.lang.reflect.Field

object ReflectionExtensions {
    private lateinit var typingIndicatorBinding: Field
    private lateinit var itemVoiceBinding: Field
    private lateinit var autoCompleteBinding: Field
    private lateinit var memberListBinding: Field
    private lateinit var voiceListBinding: Field
    private lateinit var audienceListBinding: Field
    private lateinit var speakerListBinding: Field
    private lateinit var mDraweeStringBuilderField: Field

    fun init() {
        typingIndicatorBinding = WidgetChatOverlay.TypingIndicatorViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
        itemVoiceBinding = WidgetChannelsListAdapter.ItemVoiceUser::class.java.getDeclaredField("binding").apply { isAccessible = true }
        autoCompleteBinding = AutocompleteItemViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
        memberListBinding = ChannelMembersListViewHolderMember::class.java.getDeclaredField("binding").apply { isAccessible = true }
        voiceListBinding = CallParticipantsAdapter.ViewHolderUser::class.java.getDeclaredField("binding").apply { isAccessible = true }
        audienceListBinding = AudienceViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
        speakerListBinding = SpeakerViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
        mDraweeStringBuilderField = SimpleDraweeSpanTextView::class.java.getDeclaredField("mDraweeStringBuilder").apply { isAccessible = true }
    }

    val WidgetChatOverlay.TypingIndicatorViewHolder.binding: WidgetChatOverlayBinding
        get() = typingIndicatorBinding[this] as WidgetChatOverlayBinding

    val WidgetChannelsListAdapter.ItemVoiceUser.binding: WidgetChannelsListItemVoiceUserBinding
        get() = itemVoiceBinding[this] as WidgetChannelsListItemVoiceUserBinding

    val AutocompleteItemViewHolder.binding: WidgetChatInputAutocompleteItemBinding
        get() = autoCompleteBinding[this] as WidgetChatInputAutocompleteItemBinding

    val ChannelMembersListViewHolderMember.binding: WidgetChannelMembersListItemUserBinding
        get() = memberListBinding[this] as WidgetChannelMembersListItemUserBinding

    val CallParticipantsAdapter.ViewHolderUser.binding: VoiceUserListItemUserBinding
        get() = voiceListBinding[this] as VoiceUserListItemUserBinding

    val AudienceViewHolder.binding: WidgetStageChannelAudienceBinding
        get() = audienceListBinding[this] as WidgetStageChannelAudienceBinding

    val SpeakerViewHolder.binding: WidgetStageChannelSpeakerBinding
        get() = speakerListBinding[this] as WidgetStageChannelSpeakerBinding

    val SimpleDraweeSpanTextView.mDraweeStringBuilder: DraweeSpanStringBuilder?
        get() = mDraweeStringBuilderField[this] as DraweeSpanStringBuilder?
}