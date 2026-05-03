package com.wodlog.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wod_sections",
    foreignKeys = [
        ForeignKey(
            entity = WodEntity::class,
            parentColumns = ["id"],
            childColumns = ["wodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["wodId"])]
)
data class WodSectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val wodId: Long,
    val name: String,
    val orderIndex: Int
)
