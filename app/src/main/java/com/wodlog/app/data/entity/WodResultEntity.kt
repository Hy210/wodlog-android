package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "wod_results",
    foreignKeys = [
        ForeignKey(
            entity = WodEntity::class,
            parentColumns = ["id"],
            childColumns = ["wodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["wodId"], unique = true)
    ]
)
data class WodResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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
