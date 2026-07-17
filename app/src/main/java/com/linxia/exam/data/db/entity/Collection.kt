package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey

@Entity(
    tableName = "collections",
    indices = [
        Index(value = ["question_id"]),
        Index(value = ["category_id"]),
        Index(value = ["collect_time"])
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
data class Collection(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "user_id") var userId: Long = 1,
    @ColumnInfo(name = "question_id") var questionId: Long = 0,
    @ColumnInfo(name = "category_id") var categoryId: Long = 0,
    @ColumnInfo(name = "collect_time") var collectTime: Long = System.currentTimeMillis(),
    var note: String = ""
)