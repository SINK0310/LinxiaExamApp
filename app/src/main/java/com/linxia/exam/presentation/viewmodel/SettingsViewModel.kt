package com.linxia.exam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linxia.exam.data.db.entity.UserSettings
import com.linxia.exam.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _nightMode = MutableStateFlow(false)
    val nightMode: Flow<Boolean> = _nightMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _fontSize = MutableStateFlow(16f)
    val fontSize: Flow<Float> = _fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 16f)

    private val _autoNext = MutableStateFlow(true)
    val autoNext: Flow<Boolean> = _autoNext
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _showExplanation = MutableStateFlow(true)
    val showExplanation: Flow<Boolean> = _showExplanation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _questionOrder = MutableStateFlow(1) // 1=顺序, 2=随机
    val questionOrder: Flow<Int> = _questionOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    private val _examDuration = MutableStateFlow(120) // 分钟
    val examDuration: Flow<Int> = _examDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 120)

    private val _dailyGoal = MutableStateFlow(50) // 题数
    val dailyGoal: Flow<Int> = _dailyGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 50)

    private val _notificationEnabled = MutableStateFlow(true)
    val notificationEnabled: Flow<Boolean> = _notificationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: Flow<Boolean> = _soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: Flow<Boolean> = _vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _offlineEnabled = MutableStateFlow(true)
    val offlineEnabled: Flow<Boolean> = _offlineEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = settingsRepository.getAll(1L)
            settings.forEach { setting ->
                when (setting.key) {
                    UserSettings.KEY_NIGHT_MODE -> _nightMode.value = setting.value.toBoolean()
                    UserSettings.KEY_FONT_SIZE -> _fontSize.value = setting.value.toFloat()
                    UserSettings.KEY_AUTO_NEXT -> _autoNext.value = setting.value.toBoolean()
                    UserSettings.KEY_SHOW_EXPLANATION -> _showExplanation.value = setting.value.toBoolean()
                    UserSettings.KEY_QUESTION_ORDER -> _questionOrder.value = setting.value.toInt()
                    UserSettings.KEY_EXAM_DURATION -> _examDuration.value = setting.value.toInt()
                    UserSettings.KEY_DAILY_GOAL -> _dailyGoal.value = setting.value.toInt()
                    UserSettings.KEY_NOTIFICATION_ENABLED -> _notificationEnabled.value = setting.value.toBoolean()
                    UserSettings.KEY_SOUND_ENABLED -> _soundEnabled.value = setting.value.toBoolean()
                    UserSettings.KEY_VIBRATION_ENABLED -> _vibrationEnabled.value = setting.value.toBoolean()
                    UserSettings.KEY_OFFLINE_ENABLED -> _offlineEnabled.value = setting.value.toBoolean()
                }
            }
        }
    }

    fun setNightMode(enabled: Boolean) {
        _nightMode.value = enabled
        saveSetting(UserSettings.KEY_NIGHT_MODE, enabled.toString())
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size
        saveSetting(UserSettings.KEY_FONT_SIZE, size.toString())
    }

    fun setAutoNext(enabled: Boolean) {
        _autoNext.value = enabled
        saveSetting(UserSettings.KEY_AUTO_NEXT, enabled.toString())
    }

    fun setShowExplanation(enabled: Boolean) {
        _showExplanation.value = enabled
        saveSetting(UserSettings.KEY_SHOW_EXPLANATION, enabled.toString())
    }

    fun setQuestionOrder(order: Int) {
        _questionOrder.value = order
        saveSetting(UserSettings.KEY_QUESTION_ORDER, order.toString())
    }

    fun setExamDuration(minutes: Int) {
        _examDuration.value = minutes
        saveSetting(UserSettings.KEY_EXAM_DURATION, minutes.toString())
    }

    fun setDailyGoal(count: Int) {
        _dailyGoal.value = count
        saveSetting(UserSettings.KEY_DAILY_GOAL, count.toString())
    }

    fun setNotificationEnabled(enabled: Boolean) {
        _notificationEnabled.value = enabled
        saveSetting(UserSettings.KEY_NOTIFICATION_ENABLED, enabled.toString())
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        saveSetting(UserSettings.KEY_SOUND_ENABLED, enabled.toString())
    }

    fun setVibrationEnabled(enabled: Boolean) {
        _vibrationEnabled.value = enabled
        saveSetting(UserSettings.KEY_VIBRATION_ENABLED, enabled.toString())
    }

    fun setOfflineEnabled(enabled: Boolean) {
        _offlineEnabled.value = enabled
        saveSetting(UserSettings.KEY_OFFLINE_ENABLED, enabled.toString())
    }

    private fun saveSetting(key: String, value: String) {
        viewModelScope.launch {
            val setting = UserSettings(userId = 1, key = key, value = value)
            settingsRepository.insert(setting)
        }
    }

    suspend fun clearAllData() {
        // 清除所有用户数据
    }

    suspend fun exportData(): String {
        // 导出数据为JSON
        return ""
    }

    suspend fun importData(json: String): Boolean {
        // 导入JSON数据
        return true
    }
}