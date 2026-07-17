package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.data.db.entity.Question
import com.linxia.exam.data.db.entity.UserProgress
import com.linxia.exam.data.db.entity.PracticeRecord
import com.linxia.exam.data.db.entity.ExamRecord
import com.linxia.exam.data.db.entity.WrongQuestion
import com.linxia.exam.data.db.entity.Collection
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getRootCategories(): List<Category>
    fun observeRootCategories(): Flow<List<Category>>
    suspend fun getChildren(parentId: Long): List<Category>
    fun observeChildren(parentId: Long): Flow<List<Category>>
    suspend fun getCategoryTree(): List<CategoryTreeNode>
    fun observeCategoryTree(): Flow<List<CategoryTreeNode>>
    suspend fun searchCategories(keyword: String): List<Category>
    suspend fun updateQuestionCount(categoryId: Long)
    suspend fun updateAllQuestionCounts()

    data class CategoryTreeNode(
        val category: Category,
        val children: List<CategoryTreeNode> = emptyList()
    )
}

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

interface UserProgressRepository {
    fun observeProgress(userId: Long): Flow<List<UserProgress>>
    suspend fun getProgressByCategory(userId: Long, categoryId: Long): UserProgress?
    suspend fun updateProgress(progress: UserProgress)
    suspend fun getTotalPracticed(userId: Long): Int
    suspend fun getTotalCorrect(userId: Long): Int
    fun observeProgressWithCategory(userId: Long): Flow<List<ProgressWithCategory>>

    data class ProgressWithCategory(
        val progress: UserProgress,
        val category: Category?
    )
}

interface PracticeRepository {
    suspend fun insertRecord(record: PracticeRecord)
    suspend fun insertRecords(records: List<PracticeRecord>)
    fun getRecentRecords(userId: Long, limit: Int): Flow<List<PracticeRecord>>
    suspend fun getRecordsByQuestion(userId: Long, questionId: Long): List<PracticeRecord>
    suspend fun getCountByMode(userId: Long, mode: Int): Int
    suspend fun getCorrectCountByMode(userId: Long, mode: Int): Int
    suspend fun getMostWrongQuestions(userId: Long, limit: Int): List<WrongQuestionStat>
    suspend fun getRecordsByTimeRange(userId: Long, startTime: Long, endTime: Long): List<PracticeRecord>

    data class WrongQuestionStat(
        val questionId: Long,
        val count: Int
    )
}

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
    fun getWrongQuestionsWithDetail(userId: Long): Flow<List<WrongQuestionWithDetail>>

    data class WrongQuestionWithDetail(
        val wrongQuestion: WrongQuestion,
        val question: Question?,
        val category: Category?
    )
}

interface CollectionRepository {
    suspend fun addCollection(collection: Collection)
    suspend fun removeCollection(userId: Long, questionId: Long)
    fun getAllCollections(userId: Long): Flow<List<Collection>>
    fun getCollectionsByCategory(userId: Long, categoryId: Long): Flow<List<Collection>>
    suspend fun getCollectionCount(userId: Long): Int
    suspend fun isCollected(userId: Long, questionId: Long): Boolean
    fun getCollectionsWithDetail(userId: Long): Flow<List<CollectionWithDetail>>

    data class CollectionWithDetail(
        val collection: Collection,
        val question: Question?,
        val category: Category?
    )
}