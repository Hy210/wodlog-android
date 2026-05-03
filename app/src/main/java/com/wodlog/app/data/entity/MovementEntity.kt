package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movements",
    foreignKeys = [
        ForeignKey(
            entity = WodEntity::class,
            parentColumns = ["id"],
            childColumns = ["wodId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WodSectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["wodId"]),
        Index(value = ["sectionId"])
    ]
)
data class MovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val wodId: Long,
    val sectionId: Long? = null,
    val movementName: String,
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
