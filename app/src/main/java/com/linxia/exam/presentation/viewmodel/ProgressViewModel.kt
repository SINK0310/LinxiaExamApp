package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.UserProgress
import com.linxia.exam.domain.repository.UserProgressRepository
import com.linxia.exam.domain.repository.PracticeRepository
import com.linxia.exam.domain.repository.ExamRepository
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
class ProgressViewModel @Inject constructor(
    private val progressRepository: UserProgressRepository,
    private val practiceRepository: PracticeRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _timeRange = MutableStateFlow<TimeRange>(TimeRange.ALL)
    val timeRange = _timeRange

    val allProgress: Flow<List<UserProgress>> = progressRepository.observeProgress(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val progressWithCategory: Flow<List<UserProgressRepository.ProgressWithCategory>> = progressRepository.observeProgressWithCategory(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val totalPracticed: Flow<Int> = progressRepository.getTotalPracticed(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val totalCorrect: Flow<Int> = progressRepository.getTotalCorrect(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val totalWrong: Flow<Int> = progressRepository.getTotalWrong(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val practicedCategoriesCount: Flow<Int> = progressRepository.getPracticedCategoriesCount(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val recentRecords: Flow<List<com.linxia.exam.data.db.entity.PracticeRecord>> = practiceRepository.getRecentRecords(1L, 20)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val recentExams: Flow<List<com.linxia.exam.data.db.entity.ExamRecord>> = examRepository.getRecentExams(1L, 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val averageScore: Flow<Double> = examRepository.getAverageScore(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val highestScore: Flow<Double> = examRepository.getHighestScore(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val examCount: Flow<Int> = examRepository.getExamCount(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val chapterPracticeCount: Flow<Int> = practiceRepository.getCountByMode(1L, 1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val chapterCorrectCount: Flow<Int> = practiceRepository.getCorrectCountByMode(1L, 1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val examPracticeCount: Flow<Int> = practiceRepository.getCountByMode(1L, 2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val examCorrectCount: Flow<Int> = practiceRepository.getCorrectCountByMode(1L, 2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val wrongReviewCount: Flow<Int> = practiceRepository.getCountByMode(1L, 3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val wrongReviewCorrectCount: Flow<Int> = practiceRepository.getCorrectCountByMode(1L, 3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val collectionPracticeCount: Flow<Int> = practiceRepository.getCountByMode(1L, 4)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val collectionCorrectCount: Flow<Int> = practiceRepository.getCorrectCountByMode(1L, 4)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val mostWrongQuestions: Flow<List<PracticeRepository.WrongQuestionStat>> = practiceRepository.getMostWrongQuestions(1L, 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
    }

    fun getAccuracyRate(correct: Int, total: Int): Double {
        return if (total > 0) (correct.toDouble() / total * 100) else 0.0
    }

    enum class TimeRange {
        TODAY, WEEK, MONTH, ALL
    }

    fun getTimeRangeMillis(range: TimeRange): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        return when (range) {
            TimeRange.TODAY -> {
                val startOfDay = getStartOfDay(now)
                startOfDay to now
            }
            TimeRange.WEEK -> (now - 7 * 24 * 60 * 60 * 1000L) to now
            TimeRange.MONTH -> (now - 30 * 24 * 60 * 60 * 1000L) to now
            TimeRange.ALL -> 0L to now
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}