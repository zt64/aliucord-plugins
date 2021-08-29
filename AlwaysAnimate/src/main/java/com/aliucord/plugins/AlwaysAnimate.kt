package com.aliucord.plugins

import android.content.Context
import android.widget.ImageView
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePrePatchFn
import com.aliucord.plugins.alwaysanimate.PluginSettings
import com.discord.models.guild.Guild
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.guilds.list.GuildListViewHolder
import java.lang.reflect.Field

class AlwaysAnimate : Plugin() {
    private var itemAvatarField: Field? = null
    private val WidgetChatListAdapterItemMessage.itemAvatar: ImageView
        get() = (itemAvatarField ?: javaClass.getDeclaredField("binding").apply {
            isAccessible = true
            itemAvatarField = this
        })[this] as ImageView

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Allows making server icons and member avatars always animate."
            version = "1.0.2"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        if (settings.getBool("guildIcon", true)) {
            patcher.patch(GuildListViewHolder.GuildViewHolder::class.java.getDeclaredMethod("configureGuildIconImage", Guild::class.java, Boolean::class.javaPrimitiveType), PinePrePatchFn {
                it.args[1] = true
            })
        }

//        if (settings.getBool("messageAvatar", true)) {
//            val iconUtils = IconUtils.INSTANCE
//
//            patcher.patch(WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, ChatListEntry::class.java), PinePatchFn { callFrame ->
//                val messageEntry = callFrame.args[1] as MessageEntry
//
//                if (messageEntry.message.author == null) return@PinePatchFn
//
//                val coreUser = CoreUser(messageEntry.message.author)
//                coreUser.avatar?.let {
//                    if (iconUtils.isImageHashAnimated(it)) {
//                        val itemAvatar = (callFrame.thisObject as WidgetChatListAdapterItemMessage).itemAvatar
//                        val avatarUrl = "cdn.discordapp.com/avatars/${coreUser.id}/${coreUser.avatar}.gif"
//                    }
//                }
//            })
//        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}