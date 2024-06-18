@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap.Config
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.discord.app.AppActivity
import com.discord.stores.StoreStream

@AliucordPlugin
class SystemTheme : Plugin() {
    init {
        settingsTab = SettingsTab(SystemThemeSettings::class.java).withArgs(settings)
    }
    override fun start(ctx: Context) {
        StoreStream.getUserSettingsSystem().isThemeSyncEnabled

        patcher.patch(AppActivity.Main::class.java.getDeclaredMethod("onConfigurationChanged", Configuration::class.java), InsteadHook {  (param, configuration: Configuration) ->
            val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    Utils.showToast("Dark mode off")
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    Utils.showToast("Dark mode on")
                }
            }
            null

        })
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }
}