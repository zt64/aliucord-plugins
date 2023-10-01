package alwaysanimate

import android.os.Bundle
import android.view.View
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        fun createCheckedSetting(title: String, setting: String, checked: Boolean = true) {
            return addView(
                Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, null).apply {
                    isChecked = settings.getBool(setting, checked)

                    setOnCheckedListener {
                        settings.setBool(setting, it)
                        PluginManager.stopPlugin("AlwaysAnimate")
                        PluginManager.startPlugin("AlwaysAnimate")
                    }
                }
            )
        }

        createCheckedSetting("Server icons", "guildIcons")
        createCheckedSetting("User avatars", "avatars")
        createCheckedSetting("Statuses", "status")
        createCheckedSetting("Round Animated Avatars", "roundedAvatars")
        createCheckedSetting("Disable animations when battery saver is on", "batterySaver", false)
    }
}