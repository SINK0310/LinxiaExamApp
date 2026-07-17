package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.linxia.exam.data.db.entity.PracticeExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<PracticeExercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: PracticeExercise): Long

    @Query("SELECT * FROM practice_exercises ORDER BY total_questions DESC")
    fun getAll(): Flow<List<PracticeExercise>>

    @Query("SELECT * FROM practice_exercises WHERE topic = :topic")
    fun getByTopic(topic: String): Flow<List<PracticeExercise>>

    @Query("SELECT * FROM practice_exercises WHERE id = :id")
    suspend fun getById(id: Long): PracticeExercise?

    @Query("SELECT DISTINCT topic FROM practice_exercises ORDER BY topic")
    fun getTopics(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM practice_exercises")
    suspend fun count(): Int
}
