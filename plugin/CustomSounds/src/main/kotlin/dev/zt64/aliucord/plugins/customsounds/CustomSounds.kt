package dev.zt64.aliucord.plugins.customsounds

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.patcher.component3
import com.aliucord.patcher.component4
import com.aliucord.patcher.instead
import com.discord.utilities.media.AppSound
import com.discord.utilities.media.AppSoundManager.SoundPlayer

data class SoundEntry(val title: String, val setting: String)

val soundMap: Map<AppSound, SoundEntry> by lazy {
    val companion = AppSound.Companion

    mapOf(
        companion.sounD_CALL_CALLING to SoundEntry("Call - Calling", "callCalling"),
        companion.sounD_CALL_RINGING to SoundEntry("Call - Ringing", "callRinging"),
        companion.sounD_RECONNECT to SoundEntry("Reconnect", "reconnect"),
        companion.sounD_DEAFEN to SoundEntry("Deafen", "deafen"),
        companion.sounD_UNDEAFEN to SoundEntry("Undeafen", "undeafen"),
        companion.sounD_MUTE to SoundEntry("Mute", "mute"),
        companion.sounD_UNMUTE to SoundEntry("Unmute", "unmute"),
        companion.sounD_STREAM_STARTED to SoundEntry("Stream Started", "streamStarted"),
        companion.sounD_STREAM_ENDED to SoundEntry("Stream Ended", "streamEnded"),
        companion.sounD_STREAM_USER_JOINED to SoundEntry(
            "Stream User Joined",
            "streamUserJoined"
        ),
        companion.sounD_STREAM_USER_LEFT to SoundEntry("Stream User Left", "streamUserLeft"),
        companion.sounD_USER_JOINED to SoundEntry("User Joined", "userJoined"),
        companion.sounD_USER_LEFT to SoundEntry("User Left", "userLeft"),
        companion.sounD_USER_MOVED to SoundEntry("User Moved", "userMoved")
    )
}

@AliucordPlugin
class CustomSounds : Plugin() {
    init {
        settingsTab = SettingsTab(SoundSettings::class.java).withArgs(settings)
    }

    override fun start(ctx: Context) {
        val mediaPlayerField = SoundPlayer::class.java.getDeclaredField("mediaPlayer").apply {
            isAccessible = true
        }

        patcher.instead<SoundPlayer>(
            Context::class.java,
            AppSound::class.java,
            Function0::class.java
        ) { (_, context: Context, sound: AppSound, f: Function0<Unit>) ->
            val player = MediaPlayer()
            mediaPlayerField.set(this, player)

            player.setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .apply {
                        setContentType(sound.contentType)
                        setUsage(sound.usage)
                    }.build()
            )

            soundMap[sound]?.let { settings.getString(it.setting, null) }?.let { soundPath ->
                context.contentResolver
                    .openFileDescriptor(Uri.parse(soundPath), "r")
                    .use {
                        player.setDataSource(it?.fileDescriptor)
                    }
            } ?: context.resources.openRawResourceFd(sound.resId).use {
                player.setDataSource(it)
            }

            player.isLooping = sound.shouldLoop
            player.setOnCompletionListener { f() }

            try {
                player.prepare()
            } catch (e: Exception) {
                mediaPlayerField.set(this, null)
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }
}