package com.wodlog.app.data.local

import com.wodlog.app.data.entity.Condition
import com.wodlog.app.data.entity.MovementCategory
import com.wodlog.app.data.entity.RxStatus
import com.wodlog.app.data.entity.ScoreType
import com.wodlog.app.data.entity.WodType
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WodlogTypeConvertersTest {
    private val converters = WodlogTypeConverters()

    @Test
    fun localDate_roundTripsThroughString() {
        val date = LocalDate.of(2026, 5, 3)

        val stored = converters.localDateToString(date)

        assertEquals(date, converters.stringToLocalDate(stored))
    }

    @Test
    fun instant_roundTripsThroughEpochMillis() {
        val instant = Instant.ofEpochMilli(1_777_777_777_000)

        val stored = converters.instantToEpochMillis(instant)

        assertEquals(instant, converters.epochMillisToInstant(stored))
    }

    @Test
    fun enums_roundTripThroughName() {
        assertEquals(WodType.AMRAP, converters.stringToWodType(converters.wodTypeToString(WodType.AMRAP)))
        assertEquals(
            MovementCategory.WEIGHTLIFTING,
            converters.stringToMovementCategory(
                converters.movementCategoryToString(MovementCategory.WEIGHTLIFTING)
            )
        )
        assertEquals(ScoreType.TIME, converters.stringToScoreType(converters.scoreTypeToString(ScoreType.TIME)))
        assertEquals(RxStatus.SCALED, converters.stringToRxStatus(converters.rxStatusToString(RxStatus.SCALED)))
        assertEquals(Condition.TIRED, converters.stringToCondition(converters.conditionToString(Condition.TIRED)))
    }

    @Test
    fun nullableValues_remainNull() {
        assertNull(converters.localDateToString(null))
        assertNull(converters.stringToLocalDate(null))
        assertNull(converters.instantToEpochMillis(null))
        assertNull(converters.epochMillisToInstant(null))
        assertNull(converters.wodTypeToString(null))
        assertNull(converters.stringToWodType(null))
    }

    @Test
    fun stringList_roundTripsWhenValuesContainSeparators() {
        val value = listOf("Fran", "rounds,reps", "barbell|pull-up", "")

        val stored = converters.stringListToString(value)

        assertEquals(value, converters.stringToStringList(stored))
    }

    @Test
    fun emptyStringList_roundTrips() {
        val value = emptyList<String>()

        val stored = converters.stringListToString(value)

        assertEquals(value, converters.stringToStringList(stored))
    }
}
