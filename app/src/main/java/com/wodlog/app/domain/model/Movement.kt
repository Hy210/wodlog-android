package com.wodlog.app.domain.model

data class Movement(
    val id: Long = 0L,
    val wodId: Long,
    val sectionId: Long? = null,
    val name: String,
    val category: MovementCategory? = null,
    val weightKg: Double? = null,
    val reps: Int? = null,
    val sets: Int? = null,
    val rounds: Int? = null,
    val distanceMeters: Double? = null,
    val calories: Double? = null,
    val durationSeconds: Int? = null,
    val orderIndex: Int,
    val notes: String? = null
)
