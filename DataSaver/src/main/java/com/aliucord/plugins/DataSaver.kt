package com.aliucord.plugins

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import com.aliucord.Utils
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.discord.stores.StoreStream

class DataSaver : Plugin() {
    private val storeUserSettings = StoreStream.getUserSettings()
    private val connectivityManager = Utils.getAppContext().getSystemService(ConnectivityManager::class.java)
    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            with(connectivityManager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                ?: return) {
                storeUserSettings.setIsEmbedMediaInlined(Utils.appActivity, this)
                storeUserSettings.setIsAttachmentMediaInline(Utils.appActivity, this)
            }
        }
    }

    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Disables media previews when on mobile data to limit data usage."
            version = "1.1.1"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        storeUserSettings.getIsSyncTextAndImagesEnabled(false)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun stop(context: Context) = connectivityManager.unregisterNetworkCallback(networkCallback)
}