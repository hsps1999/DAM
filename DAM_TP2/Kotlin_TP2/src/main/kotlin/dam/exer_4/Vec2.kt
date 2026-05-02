package dam.exer_4

import kotlin.math.hypot

// Classe implementa Comparable<T> (comparison operators)
data class Vec2(val x: Double, val y: Double) : Comparable<Vec2> {

    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y) // vec1 + vec2
    operator fun minus(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y) // vec1 - vec2
    operator fun times(scalar: Double): Vec2 = Vec2(x * scalar, y * scalar) // vec1 * scl
    operator fun unaryMinus(): Vec2 = Vec2(-x, -y) // -vec1

    val magnitude: Double
        get() = hypot(x, y)

    override operator fun compareTo(other: Vec2): Int {
        return this.magnitude.compareTo(other.magnitude)
    }

    /*
    // hypot calcula sqrt(x * x + y * y) - Comprimento Euclidiano (magnitude)
    fun magnitude(x: Double, y: Double) = hypot(x, y)

    // Sobrecarga do operador compareTo
    override fun compareTo(other: Vec2): Int {
        // Comparar a magnitude dos vetores
        return magnitude(this.x, this.y)
            .compareTo(magnitude(other.x, other.y))
    }
    */

    // Produto escalar
    fun dot(other: Vec2): Double {
        return (this.x * other.x) + (this.y * other.y)
    }

    // Vetor Normalizado (Unitário)
    fun normalized(): Vec2 {
        if (magnitude == 0.0) {
            throw IllegalStateException("Cannot normalize a zero vector.")
        }
        return Vec2(x / magnitude, y / magnitude)
    }

    // Sobrecarga do operador get
    operator fun get(index: Int): Double {
        return when (index) {
            0 -> x // índice 0
            1 -> y // índice 1
            // lança erro para qualquer outro índice
            else -> throw IndexOutOfBoundsException("Invalid index $index for Vec2")
        }
    }
}

// Challenge
// Extension function
// para suportar a ordem inversa
// this é o escalar
operator fun Double.times(vec: Vec2): Vec2 {
    return Vec2(this * vec.x, this * vec.y)
}   