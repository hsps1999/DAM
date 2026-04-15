package dam_a46104.catsndogs.data.repository

import dam_a46104.catsndogs.data.model.ImageItem
import dam_a46104.catsndogs.data.remote.DogApiService

/**
 * Única fonte de verdade da aplicação para dados de imagens de cães.
 *
 * Na Phase 1, delega directamente na API sem cache local.
 * A lógica de cache e fallback offline será adicionada na Phase 2.
 *
 * @property api Instância de [DogApiService] usada para chamadas de rede.
 */
class ImageRepository private constructor(
    private val api: DogApiService
) {

    /**
     * Obtém uma lista de imagens aleatórias da Dog CEO API e mapeia-as
     * para o modelo de domínio [ImageItem].
     *
     * O [id] de cada item é derivado do nome do ficheiro no URL (determinístico).
     * A [breed] e a [subBreed] são extraídas do segmento do URL a seguir a `/breeds/`.
     *
     * @param count Número de imagens a pedir (máximo 50).
     * @return Lista de [ImageItem] pronta a apresentar na UI.
     * @throws Exception Propaga exceções de rede para o caller (ViewModel) tratar.
     */
    suspend fun fetchRandomImages(count: Int): List<ImageItem> {
        val response = api.getRandomImages(count)
        return response.message.map { url -> url.toImageItem() }
    }

    companion object {

        @Volatile
        private var instance: ImageRepository? = null

        /**
         * Devolve a instância singleton de [ImageRepository].
         * Thread-safe via double-checked locking.
         *
         * @param api Instância de [DogApiService] a injectar.
         */
        fun getInstance(api: DogApiService): ImageRepository =
            instance ?: synchronized(this) {
                instance ?: ImageRepository(api).also { instance = it }
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
