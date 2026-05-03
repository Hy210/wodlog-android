package com.wodlog.app.presentation.resultedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.RxStatus
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.util.ValidationError
import com.wodlog.app.util.WodlogValidators
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultEditViewModel(
    private val repository: WodlogRepository,
    private val nowProvider: () -> Instant = { Instant.now() }
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResultEditUiState())
    val uiState: StateFlow<ResultEditUiState> = _uiState.asStateFlow()

    private var loadedResult: WodResult? = null

    fun loadResult(wodId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    wodId = wodId,
                    isLoading = true,
                    message = null,
                    validationErrors = emptyList()
                )
            }

            val result = repository.getResultForWod(wodId)
            loadedResult = result

            _uiState.value = if (result == null) {
                ResultEditUiState(wodId = wodId)
            } else {
                ResultEditUiState(
                    wodId = wodId,
                    scoreType = result.scoreType,
                    timeSecondsInput = result.timeSeconds?.toString().orEmpty(),
                    roundsInput = result.rounds?.toString().orEmpty(),
                    repsInput = result.extraReps?.toString().orEmpty(),
                    totalRepsInput = result.totalReps?.toString().orEmpty(),
                    loadInput = result.loadKg?.toInputString().orEmpty(),
                    distanceInput = result.distanceMeters?.toInputString().orEmpty(),
                    caloriesInput = result.calories?.toInputString().orEmpty(),
                    rxStatus = result.rxStatus,
                    rpeInput = result.rpe?.toString().orEmpty(),
                    condition = result.condition ?: Condition.UNKNOWN,
                    memoInput = result.memo.orEmpty(),
                    hasExistingResult = true
                )
            }
        }
    }

    fun onScoreTypeChange(value: ScoreType) {
        updateInput { copy(scoreType = value) }
    }

    fun onTimeSecondsChange(value: String) {
        updateInput { copy(timeSecondsInput = value) }
    }

    fun onRoundsChange(value: String) {
        updateInput { copy(roundsInput = value) }
    }

    fun onRepsChange(value: String) {
        updateInput { copy(repsInput = value) }
    }

    fun onTotalRepsChange(value: String) {
        updateInput { copy(totalRepsInput = value) }
    }

    fun onLoadChange(value: String) {
        updateInput { copy(loadInput = value) }
    }

    fun onDistanceChange(value: String) {
        updateInput { copy(distanceInput = value) }
    }

    fun onCaloriesChange(value: String) {
        updateInput { copy(caloriesInput = value) }
    }

    fun onRxStatusChange(value: RxStatus) {
        updateInput { copy(rxStatus = value) }
    }

    fun onRpeChange(value: String) {
        updateInput { copy(rpeInput = value) }
    }

    fun onConditionChange(value: Condition) {
        updateInput { copy(condition = value) }
    }

    fun onMemoChange(value: String) {
        updateInput { copy(memoInput = value) }
    }

    fun saveResult() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, message = null, savedResultId = null) }

            val currentState = _uiState.value
            val parsedInput = currentState.toParsedInput()
            val validationErrors = WodlogValidators.validateResultInput(
                scoreType = currentState.scoreType,
                timeSeconds = parsedInput.timeSeconds,
                rounds = parsedInput.rounds,
                reps = parsedInput.reps,
                totalReps = parsedInput.totalReps,
                loadKg = parsedInput.loadKg,
                distanceMeters = parsedInput.distanceMeters,
                calories = parsedInput.calories,
                rpe = parsedInput.rpe,
                condition = currentState.condition
            ).errors
            val errors = (parsedInput.parseErrors + validationErrors).distinct()

            if (errors.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        validationErrors = errors,
                        message = "Check result input values"
                    )
                }
                return@launch
            }

            val now = nowProvider()
            val result = WodResult(
                id = loadedResult?.id ?: 0L,
                wodId = currentState.wodId,
                scoreType = requireNotNull(currentState.scoreType),
                timeSeconds = parsedInput.timeSeconds,
                rounds = parsedInput.rounds,
                extraReps = parsedInput.reps,
                totalReps = parsedInput.totalReps,
                loadKg = parsedInput.loadKg,
                distanceMeters = parsedInput.distanceMeters,
                calories = parsedInput.calories,
                rxStatus = currentState.rxStatus,
                rpe = parsedInput.rpe,
                condition = currentState.condition,
                memo = currentState.memoInput.trimToNull(),
                createdAt = loadedResult?.createdAt ?: now,
                updatedAt = now
            )
            val resultId = repository.saveWodResult(result)
            loadedResult = result.copy(id = resultId)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    validationErrors = emptyList(),
                    message = "Result saved",
                    savedResultId = resultId,
                    hasExistingResult = true
                )
            }
        }
    }

    fun onSavedNavigationHandled() {
        _uiState.update { it.copy(savedResultId = null) }
    }

    private fun ResultEditUiState.toParsedInput(): ParsedResultInput {
        val timeSeconds = parseOptionalInt(timeSecondsInput)
        val rounds = parseOptionalInt(roundsInput)
        val reps = parseOptionalInt(repsInput)
        val totalReps = parseOptionalInt(totalRepsInput)
        val loadKg = parseOptionalDouble(loadInput)
        val distanceMeters = parseOptionalDouble(distanceInput)
        val calories = parseOptionalDouble(caloriesInput)
        val rpe = parseOptionalInt(rpeInput)

        val parseErrors = buildList {
            if (timeSecondsInput.isNotBlank() && timeSeconds == null) add(ValidationError.RESULT_TIME_INVALID)
            if (roundsInput.isNotBlank() && rounds == null) add(ValidationError.RESULT_ROUNDS_INVALID)
            if (repsInput.isNotBlank() && reps == null) add(ValidationError.RESULT_REPS_INVALID)
            if (totalRepsInput.isNotBlank() && totalReps == null) add(ValidationError.RESULT_TOTAL_REPS_INVALID)
            if (loadInput.isNotBlank() && loadKg == null) add(ValidationError.RESULT_LOAD_INVALID)
            if (distanceInput.isNotBlank() && distanceMeters == null) add(ValidationError.RESULT_DISTANCE_INVALID)
            if (caloriesInput.isNotBlank() && calories == null) add(ValidationError.RESULT_CALORIES_INVALID)
            if (rpeInput.isNotBlank() && rpe == null) add(ValidationError.RESULT_RPE_INVALID)
        }

        return ParsedResultInput(
            timeSeconds = timeSeconds,
            rounds = rounds,
            reps = reps,
            totalReps = totalReps,
            loadKg = loadKg,
            distanceMeters = distanceMeters,
            calories = calories,
            rpe = rpe,
            parseErrors = parseErrors
        )
    }

    private fun updateInput(transform: ResultEditUiState.() -> ResultEditUiState) {
        _uiState.update {
            it.transform().copy(
                validationErrors = emptyList(),
                message = null
            )
        }
    }

    private fun parseOptionalInt(value: String): Int? {
        return value.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    }

    private fun parseOptionalDouble(value: String): Double? {
        return value.trim().takeIf { it.isNotEmpty() }?.toDoubleOrNull()
    }

    private fun Double.toInputString(): String {
        return if (this % 1.0 == 0.0) {
            toLong().toString()
        } else {
            toString()
        }
    }

    private fun String.trimToNull(): String? {
        return trim().takeIf { it.isNotEmpty() }
    }
}

private data class ParsedResultInput(
    val timeSeconds: Int?,
    val rounds: Int?,
    val reps: Int?,
    val totalReps: Int?,
    val loadKg: Double?,
    val distanceMeters: Double?,
    val calories: Double?,
    val rpe: Int?,
    val parseErrors: List<ValidationError>
)
