package com.aliucord.plugins

import android.content.Context
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import top.canyie.pine.callback.MethodReplacement

class NoLinkify : Plugin() {
    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Disables highlighting for phone numbers and addresses in messages\n."
            version = "1.0.0"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        patcher.patch(WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("shouldLinkify", String::class.java), MethodReplacement.returnConstant(false))
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}