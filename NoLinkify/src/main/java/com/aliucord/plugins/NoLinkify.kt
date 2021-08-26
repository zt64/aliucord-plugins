package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePatchFn
import com.discord.models.message.Message
import com.discord.stores.StoreMessageState
import com.discord.utilities.textprocessing.DiscordParser
import com.discord.utilities.textprocessing.MessagePreprocessor
import com.discord.utilities.textprocessing.MessageRenderContext
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.MessageEntry
import top.canyie.pine.Pine.CallFrame
import top.canyie.pine.callback.MethodReplacement

class NoLinkify : Plugin() {
    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Disables linkify for phone numbers and email addresses in messages."
            version = "1.0.0"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        // patcher.patch(WidgetChatListAdapterItemMessage.class.getDeclaredMethod("shouldLinkify", String.class), MethodReplacement.returnConstant(false));
        patcher.patch(WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("processMessageText", SimpleDraweeSpanTextView::class.java, MessageEntry::class.java), PinePatchFn {
            with(it.args[0] as SimpleDraweeSpanTextView) {
                text = "5"
                autoLinkMask = 0
                detachCurrentDraweeSpanStringBuilder()
            }
        })

        val getMessagePreprocessor = WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("getMessagePreprocessor", Long::class.javaPrimitiveType, Message::class.java, StoreMessageState.State::class.java)
            .apply { isAccessible = true }
        val getMessageRenderContext = WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("getMessageRenderContext", Context::class.java, MessageEntry::class.java, Function1::class.java)
            .apply { isAccessible = true }
        val getSpoilerClickHandler = WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("getSpoilerClickHandler", Message::class.java)
            .apply { isAccessible = true }
        val shouldLinkify = WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("shouldLinkify", String::class.java)
            .apply { isAccessible = true }

        patcher.patch(WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("processMessageText", SimpleDraweeSpanTextView::class.java, MessageEntry::class.java), object : MethodReplacement() {
            @SuppressLint("ResourceType")
            override fun replaceCall(callFrame: CallFrame): Any? {
                val _this = callFrame.thisObject as WidgetChatListAdapterItemMessage
                val messageEntry = callFrame.args[1] as MessageEntry
                val message = messageEntry.message
                val isWebhook = message.isWebhook
                val z3 = message.editedTimestamp?.g() ?: 0 > 0
                val type = messageEntry.message.type

                with(callFrame.args[0] as SimpleDraweeSpanTextView) {
                    val str = if (message.isSourceDeleted) {
                        context.resources.getString(2131893762)
                    } else {
                        message.content
                    }

                    val messagePreprocessor = getMessagePreprocessor.invoke(_this, WidgetChatListAdapterItemMessage.`access$getAdapter$p`(_this).data.userId, message, messageEntry.messageState) as MessagePreprocessor
                    val parseChannelMessage = DiscordParser.parseChannelMessage(context, str, getMessageRenderContext
                        .invoke(_this, context, messageEntry, getSpoilerClickHandler.invoke(_this, message)) as
                            MessageRenderContext, messagePreprocessor, if (isWebhook) DiscordParser.ParserOptions.ALLOW_MASKED_LINKS else DiscordParser.ParserOptions.DEFAULT, z3)

                    apply {
                        autoLinkMask = if (messagePreprocessor.isLinkifyConflicting || !(shouldLinkify.invoke(_this, message.content) as Boolean)) 0 else 6
                        visibility = if (parseChannelMessage.length <= 0) View.GONE else View.VISIBLE
                        alpha = if (type != null && type == -1) 0.5f else 1.0f
                        setDraweeSpanStringBuilder(parseChannelMessage)
                    }
                }
                return null
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}