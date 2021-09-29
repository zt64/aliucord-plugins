package tk.zt64.plugins.noburnin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.views.Divider
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

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

        val extraSettings = listOf(
                addCheckedSetting("Channel Icon", "Whether the channel icon is hidden", "hideChannelIcon", false),
                addCheckedSetting("Toolbar Title", "Whether the toolbar title is hidden", "hideTitle", false),
                addCheckedSetting("Unread Counter", "Whether the unread counter is hidden", "hideUnread", true),
                addCheckedSetting("Drawer Button", "Whether the drawer button is hidden", "hideDrawerButton", true),
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