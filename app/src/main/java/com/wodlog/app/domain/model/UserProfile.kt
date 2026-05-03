package com.wodlog.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class UserProfile(
    val id: Long = 1L,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val crossfitStartDate: LocalDate? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
