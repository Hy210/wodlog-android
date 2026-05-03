package com.wodlog.app.presentation.lifestyle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.util.ValidationError
import com.wodlog.app.util.WodlogDateUtils
import com.wodlog.app.util.WodlogValidators
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LifestyleViewModel(
    private val repository: WodlogRepository,
    private val todayProvider: () -> LocalDate = { LocalDate.now() },
    private val nowProvider: () -> Instant = { Instant.now() }
) : ViewModel() {
    private val initialWeekStartDate = WodlogDateUtils.weekStart(todayProvider())
    private val _uiState = MutableStateFlow(
        LifestyleUiState(
            weekStartDateInput = WodlogDateUtils.formatDate(initialWeekStartDate)
        )
    )
    val uiState: StateFlow<LifestyleUiState> = _uiState.asStateFlow()

    private var loadedLog: LifestyleLog? = null

    fun loadCurrentWeek() {
        loadLifestyleLog(WodlogDateUtils.weekStart(todayProvider()))
    }

    fun loadLifestyleLog(weekStartDate: LocalDate) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    weekStartDateInput = WodlogDateUtils.formatDate(weekStartDate),
                    isLoading = true,
                    message = null,
                    validationErrors = emptyList(),
                    savedLifestyleLogId = null
                )
            }

            val log = repository.getLifestyleLogByWeekStart(weekStartDate)
            loadedLog = log

            _uiState.value = if (log == null) {
                LifestyleUiState(
                    weekStartDateInput = WodlogDateUtils.formatDate(weekStartDate)
                )
            } else {
                LifestyleUiState(
                    weekStartDateInput = WodlogDateUtils.formatDate(log.weekStartDate),
                    dietSummaryInput = log.mealSummary.orEmpty(),
                    hasAlcohol = log.alcohol == true,
                    alcoholAmountPerWeekInput = log.alcoholAmountPerWeek.orEmpty(),
                    hasSmoking = log.smoking == true,
                    smokingAmountPerWeekInput = log.smokingAmountPerWeek.orEmpty(),
                    averageSleepHoursInput = log.sleepAverageHours?.toInputString().orEmpty(),
                    memoInput = log.notes.orEmpty(),
                    hasExistingLog = true
                )
            }
        }
    }

    fun onWeekStartDateChange(value: String) {
        updateInput { copy(weekStartDateInput = value) }
    }

    fun onDietSummaryChange(value: String) {
        updateInput { copy(dietSummaryInput = value) }
    }

    fun onHasAlcoholChange(value: Boolean) {
        updateInput {
            copy(
                hasAlcohol = value,
                alcoholAmountPerWeekInput = if (value) alcoholAmountPerWeekInput else ""
            )
        }
    }

    fun onAlcoholAmountChange(value: String) {
        updateInput { copy(alcoholAmountPerWeekInput = value) }
    }

    fun onHasSmokingChange(value: Boolean) {
        updateInput {
            copy(
                hasSmoking = value,
                smokingAmountPerWeekInput = if (value) smokingAmountPerWeekInput else ""
            )
        }
    }

    fun onSmokingAmountChange(value: String) {
        updateInput { copy(smokingAmountPerWeekInput = value) }
    }

    fun onAverageSleepHoursChange(value: String) {
        updateInput { copy(averageSleepHoursInput = value) }
    }

    fun onMemoChange(value: String) {
        updateInput { copy(memoInput = value) }
    }

    fun saveLifestyleLog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    message = null,
                    savedLifestyleLogId = null
                )
            }

            val currentState = _uiState.value
            val parsedInput = currentState.toParsedInput()
            val parseErrors = parsedInput.parseErrors

            if (parsedInput.weekStartDate == null) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        validationErrors = (parseErrors + ValidationError.WOD_DATE_INVALID).distinct(),
                        message = "Check lifestyle input values"
                    )
                }
                return@launch
            }

            val validationErrors = WodlogValidators.validateLifestyleInput(
                weekStartDate = parsedInput.weekStartDate,
                sleepAverageHours = parsedInput.averageSleepHours,
                alcoholAmountPerWeek = parsedInput.alcoholAmountPerWeek,
                smokingAmountPerWeek = parsedInput.smokingAmountPerWeek
            ).errors
            val errors = (parseErrors + validationErrors).distinct()

            if (errors.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        validationErrors = errors,
                        message = "Check lifestyle input values"
                    )
                }
                return@launch
            }

            val now = nowProvider()
            val log = LifestyleLog(
                id = loadedLog?.id ?: 0L,
                weekStartDate = parsedInput.weekStartDate,
                mealSummary = currentState.dietSummaryInput.trimToNull(),
                alcohol = currentState.hasAlcohol,
                alcoholAmountPerWeek = parsedInput.alcoholAmountPerWeek
                    ?.toInputString()
                    ?.takeIf { currentState.hasAlcohol },
                smoking = currentState.hasSmoking,
                smokingAmountPerWeek = parsedInput.smokingAmountPerWeek
                    ?.toInputString()
                    ?.takeIf { currentState.hasSmoking },
                sleepAverageHours = parsedInput.averageSleepHours,
                notes = currentState.memoInput.trimToNull(),
                createdAt = loadedLog?.createdAt ?: now,
                updatedAt = now
            )
            val logId = repository.saveLifestyleLog(log)
            loadedLog = log.copy(id = logId)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    validationErrors = emptyList(),
                    message = "Lifestyle log saved",
                    savedLifestyleLogId = logId,
                    hasExistingLog = true
                )
            }
        }
    }

    private fun LifestyleUiState.toParsedInput(): ParsedLifestyleInput {
        val weekStartDate = WodlogDateUtils.parseDateOrNull(weekStartDateInput.trim())
        val alcoholAmount = parseOptionalDouble(alcoholAmountPerWeekInput)
            ?.takeIf { hasAlcohol }
        val smokingAmount = parseOptionalDouble(smokingAmountPerWeekInput)
            ?.takeIf { hasSmoking }
        val averageSleepHours = parseOptionalDouble(averageSleepHoursInput)

        val parseErrors = buildList {
            if (weekStartDateInput.isNotBlank() && weekStartDate == null) {
                add(ValidationError.WOD_DATE_INVALID)
            }
            if (hasAlcohol && alcoholAmountPerWeekInput.isNotBlank() && parseOptionalDouble(alcoholAmountPerWeekInput) == null) {
                add(ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_INVALID)
            }
            if (hasSmoking && smokingAmountPerWeekInput.isNotBlank() && parseOptionalDouble(smokingAmountPerWeekInput) == null) {
                add(ValidationError.LIFESTYLE_SMOKING_AMOUNT_INVALID)
            }
            if (averageSleepHoursInput.isNotBlank() && averageSleepHours == null) {
                add(ValidationError.LIFESTYLE_SLEEP_HOURS_INVALID)
            }
        }

        return ParsedLifestyleInput(
            weekStartDate = weekStartDate,
            alcoholAmountPerWeek = alcoholAmount,
            smokingAmountPerWeek = smokingAmount,
            averageSleepHours = averageSleepHours,
            parseErrors = parseErrors
        )
    }

    private fun updateInput(transform: LifestyleUiState.() -> LifestyleUiState) {
        _uiState.update {
            it.transform().copy(
                validationErrors = emptyList(),
                message = null,
                savedLifestyleLogId = null
            )
        }
    }

    private fun parseOptionalDouble(value: String): Double? {
        return value.trim().takeIf { it.isNotEmpty() }?.toDoubleOrNull()
    }

    private fun String.trimToNull(): String? {
        return trim().takeIf { it.isNotEmpty() }
    }

    private fun Double.toInputString(): String {
        return if (this % 1.0 == 0.0) {
            toLong().toString()
        } else {
            toString()
        }
    }
}

private data class ParsedLifestyleInput(
    val weekStartDate: LocalDate?,
    val alcoholAmountPerWeek: Double?,
    val smokingAmountPerWeek: Double?,
    val averageSleepHours: Double?,
    val parseErrors: List<ValidationError>
)
