package com.linxia.exam.di

import androidx.room.Room
import com.linxia.exam.data.db.AppDatabase
import com.linxia.exam.data.db.dao.*
import com.linxia.exam.data.repository.*
import com.linxia.exam.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideQuestionDao(db: AppDatabase): QuestionDao = db.questionDao()

    @Provides
    fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()

    @Provides
    fun providePracticeRecordDao(db: AppDatabase): PracticeRecordDao = db.practiceRecordDao()

    @Provides
    fun provideExamRecordDao(db: AppDatabase): ExamRecordDao = db.examRecordDao()

    @Provides
    fun provideWrongQuestionDao(db: AppDatabase): WrongQuestionDao = db.wrongQuestionDao()

    @Provides
    fun provideCollectionDao(db: AppDatabase): CollectionDao = db.collectionDao()

    @Provides
    fun provideOfflineCacheDao(db: AppDatabase): OfflineCacheDao = db.offlineCacheDao()

    @Provides
    fun provideUserSettingsDao(db: AppDatabase): UserSettingsDao = db.userSettingsDao()

    @Provides
    fun provideQuestionBankVersionDao(db: AppDatabase): QuestionBankVersionDao = db.questionBankVersionDao()

    @Provides
    fun provideMockExamDao(db: AppDatabase): MockExamDao = db.mockExamDao()

    @Provides
    fun provideMockExamQuestionDao(db: AppDatabase): MockExamQuestionDao = db.mockExamQuestionDao()

    @Provides
    fun providePracticeExerciseDao(db: AppDatabase): PracticeExerciseDao = db.practiceExerciseDao()

    @Provides
    fun providePracticeQuestionDao(db: AppDatabase): PracticeQuestionDao = db.practiceQuestionDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository = impl

    @Provides
    fun provideQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository = impl

    @Provides
    fun provideUserProgressRepository(impl: UserProgressRepositoryImpl): UserProgressRepository = impl

    @Provides
    fun providePracticeRecordRepository(impl: PracticeRecordRepositoryImpl): PracticeRecordRepository = impl

    @Provides
    fun provideExamRecordRepository(impl: ExamRecordRepositoryImpl): ExamRecordRepository = impl

    @Provides
    fun provideWrongQuestionRepository(impl: WrongQuestionRepositoryImpl): WrongQuestionRepository = impl

    @Provides
    fun provideCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository = impl

    @Provides
    fun provideOfflineCacheRepository(impl: OfflineCacheRepositoryImpl): OfflineCacheRepository = impl

    @Provides
    fun provideUserSettingsRepository(impl: UserSettingsRepositoryImpl): UserSettingsRepository = impl

    @Provides
    fun provideQuestionBankVersionRepository(impl: QuestionBankVersionRepositoryImpl): QuestionBankVersionRepository = impl
}