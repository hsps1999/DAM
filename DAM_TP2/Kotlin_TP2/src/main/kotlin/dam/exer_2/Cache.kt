package dam.exer_2

/*
* Classe genérica Cache
* Ao usar o Any, é definido um Upper Bound
* que proíbe o uso de chaves e valores nulos
* garantindo que a cache é segura (type-safe)
* */
class Cache<K : Any, V : Any> {
    // Armazenamento interno (conteúdo mutável - val)
    private val map: MutableMap<K, V> = mutableMapOf()

    // Insere ou atualiza um valor
    fun put(key: K, value: V) {
        map[key] = value
    }

    // Devolve o valor ou null se a chave não existir
    fun get(key: K): V? = map[key]

    // Remove a entrada da cache
    fun evict(key: K) {
        map.remove(key)
    }

    // Retorna o número de elementos na cache
    fun size(): Int = map.size

    // Função nativa de MutableMap
    // default é uma lambda (Higher-Order Function)
    // que garante que o código só corre se for mesmo necessário (lazy evaluation)
    fun getOrPut(key: K, default: () -> V): V = map.getOrPut(key, default)

    fun transform(key: K, action: (V) -> V): Boolean {
        val currentValue = map[key]  // Vai buscar o valor associado à chave
        // se existir aplica a lambda a esse v atualiza a cache e devolve true
        if (currentValue != null) {
            map[key] = action(currentValue)
            return true
        } else {
            return false // se não devolve false
        }
    }

    // cria uma copia nova e imutavel do MutableMap
    fun snapshot(): Map<K, V> = map.toMap()

    // Retorna um mapa imutável apenas com os valores que satisfazem a condição
    fun filterValues(predicate: (V) -> Boolean): Map<K, V> = map.filterValues(predicate)
}

