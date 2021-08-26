package com.aliucord.plugins

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import androidx.core.graphics.ColorUtils
import com.aliucord.Logger
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.rolecoloreverywhere.PluginSettings
import com.aliucord.wrappers.ChannelWrapper.Companion.guildId
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.discord.databinding.WidgetChannelsListItemVoiceUserBinding
import com.discord.databinding.WidgetChatOverlayBinding
import com.discord.models.member.GuildMember
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.guilds.RoleUtils
import com.discord.utilities.textprocessing.node.UserMentionNode
import com.discord.widgets.channels.list.WidgetChannelsListAdapter
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemVoiceUser
import com.discord.widgets.chat.overlay.ChatTypingModel
import com.discord.widgets.chat.overlay.WidgetChatOverlay
import com.discord.widgets.chat.overlay.`ChatTypingModel$Companion$getTypingUsers$1$1`
import top.canyie.pine.Pine
import top.canyie.pine.callback.MethodHook
import java.lang.reflect.Field

class RoleColorEverywhere : Plugin() {
    private val logger = Logger("RoleColorEverywhere")

    private val typingUsers = HashMap<String, Int>()

    private var typingIndicatorBinding: Field? = null
    private val WidgetChatOverlay.TypingIndicatorViewHolder.binding: WidgetChatOverlayBinding
        get() = (typingIndicatorBinding ?: javaClass.getDeclaredField("binding").apply {
            isAccessible = true
            typingIndicatorBinding = this
        })[this] as WidgetChatOverlayBinding

    private var itemVoiceBinding: Field? = null
    private val WidgetChannelsListAdapter.ItemVoiceUser.binding: WidgetChannelsListItemVoiceUserBinding
        get() = (itemVoiceBinding ?: javaClass.getDeclaredField("binding").apply {
            isAccessible = true
            itemVoiceBinding = this
        })[this] as WidgetChannelsListItemVoiceUserBinding

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Displays the highest role color in more places like mentions and typing text"
            version = "1.0.0"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun start(context: Context) {
        val guildStore = StoreStream.getGuilds()

        if (settings.getBool("typingText", true)) {
            patcher.patch(`ChatTypingModel$Companion$getTypingUsers$1$1`::class.java.getDeclaredMethod("call", Map::class.java, Map::class.java), PinePatchFn {
                typingUsers.clear()

                val channel = StoreStream.getChannelsSelected().selectedChannel

                if (channel.isDM()) return@PinePatchFn

                val users = it.args[0] as Map<Long, User>
                val members = it.args[1] as Map<Long, GuildMember>
                val roles = guildStore.roles[channel.guildId]

                members.forEach { entry ->
                    val role = RoleUtils.getHighestRole(roles, entry.value)

                    if (!RoleUtils.isDefaultColor(role)) {
                        typingUsers[GuildMember.getNickOrUsername(entry.value, users[entry.key])] = RoleUtils.getOpaqueColor(role)
                    }
                }
            })

            patcher.patch(WidgetChatOverlay.TypingIndicatorViewHolder::class.java.getDeclaredMethod("configureTyping", ChatTypingModel.Typing::class.java), PinePatchFn {
                val binding = (it.thisObject as WidgetChatOverlay.TypingIndicatorViewHolder).binding

                binding.g.apply {
                    text = SpannableString(text).apply {
                        typingUsers.forEach { (username, color) ->
                            val start = text.indexOf(username)
                            setSpan(ForegroundColorSpan(color), start, start + username.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            })
        }

        if (settings.getBool("mentions", true)) {
            patcher.patch(UserMentionNode::class.java.getDeclaredMethod("renderUserMention", SpannableStringBuilder::class.java, UserMentionNode.RenderContext::class.java), object : MethodHook() {
                private var length: Int = 0

                override fun beforeCall(callFrame: Pine.CallFrame) {
                    length = (callFrame.args[0] as SpannableStringBuilder).length
                }

                override fun afterCall(callFrame: Pine.CallFrame) {
                    val userMentionNode = callFrame.thisObject as UserMentionNode<UserMentionNode.RenderContext>
                    val guild = guildStore.getGuild(StoreStream.getGuildSelected().selectedGuildId)

                    val roles = guildStore.roles[guild.id]
                    val role = RoleUtils.getHighestRole(roles, guildStore.getMember(guild.id, userMentionNode.userId))

                    if (RoleUtils.isDefaultColor(role)) return

                    val foregroundColor = RoleUtils.getOpaqueColor(role)
                    val backgroundColor = ColorUtils.setAlphaComponent(ColorUtils.blendARGB(foregroundColor, Color.BLACK, 0.6f), 30)

                    val spannableStringBuilder = callFrame.args[0] as SpannableStringBuilder
                    spannableStringBuilder.setSpan(ForegroundColorSpan(foregroundColor), length, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableStringBuilder.setSpan(BackgroundColorSpan(backgroundColor), length, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            })
        }

        if (settings.getBool("voiceChannel", true)) {
            patcher.patch(WidgetChannelsListAdapter.ItemVoiceUser::class.java.getDeclaredMethod("onConfigure", Int::class.java, ChannelListItem::class.java), PinePatchFn {
                val channelListItemVoiceUser = it.args[1] as ChannelListItemVoiceUser
                val binding = (it.thisObject as WidgetChannelsListAdapter.ItemVoiceUser).binding
                val guildMember = channelListItemVoiceUser.computed
                val roles = guildStore.roles[guildMember.guildId]
                val role = RoleUtils.getHighestRole(roles, guildMember)

                if (RoleUtils.isDefaultColor(role)) return@PinePatchFn

                binding.g.setTextColor(RoleUtils.getOpaqueColor(role))
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}