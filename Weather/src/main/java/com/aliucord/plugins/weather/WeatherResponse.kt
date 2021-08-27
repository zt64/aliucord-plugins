package com.aliucord.plugins.weather

class WeatherResponse(
    val current_condition: Array<Condition>,
    val nearest_area: Array<NearestArea>
) {
    class Condition (
        val FeelsLikeC: String,
        val FeelsLikeF: String,
        val cloudcover: String,
        val humidity: String,
        val temp_C: String,
        val temp_F: String,
        val uvIndex: String,
        val weatherDesc: Array<WeatherDesc>,
        val winddir16Point: String,
        val windspeedKmph: String,
        val windspeedMiles: String
    )

    class WeatherDesc (
        val value: String
    )

    class NearestArea (
        val areaName: Array<AreaName>,
        val country: Array<Country>,
        val region: Array<Region>
    )

    class AreaName (
        val value: String
    )

    class Country (
        val value: String
    )

    class Region (
        val value: String
    )
}