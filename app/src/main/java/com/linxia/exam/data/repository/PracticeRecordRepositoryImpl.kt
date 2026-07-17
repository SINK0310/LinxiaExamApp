package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.PracticeRecordDao
import com.linxia.exam.data.db.entity.PracticeRecord
import com.linxia.exam.domain.repository.PracticeRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeRecordRepositoryImpl @Inject constructor(
    private val practiceRecordDao: PracticeRecordDao
) : PracticeRecordRepository {

    override suspend fun insert(record: PracticeRecord): Long {
        return practiceRecordDao.insert(record)
    }

    override suspend fun insertAll(records: List<PracticeRecord>) {
        practiceRecordDao.insertAll(records)
    }

    override fun getRecentRecords(userId: Long, limit: Int): Flow<List<PracticeRecord>> {
        return practiceRecordDao.getRecent(userId, limit)
    }

    override suspend fun getByQuestion(userId: Long, questionId: Long): List<PracticeRecord> {
        return practiceRecordDao.getLatestByQuestion(userId, questionId)?.let { listOf(it) } ?: emptyList()
    }

    override suspend fun getByCategory(userId: Long, categoryId: Long): List<PracticeRecord> {
        return practiceRecordDao.getByCategory(userId, categoryId)
    }

    override suspend fun getByMode(userId: Long, mode: Int): List<PracticeRecord> {
        return practiceRecordDao.getByMode(userId, mode)
    }

    override suspend fun getCountByMode(userId: Long, mode: Int): Int {
        return practiceRecordDao.getCountByMode(userId, mode)
    }

    override suspend fun getCorrectCountByMode(userId: Long, mode: Int): Int {
        return practiceRecordDao.getCorrectCountByMode(userId, mode)
    }

    override suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<PracticeRecord> {
        return practiceRecordDao.getByTimeRange(userId, startTime, endTime)
    }

    override suspend fun clearUserRecords(userId: Long) {
        practiceRecordDao.clearAll(userId)
    }

    override suspend fun getMostWrongQuestions(userId: Long, limit: Int): List<PracticeRecordRepository.WrongQuestionStat> {
        return practiceRecordDao.getMostWrongQuestions(userId, limit).map { daoStat ->
            PracticeRecordRepository.WrongQuestionStat(
                questionId = daoStat.questionId,
                count = daoStat.count
            )
        }
    }

    override fun getRecentWithDetail(userId: Long, limit: Int): Flow<List<PracticeRecordRepository.PracticeRecordWithDetail>> {
        return practiceRecordDao.getRecentWithDetail(userId, limit).map { list ->
            list.map { daoDetail ->
                PracticeRecordRepository.PracticeRecordWithDetail(
                    record = daoDetail.record,
                    question = daoDetail.question
                )
            }
        }
    }
}
