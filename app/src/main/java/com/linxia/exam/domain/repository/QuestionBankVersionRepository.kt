package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.QuestionBankVersion
import kotlinx.coroutines.flow.Flow

interface QuestionBankVersionRepository {
    suspend fun insert(version: QuestionBankVersion)
    suspend fun update(version: QuestionBankVersion)
    suspend fun getActiveVersion(): QuestionBankVersion?
    suspend fun getLatestVersion(): QuestionBankVersion?
    suspend fun getByVersion(version: String): QuestionBankVersion?
    fun getAllVersions(): Flow<List<QuestionBankVersion>>
    suspend fun deleteByVersion(version: String)
}