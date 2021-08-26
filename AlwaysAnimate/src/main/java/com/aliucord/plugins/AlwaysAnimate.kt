package com.aliucord.plugins

import android.content.Context
import android.widget.ImageView
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePatchFn
import com.aliucord.patcher.PinePrePatchFn
import com.aliucord.plugins.alwaysanimate.PluginSettings
import com.aliucord.utils.ReflectUtils
import com.discord.models.guild.Guild
import com.discord.models.user.CoreUser
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.list.entries.MessageEntry
import com.discord.widgets.guilds.list.GuildListViewHolder

class AlwaysAnimate : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Allows making server icons and member avatars always animate."
            version = "1.0.1"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        if (settings.getBool("guildIcon", true)) {
            patcher.patch(GuildListViewHolder.GuildViewHolder::class.java.getDeclaredMethod("configureGuildIconImage", Guild::class.java, Boolean::class.javaPrimitiveType), PinePrePatchFn { it.args[1] = true })
        }

        if (settings.getBool("authorAvatar", true)) {
            val iconUtils = IconUtils.INSTANCE
            patcher.patch(WidgetChatListAdapterItemMessage::class.java.getDeclaredMethod("onConfigure", Int::class.javaPrimitiveType, ChatListEntry::class.java), PinePatchFn {
                val messageEntry = it.args[1] as MessageEntry
                val coreUser = CoreUser(messageEntry.message.author)
                if (!iconUtils.isImageHashAnimated(coreUser.avatar)) return@PinePatchFn
                try {
                    val imageView = ReflectUtils.getField(it.thisObject, "itemAvatar") as ImageView?
                    if (imageView != null) IconUtils.setIcon(imageView, coreUser.avatar)
                    // String avatar = iconUtils.getForGuildMember(messageEntry.getAuthor(), 1, true);
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}