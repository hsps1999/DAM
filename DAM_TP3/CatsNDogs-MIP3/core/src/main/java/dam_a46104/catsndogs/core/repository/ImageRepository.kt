package dam_a46104.catsndogs.core.repository

import dam_a46104.catsndogs.core.R
import dam_a46104.catsndogs.core.common.UiState
import dam_a46104.catsndogs.core.local.AppDatabase
import dam_a46104.catsndogs.core.local.toCachedImage
import dam_a46104.catsndogs.core.local.toFavoriteEntry
import dam_a46104.catsndogs.core.local.toImageItem
import dam_a46104.catsndogs.core.model.ImageItem
import dam_a46104.catsndogs.core.remote.DogApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Única fonte de verdade da aplicação para dados de imagens de cães.
 *
 * Delega na API para fetch e persiste os resultados no [AppDatabase] (Room).
 * Implementa cache LRU de até 50 itens via [dam_a46104.catsndogs.core.local.CacheDao.pruneToLimit].
 *
 * Expõe streams reativos via [Flow] — cada módulo de UI converte conforme necessário:
 * - `:app-xml` → `.asLiveData()`
 * - `:app-compose` → `collectAsStateWithLifecycle()`
 *
 * @property api      Instância de [DogApiService] usada para chamadas de rede.
 * @property database Base de dados Room — resolve os DAOs internamente.
 */
