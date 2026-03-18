package dam_a46104.sketch2art.ui.main

sealed class MainUiState {
    object Idle : MainUiState()
    object Loading : MainUiState()
    data class Success(val imageUrl: String) : MainUiState()
    data class Error(val message: String) : MainUiState()
}
