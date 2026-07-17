package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.linxia.exam.data.db.entity.PracticeRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PracticeRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<PracticeRecord>)

    @Query("SELECT * FROM practice_records WHERE id = :id")
    suspend fun getById(id: Long): PracticeRecord?

    @Query("SELECT * FROM practice_records WHERE user_id = :userId AND question_id = :questionId ORDER BY practice_time DESC LIMIT 1")
    suspend fun getLatestByQuestion(userId: Long, questionId: Long): PracticeRecord?

    @Query("SELECT * FROM practice_records WHERE user_id = :userId AND category_id = :categoryId ORDER BY practice_time DESC")
    suspend fun getByCategory(userId: Long, categoryId: Long): List<PracticeRecord>

    @Query("SELECT * FROM practice_records WHERE user_id = :userId AND practice_mode = :mode ORDER BY practice_time DESC")
    suspend fun getByMode(userId: Long, mode: Int): List<PracticeRecord>

    @Query("SELECT * FROM practice_records WHERE user_id = :userId ORDER BY practice_time DESC LIMIT :limit")
    suspend fun getRecent(userId: Long, limit: Int): List<PracticeRecord>

    @Query("SELECT COUNT(*) FROM practice_records WHERE user_id = :userId AND practice_mode = :mode")
    suspend fun getCountByMode(userId: Long, mode: Int): Int

    @Query("SELECT COUNT(*) FROM practice_records WHERE user_id = :userId AND is_correct = 1 AND practice_mode = :mode")
    suspend fun getCorrectCountByMode(userId: Long, mode: Int): Int

    @Query("SELECT * FROM practice_records WHERE user_id = :userId AND practice_time >= :startTime AND practice_time <= :endTime ORDER BY practice_time DESC")
    suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<PracticeRecord>

    @Query("DELETE FROM practice_records WHERE user_id = :userId")
    suspend fun clearAll(userId: Long)

    @Query("DELETE FROM practice_records WHERE user_id = :userId AND practice_time < :beforeTime")
    suspend fun deleteBefore(userId: Long, beforeTime: Long)

    @Query("SELECT question_id AS questionId, COUNT(*) AS `count` FROM practice_records WHERE user_id = :userId AND is_correct = 0 GROUP BY question_id ORDER BY `count` DESC LIMIT :limit")
    suspend fun getMostWrongQuestions(userId: Long, limit: Int): List<WrongQuestionStat>

    @Transaction
    @Query("SELECT * FROM practice_records WHERE user_id = :userId ORDER BY practice_time DESC LIMIT :limit")
    fun getRecentWithDetail(userId: Long, limit: Int): Flow<List<PracticeRecordWithDetail>>

    data class WrongQuestionStat(
        var questionId: Long,
        var count: Int
    )

    data class PracticeRecordWithDetail(
        @Embedded var record: PracticeRecord,
        @Relation(
            parentColumn = "question_id",
            entityColumn = "id",
            entity = com.linxia.exam.data.db.entity.Question::class
        )
        var question: com.linxia.exam.data.db.entity.Question?
    )
}