package dam_a46104.catsndogs.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dam_a46104.catsndogs.data.model.ImageItem

/**
 * Entidade Room que representa uma imagem guardada em cache local.
 *
 * Mapeada bidirecionalmente para/de [ImageItem] via as funções de extensão
 * [ImageItem.toCachedImage] e [CachedImage.toImageItem] definidas neste ficheiro.
 *
 * O limite máximo de 50 registos é gerido por [CacheDao.pruneToLimit].
 *
 * @property id       Chave primária, derivada do nome do ficheiro no URL (ex: "n02088094_1003").
 * @property url      URL completo da imagem.
 * @property breed    Raça principal.
 * @property subBreed Subração, se existir. Nulo caso contrário.
 * @property cachedAt Timestamp Unix (ms) do momento de criação do [ImageItem] — usado para LRU.
 */
@Entity(tableName = "cached_images")
data class CachedImage(
    @PrimaryKey val id: String,
    val url: String,
    val breed: String,
    val subBreed: String?,
    val cachedAt: Long
)

/** Converte um [ImageItem] de domínio para a entidade Room [CachedImage]. */
fun ImageItem.toCachedImage(): CachedImage = CachedImage(
    id = id,
    url = url,
    breed = breed,
    subBreed = subBreed,
    cachedAt = cachedAt
)

/** Converte uma entidade Room [CachedImage] para o modelo de domínio [ImageItem]. */
fun CachedImage.toImageItem(): ImageItem = ImageItem(
    id = id,
    url = url,
    breed = breed,
    subBreed = subBreed,
    cachedAt = cachedAt
)
