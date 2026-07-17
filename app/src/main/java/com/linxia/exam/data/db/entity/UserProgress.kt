package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "user_progress",
    indices = [Index(value = ["category_id"])]
)
data class UserProgress(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "user_id") var userId: Long = 1,
    @ColumnInfo(name = "category_id") var categoryId: Long = 0,
    @ColumnInfo(name = "total_questions") var totalQuestions: Int = 0,
    @ColumnInfo(name = "practiced_count") var practicedCount: Int = 0,
    @ColumnInfo(name = "correct_count") var correctCount: Int = 0,
    @ColumnInfo(name = "wrong_count") var wrongCount: Int = 0,
    @ColumnInfo(name = "last_practice_time") var lastPracticeTime: Long = 0,
    @ColumnInfo(name = "mastery_level") var masteryLevel: Int = 0
)