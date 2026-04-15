package dam_a46104.catsndogs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dam_a46104.catsndogs.data.model.ImageItem
import dam_a46104.catsndogs.data.repository.ImageRepository
import dam_a46104.catsndogs.ui.common.UiState
import kotlinx.coroutines.launch

/**
 * ViewModel da [dam_a46104.catsndogs.ui.main.MainActivity].
 *
 * Expõe o estado da lista de imagens via [LiveData] e delega ao [ImageRepository]
 * todas as operações de dados. Sobrevive a rotações de ecrã.
 *
 * @property repository Repositório de imagens injectado via [Factory].
 */
class MainViewModel(
    private val repository: ImageRepository
) : ViewModel() {

    private val _images = MutableLiveData<UiState<List<ImageItem>>>()

    /** Estado observável da lista de imagens. */
    val images: LiveData<UiState<List<ImageItem>>> = _images

    /**
     * Pede ao repositório uma nova lista de imagens aleatórias.
     *
     * Emite [UiState.Loading] imediatamente, seguido de [UiState.Success]
     * ou [UiState.Error] conforme o resultado da chamada de rede.
     *
     * @param count Número de imagens a carregar (default: 20).
     */
    fun loadImages(count: Int = 20) {
        _images.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.fetchRandomImages(count)
                _images.value = UiState.Success(result)
            } catch (e: Exception) {
                _images.value = UiState.Error(e.message ?: "Erro desconhecido ao carregar imagens.")
            }
        }
    }

    // ---------- Factory para injecção manual (sem Hilt) ----------

    /**
     * Factory que permite instanciar [MainViewModel] com o [ImageRepository] necessário.
     * Usado em conjunto com `ViewModelProvider` na Activity.
     */
    class Factory(private val repository: ImageRepository) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
