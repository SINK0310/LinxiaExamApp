package com.linxia.exam.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "categories",
    indices = [Index(value = ["parent_id"])],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class Category(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var parentId: Long = 0,
    var name: String = "",
    var level: Int = 1,
    var sortOrder: Int = 0,
    var questionCount: Int = 0,
    var icon: String = "",
    var color: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROOT_PARENT_ID = 0L
        const val LEVEL_ONE = 1
        const val LEVEL_TWO = 2
        const val LEVEL_THREE = 3
    }
}