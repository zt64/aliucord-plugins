package weather

data class WeatherResponse(
    val current_condition: List<Condition>,
    val nearest_area: List<NearestArea>
) {
    data class Condition(
        val FeelsLikeC: String,
        val FeelsLikeF: String,
        val cloudcover: String,
        val humidity: String,
        val temp_C: String,
        val temp_F: String,
        val uvIndex: String,
        val weatherDesc: List<TextValue>,
        val winddir16Point: String,
        val windspeedKmph: String,
        val windspeedMiles: String
    )

    data class NearestArea(
        val areaName: List<TextValue>,
        val country: List<TextValue>,
        val region: List<TextValue>
    )

    data class TextValue(val value: String)
}