package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.WrongQuestionDao
import com.linxia.exam.data.db.entity.WrongQuestion
import kotlinx.coroutines.flow.Flow

class WrongQuestionRepositoryImpl(
    private val wrongQuestionDao: WrongQuestionDao
) : WrongQuestionRepository {
    override suspend fun insert(wrongQuestion: WrongQuestion): Long {
        return wrongQuestionDao.insert(wrongQuestion)
    }

    override suspend fun insertAll(wrongQuestions: List<WrongQuestion>) {
        wrongQuestionDao.insertAll(wrongQuestions)
    }

    override suspend fun update(wrongQuestion: WrongQuestion) {
        wrongQuestionDao.update(wrongQuestion)
    }

    override suspend fun getByQuestion(userId: Long, questionId: Long): WrongQuestion? {
        return wrongQuestionDao.getByQuestion(userId, questionId)
    }

    override fun getAll(userId: Long): Flow<List<WrongQuestion>> {
        return wrongQuestionDao.getAll(userId)
    }

    override fun getByStatus(userId: Long, status: Int): Flow<List<WrongQuestion>> {
        return wrongQuestionDao.getByStatus(userId, status)
    }

    override fun getByCategory(userId: Long, categoryId: Long): Flow<List<WrongQuestion>> {
        return wrongQuestionDao.getByCategory(userId, categoryId)
    }

    override suspend fun getTotalCount(userId: Long): Int {
        return wrongQuestionDao.getTotalCount(userId)
    }

    override suspend fun getMasteredCount(userId: Long): Int {
        return wrongQuestionDao.getMasteredCount(userId)
    }

    override suspend fun getUnmasteredCount(userId: Long): Int {
        return wrongQuestionDao.getUnmasteredCount(userId)
    }

    override suspend fun deleteByQuestion(userId: Long, questionId: Long) {
        wrongQuestionDao.deleteByQuestion(userId, questionId)
    }

    override suspend fun clearMastered(userId: Long) {
        wrongQuestionDao.clearMastered(userId)
    }

    override suspend fun clearAll(userId: Long) {
        wrongQuestionDao.clearAll(userId)
    }

    override fun getAllWithDetail(userId: Long): Flow<List<WrongQuestionDao.WrongQuestionWithDetail>> {
        return wrongQuestionDao.getAllWithDetail(userId)
    }
}