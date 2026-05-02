package dam_a46104.jetpackweatherapp.ui

data class WeatherUiState(
    val latitude: Float         = 38.7223f,
    val longitude: Float        = -9.1393f,
    val temperature: Float      = 0f,
    val windspeed: Float        = 0f,
    val winddirection: Int      = 0,
    val weathercode: Int        = 0,
    val seaLevelPressure: Float = 0f,
    val time: String            = "",
    val isDay: Boolean          = true,
    val isLoading: Boolean      = false,
    val errorMessage: String?   = null
)