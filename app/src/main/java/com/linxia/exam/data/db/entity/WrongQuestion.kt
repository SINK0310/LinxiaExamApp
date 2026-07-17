package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey

@Entity(
    tableName = "wrong_questions",
    indices = [
        Index(value = ["question_id"]),
        Index(value = ["category_id"]),
        Index(value = ["mastery_status"]),
        Index(value = ["last_wrong_time"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["question_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class WrongQuestion(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "user_id") var userId: Long = 1,
    @ColumnInfo(name = "question_id") var questionId: Long = 0,
    @ColumnInfo(name = "category_id") var categoryId: Long = 0,
    @ColumnInfo(name = "user_answer") var userAnswer: String = "",
    @ColumnInfo(name = "wrong_count") var wrongCount: Int = 1,
    @ColumnInfo(name = "last_wrong_time") var lastWrongTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_reviewed") var isReviewed: Int = 0,
    @ColumnInfo(name = "review_count") var reviewCount: Int = 0,
    @ColumnInfo(name = "last_review_time") var lastReviewTime: Long = 0,
    @ColumnInfo(name = "mastery_status") var masteryStatus: Int = 0
) {
    companion object {
        const val STATUS_NOT_MASTERED = 0
        const val STATUS_REVIEWING = 1
        const val STATUS_MASTERED = 2
    }
}