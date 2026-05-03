package com.wodlog.app.util

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class WodlogDateUtilsTest {
    @Test
    fun monthRange_returnsStartInclusiveAndNextMonthExclusive() {
        val range = WodlogDateUtils.monthRange(2026, 5)

        assertEquals(LocalDate.of(2026, 5, 1), range.first)
        assertEquals(LocalDate.of(2026, 6, 1), range.second)
    }

    @Test
    fun monthRange_decemberMovesToNextYear() {
        val range = WodlogDateUtils.monthRange(2026, 12)

        assertEquals(LocalDate.of(2026, 12, 1), range.first)
        assertEquals(LocalDate.of(2027, 1, 1), range.second)
    }

    @Test
    fun monthRange_invalidMonthThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            WodlogDateUtils.monthRange(2026, 13)
        }
    }

    @Test
    fun weekStart_returnsMondayForSameWeek() {
        val weekStart = WodlogDateUtils.weekStart(LocalDate.of(2026, 5, 3))

        assertEquals(LocalDate.of(2026, 4, 27), weekStart)
    }

    @Test
    fun weekStart_returnsSameDateWhenAlreadyMonday() {
        val monday = LocalDate.of(2026, 4, 27)

        assertEquals(monday, WodlogDateUtils.weekStart(monday))
    }

    @Test
    fun calculateTrainingDays_returnsZeroForFutureStartDate() {
        val days = WodlogDateUtils.calculateTrainingDays(
            startDate = LocalDate.of(2026, 5, 4),
            today = LocalDate.of(2026, 5, 3)
        )

        assertEquals(0L, days)
    }

    @Test
    fun formatDate_usesIsoLocalDate() {
        assertEquals("2026-05-03", WodlogDateUtils.formatDate(LocalDate.of(2026, 5, 3)))
    }

    @Test
    fun parseDateOrNull_returnsDateForValidString() {
        assertEquals(LocalDate.of(2026, 5, 3), WodlogDateUtils.parseDateOrNull("2026-05-03"))
    }

    @Test
    fun parseDateOrNull_returnsNullForInvalidString() {
        assertNull(WodlogDateUtils.parseDateOrNull("not-a-date"))
    }
}
