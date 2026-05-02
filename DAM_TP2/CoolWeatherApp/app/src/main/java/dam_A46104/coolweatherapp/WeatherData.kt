package dam_A46104.coolweatherapp

// Classes de molde para o JSON
data class WeatherData(
    var latitude: Float,
    var longitude: Float,
    var timezone: String,
    var current_weather: CurrentWeather,
    var hourly: Hourly
)

data class CurrentWeather(
    var temperature: Float,
    var windspeed: Float,
    var winddirection: Int,
    var weathercode: Int,
    var is_day: Int, // Mudar o fundo consoante dia/ noite
    var time: String
)

data class Hourly(
    var time: ArrayList<String>,
    var temperature_2m: ArrayList<Float>,
    var weathercode: ArrayList<Int>,
    var pressure_msl: ArrayList<Float>,
    var windspeed_10m: ArrayList<Float>
)
