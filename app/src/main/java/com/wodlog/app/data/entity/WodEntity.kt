package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "wods",
    indices = [Index(value = ["date"])]
)
data class WodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: LocalDate,
    val title: String,
    val type: WodType,
    val rawText: String? = null,
    val notes: String? = null,
    val sourceType: WodSourceType = WodSourceType.MANUAL,
    val sourceUrl: String? = null,
    val importedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
