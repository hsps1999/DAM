package dam_a46104.jetpackweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_a46104.jetpackweatherapp.data.WeatherApiClient
import dam_a46104.jetpackweatherapp.ui.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // Coordenadas pendentes — atualizadas durante digitação mas só
    // aplicadas ao uiState no fetchWeather()
    private var pendingLatitude: Float = _uiState.value.latitude
    private var pendingLongitude: Float = _uiState.value.longitude

    init {
        fetchWeather()
    }

    fun updateLatitude(lat: Float) {
        pendingLatitude = lat
    }

    fun updateLongitude(lon: Float) {
        pendingLongitude = lon
    }

    fun fetchWeather() {
        viewModelScope.launch {
            // Aplica as coordenadas pendentes ao estado antes do fetch
            _uiState.update {
                it.copy(
                    latitude = pendingLatitude,
                    longitude = pendingLongitude,
                    isLoading = true,
                    errorMessage = null
                )
            }

            val data = WeatherApiClient.getWeather(pendingLatitude, pendingLongitude)

            if (data != null) {
                val currentTimeIndex = data.hourly.time.indexOf(data.currentWeather.time)
                val pressure = if (currentTimeIndex >= 0)
                    data.hourly.pressureMsl[currentTimeIndex]
                else
                    data.hourly.pressureMsl.firstOrNull() ?: 0f

                _uiState.update {
                    it.copy(
                        temperature = data.currentWeather.temperature,
                        windspeed = data.currentWeather.windspeed,
                        winddirection = data.currentWeather.winddirection,
                        weathercode = data.currentWeather.weathercode,
                        seaLevelPressure = pressure,
                        time = data.currentWeather.time,
                        isDay = data.currentWeather.isDay == 1,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Failed to fetch weather data")
                }
            }
        }
    }
}