package com.aliucord.plugins

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.text.format.DateUtils
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import kotlin.math.ceil

class SystemInfo : Plugin() {
    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Adds a systeminfo slash command that provides basic system information."
            version = "1.1.6"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        val memInfo = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memInfo)
        commands.registerCommand("system-info", "Get system information", emptyList()) {
            val uptime = DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000)
            val embedBuilder = MessageEmbedBuilder().setColor(0x00FFA200)
                .setTitle("System Information")
                .addField("Brand:", Build.BRAND, true)
                .addField("Product:", Build.PRODUCT, true)
                .addField("Bootloader:", Build.BOOTLOADER, true)
                .addField("Board:", Build.BOARD, true)
                .addField("OS Version:", Build.VERSION.RELEASE, true)
                .addField("OS Codename:", Build.VERSION.CODENAME, true)
                .addField("Total Memory:", ceil(memInfo.totalMem / 1e+9).toString() + " GB", true)
                .addField("Uptime:", uptime, true)
            CommandResult(null, listOf(embedBuilder.build()), false)
        }
    }

    override fun stop(context: Context) = commands.unregisterAll()
}