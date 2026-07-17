package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.OfflineCacheDao
import com.linxia.exam.data.db.entity.OfflineCache
import com.linxia.exam.domain.repository.OfflineCacheRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineCacheRepositoryImpl @Inject constructor(
    private val offlineCacheDao: OfflineCacheDao
) : OfflineCacheRepository {

    override suspend fun insert(cache: OfflineCache) {
        offlineCacheDao.insert(cache)
    }

    override suspend fun insertAll(caches: List<OfflineCache>) {
        offlineCacheDao.insertAll(caches)
    }

    override suspend fun getByKey(key: String): OfflineCache? {
        return offlineCacheDao.getByKey(key)
    }

    override suspend fun getValidByKey(key: String, currentTime: Long): OfflineCache? {
        val cache = offlineCacheDao.getByKey(key) ?: return null
        return if (cache.expireTime == 0L || cache.expireTime > currentTime) cache else null
    }

    override suspend fun deleteByKey(key: String) {
        offlineCacheDao.deleteByKey(key)
    }

    override suspend fun deleteExpired(currentTime: Long): Int {
        return offlineCacheDao.deleteExpired(currentTime)
    }

    override suspend fun clearAll() {
        offlineCacheDao.clearAll()
    }
}
