package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.Utils;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.MessageEmbed;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.models.commands.ApplicationCommandOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Weather extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Weather";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        List<ApplicationCommandOption> arguments = Collections.singletonList(
                new ApplicationCommandOption(ApplicationCommandType.STRING, "location", "The word to search for", null, false, true, null, null)
        );

        commands.registerCommand("weather", "Get the weather for the current location, or a specific location", arguments, args -> {
            MessageEmbed embed = new MessageEmbed();

            String location = (String) args.get("location");

            if (location == null) {
                embed.setTitle("Weather for current location");

                try {
                    String weather = fetchWeather(null);
                    embed.setDescription(String.format("```\n%s\n```", weather));
                } catch (Throwable throwable) {
                    return new CommandsAPI.CommandResult("Uh oh, failed to fetch weather data for current location", null, false);
                }
            } else {
                embed.setTitle(String.format("Weather for %s", location));

                try {
                    String weather = fetchWeather(location);
                    embed.setDescription(String.format("```\n%s\n```", weather));
                } catch (Throwable throwable) {
                    return new CommandsAPI.CommandResult("Uh oh, failed to fetch weather data", null, false);
                }
            }

            return new CommandsAPI.CommandResult(null, Collections.singletonList(embed), false);
        });
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }

    private String fetchWeather(String location) throws IOException {
        URL url;
        if (location == null) {
            url = new URL("http://wttr.in/?0T&force-ansi=1");
        } else {
            url = new URL(String.format("http://wttr.in/%s?0T&force-ansi=1", URLEncoder.encode(location, "UTF-8")));
        }

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Aliucord");

        String line;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}