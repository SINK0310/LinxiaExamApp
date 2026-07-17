package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "user_settings",
    indices = [Index(value = ["user_id", "key"], unique = true)]
)
data class UserSettings(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "user_id") var userId: Long = 1,
    var key: String = "",
    var value: String = ""
) {
    companion object {
        const val KEY_DAILY_GOAL = "daily_goal"
        const val KEY_EXAM_DURATION = "exam_duration"
        const val KEY_QUESTION_ORDER = "question_order"
        const val KEY_SHOW_EXPLANATION = "show_explanation"
        const val KEY_AUTO_NEXT = "auto_next"
        const val KEY_NIGHT_MODE = "night_mode"
        const val KEY_FONT_SIZE = "font_size"
        const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        const val KEY_OFFLINE_ENABLED = "offline_enabled"
        const val KEY_LAST_SYNC_TIME = "last_sync_time"
        const val KEY_REVIEW_REMINDER = "review_reminder"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    }
}