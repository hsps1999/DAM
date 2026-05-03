package dam_a46104.catsndogs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dam_a46104.catsndogs.core.R
import dam_a46104.catsndogs.core.common.UiState
import dam_a46104.catsndogs.core.model.ImageItem
import dam_a46104.catsndogs.core.repository.ImageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * ViewModel do [dam_a46104.catsndogs.ui.details.ImageDetailsActivity].
 *
 * Resolve um [ImageItem] a partir do seu [id] consultando a cache em memória
 * do [ImageRepository]. Expõe também o estado de favorito via [isFavorite]
 * e permite alternar esse estado via [toggleFavorite].
 *
 * A partir do MIP-3, [isFavorite] e [favorites] são derivados de Flow do :core
 * e convertidos para LiveData via [asLiveData].
 *
 * @param application Application context, necessário para o [AndroidViewModel].
 * @param repository  Repositório de imagens injectado via [Factory].
 */
class DetailsViewModel(
    application: Application,
    private val repository: ImageRepository
) : AndroidViewModel(application) {

    private val _imageDetail = MutableLiveData<UiState<ImageItem>>()

    /** Estado observável do detalhe da imagem. */
    val imageDetail: LiveData<UiState<ImageItem>> = _imageDetail

    /**
     * ID da imagem actualmente carregada, como StateFlow para encadear [isFavorite]
     * via [flatMapLatest] — correctamente cancelado e resubscrito em rotação de ecrã.
     */
    private val _currentId = MutableStateFlow("")

    /**
     * Observa se a imagem actualmente carregada é favorita.
     * Usa [flatMapLatest]: quando [_currentId] muda, o Flow anterior é cancelado
     * e um novo é subscrito automaticamente. Convertido para LiveData para os observers XML.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: LiveData<Boolean> = _currentId
        .flatMapLatest { id ->
            if (id.isNotEmpty()) repository.isFavorite(id)
            else kotlinx.coroutines.flow.flowOf(false)
        }
        .asLiveData()

    /**
     * Lista de todos os favoritos actuais (para a FavoritesBar).
     * Convertido de Flow para LiveData.
     */
    val favorites: LiveData<List<ImageItem>> = repository.getFavorites().asLiveData()

    /**
     * Carrega os dados de uma imagem a partir do [id] fornecido pelo Intent.
     *
     * Emite [UiState.Loading] de imediato; depois [UiState.Success] se o item
     * for encontrado (em memória, cache Room ou favoritos Room),
     * ou [UiState.Error] se o id não existir em nenhuma camada.
     *
     * @param id Identificador único da imagem (passado via Intent extra).
     */
    fun loadImage(id: String) {
        _currentId.value = id
        _imageDetail.value = UiState.Loading
        viewModelScope.launch {
            val item = repository.findById(id)
            _imageDetail.value = if (item != null) {
                UiState.Success(item)
            } else {
                UiState.Error(R.string.error_unknown)
            }
        }
    }

    /**
     * Alterna o estado de favorito da imagem actualmente carregada.
     * Operação assíncrona — lançada em [viewModelScope].
     * Não faz nada se nenhuma imagem estiver carregada com sucesso.
     */
    fun toggleFavorite() {
        val item = (_imageDetail.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.toggleFavorite(item)
        }
    }

    // ---------- Factory para injecção manual (sem Hilt) ----------

    /**
     * Factory que permite instanciar [DetailsViewModel] com o [ImageRepository] necessário.
     */
    class Factory(
        private val application: Application,
        private val repository: ImageRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                return DetailsViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
