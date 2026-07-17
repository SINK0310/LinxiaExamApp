package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.Question
import com.linxia.exam.domain.usecase.GetQuestionsByCategoryUseCase
import com.linxia.exam.domain.usecase.GetRandomQuestionsUseCase
import com.linxia.exam.domain.usecase.GetCollectedQuestionsUseCase
import com.linxia.exam.domain.usecase.GetWrongQuestionsUseCase
import com.linxia.exam.domain.usecase.SearchQuestionsUseCase
import com.linxia.exam.domain.usecase.ToggleCollectedUseCase
import com.linxia.exam.domain.usecase.ToggleWrongUseCase
import com.linxia.exam.domain.usecase.GetQuestionByIdUseCase
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
class QuestionViewModel @Inject constructor(
    private val getQuestionsByCategoryUseCase: GetQuestionsByCategoryUseCase,
    private val getRandomQuestionsUseCase: GetRandomQuestionsUseCase,
    private val getCollectedQuestionsUseCase: GetCollectedQuestionsUseCase,
    private val getWrongQuestionsUseCase: GetWrongQuestionsUseCase,
    private val searchQuestionsUseCase: SearchQuestionsUseCase,
    private val toggleCollectedUseCase: ToggleCollectedUseCase,
    private val toggleWrongUseCase: ToggleWrongUseCase,
    private val getQuestionByIdUseCase: GetQuestionByIdUseCase
) : ViewModel() {

    private val _currentCategoryId = MutableStateFlow<Long>(0)
    val currentCategoryId = _currentCategoryId

    private val _practiceMode = MutableStateFlow<PracticeMode>(PracticeMode.CHAPTER)
    val practiceMode = _practiceMode

    private val _shuffleQuestions = MutableStateFlow(false)
    val shuffleQuestions = _shuffleQuestions

    val questionsByCategory: Flow<List<Question>> = combine(_currentCategoryId) { categoryId ->
        if (categoryId > 0) getQuestionsByCategoryUseCase(categoryId) else emptyList()
    }.flattenMerge().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val randomQuestions: Flow<List<Question>> = combine(_currentCategoryId, _practiceMode, _shuffleQuestions) { categoryId, mode, shuffle ->
        if (categoryId > 0 && shuffle) getRandomQuestionsUseCase(categoryId, 20) else emptyList()
    }.flattenMerge().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val collectedQuestions: Flow<List<Question>> = getCollectedQuestionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val wrongQuestions: Flow<List<Question>> = getWrongQuestionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setCategory(categoryId: Long) {
        _currentCategoryId.value = categoryId
    }

    fun setPracticeMode(mode: PracticeMode) {
        _practiceMode.value = mode
    }

    fun setShuffle(shuffle: Boolean) {
        _shuffleQuestions.value = shuffle
    }

    suspend fun searchQuestions(keyword: String): List<Question> {
        return searchQuestionsUseCase(keyword)
    }

    suspend fun toggleCollected(questionId: Long, collected: Boolean) {
        toggleCollectedUseCase(questionId, collected)
    }

    suspend fun toggleWrong(questionId: Long, wrong: Boolean) {
        toggleWrongUseCase(questionId, wrong)
    }

    suspend fun getQuestion(id: Long): Question? {
        return getQuestionByIdUseCase(id)
    }

    enum class PracticeMode {
        CHAPTER, MOCK_EXAM, WRONG_REVIEW, COLLECTION
    }
}