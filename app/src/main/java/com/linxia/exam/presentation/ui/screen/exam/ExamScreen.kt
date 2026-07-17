package com.linxia.exam.presentation.ui.screen.exam

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
import com.linxia.exam.data.db.entity.ExamRecord
import com.linxia.exam.domain.repository.CategoryRepository
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.ExamViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExamScreen(
    modifier: Modifier = Modifier,
    onStartExam: (ExamViewModel.ExamType, List<Long>, Int, Int) -> Unit,
    onViewExamDetail: (ExamRecord) -> Unit
) {
    val viewModel: ExamViewModel = viewModel()
    val examType = viewModel.examType.collectAsStateWithLifecycle().value
    val selectedCategories = viewModel.selectedCategories.collectAsStateWithLifecycle().value
    val questionCount = viewModel.questionCount.collectAsStateWithLifecycle().value
    val examDuration = viewModel.examDuration.collectAsStateWithLifecycle().value
    val isExamRunning = viewModel.isExamRunning.collectAsStateWithLifecycle().value
    val allExams = viewModel.allExams.collectAsStateWithLifecycle().value ?: emptyList()
    val recentExams = viewModel.recentExams.collectAsStateWithLifecycle().value ?: emptyList()
    val averageScore = viewModel.averageScore.collectAsStateWithLifecycle().value ?: 0.0
    val highestScore = viewModel.highestScore.collectAsStateWithLifecycle().value ?: 0.0
    val examCount = viewModel.examCount.collectAsStateWithLifecycle().value ?: 0

    LinxiaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("模拟考试", fontWeight = FontWeight.Bold) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isExamRunning) {
                    ExamRunningView(
                        examType = examType,
                        selectedCategories = selectedCategories,
                        questionCount = questionCount,
                        examDuration = examDuration,
                        onFinish = { /* finish exam */ },
                        onCancel = { viewModel.cancelExam() }
                    )
                } else {
                    ExamSetupView(
                        examType = examType,
                        onExamTypeChange = { viewModel.setExamType(it) },
                        selectedCategories = selectedCategories,
                        onCategoriesChange = { viewModel.setSelectedCategories(it) },
                        questionCount = questionCount,
                        onQuestionCountChange = { viewModel.setQuestionCount(it) },
                        examDuration = examDuration,
                        onDurationChange = { viewModel.setExamDuration(it) },
                        onStartExam = { viewModel.startExam() },
                        onStartExamCallback = { onStartExam(examType, selectedCategories, questionCount, examDuration) }
                    )

                    // 考试记录
                    if (recentExams.isNotEmpty()) {
                        ExamRecordsSection(
                            exams = recentExams,
                            onItemClick = onViewExamDetail
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExamSetupView(
    examType: ExamViewModel.ExamType,
    onExamTypeChange: (ExamViewModel.ExamType) -> Unit,
    selectedCategories: List<Long>,
    onCategoriesChange: (List<Long>) -> Unit,
    questionCount: Int,
    onQuestionCountChange: (Int) -> Unit,
    examDuration: Int,
    onDurationChange: (Int) -> Unit,
    onStartExam: () -> Unit,
    onStartExamCallback: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 考试类型选择
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("考试类型", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExamViewModel.ExamType.values().forEach { type ->
                        val selected = examType == type
                        OutlinedButton(
                            onClick = { onExamTypeChange(type) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(type.label)
                        }
                    }
                }
            }
        }

        // 分类选择
        if (examType != ExamViewModel.ExamType.FULL_MOCK) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("考试范围", style = MaterialTheme.typography.titleMedium)
                        Text("${selectedCategories.size} 个分类", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { /* show category picker */ }) {
                        Text(selectedCategories.joinToString(", ") { it.toString() })
                    }
                }
            }
        }

        // 题目数量
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("题目数量", style = MaterialTheme.typography.titleMedium)
                    Text("$questionCount 道", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = questionCount.toFloat(),
                    onValueChange = { onQuestionCountChange(it.toInt()) },
                    valueRange = 10f..200f,
                    steps = 19
                )
            }
        }

        // 考试时长
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("考试时长", style = MaterialTheme.typography.titleMedium)
                    Text("$examDuration 分钟", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = examDuration.toFloat(),
                    onValueChange = { onDurationChange(it.toInt()) },
                    valueRange = 30f..240f,
                    steps = 7
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 开始考试按钮
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            TextButton(
                onClick = { onStartExam(); onStartExamCallback() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("开始考试", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 统计信息
        ExamStatsCard(
            examCount = examCount,
            averageScore = averageScore,
            highestScore = highestScore
        )
    }
}

@Composable
fun ExamRunningView(
    examType: ExamViewModel.ExamType,
    selectedCategories: List<Long>,
    questionCount: Int,
    examDuration: Int,
    onFinish: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("考试进行中...", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(120.dp),
            strokeWidth = 12.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("${examType.label} | $questionCount 道题 | $examDuration 分钟", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onCancel) { Text("放弃考试") }
            FilledButton(onClick = onFinish) { Text("交卷") }
        }
    }
}

@Composable
fun ExamStatsCard(examCount: Int, averageScore: Double, highestScore: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("考试次数", examCount.toString(), MaterialTheme.colorScheme.onPrimaryContainer)
            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
            StatItem("平均分", String.format("%.1f", averageScore), MaterialTheme.colorScheme.onPrimaryContainer)
            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
            StatItem("最高分", String.format("%.1f", highestScore), MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun ExamRecordsSection(
    exams: List<ExamRecord>,
    onItemClick: (ExamRecord) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("近期考试记录", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, top = 16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(exams) { exam ->
                ExamRecordItem(exam = exam, onClick = { onItemClick(exam) })
            }
        }
    }
}

@Composable
fun ExamRecordItem(exam: ExamRecord, onClick: () -> Unit) {
    val date = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(exam.endTime))
    val scoreColor = when {
        exam.score >= 90 -> MaterialTheme.colorScheme.primary
        exam.score >= 60 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(exam.examTypeLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.1f", exam.score),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
                Text("${exam.correctCount}/${exam.totalQuestions}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

val ExamViewModel.ExamType.label: String
    get() = when (this) {
        ExamViewModel.ExamType.FULL_MOCK -> "全真模拟"
        ExamViewModel.ExamType.CHAPTER_TEST -> "章节测试"
        ExamViewModel.ExamType.SPECIAL_TOPIC -> "专项突破"
    }

val ExamRecord.examTypeLabel: String
    get() = when (examType) {
        1 -> "全真模拟"
        2 -> "章节测试"
        3 -> "专项突破"
        else -> "未知"
    }