package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.QuestionBankVersionDao
import com.linxia.exam.data.db.entity.QuestionBankVersion
import com.linxia.exam.domain.repository.QuestionBankVersionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionBankVersionRepositoryImpl @Inject constructor(
    private val questionBankVersionDao: QuestionBankVersionDao
) : QuestionBankVersionRepository {

    override suspend fun insert(version: QuestionBankVersion) {
        questionBankVersionDao.insert(version)
    }

    override suspend fun update(version: QuestionBankVersion) {
        questionBankVersionDao.update(version)
    }

    override suspend fun getActiveVersion(): QuestionBankVersion? {
        return questionBankVersionDao.getActiveVersion()
    }

    override suspend fun getLatestVersion(): QuestionBankVersion? {
        return questionBankVersionDao.getLatestVersion()
    }

    override suspend fun getByVersion(version: String): QuestionBankVersion? {
        return questionBankVersionDao.getByVersion(version)
    }

    override fun getAllVersions(): Flow<List<QuestionBankVersion>> {
        return questionBankVersionDao.getAllVersions()
    }

    override suspend fun deleteByVersion(version: String) {
        questionBankVersionDao.deleteByVersion(version)
    }
}
