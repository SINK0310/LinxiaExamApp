package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.UserProgressDao
import com.linxia.exam.data.db.entity.UserProgress
import com.linxia.exam.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProgressRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao
) : UserProgressRepository {

    override suspend fun insert(progress: UserProgress) {
        userProgressDao.insert(progress)
    }

    override suspend fun insertAll(progressList: List<UserProgress>) {
        userProgressDao.insertAll(progressList)
    }

    override suspend fun update(progress: UserProgress) {
        userProgressDao.update(progress)
    }

    override suspend fun getByCategory(userId: Long, categoryId: Long): UserProgress? {
        return userProgressDao.getByCategory(userId, categoryId)
    }

    override fun getAllByUser(userId: Long): Flow<List<UserProgress>> {
        return userProgressDao.getAllByUser(userId)
    }

    override suspend fun getTotalPracticed(userId: Long): Int {
        return userProgressDao.getTotalPracticed(userId)
    }

    override suspend fun getTotalCorrect(userId: Long): Int {
        return userProgressDao.getTotalCorrect(userId)
    }

    override suspend fun getTotalWrong(userId: Long): Int {
        return userProgressDao.getTotalWrong(userId)
    }

    override suspend fun getPracticedCategoriesCount(userId: Long): Int {
        return userProgressDao.getPracticedCategoriesCount(userId)
    }

    override suspend fun clearUserProgress(userId: Long) {
        userProgressDao.clearAll(userId)
    }

    override fun getProgressWithCategory(userId: Long): Flow<List<UserProgressRepository.ProgressWithCategory>> {
        return userProgressDao.getProgressWithCategory(userId).map { list ->
            list.map { daoDetail ->
                UserProgressRepository.ProgressWithCategory(
                    progress = daoDetail.progress,
                    category = daoDetail.category
                )
            }
        }
    }
}
