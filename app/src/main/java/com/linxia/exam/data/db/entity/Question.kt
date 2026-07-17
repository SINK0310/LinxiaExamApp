package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey

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
data class Question(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "category_id") var categoryId: Long = 0,
    @ColumnInfo(name = "question_type") var questionType: Int = 1,
    var content: String = "",
    @ColumnInfo(name = "option_a") var optionA: String = "",
    @ColumnInfo(name = "option_b") var optionB: String = "",
    @ColumnInfo(name = "option_c") var optionC: String = "",
    @ColumnInfo(name = "option_d") var optionD: String = "",
    @ColumnInfo(name = "option_e") var optionE: String = "",
    @ColumnInfo(name = "option_f") var optionF: String = "",
    @ColumnInfo(name = "correct_answer") var correctAnswer: String = "",
    var explanation: String = "",
    var difficulty: Int = 2,
    var source: String = "",
    var tags: String = "",
    @ColumnInfo(name = "is_collected") var isCollected: Int = 0,
    @ColumnInfo(name = "is_wrong") var isWrong: Int = 0,
    @ColumnInfo(name = "created_at") var createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") var updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val TYPE_SINGLE = 1
        const val TYPE_MULTIPLE = 2
        const val TYPE_TRUE_FALSE = 3
        const val TYPE_SHORT_ANSWER = 4

        const val DIFF_EASY = 1
        const val DIFF_MEDIUM = 2
        const val DIFF_HARD = 3
    }

    fun getOptions(): List<String> {
        return listOf(optionA, optionB, optionC, optionD, optionE, optionF)
            .filter { it.isNotBlank() }
    }

    fun getOptionLabels(): List<String> {
        return listOf("A", "B", "C", "D", "E", "F").take(getOptions().size)
    }

    fun getCorrectLabels(): List<String> {
        return correctAnswer.map { it - 'A' }.map { getOptionLabels().getOrNull(it) ?: "" }
            .filter { it.isNotBlank() }
    }
}