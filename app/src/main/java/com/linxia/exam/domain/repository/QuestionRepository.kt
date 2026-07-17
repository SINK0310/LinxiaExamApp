package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun insertAll(questions: List<Question>)
    suspend fun insert(question: Question): Long
    suspend fun update(question: Question)
    suspend fun deleteById(id: Long)
    suspend fun getById(id: Long): Question?
    fun getByCategory(categoryId: Long): Flow<List<Question>>
    suspend fun getByCategorySync(categoryId: Long): List<Question>
    suspend fun getRandomByCategory(categoryId: Long, limit: Int): List<Question>
    suspend fun getRandomByCategories(categoryIds: List<Long>, limit: Int): List<Question>
    fun getCollectedQuestions(): Flow<List<Question>>
    fun getWrongQuestions(): Flow<List<Question>>
    suspend fun searchQuestions(keyword: String): List<Question>
    suspend fun getCountByCategory(categoryId: Long): Int
    suspend fun getTotalCount(): Int
    suspend fun getRandomQuestions(limit: Int): List<Question>
    suspend fun getRandomByDifficulty(difficulty: Int, categoryId: Long, limit: Int): List<Question>
    suspend fun setCollected(id: Long, collected: Int)
    suspend fun setWrong(id: Long, wrong: Int)
    suspend fun clearAllFlags()

    data class QuestionWithCategory(
        var question: Question,
        var category: com.linxia.exam.data.db.entity.Category?
    )
}