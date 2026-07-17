package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.PracticeRecordDao
import com.linxia.exam.data.db.entity.PracticeRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PracticeRepositoryImpl @Inject constructor(
    private val practiceRecordDao: PracticeRecordDao
) : PracticeRepository {

    override suspend fun insertRecord(record: PracticeRecord) {
        practiceRecordDao.insert(record)
    }

    override suspend fun insertRecords(records: List<PracticeRecord>) {
        practiceRecordDao.insertAll(records)
    }

    override fun getRecentRecords(userId: Long, limit: Int): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getRecentRecords(userId, limit)
    }

    override suspend fun getRecordsByQuestion(userId: Long, questionId: Long): List<PracticeRecord> {
        return practiceRecordDao.getByQuestion(userId, questionId)
    }

    override suspend fun getCountByMode(userId: Long, mode: Int): Int {
        return practiceRecordDao.getCountByMode(userId, mode)
    }

    override suspend fun getCorrectCountByMode(userId: Long, mode: Int): Int {
        return practiceRecordDao.getCorrectCountByMode(userId, mode)
    }

    override suspend fun getMostWrongQuestions(userId: Long, limit: Int): List<PracticeRecordDao.WrongQuestionStat> {
        return practiceRecordDao.getMostWrongQuestions(userId, limit)
    }

    override suspend fun getRecordsByTimeRange(userId: Long, startTime: Long, endTime: Long): List<PracticeRecord> {
        return practiceRecordDao.getByTimeRange(userId, startTime, endTime)
    }
}