package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.ExamRecord
import kotlinx.coroutines.flow.Flow

interface ExamRecordRepository {
    suspend fun insert(record: ExamRecord): Long
    suspend fun getById(id: Long): ExamRecord?
    fun getAllByUser(userId: Long): Flow<List<ExamRecord>>
    suspend fun getRecentExams(userId: Long, limit: Int): List<ExamRecord>
    fun getByType(userId: Long, type: Int): Flow<List<ExamRecord>>
    suspend fun getAverageScore(userId: Long): Double?
    suspend fun getHighestScore(userId: Long): Double?
    suspend fun getExamCount(userId: Long): Int
    suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<ExamRecord>
    suspend fun clearUserExams(userId: Long)
}