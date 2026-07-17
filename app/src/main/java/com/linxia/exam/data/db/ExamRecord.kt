package com.linxia.exam.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_records")
data class ExamRecord(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var userId: Long = 1,
    var examType: Int = 1, // 1=全真模拟, 2=章节测试, 3=专项突破
    var categoryIds: String = "", // JSON数组
    var totalQuestions: Int = 0,
    var correctCount: Int = 0,
    var wrongCount: Int = 0,
    var score: Double = 0.0,
    var timeSpent: Int = 0, // 秒
    var startTime: Long = 0,
    var endTime: Long = 0,
    var questionIds: String = "", // JSON数组
    var userAnswers: String = "" // JSON对象
) {
    companion object {
        const val TYPE_FULL_MOCK = 1
        const val TYPE_CHAPTER_TEST = 2
        const val TYPE_SPECIAL_TOPIC = 3
    }
}