package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linxia.exam.data.db.entity.OfflineCache

@Dao
interface OfflineCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: OfflineCache): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(caches: List<OfflineCache>)

    @Query("SELECT * FROM offline_cache WHERE cache_key = :key")
    suspend fun getByKey(key: String): OfflineCache?

    @Query("SELECT * FROM offline_cache WHERE expire_time > 0 AND expire_time < :now")
    suspend fun getExpired(now: Long): List<OfflineCache>

    @Query("DELETE FROM offline_cache WHERE cache_key = :key")
    suspend fun deleteByKey(key: String)

    @Query("DELETE FROM offline_cache WHERE expire_time > 0 AND expire_time < :now")
    suspend fun deleteExpired(now: Long): Int

    @Query("DELETE FROM offline_cache")
    suspend fun clearAll()
}