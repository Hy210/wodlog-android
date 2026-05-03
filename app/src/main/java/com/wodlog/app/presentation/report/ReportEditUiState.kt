package com.wodlog.app.presentation.report

import com.wodlog.app.domain.model.AiReport

data class ReportEditUiState(
    val wodId: Long = 0L,
    val reports: List<AiReport> = emptyList(),
    val selectedReportId: Long? = null,
    val answerInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val validationErrors: List<String> = emptyList(),
    val message: String? = null,
    val errorMessage: String? = null
)
