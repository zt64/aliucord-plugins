package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.ReflectUtils;
import com.discord.stores.StoreStream;

import java.util.Collections;

public class Token extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("zt", 289556910426816513L) };
        manifest.description = "Adds a token slash command to tell you your Discord token.";
        manifest.version = "1.0.1";
        manifest.updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        commands.registerCommand("token", "Tells you your token", Collections.emptyList(), ctx -> {
            try {
                String token = (String) ReflectUtils.getField(StoreStream.getAuthentication(), "authToken");
                return new CommandsAPI.CommandResult("```\n" + token + "```", null, false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return new CommandsAPI.CommandResult("Uh oh, failed to get token", null, false);
            }
        });
    }

    @Override
    public void stop(Context context) { commands.unregisterAll(); }
}