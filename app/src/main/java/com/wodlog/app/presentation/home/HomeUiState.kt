package com.wodlog.app.presentation.home

import com.wodlog.app.domain.model.CafeSource

data class HomeUiState(
    val cafeSources: List<CafeSource> = emptyList(),
    val isLoadingCafeSources: Boolean = true,
    val isCafeSourcePickerVisible: Boolean = false,
    val errorMessage: String? = null
)
