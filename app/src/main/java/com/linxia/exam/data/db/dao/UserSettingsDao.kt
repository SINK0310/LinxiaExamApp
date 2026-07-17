package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linxia.exam.data.db.entity.UserSettings

@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: UserSettings): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(settingsList: List<UserSettings>)

    @Query("SELECT * FROM user_settings WHERE user_id = :userId AND key = :key")
    suspend fun getByKey(userId: Long, key: String): UserSettings?

    @Query("SELECT * FROM user_settings WHERE user_id = :userId")
    suspend fun getAll(userId: Long): List<UserSettings>

    @Query("DELETE FROM user_settings WHERE user_id = :userId AND key = :key")
    suspend fun deleteByKey(userId: Long, key: String)

    @Query("DELETE FROM user_settings WHERE user_id = :userId")
    suspend fun clearAll(userId: Long)
}