package dam.exer_1

fun main() {
    val events = listOf(
        Event.Login("alice", 1_000),
        Event.Purchase("alice", 49.99, 1_100),
        Event.Purchase("bob", 19.99, 1_200),
        Event.Login("bob", 1_050),
        Event.Purchase("alice", 15.00 , 1_300) ,
        Event.Logout("alice", 1_400) ,
        Event.Logout("bob", 1_500)
    )

    // Processar eventos
    processEvents(events) { event ->
        // O 'when' é exaustivo: trata todos os casos
        when (event) {
            is Event.Login ->
                println("[LOGIN] ${event.username} logged in at t=${event.timeStamp}.")
            is Event.Purchase ->
                println("[PURCHASE] ${event.username} spent $${event.amount} at t=${event.timeStamp}.")
            is Event.Logout ->
                println("[LOGOUT] ${event.username} logged out at ${event.timeStamp}.")
        }
    }

    // Imprimir totais usando a extensão totalSpent
    println("Total spent by alice: $${"%.2f".format(events.totalSpent("alice"))}")
    println("Total spent by bob: $${events.totalSpent("bob")}")

    println("\nEvents for alice:")
    // Filtrar eventos da alice
    val aliceEvents = events.filterByUser("alice")

    // Processar a lista filtrada
    aliceEvents.forEach { event ->
        when (event) {
            is Event.Login ->
                println("Login(username=${event.username}, timestamp=${event.timeStamp})")
            is Event.Purchase ->
                println("Purchase(username=${event.username}, amount=${event.amount}, timestamp=${event.timeStamp})")
            is Event.Logout ->
                println("Logout(username=${event.username}, timestamp=${event.timeStamp})")
        }
    }
}
