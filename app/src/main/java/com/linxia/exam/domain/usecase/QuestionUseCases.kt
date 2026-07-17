package com.linxia.exam.domain.usecase

import com.linxia.exam.data.db.entity.Question
import com.linxia.exam.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionsByCategoryUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(categoryId: Long): Flow<List<Question>> {
        return questionRepository.getByCategory(categoryId)
    }

    suspend fun getSync(categoryId: Long): List<Question> {
        return questionRepository.getByCategorySync(categoryId)
    }
}

class GetRandomQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(categoryId: Long, limit: Int): List<Question> {
        return questionRepository.getRandomByCategory(categoryId, limit)
    }

    suspend fun invoke(categoryIds: List<Long>, limit: Int): List<Question> {
        return questionRepository.getRandomByCategories(categoryIds, limit)
    }
}

class GetCollectedQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(): Flow<List<Question>> {
        return questionRepository.getCollectedQuestions()
    }
}

class GetWrongQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(): Flow<List<Question>> {
        return questionRepository.getWrongQuestions()
    }
}

class SearchQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(keyword: String): List<Question> {
        return questionRepository.searchQuestions(keyword)
    }
}

class ToggleCollectedUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(questionId: Long, collected: Boolean) {
        questionRepository.setCollected(questionId, if (collected) 1 else 0)
    }
}

class ToggleWrongUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(questionId: Long, wrong: Boolean) {
        questionRepository.setWrong(questionId, if (wrong) 1 else 0)
    }
}

class GetQuestionByIdUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(id: Long): Question? {
        return questionRepository.getQuestionById(id)
    }
}

class GetCategoryTreeUseCase @Inject constructor(
    private val categoryRepository: com.linxia.exam.domain.repository.CategoryRepository
) {
    operator fun invoke(): Flow<List<com.linxia.exam.domain.repository.CategoryRepository.CategoryWithChildren>> {
        return categoryRepository.getCategoryTree()
    }

    suspend fun getSync(): List<com.linxia.exam.domain.repository.CategoryRepository.CategoryWithChildren> {
        return categoryRepository.getCategoryTreeSync()
    }
}

class SearchCategoriesUseCase @Inject constructor(
    private val categoryRepository: com.linxia.exam.domain.repository.CategoryRepository
) {
    operator fun invoke(keyword: String): Flow<List<com.linxia.exam.data.db.entity.Category>> {
        return categoryRepository.searchCategories(keyword)
    }
}