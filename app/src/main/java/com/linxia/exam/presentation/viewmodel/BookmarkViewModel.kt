package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.Bookmark
import com.linxia.exam.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _filterCategory = MutableStateFlow<Long>(0)
    val filterCategory = _filterCategory

    val allBookmarks: Flow<List<Bookmark>> = bookmarkRepository.getAll(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val bookmarksWithDetail: Flow<List<BookmarkRepository.BookmarkWithDetail>> = bookmarkRepository.getAllWithDetail(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val bookmarkCount: Flow<Int> = flow { emit(bookmarkRepository.getCount(1L)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun setFilterCategory(categoryId: Long) {
        _filterCategory.value = categoryId
    }

    suspend fun toggleBookmark(questionId: Long, categoryId: Long, note: String = "") {
        val existing = bookmarkRepository.getByQuestion(1L, questionId)
        if (existing != null) {
            bookmarkRepository.deleteByQuestion(1L, questionId)
        } else {
            val bookmark = Bookmark(
                userId = 1,
                questionId = questionId,
                categoryId = categoryId,
                collectTime = System.currentTimeMillis(),
                note = note
            )
            bookmarkRepository.insert(bookmark)
        }
    }

    suspend fun updateNote(bookmarkId: Long, note: String) {
    }

    suspend fun removeBookmark(questionId: Long) {
        bookmarkRepository.deleteByQuestion(1L, questionId)
    }

    suspend fun clearAllBookmarks() {
        bookmarkRepository.clearAll(1L)
    }
}
