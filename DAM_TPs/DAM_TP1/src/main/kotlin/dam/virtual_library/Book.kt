package dam.virtual_library

abstract class Book(
    val title: String,
    val author: String,
    val publicationYear: Int,
    initialCopies: Int
) {
    // Custom getter baseado no ano de publicação
    val era: String
        get() = when {
            publicationYear < 1980 -> "Classic"
            publicationYear in 1980..2010 -> "Modern"
            else -> "Contemporary"
        }

    // Custom setter para as cópias disponíveis
    var availableCopies: Int = initialCopies
        set(value) {
            if (value < 0) return // Previne valores negativos

            field = value // Atualiza o "backing field"

            if (value == 0) {
                println("Warning: Book is now out of stock!")
            }
        }

    // Bloco init executado na criação do objeto
    init {
        println("Book '$title' by $author has been added to the library.")
    }

    // Metodo abstrato a ser implementado pelas subclasses
    abstract fun getStorageInfo(): String

    // Customização da impressão do objeto
    override fun toString(): String {
        return "Title: $title, Author: $author, Era: $era, Available: $availableCopies copies\nStorage: ${getStorageInfo()}"
    }
}