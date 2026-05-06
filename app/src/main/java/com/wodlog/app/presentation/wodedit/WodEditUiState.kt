package com.wodlog.app.presentation.wodedit

import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.WodSourceType
import com.wodlog.app.domain.model.WodType
import java.time.Instant
import com.wodlog.app.util.ValidationError

data class WodEditUiState(
    val editingWodId: Long? = null,
    val dateInput: String = "",
    val titleInput: String = "",
    val wodType: WodType? = null,
    val rawTextInput: String = "",
    val memoInput: String = "",
    val sourceType: WodSourceType = WodSourceType.MANUAL,
    val sourceUrl: String? = null,
    val importedAt: Instant? = null,
    val sections: List<WodSectionInputState> = emptyList(),
    val movements: List<MovementInputState> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val validationErrors: List<ValidationError> = emptyList(),
    val message: String? = null,
    val savedWodId: Long? = null
)

data class WodSectionInputState(
    val localId: String,
    val originalId: Long? = null,
    val titleInput: String = "",
    val memoInput: String = ""
)

data class MovementInputState(
    val localId: String,
    val originalId: Long? = null,
    val sectionLocalId: String? = null,
    val nameInput: String = "",
    val weightInput: String = "",
    val repsInput: String = "",
    val setsInput: String = "",
    val roundsInput: String = "",
    val distanceInput: String = "",
    val caloriesInput: String = "",
    val timeSecondsInput: String = "",
    val category: MovementCategory = MovementCategory.OTHER,
    val memoInput: String = ""
)
