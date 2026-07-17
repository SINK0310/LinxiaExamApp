package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.linxia.exam.data.db.entity.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookmarks: List<Bookmark>)

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId AND question_id = :questionId")
    suspend fun getByQuestion(userId: Long, questionId: Long): Bookmark?

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId ORDER BY collect_time DESC")
    fun getAll(userId: Long): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId AND category_id = :categoryId ORDER BY collect_time DESC")
    fun getByCategory(userId: Long, categoryId: Long): Flow<List<Bookmark>>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE user_id = :userId")
    suspend fun getCount(userId: Long): Int

    @Query("DELETE FROM bookmarks WHERE user_id = :userId AND question_id = :questionId")
    suspend fun deleteByQuestion(userId: Long, questionId: Long)

    @Query("DELETE FROM bookmarks WHERE user_id = :userId")
    suspend fun clearAll(userId: Long)

    @Transaction
    @Query("SELECT * FROM bookmarks WHERE user_id = :userId ORDER BY collect_time DESC")
    fun getAllWithDetail(userId: Long): Flow<List<BookmarkWithDetail>>

    data class BookmarkWithDetail(
        @Embedded var bookmark: Bookmark,
        @Relation(
            parentColumn = "question_id",
            entityColumn = "id",
            entity = com.linxia.exam.data.db.entity.Question::class
        )
        var question: com.linxia.exam.data.db.entity.Question?,
        @Relation(
            parentColumn = "category_id",
            entityColumn = "id",
            entity = com.linxia.exam.data.db.entity.Category::class
        )
        var category: com.linxia.exam.data.db.entity.Category?
    )
}