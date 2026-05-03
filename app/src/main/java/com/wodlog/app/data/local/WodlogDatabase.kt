package com.wodlog.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wodlog.app.data.dao.AiReportDao
import com.wodlog.app.data.dao.LifestyleLogDao
import com.wodlog.app.data.dao.MovementDao
import com.wodlog.app.data.dao.UserProfileDao
import com.wodlog.app.data.dao.WodDao
import com.wodlog.app.data.dao.WodResultDao
import com.wodlog.app.data.dao.WodSectionDao
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
abstract class WodlogDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    abstract fun wodDao(): WodDao

    abstract fun wodSectionDao(): WodSectionDao

    abstract fun movementDao(): MovementDao

    abstract fun wodResultDao(): WodResultDao

    abstract fun lifestyleLogDao(): LifestyleLogDao

    abstract fun aiReportDao(): AiReportDao

    companion object {
        @Volatile
        private var instance: WodlogDatabase? = null

        fun getInstance(context: Context): WodlogDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WodlogDatabase::class.java,
                    "wodlog.db"
                ).build().also { instance = it }
            }
        }
    }
}
