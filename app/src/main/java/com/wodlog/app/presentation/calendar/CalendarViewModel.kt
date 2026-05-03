package com.wodlog.app.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.repository.WodlogRepository
import java.time.DateTimeException
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val repository: WodlogRepository,
    todayProvider: () -> LocalDate = { LocalDate.now() }
) : ViewModel() {
    private val today = todayProvider()

    private val _uiState = MutableStateFlow(
        CalendarUiState(
            visibleYear = today.year,
            visibleMonth = today.monthValue,
            selectedDate = today
        )
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun loadMonth(year: Int, month: Int) {
        val targetMonth = try {
            YearMonth.of(year, month)
        } catch (_: DateTimeException) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Invalid calendar month"
                )
            }
            return
        }
        val selectedDate = _uiState.value.selectedDate.clampToMonth(targetMonth)

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    visibleYear = targetMonth.year,
                    visibleMonth = targetMonth.monthValue,
                    selectedDate = selectedDate,
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                repository.getWodsByMonth(targetMonth.year, targetMonth.monthValue)
            }.onSuccess { monthWods ->
                _uiState.value = buildState(
                    targetMonth = targetMonth,
                    selectedDate = selectedDate,
                    monthWods = monthWods
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load calendar"
                    )
                }
            }
        }
    }

    fun goToPreviousMonth() {
        val currentMonth = YearMonth.of(_uiState.value.visibleYear, _uiState.value.visibleMonth)
        loadMonth(currentMonth.minusMonths(1))
    }

    fun goToNextMonth() {
        val currentMonth = YearMonth.of(_uiState.value.visibleYear, _uiState.value.visibleMonth)
        loadMonth(currentMonth.plusMonths(1))
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { state ->
            val monthWods = state.monthWods
            state.copy(
                selectedDate = date,
                selectedDateWods = monthWods.wodsForDate(date),
                errorMessage = null
            )
        }
    }

    fun refresh() {
        val state = _uiState.value
        loadMonth(state.visibleYear, state.visibleMonth)
    }

    private fun loadMonth(targetMonth: YearMonth) {
        loadMonth(targetMonth.year, targetMonth.monthValue)
    }

    private fun buildState(
        targetMonth: YearMonth,
        selectedDate: LocalDate,
        monthWods: List<Wod>
    ): CalendarUiState {
        return CalendarUiState(
            visibleYear = targetMonth.year,
            visibleMonth = targetMonth.monthValue,
            selectedDate = selectedDate,
            monthWods = monthWods.sortedByDescending { it.date },
            selectedDateWods = monthWods.wodsForDate(selectedDate),
            recordedDates = monthWods.map { it.date }.toSet(),
            isLoading = false,
            errorMessage = null
        )
    }

    private fun LocalDate.clampToMonth(month: YearMonth): LocalDate {
        val day = dayOfMonth.coerceAtMost(month.lengthOfMonth())
        return LocalDate.of(month.year, month.monthValue, day)
    }

    private fun List<Wod>.wodsForDate(date: LocalDate): List<Wod> {
        return filter { it.date == date }.sortedByDescending { it.createdAt }
    }
}
