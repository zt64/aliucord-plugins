package com.aliucord.plugins.weather;

public class WeatherResponse {
    public Condition[] current_condition;
    public static class Condition {
        public String FeelsLikeC;
        public String FeelsLikeF;
        public String cloudcover;
        public String humidity;
        public String temp_C;
        public String temp_F;
        public String uvIndex;
        public WeatherDesc[] weatherDesc;
        public String winddir16Point;
        public String windspeedKmph;
        public String windspeedMiles;
    }
    public static class WeatherDesc {
        public String value;
    }

    public NearestArea[] nearest_area;
    public static class NearestArea {
        public AreaName[] areaName;
        public Country[] country;
        public Region[] region;
    }
    public static class AreaName {
        public String value;
    }
    public static class Country {
        public String value;
    }
    public static class Region {
        public String value;
    }
}