package dam_a46104.jetpackweatherapp.data

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteLocation(
    val name: String,
    val latitude: Float,
    val longitude: Float
)