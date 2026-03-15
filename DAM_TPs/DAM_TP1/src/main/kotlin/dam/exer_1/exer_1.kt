package dam.exer_1

fun main() {
    println("--- 50 Quadrados Perfeitos ---")

    // a) Usando o construtor IntArray
    val squaresA = IntArray(50) { i -> (i+1) * (i+1) }
    println("Alínea a):")
    println(squaresA.joinToString())

    // b) Usando range e map()
    val squaresB = (1..50).map { it * it }
    println("\nAlínea b):")
    println(squaresB.joinToString())

    // c) Usando Array com construtor
    val squaresC = Array(50) { i -> (i+1) * (i+1) }
    println("\nAlínea c):")
    println(squaresC.joinToString())

    // Adicional (Potências de 2)
    val powersX = (0..10).map { 1 shl it }
    println("\nPrimeiras 11 potências de 2:")
    println(powersX.joinToString())
}