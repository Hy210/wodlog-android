package com.wodlog.app.presentation.calendar

import com.wodlog.app.domain.model.Wod
import java.time.LocalDate

data class CalendarUiState(
    val visibleYear: Int,
    val visibleMonth: Int,
    val selectedDate: LocalDate,
    val monthWods: List<Wod> = emptyList(),
    val selectedDateWods: List<Wod> = emptyList(),
    val recordedDates: Set<LocalDate> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
