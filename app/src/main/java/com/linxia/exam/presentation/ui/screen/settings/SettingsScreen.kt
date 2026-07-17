package com.linxia.exam.presentation.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linxia.exam.presentation.ui.theme.LinxiaTheme
import com.linxia.exam.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.viewmodel.compose.viewModel

@AndroidEntryPoint
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel()

    val nightMode by viewModel.nightMode
    val fontSize by viewModel.fontSize
    val autoNext by viewModel.autoNext
    val showExplanation by viewModel.showExplanation
    val questionOrder by viewModel.questionOrder
    val examDuration by viewModel.examDuration
    val dailyGoal by viewModel.dailyGoal
    val notificationEnabled by viewModel.notificationEnabled
    val soundEnabled by viewModel.soundEnabled
    val vibrationEnabled by viewModel.vibrationEnabled
    val offlineEnabled by viewModel.offlineEnabled

    LinxiaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("设置", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) }
                )
            }
        ) { paddingValues ->
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 外观设置
                item {
                    SettingsSection(title = "外观") {
                        SettingsRow(
                            label = "夜间模式",
                            subtitle = "跟随系统/手动开启",
                            trailing = {
                                Switch(checked = nightMode, onCheckedChange = { viewModel.setNightMode(it) })
                            }
                        )
                        SettingsRow(
                            label = "字体大小",
                            subtitle = "${fontSize.toInt()}sp",
                            trailing = {
                                Slider(
                                    value = fontSize,
                                    onValueChange = { viewModel.setFontSize(it) },
                                    valueRange = 12f..24f,
                                    steps = 12,
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        )
                    }
                }

                // 练习设置
                item {
                    SettingsSection(title = "练习设置") {
                        SettingsRow(
                            label = "自动下一题",
                            subtitle = "答题后自动跳转下一题",
                            trailing = {
                                Switch(checked = autoNext, onCheckedChange = { viewModel.setAutoNext(it) })
                            }
                        )
                        SettingsRow(
                            label = "答题后显示解析",
                            subtitle = "自动显示正确答案和解析",
                            trailing = {
                                Switch(checked = showExplanation, onCheckedChange = { viewModel.setShowExplanation(it) })
                            }
                        )
                        SettingsRow(
                            label = "题目顺序",
                            subtitle = if (questionOrder == 1) "顺序练习" else "随机练习",
                            trailing = {
                                Text(if (questionOrder == 1) "顺序练习" else "随机练习",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            },
                            onClick = { /* show order picker */ }
                        )
                    }
                }

                // 考试设置
                item {
                    SettingsSection(title = "考试设置") {
                        SettingsRow(
                            label = "模拟考试时长",
                            subtitle = "$examDuration 分钟",
                            trailing = {
                                Text("$examDuration 分钟",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            },
                            onClick = { /* show duration picker */ }
                        )
                        SettingsRow(
                            label = "每日目标",
                            subtitle = "$dailyGoal 道题",
                            trailing = {
                                Text("$dailyGoal 道",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            },
                            onClick = { /* show goal picker */ }
                        )
                    }
                }

                // 通知与声音
                item {
                    SettingsSection(title = "通知与声音") {
                        SettingsRow(
                            label = "每日提醒",
                            subtitle = "每天定时提醒练习",
                            trailing = {
                                Switch(checked = notificationEnabled, onCheckedChange = { viewModel.setNotificationEnabled(it) })
                            }
                        )
                        SettingsRow(
                            label = "音效",
                            subtitle = "答题音效反馈",
                            trailing = {
                                Switch(checked = soundEnabled, onCheckedChange = { viewModel.setSoundEnabled(it) })
                            }
                        )
                        SettingsRow(
                            label = "震动反馈",
                            subtitle = "答题时震动提示",
                            trailing = {
                                Switch(checked = vibrationEnabled, onCheckedChange = { viewModel.setVibrationEnabled(it) })
                            }
                        )
                    }
                }

                // 数据管理
                item {
                    SettingsSection(title = "数据管理") {
                        SettingsRow(
                            label = "离线模式",
                            subtitle = "启用后自动缓存题库",
                            trailing = {
                                Switch(checked = offlineEnabled, onCheckedChange = { viewModel.setOfflineEnabled(it) })
                            }
                        )
                        SettingsRow(
                            label = "清除缓存",
                            subtitle = "清理临时文件",
                            trailing = {
                                Icon(androidx.compose.material.icons.Icons.Default.Delete, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            onClick = { /* show clear cache dialog */ }
                        )
                        SettingsRow(
                            label = "导出数据",
                            subtitle = "备份学习记录",
                            trailing = {
                                Icon(androidx.compose.material.icons.Icons.Default.Download, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            onClick = { /* export data */ }
                        )
                        SettingsRow(
                            label = "导入数据",
                            subtitle = "恢复学习记录",
                            trailing = {
                                Icon(androidx.compose.material.icons.Icons.Default.Upload, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            onClick = { /* import data */ }
                        )
                        SettingsRow(
                            label = "清空所有数据",
                            subtitle = "删除所有练习记录、错题、收藏",
                            trailing = {
                                Icon(androidx.compose.material.icons.Icons.Default.DeleteForever, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error)
                            },
                            onClick = { /* show confirm dialog */ },
                            destructive = true
                        )
                    }
                }

                // 关于
                item {
                    SettingsSection(title = "关于") {
                        SettingsRow(
                            label = "版本",
                            subtitle = "1.0.0",
                            trailing = {
                                Text("1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        )
                        SettingsRow(
                            label = "题库版本",
                            subtitle = "2024.01",
                            trailing = {
                                Text("2024.01", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        )
                        SettingsRow(
                            label = "隐私政策",
                            trailing = {
                                Icon(androidx.compose.material.icons.Icons.Default.PrivacyTip, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            onClick = { /* open privacy policy */ }
                        )
                        SettingsRow(
                            label = "用户协议",
                            trailing = {
                                Icon(androidx.compose.material.icons.Icons.Default.Description, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            onClick = { /* open terms */ }
                        )
                        SettingsRow(
                            label = "检查更新",
                            trailing = {
                                Icon(androidx.compose.material.icons.Icons.Default.SystemUpdate, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            onClick = { /* check update */ }
                        )
                    }
                }

                item {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        TextButton(onClick = onLogout) {
                            Text("退出登录", color = MaterialTheme.colorScheme.error, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, bottom = 4.dp)
        )
        Card {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    label: String,
    subtitle: String = "",
    trailing: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    destructive: Boolean = false
) {
    val textColor = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(if (onClick != null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface)
            .clickable { onClick?.invoke() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = textColor)
            if (subtitle.isNotBlank()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailing()
    }
}