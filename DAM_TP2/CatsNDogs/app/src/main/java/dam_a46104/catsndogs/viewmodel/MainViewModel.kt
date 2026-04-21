package dam_a46104.catsndogs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dam_a46104.catsndogs.R
import dam_a46104.catsndogs.data.model.ImageItem
import dam_a46104.catsndogs.data.repository.ImageRepository
import dam_a46104.catsndogs.ui.common.UiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel da [dam_a46104.catsndogs.ui.main.MainActivity].
 *
 * Estende [AndroidViewModel] para ter acesso ao [Application] context,
 * necessário para resolver strings de erro localizadas via [getString].
 *
 * Expõe o estado da lista de imagens via [LiveData] e delega ao [ImageRepository]
 * todas as operações de dados. Sobrevive a rotações de ecrã.
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

    /** Lista de favoritos actuais — usada pela FavoritesBar na MainActivity. */
    val favorites: LiveData<List<ImageItem>> = repository.getFavorites()

    /**
     * Pede ao repositório uma nova lista de imagens aleatórias.
     *
     * Emite [UiState.Loading] imediatamente, seguido de [UiState.Success]
     * ou [UiState.Error] conforme o resultado da chamada de rede.
     * Cada tipo de exceção é mapeado para uma mensagem de erro user-friendly.
     *
     * @param count Número de imagens a carregar (default: 20).
     */
    fun loadImages(count: Int = 20) {
        _images.value = UiState.Loading
        viewModelScope.launch {
            try {
                val (result, isFromCache) = repository.fetchRandomImages(count)
                _images.value = UiState.Success(result, isFromCache)
            } catch (e: IOException) {
                // Sem rede, timeout ou falha de I/O
                _images.value = UiState.Error(
                    getApplication<Application>().getString(R.string.error_no_network)
                )
            } catch (e: HttpException) {
                // Resposta HTTP com código de erro (4xx / 5xx)
                _images.value = UiState.Error(
                    getApplication<Application>().getString(R.string.error_server)
                )
            } catch (e: Exception) {
                // Qualquer outro erro inesperado
                _images.value = UiState.Error(
                    getApplication<Application>().getString(R.string.error_unknown)
                )
            }
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
