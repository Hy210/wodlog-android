package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.WodResultEntity

@Dao
@JvmSuppressWildcards
interface WodResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: WodResultEntity): Long

    @Update
    suspend fun update(result: WodResultEntity): Int

    @Delete
    suspend fun delete(result: WodResultEntity): Int

    @Query("SELECT * FROM wod_results WHERE id = :id")
    suspend fun getById(id: Long): WodResultEntity?

    @Query("SELECT * FROM wod_results WHERE wodId = :wodId")
    suspend fun getByWodId(wodId: Long): WodResultEntity?

    @Query("DELETE FROM wod_results WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
