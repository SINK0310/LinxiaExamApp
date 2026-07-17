package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.domain.usecase.GetCategoryTreeUseCase
import com.linxia.exam.domain.usecase.GetRootCategoriesUseCase
import com.linxia.exam.domain.usecase.GetChildCategoriesUseCase
import com.linxia.exam.domain.usecase.SearchCategoriesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoryTreeUseCase: GetCategoryTreeUseCase,
    private val getRootCategoriesUseCase: GetRootCategoriesUseCase,
    private val getChildCategoriesUseCase: GetChildCategoriesUseCase,
    private val searchCategoriesUseCase: SearchCategoriesUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery

    val categoryTree: Flow<List<CategoryRepository.CategoryTreeNode>> = getCategoryTreeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val rootCategories: Flow<List<Category>> = getRootCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val searchResults: Flow<List<Category>> = combine(_searchQuery) { query ->
        if (query.isBlank()) emptyList() else searchCategoriesUseCase(query)
    }.flattenMerge().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun getChildren(parentId: Long): Flow<List<Category>> {
        return getChildCategoriesUseCase(parentId)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}