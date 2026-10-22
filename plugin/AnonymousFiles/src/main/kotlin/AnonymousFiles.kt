import android.content.Context
import android.webkit.MimeTypeMap
import androidx.fragment.app.FragmentActivity
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.after
import com.aliucord.widgets.BottomSheet
import com.discord.widgets.chat.MessageContent
import com.discord.widgets.chat.MessageManager
import com.discord.widgets.chat.input.ChatInputViewModel
import com.lytefast.flexinput.model.Attachment
import de.robv.android.xposed.XposedBridge
import java.util.HashMap
import kotlin.jvm.functions.Function1

@AliucordPlugin(requiresRestart = true)
class AnonymousFiles : Plugin() {

    private val anonymizedNames = HashMap<Long, String>()

    private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private val mediaExtensions = setOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif", "avif", "apng", "svg", "ico", "tiff", "tif",
        "mp4", "mov", "avi", "mkv", "webm", "3gp", "m4v", "wmv", "flv", "ogv", "ts", "mpg", "mpeg",
        "mp3", "ogg", "wav", "flac", "m4a", "aac", "opus", "wma", "mid", "midi", "amr", "aiff"
    )

    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    override fun start(context: Context) {
        patchGetDisplayName()
        patchSendMessage()
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        anonymizedNames.clear()
    }

    private fun patchGetDisplayName() {
        patcher.after<Attachment<*>>("getDisplayName") { callFrame ->
            if (anonymizedNames.isEmpty()) return@after
            val attachment = callFrame.thisObject as? Attachment<*> ?: return@after
            val anonName = anonymizedNames[attachment.id] ?: return@after
            callFrame.result = anonName
        }
    }

    private fun patchSendMessage() {
        val method = ChatInputViewModel::class.java.getDeclaredMethod(
            "sendMessage",
            Context::class.java,
            MessageManager::class.java,
            MessageContent::class.java,
            List::class.java,
            Boolean::class.javaPrimitiveType,
            Function1::class.java
        )

        patcher.patch(
            method,
            InsteadHook { callFrame ->
                @Suppress("UNCHECKED_CAST")
                val attachments = callFrame.args[3] as? List<Attachment<*>>
                if (attachments.isNullOrEmpty()) {
                    return@InsteadHook XposedBridge.invokeOriginalMethod(
                        callFrame.method,
                        callFrame.thisObject,
                        callFrame.args
                    )
                }

                try {
                    val promptOnSend = settings.getBool(
                        PluginSettings.KEY_PROMPT_ON_SEND,
                        PluginSettings.DEFAULT_PROMPT_ON_SEND
                    )
                    val mediaOnly = settings.getBool(
                        PluginSettings.KEY_MEDIA_ONLY,
                        PluginSettings.DEFAULT_MEDIA_ONLY
                    )
                    val excludedRaw = settings.getString(
                        PluginSettings.KEY_EXCLUDED_EXTS,
                        PluginSettings.DEFAULT_EXCLUDED_EXTS
                    )
                    val excludedSet = excludedRaw.split(',', ';', ' ')
                        .map { it.trim().removePrefix(".").lowercase() }
                        .filter { it.isNotEmpty() }
                        .toSet()
                    val nameLength = settings.getInt(
                        PluginSettings.KEY_NAME_LENGTH,
                        PluginSettings.DEFAULT_NAME_LENGTH
                    ).coerceIn(1, 64)

                    if (!promptOnSend) {
                        val targeted = attachments.filter { shouldAnonymize(it, mediaOnly, excludedSet) }
                        targeted.forEach { att ->
                            anonymizedNames[att.id] = generateRandomName(att.displayName, nameLength)
                        }
                        val result = XposedBridge.invokeOriginalMethod(
                            callFrame.method,
                            callFrame.thisObject,
                            callFrame.args
                        )
                        val targetIds = targeted.map { it.id }
                        Utils.mainThread.postDelayed({
                            targetIds.forEach { anonymizedNames.remove(it) }
                        }, 3000L)
                        return@InsteadHook result
                    }

                    val activity = Utils.appActivity as? FragmentActivity
                    if (activity == null || activity.isFinishing || activity.isDestroyed || activity.supportFragmentManager.isStateSaved) {
                        return@InsteadHook XposedBridge.invokeOriginalMethod(
                            callFrame.method,
                            callFrame.thisObject,
                            callFrame.args
                        )
                    }

                    @Suppress("UNCHECKED_CAST")
                    val callback = callFrame.args.getOrNull(5) as? Function1<Boolean, Unit>

                    SendPromptBottomSheet(
                        attachments = attachments,
                        onConfirm = { selected ->
                            selected.forEach { att ->
                                anonymizedNames[att.id] = generateRandomName(att.displayName, nameLength)
                            }
                            XposedBridge.invokeOriginalMethod(
                                callFrame.method,
                                callFrame.thisObject,
                                callFrame.args
                            )
                            val selectedIds = selected.map { it.id }
                            Utils.mainThread.postDelayed({
                                selectedIds.forEach { anonymizedNames.remove(it) }
                            }, 3000L)
                        },
                        onCancel = {
                            callback?.invoke(false)
                        }
                    ).show(activity.supportFragmentManager, "SendPromptBottomSheet")

                    null
                } catch (t: Throwable) {
                    logger.error("AnonymousFiles: failed to handle send", t)
                    XposedBridge.invokeOriginalMethod(
                        callFrame.method,
                        callFrame.thisObject,
                        callFrame.args
                    )
                }
            }
        )
    }

    private fun shouldAnonymize(
        attachment: Attachment<*>,
        mediaOnly: Boolean,
        excludedExts: Set<String>
    ): Boolean {
        val name = attachment.displayName ?: return true
        val ext = name.substringAfterLast('.', "").lowercase()
        if (ext.isNotEmpty() && ext in excludedExts) {
            return false
        }
        if (mediaOnly && !isMediaExtension(ext)) {
            return false
        }
        return true
    }

    private fun isMediaExtension(ext: String): Boolean {
        if (ext.isEmpty()) return false
        if (ext in mediaExtensions) return true
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: return false
        return mime.startsWith("image/") || mime.startsWith("video/") || mime.startsWith("audio/")
    }

    private fun generateRandomName(originalName: String?, length: Int): String {
        val str = buildString(length) {
            repeat(length) {
                append(charPool.random())
            }
        }
        val ext = originalName?.substringAfterLast('.', "") ?: ""
        return if (ext.isNotEmpty()) "$str.$ext" else str
    }
}