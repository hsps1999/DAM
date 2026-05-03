package dam_a46104.catsndogs.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dam_a46104.catsndogs.core.model.ImageItem
import dam_a46104.catsndogs.core.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val imageId: String,
    private val repo: ImageRepository
) : ViewModel() {

    private val _image = MutableStateFlow<ImageItem?>(null)
    val image: StateFlow<ImageItem?> = _image.asStateFlow()

    // O stateIn mantém a cache durante recompositions mas desliga-se se não houver subscribers
    val isFavorite: StateFlow<Boolean> = repo.isFavorite(imageId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            _image.value = repo.findById(imageId)
        }
    }

    fun toggleFavorite() {
        val currentImage = _image.value ?: return
        viewModelScope.launch {
            repo.toggleFavorite(currentImage)
        }
    }

    companion object {
        fun provideFactory(
            imageId: String,
            repository: ImageRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                    return DetailsViewModel(imageId, repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
