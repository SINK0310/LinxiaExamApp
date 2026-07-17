package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.linxia.exam.data.db.entity.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question): Long

    @Update
    suspend fun update(question: Question)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM questions WHERE category_id = :categoryId")
    suspend fun deleteByCategory(categoryId: Long)

    @Query("DELETE FROM questions")
    suspend fun deleteAll()

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: Long): Question?

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Long

    @Query("SELECT * FROM questions WHERE category_id = :categoryId ORDER BY id")
    suspend fun getByCategory(categoryId: Long): List<Question>

    @Query("SELECT * FROM questions WHERE category_id = :categoryId ORDER BY id")
    fun getByCategoryFlow(categoryId: Long): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE category_id = :categoryId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomByCategory(categoryId: Long, limit: Int): List<Question>

    @Query("SELECT * FROM questions WHERE question_type = :type AND category_id = :categoryId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomByCategoryAndType(categoryId: Long, type: Int, limit: Int): List<Question>

    @Query("SELECT * FROM questions WHERE is_collected = 1 ORDER BY updated_at DESC")
    fun getCollectedQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE is_wrong = 1 ORDER BY updated_at DESC")
    fun getWrongQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE category_id IN (:categoryIds) ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomByCategories(categoryIds: List<Long>, limit: Int): List<Question>

    @Query("SELECT * FROM questions WHERE category_id IN (:categoryIds) ORDER BY id")
    suspend fun getByCategories(categoryIds: List<Long>): List<Question>

    @Query("SELECT COUNT(*) FROM questions WHERE category_id = :categoryId")
    suspend fun getCountByCategory(categoryId: Long): Int

    @Query("SELECT COUNT(*) FROM questions WHERE category_id IN (:categoryIds)")
    suspend fun getCountByCategories(categoryIds: List<Long>): Int

    @Query("SELECT * FROM questions WHERE difficulty = :difficulty AND category_id = :categoryId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomByDifficulty(categoryId: Long, difficulty: Int, limit: Int): List<Question>

    @Query("SELECT * FROM questions WHERE content LIKE '%' || :keyword || '%' OR explanation LIKE '%' || :keyword || '%' LIMIT 50")
    suspend fun search(keyword: String): List<Question>

    @Transaction
    @Query("SELECT * FROM questions WHERE category_id = :categoryId ORDER BY id")
    fun getQuestionsWithCategory(categoryId: Long): Flow<List<QuestionWithCategory>>

    @Transaction
    @Query("SELECT * FROM questions WHERE id IN (:ids) ORDER BY id")
    suspend fun getByIdsWithCategory(ids: List<Long>): List<QuestionWithCategory>

    data class QuestionWithCategory(
        @Embedded var question: Question,
        @Relation(
            parentColumn = "category_id",
            entityColumn = "id",
            entity = com.linxia.exam.data.db.entity.Category::class
        )
        var category: com.linxia.exam.data.db.entity.Category?
    )
}