package com.wodlog.app.presentation.profile

import com.wodlog.app.util.ValidationError

data class ProfileUiState(
    val heightCmInput: String = "",
    val weightKgInput: String = "",
    val crossfitStartDateInput: String = "",
    val trainingDays: Long = 0L,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val validationErrors: List<ValidationError> = emptyList(),
    val message: String? = null,
    val hasProfile: Boolean = false
)
