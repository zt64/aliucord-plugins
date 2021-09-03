package com.aliucord.plugins

import android.content.Context
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$onConfigure$5`
import top.canyie.pine.Pine.CallFrame
import top.canyie.pine.callback.MethodReplacement

@AliucordPlugin
class AvatarMention : Plugin() {
    override fun start(context: Context) {
        patcher.patch(`WidgetChatListAdapterItemMessage$onConfigure$5`::class.java.getDeclaredMethod("onClick", View::class.java), object : MethodReplacement() {
            override fun replaceCall(callFrame: CallFrame): Any {
                val thisObject = callFrame.thisObject as `WidgetChatListAdapterItemMessage$onConfigure$5`

                return WidgetChatListAdapterItemMessage.`access$getAdapter$p`(thisObject.`this$0`)
                    .apply { eventHandler.onMessageAuthorNameClicked(thisObject.`$message`, this.data.guildId) }
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}