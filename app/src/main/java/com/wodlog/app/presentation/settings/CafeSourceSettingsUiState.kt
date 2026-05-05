package com.wodlog.app.presentation.settings

import com.wodlog.app.domain.model.CafeSource

data class CafeSourceSettingsUiState(
    val cafeSources: List<CafeSource> = emptyList(),
    val boxName: String = "",
    val boardUrl: String = "",
    val titleKeywordsText: String = DefaultCafeSourceKeywords.joinToString(", "),
    val preferMobileUrl: Boolean = true,
    val editingCafeSourceId: Long? = null,
    val deleteTarget: CafeSource? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val boxNameError: String? = null,
    val boardUrlError: String? = null,
    val message: String? = null,
    val errorMessage: String? = null
) {
    val isEditing: Boolean
        get() = editingCafeSourceId != null
}

val DefaultCafeSourceKeywords = listOf(
    "WOD",
    "오늘의 와드",
    "오늘 와드",
    "Metcon",
    "Workout"
)
