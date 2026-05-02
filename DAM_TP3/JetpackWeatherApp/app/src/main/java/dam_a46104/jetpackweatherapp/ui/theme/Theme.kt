package dam_a46104.jetpackweatherapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = Coral,
    onPrimary = TextOnDark,
    secondary = Coral,
    background = ClearNightTop,
    surface = GlassDark,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
    error = Coral
)

@Composable
fun JetpackWeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}