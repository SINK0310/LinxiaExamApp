package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "mock_exams",
    indices = [Index(value = ["name"])]
)
data class MockExam(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var description: String = "",
    @ColumnInfo(name = "total_questions") var totalQuestions: Int = 0,
    @ColumnInfo(name = "total_score") var totalScore: Double = 0.0,
    @ColumnInfo(name = "time_limit") var timeLimit: Int = 120,
    @ColumnInfo(name = "created_at") var createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") var updatedAt: Long = System.currentTimeMillis()
)
