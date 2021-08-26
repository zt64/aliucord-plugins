package com.aliucord.plugins

import android.content.Context
import android.view.View
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$onConfigure$5`
import top.canyie.pine.Pine.CallFrame
import top.canyie.pine.callback.MethodReplacement

class AvatarMention : Plugin() {
    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Tapping a message avatar will mention the user."
            version = "1.1.1"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

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