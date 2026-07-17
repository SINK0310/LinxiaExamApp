package com.linxia.exam.data.repository

import com.linxia.exam.data.db.entity.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun getQuestionsByCategory(categoryId: Long): List<Question>
    fun observeQuestionsByCategory(categoryId: Long): Flow<List<Question>>
    suspend fun getRandomQuestionsByCategory(categoryId: Long, count: Int): List<Question>
    suspend fun getRandomQuestionsByCategories(categoryIds: List<Long>, count: Int): List<Question>
    suspend fun getCollectedQuestions(): List<Question>
    fun observeCollectedQuestions(): Flow<List<Question>>
    suspend fun getWrongQuestions(): List<Question>
    fun observeWrongQuestions(): Flow<List<Question>>
    suspend fun searchQuestions(keyword: String): List<Question>
    suspend fun getQuestionById(id: Long): Question?
    suspend fun toggleCollected(questionId: Long, collected: Boolean)
    suspend fun toggleWrong(questionId: Long, wrong: Boolean)
    suspend fun insertQuestions(questions: List<Question>)
    suspend fun getTotalCount(): Int
    suspend fun getCountByCategory(categoryId: Long): Int
}