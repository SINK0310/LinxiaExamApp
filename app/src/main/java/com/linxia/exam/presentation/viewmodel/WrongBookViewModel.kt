package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.WrongQuestion
import com.linxia.exam.domain.repository.WrongQuestionRepository
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
class WrongBookViewModel @Inject constructor(
    private val wrongQuestionRepository: WrongQuestionRepository
) : ViewModel() {

    private val _filterStatus = MutableStateFlow<FilterStatus>(FilterStatus.ALL)
    val filterStatus = _filterStatus

    private val _filterCategory = MutableStateFlow<Long>(0)
    val filterCategory = _filterCategory

    val allWrongQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getAllWrongQuestions(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val unmasteredQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getWrongQuestionsByStatus(1L, 0)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val reviewingQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getWrongQuestionsByStatus(1L, 1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val masteredQuestions: Flow<List<WrongQuestion>> = wrongQuestionRepository.getWrongQuestionsByStatus(1L, 2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val filteredQuestions: Flow<List<WrongQuestion>> = combine(_filterStatus, _filterCategory) { status, categoryId ->
        when (status) {
            FilterStatus.ALL -> allWrongQuestions
            FilterStatus.UNMASTERED -> unmasteredQuestions
            FilterStatus.REVIEWING -> reviewingQuestions
            FilterStatus.MASTERED -> masteredQuestions
        }
    }.flattenMerge()
        .map { list ->
            if (categoryId == 0L) list else list.filter { it.categoryId == categoryId }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val totalCount: Flow<Int> = wrongQuestionRepository.getWrongQuestionCount(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val unmasteredCount: Flow<Int> = wrongQuestionRepository.getUnmasteredCount(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val masteredCount: Flow<Int> = wrongQuestionRepository.getMasteredCount(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val wrongQuestionsWithDetail: Flow<List<WrongQuestionRepository.WrongQuestionWithDetail>> = wrongQuestionRepository.getWrongQuestionsWithDetail(1L)
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
        wrongQuestionRepository.updateWrongQuestion(updated)
    }

    suspend fun markAsReviewing(wrongQuestion: WrongQuestion) {
        val updated = wrongQuestion.copy(
            masteryStatus = WrongQuestion.STATUS_REVIEWING,
            isReviewed = 1,
            lastReviewTime = System.currentTimeMillis(),
            reviewCount = wrongQuestion.reviewCount + 1
        )
        wrongQuestionRepository.updateWrongQuestion(updated)
    }

    suspend fun removeWrongQuestion(questionId: Long) {
        wrongQuestionRepository.removeWrongQuestion(1L, questionId)
    }

    suspend fun clearMastered() {
        wrongQuestionRepository.clearMastered(1L)
    }

    enum class FilterStatus {
        ALL, UNMASTERED, REVIEWING, MASTERED
    }
}