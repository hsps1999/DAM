package dam_a46104.sketch2art.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_a46104.sketch2art.data.repository.DrawingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: DrawingRepository = DrawingRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun generateArt(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            repository.generateArt(bitmap)
                .onSuccess { url ->
                    _uiState.value = MainUiState.Success(url)
                }
                .onFailure { error ->
                    _uiState.value = MainUiState.Error(error.message ?: "Unknown Error")
                }
        }
    }

    fun resetState() {
        _uiState.value = MainUiState.Idle
    }
}
