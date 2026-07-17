package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.Collection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    suspend fun insert(collection: Collection): Long
    suspend fun insertAll(collections: List<Collection>)
    suspend fun getByQuestion(userId: Long, questionId: Long): Collection?
    fun getAll(userId: Long): Flow<List<Collection>>
    fun getByCategory(userId: Long, categoryId: Long): Flow<List<Collection>>
    suspend fun getCount(userId: Long): Int
    suspend fun deleteByQuestion(userId: Long, questionId: Long)
    suspend fun clearAll(userId: Long)
    fun getAllWithDetail(userId: Long): Flow<List<CollectionRepository.CollectionWithDetail>>

    data class CollectionWithDetail(
        var collection: Collection,
        var question: com.linxia.exam.data.db.entity.Question?,
        var category: com.linxia.exam.data.db.entity.Category?
    )
}