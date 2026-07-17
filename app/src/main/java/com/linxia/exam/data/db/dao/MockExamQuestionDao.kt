package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linxia.exam.data.db.entity.MockExamQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface MockExamQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<MockExamQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: MockExamQuestion): Long

    @Query("SELECT * FROM mock_exam_questions WHERE exam_id = :examId ORDER BY sort_order")
    fun getByExamId(examId: Long): Flow<List<MockExamQuestion>>

    @Query("SELECT * FROM mock_exam_questions WHERE id = :id")
    suspend fun getById(id: Long): MockExamQuestion?

    @Query("DELETE FROM mock_exam_questions WHERE exam_id = :examId")
    suspend fun deleteByExamId(examId: Long)
}
