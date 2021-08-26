package com.aliucord.plugins

import android.content.Context
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.utils.ReflectUtils
import com.discord.stores.StoreStream

class Token : Plugin() {
    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Adds a token slash command to tell you your Discord token."
            version = "1.1.1"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        commands.registerCommand("token", "Tells you your token", emptyList()) {
            try {
                val token = ReflectUtils.getField(StoreStream.getAuthentication(), "authToken") as String?
                return@registerCommand CommandResult("```\n$token```", null, false)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
                return@registerCommand CommandResult("Uh oh, failed to get token", null, false)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                return@registerCommand CommandResult("Uh oh, failed to get token", null, false)
            }
        }
    }

    override fun stop(context: Context) = commands.unregisterAll()
}