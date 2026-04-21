package dam_a46104.catsndogs.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dam_a46104.catsndogs.data.model.ImageItem

/**
 * Entidade Room que representa uma imagem marcada como favorita.
 *
 * Política FIFO: ao inserir o 6.º favorito, o [dam_a46104.catsndogs.data.repository.ImageRepository]
 * remove o registo com [favoritedAt] mais antigo via [FavoriteDao.getOldest].
 *
 * @property id          Chave primária — igual ao [ImageItem.id].
 * @property url         URL completo da imagem.
 * @property breed       Raça principal.
 * @property subBreed    Subração, se existir.
 * @property favoritedAt Timestamp Unix (ms) do momento em que foi marcado. Usado para FIFO.
 */
@Entity(tableName = "favorites")
data class FavoriteEntry(
    @PrimaryKey val id: String,
    val url: String,
    val breed: String,
    val subBreed: String?,
    val favoritedAt: Long
)

/** Converte um [ImageItem] de domínio para a entidade Room [FavoriteEntry]. */
fun ImageItem.toFavoriteEntry(): FavoriteEntry = FavoriteEntry(
    id = id,
    url = url,
    breed = breed,
    subBreed = subBreed,
    favoritedAt = System.currentTimeMillis()
)

/** Converte uma entidade Room [FavoriteEntry] para o modelo de domínio [ImageItem]. */
fun FavoriteEntry.toImageItem(): ImageItem = ImageItem(
    id = id,
    url = url,
    breed = breed,
    subBreed = subBreed,
    cachedAt = favoritedAt
)
