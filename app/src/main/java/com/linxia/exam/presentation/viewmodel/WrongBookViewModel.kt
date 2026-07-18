package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.WrongQuestion
import com.linxia.exam.domain.repository.WrongQuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class WrongBookViewModel @Inject constructor(
    private val wrongQuestionRepository: WrongQuestionRepository
) : ViewModel() {

    private val _filterStatus = MutableStateFlow<FilterStatus>(FilterStatus.ALL)
    val filterStatus = _filterStatus

    private val _filterCategory = MutableStateFlow<Long>(0)
    val filterCategory = _filterCategory

    val allWrongQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getAll(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val unmasteredQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getByStatus(1L, 0)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val reviewingQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getByStatus(1L, 1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val masteredQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getByStatus(1L, 2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val filteredQuestions: Flow<List<WrongQuestion>> = combine(_filterStatus, _filterCategory) { status, _ ->
        when (status) {
            FilterStatus.ALL -> allWrongQuestions
            FilterStatus.UNMASTERED -> unmasteredQuestions
            FilterStatus.REVIEWING -> reviewingQuestions
            FilterStatus.MASTERED -> masteredQuestions
        }
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val totalCount: Flow<Int> = flow { emit(wrongQuestionRepository.getTotalCount(1L)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val unmasteredCount: Flow<Int> = flow { emit(wrongQuestionRepository.getUnmasteredCount(1L)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val masteredCount: Flow<Int> = flow { emit(wrongQuestionRepository.getMasteredCount(1L)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val wrongQuestionsWithDetail: Flow<List<WrongQuestionRepository.WrongQuestionWithDetail>> = wrongQuestionRepository.getAllWithDetail(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setFilterStatus(status: FilterStatus) {
        _filterStatus.value = status
    }

    fun setFilterCategory(categoryId: Long) {
        _filterCategory.value = categoryId
    }

    suspend fun markAsMastered(wrongQuestion: WrongQuestion) {
        val updated = wrongQuestion.copy(
            masteryStatus = WrongQuestion.STATUS_MASTERED,
            isReviewed = 1,
            lastReviewTime = System.currentTimeMillis(),
            reviewCount = wrongQuestion.reviewCount + 1
        )
        wrongQuestionRepository.update(updated)
    }

    suspend fun markAsReviewing(wrongQuestion: WrongQuestion) {
        val updated = wrongQuestion.copy(
            masteryStatus = WrongQuestion.STATUS_REVIEWING,
            isReviewed = 1,
            lastReviewTime = System.currentTimeMillis(),
            reviewCount = wrongQuestion.reviewCount + 1
        )
        wrongQuestionRepository.update(updated)
    }

    suspend fun removeWrongQuestion(questionId: Long) {
        wrongQuestionRepository.deleteByQuestion(1L, questionId)
    }

    suspend fun clearMastered() {
        wrongQuestionRepository.clearMastered(1L)
    }

    enum class FilterStatus {
        ALL, UNMASTERED, REVIEWING, MASTERED
    }
}
