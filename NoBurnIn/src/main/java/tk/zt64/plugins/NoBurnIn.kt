package tk.zt64.plugins

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.discord.databinding.WidgetHomeBinding
import com.discord.widgets.home.WidgetHome
import com.discord.widgets.home.WidgetHomeHeaderManager
import com.discord.widgets.home.WidgetHomeModel
import tk.zt64.plugins.noburnin.PluginSettings

@AliucordPlugin
class NoBurnIn: Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        patcher.patch(WidgetHomeHeaderManager::class.java.getDeclaredMethod("configure", WidgetHome::class.java, WidgetHomeModel::class.java, WidgetHomeBinding::class.java), PinePatchFn {
            val widgetHome = it.args[0] as WidgetHome

            if (settings.getBool("hideToolbar", false))
                widgetHome.toolbar.visibility = View.GONE
            else {
                if (settings.getBool("hideChannelIcon", false))
                    widgetHome.actionBarTitleLayout.i.root.findViewById<ImageView>(Utils.getResId("toolbar_icon", "id"))?.visibility = View.GONE
                if (settings.getBool("hideTitle", false))
                    widgetHome.setActionBarTitle("")
                if (settings.getBool("hideUnread", true))
                    widgetHome.unreadCountView.visibility = View.GONE
                if (settings.getBool("hideDrawerButton", true))
                    widgetHome.setActionBarDisplayHomeAsUpEnabled(false)
                if (settings.getBool("hideThreadsButton", true))
                    widgetHome.toolbar.menu.findItem(Utils.getResId("menu_chat_thread_browser", "id")).isVisible = false
                if (settings.getBool("hideMembersButton", true))
                    widgetHome.toolbar.menu.findItem(Utils.getResId("menu_chat_side_panel", "id")).isVisible = false
                if (settings.getBool("hideCallButton", true))
                    widgetHome.toolbar.menu.findItem(Utils.getResId("menu_chat_start_call", "id")).isVisible = false
                if (settings.getBool("hideVideoButton", true))
                    widgetHome.toolbar.menu.findItem(Utils.getResId("menu_chat_start_video_call", "id")).isVisible = false
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}