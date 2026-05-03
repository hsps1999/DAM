package dam_a46104.jetpackweatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dam_a46104.jetpackweatherapp.data.WmoWeatherCode
import dam_a46104.jetpackweatherapp.ui.theme.ClearDayBottom
import dam_a46104.jetpackweatherapp.ui.theme.ClearDayTop
import dam_a46104.jetpackweatherapp.ui.theme.ClearNightBottom
import dam_a46104.jetpackweatherapp.ui.theme.ClearNightTop
import dam_a46104.jetpackweatherapp.ui.theme.RainBottom
import dam_a46104.jetpackweatherapp.ui.theme.RainTop
import dam_a46104.jetpackweatherapp.ui.theme.SnowBottom
import dam_a46104.jetpackweatherapp.ui.theme.SnowTop
import dam_a46104.jetpackweatherapp.ui.theme.ThunderBottom
import dam_a46104.jetpackweatherapp.ui.theme.ThunderTop

fun getWeatherGradient(weathercode: Int, isDay: Boolean): Pair<Color, Color> {
    val wCode = WmoWeatherCode.fromCode(weathercode)
    return when (wCode) {
        WmoWeatherCode.CLEAR_SKY,
        WmoWeatherCode.MAINLY_CLEAR,
        WmoWeatherCode.PARTLY_CLOUDY ->
            if (isDay) Pair(ClearDayTop, ClearDayBottom)
            else Pair(ClearNightTop, ClearNightBottom)

        WmoWeatherCode.THUNDERSTORM,
        WmoWeatherCode.THUNDERSTORM_HAIL_SLIGHT,
        WmoWeatherCode.THUNDERSTORM_HAIL_HEAVY ->
            Pair(ThunderTop, ThunderBottom)

        WmoWeatherCode.SNOW_SLIGHT,
        WmoWeatherCode.SNOW_MODERATE,
        WmoWeatherCode.SNOW_HEAVY,
        WmoWeatherCode.SNOW_GRAINS,
        WmoWeatherCode.SNOW_SHOWERS_SLIGHT,
        WmoWeatherCode.SNOW_SHOWERS_HEAVY ->
            Pair(SnowTop, SnowBottom)

        else ->
            if (isDay) Pair(RainTop, RainBottom)
            else Pair(ClearNightTop, ClearNightBottom)
    }
}

fun isSnowCondition(weathercode: Int): Boolean {
    val wCode = WmoWeatherCode.fromCode(weathercode)
    return wCode in listOf(
        WmoWeatherCode.SNOW_SLIGHT, WmoWeatherCode.SNOW_MODERATE,
        WmoWeatherCode.SNOW_HEAVY, WmoWeatherCode.SNOW_GRAINS,
        WmoWeatherCode.SNOW_SHOWERS_SLIGHT, WmoWeatherCode.SNOW_SHOWERS_HEAVY
    )
}

@Composable
fun WeatherBackground(weathercode: Int, isDay: Boolean, content: @Composable () -> Unit) {
    val (topColor, bottomColor) = getWeatherGradient(weathercode, isDay)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(topColor, bottomColor))
            )
    ) {
        WeatherParticles(weathercode = weathercode)
        content()
    }
}