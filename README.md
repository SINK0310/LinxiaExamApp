# Linxia Exam App - 临夏事业编考试题库APP

甘肃省临夏回族自治州事业编考试题库刷题软件

## 技术栈

- **Android**: Kotlin + Jetpack Compose + Room + Hilt + Flow + Coroutines
- **iOS**: Swift + SwiftUI + SwiftData (计划中)
- **架构**: MVVM + Clean Architecture + Repository Pattern

## 核心功能

1. **章节练习** - 按知识点分类刷题，支持顺序/随机模式
2. **模拟考试** - 全真模拟、章节测试、专项突破，限时计分
3. **错题本** - 自动收集错题，支持标记掌握程度(未掌握/复习中/已掌握)
4. **收藏夹** - 标记重点题目，支持添加笔记
5. **学习进度** - 统计练习量、正确率、掌握度、高频错题
6. **离线缓存** - 题库本地化，支持离线刷题
7. **每日提醒** - 定时推送练习提醒

## 题库覆盖范围

### 公共基础知识
- 马克思主义哲学
- 毛中特/邓三科
- 党史党建
- 时事政治
- 法律法规
- 行政职业能力测验
  - 言语理解
  - 数量关系
  - 判断推理
  - 资料分析
- 公文写作
- **临夏州情/临夏本地知识**
  - 临夏历史沿革
  - 临夏地理气候
  - 临夏民族宗教
  - 临夏经济发展
  - 临夏旅游文化

### 职业能力倾向测验 (行测)
- 言语理解与表达
- 数量关系
- 判断推理
- 资料分析
- 常识判断

### 申论 (如考)
- 归纳概括
- 提出对策
- 综合分析
- 文章写作

## 数据库设计

- **categories** - 分类表(支持三级分类树)
- **questions** - 题目表(单选/多选/判断/简答)
- **user_progress** - 用户进度表
- **practice_records** - 练习记录表
- **exam_records** - 模拟考试记录表
- **wrong_questions** - 错题本表
- **collections** - 收藏表
- **offline_cache** - 离线缓存表
- **user_settings** - 用户设置表
- **question_bank_versions** - 题库版本表

## 项目结构

```
app/
├── src/main/java/com/linxia/exam/
│   ├── data/
│   │   ├── db/           # Room数据库
│   │   │   ├── entity/   # 实体类
│   │   │   ├── dao/      # DAO接口
│   │   │   └── AppDatabase.kt
│   │   ├── repository/   # 数据仓库实现
│   │   └── source/       # 数据源
│   ├── domain/
│   │   ├── model/        # 领域模型
│   │   ├── repository/   # 仓库接口
│   │   └── usecase/      # 用例
│   ├── presentation/
│   │   ├── ui/
│   │   │   ├── theme/    # 主题样式
│   │   │   ├── components/ # 通用组件
│   │   │   └── screen/   # 页面
│   │   └── viewmodel/    # ViewModels
│   ├── di/               # Hilt依赖注入
│   └── util/             # 工具类
```

## 构建要求

- Android Studio Koala (2024.1.2) 或更高版本
- JDK 17
- Kotlin 1.9.22+
- Gradle 8.4+

## 待开发

- [ ] 题库爬虫/导入工具 (Python/Node.js)
- [ ] iOS版本
- [ ] 云同步功能
- [ ] 视频/音频解析
- [ ] AI智能组卷
- [ ] 学习计划制定

## 许可证

MIT License