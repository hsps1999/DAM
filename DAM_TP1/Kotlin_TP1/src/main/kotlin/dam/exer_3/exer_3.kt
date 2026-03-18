package dam.exer_3

fun main() {
    println("--- Sequência de ressaltos ---")

    fun bouncesSeq(): Sequence<Double> {
        return generateSequence(100.0) { it * 0.6 } // Ressalto até 60% da altura
            .filter { it >= 1.0 } // filtro para ressaltos até 1 metro de altura
            .take(15) // primeiros 15 ressaltos
    }

    val bounces = bouncesSeq()
    bounces.forEachIndexed { i, h ->
        println("Ressalto ${i + 1}: ${"%.2f".format(h)} m")
    }
}