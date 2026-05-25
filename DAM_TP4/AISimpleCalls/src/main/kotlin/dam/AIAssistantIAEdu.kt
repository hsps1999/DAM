package dam

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * AIAssistantIAEdu interfaces with the iaedu.pt proxy (Flowise-based).
 *
 * Supports both GPT and Claude agents via the same proxy format.
 * Use keyPrefix = "IAEDU_GPT" or "IAEDU_CLAUDE" to select which model.
 *
 * Required config properties (replace PREFIX with IAEDU_GPT or IAEDU_CLAUDE):
 *  PREFIX_API_KEY    - the sk-usr-... key from the iaedu platform
 *  PREFIX_ENDPOINT   - the full stream URL from "Endpoint da API"
 *  PREFIX_CHANNEL_ID - the value from "ID do Canal"
 *
 * API differences vs native OpenAI/Anthropic:
 *  - Body is application/x-www-form-urlencoded
 *  - Auth via x-api-key header (not Bearer)
 *  - Response is NDJSON: one JSON object per line, fields "type" and "content"
 */
class AIAssistantIAEdu(
    override val properties: Properties,
    private val keyPrefix: String = "IAEDU_GPT"
) : AIAssistant {

    override fun getSystem() = keyPrefix.replace("_", "-")   // e.g. "IAEDU-GPT"
    override val apiKeyName = "${keyPrefix}_API_KEY"
    override var model = keyPrefix.lowercase()                // e.g. "iaedu_gpt"

    private val endpointUrl: String = properties.getProperty("${keyPrefix}_ENDPOINT")
        ?: throw IllegalStateException("${keyPrefix}_ENDPOINT not found in configuration file.")

    private val channelId: String = properties.getProperty("${keyPrefix}_CHANNEL_ID")
        ?: throw IllegalStateException("${keyPrefix}_CHANNEL_ID not found in configuration file.")

    // Single thread_id per session — keeps conversation context across messages
    private val threadId: String = UUID.randomUUID().toString().replace("-", "").take(20)

    // Read from config; iaedu proxy ignores these (set server-side), but they satisfy Task 2
    private val temperature: Double = properties.getProperty("TEMPERATURE")?.toDoubleOrNull() ?: 0.7
    private val maxTokens: Int = properties.getProperty("MAX_TOKENS")?.toIntOrNull() ?: 800

    // Extended timeouts for streaming responses (iaedu can be slow)
    override val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    init {
        println("⚙️  Temperature: $temperature | Max tokens: $maxTokens (proxy-side fixed, shown for Task 2)")
    }

    override fun buildRequest(prompt: String): Request {
        val body = FormBody.Builder()
            .add("channel_id", channelId)
            .add("thread_id", threadId)
            .add("user_info", "{}")
            .add("message", prompt)
            .build()

        return Request.Builder()
            .url(endpointUrl)
            .addHeader("x-api-key", apiKey)
            .post(body)
            .build()
    }

    override fun makeApiCall(prompt: String): String {
        logger.info("Prompt:\n$prompt")
        val request = buildRequest(prompt)
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                throw Exception("Error in API call: ${response.code} - ${response.message}\nResponse: $errorBody")
            }
            val responseBody = response.body?.string() ?: return "Error: empty response"
            logger.debug("Raw API response: {}", responseBody)
            return parseNDJSONResponse(responseBody)
        }
    }

    // iaedu response format: one JSON object per line
    // {"run_id":"...","type":"token","content":"Hello"}
    // {"run_id":"...","type":"error","content":"..."}
    // {"run_id":"...","type":"done","content":"..."}
    private fun parseNDJSONResponse(body: String): String {
        val sb = StringBuilder()
        val errors = mutableListOf<String>()

        for (line in body.lines()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            try {
                val json = JSONObject(trimmed)
                when (json.optString("type")) {
                    "token"  -> sb.append(json.optString("content"))
                    "text"   -> sb.append(json.optString("content"))
                    "error"  -> errors.add(json.optString("content"))
                    // "start", "done", "agentReasoning" — ignored
                }
            } catch (e: JSONException) {
                // Not JSON — treat as plain text chunk
                sb.append(trimmed)
            }
        }

        return when {
            sb.isNotEmpty()     -> sb.toString().trim()
            errors.isNotEmpty() -> "Erro do servidor: ${errors.joinToString(" | ")}"
            else                -> body.trim()
        }
    }
}
