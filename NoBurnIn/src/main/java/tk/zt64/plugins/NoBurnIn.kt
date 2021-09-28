package tk.zt64.plugins

import android.content.Context
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.discord.widgets.home.WidgetHome

@AliucordPlugin
class NoBurnIn: Plugin() {
    override fun start(context: Context) {
        patcher.patch(WidgetHome::class.java.getDeclaredMethod("setUpToolbar"), PinePatchFn {
            (it.thisObject as WidgetHome).toolbar.visibility = View.GONE
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}