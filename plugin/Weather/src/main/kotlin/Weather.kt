import android.content.Context
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import weather.WeatherResponse
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

@AliucordPlugin
class Weather : Plugin() {
    companion object {
        private const val thermometerEmoji = "\uD83C\uDF21"
        private const val cloudEmoji = "☁️"
        private const val humidityEmoji = "\uD83D\uDCA6"
        private const val uvIndexEmoji = "\uD83D\uDD76️"
        private const val windEmoji = "\uD83D\uDCA8"
    }

    override fun start(context: Context) {
        val arguments =
            listOf(
                Utils.createCommandOption(
                    ApplicationCommandType.STRING,
                    "location",
                    "The location to query"
                )
            )

        commands.registerCommand(
            "weather",
            "Get the weather for the current location, or a specific location",
            arguments
        ) {
            val location = it.getString("location")
            val weather = try {
                Http.simpleJsonGet(
                    "https://wttr.in/${location.orEmpty()}?format=j1",
                    WeatherResponse::class.java
                )
            } catch (throwable: Throwable) {
                logger.error(throwable)
                return@registerCommand CommandResult(
                    "Uh oh, failed to fetch weather data",
                    null,
                    false
                )
            }

            val condition = weather.current_condition.first()
            val weatherDesc = condition.weatherDesc.first()
            val nearestArea = weather.nearest_area.first()
            val areaName = nearestArea.areaName.first().value
            val country = nearestArea.country.first().value
            val region = nearestArea.region.first().value

            val embed = MessageEmbedBuilder()
                .setTitle("Weather: ${weatherDesc.value}")
                .setColor(0xEDEDED)
                .setDescription(
                    buildString {
                        appendLine("**Temp**:")
                        appendLine(
                            "$thermometerEmoji **Feels Like**: `${condition.FeelsLikeF}°F`  |  `${condition.FeelsLikeC}°C`"
                        )
                        appendLine(
                            "$thermometerEmoji **Temperature**: `${condition.temp_F}°F`  |  `${condition.temp_C}°C`"
                        )
                        appendLine("**Wind**:")
                        appendLine("$windEmoji **Wind Direction**: `${condition.winddir16Point}`")
                        appendLine(
                            "$windEmoji **Wind Speed**: `${condition.windspeedMiles} MPH`  |  `${condition.windspeedKmph} KMPH`"
                        )
                        appendLine("**Other**:")
                        appendLine("$cloudEmoji  **Cloud Cover**: `${condition.cloudcover}%`")
                        appendLine("$humidityEmoji **Humidity**: `${condition.humidity}`")
                        appendLine("$uvIndexEmoji **UV Index**: `${condition.uvIndex}`")
                    }
                ).setFooter("$areaName, $region, $country", null)

            if (location != null) {
                try {
                    embed.setUrl("http://wttr.in/${URLEncoder.encode(location, "UTF-8")}")
                } catch (e: UnsupportedEncodingException) {
                    logger.error(e)
                }
            } else {
                embed.setUrl("http://wttr.in/")
            }

            CommandResult(null, listOf(embed.build()), false)
        }
    }

    override fun stop(context: Context) = commands.unregisterAll()
}