package dam_a46104.jetpackweatherapp.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import dam_a46104.jetpackweatherapp.data.WmoWeatherCode
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Determina que tipo de animação usar
enum class WeatherAnimation { NONE, RAIN, SNOW, THUNDER }

fun getWeatherAnimation(weathercode: Int): WeatherAnimation {
    val wCode = WmoWeatherCode.fromCode(weathercode) ?: return WeatherAnimation.NONE
    return when (wCode) {
        WmoWeatherCode.THUNDERSTORM,
        WmoWeatherCode.THUNDERSTORM_HAIL_SLIGHT,
        WmoWeatherCode.THUNDERSTORM_HAIL_HEAVY -> WeatherAnimation.THUNDER

        WmoWeatherCode.SNOW_SLIGHT,
        WmoWeatherCode.SNOW_MODERATE,
        WmoWeatherCode.SNOW_HEAVY,
        WmoWeatherCode.SNOW_GRAINS,
        WmoWeatherCode.SNOW_SHOWERS_SLIGHT,
        WmoWeatherCode.SNOW_SHOWERS_HEAVY -> WeatherAnimation.SNOW

        WmoWeatherCode.DRIZZLE_LIGHT,
        WmoWeatherCode.DRIZZLE_MODERATE,
        WmoWeatherCode.DRIZZLE_DENSE,
        WmoWeatherCode.FREEZING_DRIZZLE_LIGHT,
        WmoWeatherCode.FREEZING_DRIZZLE_DENSE,
        WmoWeatherCode.RAIN_SLIGHT,
        WmoWeatherCode.RAIN_MODERATE,
        WmoWeatherCode.RAIN_HEAVY,
        WmoWeatherCode.FREEZING_RAIN_LIGHT,
        WmoWeatherCode.FREEZING_RAIN_HEAVY,
        WmoWeatherCode.RAIN_SHOWERS_SLIGHT,
        WmoWeatherCode.RAIN_SHOWERS_MODERATE,
        WmoWeatherCode.RAIN_SHOWERS_VIOLENT -> WeatherAnimation.RAIN

        else -> WeatherAnimation.NONE
    }
}

@Composable
fun WeatherParticles(weathercode: Int) {
    when (getWeatherAnimation(weathercode)) {
        WeatherAnimation.RAIN -> RainAnimation()
        WeatherAnimation.SNOW -> SnowAnimation()
        WeatherAnimation.THUNDER -> ThunderAnimation()
        WeatherAnimation.NONE -> {}
    }
}

// ── CHUVA ──────────────────────────────────────────────────────────────────

data class RainDrop(
    val x: Float,
    val y: Float,
    val length: Float,
    val speed: Float,
    val alpha: Float
)

@Composable
fun RainAnimation() {
    val drops = remember {
        List(80) {
            RainDrop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                length = Random.nextFloat() * 0.04f + 0.02f,
                speed = Random.nextFloat() * 800 + 600,
                alpha = Random.nextFloat() * 0.5f + 0.2f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "rain")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drops.forEach { drop ->
            val currentY = ((drop.y + progress * (1000f / drop.speed)) % 1f)
            val startX = drop.x * size.width
            val startY = currentY * size.height
            val endY = startY + drop.length * size.height

            drawLine(
                color = Color.White.copy(alpha = drop.alpha),
                start = Offset(startX, startY),
                end = Offset(startX - 4f, endY),
                strokeWidth = 1.5f
            )
        }
    }
}

// ── NEVE ───────────────────────────────────────────────────────────────────

data class SnowFlake(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val drift: Float,
    val alpha: Float
)

@Composable
fun SnowAnimation() {
    val flakes = remember {
        List(60) {
            SnowFlake(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 1200 + 800,
                drift = Random.nextFloat() * 0.02f - 0.01f,
                alpha = Random.nextFloat() * 0.6f + 0.3f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "snow")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snow_progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        flakes.forEach { flake ->
            val currentY = ((flake.y + progress * (1500f / flake.speed)) % 1f)
            val currentX = (flake.x + flake.drift * progress * 10f) % 1f
            drawCircle(
                color = Color.White.copy(alpha = flake.alpha),
                radius = flake.radius,
                center = Offset(currentX * size.width, currentY * size.height)
            )
        }
    }
}

// ── RELÂMPAGOS ─────────────────────────────────────────────────────────────

@Composable
fun ThunderAnimation() {
    val transition = rememberInfiniteTransition(label = "thunder")

    // Flash de fundo
    val flash by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "thunder_flash"
    )

    // Relâmpago
    val boltProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bolt_progress"
    )

    val boltPositions = remember {
        List(2) { Pair(Random.nextFloat() * 0.6f + 0.2f, Random.nextFloat() * 0.3f) }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Flash branco subtil
        val flashAlpha = when {
            flash < 0.05f -> flash / 0.05f * 0.15f
            flash < 0.1f -> (0.1f - flash) / 0.05f * 0.15f
            else -> 0f
        }
        if (flashAlpha > 0f) {
            drawRect(color = Color.White.copy(alpha = flashAlpha))
        }

        // Relâmpagos
        boltPositions.forEachIndexed { index, (bx, by) ->
            val offset = index * 0.5f
            val localProgress = ((boltProgress + offset) % 1f)
            val boltAlpha = when {
                localProgress < 0.08f -> localProgress / 0.08f
                localProgress < 0.15f -> 1f - (localProgress - 0.08f) / 0.07f
                else -> 0f
            }
            if (boltAlpha > 0.05f) {
                drawBolt(
                    startX = bx * size.width,
                    startY = by * size.height,
                    alpha = boltAlpha
                )
            }
        }
    }
}

fun DrawScope.drawBolt(startX: Float, startY: Float, alpha: Float) {
    val path = Path().apply {
        moveTo(startX, startY)
        lineTo(startX - 15f, startY + 60f)
        lineTo(startX + 5f, startY + 60f)
        lineTo(startX - 20f, startY + 130f)
        lineTo(startX + 10f, startY + 75f)
        lineTo(startX - 8f, startY + 75f)
        close()
    }
    drawPath(
        path = path,
        color = Color(0xFFFFEB3B).copy(alpha = alpha),
    )
    // Glow effect
    drawPath(
        path = path,
        color = Color.White.copy(alpha = alpha * 0.4f),
    )
}