package com.linxia.exam.presentation.ui.screen.practice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linxia.exam.data.db.entity.Question
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.PracticeViewModel
import com.linxia.exam.presentation.viewmodel.QuestionViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PracticeScreen(
    modifier: Modifier = Modifier,
    categoryId: Long,
    mode: PracticeViewModel.PracticeMode,
    onFinish: () -> Unit
) {
    val practiceViewModel: PracticeViewModel = viewModel()
    val questionViewModel: QuestionViewModel = viewModel()

    val currentQuestionIndex by practiceViewModel.currentQuestionIndex
    val questions by practiceViewModel.questions
    val userAnswers by practiceViewModel.userAnswers
    val showExplanation by practiceViewModel.showExplanation
    val isFinished by practiceViewModel.isFinished
    val currentQuestion by practiceViewModel.currentQuestion
    val progress by practiceViewModel.progress
    val correctCount by practiceViewModel.correctCount

    LaunchedEffect(categoryId) {
        // 加载题目
        if (mode == PracticeViewModel.PracticeMode.CHAPTER) {
            // 章节练习 - 顺序
        } else if (mode == PracticeViewModel.PracticeMode.MOCK_EXAM) {
            // 模拟考试 - 随机
            questionViewModel.setShuffle(true)
        }
        questionViewModel.setCategory(categoryId)
    }

    LinxiaTheme {
        if (questions.isEmpty()) {
            LoadingScreen(message = "加载题目中...")
        } else if (isFinished) {
            ResultScreen(
                totalQuestions = questions.size,
                correctCount = correctCount,
                onReviewWrong = { /* 复习错题 */ },
                onRetry = { /* 重新练习 */ },
                onFinish = onFinish
            )
        } else {
            PracticeContentScreen(
                currentQuestion = currentQuestion!!,
                questionIndex = currentQuestionIndex,
                totalQuestions = questions.size,
                userAnswer = userAnswers[currentQuestion!!.id],
                showExplanation = showExplanation,
                progress = progress,
                onOptionClick = { option -> practiceViewModel.answerQuestion(currentQuestion!!.id, option) },
                onNext = { practiceViewModel.nextQuestion() },
                onPrevious = { practiceViewModel.previousQuestion() },
                onJump = { index -> practiceViewModel.jumpToQuestion(index) },
                onToggleExplanation = { practiceViewModel.toggleExplanation() },
                onCollect = { questionViewModel.toggleCollected(currentQuestion!!.id, true) },
                onMarkWrong = { questionViewModel.toggleWrong(currentQuestion!!.id, true) }
            )
        }
    }
}

@Composable
fun LoadingScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PracticeContentScreen(
    currentQuestion: Question,
    questionIndex: Int,
    totalQuestions: Int,
    userAnswer: String?,
    showExplanation: Boolean,
    progress: Int,
    onOptionClick: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onJump: (Int) -> Unit,
    onToggleExplanation: () -> Unit,
    onCollect: () -> Unit,
    onMarkWrong: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("练习中", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { /* show exit dialog */ }) {
                        Icon(Icons.Default.Close, contentDescription = "退出")
                    }
                },
                actions = {
                    // 进度条
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .padding(end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$questionIndex / $totalQuestions", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            )
        },
        bottomBar = {
            // 底部导航
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onPrevious, enabled = questionIndex > 0) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "上一题")
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (0 until totalQuestions).forEach { index ->
                            val isCurrent = index == questionIndex
                            val isAnswered = userAnswer != null
                            val isCorrect = isAnswered && userAnswer == currentQuestion.correctAnswer

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        color = when {
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            isAnswered && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                                            isAnswered -> MaterialTheme.colorScheme.errorContainer
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                                    .clickable { onJump(index) }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when {
                                        isCurrent -> MaterialTheme.colorScheme.onPrimary
                                        isAnswered && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
                                        isAnswered -> MaterialTheme.colorScheme.onErrorContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = onNext, enabled = questionIndex < totalQuestions - 1) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "下一题")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 进度条
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )

                // 题目卡片
                QuestionCard(
                    question = currentQuestion,
                    index = questionIndex + 1,
                    total = totalQuestions,
                    userAnswer = userAnswer,
                    showExplanation = showExplanation,
                    onOptionClick = onOptionClick,
                    onCollectClick = onCollect,
                    onWrongClick = onMarkWrong
                )

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleExplanation,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (showExplanation) "隐藏解析" else "显示解析")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultScreen(
    totalQuestions: Int,
    correctCount: Int,
    onReviewWrong: () -> Unit,
    onRetry: () -> Unit,
    onFinish: () -> Unit
) {
    val accuracy = if (totalQuestions > 0) (correctCount.toDouble() / totalQuestions * 100) else 0.0

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 分数圆环
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = accuracy / 100f,
                    strokeWidth = 16.dp,
                    color = when {
                        accuracy >= 90 -> MaterialTheme.colorScheme.primary
                        accuracy >= 60 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(200.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%.0f%%", accuracy),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("$correctCount / $totalQuestions 正确", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 等级
            Text(
                text = when {
                    accuracy >= 90 -> "优秀"
                    accuracy >= 80 -> "良好"
                    accuracy >= 60 -> "及格"
                    else -> "需加强"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    accuracy >= 90 -> MaterialTheme.colorScheme.primary
                    accuracy >= 60 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                }
            )

            // 操作按钮
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onRetry, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("重新练习", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onReviewWrong, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("复习错题", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onFinish, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("结束练习", fontSize = 16.sp)
                }
            }
        }
    }
}