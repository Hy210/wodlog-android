package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.WodEntity
import java.time.LocalDate

@Dao
@JvmSuppressWildcards
interface WodDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(wod: WodEntity): Long

    @Update
    suspend fun update(wod: WodEntity): Int

    @Delete
    suspend fun delete(wod: WodEntity): Int

    @Query("DELETE FROM wods WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM wods WHERE id = :id")
    suspend fun getById(id: Long): WodEntity?

    @Query("SELECT * FROM wods ORDER BY date DESC, id DESC")
    suspend fun getAllLatestFirst(): List<WodEntity>

    @Query("SELECT * FROM wods WHERE date = :date ORDER BY id DESC")
    suspend fun getByDate(date: LocalDate): List<WodEntity>

    @Query("SELECT * FROM wods WHERE date >= :startDate AND date < :endDateExclusive ORDER BY date ASC, id ASC")
    suspend fun getByDateRange(startDate: LocalDate, endDateExclusive: LocalDate): List<WodEntity>

    @Query("SELECT * FROM wods ORDER BY date DESC, id DESC LIMIT 3")
    suspend fun getRecentThree(): List<WodEntity>

    @Query("SELECT * FROM wods WHERE date <= :date ORDER BY date DESC, id DESC LIMIT :limit")
    suspend fun getRecentOnOrBefore(date: LocalDate, limit: Int): List<WodEntity>
}
