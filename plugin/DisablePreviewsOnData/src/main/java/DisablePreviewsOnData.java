import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserSettings;

@SuppressWarnings("MissingPermission")
@AliucordPlugin
public class DisablePreviewsOnData extends Plugin {
    private StoreUserSettings storeUserSettings;
    private ConnectivityManager connectivityManager;
    private NetworkCallback networkCallback;

    @Override
    public void start(Context context) {
        storeUserSettings = StoreStream.getUserSettings();
        connectivityManager = Utils.appContext.getSystemService(ConnectivityManager.class);
        
        networkCallback = new NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities == null) return;
                
                boolean onWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);

                storeUserSettings.setIsEmbedMediaInlined(Utils.appActivity, onWifi);
                storeUserSettings.setIsAttachmentMediaInline(Utils.appActivity, onWifi);
            }
        };

        storeUserSettings.getIsSyncTextAndImagesEnabled(false);
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    @Override
    public void stop(Context context) {
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}
