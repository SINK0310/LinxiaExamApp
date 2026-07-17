package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.ForeignKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["parent_id"]), Index(value = ["level"])]
)
data class Category(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "parent_id") var parentId: Long = 0,
    var name: String = "",
    var level: Int = 1,
    @ColumnInfo(name = "sort_order") var sortOrder: Int = 0,
    @ColumnInfo(name = "question_count") var questionCount: Int = 0,
    var icon: String = "",
    var color: String = ""
) {
    companion object {
        const val LEVEL_ONE = 1
        const val LEVEL_TWO = 2
        const val LEVEL_THREE = 3
    }
}