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

@SuppressWarnings("unused")
public class SystemInfo extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Adds a systeminfo slash command that provides basic system information.";
        manifest.version = "1.0.5";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        commands.registerCommand("system-info", "Get system information", Collections.emptyList(), ctx -> {
            MessageEmbedBuilder embed = new MessageEmbedBuilder();

            ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long totalMemory = memInfo.totalMem;

            String uptime = DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000);

            embed.setTitle("System Information");
            embed.setColor(0x00FFA200);
            embed.addField("Brand:", Build.BRAND, true);
            embed.addField("Product:", Build.PRODUCT, true);
            embed.addField("Bootloader:", Build.BOOTLOADER, true);
            embed.addField("Board:", Build.BOARD, true);
            embed.addField("OS Version:", Build.VERSION.RELEASE, true);
            embed.addField("OS Codename:", Build.VERSION.CODENAME, true);
            embed.addField("Total Memory:", Math.ceil(totalMemory / 1e+9) + " GB", true);
            embed.addField("Uptime:", uptime, true);

            return new CommandsAPI.CommandResult(null, Collections.singletonList(embed.build()), false);
        });
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }
}