package dam_a46104.jetpackweatherapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam_a46104.jetpackweatherapp.R
import dam_a46104.jetpackweatherapp.data.WmoWeatherCode
import dam_a46104.jetpackweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val uiState by weatherViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val wCode = WmoWeatherCode.fromCode(uiState.weathercode)
    val wImageName = when (wCode) {
        WmoWeatherCode.CLEAR_SKY,
        WmoWeatherCode.MAINLY_CLEAR,
        WmoWeatherCode.PARTLY_CLOUDY -> if (uiState.isDay) "${wCode.image}day" else "${wCode.image}night"
        else -> wCode?.image ?: "overcast"
    }
    val wIcon = context.resources.getIdentifier(wImageName, "drawable", context.packageName)

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LandscapeWeatherUI(
            wIcon = wIcon,
            uiState = uiState,
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

@Composable
fun PortraitWeatherUI(
    wIcon: Int,
    uiState: WeatherUiState,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (wIcon != 0) {
            Image(
                painter = painterResource(id = wIcon),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
        }

        CoordinatesCard(
            latitude = uiState.latitude,
            longitude = uiState.longitude,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange,
            labelTitle = stringResource(R.string.coordinates),
            labelLatitude = stringResource(R.string.latitude),
            labelLongitude = stringResource(R.string.longitude)
        )

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            WeatherCard(
                temperature = uiState.temperature,
                windSpeed = uiState.windspeed,
                windDirection = uiState.winddirection,
                weathercode = uiState.weathercode,
                seaLevelPressure = uiState.seaLevelPressure,
                time = uiState.time,
                labelTemperature = stringResource(R.string.temperature),
                labelWindSpeed = stringResource(R.string.wind_speed),
                labelWindDirection = stringResource(R.string.wind_direction),
                labelPressure = stringResource(R.string.sea_level_pressure),
                labelTime = stringResource(R.string.time)
            )
        }

        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.update_weather))
        }
    }
}

@Composable
fun LandscapeWeatherUI(
    wIcon: Int,
    uiState: WeatherUiState,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Coluna esquerda — imagem + coordenadas + botão
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (wIcon != 0) {
                Image(
                    painter = painterResource(id = wIcon),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }
            CoordinatesCard(
                latitude = uiState.latitude,
                longitude = uiState.longitude,
                onLatitudeChange = onLatitudeChange,
                onLongitudeChange = onLongitudeChange,
                labelTitle = stringResource(R.string.coordinates),
                labelLatitude = stringResource(R.string.latitude),
                labelLongitude = stringResource(R.string.longitude)
            )
            Button(
                onClick = onUpdateButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_weather))
            }
        }

        // Coluna direita — dados meteorológicos
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                WeatherCard(
                    temperature = uiState.temperature,
                    windSpeed = uiState.windspeed,
                    windDirection = uiState.winddirection,
                    weathercode = uiState.weathercode,
                    seaLevelPressure = uiState.seaLevelPressure,
                    time = uiState.time,
                    labelTemperature = stringResource(R.string.temperature),
                    labelWindSpeed = stringResource(R.string.wind_speed),
                    labelWindDirection = stringResource(R.string.wind_direction),
                    labelPressure = stringResource(R.string.sea_level_pressure),
                    labelTime = stringResource(R.string.time)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}