package dam_a46104.catsndogs.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dam_a46104.catsndogs.core.common.UiState
import dam_a46104.catsndogs.core.model.ImageItem
import dam_a46104.catsndogs.core.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repo: ImageRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<ImageItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ImageItem>>> = _uiState.asStateFlow()

    // stateIn converte o Flow normal do repository num StateFlow partilhado com a UI,
    // garantindo que não estamos sempre a reiniciar a coleção a cada recomposition, 
    // mas sim a manter o estado quente (hot stream) durante 5 segundos após
    // a última subscrição desaparecer.
    val favorites: StateFlow<List<ImageItem>> = repo.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadImages()
    }

    fun loadImages(count: Int = 20) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repo.fetchRandomImages(count)
        }
    }

    companion object {
        fun provideFactory(repository: ImageRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        return MainViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
