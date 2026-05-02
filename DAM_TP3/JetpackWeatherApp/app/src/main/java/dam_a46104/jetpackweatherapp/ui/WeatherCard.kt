package dam_a46104.jetpackweatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherCard(
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,
    labelTemperature: String,
    labelWindSpeed: String,
    labelWindDirection: String,
    labelPressure: String,
    labelTime: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            WeatherRow(label = labelTemperature, value = "$temperature °C")
            WeatherRow(label = labelWindSpeed, value = "$windSpeed km/h")
            WeatherRow(label = labelWindDirection, value = "$windDirection°")
            WeatherRow(label = labelPressure, value = "$seaLevelPressure hPa")
            WeatherRow(label = labelTime, value = time)
        }
    }
}