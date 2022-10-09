import android.content.Context
import bettermediaviewer.Patches.patchControls
import bettermediaviewer.Patches.patchToolbar
import bettermediaviewer.Patches.patchWidget
import bettermediaviewer.Patches.patchZoomLimit
import bettermediaviewer.PluginSettings
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin

@AliucordPlugin
class BetterMediaViewer : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    override fun start(context: Context) {
        patcher.run {
            patchWidget()
            patchControls()
            if (settings.getBool("bottomToolbar", false)) patchToolbar()
            if (settings.getBool("removeZoomLimit", true)) patchZoomLimit()
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}