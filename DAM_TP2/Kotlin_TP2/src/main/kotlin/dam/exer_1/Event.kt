package dam.exer_1
/*
* Sealed Class: Fornece uma herança controlada onde todas as subclasses
* diretas são conhecidas em tempo de compilação e devem ser declaradas
* no mesmo package
* */
sealed class Event(val username: String) {
    // Evento de login de utilizador
    class Login(username: String, val timeStamp: Long) : Event(username)

    // Evento de compra
    class Purchase(username: String, val amount: Double, val timeStamp: Long) : Event(username)

    // Evento de logout de utilizador
    class Logout(username: String, val timeStamp: Long) : Event(username)
}

// Filtra a lista para retornar apenas os eventos de um utilizador específico
fun List<Event>.filterByUser(username: String): List<Event> =
    this.filter { it.username == username }

// Calcula o total gasto por um utilizador em eventos de compra
fun List<Event>.totalSpent(username: String): Double =
    this.filterByUser(username)
        .filterIsInstance<Event.Purchase>()
        .sumOf { it.amount }

fun processEvents(events: List<Event>, handler:(Event) -> Unit) {
    // handler aplicado a cada elemento da lista
    events.forEach {handler(it)}
}