package dam_A46104.coolweatherapp

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {

    // Variável para testar os temas de Dia/Noite
    var day = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. O SET THEME TEM DE SER A PRIMEIRA COISA A ACONTECER
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                if (day) {
                    setTheme(R.style.Theme_Day)
                } else {
                    setTheme(R.style.Theme_Night)
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (day) {
                    setTheme(R.style.Theme_Day_Land)
                } else {
                    setTheme(R.style.Theme_Night_Land)
                }
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Só DEPOIS é desenhado o ecrã
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Referências aos elementos
        val btnUpdate = findViewById<Button>(R.id.updateButton)
        val latInput = findViewById<EditText>(R.id.latitudeInput)
        val longInput = findViewById<EditText>(R.id.longitudeInput)

        // 4. Chamada inicial (Lisboa)
        val initialLat = latInput.text.toString().toFloatOrNull() ?: 38.076f
        val initialLong = longInput.text.toString().toFloatOrNull() ?: -9.12f
        fetchWeatherData(initialLat, initialLong).start()

        // 5. Configurar o botão Update
        btnUpdate.setOnClickListener {
            val lat = latInput.text.toString().toFloatOrNull() ?: 0f
            val long = longInput.text.toString().toFloatOrNull() ?: 0f
            fetchWeatherData(lat, long).start()
        }
    }

    // Fazer o pedido à Open-Meteo e converter o JSON
    private fun WeatherAPI_Call(lat: Float, long: Float): WeatherData {
        val reqString = buildString {
            // Removi todos os espaços inválidos que estavam no código do PDF
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${long}&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
        }

        val url = URL(reqString)
        url.openStream().use { stream ->
            val request = Gson().fromJson(InputStreamReader(stream, "UTF-8"), WeatherData::class.java)
            return request
        }
    }

    // Devolve a thread que vai fazer o trabalho pesado
    private fun fetchWeatherData(lat: Float, long: Float): Thread {
        return Thread {
            val weather = WeatherAPI_Call(lat, long)
            updateUI(weather)
        }
    }

    // Atualizar a interface gráfica
    private fun updateUI(request: WeatherData) {
        runOnUiThread {
            val weatherImage: ImageView = findViewById(R.id.weatherImage)
            val pressure: TextView = findViewById(R.id.pressureValue)

            // Encontrar as novas TextViews no XML
            val temperature: TextView = findViewById(R.id.temperatureValue)
            val windSpeed: TextView = findViewById(R.id.windSpeedValue)
            val windDir: TextView = findViewById(R.id.windDirValue)
            val time: TextView = findViewById(R.id.timeValue)

            // Atualizar a Pressão
            pressure.text = "${getString(R.string.pressure_label)}: ${request.hourly.pressure_msl[12]} hPa"

            // --- NOVA LÓGICA DE IMAGENS (A LER DO XML) ---

            // Carregar as duas listas do ficheiro arrays.xml
            val codesArray = resources.getIntArray(R.array.weather_codes)
            val imagesArray = resources.getStringArray(R.array.weather_images)

            // Código que a API devolve agora
            val currentCode = request.current_weather.weathercode

            // Procurar em que posição (índice) da lista está o código
            val index = codesArray.indexOf(currentCode)

            var wImage = ""

            // Se encontrou o código na lista (index diferente de -1)
            if (index != -1) {
                val baseImageName = imagesArray[index]

                // Se for céu limpo (0), maioritariamente limpo (1) ou parcialmente nublado (2),
                // acrescentar o "day" ou "night" ao fim do nome da imagem
                wImage = when (currentCode) {
                    0, 1, 2 -> if (day) baseImageName + "day" else baseImageName + "night"
                    else -> baseImageName
                }
            }

            // 4. Aplicar a imagem na interface
            val resID = resources.getIdentifier(wImage, "drawable", packageName)

            if (resID != 0) {
                val drawable = getDrawable(resID)
                weatherImage.setImageDrawable(drawable)
            } else {
                // Prevenção de erros: se a imagem não existir, mete uma por omissão (ex: ic_launcher)
                weatherImage.setImageResource(R.mipmap.ic_launcher)
            }

            // Atualizar os restantes elementos com os dados reais
            val current = request.current_weather

            temperature.text = "${getString(R.string.temp_label)}: ${current.temperature} °C"
            windSpeed.text = "${getString(R.string.wind_speed_label)}: ${current.windspeed} km/h"
            windDir.text = "${getString(R.string.wind_dir_label)}: ${current.winddirection}°"

            // A hora vem no formato "2023-03-10T12:00". Troca do "T" por um espaço
            val formattedTime = current.time.replace("T", " ")
            time.text = "${getString(R.string.time_label)}: $formattedTime"
        }
    }
}