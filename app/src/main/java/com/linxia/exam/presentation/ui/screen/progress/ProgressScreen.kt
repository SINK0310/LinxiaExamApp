package com.linxia.exam.presentation.ui.screen.progress

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
import com.linxia.exam.data.db.entity.PracticeRecord
import com.linxia.exam.data.db.entity.UserProgress
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.ProgressViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import com.linxia.exam.data.db.entity.ExamRecord
import com.linxia.exam.domain.repository.PracticeRecordRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProgressScreen(modifier: Modifier = Modifier) {
    val viewModel: ProgressViewModel = viewModel()

    val allProgress by viewModel.allProgress
    val totalPracticed by viewModel.totalPracticed
    val totalCorrect by viewModel.totalCorrect
    val totalWrong by viewModel.totalWrong
    val practicedCategoriesCount by viewModel.practicedCategoriesCount
    val recentRecords by viewModel.recentRecords
    val recentExams by viewModel.recentExams
    val averageScore by viewModel.averageScore
    val highestScore by viewModel.highestScore
    val examCount by viewModel.examCount
    val chapterPracticeCount by viewModel.chapterPracticeCount
    val chapterCorrectCount by viewModel.chapterCorrectCount
    val examPracticeCount by viewModel.examPracticeCount
    val examCorrectCount by viewModel.examCorrectCount
    val wrongReviewCount by viewModel.wrongReviewCount
    val wrongReviewCorrectCount by viewModel.wrongReviewCorrectCount
    val collectionPracticeCount by viewModel.collectionPracticeCount
    val collectionCorrectCount by viewModel.collectionCorrectCount
    val mostWrongQuestions by viewModel.mostWrongQuestions
    val timeRange by viewModel.timeRange

    LinxiaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("学习进度", fontWeight = FontWeight.Bold) },
                    actions = {
                        TextButton(onClick = { /* time range selector */ }) { Text("时间范围") }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 总体统计卡片
                OverallStatsCard(
                    totalPracticed = totalPracticed,
                    totalCorrect = totalCorrect,
                    totalWrong = totalWrong,
                    accuracyRate = if (totalPracticed > 0) (totalCorrect.toDouble() / totalPracticed * 100) else 0.0,
                    practicedCategories = practicedCategoriesCount
                )

                // 各模式统计
                ModeStatsCard(
                    chapterPracticed = chapterPracticeCount,
                    chapterCorrect = chapterCorrectCount,
                    examPracticed = examPracticeCount,
                    examCorrect = examCorrectCount,
                    wrongPracticed = wrongReviewCount,
                    wrongCorrect = wrongReviewCorrectCount,
                    collectionPracticed = collectionPracticeCount,
                    collectionCorrect = collectionCorrectCount
                )

                // 考试统计
                ExamStatsCard(
                    examCount = examCount,
                    averageScore = averageScore,
                    highestScore = highestScore
                )

                // 分类进度
                CategoryProgressSection(
                    progressList = allProgress,
                    onItemClick = { /* navigate to category */ }
                )

                // 近期练习记录
                RecentActivitySection(
                    records = recentRecords,
                    onItemClick = { /* navigate to detail */ }
                )

                // 近期考试记录
                RecentExamsSection(
                    exams = recentExams,
                    onItemClick = { /* navigate to detail */ }
                )

                // 高频错题
                MostWrongQuestionsSection(
                    wrongQuestions = mostWrongQuestions,
                    onItemClick = { /* navigate to wrong book */ }
                )
            }
        }
    }
}

@Composable
fun OverallStatsCard(
    totalPracticed: Int,
    totalCorrect: Int,
    totalWrong: Int,
    accuracyRate: Double,
    practicedCategories: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("总体学习情况", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BigStatItem("总练习", totalPracticed.toString(), "道")
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
                BigStatItem("正确", totalCorrect.toString(), "道", MaterialTheme.colorScheme.primary)
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
                BigStatItem("错误", totalWrong.toString(), "道", MaterialTheme.colorScheme.error)
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
                BigStatItem("正确率", String.format("%.1f%%", accuracyRate), "", MaterialTheme.colorScheme.tertiary)
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
                BigStatItem("已练分类", practicedCategories.toString(), "个")
            }
        }
    }
}

@Composable
fun BigStatItem(label: String, value: String, unit: String, color: Color = MaterialTheme.colorScheme.onPrimaryContainer) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = unit, fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
    }
}

