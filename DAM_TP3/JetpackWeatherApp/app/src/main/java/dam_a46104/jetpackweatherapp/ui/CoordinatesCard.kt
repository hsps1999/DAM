package dam_a46104.jetpackweatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CoordinatesCard(
    latitude: Float,
    longitude: Float,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    labelTitle: String,
    labelLatitude: String,
    labelLongitude: String
) {
    var latText by remember(latitude) { mutableStateOf(latitude.toString()) }
    var lonText by remember(longitude) { mutableStateOf(longitude.toString()) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text     = labelTitle,
                style    = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value         = latText,
                onValueChange = { newValue ->
                    latText = newValue
                    onLatitudeChange(newValue)
                },
                label = { Text(labelLatitude) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value         = lonText,
                onValueChange = { newValue ->
                    lonText = newValue
                    onLongitudeChange(newValue)
                },
                label           = { Text(labelLongitude) },
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}