package dam.exer_2

fun main() {

    // Cache para freq de palavras (String, Int)
    val wordCache = Cache<String, Int>()

    // Dados iniciais
    wordCache.put("kotlin", 1)
    wordCache.put("scala", 1)
    wordCache.put("haskell", 1)

    println("--- Word frequency cache ---")
    println("Size: ${wordCache.size()}")
    println("Frequency of \"kotlin\": ${wordCache.get("kotlin")}")

    // getOrPut: Retorna 1 (já existe)
    println("getOrPut \"kotlin\": ${wordCache.getOrPut("kotlin") { 0 }}")

    // getOrPut: Insere e retorna 0 (não existia)
    println("getOrPut \"java\": ${wordCache.getOrPut("java") { 0 }}")

    println("Size after getOrPut: ${wordCache.size()}")

    // transform: Atualiza e retorna true
    println("Transform \"kotlin\" (+1): ${wordCache.transform("kotlin") { it + 1 }}")

    // transform: Não existe, não faz nada e retorna false
    println("Transform \"cobol\" (+1): ${wordCache.transform("cobol") { it + 1 }}")

    println("Snapshot: ${wordCache.snapshot()}")

    // Filtra e imprime apenas as palavras com freq > 0
    val wordsGreaterThanZero = wordCache.filterValues { it > 0 }
    println("Words with count > 0: $wordsGreaterThanZero")

    // Cache para registo de IDs (Int, String)
    val idCache = Cache<Int, String>()

    idCache.put(1, "Alice")
    idCache.put(2, "Bob")

    println("\n--- Id registry cache ---")
    println("Id 1 -> ${idCache.get(1)}")
    println("Id 2 -> ${idCache.get(2)}")

    // Remover o id 1
    idCache.evict(1)

    println("After evict id 1, size: ${idCache.size()}")
    println("Id 1 after evict -> ${idCache.get(1)}")
}

