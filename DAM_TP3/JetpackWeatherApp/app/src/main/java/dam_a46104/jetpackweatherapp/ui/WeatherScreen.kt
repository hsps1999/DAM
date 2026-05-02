package dam_a46104.jetpackweatherapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam_a46104.jetpackweatherapp.R
import dam_a46104.jetpackweatherapp.data.WmoWeatherCode
import dam_a46104.jetpackweatherapp.ui.theme.Coral
import dam_a46104.jetpackweatherapp.ui.theme.TextOnDark
import dam_a46104.jetpackweatherapp.ui.theme.TextSubtleOnDark
import dam_a46104.jetpackweatherapp.viewmodel.WeatherViewModel
import androidx.compose.ui.tooling.preview.Preview
import dam_a46104.jetpackweatherapp.ui.theme.JetpackWeatherAppTheme

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val uiState by weatherViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val wCode = WmoWeatherCode.fromCode(uiState.weathercode)
    val wImageName = if (uiState.isDay)
        wCode?.dayImage ?: "clear_day"
    else
        wCode?.nightImage ?: "clear_night"
    val wIcon = context.resources.getIdentifier(wImageName, "drawable", context.packageName)
    val isSnow = isSnowCondition(uiState.weathercode)
    val isOnDark = !isSnow

    WeatherBackground(weathercode = uiState.weathercode, isDay = uiState.isDay) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LandscapeWeatherUI(
                wIcon = wIcon,
                uiState = uiState,
                isOnDark = isOnDark,
                onLatitudeChange = { newValue ->
                    newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
                },
                onLongitudeChange = { newValue ->
                    newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
                },
                onUpdateButtonClick = { weatherViewModel.fetchWeather() }
            )
        } else {
            PortraitWeatherUI(
                wIcon = wIcon,
                uiState = uiState,
                isOnDark = isOnDark,
                onLatitudeChange = { newValue ->
                    newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
                },
                onLongitudeChange = { newValue ->
                    newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
                },
                onUpdateButtonClick = { weatherViewModel.fetchWeather() }
            )
        }
    }
}

@Composable
fun TemperatureHero(
    temperature: Float,
    wIcon: Int,
    locationLabel: String,
    timeLabel: String,
    isOnDark: Boolean,
    large: Boolean = true
) {
    val textColor = if (isOnDark) TextOnDark else Color(0xFF1A1A2E)
    val subtleColor = if (isOnDark) TextSubtleOnDark else Color(0x991A1A2E)
    val tempSize = if (large) 96.sp else 72.sp

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = tempSize, fontWeight = FontWeight.Black, color = textColor)) {
                    append(temperature.toInt().toString())
                }
                withStyle(SpanStyle(fontSize = (tempSize.value * 0.4f).sp, fontWeight = FontWeight.Light, color = subtleColor)) {
                    append("°")
                }
            }
        )
        Spacer(modifier = Modifier.width(12.dp))
        if (wIcon != 0) {
            Image(
                painter = painterResource(id = wIcon),
                contentDescription = null,
                modifier = Modifier.size(if (large) 80.dp else 60.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = locationLabel,
        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
    )
    Text(
        text = timeLabel,
        style = TextStyle(fontSize = 11.sp, color = subtleColor, letterSpacing = 0.5.sp)
    )
}

@Composable
fun PortraitWeatherUI(
    wIcon: Int,
    uiState: WeatherUiState,
    isOnDark: Boolean,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {
    val textColor = if (isOnDark) TextOnDark else Color(0xFF1A1A2E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TemperatureHero(
            temperature = uiState.temperature,
            wIcon = wIcon,
            locationLabel = "${uiState.latitude}, ${uiState.longitude}", // para chamar a localização das coordenadas reais em vez de Lisbon que estava hardcoded
            timeLabel = "${uiState.time} LOCAL · ${
                WmoWeatherCode.fromCode(uiState.weathercode)?.name?.replace("_", " ") ?: ""
            }",
            isOnDark = isOnDark,
            large = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        CoordinatesCard(
            latitude = uiState.latitude,
            longitude = uiState.longitude,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange,
            labelTitle = stringResource(R.string.coordinates),
            labelLatitude = stringResource(R.string.latitude),
            labelLongitude = stringResource(R.string.longitude),
            isOnDark = isOnDark
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(color = Coral, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (uiState.errorMessage != null) {
            Text(text = uiState.errorMessage, color = Coral)
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label = stringResource(R.string.wind_speed),
                    value = uiState.windspeed.toInt().toString(),
                    unit = "km/h · ${windDirection(uiState.winddirection)}",
                    icon = Icons.Default.Air,
                    isOnDark = isOnDark,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                MetricCard(
                    label = stringResource(R.string.sea_level_pressure),
                    value = uiState.seaLevelPressure.toInt().toString(),
                    unit = "hPa",
                    icon = Icons.Default.Speed,
                    isOnDark = isOnDark,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Coral)
        ) {
            Text(
                text = "${stringResource(R.string.update_weather)} →",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextOnDark
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun LandscapeWeatherUI(
    wIcon: Int,
    uiState: WeatherUiState,
    isOnDark: Boolean,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Coluna esquerda — temperatura + localização
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            TemperatureHero(
                temperature = uiState.temperature,
                wIcon = wIcon,
                locationLabel = "${uiState.latitude}, ${uiState.longitude}",
                timeLabel = "${uiState.time} · ${
                    WmoWeatherCode.fromCode(uiState.weathercode)?.name?.replace("_", " ") ?: ""
                }",
                isOnDark = isOnDark,
                large = false
            )
        }

        // Coluna direita — coordenadas + metrics + botão
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CoordinatesCard(
                latitude = uiState.latitude,
                longitude = uiState.longitude,
                onLatitudeChange = onLatitudeChange,
                onLongitudeChange = onLongitudeChange,
                labelTitle = stringResource(R.string.coordinates),
                labelLatitude = stringResource(R.string.latitude),
                labelLongitude = stringResource(R.string.longitude),
                isOnDark = isOnDark
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(color = Coral)
            } else if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage, color = Coral)
            } else {
                MetricCard(
                    label = stringResource(R.string.wind_speed),
                    value = uiState.windspeed.toInt().toString(),
                    unit = "km/h · ${windDirection(uiState.winddirection)}",
                    icon = Icons.Default.Air,
                    isOnDark = isOnDark,
                    modifier = Modifier.fillMaxWidth()
                )
                MetricCard(
                    label = stringResource(R.string.sea_level_pressure),
                    value = uiState.seaLevelPressure.toInt().toString(),
                    unit = "hPa",
                    icon = Icons.Default.Speed,
                    isOnDark = isOnDark,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = onUpdateButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Coral)
            ) {
                Text(
                    text = "${stringResource(R.string.update_weather)} →",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextOnDark
                    )
                )
            }
        }
    }
}

fun windDirection(degrees: Int): String {
    val dirs = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    return dirs[((degrees + 22) / 45) % 8]
}

@Preview(showBackground = true)
@Composable
fun WeatherUIPreview() {
    JetpackWeatherAppTheme {
        WeatherBackground(weathercode = 0, isDay = true) {
            PortraitWeatherUI(
                wIcon = 0,
                uiState = WeatherUiState(),
                isOnDark = true,
                onLatitudeChange = {},
                onLongitudeChange = {},
                onUpdateButtonClick = {}
            )
        }
    }
}