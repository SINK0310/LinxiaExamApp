package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.CollectionDao
import com.linxia.exam.data.db.entity.Collection
import com.linxia.exam.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao
) : CollectionRepository {

    override suspend fun insert(collection: Collection): Long {
        return collectionDao.insert(collection)
    }

    override suspend fun insertAll(collections: List<Collection>) {
        collectionDao.insertAll(collections)
    }

    override suspend fun getByQuestion(userId: Long, questionId: Long): Collection? {
        return collectionDao.getByQuestion(userId, questionId)
    }

    override fun getAll(userId: Long): Flow<List<Collection>> {
        return collectionDao.getAll(userId)
    }

    override fun getByCategory(userId: Long, categoryId: Long): Flow<List<Collection>> {
        return collectionDao.getByCategory(userId, categoryId)
    }

    override suspend fun getCount(userId: Long): Int {
        return collectionDao.getCount(userId)
    }

    override suspend fun deleteByQuestion(userId: Long, questionId: Long) {
        collectionDao.deleteByQuestion(userId, questionId)
    }

    override suspend fun clearAll(userId: Long) {
        collectionDao.clearAll(userId)
    }

    override fun getAllWithDetail(userId: Long): Flow<List<CollectionRepository.CollectionWithDetail>> {
        return collectionDao.getAllWithDetail(userId).map { list ->
            list.map { daoDetail ->
                CollectionRepository.CollectionWithDetail(
                    collection = daoDetail.collection,
                    question = daoDetail.question,
                    category = daoDetail.category
                )
            }
        }
    }
}
