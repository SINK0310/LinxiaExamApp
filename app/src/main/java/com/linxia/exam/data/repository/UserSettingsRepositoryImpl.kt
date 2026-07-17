package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.UserSettingsDao
import com.linxia.exam.data.db.entity.UserSettings
import com.linxia.exam.domain.repository.UserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) : UserSettingsRepository {

    override suspend fun insert(settings: UserSettings) {
        userSettingsDao.insert(settings)
    }

    override suspend fun insertAll(settingsList: List<UserSettings>) {
        userSettingsDao.insertAll(settingsList)
    }

    override suspend fun getByKey(userId: Long, key: String): UserSettings? {
        return userSettingsDao.getByKey(userId, key)
    }

    override suspend fun getAll(userId: Long): List<UserSettings> {
        return userSettingsDao.getAll(userId)
    }

    override suspend fun deleteByKey(userId: Long, key: String) {
        userSettingsDao.deleteByKey(userId, key)
    }

    override suspend fun clearAll(userId: Long) {
        userSettingsDao.clearAll(userId)
    }
}
