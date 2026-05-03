package com.wodlog.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wodlog.app.data.entity.UserProfileEntity

@Dao
@JvmSuppressWildcards
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity): Long

    @Update
    suspend fun update(profile: UserProfileEntity): Int

    @Delete
    suspend fun delete(profile: UserProfileEntity): Int

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    suspend fun getById(id: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE id = 1")
    suspend fun getProfile(): UserProfileEntity?

    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
