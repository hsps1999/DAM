package dam_a46104.jetpackweatherapp.data

enum class WmoWeatherCode(val code: Int, val image: String) {
    CLEAR_SKY(0, "clear_"),
    MAINLY_CLEAR(1, "mainly_clear_"),
    PARTLY_CLOUDY(2, "partly_cloudy_"),
    OVERCAST(3, "overcast"),
    FOG(45, "fog"),
    DEPOSITING_RIME_FOG(48, "fog"),
    DRIZZLE_LIGHT(51, "drizzle"),
    DRIZZLE_MODERATE(53, "drizzle"),
    DRIZZLE_DENSE(55, "drizzle"),
    RAIN_SLIGHT(61, "rain"),
    RAIN_MODERATE(63, "rain"),
    RAIN_HEAVY(65, "rain"),
    SNOW_SLIGHT(71, "snow"),
    SNOW_MODERATE(73, "snow"),
    SNOW_HEAVY(75, "snow"),
    SNOW_GRAINS(77, "snow"),
    RAIN_SHOWERS_SLIGHT(80, "rain_showers"),
    RAIN_SHOWERS_MODERATE(81, "rain_showers"),
    RAIN_SHOWERS_VIOLENT(82, "rain_showers"),
    THUNDERSTORM(95, "thunderstorm"),
    THUNDERSTORM_HAIL_SLIGHT(96, "thunderstorm"),
    THUNDERSTORM_HAIL_HEAVY(99, "thunderstorm");

    companion object {
        fun fromCode(code: Int): WmoWeatherCode? = entries.find { it.code == code }
    }
}