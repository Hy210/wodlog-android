package com.wodlog.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wodlog.app.data.dao.AiReportDao
import com.wodlog.app.data.dao.CafeSourceDao
import com.wodlog.app.data.dao.LifestyleLogDao
import com.wodlog.app.data.dao.MovementDao
import com.wodlog.app.data.dao.UserProfileDao
import com.wodlog.app.data.dao.WodDao
import com.wodlog.app.data.dao.WodResultDao
import com.wodlog.app.data.dao.WodSectionDao
import com.wodlog.app.data.entity.AiReportEntity
import com.wodlog.app.data.entity.CafeSourceEntity
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
        AiReportEntity::class,
        CafeSourceEntity::class
    ],
    version = 3,
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

    abstract fun cafeSourceDao(): CafeSourceDao

    companion object {
        @Volatile
        private var instance: WodlogDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `cafe_sources` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `boxName` TEXT NOT NULL,
                        `boardUrl` TEXT NOT NULL,
                        `titleKeywords` TEXT NOT NULL,
                        `preferMobileUrl` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_cafe_sources_boxName` ON `cafe_sources` (`boxName`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_cafe_sources_boardUrl` ON `cafe_sources` (`boardUrl`)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `wods` ADD COLUMN `sourceType` TEXT NOT NULL DEFAULT 'MANUAL'")
                db.execSQL("ALTER TABLE `wods` ADD COLUMN `sourceUrl` TEXT")
                db.execSQL("ALTER TABLE `wods` ADD COLUMN `importedAt` INTEGER")
            }
        }

        fun getInstance(context: Context): WodlogDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WodlogDatabase::class.java,
                    "wodlog.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
