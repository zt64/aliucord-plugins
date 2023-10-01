import android.content.Context
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.instead
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.adapter.`WidgetChatListAdapterItemMessage$onConfigure$5`

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class AvatarMention : Plugin() {
    override fun start(context: Context) {
        patcher.instead<`WidgetChatListAdapterItemMessage$onConfigure$5`>("onClick", View::class.java) {
            val adapter = WidgetChatListAdapterItemMessage.`access$getAdapter$p`(`this$0`)

            adapter.eventHandler.onMessageAuthorNameClicked(`$message`, adapter.data.guildId)
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}