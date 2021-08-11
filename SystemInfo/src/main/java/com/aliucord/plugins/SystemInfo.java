package com.aliucord.plugins;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.MessageEmbedBuilder;
import com.aliucord.entities.Plugin;

import java.util.Collections;

public class SystemInfo extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("zt", 289556910426816513L) };
        manifest.description = "Adds a systeminfo slash command that provides basic system information.";
        manifest.version = "1.0.6";
        manifest.updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memInfo);

        commands.registerCommand("system-info", "Get system information", Collections.emptyList(), ctx -> {
            String uptime = DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000);
            MessageEmbedBuilder embedBuilder = new MessageEmbedBuilder()
                    .setColor(0x00FFA200)
                    .setTitle("System Information")
                    .addField("Brand:", Build.BRAND, true)
                    .addField("Product:", Build.PRODUCT, true)
                    .addField("Bootloader:", Build.BOOTLOADER, true)
                    .addField("Board:", Build.BOARD, true)
                    .addField("OS Version:", Build.VERSION.RELEASE, true)
                    .addField("OS Codename:", Build.VERSION.CODENAME, true)
                    .addField("Total Memory:", Math.ceil(memInfo.totalMem / 1e+9) + " GB", true)
                    .addField("Uptime:", uptime, true);

            return new CommandsAPI.CommandResult(null, Collections.singletonList(embedBuilder.build()), false);
        });
    }

    @Override
    public void stop(Context context) { commands.unregisterAll(); }
}