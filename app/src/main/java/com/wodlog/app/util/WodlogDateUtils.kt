package com.wodlog.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

object WodlogDateUtils {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun monthRange(year: Int, month: Int): Pair<LocalDate, LocalDate> {
        require(month in 1..12) { "month must be in 1..12" }

        val startDate = LocalDate.of(year, month, 1)
        return startDate to startDate.plusMonths(1)
    }

    fun weekStart(date: LocalDate): LocalDate {
        val daysSinceMonday = date.dayOfWeek.value - DayOfWeek.MONDAY.value
        return date.minusDays(daysSinceMonday.toLong())
    }

    fun calculateTrainingDays(startDate: LocalDate, today: LocalDate): Long {
        if (startDate.isAfter(today)) return 0L
        return ChronoUnit.DAYS.between(startDate, today)
    }

    fun formatDate(date: LocalDate): String {
        return dateFormatter.format(date)
    }

    fun parseDateOrNull(value: String): LocalDate? {
        return try {
            LocalDate.parse(value, dateFormatter)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
