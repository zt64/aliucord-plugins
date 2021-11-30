package tk.zt64.plugins.developerutils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.ColorUtils
import com.aliucord.Utils
import com.aliucord.api.PatcherAPI

class NotificationReceiver(private val patcher: PatcherAPI, private val notificationBuilder: NotificationCompat.Builder) : BroadcastReceiver() {
    private var devMode = false
    private var selectedView: View? = null
    private var originalDrawable: Drawable? = null

    override fun onReceive(context: Context, intent: Intent) {
        context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))

        if (devMode)
            patcher.unpatchAll()
        else
            patcher.patch(View::class.java.getDeclaredMethod("performClick")) {
                val view = it.thisObject as View

                if (selectedView == view) {
                    view.setBackgroundColor(Color.TRANSPARENT)
                    view.background = originalDrawable
                    selectedView = null
                } else {
                    selectedView?.setBackgroundColor(Color.TRANSPARENT)
                    selectedView = view
                    originalDrawable = view.background.mutate()
                    selectedView!!.background = GradientDrawable().apply {
                        setColor(ColorUtils.setAlphaComponent(Color.YELLOW, 60))
                        cornerRadius = 4f
                        setStroke(6, Color.RED)
                    }
                    InfoSheet(view).show(Utils.appActivity.supportFragmentManager, "DevUtilsSheet")
                }

                false
            }

        devMode = !devMode

        notificationBuilder.setContentText("Tap to ${if (devMode) "disable" else "enable"} inspector")
        NotificationManagerCompat.from(context).notify(0, notificationBuilder.build())

        Utils.showToast("${if (devMode) "Enabled" else "Disabled"} inspector")
    }
}