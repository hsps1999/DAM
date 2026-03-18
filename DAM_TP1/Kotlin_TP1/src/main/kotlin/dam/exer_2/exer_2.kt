package dam.exer_2

fun main() {
    println(" --- CALCULADORA --- ")

    while(true) {
        println("Escolha a operação: +, -, *, /, &&, ||, !, shl, shr (ou 'sair'): ")
        val uInput = readln().trim().lowercase() // remover espaços extra e converter em lowercase

        if (uInput == "sair") {
            println("A terminar...")
            break
        }

        try {
            calc(uInput)
        } catch (e: NumberFormatException) {
            println("Erro: Entrada inválida. Por favor insira números inteiros (ou true/false para booleanos).")
        } catch (e: ArithmeticException) {
            println("Erro: Divisão por zero não é permitida.")
        }
    }
}

fun calc(op: String) {
    var result = 0

    when (op) {
        "+", "-", "*", "/" -> {
            println("Insira o primeiro número:")
            val n1 = readln().toInt()
            println("Insira o segundo número:")
            val n2 = readln().toInt()

            result = when (op) {
                "+" -> n1 + n2
                "-" -> n1 - n2
                "*" -> n1 * n2
                "/" -> n1 / n2 // Pode lançar ArithmeticException
                else -> 0
            }
        }

        // Operadores Booleanos
        "&&", "||" -> {
            val b1 = readBoolean("Insira o primeiro valor (true/false/1/0):")
            val b2 = readBoolean("Insira o segundo valor (true/false/1/0):")

            val resultBool = when (op) {
                "&&" -> b1 && b2
                "||" -> b1 || b2
                else -> false
            }

            result = if (resultBool) 1 else 0
        }

        "!" -> {
            val b1 = readBoolean("Insira o valor (true/false/1/0):")
            result = if (!b1) 1 else 0
        }

        // Operadores Bitwise
        "shl", "shr" -> {
            println("Insira o número base:")
            val n1 = readln().toInt()
            println("Insira a quantidade de shifts:")
            val n2 = readln().toInt()

            result = when (op) {
                "shl" -> n1 shl n2
                "shr" -> n1 shr n2
                else -> 0
            }
        }

        else -> {
            println("Operação desconhecida.")
            return // Sai da função para não imprimir resultados vazios
        }
    }

    printResults(result)
}

/**
 * Lê um valor booleano a partir da entrada do utilizador.
 *
 * A função aceita:
 * - "true" ou "false" (ignora maiúsculas/minúsculas)
 * - "1" para true
 * - "0" para false
 *
 * Continua a pedir entrada até que um valor válido seja inserido.
 *
 */
fun readBoolean(s: String): Boolean {
    while (true) {
        println(s)
        when (readln().trim().lowercase()) {
            "true", "1" -> return true
            "false", "0" -> return false
            else -> println("Entrada inválida! Use true/false ou 1/0.")
        }
    }
}

fun printResults(value: Int) {
    val asBoolean = value != 0 // 0 é false, qualquer outro número é true

    println("""
        --- Resultados ---
        Decimal:     $value
        Hexadecimal: 0x${value.toString(16).uppercase()}
        Booleano:    $asBoolean
    """.trimIndent())
}