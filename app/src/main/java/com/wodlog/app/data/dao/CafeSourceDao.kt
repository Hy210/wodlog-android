package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.CafeSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface CafeSourceDao {
    @Query("SELECT * FROM cafe_sources ORDER BY boxName ASC, id ASC")
    fun observeAll(): Flow<List<CafeSourceEntity>>

    @Query("SELECT * FROM cafe_sources WHERE id = :id")
    suspend fun getById(id: Long): CafeSourceEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cafeSource: CafeSourceEntity): Long

    @Update
    suspend fun update(cafeSource: CafeSourceEntity): Int

    @Delete
    suspend fun delete(cafeSource: CafeSourceEntity): Int

    @Query("DELETE FROM cafe_sources WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT COUNT(*) FROM cafe_sources")
    fun observeCount(): Flow<Int>
}
