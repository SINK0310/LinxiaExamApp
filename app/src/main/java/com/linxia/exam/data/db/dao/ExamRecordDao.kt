package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linxia.exam.data.db.entity.ExamRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ExamRecord): Long

    @Query("SELECT * FROM exam_records WHERE id = :id")
    suspend fun getById(id: Long): ExamRecord?

    @Query("SELECT * FROM exam_records WHERE user_id = :userId ORDER BY end_time DESC")
    fun getAll(userId: Long): Flow<List<ExamRecord>>

    @Query("SELECT * FROM exam_records WHERE user_id = :userId ORDER BY end_time DESC LIMIT :limit")
    suspend fun getRecent(userId: Long, limit: Int): List<ExamRecord>

    @Query("SELECT * FROM exam_records WHERE user_id = :userId AND exam_type = :type ORDER BY end_time DESC")
    fun getByType(userId: Long, type: Int): Flow<List<ExamRecord>>

    @Query("SELECT AVG(score) FROM exam_records WHERE user_id = :userId")
    suspend fun getAverageScore(userId: Long): Double?

    @Query("SELECT MAX(score) FROM exam_records WHERE user_id = :userId")
    suspend fun getHighestScore(userId: Long): Double?

    @Query("SELECT COUNT(*) FROM exam_records WHERE user_id = :userId")
    suspend fun getTotalCount(userId: Long): Int

    @Query("SELECT * FROM exam_records WHERE user_id = :userId AND end_time >= :startTime AND end_time <= :endTime ORDER BY end_time DESC")
    suspend fun getByTimeRange(userId: Long, startTime: Long, endTime: Long): List<ExamRecord>

    @Query("DELETE FROM exam_records WHERE user_id = :userId")
    suspend fun clearAll(userId: Long)
}