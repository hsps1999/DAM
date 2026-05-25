package dam

import java.util.Properties

/**
 * AIAssistantFactory creates the appropriate AIAssistant implementation
 * based on configuration settings in the provided Properties object.
 */
class AIAssistantFactory {
    companion object {
        /**
         * Creates and returns an AIAssistant based on configuration
         *
         * @param properties Configuration properties containing API keys and settings
         * @return An implementation of AIAssistant (either OpenAI or Gemini)
         */
        fun createAssistant(properties: Properties): AIAssistant {
            // Determine which assistant to create based on configuration
            return when (properties.getProperty("AI_LLM", "OPENAI")) {
                "OPENAI" -> AIAssistantOpenAI(properties)
                "GEMINI" -> AIAssistantGemini(properties)
                "OPENAI-CLASSES" -> AIAssistantOpenAIClasses(properties)
                "GEMINI-CLASSES" -> AIAssistantGeminiClasses(properties)
                "IAEDU-GPT"    -> AIAssistantIAEdu(properties, "IAEDU_GPT")
                "IAEDU-CLAUDE" -> AIAssistantIAEdu(properties, "IAEDU_CLAUDE")
                else -> throw IllegalArgumentException("Invalid AI model type. Valid values: OPENAI, GEMINI, OPENAI-CLASSES, GEMINI-CLASSES, IAEDU-GPT, IAEDU-CLAUDE.")
            }
        }
    }
}
