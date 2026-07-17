package com.linxia.exam.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LinxiaAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackPressed: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
        navigationIcon = {
            onBackPressed?.let {
                IconButton(onClick = it) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}

@Composable
fun LinxiaBottomNavBar(
    selectedItem: Int,
    onItemClick: (Int) -> Unit,
    items: List<BottomNavItem>
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 12.sp) },
                selected = selectedItem == index,
                onClick = { onItemClick(index) },
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

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun CategoryCard(
    category: com.linxia.exam.data.db.entity.Category,
    questionCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.icon),
                    contentDescription = null,
                    tint = if (category.color.isNotBlank()) Color(category.color) else MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.overflow.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$questionCount 道题目",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun QuestionCard(
    question: com.linxia.exam.data.db.entity.Question,
    index: Int,
    total: Int,
    userAnswer: String?,
    showExplanation: Boolean,
    onOptionClick: (String) -> Unit,
    onCollectClick: () -> Unit,
    onWrongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                userAnswer != null && userAnswer == question.correctAnswer -> MaterialTheme.colorScheme.primaryContainer
                userAnswer != null && userAnswer != question.correctAnswer -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "题目 $index / $total",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCollectClick) {
                        Icon(
                            imageVector = if (question.isCollected == 1) Icons.Default.Bookmark else Icons.Default.BookmarkOutline,
                            contentDescription = "收藏",
                            tint = if (question.isCollected == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onWrongClick) {
                        Icon(
                            imageVector = if (question.isWrong == 1) Icons.Default.Flag else Icons.Default.FlagOutlined,
                            contentDescription = "标记错题",
                            tint = if (question.isWrong == 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            question.getOptions().forEachIndexed { i, option ->
                val label = question.getOptionLabels()[i]
                val isSelected = userAnswer?.contains(label) == true
                val isCorrect = question.correctAnswer.contains(label)

                val optionColor = when {
                    showExplanation && isCorrect -> MaterialTheme.colorScheme.primary
                    showExplanation && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }

                val backgroundColor = when {
                    showExplanation && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                    showExplanation && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(backgroundColor, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$label. $option",
                        fontSize = 14.sp,
                        color = optionColor
                    )
                    if (showExplanation) {
                        when {
                            isCorrect -> Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "正确",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            isSelected -> Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "错误",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.RadioButtonChecked,
                            contentDescription = "已选",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = "未选",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (showExplanation && question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "解析",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "正确答案: ${question.getCorrectLabels().joinToString("、")}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = question.explanation,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
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