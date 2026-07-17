package com.linxia.exam.data.repository

import com.linxia.exam.data.db.dao.CategoryDao
import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun insertAll(categories: List<Category>) {
        categoryDao.insertAll(categories)
    }

    override suspend fun insert(category: Category): Long {
        return categoryDao.insert(category)
    }

    override suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    override suspend fun deleteById(id: Long) {
        categoryDao.deleteById(id)
    }

    override fun getChildren(parentId: Long): Flow<List<Category>> {
        return categoryDao.getChildren(parentId)
    }

    override suspend fun getChildrenSync(parentId: Long): List<Category> {
        return categoryDao.getChildrenSync(parentId)
    }

    override suspend fun getById(id: Long): Category? {
        return categoryDao.getById(id)
    }

    override fun getByLevel(level: Int): Flow<List<Category>> {
        return categoryDao.getByLevel(level)
    }

    override fun getRootCategories(): Flow<List<Category>> {
        return categoryDao.getRootCategories()
    }

    override suspend fun getRootCategoriesSync(): List<Category> {
        return categoryDao.getRootCategoriesSync()
    }

    override fun searchCategories(keyword: String): Flow<List<Category>> {
        return categoryDao.search(keyword).asFlow()
    }

    override suspend fun getChildrenCount(parentId: Long): Int {
        return categoryDao.getChildrenCount(parentId)
    }

    override suspend fun updateQuestionCount(id: Long) {
        categoryDao.updateQuestionCount(id)
    }

    override suspend fun updateAllQuestionCounts() {
        categoryDao.updateAllQuestionCounts()
    }

    override fun getCategoryTree(): Flow<List<CategoryWithChildren>> {
        return categoryDao.getCategoryTree().map { list ->
            list.map { convertToTreeNode(it) }
        }
    }

    override suspend fun getCategoryTreeSync(): List<CategoryWithChildren> {
        return categoryDao.getCategoryTreeSync().map { convertToTreeNode(it) }
    }

    private fun convertToTreeNode(node: CategoryDao.CategoryWithChildren): CategoryWithChildren {
        return CategoryWithChildren(
            category = node.category,
            children = node.children.map { convertToTreeNode(it) }
        )
    }
}