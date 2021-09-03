package com.aliucord.plugins

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.settings.WidgetSettings
import com.lytefast.flexinput.R

@AliucordPlugin
class RestartButton : Plugin() {
    @Throws(NoSuchMethodException::class)
    override fun start(context: Context) {
        val icon = ContextCompat.getDrawable(context, com.yalantis.ucrop.R.c.ucrop_rotate)?.mutate()

        patcher.patch(WidgetSettings::class.java.getDeclaredMethod("onTabSelected"), PinePatchFn {
            with(it.thisObject as WidgetSettings) {
                icon?.setTint(ColorCompat.getThemedColor(requireContext(), R.b.colorInteractiveNormal))

                requireAppActivity().t.menu.add("Restart")
                    .setIcon(icon)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener {
                        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                        context.startActivity(Intent.makeRestartActivityTask(intent?.component))
                        Runtime.getRuntime().exit(0)
                        false
                    }
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}