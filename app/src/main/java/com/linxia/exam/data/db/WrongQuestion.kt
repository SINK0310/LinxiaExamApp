package com.linxia.exam.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    var userId: Long = 1,
    var questionId: Long = 0,
    var categoryId: Long = 0,
    var userAnswer: String = "",
    var wrongCount: Int = 1,
    var lastWrongTime: Long = System.currentTimeMillis(),
    var isReviewed: Int = 0,
    var reviewCount: Int = 0,
    var lastReviewTime: Long = 0,
    var masteryStatus: Int = 0 // 0=未掌握, 1=复习中, 2=已掌握
) {
    companion object {
        const val STATUS_NOT_MASTERED = 0
        const val STATUS_REVIEWING = 1
        const val STATUS_MASTERED = 2
    }
}