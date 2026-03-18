package dam_a46104.sketch2art.data.repository

import android.graphics.Bitmap
import android.util.Log
import dam_a46104.sketch2art.data.api.FalApiService
import dam_a46104.sketch2art.data.api.models.ControlNet
import dam_a46104.sketch2art.data.api.models.FalRequest
import dam_a46104.sketch2art.util.BitmapUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DrawingRepository {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fal.run/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(FalApiService::class.java)
    private val gson = Gson()

    suspend fun generateArt(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val base64 = BitmapUtils.toBase64(bitmap)
            bitmap.recycle()
            
            val dataUri = "data:image/jpeg;base64,$base64"
            
            // Balanced prompt: Neutral subject focusing on the sketch, high quality
            val balancedPrompt = "A professional photorealistic 8k masterpiece of the sketch drawn, highly detailed, cinematic lighting."
            
            // Refined negative prompt (removed human-specific blocks to allow them if drawn)
            val refinedNegativePrompt = "blurry, low quality, distorted, messy lines, text, watermark, deformed shapes, abstract background."
            
            val request = FalRequest(
                prompt = balancedPrompt,
                negativePrompt = refinedNegativePrompt,
                guidanceScale = 7.5f, // Standard sweet spot for SDXL
                controlnets = listOf(
                    ControlNet(
                        imageUrl = dataUri,
                        conditioningScale = 1.1f // Aggressive line following
                    )
                )
            )

            Log.d("FAL_DEBUG", "Sending balanced request to fast-sdxl...")

            val response = apiService.generateImage(request = request)
            
            if (response.isSuccessful) {
                val body = response.body()
                val imageUrl = body?.images?.firstOrNull()?.url
                
                if (imageUrl != null) {
                    Log.d("FAL_RESULT", "Successfully extracted URL: $imageUrl")
                    Result.success(imageUrl)
                } else {
                    Log.e("FAL_DEBUG", "Response received but no image URL found: ${gson.toJson(body)}")
                    Result.failure(Exception("Empty response: No URL in images array"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e("FAL_DEBUG", "API Error: ${response.code()} $errorBody")
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("FAL_DEBUG", "Exception during generation: ${e.message}", e)
            Result.failure(e)
        }
    }
}
