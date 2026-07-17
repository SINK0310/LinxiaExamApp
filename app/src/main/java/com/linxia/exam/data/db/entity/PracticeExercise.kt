package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "practice_exercises",
    indices = [Index(value = ["topic"])]
)
data class PracticeExercise(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var description: String = "",
    var topic: String = "",
    @ColumnInfo(name = "total_questions") var totalQuestions: Int = 0,
    @ColumnInfo(name = "created_at") var createdAt: Long = System.currentTimeMillis()
)
