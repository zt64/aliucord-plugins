package dev.zt64.aliucord.plugins.frecents

import android.util.Base64
import com.aliucord.Http
import com.aliucord.Utils
import dev.zt64.aliucord.plugins.GatewayResponse
import discord_protos.discord_users.v1.FrecencyUserSettingsOuterClass.FrecencyUserSettings
import rx.Observable
import rx.subjects.BehaviorSubject

private const val ROUTE = "/users/@me/settings-proto/2"

class FrecencySettingsManager {
    private val frecencyUserSettingsSubject = BehaviorSubject.k0<FrecencyUserSettings>()

    @Volatile
    private var _settings: FrecencyUserSettings? = null

    var settings: FrecencyUserSettings
        get() = _settings ?: loadSettingsSync().also { _settings = it }
        set(value) {
            _settings = value
            frecencyUserSettingsSubject.onNext(value)
        }

    //     fun observeFrecencyUserSettings(): Observable<FrecencyUserSettings> {
    //         /**
    //          * Observable.fromCallable {
    //          *   ...
    //          * }
    //          * .subscribeOn(Schedulers.io())
    //          * .mergeWith(frecencyUserSettingsSubject)
    //          */
    //         return Observable.D {
    //             val res = Http.Request
    //                 .newDiscordRNRequest(ROUTE, "GET")
    //                 .execute()
    //                 .json(Response::class.java)
    //
    //             decodeSettings(res.settings)
    //         }.let {
    //             ObservableExtensionsKt.restSubscribeOn(it, true)
    //         }.a0(frecencyUserSettingsSubject)
    //     }

    fun observeSettings(): Observable<FrecencyUserSettings> {
        if (frecencyUserSettingsSubject.l.latest == null) {
            Utils.threadPool.submit<FrecencyUserSettings> { settings }.get().also {
                frecencyUserSettingsSubject.onNext(it)
            }
        }
        return frecencyUserSettingsSubject
    }

    fun updateSettings(updater: (FrecencyUserSettings) -> FrecencyUserSettings) {
        settings = updater(settings)
    }

    fun patchSettingsAsync(
        onSuccess: (String) -> Unit = { },
        onError: (String, Exception) -> Unit = { msg, e ->
            e.printStackTrace()
            Utils.showToast(msg)
            // Reset to previous state on error
            Utils.threadPool.submit { settings = loadSettingsSync() }
        }
    ) {
        Utils.threadPool.execute {
            try {
                patchSettingsSync()
                onSuccess("Settings updated successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Failed to update settings: ${e.message}", e)
            }
        }
    }

    fun handleGatewayUpdate(response: GatewayResponse) {
        if (response.settings.type != 2) return // Skip non-frecency settings

        val newSettings = decodeSettings(response.settings.proto)

        settings = if (response.partial) {
            settings.toBuilder().mergeFrom(newSettings).build()
        } else {
            newSettings
        }
    }

    private fun loadSettingsSync(): FrecencyUserSettings {
        return try {
            val res = Http.Request
                .newDiscordRNRequest(ROUTE, "GET")
                .execute()

            res.assertOk()

            decodeSettings(res.json(Response::class.java).settings)
        } catch (e: Exception) {
            throw RuntimeException("Failed to load frecency settings", e)
        }
    }

    private fun patchSettingsSync() {
        val res = Http.Request
            .newDiscordRNRequest(ROUTE, "PATCH")
            .executeWithJson(Patch(settings))

        when {
            res.statusCode == 429 -> throw RuntimeException("Rate limited")
            !res.ok() -> throw RuntimeException("HTTP ${res.statusCode}: ${res.statusMessage}")
        }
    }

    private fun decodeSettings(encoded: String): FrecencyUserSettings {
        return FrecencyUserSettings.parseFrom(Base64.decode(encoded, Base64.DEFAULT))
    }

    private data class Patch(val settings: String) {
        constructor(settings: FrecencyUserSettings) : this(
            Base64.encodeToString(settings.toByteArray(), Base64.DEFAULT)
        )
    }

    private data class Response(val settings: String)
}