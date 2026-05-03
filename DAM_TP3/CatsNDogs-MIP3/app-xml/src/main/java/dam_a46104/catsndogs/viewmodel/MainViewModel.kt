package dam_a46104.catsndogs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dam_a46104.catsndogs.core.common.UiState
import dam_a46104.catsndogs.core.model.ImageItem
import dam_a46104.catsndogs.core.repository.ImageRepository
import kotlinx.coroutines.launch

/**
 * ViewModel da [dam_a46104.catsndogs.ui.main.MainActivity].
 *
 * Estende [AndroidViewModel] para ter acesso ao [Application] context.
 *
 * Expõe o estado da lista de imagens via [LiveData] e delega ao [ImageRepository]
 * todas as operações de dados. Sobrevive a rotações de ecrã.
 *
 * A partir do MIP-3, [fetchRandomImages] devolve directamente [UiState] — o mapeamento
 * de exceções para strings de erro passou para o :core. O ViewModel limita-se a
 * resolver a string de recurso com [getString] quando o estado é [UiState.Error].
 *
 * @param application Application context (injetado pelo sistema).
 * @param repository Repositório de imagens injectado via [Factory].
 */
class MainViewModel(
    application: Application,
    private val repository: ImageRepository
) : AndroidViewModel(application) {

    private val _images = MutableLiveData<UiState<List<ImageItem>>>()

    /** Estado observável da lista de imagens. */
    val images: LiveData<UiState<List<ImageItem>>> = _images

    /**
     * Lista de favoritos actuais — usada pela FavoritesBar na MainActivity.
     * Converte o Flow do :core para LiveData para compatibilidade com os observers XML.
     */
    val favorites: LiveData<List<ImageItem>> = repository.getFavorites().asLiveData()

    /**
     * Pede ao repositório uma nova lista de imagens aleatórias.
     *
     * Emite [UiState.Loading] imediatamente, depois propaga directamente o [UiState]
     * devolvido pelo repositório (que já tratou as exceções internamente).
     *
     * @param count Número de imagens a carregar (default: 20).
     */
    fun loadImages(count: Int = 20) {
        _images.value = UiState.Loading
        viewModelScope.launch {
            _images.value = repository.fetchRandomImages(count)
        }
    }

    // ---------- Factory para injecção manual (sem Hilt) ----------

    /**
     * Factory que permite instanciar [MainViewModel] com o [ImageRepository] necessário.
     * Estende [ViewModelProvider.AndroidViewModelFactory] para suportar [AndroidViewModel].
     *
     * @param application Contexto de aplicação necessário para [AndroidViewModel].
     * @param repository Repositório a injectar no ViewModel.
     */
    class Factory(
        private val application: Application,
        private val repository: ImageRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
