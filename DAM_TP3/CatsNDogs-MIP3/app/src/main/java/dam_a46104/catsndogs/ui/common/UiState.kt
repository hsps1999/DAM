package dam_a46104.catsndogs.ui.common

/**
 * Representa os três estados possíveis de qualquer operação assíncrona na UI.
 *
 * Usado pelos ViewModels para expor estado via `LiveData<UiState<T>>`,
 * garantindo que a UI reage de forma tipada a loading, sucesso e erro.
 *
 * @param T Tipo do dado transportado em caso de sucesso.
 */
sealed class UiState<out T> {

    /** Operação em curso — mostrar indicador de loading. */
    data object Loading : UiState<Nothing>()

    /**
     * Operação concluída com sucesso.
     *
     * @property data        Resultado da operação.
     * @property isFromCache `true` se os dados vieram da cache local (modo offline);
     *                       `false` se vieram da API. Usado pela UI para mostrar
     *                       indicador de conteúdo offline.
     */
    data class Success<T>(val data: T, val isFromCache: Boolean = false) : UiState<T>()

    /**
     * Operação falhada.
     *
     * @property message Mensagem de erro legível para apresentar na UI.
     */
    data class Error(val message: String) : UiState<Nothing>()
}
