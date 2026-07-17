package com.linxia.exam.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_progress",
    indices = [Index(value = ["category_id"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserProgress(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var userId: Long = 1,
    var categoryId: Long = 0,
    var totalQuestions: Int = 0,
    var practicedCount: Int = 0,
    var correctCount: Int = 0,
    var wrongCount: Int = 0,
    var lastPracticeTime: Long = 0,
    var masteryLevel: Int = 0 // 0-100
)