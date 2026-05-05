package com.wodlog.app.presentation.cafeimport

import com.wodlog.app.domain.model.CafeSource

data class CafeImportUiState(
    val cafeSource: CafeSource? = null,
    val initialUrl: String = "",
    val currentUrl: String = "",
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val errorMessage: String? = null
)
