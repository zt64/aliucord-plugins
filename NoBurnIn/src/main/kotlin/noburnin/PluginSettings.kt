package noburnin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.views.Divider
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.discord.views.RadioManager

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        var extraSettingsVisibility = if (settings.getBool("hideToolbar", false)) View.GONE else View.VISIBLE
        val ctx = requireContext()

        fun addCheckedSetting(title: String, subtext: String, setting: String, default: Boolean): CheckedSetting {
            return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtext).apply {
                isChecked = settings.getBool(setting, default)
                visibility = extraSettingsVisibility
                setOnCheckedListener { settings.setBool(setting, it) }
                linearLayout.addView(this)
            }
        }

        val radios = listOf(
            Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.RADIO, "Status bar", null),
            Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.RADIO, "Navigation bar", null),
            Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.RADIO, "Both bars", null)
        )

        val immersiveModeEnabled = settings.getBool("immersiveMode", true)

        val radioManager = RadioManager(radios)
        val radioGroup = RadioGroup(ctx).apply { visibility = if (immersiveModeEnabled) View.VISIBLE else View.GONE }
        for (i in radios.indices) {
            val radio = radios[i]
            radio.e {
                settings.setInt("immersiveModeType", i)
                radioManager.a(radio)

                PluginManager.stopPlugin("NoBurnIn")
                PluginManager.startPlugin("NoBurnIn")
            }
            radioGroup.addView(radio)
            if (i == settings.getInt("immersiveModeType", 0)) radioManager.a(radio)
        }

        addView(
            Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, "Immersive mode", "Hide the status bar, navigation bar, or both when viewing media")
                .apply {
                    isChecked = immersiveModeEnabled
                    setOnCheckedListener {
                        settings.setBool("immersiveMode", it)
                        radioGroup.visibility = if (it) View.VISIBLE else View.GONE
                    }
                })
        addView(radioGroup)
        addView(Divider(ctx))

        val extraSettings = listOf(
            addCheckedSetting("Channel Icon", "Whether the channel icon is hidden", "hideChannelIcon", false),
            addCheckedSetting("Toolbar Text", "Whether the toolbar text is hidden", "hideText", false),
            addCheckedSetting("Unread Counter", "Whether the unread counter is hidden", "hideUnread", true),
            addCheckedSetting("Drawer Button", "Whether the drawer button is hidden", "hideDrawerButton", true),
            addCheckedSetting("Search Button", "Whether the search button is hidden", "hideSearchButton", true),
            addCheckedSetting("Threads Button", "Whether the threads button is hidden", "hideThreadsButton", true),
            addCheckedSetting("Members Button", "Whether the members button is hidden", "hideMembersButton", true),
            addCheckedSetting("Call Button", "Whether the call button is hidden in DMs", "hideCallButton", true),
            addCheckedSetting("Video Button", "Whether the video button is hidden in DMs", "hideVideoButton", true)
        )

        val divider = Divider(ctx).apply {
            visibility = extraSettingsVisibility
            addView(this)
        }

        addCheckedSetting("Toolbar", "Whether the toolbar is hidden", "hideToolbar", false).run {
            visibility = View.VISIBLE
            setOnCheckedListener { hideBar ->
                settings.setBool("hideToolbar", hideBar)

                extraSettingsVisibility = if (hideBar) View.GONE else View.VISIBLE
                extraSettings.forEach { it.visibility = extraSettingsVisibility }
                divider.visibility = extraSettingsVisibility
            }
        }
    }
}