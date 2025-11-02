import android.content.Context;
import com.aliucord.Http;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI.CommandResult;
import com.aliucord.entities.MessageEmbedBuilder;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;
import weather.WeatherResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@AliucordPlugin
public class Weather extends Plugin {
    private static final String THERMOMETER_EMOJI = "\uD83C\uDF21";
    private static final String CLOUD_EMOJI = "☁️";
    private static final String HUMIDITY_EMOJI = "\uD83D\uDCA6";
    private static final String UV_INDEX_EMOJI = "\uD83D\uDD76️";
    private static final String WIND_EMOJI = "\uD83D\uDCA8";

    @Override
    public void start(Context context) {
        commands.registerCommand(
            "weather",
            "Get the weather for the current location, or a specific location",
            java.util.Collections.singletonList(
                Utils.createCommandOption(
                    ApplicationCommandType.STRING,
                    "location",
                    "The location to query"
                )
            ),
            ctx -> {
                String location = ctx.getString("location");
                WeatherResponse weather;
                
                try {
                    weather = Http.simpleJsonGet(
                        "https://wttr.in/" + (location != null ? location : "") + "?format=j1",
                        WeatherResponse.class
                    );
                } catch (Throwable throwable) {
                    logger.error(throwable);
                    return new CommandResult("Uh oh, failed to fetch weather data", null, false);
                }

                WeatherResponse.Condition condition = weather.current_condition.get(0);
                WeatherResponse.TextValue weatherDesc = condition.weatherDesc.get(0);
                WeatherResponse.NearestArea nearestArea = weather.nearest_area.get(0);
                String areaName = nearestArea.areaName.get(0).value;
                String country = nearestArea.country.get(0).value;
                String region = nearestArea.region.get(0).value;

                StringBuilder description = new StringBuilder();
                description.append("**Temp**:\n");
                description.append(THERMOMETER_EMOJI).append(" **Feels Like**: `").append(condition.FeelsLikeF).append("°F`  |  `").append(condition.FeelsLikeC).append("°C`\n");
                description.append(THERMOMETER_EMOJI).append(" **Temperature**: `").append(condition.temp_F).append("°F`  |  `").append(condition.temp_C).append("°C`\n");
                description.append("**Wind**:\n");
                description.append(WIND_EMOJI).append(" **Wind Direction**: `").append(condition.winddir16Point).append("`\n");
                description.append(WIND_EMOJI).append(" **Wind Speed**: `").append(condition.windspeedMiles).append(" MPH`  |  `").append(condition.windspeedKmph).append(" KMPH`\n");
                description.append("**Other**:\n");
                description.append(CLOUD_EMOJI).append("  **Cloud Cover**: `").append(condition.cloudcover).append("%`\n");
                description.append(HUMIDITY_EMOJI).append(" **Humidity**: `").append(condition.humidity).append("`\n");
                description.append(UV_INDEX_EMOJI).append(" **UV Index**: `").append(condition.uvIndex).append("`\n");

                MessageEmbedBuilder embed = new MessageEmbedBuilder()
                    .setTitle("Weather: " + weatherDesc.value)
                    .setColor(0xEDEDED)
                    .setDescription(description.toString())
                    .setFooter(areaName + ", " + region + ", " + country, null);

                if (location != null) {
                    try {
                        embed.setUrl("http://wttr.in/" + URLEncoder.encode(location, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e);
                    }
                } else {
                    embed.setUrl("http://wttr.in/");
                }

                return new CommandResult(null, java.util.Collections.singletonList(embed.build()), false);
            }
        );
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }
}
