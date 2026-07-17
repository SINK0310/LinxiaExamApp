package com.linxia.exam.domain.usecase

import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryTreeUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<CategoryRepository.CategoryWithChildren>> {
        return repository.getCategoryTree()
    }

    suspend fun getTreeSync(): List<CategoryRepository.CategoryWithChildren> {
        return repository.getCategoryTreeSync()
    }
}

class GetRootCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getRootCategories()
    }
}

class GetChildCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(parentId: Long): Flow<List<Category>> {
        return repository.getChildren(parentId)
    }
}

class SearchCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(keyword: String): Flow<List<Category>> {
        return repository.searchCategories(keyword)
    }
}