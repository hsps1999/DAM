package dam_a46104.catsndogs.data.model

/**
 * DTO que representa a resposta da Dog CEO API para o endpoint
 * `GET /api/breeds/image/random/{count}`.
 *
 * @property message Lista de URLs de imagens devolvidas pela API.
 * @property status  Estado da resposta (ex.: "success" ou "error").
 */
data class DogApiResponse(
    val message: List<String>,
    val status: String
)
