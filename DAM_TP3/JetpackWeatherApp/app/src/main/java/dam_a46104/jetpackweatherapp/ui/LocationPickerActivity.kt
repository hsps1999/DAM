package dam_a46104.jetpackweatherapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dam_a46104.jetpackweatherapp.R
import dam_a46104.jetpackweatherapp.ui.theme.Coral
import dam_a46104.jetpackweatherapp.ui.theme.JetpackWeatherAppTheme
import dam_a46104.jetpackweatherapp.ui.theme.TextOnDark
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class LocationPickerActivity : ComponentActivity() {

    companion object {
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName

        val initialLat = intent.getFloatExtra(EXTRA_LATITUDE, 38.7223f)
        val initialLon = intent.getFloatExtra(EXTRA_LONGITUDE, -9.1393f)

        setContent {
            JetpackWeatherAppTheme {
                LocationPickerScreen(
                    initialLat = initialLat,
                    initialLon = initialLon,
                    onConfirm = { lat, lon ->
                        val result = Intent().apply {
                            putExtra(EXTRA_LATITUDE, lat)
                            putExtra(EXTRA_LONGITUDE, lon)
                        }
                        setResult(Activity.RESULT_OK, result)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LocationPickerScreen(
    initialLat: Float = 38.7223f,
    initialLon: Float = -9.1393f,
    onConfirm: (Float, Float) -> Unit
) {
    var selectedPoint by remember {
        mutableStateOf(GeoPoint(initialLat.toDouble(), initialLon.toDouble()))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(10.0)
                    controller.setCenter(selectedPoint)

                    val marker = Marker(this).apply {
                        position = selectedPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        isDraggable = true
                    }
                    overlays.add(marker)

                    val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            selectedPoint = p
                            marker.position = p
                            invalidate()
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint) = false
                    })
                    overlays.add(0, eventsOverlay)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                onConfirm(
                    selectedPoint.latitude.toFloat(),
                    selectedPoint.longitude.toFloat()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Coral)
        ) {
            Text(
                text = stringResource(R.string.confirm_location).uppercase() + " →",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextOnDark
                )
            )
        }
    }
}