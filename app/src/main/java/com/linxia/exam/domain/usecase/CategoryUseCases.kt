package com.linxia.exam.domain.usecase

import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryTreeUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<CategoryRepository.CategoryTreeNode>> {
        return repository.observeCategoryTree()
    }

    suspend fun getTreeSync(): List<CategoryRepository.CategoryTreeNode> {
        return repository.getCategoryTree()
    }
}

class GetRootCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.observeRootCategories()
    }
}

class GetChildCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(parentId: Long): Flow<List<Category>> {
        return repository.observeChildren(parentId)
    }
}

class SearchCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(keyword: String): List<Category> {
        return repository.searchCategories(keyword)
    }
}