package com.linxia.exam.presentation.ui.screen.wrongbook

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
import com.linxia.exam.data.db.entity.WrongQuestion
import com.linxia.exam.domain.repository.WrongQuestionRepository
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.WrongBookViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WrongBookScreen(
    modifier: Modifier = Modifier,
    onNavigateToPractice: (Long) -> Unit,
    onNavigateToQuestion: (Long) -> Unit
) {
    val viewModel: WrongBookViewModel = viewModel()
    val filterStatus = viewModel.filterStatus.collectAsStateWithLifecycle().value
    val filterCategory = viewModel.filterCategory.collectAsStateWithLifecycle().value
    val filteredQuestions = viewModel.filteredQuestions.collectAsStateWithLifecycle().value ?: emptyList()
    val totalCount = viewModel.totalCount.collectAsStateWithLifecycle().value ?: 0
    val unmasteredCount = viewModel.unmasteredCount.collectAsStateWithLifecycle().value ?: 0
    val masteredCount = viewModel.masteredCount.collectAsStateWithLifecycle().value ?: 0

    LinxiaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("错题本", fontWeight = FontWeight.Bold) },
                    actions = {
                        // 筛选菜单
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 统计卡片
                WrongBookStatsCard(
                    total = totalCount,
                    unmastered = unmasteredCount,
                    mastered = masteredCount
                )

                // 筛选标签
                FilterChips(
                    currentStatus = filterStatus,
                    onStatusChange = { viewModel.setFilterStatus(it) },
                    currentCategoryId = filterCategory,
                    onCategoryChange = { viewModel.setFilterCategory(it) }
                )

                // 错题列表
                if (filteredQuestions.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Flag,
                        title = "暂无错题",
                        subtitle = "继续练习，错题会自动收集到这里"
                    )
                } else {
                    WrongQuestionList(
                        wrongQuestions = filteredQuestions,
                        onNavigateToPractice = onNavigateToPractice,
                        onNavigateToQuestion = onNavigateToQuestion,
                        onMarkMastered = { viewModel.markAsMastered(it) },
                        onMarkReviewing = { viewModel.markAsReviewing(it) },
                        onRemove = { viewModel.removeWrongQuestion(it.questionId) }
                    )
                }
            }
        }
    }
}

@Composable
fun WrongBookStatsCard(total: Int, unmastered: Int, mastered: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("总错题", total, MaterialTheme.colorScheme.error)
            Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.fillMaxHeight().width(1.dp))
            StatItem("未掌握", unmastered, MaterialTheme.colorScheme.error)
            Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.fillMaxHeight().width(1.dp))
            StatItem("复习中", total - unmastered - mastered, MaterialTheme.colorScheme.tertiary)
            Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.fillMaxHeight().width(1.dp))
            StatItem("已掌握", mastered, MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun FilterChips(
    currentStatus: WrongBookViewModel.FilterStatus,
    onStatusChange: (WrongBookViewModel.FilterStatus) -> Unit,
    currentCategoryId: Long,
    onCategoryChange: (Long) -> Unit
) {
    SingleLineScrollRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WrongBookViewModel.FilterStatus.values().forEach { status ->
                FilterChip(
                    selected = currentStatus == status,
                    onClick = { onStatusChange(status) },
                    label = { Text(status.label) }
                )
            }
        }
    }
}

@Composable
fun WrongQuestionList(
    wrongQuestions: List<WrongQuestionRepository.WrongQuestionWithDetail>,
    onNavigateToPractice: (Long) -> Unit,
    onNavigateToQuestion: (Long) -> Unit,
    onMarkMastered: (WrongQuestion) -> Unit,
    onMarkReviewing: (WrongQuestion) -> Unit,
    onRemove: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(wrongQuestions) { item ->
            WrongQuestionItem(
                item = item,
                onClick = { onNavigateToQuestion(item.wrongQuestion.questionId) },
                onPracticeClick = { onNavigateToPractice(item.wrongQuestion.categoryId) },
                onMarkMastered = { onMarkMastered(item.wrongQuestion) },
                onMarkReviewing = { onMarkReviewing(item.wrongQuestion) },
                onRemove = { onRemove(item.wrongQuestion.questionId) }
            )
        }
    }
}

@Composable
fun WrongQuestionItem(
    item: WrongQuestionRepository.WrongQuestionWithDetail,
    onClick: () -> Unit,
    onPracticeClick: () -> Unit,
    onMarkMastered: () -> Unit,
    onMarkReviewing: () -> Unit,
    onRemove: () -> Unit
) {
    val wrongQuestion = item.wrongQuestion
    val question = item.question
    val category = item.category

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).padding(end = 12.dp), contentAlignment = Alignment.Center) {
                        when (wrongQuestion.masteryStatus) {
                            WrongQuestion.STATUS_MASTERED -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            WrongQuestion.STATUS_REVIEWING -> Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            else -> Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    Column {
                        Text(question?.content ?: "题目已删除", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (category != null) {
                            Text(category.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 状态标签
                when (wrongQuestion.masteryStatus) {
                    WrongQuestion.STATUS_MASTERED -> {
                        Chip(onClick = { /* already mastered */ }) {
                            Text("已掌握", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    WrongQuestion.STATUS_REVIEWING -> {
                        Chip(onClick = { /* already reviewing */ }) {
                            Text("复习中", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                    else -> {
                        OutlinedButton(onClick = onMarkReviewing, modifier = Modifier.weight(1f)) {
                            Text("标记复习中", style = MaterialTheme.typography.labelSmall)
                        }
                        Button(onClick = onMarkMastered, modifier = Modifier.weight(1f)) {
                            Text("标记掌握", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "移除", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun StatItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}