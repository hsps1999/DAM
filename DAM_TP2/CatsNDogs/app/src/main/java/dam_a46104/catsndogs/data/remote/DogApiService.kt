package dam_a46104.catsndogs.data.remote

import dam_a46104.catsndogs.data.model.DogApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface Retrofit que define os endpoints da Dog CEO API utilizados pela aplicação.
 *
 * Base URL: https://dog.ceo/
 */
interface DogApiService {

    /**
     * Obtém um conjunto de imagens aleatórias de cães.
     *
     * @param count Número de imagens a devolver (máximo 50 por pedido).
     * @return [DogApiResponse] com a lista de URLs e o estado da resposta.
     */
    @GET("api/breeds/image/random/{count}")
    suspend fun getRandomImages(@Path("count") count: Int): DogApiResponse
}
