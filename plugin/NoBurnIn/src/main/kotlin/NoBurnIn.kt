import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.aliucord.Utils
import com.aliucord.Utils.getResId
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.discord.databinding.WidgetHomeBinding
import com.discord.views.channelsidebar.GuildChannelSideBarActionsView
import com.discord.widgets.home.WidgetHome
import com.discord.widgets.home.WidgetHomeHeaderManager
import com.discord.widgets.home.WidgetHomeModel
import noburnin.PluginSettings

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin(requiresRestart = true)
class NoBurnIn : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET)
            .withArgs(settings)
    }

    override fun start(context: Context) {
        val toolbarIconId = getResId("toolbar_icon", "id")
        val searchButtonId = getResId("menu_chat_search", "id")
        val threadButtonId = getResId("menu_chat_thread_browser", "id")
        val membersButtonId = getResId("menu_chat_side_panel", "id")
        val callButtonId = getResId("menu_chat_start_call", "id")
        val videoButtonId = getResId("menu_chat_start_video_call", "id")

        if (settings.getBool("immersiveMode", false)) {
            WindowInsetsControllerCompat(
                Utils.appActivity.window,
                Utils.appActivity.findViewById<ViewGroup>(android.R.id.content)
            ).hide(
                when (settings.getInt("immersiveModeType", 0)) {
                    0 -> WindowInsetsCompat.Type.statusBars()
                    1 -> WindowInsetsCompat.Type.navigationBars()
                    2 -> WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.statusBars()
                    else -> return
                }
            )
        }

        patcher.after<WidgetHomeHeaderManager>(
            "configure",
            WidgetHome::class.java,
            WidgetHomeModel::class.java,
            WidgetHomeBinding::class.java
        ) { (_, home: WidgetHome) ->
            with(home) {
                if (settings.getBool("hideToolbar", false)) {
                    toolbar.visibility = View.GONE
                    unreadCountView.visibility = View.GONE
                } else {
                    if (settings.getBool("hideChannelIcon", false)) {
                        actionBarTitleLayout.findViewById<View>(toolbarIconId)?.visibility = View.GONE
                    }

                    if (settings.getBool("hideText", false)) {
                        setActionBarTitle("")
                        setActionBarSubtitle("")
                    }

                    if (settings.getBool("hideUnread", true)) unreadCountView.visibility = View.GONE
                    if (settings.getBool("hideDrawerButton", true)) {
                        setActionBarDisplayHomeAsUpEnabled(false)
                    }

                    toolbar.menu.run {
                        if (settings.getBool("hideSearchButton", true)) {
                            findItem(searchButtonId)?.isVisible = false
                        }
                        if (settings.getBool("hideThreadsButton", true)) {
                            findItem(threadButtonId)?.isVisible = false
                        }
                        if (settings.getBool("hideMembersButton", true)) {
                            findItem(membersButtonId)?.isVisible = false
                        }
                        if (settings.getBool("hideCallButton", true)) {
                            findItem(callButtonId)?.isVisible = false
                        }
                        if (settings.getBool("hideVideoButton", true)) {
                            findItem(videoButtonId)?.isVisible = false
                        }
                    }
                }
            }
        }

        // Show the search button in members list instead of the threads button
        patcher.before<GuildChannelSideBarActionsView>(
            "a",
            *Array(5) { Function1::class.java },
            *Array(4) { Boolean::class.java }
        ) {
            it.args[8] = true
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}