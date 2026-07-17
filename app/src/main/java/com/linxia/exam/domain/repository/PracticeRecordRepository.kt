package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.PracticeRecord
import kotlinx.coroutines.flow.Flow

interface PracticeRecordRepository {
    suspend fun insert(record: PracticeRecord): Long
    suspend fun insertAll(records: List<PracticeRecord>)
    fun getRecentRecords(userId: Long, limit: Int): Flow<List<PracticeRecord>>
    suspend fun getByQuestion(userId: Long, questionId: Long): List<PracticeRecord>
    suspend fun getByCategory(userId: Long, categoryId: Long): List<PracticeRecord>
    suspend fun getByMode(userId: Long, mode: Int): List<PracticeRecord>
    suspend fun getCountByMode(userId: Long, mode: Int): Int
    suspend fun getCorrectCountByMode(userId: Long, mode: Int): Int
    suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<PracticeRecord>
    suspend fun clearUserRecords(userId: Long)
    fun getRecentWithDetail(userId: Long, limit: Int): Flow<List<PracticeRecordRepository.PracticeRecordWithDetail>>

    data class PracticeRecordWithDetail(
        var record: PracticeRecord,
        var question: com.linxia.exam.data.db.entity.Question?
    )
}