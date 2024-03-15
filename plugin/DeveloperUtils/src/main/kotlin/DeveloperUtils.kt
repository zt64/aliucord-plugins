import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.utilities.fcm.NotificationClient
import com.lytefast.flexinput.R
import developerutils.NotificationReceiver

@AliucordPlugin
class DeveloperUtils : Plugin() {
    private var receiver: BroadcastReceiver? = null

    // Shut
    @Suppress("LaunchActivityFromNotification")
    override fun start(context: Context) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent("tk.zt64.plugins.INSPECT"),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder =
            NotificationCompat
                .Builder(context, NotificationClient.NOTIF_GENERAL)
                .setSmallIcon(R.e.ic_security_24dp)
                .setContentTitle("DeveloperUtils")
                .setContentText("Tap to enable inspector")
                .setContentIntent(pendingIntent)

        receiver = NotificationReceiver(patcher, notificationBuilder)

        Utils.appActivity.registerReceiver(receiver, IntentFilter("tk.zt64.plugins.INSPECT"))

        NotificationManagerCompat.from(context).notify(0, notificationBuilder.build())
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        Utils.appActivity.unregisterReceiver(receiver)
    }
}