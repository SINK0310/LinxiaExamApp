package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.linxia.exam.data.db.entity.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: UserProgress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progressList: List<UserProgress>)

    @Update
    suspend fun update(progress: UserProgress)

    @Query("SELECT * FROM user_progress WHERE user_id = :userId AND category_id = :categoryId")
    suspend fun getByCategory(userId: Long, categoryId: Long): UserProgress?

    @Query("SELECT * FROM user_progress WHERE user_id = :userId ORDER BY last_practice_time DESC")
    fun getAllByUser(userId: Long): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress WHERE user_id = :userId ORDER BY last_practice_time DESC")
    suspend fun getAllByUserSync(userId: Long): List<UserProgress>

    @Query("SELECT SUM(practiced_count) FROM user_progress WHERE user_id = :userId")
    suspend fun getTotalPracticed(userId: Long): Int

    @Query("SELECT SUM(correct_count) FROM user_progress WHERE user_id = :userId")
    suspend fun getTotalCorrect(userId: Long): Int

    @Query("SELECT SUM(wrong_count) FROM user_progress WHERE user_id = :userId")
    suspend fun getTotalWrong(userId: Long): Int

    @Query("SELECT COUNT(*) FROM user_progress WHERE user_id = :userId AND practiced_count > 0")
    suspend fun getPracticedCategoriesCount(userId: Long): Int

    @Query("DELETE FROM user_progress WHERE user_id = :userId")
    suspend fun clearAll(userId: Long)

    @Transaction
    @Query("SELECT * FROM user_progress WHERE user_id = :userId ORDER BY last_practice_time DESC")
    fun getProgressWithCategory(userId: Long): Flow<List<ProgressWithCategory>>

    data class ProgressWithCategory(
        @Embedded var progress: UserProgress,
        @Relation(
            parentColumn = "category_id",
            entityColumn = "id",
            entity = com.linxia.exam.data.db.entity.Category::class
        )
        var category: com.linxia.exam.data.db.entity.Category?
    )
}