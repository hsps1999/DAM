package dam_a46104.catsndogs.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que constrói e expõe a instância de [DogApiService] configurada com Retrofit.
 *
 * A base URL aponta para a Dog CEO API. O conversor Gson trata da deserialização automática
 * das respostas JSON para os DTOs definidos em `data/model/`.
 */
object RetrofitClient {

    private const val BASE_URL = "https://dog.ceo/"

    /** Instância de [DogApiService] pronta a usar. Criada uma única vez (lazy). */
    val dogApiService: DogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)
    }
}
