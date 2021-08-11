package com.aliucord.plugins;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.aliucord.Utils;
import com.aliucord.entities.Plugin;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserSettings;

public class DataSaver extends Plugin {
    private final StoreUserSettings storeUserSettings = StoreStream.getUserSettings();
    private final ConnectivityManager connectivityManager = Utils.getAppContext().getSystemService(ConnectivityManager.class);
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            boolean state = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            storeUserSettings.setIsEmbedMediaInlined(Utils.appActivity, state);
            storeUserSettings.setIsAttachmentMediaInline(Utils.appActivity, state);
        }
    };

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("zt", 289556910426816513L) };
        manifest.description = "Disables media previews when on mobile data to limit data usage.";
        manifest.version = "1.0.1";
        manifest.updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) { connectivityManager.registerDefaultNetworkCallback(networkCallback); }

    @Override
    public void stop(Context context) { connectivityManager.unregisterNetworkCallback(networkCallback); }
}