import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.wrappers.ChannelWrapper.Companion.isDM
import com.discord.models.member.GuildMember
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.widgets.chat.overlay.ChatTypingModel
import com.discord.widgets.chat.overlay.`ChatTypingModel$Companion$getTypingUsers$1$1`
import com.discord.widgets.chat.overlay.WidgetChatOverlay
import rolecoloreverywhere.PluginSettings
import rolecoloreverywhere.binding
import rolecoloreverywhere.patchMemberStatuses
import rolecoloreverywhere.patchMentions
import rolecoloreverywhere.patchMentionsList
import rolecoloreverywhere.patchMessages
import rolecoloreverywhere.patchProfileName
import rolecoloreverywhere.patchReactionsList
import rolecoloreverywhere.patchVoiceChannels
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@AliucordPlugin
class RoleColorEverywhere : Plugin() {
    private val typingUsers = HashMap<String, Int>()

    init {
        settingsTab =
            SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(
                settings
            )
    }

    @Suppress("UNCHECKED_CAST")
    override fun start(context: Context) {
        if (settings.getBool("typingText", true)) {
            val typingUsersTextViewId = Utils.getResId("chat_typing_users_typing", "id")

            patcher.after<`ChatTypingModel$Companion$getTypingUsers$1$1`<Any, Any, Any>>(
                "call",
                Map::class.java,
                Map::class.java
            ) {
                typingUsers.clear()

                if (StoreStream.getChannelsSelected().selectedChannel.isDM()) return@after

                val users = it.args[0] as Map<Long, User>
                val members = it.args[1] as Map<Long, GuildMember>

                members.forEach { (id, member) ->
                    val color = member.color
                    if (color != Color.BLACK) {
                        typingUsers[
                            GuildMember.getNickOrUsername(
                                member,
                                users[id]
                            )
                        ] = color
                    }
                }
            }

            patcher.after<WidgetChatOverlay.TypingIndicatorViewHolder>(
                "configureTyping",
                ChatTypingModel.Typing::class.java
            ) {
                val textView = binding.root.findViewById<TextView>(typingUsersTextViewId)

                textView.run {
                    text = SpannableString(text).apply {
                        typingUsers.forEach { (username, color) ->
                            val start =
                                text.indexOf(username).takeUnless { it == -1 } ?: return@forEach
                            setSpan(
                                ForegroundColorSpan(color),
                                start,
                                start + username.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                }
            }
        }

        patcher.run {
            if (settings.getBool("userMentions", true)) patchMentions()
            if (settings.getBool("voiceChannel", true)) patchVoiceChannels()
            if (settings.getBool("userMentionList", true)) patchMentionsList()
            if (settings.getBool("profileName", true)) {
                patchProfileName(
                    settings.getBool("profileTag", true)
                )
            }
            if (settings.getBool("messages", false)) patchMessages()
            if (settings.getBool("status", true)) patchMemberStatuses()
            if (settings.getBool("reactionList", true)) patchReactionsList()
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        typingUsers.clear()
    }
}