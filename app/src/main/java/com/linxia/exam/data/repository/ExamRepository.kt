package com.linxia.exam.data.repository

import com.linxia.exam.data.db.entity.ExamRecord
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    suspend fun insertRecord(record: ExamRecord): Long
    fun getAllExams(userId: Long): Flow<List<ExamRecord>>
    suspend fun getRecentExams(userId: Long, limit: Int): List<ExamRecord>
    suspend fun getExamById(id: Long): ExamRecord?
    suspend fun getAverageScore(userId: Long): Double?
    suspend fun getHighestScore(userId: Long): Double?
    suspend fun getExamCount(userId: Long): Int
    suspend fun getExamsByTimeRange(userId: Long, startTime: Long, endTime: Long): List<ExamRecord>
}