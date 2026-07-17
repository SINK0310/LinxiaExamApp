package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.ExamRecordDao
import com.linxia.exam.data.db.entity.ExamRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExamRepositoryImpl @Inject constructor(
    private val examRecordDao: ExamRecordDao
) : ExamRepository {

    override suspend fun insertRecord(record: ExamRecord): Long {
        return examRecordDao.insert(record)
    }

    override fun getAllExams(userId: Long): Flow<List<ExamRecord>> {
        return examRecordDao.getAllByUser(userId)
    }

    override suspend fun getRecentExams(userId: Long, limit: Int): List<ExamRecord> {
        return examRecordDao.getRecentExams(userId, limit)
    }

    override suspend fun getExamById(id: Long): ExamRecord? {
        return examRecordDao.getById(id)
    }

    override suspend fun getAverageScore(userId: Long): Double? {
        return examRecordDao.getAverageScore(userId)
    }

    override suspend fun getHighestScore(userId: Long): Double? {
        return examRecordDao.getHighestScore(userId)
    }

    override suspend fun getExamCount(userId: Long): Int {
        return examRecordDao.getExamCount(userId)
    }

    override suspend fun getExamsByTimeRange(userId: Long, startTime: Long, endTime: Long): List<ExamRecord> {
        return examRecordDao.getByTimeRange(userId, startTime, endTime)
    }
}