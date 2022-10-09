import android.app.ActivityManager
import android.content.Context
import android.os.Build.BOARD
import android.os.Build.BOOTLOADER
import android.os.Build.BRAND
import android.os.Build.PRODUCT
import android.os.Build.SUPPORTED_ABIS
import android.os.Build.VERSION.CODENAME
import android.os.Build.VERSION.RELEASE
import android.os.Build.VERSION.SDK_INT
import android.os.SystemClock
import android.text.format.DateUtils
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import java.io.File

val Long.GB
    get() = (this / 1e+9).toFixed()

fun Double.toFixed() = "%.2f".format(this)

val isRooted
    get() = System.getenv("PATH")?.split(':')?.any {
        File(it, "su").exists()
    }

fun getArch(): String {
    SUPPORTED_ABIS.forEach {
        when (it) {
            "arm64-v8a" -> return "aarch64"
            "armeabi-v7a" -> return "arm"
            "x86_64" -> return "x86_64"
            "x86" -> return "i686"
        }
    }
    return System.getProperty("os.arch")
        ?: System.getProperty("ro.product.cpu.abi")
        ?: "Unknown Architecture"
}

@AliucordPlugin
class SystemInfo : Plugin() {
    override fun start(context: Context) {
        commands.registerCommand(
            "system-info",
            "Get system information",
            Utils.createCommandOption(ApplicationCommandType.BOOLEAN, "send", "send result visible to everyone")
        ) {
            val memInfo = ActivityManager.MemoryInfo()
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memInfo)

            val totalMem = memInfo.totalMem
            val availMem = memInfo.availMem
            val usedMem = totalMem - availMem
            val percentAvail = ((availMem / totalMem.toDouble()) * 100).toFixed()

            val info = linkedMapOf(
                "Brand" to BRAND,
                "Product" to PRODUCT,
                "Board" to BOARD,
                "Architecture" to getArch(),
                "Bootloader" to BOOTLOADER,
                "Rooted" to isRooted.toString(),
                "OS Version" to "$CODENAME $RELEASE (SDK v${SDK_INT})",
                "Memory Usage" to "${usedMem.GB}/${totalMem.GB}GB (${availMem.GB}GB / $percentAvail% free)",
                "Uptime" to DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000)
            )

            if (it.getBoolOrDefault("send", false)) {
                StringBuilder("**__System Info:__**\n\n").run {
                    info.forEach { (k, v) ->
                        append("**")
                        append(k)
                        append(':')
                        append("** ")
                        append(v)
                        append('\n')
                    }

                    CommandResult(toString(), null, true)
                }
            } else {
                MessageEmbedBuilder().run {
                    setColor(0x00FFA200)
                    setTitle("System Info")
                    info.forEach { (k, v) ->
                        addField(k, v, true)
                    }

                    CommandResult(null, listOf(build()), false)
                }
            }
        }
    }

    override fun stop(context: Context) = commands.unregisterAll()
}