package dam_a46104.jetpackweatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dam_a46104.jetpackweatherapp.data.FavoriteLocation
import dam_a46104.jetpackweatherapp.data.FavoritesRepository
import dam_a46104.jetpackweatherapp.data.WeatherApiClient
import dam_a46104.jetpackweatherapp.ui.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites: StateFlow<List<FavoriteLocation>> = _favorites.asStateFlow()

    private var pendingLatitude: Float = _uiState.value.latitude
    private var pendingLongitude: Float = _uiState.value.longitude

    init {
        loadFavorites()
        fetchWeather()
    }

    private fun loadFavorites() {
        _favorites.value = FavoritesRepository.getFavorites(getApplication())
    }

    fun addFavorite(name: String) {
        val favorite = FavoriteLocation(
            name = name,
            latitude = _uiState.value.latitude,
            longitude = _uiState.value.longitude
        )
        FavoritesRepository.saveFavorite(getApplication(), favorite)
        loadFavorites()
    }

    fun deleteFavorite(favorite: FavoriteLocation) {
        FavoritesRepository.deleteFavorite(getApplication(), favorite)
        loadFavorites()
    }

    fun onFavoriteTap(favorite: FavoriteLocation) {
        onLocationPicked(favorite.latitude, favorite.longitude)
    }

    fun updateLatitude(lat: Float) {
        pendingLatitude = lat
    }

    fun updateLongitude(lon: Float) {
        pendingLongitude = lon
    }

    fun fetchWeather() {
        viewModelScope.launch {
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

                val feelsLike = if (currentTimeIndex >= 0)
                    data.hourly.apparentTemperature[currentTimeIndex]
                else
                    data.hourly.apparentTemperature.firstOrNull() ?: 0f

                val humidity = if (currentTimeIndex >= 0)
                    data.hourly.relativeHumidity2m[currentTimeIndex]
                else
                    data.hourly.relativeHumidity2m.firstOrNull() ?: 0

                val visibility = if (currentTimeIndex >= 0)
                    data.hourly.visibility[currentTimeIndex] / 1000f // metros → km
                else
                    data.hourly.visibility.firstOrNull()?.div(1000f) ?: 0f

                val uvIndex = data.daily.uvIndexMax.firstOrNull() ?: 0f

                _uiState.update {
                    it.copy(
                        temperature = data.currentWeather.temperature,
                        windspeed = data.currentWeather.windspeed,
                        winddirection = data.currentWeather.winddirection,
                        weathercode = data.currentWeather.weathercode,
                        seaLevelPressure = pressure,
                        time = data.currentWeather.time,
                        isDay = data.currentWeather.isDay == 1,
                        feelsLike = feelsLike,
                        humidity = humidity,
                        visibility = visibility,
                        uvIndex = uvIndex,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onLocationPicked(lat: Float, lon: Float) {
        pendingLatitude = lat
        pendingLongitude = lon
        fetchWeather()
    }
}