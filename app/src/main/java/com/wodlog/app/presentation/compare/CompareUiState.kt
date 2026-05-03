package com.wodlog.app.presentation.compare

import com.wodlog.app.domain.analysis.AnalysisSummary

data class CompareUiState(
    val isLoading: Boolean = false,
    val summary: AnalysisSummary? = null,
    val wodCount: Int = 0,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)
