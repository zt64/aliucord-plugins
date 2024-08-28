@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

import android.content.Context
import android.content.res.Configuration
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.app.AppActivity
import com.discord.stores.StoreStream

@AliucordPlugin
class SystemTheme : Plugin() {
    private val userSettingsSystem by lazy {
        StoreStream.getUserSettingsSystem()
    }

    init {
        settingsTab = SettingsTab(SystemThemeSettings::class.java).withArgs(settings)
    }

    override fun start(ctx: Context) {
        // Lets save the current value of the sync theme setting then restore when plugin is stopped
        if (!settings.exists("sync_theme")) {
            settings.setBool("sync_theme", userSettingsSystem.isThemeSyncEnabled)
        }
        userSettingsSystem.setIsSyncThemeEnabled(false)

        patcher.after<AppActivity>("onResume") {
            val configuration = applicationContext.resources.configuration
            val currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    userSettingsSystem.setTheme("light", false, null)
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    val theme = if (settings.getBool("amoled", false)) "pureEvil" else "dark"
                    userSettingsSystem.setTheme(theme, false, null)
                }
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()

        // Restore the sync theme setting to its original value
        userSettingsSystem.setIsSyncThemeEnabled(settings.getBool("sync_theme", false))
    }
}