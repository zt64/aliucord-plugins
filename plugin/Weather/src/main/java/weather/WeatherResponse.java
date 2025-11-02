package weather;

import java.util.List;

public class WeatherResponse {
    public List<Condition> current_condition;
    public List<NearestArea> nearest_area;

    public static class Condition {
        public String FeelsLikeC;
        public String FeelsLikeF;
        public String cloudcover;
        public String humidity;
        public String temp_C;
        public String temp_F;
        public String uvIndex;
        public List<TextValue> weatherDesc;
        public String winddir16Point;
        public String windspeedKmph;
        public String windspeedMiles;
    }

    public static class NearestArea {
        public List<TextValue> areaName;
        public List<TextValue> country;
        public List<TextValue> region;
    }

    public static class TextValue {
        public String value;
    }
}
