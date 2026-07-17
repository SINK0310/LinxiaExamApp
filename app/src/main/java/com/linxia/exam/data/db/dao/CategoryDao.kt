package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.linxia.exam.data.db.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): Category?

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Long

    @Query("SELECT * FROM categories WHERE parent_id = :parentId ORDER BY sort_order, id")
    suspend fun getChildren(parentId: Long): List<Category>

    @Query("SELECT * FROM categories WHERE parent_id = 0 ORDER BY sort_order, id")
    fun getRootCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE parent_id = 0 ORDER BY sort_order, id")
    suspend fun getRootCategoriesSync(): List<Category>

    @Query("SELECT * FROM categories WHERE level = :level ORDER BY sort_order, id")
    suspend fun getByLevel(level: Int): List<Category>

    @Query("SELECT * FROM categories ORDER BY level, sort_order, id")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY level, sort_order, id")
    suspend fun getAllCategoriesSync(): List<Category>

    @Query("SELECT * FROM categories WHERE name LIKE '%' || :keyword || '%' ORDER BY level, sort_order")
    suspend fun search(keyword: String): List<Category>

    @Transaction
    @Query("SELECT * FROM categories WHERE parent_id = 0 ORDER BY sort_order, id")
    fun getCategoryTree(): Flow<List<CategoryWithChildren>>

    @Transaction
    @Query("SELECT * FROM categories WHERE parent_id = 0 ORDER BY sort_order, id")
    suspend fun getCategoryTreeSync(): List<CategoryWithChildren>

    @Query("UPDATE categories SET question_count = (SELECT COUNT(*) FROM questions WHERE category_id = categories.id) WHERE id = :id")
    suspend fun updateQuestionCount(id: Long)

    @Query("UPDATE categories SET question_count = (SELECT COUNT(*) FROM questions WHERE category_id = categories.id)")
    suspend fun updateAllQuestionCounts()

    data class CategoryWithChildren(
        @Embedded var category: Category,
        @Relation(
            parentColumn = "id",
            entityColumn = "parent_id",
            entity = Category::class
        )
        var children: List<CategoryWithChildren> = emptyList()
    )
}