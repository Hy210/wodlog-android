package com.wodlog.app.presentation.resultedit

import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.util.ValidationError

data class ResultEditUiState(
    val wodId: Long = 0L,
    val scoreType: ScoreType? = null,
    val timeSecondsInput: String = "",
    val roundsInput: String = "",
    val repsInput: String = "",
    val totalRepsInput: String = "",
    val loadInput: String = "",
    val distanceInput: String = "",
    val caloriesInput: String = "",
    val rxStatus: RxStatus = RxStatus.UNKNOWN,
    val rpeInput: String = "",
    val condition: Condition = Condition.UNKNOWN,
    val memoInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val validationErrors: List<ValidationError> = emptyList(),
    val message: String? = null,
    val savedResultId: Long? = null,
    val hasExistingResult: Boolean = false
)
