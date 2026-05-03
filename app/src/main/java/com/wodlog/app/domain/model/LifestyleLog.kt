package com.wodlog.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class LifestyleLog(
    val id: Long = 0L,
    val weekStartDate: LocalDate,
    val mealSummary: String? = null,
    val alcohol: Boolean? = null,
    val alcoholAmountPerWeek: String? = null,
    val smoking: Boolean? = null,
    val smokingAmountPerWeek: String? = null,
    val sleepAverageHours: Double? = null,
    val notes: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
