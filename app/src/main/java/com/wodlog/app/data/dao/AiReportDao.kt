package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.AiReportEntity

@Dao
@JvmSuppressWildcards
interface AiReportDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(report: AiReportEntity): Long

    @Update
    suspend fun update(report: AiReportEntity): Int

    @Delete
    suspend fun delete(report: AiReportEntity): Int

    @Query("SELECT * FROM ai_reports WHERE id = :id")
    suspend fun getById(id: Long): AiReportEntity?

    @Query("SELECT * FROM ai_reports WHERE targetWodId = :wodId ORDER BY createdAt DESC, id DESC")
    suspend fun getByWodId(wodId: Long): List<AiReportEntity>

    @Query("DELETE FROM ai_reports WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
