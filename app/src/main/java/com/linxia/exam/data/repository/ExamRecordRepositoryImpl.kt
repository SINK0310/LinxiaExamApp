package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.ExamRecordDao
import com.linxia.exam.data.db.entity.ExamRecord
import com.linxia.exam.domain.repository.ExamRecordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRecordRepositoryImpl @Inject constructor(
    private val examRecordDao: ExamRecordDao
) : ExamRecordRepository {

    override suspend fun insert(record: ExamRecord): Long {
        return examRecordDao.insert(record)
    }

    override suspend fun getById(id: Long): ExamRecord? {
        return examRecordDao.getById(id)
    }

    override fun getAllByUser(userId: Long): Flow<List<ExamRecord>> {
        return examRecordDao.getAll(userId)
    }

    override suspend fun getRecentExams(userId: Long, limit: Int): List<ExamRecord> {
        return examRecordDao.getRecent(userId, limit)
    }

    override fun getByType(userId: Long, type: Int): Flow<List<ExamRecord>> {
        return examRecordDao.getByType(userId, type)
    }

    override suspend fun getAverageScore(userId: Long): Double? {
        return examRecordDao.getAverageScore(userId)
    }

    override suspend fun getHighestScore(userId: Long): Double? {
        return examRecordDao.getHighestScore(userId)
    }

    override suspend fun getExamCount(userId: Long): Int {
        return examRecordDao.getTotalCount(userId)
    }

    override suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<ExamRecord> {
        return examRecordDao.getByTimeRange(userId, startTime, endTime)
    }

    override suspend fun clearUserExams(userId: Long) {
        examRecordDao.clearAll(userId)
    }
}
