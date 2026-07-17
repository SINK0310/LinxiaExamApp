package com.linxia.exam.data.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class SeedDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabase()
        }
    }

    private suspend fun seedDatabase() {
        try {
            val json = context.assets.open("seed_data.json")
                .bufferedReader().use { it.readText() }
            val data = JSONObject(json)

            val db = AppDatabase.getInstance(context)

            // 导入分类
            if (db.categoryDao().count() == 0L) {
                val categories = data.getJSONArray("categories")
                for (i in 0 until categories.length()) {
                    val c = categories.getJSONObject(i)
                    val id = c.optLong("id", 0)
                    db.categoryDao().insert(
                        com.linxia.exam.data.db.entity.Category(
                            id = id,
                            parentId = c.optLong("parent_id", 0),
                            name = c.optString("name", ""),
                            level = c.optInt("level", 1),
                            sortOrder = c.optInt("sort_order", 0),
                            questionCount = c.optInt("question_count", 0),
                            icon = c.optString("icon", ""),
                            color = c.optString("color", "")
                        )
                    )
                }
            }

            // 导入题目
            if (db.questionDao().count() == 0L) {
                val questions = data.getJSONArray("questions")
                for (i in 0 until questions.length()) {
                    val q = questions.getJSONObject(i)
                    db.questionDao().insert(
                        com.linxia.exam.data.db.entity.Question(
                            id = q.optLong("id", 0),
                            categoryId = q.optLong("category_id", 0),
                            questionType = q.optInt("question_type", 1),
                            content = q.optString("content", ""),
                            optionA = q.optString("option_a", ""),
                            optionB = q.optString("option_b", ""),
                            optionC = q.optString("option_c", ""),
                            optionD = q.optString("option_d", ""),
                            optionE = q.optString("option_e", ""),
                            optionF = q.optString("option_f", ""),
                            correctAnswer = q.optString("correct_answer", ""),
                            explanation = q.optString("explanation", ""),
                            difficulty = q.optInt("difficulty", 2),
                            source = q.optString("source", ""),
                            tags = q.optString("tags", "[]"),
                            isCollected = q.optInt("is_collected", 0),
                            isWrong = q.optInt("is_wrong", 0)
                        )
                    )
                }
            }

            // 导入模拟考试
            if (db.mockExamDao().count() == 0) {
                val exams = data.getJSONArray("mock_exams")
                for (i in 0 until exams.length()) {
                    val e = exams.getJSONObject(i)
                    db.mockExamDao().insert(
                        com.linxia.exam.data.db.entity.MockExam(
                            id = e.optLong("id", 0),
                            name = e.optString("name", ""),
                            description = e.optString("description", ""),
                            totalQuestions = e.optInt("total_questions", 0),
                            totalScore = e.optDouble("total_score", 0.0),
                            timeLimit = e.optInt("time_limit", 120)
                        )
                    )
                }

                val questions = data.getJSONArray("mock_exam_questions")
                val questionList = mutableListOf<com.linxia.exam.data.db.entity.MockExamQuestion>()
                for (i in 0 until questions.length()) {
                    val q = questions.getJSONObject(i)
                    questionList.add(
                        com.linxia.exam.data.db.entity.MockExamQuestion(
                            id = q.optLong("id", 0),
                            examId = q.optLong("exam_id", 0),
                            questionId = if (q.has("question_id")) q.getLong("question_id") else null,
                            questionType = q.optInt("question_type", 1),
                            content = q.optString("content", ""),
                            optionA = q.optString("option_a", ""),
                            optionB = q.optString("option_b", ""),
                            optionC = q.optString("option_c", ""),
                            optionD = q.optString("option_d", ""),
                            correctAnswer = q.optString("correct_answer", ""),
                            explanation = q.optString("explanation", ""),
                            score = q.optDouble("score", 0.0),
                            sortOrder = q.optInt("sort_order", 0)
                        )
                    )
                }
                db.mockExamQuestionDao().insertAll(questionList)
            }

            // 导入练习板块
            if (db.practiceExerciseDao().count() == 0) {
                val exercises = data.getJSONArray("practice_exercises")
                val exerciseList = mutableListOf<com.linxia.exam.data.db.entity.PracticeExercise>()
                for (i in 0 until exercises.length()) {
                    val e = exercises.getJSONObject(i)
                    exerciseList.add(
                        com.linxia.exam.data.db.entity.PracticeExercise(
                            id = e.optLong("id", 0),
                            name = e.optString("name", ""),
                            description = e.optString("description", ""),
                            topic = e.optString("topic", ""),
                            totalQuestions = e.optInt("total_questions", 0)
                        )
                    )
                }
                db.practiceExerciseDao().insertAll(exerciseList)

                val questions = data.getJSONArray("practice_questions")
                val pqList = mutableListOf<com.linxia.exam.data.db.entity.PracticeQuestion>()
                for (i in 0 until questions.length()) {
                    val q = questions.getJSONObject(i)
                    pqList.add(
                        com.linxia.exam.data.db.entity.PracticeQuestion(
                            id = q.optLong("id", 0),
                            exerciseId = q.optLong("exercise_id", 0),
                            questionType = q.optInt("question_type", 1),
                            content = q.optString("content", ""),
                            optionA = q.optString("option_a", ""),
                            optionB = q.optString("option_b", ""),
                            optionC = q.optString("option_c", ""),
                            optionD = q.optString("option_d", ""),
                            correctAnswer = q.optString("correct_answer", ""),
                            explanation = q.optString("explanation", ""),
                            sortOrder = q.optInt("sort_order", 0)
                        )
                    )
                }
                db.practiceQuestionDao().insertAll(pqList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
