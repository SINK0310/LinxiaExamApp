package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.UserProgress
import kotlinx.coroutines.flow.Flow

interface UserProgressRepository {
    suspend fun insert(progress: UserProgress)
    suspend fun insertAll(progressList: List<UserProgress>)
    suspend fun update(progress: UserProgress)
    suspend fun getByCategory(userId: Long, categoryId: Long): UserProgress?
    fun getAllByUser(userId: Long): Flow<List<UserProgress>>
    suspend fun getTotalPracticed(userId: Long): Int
    suspend fun getTotalCorrect(userId: Long): Int
    suspend fun getTotalWrong(userId: Long): Int
    suspend fun getPracticedCategoriesCount(userId: Long): Int
    suspend fun clearUserProgress(userId: Long)
    fun getProgressWithCategory(userId: Long): Flow<List<UserProgressRepository.ProgressWithCategory>>

    data class ProgressWithCategory(
        var progress: UserProgress,
        var category: com.linxia.exam.data.db.entity.Category?
    )
}