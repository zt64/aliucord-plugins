package dev.zt64.aliucord.plugins.frecents

import android.util.Base64
import com.aliucord.Constants
import com.aliucord.Http
import com.aliucord.Utils
import discord_protos.discord_users.v1.FrecencyUserSettings
import rx.Observable
import rx.subjects.BehaviorSubject
import java.io.File

private const val ROUTE = "/users/@me/settings-proto/2"

class FrecencySettingsManager {
    private val frecencyUserSettingsSubject = BehaviorSubject.k0<FrecencyUserSettings>()

    @Volatile
    private var _settings: FrecencyUserSettings? = null

    private var debug = false

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
    //                 .json(Response::class.java2)
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

    fun updateSettings(updater: FrecencyUserSettings.() -> FrecencyUserSettings) {
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
            // Wire doesn't support partial merging like protobuf, so we manually merge non-null fields
            settings.copy(
                versions = newSettings.versions ?: settings.versions,
                favorite_gifs = newSettings.favorite_gifs ?: settings.favorite_gifs,
                favorite_stickers = newSettings.favorite_stickers ?: settings.favorite_stickers,
                sticker_frecency = newSettings.sticker_frecency ?: settings.sticker_frecency,
                favorite_emojis = newSettings.favorite_emojis ?: settings.favorite_emojis,
                emoji_frecency = newSettings.emoji_frecency ?: settings.emoji_frecency,
                application_command_frecency = newSettings.application_command_frecency ?: settings.application_command_frecency,
                favorite_soundboard_sounds = newSettings.favorite_soundboard_sounds ?: settings.favorite_soundboard_sounds,
                application_frecency = newSettings.application_frecency ?: settings.application_frecency,
                heard_sound_frecency = newSettings.heard_sound_frecency ?: settings.heard_sound_frecency,
                played_sound_frecency = newSettings.played_sound_frecency ?: settings.played_sound_frecency,
                guild_and_channel_frecency = newSettings.guild_and_channel_frecency ?: settings.guild_and_channel_frecency,
                emoji_reaction_frecency = newSettings.emoji_reaction_frecency ?: settings.emoji_reaction_frecency
            )
        } else {
            newSettings
        }
    }

    private fun loadSettingsSync(): FrecencyUserSettings {
        return try {
            if (debug) {
                try {
                    val bin = File(Constants.BASE_PATH, "Frecents.bin").readBytes()
                    return FrecencyUserSettings.ADAPTER.decode(bin)
                } catch (e: Exception) {
                    Utils.showToast("Falling back to network load: ${e.message}")
                }
            }

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
        if (debug) return

        val res = Http.Request
            .newDiscordRNRequest(ROUTE, "PATCH")
            .executeWithJson(Patch(settings))

        when {
            res.statusCode == 429 -> throw RuntimeException("Rate limited")
            !res.ok() -> throw RuntimeException("HTTP ${res.statusCode}: ${res.statusMessage}")
        }
    }

    private fun decodeSettings(encoded: String): FrecencyUserSettings {
        return FrecencyUserSettings.ADAPTER.decode(Base64.decode(encoded, Base64.DEFAULT))
    }

    private data class Patch(val settings: String) {
        constructor(settings: FrecencyUserSettings) : this(
            Base64.encodeToString(FrecencyUserSettings.ADAPTER.encode(settings), Base64.DEFAULT)
        )
    }

    private data class Response(val settings: String)
}