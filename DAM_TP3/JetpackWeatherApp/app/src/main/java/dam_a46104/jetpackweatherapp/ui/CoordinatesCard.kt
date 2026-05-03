package dam_a46104.jetpackweatherapp.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_a46104.jetpackweatherapp.R
import dam_a46104.jetpackweatherapp.ui.theme.Coral
import dam_a46104.jetpackweatherapp.ui.theme.GlassLight
import dam_a46104.jetpackweatherapp.ui.theme.TextOnDark
import dam_a46104.jetpackweatherapp.ui.theme.TextSubtleOnDark

@Composable
fun CoordinatesCard(
    latitude: Float,
    longitude: Float,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onLocationPicked: (Float, Float) -> Unit,
    labelTitle: String,
    labelLatitude: String,
    labelLongitude: String,
    isOnDark: Boolean = true
) {
    var latText by remember(latitude) { mutableStateOf(latitude.toString()) }
    var lonText by remember(longitude) { mutableStateOf(longitude.toString()) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val lat = result.data?.getFloatExtra(LocationPickerActivity.EXTRA_LATITUDE, latitude) ?: latitude
            val lon = result.data?.getFloatExtra(LocationPickerActivity.EXTRA_LONGITUDE, longitude) ?: longitude
            onLocationPicked(lat, lon)
        }
    }

    val cardColor = if (isOnDark) GlassLight else Color(0x99FFFFFF)
    val textColor = if (isOnDark) TextOnDark else Color(0xFF1A1A2E)
    val subtleColor = if (isOnDark) TextSubtleOnDark else Color(0x991A1A2E)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Coral,
        unfocusedBorderColor = subtleColor,
        focusedLabelColor = Coral,
        unfocusedLabelColor = subtleColor,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = Coral
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = labelTitle.uppercase(),
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            color = subtleColor
                        )
                    )
                    Text(
                        text = "WGS-84 · DECIMAL",
                        style = TextStyle(fontSize = 9.sp, color = subtleColor),
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )
                }
                IconButton(
                    onClick = {
                        val intent = android.content.Intent(
                            context,
                            LocationPickerActivity::class.java
                        ).apply {
                            putExtra(LocationPickerActivity.EXTRA_LATITUDE, latitude)
                            putExtra(LocationPickerActivity.EXTRA_LONGITUDE, longitude)
                        }
                        launcher.launch(intent)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = stringResource(R.string.pick_location),
                        tint = Coral
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = latText,
                    onValueChange = { newValue ->
                        latText = newValue
                        onLatitudeChange(newValue)
                    },
                    label = {
                        Text(
                            labelLatitude.uppercase(),
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 0.5.sp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = lonText,
                    onValueChange = { newValue ->
                        lonText = newValue
                        onLongitudeChange(newValue)
                    },
                    label = {
                        Text(
                            labelLongitude.uppercase(),
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 0.5.sp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = fieldColors,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}