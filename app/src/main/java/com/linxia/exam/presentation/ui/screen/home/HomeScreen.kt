package com.linxia.exam.presentation.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linxia.exam.presentation.ui.components.CategoryCard
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.CategoryViewModel
import com.linxia.exam.presentation.viewmodel.ProgressViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToCategory: (Long) -> Unit,
    onNavigateToPractice: (Long) -> Unit,
    onNavigateToExam: () -> Unit,
    onNavigateToWrongBook: () -> Unit,
    onNavigateToCollection: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val categoryViewModel: CategoryViewModel = viewModel()
    val progressViewModel: ProgressViewModel = viewModel()

    val categoryTree by categoryViewModel.categoryTree
    val totalPracticed by progressViewModel.totalPracticed
    val totalCorrect by progressViewModel.totalCorrect
    val totalWrong by progressViewModel.totalWrong
    val accuracyRate = if (totalPracticed > 0) (totalCorrect.toDouble() / totalPracticed * 100) else 0.0

    LinxiaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("临夏事业编刷题", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "设置")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            bottomBar = {
                BottomNavBar(onNavigateToPractice, onNavigateToExam, onNavigateToWrongBook, onNavigateToCollection, onNavigateToProgress)
            }
        ) { paddingValues ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 学习进度卡片
                    ProgressCard(
                        totalPracticed = totalPracticed,
                        correctCount = totalCorrect,
                        wrongCount = totalWrong,
                        accuracyRate = accuracyRate
                    )

                    // 快捷入口
                    QuickActionsCard(
                        onPracticeClick = onNavigateToPractice,
                        onExamClick = onNavigateToExam,
                        onWrongBookClick = onNavigateToWrongBook,
                        onCollectionClick = onNavigateToCollection
                    )

                    // 推荐分类
                    Text("推荐分类", style = MaterialTheme.typography.headlineSmall)
                    LazyVerticalGrid(
                        cells = GridCells.Fixed(2),
                        contentPadding = PaddingValues(0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categoryTree.take(6).flatMap { it.children.take(2) }.take(6)) { node ->
                            val progress = progressViewModel.getProgressForCategory(node.category.id)
                            CategoryCard(
                                category = node.category,
                                questionCount = progress?.practicedCount ?: node.category.questionCount,
                                onClick = { onNavigateToCategory(node.category.id) }
                            )
                        }
                    }

                    // 全部分类入口
                    TextButton(
                        onClick = { onNavigateToCategory(0) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("查看全部分类", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressCard(
    totalPracticed: Int,
    correctCount: Int,
    wrongCount: Int,
    accuracyRate: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("今日学习进度", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressItem("总练习", "$totalPracticed", "道")
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
                ProgressItem("正确", "$correctCount", "道")
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
                ProgressItem("错误", "$wrongCount", "道")
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxHeight().width(1.dp))
                ProgressItem("正确率", String.format("%.1f", accuracyRate), "%")
            }
        }
    }
}

@Composable
fun ProgressItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(text = unit, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
    }
}

@Composable
fun QuickActionsCard(
    onPracticeClick: () -> Unit,
    onExamClick: () -> Unit,
    onWrongBookClick: () -> Unit,
    onCollectionClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionItem(Icons.Default.Quiz, "章节练习", onPracticeClick)
                QuickActionItem(Icons.Default.Timer, "模拟考试", onExamClick)
                QuickActionItem(Icons.Default.Flag, "错题本", onWrongBookClick)
                QuickActionItem(Icons.Default.Bookmark, "收藏夹", onCollectionClick)
            }
        }
    }
}

@Composable
fun QuickActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 16.dp)
            .background(Color.Transparent)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun BottomNavBar(
    onPracticeClick: () -> Unit,
    onExamClick: () -> Unit,
    onWrongBookClick: () -> Unit,
    onCollectionClick: () -> Unit,
    onProgressClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        val items = listOf(
            NavigationBarItemData(Icons.Default.Quiz, "练习", onPracticeClick),
            NavigationBarItemData(Icons.Default.Timer, "考试", onExamClick),
            NavigationBarItemData(Icons.Default.Flag, "错题", onWrongBookClick),
            NavigationBarItemData(Icons.Default.Bookmark, "收藏", onCollectionClick),
            NavigationBarItemData(Icons.Default.Leaderboard, "进度", onProgressClick)
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label, fontSize = 10.sp) },
                selected = false,
                onClick = item.onClick,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class NavigationBarItemData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val onClick: () -> Unit
)