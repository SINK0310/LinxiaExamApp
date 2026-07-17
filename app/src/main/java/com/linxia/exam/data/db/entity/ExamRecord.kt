package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "exam_records",
    indices = [
        Index(value = ["user_id", "end_time"]),
        Index(value = ["exam_type"])
    ]
)
data class ExamRecord(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "user_id") var userId: Long = 1,
    @ColumnInfo(name = "exam_type") var examType: Int = 1,
    @ColumnInfo(name = "category_ids") var categoryIds: String = "",
    @ColumnInfo(name = "total_questions") var totalQuestions: Int = 0,
    @ColumnInfo(name = "correct_count") var correctCount: Int = 0,
    @ColumnInfo(name = "wrong_count") var wrongCount: Int = 0,
    @ColumnInfo(name = "score") var score: Double = 0.0,
    @ColumnInfo(name = "time_spent") var timeSpent: Int = 0,
    @ColumnInfo(name = "start_time") var startTime: Long = 0,
    @ColumnInfo(name = "end_time") var endTime: Long = 0,
    @ColumnInfo(name = "question_ids") var questionIds: String = "",
    @ColumnInfo(name = "user_answers") var userAnswers: String = ""
) {
    companion object {
        const val TYPE_FULL_MOCK = 1
        const val TYPE_CHAPTER_TEST = 2
        const val TYPE_SPECIAL_TOPIC = 3
    }
}