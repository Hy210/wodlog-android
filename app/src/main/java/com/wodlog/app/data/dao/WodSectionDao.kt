package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.WodSectionEntity

@Dao
@JvmSuppressWildcards
interface WodSectionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(section: WodSectionEntity): Long

    @Update
    suspend fun update(section: WodSectionEntity): Int

    @Delete
    suspend fun delete(section: WodSectionEntity): Int

    @Query("SELECT * FROM wod_sections WHERE id = :id")
    suspend fun getById(id: Long): WodSectionEntity?

    @Query("SELECT * FROM wod_sections WHERE wodId = :wodId ORDER BY orderIndex ASC, id ASC")
    suspend fun getByWodId(wodId: Long): List<WodSectionEntity>

    @Query("DELETE FROM wod_sections WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
