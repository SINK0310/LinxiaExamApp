package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.Question
import com.linxia.exam.domain.repository.PracticeRepository
import com.linxia.exam.domain.repository.UserProgressRepository
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
class PracticeViewModel @Inject constructor(
    private val practiceRepository: PracticeRepository,
    private val userProgressRepository: UserProgressRepository,
    private val wrongQuestionRepository: WrongQuestionRepository
) : ViewModel() {

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions = _questions

    private val _userAnswers = MutableStateFlow<MutableMap<Long, String>>(mutableMapOf())
    val userAnswers = _userAnswers

    private val _showExplanation = MutableStateFlow(false)
    val showExplanation = _showExplanation

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished

    private val _practiceMode = MutableStateFlow<PracticeMode>(PracticeMode.CHAPTER)
    val practiceMode = _practiceMode

    val currentQuestion: Flow<Question?> = combine(_questions, _currentQuestionIndex) { questions, index ->
        questions.getOrNull(index)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val progress: Flow<Int> = combine(_questions, _currentQuestionIndex) { questions, index ->
        if (questions.isEmpty()) 0 else ((index + 1) * 100 / questions.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val correctCount: Flow<Int> = combine(_questions, _userAnswers) { questions, answers ->
        questions.count { q ->
            answers[q.id] == q.correctAnswer
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun setQuestions(questions: List<Question>, mode: PracticeMode) {
        _questions.value = questions
        _practiceMode.value = mode
        _currentQuestionIndex.value = 0
        _userAnswers.value = mutableMapOf()
        _showExplanation.value = false
        _isFinished.value = false
    }

    fun answerQuestion(questionId: Long, answer: String) {
        _userAnswers.value = _userAnswers.value.toMutableMap().apply { put(questionId, answer) }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value++
            _showExplanation.value = false
        } else {
            finishPractice()
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value--
            _showExplanation.value = false
        }
    }

    fun jumpToQuestion(index: Int) {
        if (index in 0 until _questions.value.size) {
            _currentQuestionIndex.value = index
            _showExplanation.value = false
        }
    }

    fun toggleExplanation() {
        _showExplanation.value = !_showExplanation.value
    }

    private fun finishPractice() {
        _isFinished.value = true
        savePracticeRecord()
    }

    private fun savePracticeRecord() {
        viewModelScope.launch {
            val questions = _questions.value
            val answers = _userAnswers.value
            val mode = _practiceMode.value.ordinal + 1
            val userId = 1L

            questions.forEachIndexed { index, question ->
                val userAnswer = answers[question.id] ?: ""
                val isCorrect = if (userAnswer == question.correctAnswer) 1 else 0

                // 保存练习记录
                val record = com.linxia.exam.data.db.entity.PracticeRecord(
                    userId = userId,
                    questionId = question.id,
                    categoryId = question.categoryId,
                    practiceMode = mode,
                    userAnswer = userAnswer,
                    isCorrect = isCorrect,
                    timeSpent = 0 // TODO: 计算实际耗时
                )
                practiceRepository.insertRecord(record)

                // 更新错题本
                if (isCorrect == 0) {
                    val wrongQuestion = com.linxia.exam.data.db.entity.WrongQuestion(
                        userId = userId,
                        questionId = question.id,
                        categoryId = question.categoryId,
                        userAnswer = userAnswer,
                        wrongCount = 1,
                        lastWrongTime = System.currentTimeMillis(),
                        masteryStatus = com.linxia.exam.data.db.entity.WrongQuestion.STATUS_NOT_MASTERED
                    )
                    wrongQuestionRepository.addWrongQuestion(wrongQuestion)
                }

                // 更新用户进度
                val progress = userProgressRepository.getProgressByCategory(userId, question.categoryId) ?:
                    com.linxia.exam.data.db.entity.UserProgress(
                        userId = userId,
                        categoryId = question.categoryId,
                        totalQuestions = 0,
                        practicedCount = 0,
                        correctCount = 0,
                        wrongCount = 0
                    )

                progress.practicedCount++
                if (isCorrect == 1) progress.correctCount++ else progress.wrongCount++
                progress.lastPracticeTime = System.currentTimeMillis()
                progress.masteryLevel = (progress.correctCount * 100 / progress.totalQuestions).coerceAtMost(100)
                userProgressRepository.updateProgress(progress)
            }
        }
    }

    enum class PracticeMode {
        CHAPTER, MOCK_EXAM, WRONG_REVIEW, COLLECTION
    }
}