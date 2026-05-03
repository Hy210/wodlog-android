package com.wodlog.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wodlog.app.data.entity.AiReportEntity
import com.wodlog.app.data.entity.LifestyleLogEntity
import com.wodlog.app.data.entity.MovementEntity
import com.wodlog.app.data.entity.UserProfileEntity
import com.wodlog.app.data.entity.WodEntity
import com.wodlog.app.data.entity.WodResultEntity
import com.wodlog.app.data.entity.WodSectionEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WodEntity::class,
        WodSectionEntity::class,
        MovementEntity::class,
        WodResultEntity::class,
        LifestyleLogEntity::class,
        AiReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(WodlogTypeConverters::class)
abstract class WodlogDatabase : RoomDatabase()
