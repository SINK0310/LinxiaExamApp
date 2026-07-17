package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.linxia.exam.data.db.entity.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: Collection): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(collections: List<Collection>)

    @Query("SELECT * FROM collections WHERE user_id = :userId AND question_id = :questionId")
    suspend fun getByQuestion(userId: Long, questionId: Long): Collection?

    @Query("SELECT * FROM collections WHERE user_id = :userId ORDER BY collect_time DESC")
    fun getAll(userId: Long): Flow<List<Collection>>

    @Query("SELECT * FROM collections WHERE user_id = :userId AND category_id = :categoryId ORDER BY collect_time DESC")
    fun getByCategory(userId: Long, categoryId: Long): Flow<List<Collection>>

    @Query("SELECT COUNT(*) FROM collections WHERE user_id = :userId")
    suspend fun getCount(userId: Long): Int

    @Query("DELETE FROM collections WHERE user_id = :userId AND question_id = :questionId")
    suspend fun deleteByQuestion(userId: Long, questionId: Long)

    @Query("DELETE FROM collections WHERE user_id = :userId")
    suspend fun clearAll(userId: Long)

    @Transaction
    @Query("SELECT * FROM collections WHERE user_id = :userId ORDER BY collect_time DESC")
    fun getAllWithDetail(userId: Long): Flow<List<CollectionWithDetail>>

    data class CollectionWithDetail(
        @Embedded var collection: Collection,
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