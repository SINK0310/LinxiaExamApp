package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.BookmarkDao
import com.linxia.exam.data.db.entity.Bookmark
import com.linxia.exam.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override suspend fun insert(bookmark: Bookmark): Long {
        return bookmarkDao.insert(bookmark)
    }

    override suspend fun insertAll(bookmarks: List<Bookmark>) {
        bookmarkDao.insertAll(bookmarks)
    }

    override suspend fun getByQuestion(userId: Long, questionId: Long): Bookmark? {
        return bookmarkDao.getByQuestion(userId, questionId)
    }

    override fun getAll(userId: Long): Flow<List<Bookmark>> {
        return bookmarkDao.getAll(userId)
    }

    override fun getByCategory(userId: Long, categoryId: Long): Flow<List<Bookmark>> {
        return bookmarkDao.getByCategory(userId, categoryId)
    }

    override suspend fun getCount(userId: Long): Int {
        return bookmarkDao.getCount(userId)
    }

    override suspend fun deleteByQuestion(userId: Long, questionId: Long) {
        bookmarkDao.deleteByQuestion(userId, questionId)
    }

    override suspend fun clearAll(userId: Long) {
        bookmarkDao.clearAll(userId)
    }

    override fun getAllWithDetail(userId: Long): Flow<List<BookmarkRepository.BookmarkWithDetail>> {
        return bookmarkDao.getAllWithDetail(userId).map { list ->
            list.map { daoDetail ->
                BookmarkRepository.BookmarkWithDetail(
                    bookmark = daoDetail.bookmark,
                    question = daoDetail.question,
                    category = daoDetail.category
                )
            }
        }
    }
}
