package com.wodlog.app.util

import com.wodlog.app.domain.model.Condition
import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.ScoreType
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodType
import java.time.DayOfWeek
import java.time.LocalDate

object WodlogValidators {
    fun validateProfile(profile: UserProfile, today: LocalDate): ValidationResult {
        return validateProfileInput(
            heightCm = profile.heightCm,
            weightKg = profile.weightKg,
            crossfitStartDate = profile.crossfitStartDate,
            today = today
        )
    }

    fun validateProfileInput(
        heightCm: Double?,
        weightKg: Double?,
        crossfitStartDate: LocalDate?,
        today: LocalDate
    ): ValidationResult {
        var result = ValidationResult.Valid

        if (heightCm != null && heightCm !in 50.0..250.0) {
            result += ValidationError.HEIGHT_OUT_OF_RANGE
        }
        if (weightKg != null && weightKg !in 20.0..300.0) {
            result += ValidationError.WEIGHT_OUT_OF_RANGE
        }
        if (crossfitStartDate != null && crossfitStartDate.isAfter(today)) {
            result += ValidationError.CROSSFIT_START_DATE_IN_FUTURE
        }

        return result
    }

    fun validateWod(wod: Wod): ValidationResult {
        return validateWodInput(
            date = wod.date,
            title = wod.title,
            type = wod.type
        )
    }

    fun validateWodInput(
        date: LocalDate?,
        title: String,
        type: WodType?
    ): ValidationResult {
        var result = ValidationResult.Valid

        if (date == null) {
            result += ValidationError.WOD_DATE_REQUIRED
        }
        if (title.isBlank()) {
            result += ValidationError.WOD_TITLE_BLANK
        }
        if (type == null) {
            result += ValidationError.WOD_TYPE_REQUIRED
        }

        return result
    }

    fun validateMovement(movement: Movement): ValidationResult {
        return validateMovementInput(
            name = movement.name,
            category = movement.category,
            weightKg = movement.weightKg,
            reps = movement.reps,
            sets = movement.sets,
            rounds = movement.rounds,
            distanceMeters = movement.distanceMeters,
            calories = movement.calories,
            durationSeconds = movement.durationSeconds
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun validateMovementInput(
        name: String,
        category: MovementCategory?,
        weightKg: Double?,
        reps: Int?,
        sets: Int?,
        rounds: Int?,
        distanceMeters: Double?,
        calories: Double?,
        durationSeconds: Int?
    ): ValidationResult {
        var result = ValidationResult.Valid

        if (name.isBlank()) {
            result += ValidationError.MOVEMENT_NAME_BLANK
        }
        if (weightKg != null && weightKg < 0.0) {
            result += ValidationError.MOVEMENT_WEIGHT_NEGATIVE
        }
        if (reps != null && reps < 0) {
            result += ValidationError.MOVEMENT_REPS_NEGATIVE
        }
        if (sets != null && sets < 0) {
            result += ValidationError.MOVEMENT_SETS_NEGATIVE
        }
        if (rounds != null && rounds < 0) {
            result += ValidationError.MOVEMENT_ROUNDS_NEGATIVE
        }
        if (distanceMeters != null && distanceMeters < 0.0) {
            result += ValidationError.MOVEMENT_DISTANCE_NEGATIVE
        }
        if (calories != null && calories < 0.0) {
            result += ValidationError.MOVEMENT_CALORIES_NEGATIVE
        }
        if (durationSeconds != null && durationSeconds < 0) {
            result += ValidationError.MOVEMENT_DURATION_NEGATIVE
        }

        return result
    }

    fun validateResult(result: WodResult): ValidationResult {
        return validateResultInput(
            scoreType = result.scoreType,
            timeSeconds = result.timeSeconds,
            rounds = result.rounds,
            reps = result.extraReps,
            totalReps = result.totalReps,
            loadKg = result.loadKg,
            distanceMeters = result.distanceMeters,
            calories = result.calories,
            rpe = result.rpe,
            condition = result.condition
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun validateResultInput(
        scoreType: ScoreType?,
        timeSeconds: Int?,
        rounds: Int?,
        reps: Int?,
        totalReps: Int?,
        loadKg: Double?,
        distanceMeters: Double?,
        calories: Double?,
        rpe: Int?,
        condition: Condition?
    ): ValidationResult {
        var result = ValidationResult.Valid

        if (scoreType == null) {
            result += ValidationError.SCORE_TYPE_REQUIRED
        }
        if (timeSeconds != null && timeSeconds < 0) {
            result += ValidationError.RESULT_TIME_NEGATIVE
        }
        if (rounds != null && rounds < 0) {
            result += ValidationError.RESULT_ROUNDS_NEGATIVE
        }
        if (reps != null && reps < 0) {
            result += ValidationError.RESULT_REPS_NEGATIVE
        }
        if (totalReps != null && totalReps < 0) {
            result += ValidationError.RESULT_TOTAL_REPS_NEGATIVE
        }
        if (loadKg != null && loadKg < 0.0) {
            result += ValidationError.RESULT_LOAD_NEGATIVE
        }
        if (distanceMeters != null && distanceMeters < 0.0) {
            result += ValidationError.RESULT_DISTANCE_NEGATIVE
        }
        if (calories != null && calories < 0.0) {
            result += ValidationError.RESULT_CALORIES_NEGATIVE
        }
        if (rpe != null && rpe !in 1..10) {
            result += ValidationError.RESULT_RPE_OUT_OF_RANGE
        }

        return result
    }

    fun validateLifestyle(log: LifestyleLog): ValidationResult {
        return validateLifestyleInput(
            weekStartDate = log.weekStartDate,
            sleepAverageHours = log.sleepAverageHours,
            alcoholAmountPerWeek = null,
            smokingAmountPerWeek = null
        )
    }

    fun validateLifestyleInput(
        weekStartDate: LocalDate,
        sleepAverageHours: Double?,
        alcoholAmountPerWeek: Double?,
        smokingAmountPerWeek: Double?
    ): ValidationResult {
        var result = ValidationResult.Valid

        if (weekStartDate.dayOfWeek != DayOfWeek.MONDAY) {
            result += ValidationError.LIFESTYLE_WEEK_START_NOT_MONDAY
        }
        if (sleepAverageHours != null && sleepAverageHours !in 0.0..24.0) {
            result += ValidationError.LIFESTYLE_SLEEP_HOURS_OUT_OF_RANGE
        }
        if (alcoholAmountPerWeek != null && alcoholAmountPerWeek < 0.0) {
            result += ValidationError.LIFESTYLE_ALCOHOL_AMOUNT_NEGATIVE
        }
        if (smokingAmountPerWeek != null && smokingAmountPerWeek < 0.0) {
            result += ValidationError.LIFESTYLE_SMOKING_AMOUNT_NEGATIVE
        }

        return result
    }
}
