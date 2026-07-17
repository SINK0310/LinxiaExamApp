package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.WrongQuestion
import kotlinx.coroutines.flow.Flow

interface WrongQuestionRepository {
    suspend fun insert(wrongQuestion: WrongQuestion): Long
    suspend fun insertAll(wrongQuestions: List<WrongQuestion>)
    suspend fun update(wrongQuestion: WrongQuestion)
    suspend fun getByQuestion(userId: Long, questionId: Long): WrongQuestion?
    fun getAll(userId: Long): Flow<List<WrongQuestion>>
    fun getByStatus(userId: Long, status: Int): Flow<List<WrongQuestion>>
    fun getByCategory(userId: Long, categoryId: Long): Flow<List<WrongQuestion>>
    suspend fun getTotalCount(userId: Long): Int
    suspend fun getMasteredCount(userId: Long): Int
    suspend fun getUnmasteredCount(userId: Long): Int
    suspend fun deleteByQuestion(userId: Long, questionId: Long)
    suspend fun clearMastered(userId: Long)
    suspend fun clearAll(userId: Long)
    fun getAllWithDetail(userId: Long): Flow<List<WrongQuestionRepository.WrongQuestionWithDetail>>

    data class WrongQuestionWithDetail(
        var wrongQuestion: WrongQuestion,
        var question: com.linxia.exam.data.db.entity.Question?,
        var category: com.linxia.exam.data.db.entity.Category?
    )
}