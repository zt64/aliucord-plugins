package com.aliucord.plugins

import android.content.Context
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.InsteadHook
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$onConfigure$5`

@AliucordPlugin
class AvatarMention : Plugin() {
    override fun start(context: Context) {
        patcher.patch(`WidgetChatListAdapterItemMessage$onConfigure$5`::class.java.getDeclaredMethod("onClick", View::class.java), InsteadHook {
            val thisObject = it.thisObject as `WidgetChatListAdapterItemMessage$onConfigure$5`

            WidgetChatListAdapterItemMessage.`access$getAdapter$p`(thisObject.`this$0`)
                    .apply { eventHandler.onMessageAuthorNameClicked(thisObject.`$message`, data.guildId) }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}