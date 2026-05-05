package com.wodlog.app.presentation.cafeimport

import com.wodlog.app.domain.model.CafeSource
import com.wodlog.app.domain.model.CafePostCandidate

data class CafeImportUiState(
    val cafeSource: CafeSource? = null,
    val initialUrl: String = "",
    val currentUrl: String = "",
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val errorMessage: String? = null,
    val isExtractingCandidates: Boolean = false,
    val candidates: List<CafePostCandidate> = emptyList(),
    val isCandidateListVisible: Boolean = false,
    val candidateMessage: String? = null
)
