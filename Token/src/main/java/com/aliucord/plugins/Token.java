package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.MessageEmbedBuilder;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.ReflectUtils;
import com.discord.stores.StoreStream;

import java.util.Collections;

@SuppressWarnings("unused")
public class Token extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Adds a token slash command to tell you your Discord token.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        commands.registerCommand("token", "Tells you your token", Collections.emptyList(), ctx -> {
            MessageEmbedBuilder embed = new MessageEmbedBuilder();
            String token;

            try {
                token = (String) ReflectUtils.getField(StoreStream.getAuthentication(), "authToken", true);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return new CommandsAPI.CommandResult("Uh oh, failed to get token", null, false);
            }

            return new CommandsAPI.CommandResult("DO NOT SHARE THIS TOKEN WITH ANYBODY:\n" + "```\n" + token + "```", Collections.emptyList(), false);
        });
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }
}