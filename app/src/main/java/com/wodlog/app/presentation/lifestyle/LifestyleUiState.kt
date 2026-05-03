package com.wodlog.app.presentation.lifestyle

import com.wodlog.app.util.ValidationError

data class LifestyleUiState(
    val weekStartDateInput: String = "",
    val dietSummaryInput: String = "",
    val hasAlcohol: Boolean = false,
    val alcoholAmountPerWeekInput: String = "",
    val hasSmoking: Boolean = false,
    val smokingAmountPerWeekInput: String = "",
    val averageSleepHoursInput: String = "",
    val memoInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val validationErrors: List<ValidationError> = emptyList(),
    val message: String? = null,
    val savedLifestyleLogId: Long? = null,
    val hasExistingLog: Boolean = false
)
