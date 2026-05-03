package dam_a46104.jetpackweatherapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    @SerialName("current_weather")
    val currentWeather: CurrentWeather,
    val hourly: Hourly,
    val daily: Daily
)

@Serializable
data class CurrentWeather(
    val temperature: Float,
    val windspeed: Float,
    val winddirection: Int,
    val weathercode: Int,
    @SerialName("is_day")
    val isDay: Int,
    val time: String
)

@Serializable
data class Hourly(
    val time: List<String>,
    @SerialName("temperature_2m")
    val temperature2m: List<Float>,
    val weathercode: List<Int>,
    @SerialName("pressure_msl")
    val pressureMsl: List<Float>,
    @SerialName("windspeed_10m")
    val windspeed10m: List<Float>,
    @SerialName("apparent_temperature")
    val apparentTemperature: List<Float>,
    @SerialName("relativehumidity_2m")
    val relativeHumidity2m: List<Int>,
    val visibility: List<Float>
)

@Serializable
data class Daily(
    val time: List<String>,
    @SerialName("uv_index_max")
    val uvIndexMax: List<Float>
)