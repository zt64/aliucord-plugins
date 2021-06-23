package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.Http;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.MessageEmbedBuilder;
import com.aliucord.entities.Plugin;
import com.aliucord.plugins.weather.WeatherResponse;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.models.commands.ApplicationCommandOption;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class Weather extends Plugin {
    public final String thermometerEmoji = "\uD83C\uDF21";
    public final String cloudEmoji = "☁️";
    public final String humidityEmoji = "\uD83D\uDCA6";
    public final String uvIndexEmoji = "\uD83D\uDD76️";
    public final String windEmoji = "\uD83D\uDCA8";

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Adds a weather slash command to get information for the current location or one that's provided.";
        manifest.version = "1.0.3";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        List<ApplicationCommandOption> arguments = Collections.singletonList(
                new ApplicationCommandOption(ApplicationCommandType.STRING, "location", "The location to query", null, false, true, null, null)
        );

        commands.registerCommand("weather", "Get the weather for the current location, or a specific location", arguments, args -> {
            MessageEmbedBuilder embed = new MessageEmbedBuilder();
            String location = (String) args.get("location");

            WeatherResponse weather;

            try {
                weather = Http.simpleJsonGet("http://wttr.in/" + (location == null ? "" : location) + "?format=j1", WeatherResponse.class);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return new CommandsAPI.CommandResult("Uh oh, failed to fetch weather data", null, false);
            }

            WeatherResponse.Condition condition = weather.current_condition.get(0);
            WeatherResponse.WeatherDesc weatherDesc = condition.weatherDesc.get(0);
            WeatherResponse.NearestArea nearestArea = weather.nearest_area.get(0);
            WeatherResponse.AreaName areaName = nearestArea.areaName.get(0);
            WeatherResponse.Country country = nearestArea.country.get(0);

            embed.setTitle("Weather for " + areaName.value)
                    .setColor(0xEDEDED)
                    .setDescription(String.format(Locale.ENGLISH,
                        "%s\n\n" +
                        "**Temp**:\n" +
                        "  %s ️**Feels Like**: `%s°F`  |  `%s°C`\n" +
                        "  %s **Temperature**: `%s°F`  |  `%s°C`\n" +
                        "\n" +
                        "**Wind**:\n" +
                        "  %s **Wind Direction**: `%s`\n" +
                        "  %s **Wind Speed**: `%s MPH`  |  `%s KMPH`\n" +
                        "\n" +
                        "**Other**:\n" +
                        "  %s️ **Cloud Cover**: `%s%%`\n" +
                        "  %s **Humidity**: `%s%%`\n" +
                        "  %s **UV Index**: `%s`\n",
                        weatherDesc.value,
                        thermometerEmoji,
                        condition.FeelsLikeF,
                        condition.FeelsLikeC,
                        thermometerEmoji,
                        condition.temp_F,
                        condition.temp_C,
                        windEmoji,
                        condition.winddir16Point,
                        windEmoji,
                        condition.windspeedMiles,
                        condition.windspeedKmph,
                        cloudEmoji,
                        condition.cloudcover,
                        humidityEmoji,
                        condition.humidity,
                        uvIndexEmoji,
                        condition.uvIndex
                    ))
                    .setFooter(areaName.value + ", " + country.value, null);

            try {
                embed.setUrl("http://wttr.in/" + (location == null ? "" : URLEncoder.encode(location, "UTF-8")));
            } catch (UnsupportedEncodingException ignored) {}

            return new CommandsAPI.CommandResult(null, Collections.singletonList(embed.build()), false);
        });
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }
}