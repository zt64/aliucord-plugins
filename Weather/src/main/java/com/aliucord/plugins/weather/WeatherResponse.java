package com.aliucord.plugins.weather;

import java.util.List;

public class WeatherResponse {
    public List<Condition> current_condition;
    public static class Condition {
        public String FeelsLikeC;
        public String FeelsLikeF;
        public String cloudcover;
        public String humidity;
        public String temp_C;
        public String temp_F;
        public String uvIndex;
        public List<WeatherDesc> weatherDesc;
        public String winddir16Point;
        public String windspeedKmph;
        public String windspeedMiles;
    }
    public static class WeatherDesc {
        public String value;
    }

    public List<NearestArea> nearest_area;
    public static class NearestArea {
        public List<AreaName> areaName;
        public List<Country> country;
    }
    public static class AreaName {
        public String value;
    }
    public static class Country {
        public String value;
    }
}