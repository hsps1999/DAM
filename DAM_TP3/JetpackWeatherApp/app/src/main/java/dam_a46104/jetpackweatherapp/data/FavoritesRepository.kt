package dam_a46104.jetpackweatherapp.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object FavoritesRepository {

    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorites"

    fun getFavorites(context: Context): List<FavoriteLocation> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveFavorite(context: Context, favorite: FavoriteLocation) {
        val current = getFavorites(context).toMutableList()
        if (current.none { it.name == favorite.name }) {
            current.add(favorite)
            persist(context, current)
        }
    }

    fun deleteFavorite(context: Context, favorite: FavoriteLocation) {
        val current = getFavorites(context).toMutableList()
        current.removeAll { it.name == favorite.name }
        persist(context, current)
    }

    private fun persist(context: Context, favorites: List<FavoriteLocation>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FAVORITES, Json.encodeToString(favorites)).apply()
    }
}