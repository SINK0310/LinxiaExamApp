package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.CategoryDao
import com.linxia.exam.data.db.entity.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getRootCategories(): List<Category>
    fun observeRootCategories(): Flow<List<Category>>
    suspend fun getChildren(parentId: Long): List<Category>
    fun observeChildren(parentId: Long): Flow<List<Category>>
    suspend fun getCategoryTree(): List<CategoryTreeNode>
    fun observeCategoryTree(): Flow<List<CategoryTreeNode>>
    suspend fun searchCategories(keyword: String): List<Category>
    suspend fun updateQuestionCount(categoryId: Long)
    suspend fun updateAllQuestionCounts()

    data class CategoryTreeNode(
        val category: Category,
        val children: List<CategoryTreeNode> = emptyList()
    )
}