package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1L,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val crossfitStartDate: LocalDate? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
