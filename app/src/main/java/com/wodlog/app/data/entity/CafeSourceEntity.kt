package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "cafe_sources",
    indices = [
        Index(value = ["boxName"]),
        Index(value = ["boardUrl"], unique = true)
    ]
)
data class CafeSourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val boxName: String,
    val boardUrl: String,
    val titleKeywords: List<String>,
    val preferMobileUrl: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
