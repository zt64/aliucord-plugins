package com.aliucord.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import top.canyie.pine.callback.MethodReplacement

@AliucordPlugin
class NoLinkify : Plugin() {
    override fun start(context: Context) {
        patcher.patch(WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("shouldLinkify", String::class.java), MethodReplacement.returnConstant(false))
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}