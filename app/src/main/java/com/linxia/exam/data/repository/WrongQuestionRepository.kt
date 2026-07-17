package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.WrongQuestionDao
import com.linxia.exam.data.db.entity.Question
import com.linxia.exam.data.db.entity.WrongQuestion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface WrongQuestionRepository {
    suspend fun addWrongQuestion(wrongQuestion: WrongQuestion)
    suspend fun updateWrongQuestion(wrongQuestion: WrongQuestion)
    fun getAllWrongQuestions(userId: Long): Flow<List<WrongQuestion>>
    fun getWrongQuestionsByStatus(userId: Long, status: Int): Flow<List<WrongQuestion>>
    fun getWrongQuestionsByCategory(userId: Long, categoryId: Long): Flow<List<WrongQuestion>>
    suspend fun getWrongQuestionCount(userId: Long): Int
    suspend fun getUnmasteredCount(userId: Long): Int
    suspend fun getMasteredCount(userId: Long): Int
    suspend fun removeWrongQuestion(userId: Long, questionId: Long)
    suspend fun clearMastered(userId: Long)
    suspend fun clearAll(userId: Long)
    fun getWrongQuestionsWithDetail(userId: Long): Flow<List<WrongQuestionDao.WrongQuestionWithDetail>>
}