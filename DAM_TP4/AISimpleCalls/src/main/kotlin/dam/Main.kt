package dam

import kotlinx.coroutines.runBlocking

/**
 * Main entry point for the LLM Assistant application
 */
fun main() = runBlocking {
    println("\n🤖 Starting LLM Assistant application... 😀😀😀😀😀\n")

    // Get configuration properties
    val properties = getProperties()

    // Set up logging
    configureLogging(properties)
    println()

    // Write LLM used
    println("✨ Using AI_LLM: ${properties.getProperty("AI_LLM")}")

    // Use the factory to create the appropriate assistant based on configuration
    val assistant: AIAssistant = AIAssistantFactory.createAssistant(properties)
    println()

    // Write system and model
    println("✨ Using: ${assistant.getSystem()} ${assistant.model}\n")

    // Ask the user to choose the mode
    println("🔧 Choose mode:")
    println("   [1] Chat  — general questions and answers")
    println("   [2] Sentiment analysis  — rates text on a 1-7 scale and returns JSON")
    print("➡️  Enter 1 or 2: ")
    val modeInput = readlnOrNull()?.trim()
    val sentimentMode = modeInput == "2"

    if (sentimentMode) {
        println("\n📊 Sentiment analysis mode active.")
        println("💬 Enter any text and the AI will rate its sentiment (1=Very Negative … 7=Very Positive).")
    } else {
        println("\n💬 Chat mode active. Type your questions and press Enter.")
    }
    println("💬 Press Ctrl+D (Unix/Mac) or Ctrl+Z (Windows) to exit.\n")

    // Main interaction loop
    while (true) {
        println("➖➖➖➖➖➖➖➖➖➖")
        val prompt = if (sentimentMode) "📝 Text to analyse: " else "🧠 Your question: "
        print(prompt)
        val input = readlnOrNull() ?: break

        if (input.isBlank()) {
            println("⚠️ Please enter some text or press Ctrl+D to exit.")
            continue
        }

        val output = if (sentimentMode) assistant.analyzeSentiment(input) else assistant.processInput(input)
        val label = if (sentimentMode) "📊 Sentiment result" else "🤖 Answer"
        println("\n$label: $output\n\n")
    }

    // Bye message
    println("\n👋 Thank you for using LLM Assistant. Goodbye!")

}

/**
 * The temperature value (typically between 0.0 and 1.0) affects how deterministic
 * or creative the AI model's responses will be:
 * - Low temperature (e.g., 0.1-0.3): More deterministic, focused, and predictable responses.
 *   The model is more likely to choose the most probable next token at each step.
 * - Medium temperature (e.g., 0.4-0.7): Balanced between determinism and creativity,
 *   providing reasonably varied responses while maintaining coherence.
 * - High temperature (e.g., 0.8-1.0): More random, diverse, and creative responses.
 *   The model may take more risks and generate more surprising content.
 *
 * Use cases:
 *  1. For technical documentation: use low temperature (0.1-0.3)
 *  2. For creative storytelling: use high temperature (0.8-1.0)
 *  3. For conversation: use medium temperature (0.4-0.7)
 *  4. For code generation: use low-medium temperature (0.2-0.5)
 *  5. For summarization: use medium temperature (0.4-0.7)
 *  6. For sentiment analysis: use high temperature (0.8-1.0)
 *  7. For image generation: use medium temperature (0.4-0.7)
 *  8. For image captioning: use medium temperature (0.4-0.7)
 *  9. For question answering: use medium temperature (0.4-0.7)
 * 10. For chatbots: use medium temperature (0.4-0.7)
 * 11. For summarization: use medium temperature (0.4-0.7)
 * 12. For translation: use low temperature (0.1-0.3)
 * 13. For voice conversion: use low temperature (0.1-0.3)
 */
