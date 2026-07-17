package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.ExamRecord
import com.linxia.exam.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _examType = MutableStateFlow<ExamType>(ExamType.FULL_MOCK)
    val examType = _examType

    private val _selectedCategories = MutableStateFlow<List<Long>>(emptyList())
    val selectedCategories = _selectedCategories

    private val _questionCount = MutableStateFlow(100)
    val questionCount = _questionCount

    private val _examDuration = MutableStateFlow(120) // 分钟
    val examDuration = _examDuration

    private val _isExamRunning = MutableStateFlow(false)
    val isExamRunning = _isExamRunning

    private val _examStartTime = MutableStateFlow<Long>(0)
    val examStartTime = _examStartTime

    val allExams: Flow<List<ExamRecord>> = examRepository.getAllExams(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val recentExams: Flow<List<ExamRecord>> = examRepository.getRecentExams(1L, 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val averageScore: Flow<Double> = examRepository.getAverageScore(1L)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val highestScore: Flow<Double> = examRepository.getHighestScore(1L)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val examCount: Flow<Int> = examRepository.getExamCount(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun setExamType(type: ExamType) {
        _examType.value = type
    }

    fun setSelectedCategories(categories: List<Long>) {
        _selectedCategories.value = categories
    }

    fun setQuestionCount(count: Int) {
        _questionCount.value = count.coerceIn(10, 200)
    }

    fun setExamDuration(minutes: Int) {
        _examDuration.value = minutes.coerceIn(30, 240)
    }

    fun startExam() {
        _isExamRunning.value = true
        _examStartTime.value = System.currentTimeMillis()
    }

    suspend fun finishExam(
        totalQuestions: Int,
        correctCount: Int,
        wrongCount: Int,
        score: Double,
        questionIds: List<Long>,
        userAnswers: Map<Long, String>
    ) {
        _isExamRunning.value = false
        val endTime = System.currentTimeMillis()
        val timeSpent = ((endTime - _examStartTime.value) / 1000).toInt()

        val record = ExamRecord(
            userId = 1,
            examType = _examType.value.ordinal + 1,
            categoryIds = kotlinx.serialization.json.Json.encodeToString(_selectedCategories.value),
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            wrongCount = wrongCount,
            score = score,
            timeSpent = timeSpent,
            startTime = _examStartTime.value,
            endTime = endTime,
            questionIds = kotlinx.serialization.json.Json.encodeToString(questionIds),
            userAnswers = kotlinx.serialization.json.Json.encodeToString(userAnswers)
        )
        examRepository.insertRecord(record)
    }

    fun cancelExam() {
        _isExamRunning.value = false
    }

    enum class ExamType {
        FULL_MOCK, CHAPTER_TEST, SPECIAL_TOPIC
    }
}