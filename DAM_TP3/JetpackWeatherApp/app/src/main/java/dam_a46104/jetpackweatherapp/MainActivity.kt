package dam_a46104.jetpackweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dam_a46104.jetpackweatherapp.ui.WeatherUI
import dam_a46104.jetpackweatherapp.ui.theme.JetpackWeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackWeatherAppTheme {
                WeatherUI()
            }
        }
    }
}