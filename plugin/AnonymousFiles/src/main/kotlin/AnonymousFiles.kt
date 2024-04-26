import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.lytefast.flexinput.model.Attachment

private const val LENGTH = 8

@AliucordPlugin
class AnonymousFiles : Plugin() {
    private val charPool by lazy {
        ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }

    override fun start(context: Context) {
        patcher.after<Attachment<*>>("getDisplayName") {
            val ext = (it.result as String).substringAfterLast('.')

            var str = ""

            repeat(LENGTH) {
                str += charPool.random()
            }

            it.result = "$str.$ext"
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}