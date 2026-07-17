package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linxia.exam.data.db.entity.MockExam
import kotlinx.coroutines.flow.Flow

@Dao
interface MockExamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exams: List<MockExam>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exam: MockExam): Long

    @Query("SELECT * FROM mock_exams ORDER BY id")
    fun getAll(): Flow<List<MockExam>>

    @Query("SELECT * FROM mock_exams WHERE id = :id")
    suspend fun getById(id: Long): MockExam?

    @Query("SELECT COUNT(*) FROM mock_exams")
    suspend fun count(): Int
}
