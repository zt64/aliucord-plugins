import android.content.Context
import android.util.Base64
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.stores.StoreStream
import com.discord.utilities.rest.RestAPI

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class Token : Plugin() {
    override fun start(context: Context) {
        val options = listOf(Utils.createCommandOption(ApplicationCommandType.BOOLEAN, "send", "Send visible to everyone"))

        commands.registerCommand("token", "Tells you your token", options) {
            if (it.getBoolOrDefault("send", false)) {
                CommandResult(genFakeToken(), null, true)
            } else {
                try {
                    CommandResult("```\n${RestAPI.AppHeadersProvider.INSTANCE.authToken}```", null, false)
                } catch (e: ReflectiveOperationException) {
                    logger.error(e)
                    CommandResult("Uh oh, failed to get token", null, false)
                }
            }
        }
    }

    // imagine if this somehow generates the actual token that'd be pretty funny dont u think
    private fun genFakeToken(): String {
        val id = StoreStream.getUsers().me.id.toString().toByteArray(Charsets.UTF_8)
        val sb = StringBuilder(Base64.encodeToString(id, Base64.DEFAULT).removeSuffix("\n")).append('.')

        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + '_' + '-'

        for (i in 1..7 + 28) {
            if (i == 8) sb.append('.')
            else sb.append(chars.random())
        }

        return sb.toString()
    }

    override fun stop(context: Context) = commands.unregisterAll()
}