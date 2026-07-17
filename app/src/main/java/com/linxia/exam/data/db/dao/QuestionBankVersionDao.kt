package com.linxia.exam.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.linxia.exam.data.db.entity.QuestionBankVersion
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionBankVersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(version: QuestionBankVersion): Long

    @Update
    suspend fun update(version: QuestionBankVersion)

    @Query("SELECT * FROM question_bank_versions WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveVersion(): QuestionBankVersion?

    @Query("SELECT * FROM question_bank_versions ORDER BY release_time DESC LIMIT 1")
    suspend fun getLatestVersion(): QuestionBankVersion?

    @Query("SELECT * FROM question_bank_versions WHERE version = :version")
    suspend fun getByVersion(version: String): QuestionBankVersion?

    @Query("SELECT * FROM question_bank_versions ORDER BY release_time DESC")
    fun getAllVersions(): Flow<List<QuestionBankVersion>>

    @Query("DELETE FROM question_bank_versions WHERE version = :version")
    suspend fun deleteByVersion(version: String)
}