package com.linxia.exam.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "questions",
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["question_type"]),
        Index(value = ["difficulty"]),
        Index(value = ["is_collected"]),
        Index(value = ["is_wrong"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class Question(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var categoryId: Long = 0,
    var questionType: Int = 1, // 1=单选, 2=多选, 3=判断, 4=简答
    var content: String = "",
    var optionA: String = "",
    var optionB: String = "",
    var optionC: String = "",
    var optionD: String = "",
    var optionE: String = "",
    var optionF: String = "",
    var correctAnswer: String = "",
    var explanation: String = "",
    var difficulty: Int = 2, // 1=易, 2=中, 3=难
    var source: String = "",
    var tags: String = "", // JSON数组字符串
    var isCollected: Int = 0,
    var isWrong: Int = 0,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_SINGLE_CHOICE = 1
        const val TYPE_MULTIPLE_CHOICE = 2
        const val TYPE_TRUE_FALSE = 3
        const val TYPE_SHORT_ANSWER = 4

        const val DIFFICULTY_EASY = 1
        const val DIFFICULTY_MEDIUM = 2
        const val DIFFICULTY_HARD = 3

        fun getOptions(question: Question): List<String> {
            return listOf(
                question.optionA,
                question.optionB,
                question.optionC,
                question.optionD,
                question.optionE,
                question.optionF
            ).filter { it.isNotBlank() }
        }

        fun getOptionLabels(count: Int): List<String> {
            return listOf("A", "B", "C", "D", "E", "F").take(count)
        }
    }
}