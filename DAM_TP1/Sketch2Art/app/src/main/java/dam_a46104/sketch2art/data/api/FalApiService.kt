package dam_a46104.sketch2art.data.api

import dam_a46104.sketch2art.BuildConfig
import dam_a46104.sketch2art.data.api.models.FalRequest
import dam_a46104.sketch2art.data.api.models.FalResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FalApiService {
    @POST("fal-ai/fast-sdxl")
    suspend fun generateImage(
        @Header("Authorization") apiKey: String = "Key ${BuildConfig.FAL_API_KEY}",
        @Body request: FalRequest
    ): Response<FalResponse>
}
