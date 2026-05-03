package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.LifestyleLogEntity
import java.time.LocalDate

@Dao
@JvmSuppressWildcards
interface LifestyleLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: LifestyleLogEntity): Long

    @Update
    suspend fun update(log: LifestyleLogEntity): Int

    @Delete
    suspend fun delete(log: LifestyleLogEntity): Int

    @Query("SELECT * FROM lifestyle_logs WHERE id = :id")
    suspend fun getById(id: Long): LifestyleLogEntity?

    @Query("SELECT * FROM lifestyle_logs WHERE weekStartDate = :weekStartDate")
    suspend fun getByWeekStartDate(weekStartDate: LocalDate): LifestyleLogEntity?

    @Query("SELECT * FROM lifestyle_logs ORDER BY weekStartDate DESC, id DESC")
    suspend fun getAllLatestFirst(): List<LifestyleLogEntity>

    @Query("DELETE FROM lifestyle_logs WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
