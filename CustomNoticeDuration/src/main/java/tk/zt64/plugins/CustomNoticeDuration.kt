package tk.zt64.plugins

import android.animation.ValueAnimator
import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.widgets.notice.NoticePopup
import tk.zt64.plugins.customnoticeduration.PluginSettings

@AliucordPlugin
class CustomNoticeDuration : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        patcher.patch(NoticePopup::class.java.getDeclaredMethod("getAutoDismissAnimator", Integer::class.javaObjectType, Function0::class.java)){
            val result = it.result as ValueAnimator
            if (settings.getBool("autoDismissNotice", true)) result.duration = settings.getLong("noticeDuration", 5000)
            else result.cancel()
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}