package dam_a46104.jetpackweatherapp.data

enum class WmoWeatherCode(val code: Int, val dayImage: String, val nightImage: String) {
    CLEAR_SKY(0, "clear_day", "clear_night"),
    MAINLY_CLEAR(1, "mostly_clear_day", "mostly_clear_night"),
    PARTLY_CLOUDY(2, "partly_cloudy_day", "partly_cloudy_night"),
    OVERCAST(3, "cloudy", "cloudy"),
    FOG(45, "fog", "fog"),
    DEPOSITING_RIME_FOG(48, "fog_light", "fog_light"),
    DRIZZLE_LIGHT(51, "drizzle", "drizzle"),
    DRIZZLE_MODERATE(53, "drizzle", "drizzle"),
    DRIZZLE_DENSE(55, "drizzle", "drizzle"),
    FREEZING_DRIZZLE_LIGHT(56, "freezing_drizzle", "freezing_drizzle"),
    FREEZING_DRIZZLE_DENSE(57, "freezing_drizzle", "freezing_drizzle"),
    RAIN_SLIGHT(61, "rain_light", "rain_light"),
    RAIN_MODERATE(63, "rain", "rain"),
    RAIN_HEAVY(65, "rain_heavy", "rain_heavy"),
    FREEZING_RAIN_LIGHT(66, "freezing_rain_light", "freezing_rain_light"),
    FREEZING_RAIN_HEAVY(67, "freezing_rain_heavy", "freezing_rain_heavy"),
    SNOW_SLIGHT(71, "snow_light", "snow_light"),
    SNOW_MODERATE(73, "snow", "snow"),
    SNOW_HEAVY(75, "snow_heavy", "snow_heavy"),
    SNOW_GRAINS(77, "flurries", "flurries"),
    RAIN_SHOWERS_SLIGHT(80, "rain_light", "rain_light"),
    RAIN_SHOWERS_MODERATE(81, "rain", "rain"),
    RAIN_SHOWERS_VIOLENT(82, "rain_heavy", "rain_heavy"),
    SNOW_SHOWERS_SLIGHT(85, "snow_light", "snow_light"),
    SNOW_SHOWERS_HEAVY(86, "snow_heavy", "snow_heavy"),
    ICE_PELLETS_SLIGHT(87, "ice_pellets_light", "ice_pellets_light"),
    ICE_PELLETS_MODERATE(88, "ice_pellets", "ice_pellets"),
    ICE_PELLETS_HEAVY(89, "ice_pellets_heavy", "ice_pellets_heavy"),
    THUNDERSTORM(95, "tstorm", "tstorm"),
    THUNDERSTORM_HAIL_SLIGHT(96, "tstorm", "tstorm"),
    THUNDERSTORM_HAIL_HEAVY(99, "tstorm", "tstorm");

    companion object {
        fun fromCode(code: Int): WmoWeatherCode? = entries.find { it.code == code }
    }
}