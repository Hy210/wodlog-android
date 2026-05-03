package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.MovementEntity

@Dao
@JvmSuppressWildcards
interface MovementDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(movement: MovementEntity): Long

    @Update
    suspend fun update(movement: MovementEntity): Int

    @Delete
    suspend fun delete(movement: MovementEntity): Int

    @Query("SELECT * FROM movements WHERE id = :id")
    suspend fun getById(id: Long): MovementEntity?

    @Query("SELECT * FROM movements WHERE wodId = :wodId ORDER BY orderIndex ASC, id ASC")
    suspend fun getByWodId(wodId: Long): List<MovementEntity>

    @Query("SELECT * FROM movements WHERE sectionId = :sectionId ORDER BY orderIndex ASC, id ASC")
    suspend fun getBySectionId(sectionId: Long): List<MovementEntity>

    @Query("DELETE FROM movements WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
