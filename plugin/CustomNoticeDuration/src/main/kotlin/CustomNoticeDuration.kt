import android.animation.ValueAnimator
import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.widgets.notice.NoticePopup
import customnoticeduration.PluginSettings

@AliucordPlugin
class CustomNoticeDuration : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        patcher.after<NoticePopup>("getAutoDismissAnimator", Integer::class.javaObjectType, Function0::class.java) {
            val result = it.result as ValueAnimator

            if (settings.getBool("autoDismissNotice", true)) {
                result.duration = settings.getLong("noticeDuration", 5000)
            } else {
                result.cancel()
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}