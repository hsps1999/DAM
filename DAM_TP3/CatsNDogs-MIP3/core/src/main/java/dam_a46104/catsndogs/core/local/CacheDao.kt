package dam_a46104.catsndogs.core.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO de acesso à tabela `cached_images`.
 *
 * **Política LRU:** máximo de 50 registos. Após cada [insertAll], chamar
 * [pruneToLimit] para remover os registos mais antigos (por [CachedImage.cachedAt])
 * que excedam o limite. Favoritos não estão sujeitos a este limite (Extensão F).
 *
 * Todas as funções são `suspend` — devem ser chamadas a partir de uma coroutine.
 */
@Dao
interface CacheDao {

    /**
     * Insere ou substitui uma lista de imagens em cache.
     *
     * [OnConflictStrategy.REPLACE] garante comportamento de upsert:
     * se um registo com o mesmo [CachedImage.id] existir, é substituído
     * (e o [CachedImage.cachedAt] é atualizado).
     *
     * @param images Lista de entidades a inserir.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<CachedImage>)

    /**
     * Devolve todas as imagens em cache, ordenadas da mais recente para a mais antiga.
     *
     * @return Lista de [CachedImage] ordenada por [CachedImage.cachedAt] DESC.
     */
    @Query("SELECT * FROM cached_images ORDER BY cachedAt DESC")
    suspend fun getAllCached(): List<CachedImage>

    /**
     * Remove os registos mais antigos, mantendo apenas os 50 mais recentes.
     *
     * A subquery seleciona os IDs dos 50 registos com [CachedImage.cachedAt] mais alto
     * (mais recentes); todos os outros são apagados. Deve ser chamado após [insertAll].
     */
    @Query(
        """
        DELETE FROM cached_images
        WHERE id NOT IN (
            SELECT id FROM cached_images
            ORDER BY cachedAt DESC
            LIMIT 50
        )
        """
    )
    suspend fun pruneToLimit()

    /**
     * Procura um [CachedImage] pelo seu [id] na tabela Room.
     * Usado como fallback pelo Repository quando a lista em memória está vazia
     * (ex: após reiniciar a app).
     *
     * @param id Identificador único da imagem.
     * @return O [CachedImage] correspondente, ou `null` se não existir em cache.
     */
    @Query("SELECT * FROM cached_images WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): CachedImage?
}
