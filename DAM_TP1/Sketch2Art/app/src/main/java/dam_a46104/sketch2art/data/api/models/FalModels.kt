package dam_a46104.sketch2art.data.api.models

import com.google.gson.annotations.SerializedName

data class FalRequest(
    @SerializedName("prompt") val prompt: String,
    @SerializedName("negative_prompt") val negativePrompt: String? = null,
    @SerializedName("controlnets") val controlnets: List<ControlNet>,
    @SerializedName("sync_mode") val syncMode: Boolean = true,
    @SerializedName("guidance_scale") val guidanceScale: Float = 7.5f
)

data class ControlNet(
    @SerializedName("model_name") val modelName: String = "scribble",
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("conditioning_scale") val conditioningScale: Float = 1.0f
)

data class FalResponse(
    @SerializedName("images") val images: List<ImageResult>?
)

data class ImageResult(
    @SerializedName("url") val url: String
)
