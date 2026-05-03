package dam_a46104.catsndogs.core.local

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO de acesso à tabela `favorites`.
 *
 * A lógica de negócio FIFO (máx. 5) é gerida pelo
 * [dam_a46104.catsndogs.core.repository.ImageRepository], não aqui —
 * o DAO mantém-se simples e testável de forma independente.
 *
 * Funções que devolvem [Flow] **não são** `suspend` (Room observa automaticamente).
 * Funções de escrita e contagem **são** `suspend` — chamadas a partir de uma coroutine.
 */
@Dao
interface FavoriteDao {

    /**
     * Insere ou substitui um favorito.
     * [OnConflictStrategy.REPLACE] garante upsert por [FavoriteEntry.id].
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FavoriteEntry)

    /** Remove o favorito com o [id] fornecido. */
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Devolve todos os favoritos ordenados do mais antigo para o mais recente (FIFO).
     * [Flow] — Room actualiza automaticamente quando a tabela muda.
     */
    @Query("SELECT * FROM favorites ORDER BY favoritedAt ASC")
    fun getAll(): Flow<List<FavoriteEntry>>

    /** Número total de favoritos actualmente guardados. */
    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun count(): Int

    /**
     * Devolve o favorito mais antigo (menor [FavoriteEntry.favoritedAt]),
     * para aplicar a política FIFO ao adicionar o 6.º item.
     */
    @Query("SELECT * FROM favorites ORDER BY favoritedAt ASC LIMIT 1")
    suspend fun getOldest(): FavoriteEntry?

    /**
     * Observa se o item com o dado [id] é favorito.
     * [Flow] — actualiza automaticamente quando o estado muda.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    /**
     * Versão síncrona de [isFavorite], usada internamente pelo Repository
     * para decidir o sentido do toggle sem necessitar de observer.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavoriteSync(id: String): Boolean

    /**
     * Procura um [FavoriteEntry] pelo seu [id] na tabela Room.
     * Usado como terceiro fallback pelo Repository quando o item não está
     * nem em memória nem na tabela de cache.
     *
     * @param id Identificador único da imagem.
     * @return O [FavoriteEntry] correspondente, ou `null` se não for favorito.
     */
    @Query("SELECT * FROM favorites WHERE id = :id LIMIT 1")
    suspend fun findByIdSync(id: String): FavoriteEntry?
}
