package com.linxia.exam.data.repository

import com.linxia.exam.data.db.entity.PracticeRecord
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    suspend fun insertRecord(record: PracticeRecord)
    suspend fun insertRecords(records: List<PracticeRecord>)
    fun getRecentRecords(userId: Long, limit: Int): Flow<List<PracticeRecord>>
    suspend fun getRecordsByQuestion(userId: Long, questionId: Long): List<PracticeRecord>
    suspend fun getCountByMode(userId: Long, mode: Int): Int
    suspend fun getCorrectCountByMode(userId: Long, mode: Int): Int
    suspend fun getMostWrongQuestions(userId: Long, limit: Int): List<PracticeRecordDao.WrongQuestionStat>
    suspend fun getRecordsByTimeRange(userId: Long, startTime: Long, endTime: Long): List<PracticeRecord>
}