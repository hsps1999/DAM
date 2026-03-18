package dam.virtual_library

class DigitalBook(
    title: String,
    author: String,
    publicationYear: Int,
    initialCopies: Int,
    val fileSize: Double,
    val format: String
) : Book(title, author, publicationYear, initialCopies) {

    // Implementação do metodo abstrato da classe Book
    override fun getStorageInfo(): String {
        return "Stored digitally: $fileSize MB, Format: $format"
    }
}