package com.hugo.geminiimageapp

sealed interface UiState {
    data object Initial : UiState
    data object Loading : UiState
    data class Success(val outputText: String) : UiState
    data class Error(val errorMessage: String) : UiState
}

data class HistoryEntry(
    val imageResId: Int,
    val prompt: String,
    val response: String,
)
