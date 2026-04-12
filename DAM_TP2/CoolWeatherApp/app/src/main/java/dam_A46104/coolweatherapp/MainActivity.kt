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

            // TODO: Aqui irás adicionar findViewById para Temperatura, Vento, etc.

            // Atualizar a Pressão
            pressure.text = "Pressão: " + request.hourly.pressure_msl[12].toString() + " hPa"

            // Lógica para ir buscar a imagem baseada no código do clima
            val mapt = getWeatherCodeMap()
            val wCode = mapt.get(request.current_weather.weathercode)

            val wImage = when (wCode) {
                WMO_WeatherCode.CLEAR_SKY,
                WMO_WeatherCode.MAINLY_CLEAR,
                WMO_WeatherCode.PARTLY_CLOUDY -> if (day) wCode?.image + "day" else wCode?.image + "night"
                else -> wCode?.image
            }

            val res = resources

            // Vai buscar o ID dinâmico da imagem.
            // O getPackageName() ajuda o Android a encontrar a imagem com o nome exato da string 'wImage'
            val resID = res.getIdentifier(wImage, "drawable", packageName)

            // Segurança extra: só aplica a imagem se ela existir na pasta drawable (resID != 0)
            if (resID != 0) {
                val drawable = getDrawable(resID)
                weatherImage.setImageDrawable(drawable)
            }

            // TODO: Atualizar os outros elementos com dados de 'request.current_weather'
        }
    }
}