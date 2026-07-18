package com.linxia.exam.presentation.ui.screen.category

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
import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.presentation.ui.components.CategoryCard
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.CategoryViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import com.linxia.exam.domain.repository.CategoryRepository
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    categoryId: Long,
    onNavigateToPractice: (Long) -> Unit,
    onNavigateToSubCategory: (Long) -> Unit
) {
    val viewModel: CategoryViewModel = viewModel()
    val categoryTree by viewModel.categoryTree
    val children = if (categoryId == 0L) {
        categoryTree.flatMap { it.children }
    } else {
        categoryTree.flatMap { it.children }.flatMap { it.children }.find { it.category.id == categoryId }?.children ?: emptyList()
    }
    val currentCategory = categoryTree.flatMap { it.children }.flatMap { it.children }.find { it.category.id == categoryId }?.category

    LinxiaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentCategory?.name ?: "全部分类", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = { /* navigate back */ }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (children.isEmpty() && categoryId != 0L) {
                // 叶子分类 - 显示题目列表或练习入口
                LeafCategoryScreen(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    category = currentCategory!!,
                    onNavigateToPractice = onNavigateToPractice
                )
            } else {
                // 显示子分类列表
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(children) { node ->
                        CategoryCard(
                            category = node.category,
                            questionCount = node.category.questionCount,
                            onClick = {
                                if (node.children.isEmpty()) {
                                    onNavigateToPractice(node.category.id)
                                } else {
                                    onNavigateToSubCategory(node.category.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeafCategoryScreen(
    modifier: Modifier,
    category: Category,
    onNavigateToPractice: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // 分类图标
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(category.icon),
                contentDescription = null,
                tint = if (category.color.isNotBlank()) Color(category.color) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${category.questionCount} 道题目",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 快捷操作按钮
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onNavigateToPractice(category.id) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("开始练习", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = { /* 随机练习 */ },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("随机练习", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = { /* 章节测试 */ },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("章节测试", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun CategoryListScreen(
    modifier: Modifier,
    categories: List<Category>,
    onItemClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                questionCount = category.questionCount,
                onClick = { onItemClick(category.id) }
            )
        }
    }
}

fun getCategoryIcon(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "politics" -> Icons.Default.Gavel
        "law" -> Icons.Default.Scale
        "history" -> Icons.Default.MenuBook
        "geography" -> Icons.Default.Public
        "economy" -> Icons.Default.AttachMoney
        "science" -> Icons.Default.Science
        "culture" -> Icons.Default.TheaterComedy
        "local" -> Icons.Default.LocationCity
        "ethnic" -> Icons.Default.Diversity3
        "religion" -> Icons.Default.TempleBuddhist
        "tourism" -> Icons.Default.LocalAttraction
        "ecology" -> Icons.Default.Eco
        "governance" -> Icons.Default.AccountBalance
        "livelihood" -> Icons.Default.Home
        else -> Icons.Default.Quiz
    }
}