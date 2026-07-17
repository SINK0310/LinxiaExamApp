package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    suspend fun insert(bookmark: Bookmark): Long
    suspend fun insertAll(bookmarks: List<Bookmark>)
    suspend fun getByQuestion(userId: Long, questionId: Long): Bookmark?
    fun getAll(userId: Long): Flow<List<Bookmark>>
    fun getByCategory(userId: Long, categoryId: Long): Flow<List<Bookmark>>
    suspend fun getCount(userId: Long): Int
    suspend fun deleteByQuestion(userId: Long, questionId: Long)
    suspend fun clearAll(userId: Long)
    fun getAllWithDetail(userId: Long): Flow<List<BookmarkRepository.BookmarkWithDetail>>

    data class BookmarkWithDetail(
        var bookmark: Bookmark,
        var question: com.linxia.exam.data.db.entity.Question?,
        var category: com.linxia.exam.data.db.entity.Category?
    )
}