package dam_a46104.catsndogs.core.model

/**
 * Modelo de domínio que representa uma imagem de cão obtida da Dog CEO API.
 *
 * @property id       Identificador único derivado do nome do ficheiro no URL (ex.: "n02110958_15626").
 * @property url      URL completo da imagem.
 * @property breed    Raça principal extraída do URL (ex.: "hound").
 * @property subBreed Subração, se existir (ex.: "afghan"). Nulo caso contrário.
 * @property isFavorite Indica se o item está marcado como favorito. Default: false.
 * @property cachedAt Timestamp Unix (ms) para política de cache LRU. Default: momento de criação.
 */
data class ImageItem(
    val id: String,
    val url: String,
    val breed: String,
    val subBreed: String? = null,
    val isFavorite: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)
