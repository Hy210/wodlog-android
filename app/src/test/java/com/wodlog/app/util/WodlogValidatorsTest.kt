package com.wodlog.app.util

import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.WodType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WodlogValidatorsTest {
    private val today = LocalDate.of(2026, 5, 3)

    @Test
    fun validateProfileInput_validProfileIsValid() {
        val result = WodlogValidators.validateProfileInput(
            heightCm = 180.0,
            weightKg = 82.5,
            crossfitStartDate = LocalDate.of(2025, 1, 1),
            today = today
        )

        assertTrue(result.isValid)
    }

    @Test
    fun validateProfileInput_futureStartDateIsInvalid() {
        val result = WodlogValidators.validateProfileInput(
            heightCm = null,
            weightKg = null,
            crossfitStartDate = LocalDate.of(2026, 5, 4),
            today = today
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.CROSSFIT_START_DATE_IN_FUTURE))
    }

    @Test
    fun validateProfileInput_heightAndWeightOutOfRangeAreInvalid() {
        val result = WodlogValidators.validateProfileInput(
            heightCm = 260.0,
            weightKg = 10.0,
            crossfitStartDate = null,
            today = today
        )

        assertEquals(
            listOf(
                ValidationError.HEIGHT_OUT_OF_RANGE,
                ValidationError.WEIGHT_OUT_OF_RANGE
            ),
            result.errors
        )
    }

    @Test
    fun validateWodInput_blankTitleIsInvalid() {
        val result = WodlogValidators.validateWodInput(
            date = today,
            title = "   ",
            type = WodType.FOR_TIME
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.WOD_TITLE_BLANK))
    }

    @Test
    fun validateWodInput_missingDateAndTypeAreInvalid() {
        val result = WodlogValidators.validateWodInput(
            date = null,
            title = "Fran",
            type = null
        )

        assertEquals(
            listOf(
                ValidationError.WOD_DATE_REQUIRED,
                ValidationError.WOD_TYPE_REQUIRED
            ),
            result.errors
        )
    }

    @Test
    fun validateMovementInput_negativeMetricIsInvalid() {
        val result = WodlogValidators.validateMovementInput(
            name = "Thruster",
            category = null,
            weightKg = -1.0,
            reps = 21,
            sets = 3,
            rounds = 5,
            distanceMeters = 0.0,
            calories = 0.0,
            durationSeconds = 60
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.MOVEMENT_WEIGHT_NEGATIVE))
    }

    @Test
    fun validateMovementInput_blankNameIsInvalid() {
        val result = WodlogValidators.validateMovementInput(
            name = "",
            category = null,
            weightKg = null,
            reps = null,
            sets = null,
            rounds = null,
            distanceMeters = null,
            calories = null,
            durationSeconds = null
        )

        assertTrue(result.errors.contains(ValidationError.MOVEMENT_NAME_BLANK))
    }

    @Test
    fun validateResultInput_rpeElevenIsInvalid() {
        val result = WodlogValidators.validateResultInput(
            scoreType = ScoreType.TIME,
            timeSeconds = 420,
            rounds = null,
            reps = null,
            totalReps = null,
            loadKg = null,
            distanceMeters = null,
            calories = null,
            rpe = 11,
            condition = null
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.RESULT_RPE_OUT_OF_RANGE))
    }

    @Test
    fun validateResultInput_negativeRepsAndMissingScoreTypeAreInvalid() {
        val result = WodlogValidators.validateResultInput(
            scoreType = null,
            timeSeconds = null,
            rounds = null,
            reps = -1,
            totalReps = null,
            loadKg = null,
            distanceMeters = null,
            calories = null,
            rpe = null,
            condition = null
        )

        assertTrue(result.errors.contains(ValidationError.SCORE_TYPE_REQUIRED))
        assertTrue(result.errors.contains(ValidationError.RESULT_REPS_NEGATIVE))
    }

    @Test
    fun validateLifestyleInput_nonMondayWeekStartIsInvalid() {
        val result = WodlogValidators.validateLifestyleInput(
            weekStartDate = LocalDate.of(2026, 5, 3),
            sleepAverageHours = 7.0,
            alcoholAmountPerWeek = null,
            smokingAmountPerWeek = null
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.LIFESTYLE_WEEK_START_NOT_MONDAY))
    }

    @Test
    fun validateLifestyleInput_averageSleepHoursTwentyFiveIsInvalid() {
        val result = WodlogValidators.validateLifestyleInput(
            weekStartDate = LocalDate.of(2026, 4, 27),
            sleepAverageHours = 25.0,
            alcoholAmountPerWeek = null,
            smokingAmountPerWeek = null
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(ValidationError.LIFESTYLE_SLEEP_HOURS_OUT_OF_RANGE))
    }

    @Test
    fun validateLifestyleInput_negativeAmountsAreInvalid() {
        val result = WodlogValidators.validateLifestyleInput(
            weekStartDate = LocalDate.of(2026, 4, 27),
            sleepAverageHours = 7.0,
            alcoholAmountPerWeek = -1.0,
            smokingAmountPerWeek = -2.0
        )

        assertTrue(result.errors.contains(ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_NEGATIVE))
        assertTrue(result.errors.contains(ValidationError.LIFESTYLE_SMOKING_AMOUNT_NEGATIVE))
    }

    @Test
    fun validateLifestyleInput_blankOptionalValuesAreRepresentedAsNullAndValid() {
        val result = WodlogValidators.validateLifestyleInput(
            weekStartDate = LocalDate.of(2026, 4, 27),
            sleepAverageHours = null,
            alcoholAmountPerWeek = null,
            smokingAmountPerWeek = null
        )

        assertTrue(result.isValid)
    }
}
