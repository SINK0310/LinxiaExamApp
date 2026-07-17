package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.UserSettings

interface UserSettingsRepository {
    suspend fun insert(settings: UserSettings)
    suspend fun insertAll(settingsList: List<UserSettings>)
    suspend fun getByKey(userId: Long, key: String): UserSettings?
    suspend fun getAll(userId: Long): List<UserSettings>
    suspend fun deleteByKey(userId: Long, key: String)
    suspend fun clearAll(userId: Long)
}