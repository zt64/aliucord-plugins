package com.aliucord.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.Plugin
import com.aliucord.utils.ReflectUtils
import com.discord.stores.StoreStream

@AliucordPlugin
class Token : Plugin() {
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