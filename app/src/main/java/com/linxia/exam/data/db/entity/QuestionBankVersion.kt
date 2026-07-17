package com.linxia.exam.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "question_bank_versions",
    indices = [Index(value = ["is_active"])]
)
data class QuestionBankVersion(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var version: String = "",
    var description: String = "",
    @ColumnInfo(name = "total_questions") var totalQuestions: Int = 0,
    @ColumnInfo(name = "download_url") var downloadUrl: String = "",
    @ColumnInfo(name = "file_size") var fileSize: Long = 0,
    var checksum: String = "",
    @ColumnInfo(name = "force_update") var forceUpdate: Int = 0,
    @ColumnInfo(name = "release_time") var releaseTime: Long = 0,
    @ColumnInfo(name = "is_active") var isActive: Int = 0
)