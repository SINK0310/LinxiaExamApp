package com.linxia.exam.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linxia.exam.data.db.dao.*
import com.linxia.exam.data.db.entity.*

@Database(
    entities = [
        Category::class,
        Question::class,
        UserProgress::class,
        PracticeRecord::class,
        ExamRecord::class,
        WrongQuestion::class,
        Collection::class,
        OfflineCache::class,
        UserSettings::class,
        QuestionBankVersion::class,
        MockExam::class,
        MockExamQuestion::class,
        PracticeExercise::class,
        PracticeQuestion::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun practiceRecordDao(): PracticeRecordDao
    abstract fun examRecordDao(): ExamRecordDao
    abstract fun wrongQuestionDao(): WrongQuestionDao
    abstract fun collectionDao(): CollectionDao
    abstract fun offlineCacheDao(): OfflineCacheDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun questionBankVersionDao(): QuestionBankVersionDao
    abstract fun mockExamDao(): MockExamDao
    abstract fun mockExamQuestionDao(): MockExamQuestionDao
    abstract fun practiceExerciseDao(): PracticeExerciseDao
    abstract fun practiceQuestionDao(): PracticeQuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "linxia_exam_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}