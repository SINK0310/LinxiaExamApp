package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey

@Entity(
    tableName = "practice_questions",
    indices = [Index(value = ["exercise_id"])],
    foreignKeys = [
        ForeignKey(
            entity = PracticeExercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PracticeQuestion(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "exercise_id") var exerciseId: Long = 0,
    @ColumnInfo(name = "question_type") var questionType: Int = 1,
    var content: String = "",
    @ColumnInfo(name = "option_a") var optionA: String = "",
    @ColumnInfo(name = "option_b") var optionB: String = "",
    @ColumnInfo(name = "option_c") var optionC: String = "",
    @ColumnInfo(name = "option_d") var optionD: String = "",
    @ColumnInfo(name = "correct_answer") var correctAnswer: String = "",
    var explanation: String = "",
    @ColumnInfo(name = "sort_order") var sortOrder: Int = 0
)
