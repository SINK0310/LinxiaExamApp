package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linxia.exam.data.db.entity.PracticeQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<PracticeQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: PracticeQuestion): Long

    @Query("SELECT * FROM practice_questions WHERE exercise_id = :exerciseId ORDER BY sort_order")
    fun getByExerciseId(exerciseId: Long): Flow<List<PracticeQuestion>>

    @Query("SELECT * FROM practice_questions WHERE id = :id")
    suspend fun getById(id: Long): PracticeQuestion?

    @Query("DELETE FROM practice_questions WHERE exercise_id = :exerciseId")
    suspend fun deleteByExerciseId(exerciseId: Long)
}
