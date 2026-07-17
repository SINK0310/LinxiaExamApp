# 临夏事业编题库爬虫与导入工具

本目录包含题库数据采集、清洗、导入的完整工具链。

## 📁 文件说明

| 文件 | 用途 |
|------|------|
| `import_question_bank.py` | 核心导入工具，支持JSON/SQLite互转 |
| `advanced_crawler.py` | 高级爬虫框架，支持多站点采集 |
| `quick_import.py` | 一键导入内置示例题库 |
| `question_bank_sample.json` | 示例题库数据（含20道临夏特色题） |
| `requirements_python.txt` | Python依赖包 |

## 🚀 快速开始

### 1. 安装依赖
```bash
pip install -r requirements_python.txt
```

### 2. 一键导入示例题库
```bash
python quick_import.py
```
这将创建 `linxia_exam_db` SQLite数据库，包含完整的分类树和20道示例题目。

### 3. 自定义JSON导入
```bash
# 导入自己的JSON题库
python import_question_bank.py my_questions.json -d linxia_exam_db

# 同时导出SQL文件（可用于预置数据库）
python import_question_bank.py my_questions.json -d linxia_exam_db -s questions.sql
```

## 📋 JSON题库格式规范

```json
{
  "version": "2024.01.15",
  "description": "临夏事业编题库 2024年1月版",
  "totalQuestions": 2500,
  "categories": [
    {
      "id": 1,
      "name": "公共基础知识",
      "level": 1,
      "parentId": 0,
      "sortOrder": 1,
      "icon": "school",
      "color": "#1B5E20",
      "children": [
        {
          "id": 2,
          "name": "马克思主义哲学",
          "level": 2,
          "parentId": 1,
          "sortOrder": 1,
          "children": [
            {"id": 3, "name": "哲学基本问题", "level": 3, "parentId": 2, "sortOrder": 1}
          ]
        }
      ]
    }
  ],
  "questions": [
    {
      "id": 1,
      "categoryId": 3,
      "questionType": 1,
      "content": "哲学的基本问题是：",
      "optionA": "物质和意识的同一性问题",
      "optionB": "存在和思维的同一性问题",
      "optionC": "世界的本原是什么问题",
      "optionD": "世界是统一的还是多样的问题",
      "correctAnswer": "B",
      "explanation": "哲学的基本问题是思维与存在的关系问题...",
      "difficulty": 1,
      "source": "2023年临夏事业编真题",
      "tags": ["哲学", "基本问题"]
    }
  ]
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `questionType` | int | ✅ | 1=单选, 2=多选, 3=判断, 4=简答 |
| `difficulty` | int | ❌ | 1=易, 2=中, 3=难 (默认2) |
| `correctAnswer` | string | ✅ | 如 "A", "AB", "T", "F" |
| `categoryId` | int | ✅ | 对应categories中的id |
| `tags` | array | ❌ | 标签数组，自动转JSON存储 |

## 🕷️ 爬虫使用指南

### 基础爬虫框架

```python
from advanced_crawler import AdvancedCrawler, DatabaseManager

async def main():
    db = DatabaseManager("linxia_exam_db")
    
    async with AdvancedCrawler(db, "gwy_kaoshi") as crawler:
        # 爬取某个分类列表页
        await crawler.crawl_category(
            "https://www.gwykaoshi.com/question/list?cat=临夏",
            max_pages=5
        )
        
        print(f"统计: {crawler.stats}")

asyncio.run(main())
```

### 支持的目标站点

在 `advanced_crawler.py` 中配置 `CrawlTarget`：

```python
CrawlTarget(
    name="自定义站点",
    base_url="https://example.com",
    list_selector=".question-list a",      # 列表页题目链接选择器
    detail_selector=".question-content",   # 详情页内容选择器
    next_page_selector=".next-page",       # 下一页按钮选择器
    category_map={                         # URL关键词 -> 分类ID映射
        "linxia": 100,
        "xingce": 27,
        "shenlun": 300,
    }
)
```

### 选择器调试技巧

```bash
# 在浏览器控制台测试选择器
document.querySelectorAll('.question-list a')
document.querySelector('.question-content').innerText
```

## 🔧 数据清洗规则

导入时自动应用以下清洗：

1. **去重**：基于题目前200字符MD5哈希
2. **分类自动匹配**：
   - 临夏关键词 → 临夏州情分类
   - 行测关键词 → 行政职业能力测验
   - 申论关键词 → 申论分类
   - 其他 → 公共基础知识
3. **题型识别**：
   - 含"多选/以下哪些" → 多选题
   - 含"判断/对错/是否" → 判断题
   - 含"简述/论述/分析" → 简答题
   - 默认 → 单选题
4. **答案提取**：支持多种格式（答案：A / 【答案】A / 正确答案：A）
5. **HTML清洗**：移除脚本、样式、多余空白

## 📦 预置数据库生成

用于APP发布时预置题库：

```bash
# 1. 导入完整题库
python import_question_bank.py full_questions.json -d linxia_exam_db

# 2. 导出SQL
python import_question_bank.py full_questions.json -d linxia_exam_db -s preload.sql

# 3. 压缩数据库文件
gzip linxia_exam_db

# 4. 放入APP assets目录
cp linxia_exam_db.gz app/src/main/assets/
```

在APP首次启动时解压预置数据库：
```kotlin
// AppDatabase.kt 中添加
if (!dbFile.exists()) {
    copyFromAssets("linxia_exam_db.gz", dbFile)
}
```

## 📊 数据库查询示例

```sql
-- 查看分类树
WITH RECURSIVE cat_tree AS (
    SELECT id, parent_id, name, level, 0 as depth FROM categories WHERE parent_id = 0
    UNION ALL
    SELECT c.id, c.parent_id, c.name, c.level, ct.depth + 1
    FROM categories c JOIN cat_tree ct ON c.parent_id = ct.id
)
SELECT printf('%s%s', repeat('  ', depth), name) as tree FROM cat_tree ORDER BY depth, sort_order;

-- 按分类统计题目数
SELECT c.name, COUNT(q.id) as count 
FROM categories c 
LEFT JOIN questions q ON c.id = q.category_id 
GROUP BY c.id 
ORDER BY count DESC;

-- 查找重复题目
SELECT content, COUNT(*) 
FROM questions 
GROUP BY content 
HAVING COUNT(*) > 1;

-- 随机抽题（模拟考试）
SELECT * FROM questions 
WHERE category_id IN (1,2,3) 
ORDER BY RANDOM() LIMIT 100;
```

## ⚠️ 注意事项

1. **版权合规**：仅采集公开公域试题，遵守robots.txt，设置合理延迟
2. **频率控制**：默认每请求间隔1秒，并发限制5个/域名
3. **错误处理**：自动重试3次，失败记录日志不中断
4. **增量更新**：支持基于版本号/时间戳的增量导入
5. **数据备份**：导入前自动备份，支持回滚

## 📈 扩展建议

- 添加OCR识别PDF/图片试题
- 接入大模型自动生成解析
- 构建题目知识图谱
- 实现智能组卷算法
- 增加题目质量评分机制