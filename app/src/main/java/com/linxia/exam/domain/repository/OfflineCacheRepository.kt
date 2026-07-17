package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.OfflineCache

interface OfflineCacheRepository {
    suspend fun insert(cache: OfflineCache)
    suspend fun insertAll(caches: List<OfflineCache>)
    suspend fun getByKey(key: String): OfflineCache?
    suspend fun getValidByKey(key: String, currentTime: Long): OfflineCache?
    suspend fun deleteByKey(key: String)
    suspend fun deleteExpired(currentTime: Long): Int
    suspend fun clearAll()
}