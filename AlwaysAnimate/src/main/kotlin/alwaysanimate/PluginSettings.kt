package alwaysanimate

import android.os.Bundle
import android.view.View
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        fun createCheckedSetting(title: String, setting: String, checked: Boolean = true): CheckedSetting {
            return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, null).apply {
                isChecked = settings.getBool(setting, checked)

                setOnCheckedListener {
                    settings.setBool(setting, it)
                    PluginManager.stopPlugin("AlwaysAnimate")
                    PluginManager.startPlugin("AlwaysAnimate")
                }
            }
        }

        addView(createCheckedSetting("Server icons", "guildIcons"))
        addView(createCheckedSetting("User avatars", "avatars"))
        addView(createCheckedSetting("Statuses", "status"))
        addView(createCheckedSetting("Round Animated Avatars", "roundedAvatars"))
        addView(createCheckedSetting("Disable animations when battery saver is on", "batterySaver", false))
    }
}