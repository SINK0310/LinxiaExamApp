package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.QuestionDao
import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.data.db.entity.Question
import com.linxia.exam.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao
) : QuestionRepository {

    override suspend fun insertAll(questions: List<Question>) {
        questionDao.insertAll(questions)
    }

    override suspend fun insert(question: Question): Long {
        return questionDao.insert(question)
    }

    override suspend fun update(question: Question) {
        questionDao.update(question)
    }

    override suspend fun deleteById(id: Long) {
        questionDao.deleteById(id)
    }

    override suspend fun getById(id: Long): Question? {
        return questionDao.getById(id)
    }

    override fun getByCategory(categoryId: Long): Flow<List<Question>> {
        return questionDao.getByCategoryFlow(categoryId)
    }

    override suspend fun getByCategorySync(categoryId: Long): List<Question> {
        return questionDao.getByCategory(categoryId)
    }

    override suspend fun getRandomByCategory(categoryId: Long, limit: Int): List<Question> {
        return questionDao.getRandomByCategory(categoryId, limit)
    }

    override suspend fun getRandomByCategories(categoryIds: List<Long>, limit: Int): List<Question> {
        return questionDao.getRandomByCategories(categoryIds, limit)
    }

    override fun getCollectedQuestions(): Flow<List<Question>> {
        return questionDao.getCollectedQuestions()
    }

    override fun getWrongQuestions(): Flow<List<Question>> {
        return questionDao.getWrongQuestions()
    }

    override suspend fun searchQuestions(keyword: String): List<Question> {
        return questionDao.search(keyword)
    }

    override suspend fun getCountByCategory(categoryId: Long): Int {
        return questionDao.getCountByCategory(categoryId)
    }

    override suspend fun getTotalCount(): Int {
        return questionDao.getTotalCount()
    }

    override suspend fun getRandomQuestions(limit: Int): List<Question> {
        return questionDao.getRandomQuestions(limit)
    }

    override suspend fun getRandomByDifficulty(difficulty: Int, categoryId: Long, limit: Int): List<Question> {
        return questionDao.getRandomByDifficulty(categoryId, difficulty, limit)
    }

    override suspend fun setCollected(id: Long, collected: Int) {
        questionDao.setCollected(id, collected)
    }

    override suspend fun setWrong(id: Long, wrong: Int) {
        questionDao.setWrong(id, wrong)
    }

    override suspend fun clearAllFlags() {
        questionDao.clearAllFlags()
    }

    override data class QuestionWithCategory(
        var question: Question,
        var category: Category?
    )
}