import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.instead
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage

@AliucordPlugin
class NoLinkify : Plugin() {
    override fun start(context: Context) {
        patcher.instead<WidgetChatListAdapterItemMessage>("shouldLinkify", String::class.java) {
            false
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}