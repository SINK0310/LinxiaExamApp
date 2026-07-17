package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "practice_records",
    indices = [
        Index(value = ["user_id", "practice_time"]),
        Index(value = ["question_id"]),
        Index(value = ["category_id"]),
        Index(value = ["practice_mode"])
    ]
)
data class PracticeRecord(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "user_id") var userId: Long = 1,
    @ColumnInfo(name = "question_id") var questionId: Long = 0,
    @ColumnInfo(name = "category_id") var categoryId: Long = 0,
    @ColumnInfo(name = "practice_mode") var practiceMode: Int = 1,
    @ColumnInfo(name = "user_answer") var userAnswer: String = "",
    @ColumnInfo(name = "is_correct") var isCorrect: Int = 0,
    @ColumnInfo(name = "time_spent") var timeSpent: Int = 0,
    @ColumnInfo(name = "practice_time") var practiceTime: Long = System.currentTimeMillis()
) {
    companion object {
        const val MODE_CHAPTER_PRACTICE = 1
        const val MODE_MOCK_EXAM = 2
        const val MODE_WRONG_REVIEW = 3
        const val MODE_COLLECTION_PRACTICE = 4
    }
}