class ImageRepository private constructor(
    private val api: DogApiService,
    private val database: AppDatabase
) {

    /** DAO de cache, resolvido a partir do [database]. */
    private val cacheDao = database.cacheDao()

    /** DAO de favoritos, resolvido a partir do [database]. */
    private val favoriteDao = database.favoriteDao()

    /**
     * Cache em memória do último conjunto de imagens obtido.
     * Permite [findById] síncrono sem hit ao Room.
     * Atualizado a cada [fetchRandomImages] bem-sucedido.
     */
    private var cachedImages: List<ImageItem> = emptyList()

    /**
     * Obtém uma lista de imagens aleatórias da Dog CEO API e mapeia-as
     * para o modelo de domínio [ImageItem].
     *
     * **Comportamento offline:** em caso de [IOException] (sem rede, timeout),
     * tenta devolver o conteúdo em cache Room como [UiState.Success] com
     * `isFromCache = true`. Se a cache também estiver vazia, devolve
     * [UiState.Error] com o ID de string apropriado.
     *
     * @param count Número de imagens a pedir à API (máximo 50).
     * @return [UiState] com o resultado da operação:
     *   - [UiState.Success] com dados frescos ou da cache
     *   - [UiState.Error] com ID de string resource em caso de falha sem fallback
     */
    suspend fun fetchRandomImages(count: Int): UiState<List<ImageItem>> {
        return try {
            val response = api.getRandomImages(count)
            val result = response.message.map { url -> url.toImageItem() }
            cachedImages = result
            // Persistir em Room e aplicar pruning LRU no dispatcher de I/O
            withContext(Dispatchers.IO) {
                cacheDao.insertAll(result.map { it.toCachedImage() })
                cacheDao.pruneToLimit()
            }
            UiState.Success(result, isFromCache = false)
        } catch (e: IOException) {
            // Sem rede ou timeout — tentar cache Room antes de devolver erro
            val cached = getCachedImages()
            if (cached.isNotEmpty()) {
                cachedImages = cached  // atualiza memória para findById() continuar a funcionar
                UiState.Success(cached, isFromCache = true)
            } else {
                UiState.Error(R.string.error_no_network)
            }
        } catch (e: HttpException) {
            // Erro do servidor: não há fallback (o servidor respondeu com erro)
            UiState.Error(R.string.error_server)
        } catch (e: Exception) {
            // Qualquer outro erro inesperado (ex: JSON malformado)
            UiState.Error(R.string.error_unknown)
        }
    }

    /**
     * Procura um [ImageItem] por [id] com fallback em três camadas:
     * 1. Lista em memória (`cachedImages`) — sem I/O, instantâneo
     * 2. Tabela `cached_images` do Room — persiste entre sessões
     * 3. Tabela `favorites` do Room — garante que favoritos sempre carregam
     *
     * Usado pelo DetailsViewModel para resolver o item a apresentar sem chamada de rede.
     *
     * @param id Identificador único da imagem.
     * @return O [ImageItem] correspondente, ou `null` se não existir em nenhuma camada.
     */
    suspend fun findById(id: String): ImageItem? = withContext(Dispatchers.IO) {
        cachedImages.find { it.id == id }
            ?: cacheDao.findById(id)?.toImageItem()
            ?: favoriteDao.findByIdSync(id)?.toImageItem()
    }

    /**
     * Devolve todos os itens persistidos em cache Room, do mais recente para o mais antigo.
     * Máximo de 50 itens (garantido por [dam_a46104.catsndogs.core.local.CacheDao.pruneToLimit]).
     *
     * @return Lista de [ImageItem] lida do Room via [Dispatchers.IO].
     */
    private suspend fun getCachedImages(): List<ImageItem> =
        withContext(Dispatchers.IO) {
            cacheDao.getAllCached().map { it.toImageItem() }
        }

    /**
     * Alterna o estado de favorito de um [ImageItem] (toggle).
     *
     * Se já for favorito, remove-o. Caso contrário:
     * - Se já existirem 5 favoritos, remove o mais antigo (política FIFO)
     * - Insere o novo favorito
     *
     * @param item O [ImageItem] a marcar/desmarcar.
     */
    suspend fun toggleFavorite(item: ImageItem) {
        withContext(Dispatchers.IO) {
            if (favoriteDao.isFavoriteSync(item.id)) {
                favoriteDao.deleteById(item.id)
            } else {
                if (favoriteDao.count() >= 5) {
                    favoriteDao.getOldest()?.let { favoriteDao.deleteById(it.id) }
                }
                favoriteDao.insert(item.toFavoriteEntry())
            }
        }
    }

    /**
     * Devolve a lista de favoritos como [Flow], mapeada para [ImageItem].
     * Ordenada por [dam_a46104.catsndogs.core.local.FavoriteEntry.favoritedAt] ASC (FIFO).
     *
     * Cada módulo de UI converte conforme necessário:
     * - `:app-xml` → `.asLiveData()`
     * - `:app-compose` → `collectAsStateWithLifecycle()`
     */
    fun getFavorites(): Flow<List<ImageItem>> =
        favoriteDao.getAll().map { list -> list.map { it.toImageItem() } }

    /**
     * Observa se o item com o dado [id] é favorito, via [Flow].
     *
     * @param id Identificador único da imagem.
     */
    fun isFavorite(id: String): Flow<Boolean> = favoriteDao.isFavorite(id)

    companion object {

        @Volatile
        private var instance: ImageRepository? = null

        /**
         * Devolve a instância singleton de [ImageRepository].
         * Thread-safe via double-checked locking.
         *
         * @param api      Instância de [DogApiService] a injectar.
         * @param database [AppDatabase] que fornece os DAOs de persistência.
         */
        fun getInstance(api: DogApiService, database: AppDatabase): ImageRepository =
            instance ?: synchronized(this) {
                instance ?: ImageRepository(api, database).also { instance = it }
            }
    }
}

// ---------- Funções de mapeamento (privadas ao ficheiro) ----------

/**
 * Converte um URL de imagem da Dog CEO API num [ImageItem].
 *
 * Exemplo de URL:
 * `https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg`
 *  → id = "n02088094_1003", breed = "hound", subBreed = "afghan"
 */
private fun String.toImageItem(): ImageItem {
    val segments = this.trimEnd('/').split("/")

    // Segmento imediatamente a seguir a "breeds"
    val breedsIndex = segments.indexOf("breeds")
    val breedSegment = if (breedsIndex >= 0 && breedsIndex + 1 < segments.size) {
        segments[breedsIndex + 1]
    } else {
        "unknown"
    }

    // Raça principal e subraça (separadas por "-")
    val breedParts = breedSegment.split("-", limit = 2)
    val breed = breedParts[0]
    val subBreed = if (breedParts.size > 1) breedParts[1] else null

    // ID = nome do ficheiro sem extensão
    val filename = segments.last()
    val id = filename.substringBeforeLast(".")

    return ImageItem(
        id = id,
        url = this,
        breed = breed,
        subBreed = subBreed
    )
}
