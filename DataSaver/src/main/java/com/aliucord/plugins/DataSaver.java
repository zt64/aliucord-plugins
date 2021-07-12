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

@SuppressWarnings("unused")
public class DataSaver extends Plugin {
    private final ConnectivityManager connectivityManager = Utils.getAppContext().getSystemService(ConnectivityManager.class);
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            StoreUserSettings storeUserSettings = StoreStream.getUserSettings();
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            setMediaSettings(storeUserSettings, networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
        }
    };

    private void setMediaSettings(StoreUserSettings storeUserSettings, boolean state) {
        storeUserSettings.setIsEmbedMediaInlined(Utils.appActivity, state);
        storeUserSettings.setIsAttachmentMediaInline(Utils.appActivity, state);
    }

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Disables media previews when on mobile data to limit data usage.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) { connectivityManager.registerDefaultNetworkCallback(networkCallback); }

    @Override
    public void stop(Context context) { connectivityManager.unregisterNetworkCallback(networkCallback); }
}