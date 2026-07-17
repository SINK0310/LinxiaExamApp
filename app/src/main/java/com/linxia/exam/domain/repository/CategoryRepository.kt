package com.linxia.exam.domain.repository

import com.linxia.exam.data.db.entity.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insertAll(categories: List<Category>)
    suspend fun insert(category: Category): Long
    suspend fun update(category: Category)
    suspend fun deleteById(id: Long)
    fun getChildren(parentId: Long): Flow<List<Category>>
    suspend fun getChildrenSync(parentId: Long): List<Category>
    suspend fun getById(id: Long): Category?
    fun getByLevel(level: Int): Flow<List<Category>>
    fun getRootCategories(): Flow<List<Category>>
    suspend fun getRootCategoriesSync(): List<Category>
    fun searchCategories(keyword: String): Flow<List<Category>>
    suspend fun getChildrenCount(parentId: Long): Int
    suspend fun updateQuestionCount(id: Long)
    suspend fun updateAllQuestionCounts()
    fun getCategoryTree(): Flow<List<CategoryRepository.CategoryWithChildren>>
    suspend fun getCategoryTreeSync(): List<CategoryRepository.CategoryWithChildren>

    data class CategoryWithChildren(
        var category: Category,
        var children: List<CategoryWithChildren> = emptyList()
    )
}