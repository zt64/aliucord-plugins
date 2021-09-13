package com.aliucord.plugins.rolecoloreverywhere

import com.discord.databinding.WidgetChannelMembersListItemUserBinding
import com.discord.databinding.WidgetChannelsListItemVoiceUserBinding
import com.discord.databinding.WidgetChatInputAutocompleteItemBinding
import com.discord.databinding.WidgetChatOverlayBinding
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember
import com.discord.widgets.chat.input.autocomplete.adapter.AutocompleteItemViewHolder
import com.discord.widgets.chat.overlay.WidgetChatOverlay
import com.facebook.drawee.span.DraweeSpanStringBuilder
import java.lang.reflect.Field

object ReflectionExtensions {
    private lateinit var typingIndicatorBinding: Field
    private lateinit var itemVoiceBinding: Field
    private lateinit var autoCompleteBinding: Field
    private lateinit var memberListBinding: Field
    private lateinit var mDraweeStringBuilderField: Field

    fun init() {
        typingIndicatorBinding = WidgetChatOverlay.TypingIndicatorViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
        itemVoiceBinding = WidgetChannelsListAdapter.ItemVoiceUser::class.java.getDeclaredField("binding").apply { isAccessible = true }
        autoCompleteBinding = AutocompleteItemViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
        memberListBinding = ChannelMembersListViewHolderMember::class.java.getDeclaredField("binding").apply { isAccessible = true }
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

    val SimpleDraweeSpanTextView.mDraweeStringBuilder: DraweeSpanStringBuilder?
        get() = mDraweeStringBuilderField[this] as DraweeSpanStringBuilder?
}