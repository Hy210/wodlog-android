package com.wodlog.app.presentation.prompt

data class PromptUiState(
    val isLoading: Boolean = false,
    val promptText: String = "",
    val errorMessage: String? = null,
    val copyMessage: String? = null,
    val wodTitle: String? = null
)
