package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.Collection
import com.linxia.exam.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
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

    val allCollections: Flow<List<Collection>> = collectionRepository.getAll(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val collectionsWithDetail: Flow<List<CollectionRepository.CollectionWithDetail>> = collectionRepository.getAllWithDetail(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val collectionCount: Flow<Int> = flow { emit(collectionRepository.getCount(1L)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun setFilterCategory(categoryId: Long) {
        _filterCategory.value = categoryId
    }

    suspend fun toggleCollection(questionId: Long, categoryId: Long, note: String = "") {
        val existing = collectionRepository.getByQuestion(1L, questionId)
        if (existing != null) {
            collectionRepository.deleteByQuestion(1L, questionId)
        } else {
            val collection = Collection(
                userId = 1,
                questionId = questionId,
                categoryId = categoryId,
                collectTime = System.currentTimeMillis(),
                note = note
            )
            collectionRepository.insert(collection)
        }
    }

    suspend fun updateNote(collectionId: Long, note: String) {
    }

    suspend fun removeCollection(questionId: Long) {
        collectionRepository.deleteByQuestion(1L, questionId)
    }

    suspend fun clearAllCollections() {
        collectionRepository.clearAll(1L)
    }
}
