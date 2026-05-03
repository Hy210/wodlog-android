package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "lifestyle_logs",
    indices = [Index(value = ["weekStartDate"], unique = true)]
)
data class LifestyleLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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
