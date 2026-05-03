package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "ai_reports",
    foreignKeys = [
        ForeignKey(
            entity = WodEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetWodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["targetWodId"])]
)
data class AiReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val targetWodId: Long,
    val promptText: String? = null,
    val reportText: String,
    val userMemo: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
