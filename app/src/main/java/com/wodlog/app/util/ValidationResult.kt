package com.wodlog.app.util

data class ValidationResult(
    val errors: List<ValidationError> = emptyList()
) {
    val isValid: Boolean
        get() = errors.isEmpty()

    operator fun plus(error: ValidationError): ValidationResult {
        return copy(errors = errors + error)
    }

    operator fun plus(other: ValidationResult): ValidationResult {
        return copy(errors = errors + other.errors)
    }

    companion object {
        val Valid = ValidationResult()

        fun invalid(vararg errors: ValidationError): ValidationResult {
            return ValidationResult(errors.toList())
        }
    }
}

enum class ValidationError {
    HEIGHT_OUT_OF_RANGE,
    WEIGHT_OUT_OF_RANGE,
    CROSSFIT_START_DATE_IN_FUTURE,
    WOD_DATE_INVALID,
    WOD_DATE_REQUIRED,
    WOD_TITLE_BLANK,
    WOD_TYPE_REQUIRED,
    MOVEMENT_WEIGHT_INVALID,
    MOVEMENT_REPS_INVALID,
    MOVEMENT_SETS_INVALID,
    MOVEMENT_ROUNDS_INVALID,
    MOVEMENT_DISTANCE_INVALID,
    MOVEMENT_CALORIES_INVALID,
    MOVEMENT_DURATION_INVALID,
    MOVEMENT_NAME_BLANK,
    MOVEMENT_WEIGHT_NEGATIVE,
    MOVEMENT_REPS_NEGATIVE,
    MOVEMENT_SETS_NEGATIVE,
    MOVEMENT_ROUNDS_NEGATIVE,
    MOVEMENT_DISTANCE_NEGATIVE,
    MOVEMENT_CALORIES_NEGATIVE,
    MOVEMENT_DURATION_NEGATIVE,
    SCORE_TYPE_REQUIRED,
    RESULT_TIME_INVALID,
    RESULT_ROUNDS_INVALID,
    RESULT_REPS_INVALID,
    RESULT_TOTAL_REPS_INVALID,
    RESULT_LOAD_INVALID,
    RESULT_DISTANCE_INVALID,
    RESULT_CALORIES_INVALID,
    RESULT_RPE_INVALID,
    RESULT_TIME_NEGATIVE,
    RESULT_ROUNDS_NEGATIVE,
    RESULT_REPS_NEGATIVE,
    RESULT_TOTAL_REPS_NEGATIVE,
    RESULT_LOAD_NEGATIVE,
    RESULT_DISTANCE_NEGATIVE,
    RESULT_CALORIES_NEGATIVE,
    RESULT_RPE_OUT_OF_RANGE,
    LIFESTYLE_WEEK_START_NOT_MONDAY,
    LIFESTYLE_SLEEP_HOURS_OUT_OF_RANGE,
    LIFESTYLE_ALCOHOL_AMOUNT_NEGATIVE,
    LIFESTYLE_SMOKING_AMOUNT_NEGATIVE
}
