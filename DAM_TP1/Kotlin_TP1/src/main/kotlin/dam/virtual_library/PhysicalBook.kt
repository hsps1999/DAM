package dam.virtual_library

class PhysicalBook(
    title: String,
    author: String,
    publicationYear: Int,
    initialCopies: Int,
    val weight: Int,
    val hasHardcover: Boolean = true // Valor true por omissão, conforme pedido
) : Book(title, author, publicationYear, initialCopies) {

    // Implementação do metodo abstrato da classe Book
    override fun getStorageInfo(): String {
        val hardcoverText = if (hasHardcover) "Yes" else "No"
        return "Physical book: ${weight}g, Hardcover: $hardcoverText"
    }
}