@Composable
fun ModeStatsCard(
    chapterPracticed: Int, chapterCorrect: Int,
    examPracticed: Int, examCorrect: Int,
    wrongPracticed: Int, wrongCorrect: Int,
    collectionPracticed: Int, collectionCorrect: Int
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("练习模式统计", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ModeStatItem("章节练习", chapterPracticed, chapterCorrect)
                ModeStatItem("模拟考试", examPracticed, examCorrect)
                ModeStatItem("错题复习", wrongPracticed, wrongCorrect)
                ModeStatItem("收藏练习", collectionPracticed, collectionCorrect)
            }
        }
    }
}

@Composable
fun ModeStatItem(label: String, total: Int, correct: Int) {
    val accuracy = if (total > 0) (correct.toDouble() / total * 100) else 0.0
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text("$correct / $total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(String.format("%.1f%%", accuracy), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ExamStatsCard(examCount: Int, averageScore: Double, highestScore: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("考试表现", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ExamStatItem("考试次数", examCount.toString(), "次")
                Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.fillMaxHeight().width(1.dp))
                ExamStatItem("平均分", String.format("%.1f", averageScore), "分")
                Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.fillMaxHeight().width(1.dp))
                ExamStatItem("最高分", String.format("%.1f", highestScore), "分")
            }
        }
    }
}

@Composable
fun ExamStatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CategoryProgressSection(
    progressList: List<UserProgress>,
    onItemClick: (UserProgress) -> Unit
) {
    val practicedList = progressList.filter { it.practicedCount > 0 }
        .sortedByDescending { it.lastPracticeTime }
        .take(6)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("分类掌握度", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, top = 16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(practicedList) { progress ->
                CategoryProgressItem(progress = progress, onClick = { onItemClick(progress) })
            }
        }
    }
}

@Composable
fun CategoryProgressItem(progress: UserProgress, onClick: () -> Unit) {
    val masteryLevel = progress.masteryLevel
    val masteryColor = when {
        masteryLevel >= 80 -> MaterialTheme.colorScheme.primary
        masteryLevel >= 60 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(progress.category?.name ?: "未知分类", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text("$masteryLevel%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = masteryColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = masteryLevel / 100f,
                color = masteryColor,
                trackColor = masteryColor.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("练习 ${progress.practicedCount} 题  |  正确 ${progress.correctCount}  |  错误 ${progress.wrongCount}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("正确率 ${if (progress.practicedCount > 0) String.format("%.1f%%", progress.correctCount.toDouble() / progress.practicedCount * 100) else "0%"}",
                    style = MaterialTheme.typography.bodySmall, color = masteryColor)
            }
        }
    }
}

@Composable
fun RecentActivitySection(
    records: List<PracticeRecord>,
    onItemClick: (PracticeRecord) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("近期练习", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, top = 16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(records.take(5)) { record ->
                PracticeRecordItem(record = record, onClick = { onItemClick(record) })
            }
        }
    }
}

@Composable
fun PracticeRecordItem(record: PracticeRecord, onClick: () -> Unit) {
    val date = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(record.practiceTime))
    val modeLabel = when (record.practiceMode) {
        1 -> "章节练习"
        2 -> "模拟考试"
        3 -> "错题复习"
        4 -> "收藏练习"
        else -> "未知"
    }
    val isCorrect = record.isCorrect == 1

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp).padding(end = 12.dp)
                )
                Column {
                    Text(record.question?.content?.take(30) ?: "题目已删除", style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                    Text("$date  $modeLabel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(if (isCorrect) "正确" else "错误",
                style = MaterialTheme.typography.labelMedium,
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RecentExamsSection(
    exams: List<ExamRecord>,
    onItemClick: (ExamRecord) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("近期考试", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, top = 16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(exams.take(5)) { exam ->
                ExamRecordItem(exam = exam, onClick = { onItemClick(exam) })
            }
        }
    }
}

@Composable
fun MostWrongQuestionsSection(
    wrongQuestions: List<PracticeRecordRepository.WrongQuestionStat>,
    onItemClick: (PracticeRecordRepository.WrongQuestionStat) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("高频错题", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, top = 16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(wrongQuestions.take(10)) { stat ->
                MostWrongItem(stat = stat, onClick = { onItemClick(stat) })
            }
        }
    }
}

@Composable
fun MostWrongItem(stat: PracticeRecordRepository.WrongQuestionStat, onClick: () -> Unit) {
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
                Text("题目 ID: ${stat.questionId}", style = MaterialTheme.typography.bodyMedium)
                Text("错误 ${stat.count} 次", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}