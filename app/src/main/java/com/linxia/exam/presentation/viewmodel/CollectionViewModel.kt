package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.Collection
import com.linxia.exam.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _filterCategory = MutableStateFlow<Long>(0)
    val filterCategory = _filterCategory

    val allCollections: Flow<List<Collection>> = collectionRepository.getAllCollections(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val filteredCollections: Flow<List<Collection>> = combine(_filterCategory) { categoryId ->
        allCollections.map { list ->
            if (categoryId == 0L) list else list.filter { it.categoryId == categoryId }
        }
    }.flattenMerge()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val collectionsWithDetail: Flow<List<CollectionRepository.CollectionWithDetail>> = collectionRepository.getCollectionsWithDetail(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val collectionCount: Flow<Int> = collectionRepository.getCollectionCount(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun setFilterCategory(categoryId: Long) {
        _filterCategory.value = categoryId
    }

    suspend fun toggleCollection(questionId: Long, categoryId: Long, note: String = "") {
        val existing = collectionRepository.getCollectionByQuestion(1L, questionId)
        if (existing != null) {
            collectionRepository.removeCollection(1L, questionId)
        } else {
            val collection = Collection(
                userId = 1,
                questionId = questionId,
                categoryId = categoryId,
                collectTime = System.currentTimeMillis(),
                note = note
            )
            collectionRepository.addCollection(collection)
        }
    }

    suspend fun updateNote(collectionId: Long, note: String) {
        // 需要先获取collection再更新
    }

    suspend fun removeCollection(questionId: Long) {
        collectionRepository.removeCollection(1L, questionId)
    }

    suspend fun clearAllCollections() {
        collectionRepository.clearAllCollections(1L)
    }
}