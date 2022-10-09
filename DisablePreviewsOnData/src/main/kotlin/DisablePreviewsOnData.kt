import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.stores.StoreStream

@AliucordPlugin
class DisablePreviewsOnData : Plugin() {
    private val storeUserSettings = StoreStream.getUserSettings()
    private val connectivityManager = Utils.appContext.getSystemService(ConnectivityManager::class.java)
    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return
            val onWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

            storeUserSettings.setIsEmbedMediaInlined(Utils.appActivity, onWifi)
            storeUserSettings.setIsAttachmentMediaInline(Utils.appActivity, onWifi)
        }
    }

    override fun start(context: Context) {
        storeUserSettings.getIsSyncTextAndImagesEnabled(false)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun stop(context: Context) = connectivityManager.unregisterNetworkCallback(networkCallback)
}