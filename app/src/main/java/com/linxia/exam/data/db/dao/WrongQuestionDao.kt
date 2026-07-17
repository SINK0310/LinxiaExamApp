package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.linxia.exam.data.db.entity.WrongQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface WrongQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wrongQuestion: WrongQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(wrongQuestions: List<WrongQuestion>)

    @Update
    suspend fun update(wrongQuestion: WrongQuestion)

    @Query("SELECT * FROM wrong_questions WHERE user_id = :userId AND question_id = :questionId")
    suspend fun getByQuestion(userId: Long, questionId: Long): WrongQuestion?

    @Query("SELECT * FROM wrong_questions WHERE user_id = :userId ORDER BY last_wrong_time DESC")
    fun getAll(userId: Long): Flow<List<WrongQuestion>>

    @Query("SELECT * FROM wrong_questions WHERE user_id = :userId AND mastery_status = :status ORDER BY last_wrong_time DESC")
    fun getByStatus(userId: Long, status: Int): Flow<List<WrongQuestion>>

    @Query("SELECT * FROM wrong_questions WHERE user_id = :userId AND category_id = :categoryId ORDER BY last_wrong_time DESC")
    fun getByCategory(userId: Long, categoryId: Long): Flow<List<WrongQuestion>>

    @Query("SELECT COUNT(*) FROM wrong_questions WHERE user_id = :userId")
    suspend fun getTotalCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM wrong_questions WHERE user_id = :userId AND mastery_status = 2")
    suspend fun getMasteredCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM wrong_questions WHERE user_id = :userId AND mastery_status = 0")
    suspend fun getUnmasteredCount(userId: Long): Int

    @Query("DELETE FROM wrong_questions WHERE user_id = :userId AND question_id = :questionId")
    suspend fun deleteByQuestion(userId: Long, questionId: Long)

    @Query("DELETE FROM wrong_questions WHERE user_id = :userId AND mastery_status = 2")
    suspend fun clearMastered(userId: Long)

    @Query("DELETE FROM wrong_questions WHERE user_id = :userId")
    suspend fun clearAll(userId: Long)

    @Transaction
    @Query("SELECT * FROM wrong_questions WHERE user_id = :userId ORDER BY last_wrong_time DESC")
    fun getAllWithDetail(userId: Long): Flow<List<WrongQuestionWithDetail>>

    data class WrongQuestionWithDetail(
        @Embedded var wrongQuestion: WrongQuestion,
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