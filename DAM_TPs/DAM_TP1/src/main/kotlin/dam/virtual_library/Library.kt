package dam.virtual_library

class Library(val name: String) {

    // Armazena a lista de livros da biblioteca
    private val books = mutableListOf<Book>()

    // O "Companion Object" atua como um elemento estático partilhado por todas as instâncias
    companion object {
        private var totalBooksAdded = 0

        // Metodo para recuperar o total de livros criados
        fun getTotalBooksCreated(): Int {
            return totalBooksAdded
        }
    }

    // Adiciona um livro à coleção
    fun addBook(book: Book) {
        books.add(book)
        totalBooksAdded++
    }

    // Procura um livro e tenta realizar o empréstimo
    fun borrowBook(title: String) {
        val book = books.find { it.title == title }

        if (book != null) {
            if (book.availableCopies > 0) {
                println("Successfully borrowed '${book.title}'. Copies remaining: ${book.availableCopies}")
                // Diminui as cópias (aciona o custom setter na classe Book caso chegue a 0)
                book.availableCopies--
            } else {
                println("Sorry, no copies left for '${book.title}'.")
            }
        } else {
            println("Book '$title' not found in the library.")
        }
    }

    // Devolve um livro à biblioteca
    fun returnBook(title: String) {
        val book = books.find { it.title == title }

        if (book != null) {
            book.availableCopies++ // Aumenta as cópias
            println("Book '${book.title}' returned successfully. Copies available: ${book.availableCopies}")
        } else {
            println("Book '$title' not found.")
        }
    }

    // Mostra os detalhes de todos os livros
    fun showBooks() {
        println("\n--- Library Catalog ---")
        books.forEach { println(it.toString()) }
    }

    // Pesquisa livros por um autor específico
    fun searchByAuthor(author: String) {
        val authorBooks = books.filter { it.author == author }
        println("Books by $author:")

        if (authorBooks.isEmpty()) {
            println("No books found.")
        } else {
            authorBooks.forEach {
                // Pequeno ajuste para imprimir "copy" no singular se for apenas 1
                val copyWord = if (it.availableCopies == 1) "copy" else "copies"
                println("- ${it.title} (${it.era}, ${it.availableCopies} $copyWord available)")
            }
        }
    }
}

// Data class independente para os membros da biblioteca
data class LibraryMember(
    val name: String,
    val membershipId: String,
    val borrowedBooks: List<String> = emptyList() // Inicializado com uma lista vazia por omissão
)