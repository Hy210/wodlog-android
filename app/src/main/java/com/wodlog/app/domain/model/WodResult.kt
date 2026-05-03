package com.wodlog.app.domain.model

import java.time.Instant

data class WodResult(
    val id: Long = 0L,
    val wodId: Long,
    val scoreType: ScoreType,
    val timeSeconds: Int? = null,
    val rounds: Int? = null,
    val extraReps: Int? = null,
    val totalReps: Int? = null,
    val loadKg: Double? = null,
    val distanceMeters: Double? = null,
    val calories: Double? = null,
    val rxStatus: RxStatus = RxStatus.UNKNOWN,
    val rpe: Int? = null,
    val condition: Condition? = null,
    val memo: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
