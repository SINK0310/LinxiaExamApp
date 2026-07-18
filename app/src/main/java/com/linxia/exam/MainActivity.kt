package com.linxia.exam

import android.os.Bundle
import com.google.gson.Gson
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.linxia.exam.presentation.ui.screen.category.CategoryScreen
import com.linxia.exam.presentation.ui.screen.bookmark.BookmarkScreen
import com.linxia.exam.presentation.ui.screen.exam.ExamScreen
import com.linxia.exam.presentation.ui.screen.home.HomeScreen
import com.linxia.exam.presentation.ui.screen.practice.PracticeScreen
import com.linxia.exam.presentation.ui.screen.progress.ProgressScreen
import com.linxia.exam.presentation.ui.screen.settings.SettingsScreen
import com.linxia.exam.presentation.ui.screen.wrongbook.WrongBookScreen
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.CategoryViewModel
import com.linxia.exam.presentation.viewmodel.ExamViewModel
import com.linxia.exam.presentation.viewmodel.ProgressViewModel
import com.linxia.exam.presentation.viewmodel.QuestionViewModel
import com.linxia.exam.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.getLong
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val questionViewModel: QuestionViewModel by viewModel()
    private val examViewModel: ExamViewModel by viewModel()
    private val progressViewModel: ProgressViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinxiaTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        categoryViewModel = categoryViewModel,
                        questionViewModel = questionViewModel,
                        examViewModel = examViewModel,
                        progressViewModel = progressViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: androidx.navigation.NavController,
    categoryViewModel: CategoryViewModel,
    questionViewModel: QuestionViewModel,
    examViewModel: ExamViewModel,
    progressViewModel: ProgressViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                onNavigateToCategory = { categoryId ->
                    if (categoryId > 0) {
                        navController.navigate("category/$categoryId")
                    } else {
                        navController.navigate("categories")
                    }
                },
                onNavigateToPractice = { categoryId ->
                    questionViewModel.setCategory(categoryId)
                    questionViewModel.setPracticeMode(QuestionViewModel.PracticeMode.CHAPTER)
                    navController.navigate("practice")
                },
                onNavigateToExam = {
                    navController.navigate("exam")
                },
                onNavigateToWrongBook = {
                    navController.navigate("wrongbook")
                },
                onNavigateToCollection = {
                    navController.navigate("collection")
                },
                onNavigateToProgress = {
                    navController.navigate("progress")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable(
            route = "category/{categoryId}",
            arguments = listOf(androidx.navigation.navArgument("categoryId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.getLong("categoryId")
            CategoryScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                categoryId = categoryId,
                onNavigateToPractice = { catId ->
                    questionViewModel.setCategory(catId)
                    questionViewModel.setPracticeMode(QuestionViewModel.PracticeMode.CHAPTER)
                    navController.navigate("practice")
                },
                onNavigateToSubCategory = { catId ->
                    navController.navigate("category/$catId")
                }
            )
        }

        composable("categories") {
            CategoryScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                categoryId = 0,
                onNavigateToPractice = { catId ->
                    questionViewModel.setCategory(catId)
                    questionViewModel.setPracticeMode(QuestionViewModel.PracticeMode.CHAPTER)
                    navController.navigate("practice")
                },
                onNavigateToSubCategory = { catId ->
                    navController.navigate("category/$catId")
                }
            )
        }

        composable("practice") {
            PracticeScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                onNavigateBack = { navController.popBackStack() },
                onFinishPractice = { /* save record */ }
            )
        }

        composable("exam") {
            ExamScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                onStartExam = { type, categories, count, duration ->
                    examViewModel.setExamType(type)
                    examViewModel.setSelectedCategories(categories)
                    examViewModel.setQuestionCount(count)
                    examViewModel.setExamDuration(duration)
                    examViewModel.startExam()
                    navController.navigate("exam_running")
                },
                onViewExamDetail = { record ->
                    // TODO: show exam detail
                }
            )
        }

        composable("exam_running") {
            // Exam running screen
            Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text("考试进行中...")
            }
        }

        composable("wrongbook") {
            WrongBookScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                onNavigateToPractice = { categoryId ->
                    questionViewModel.setCategory(categoryId)
                    questionViewModel.setPracticeMode(QuestionViewModel.PracticeMode.WRONG_REVIEW)
                    navController.navigate("practice")
                },
                onNavigateToQuestion = { questionId ->
                    // TODO: show question detail
                }
            )
        }

        composable("collection") {
            BookmarkScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                onNavigateToPractice = { categoryId ->
                    questionViewModel.setCategory(categoryId)
                    questionViewModel.setPracticeMode(QuestionViewModel.PracticeMode.COLLECTION)
                    navController.navigate("practice")
                },
                onNavigateToQuestion = { questionId ->
                    // TODO: show question detail
                }
            )
        }

        composable("progress") {
            ProgressScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize()
            )
        }

        composable("settings") {
            SettingsScreen(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                onLogout = { /* logout */ }
            )
        }
    }
}