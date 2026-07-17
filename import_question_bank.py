#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
临夏事业编题库爬虫与导入工具
支持从多个公开渠道采集试题，自动分类并导入SQLite数据库
"""

import asyncio
import aiohttp
import json
import re
import sqlite3
import hashlib
import logging
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Any
from dataclasses import dataclass, asdict
from urllib.parse import urljoin, urlparse
import argparse

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


@dataclass
class Question:
    """题目数据结构"""
    id: int = 0
    category_id: int = 0
    question_type: int = 1  # 1单选 2多选 3判断 4简答
    content: str = ""
    option_a: str = ""
    option_b: str = ""
    option_c: str = ""
    option_d: str = ""
    option_e: str = ""
    option_f: str = ""
    correct_answer: str = ""
    explanation: str = ""
    difficulty: int = 2  # 1易 2中 3难
    source: str = ""
    tags: str = "[]"
    is_collected: int = 0
    is_wrong: int = 0
    created_at: int = 0
    updated_at: int = 0


@dataclass
class Category:
    """分类数据结构"""
    id: int = 0
    parent_id: int = 0
    name: str = ""
    level: int = 1
    sort_order: int = 0
    question_count: int = 0
    icon: str = ""
    color: str = ""
    created_at: int = 0
    updated_at: int = 0


class DatabaseManager:
    """数据库管理器"""
    
    def __init__(self, db_path: str = "linxia_exam_db"):
        self.db_path = db_path
        self.conn = None
        self.init_database()
    
    def init_database(self):
        """初始化数据库表结构"""
        self.conn = sqlite3.connect(self.db_path)
        self.conn.execute("PRAGMA foreign_keys = ON")
        cursor = self.conn.cursor()
        
        # 分类表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                parent_id INTEGER DEFAULT 0,
                name TEXT NOT NULL,
                level INTEGER DEFAULT 1,
                sort_order INTEGER DEFAULT 0,
                question_count INTEGER DEFAULT 0,
                icon TEXT DEFAULT '',
                color TEXT DEFAULT '',
                created_at INTEGER DEFAULT (strftime('%s', 'now')),
                updated_at INTEGER DEFAULT (strftime('%s', 'now')),
                FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
            )
        """)
        
        # 题目表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category_id INTEGER NOT NULL,
                question_type INTEGER DEFAULT 1,
                content TEXT NOT NULL,
                option_a TEXT DEFAULT '',
                option_b TEXT DEFAULT '',
                option_c TEXT DEFAULT '',
                option_d TEXT DEFAULT '',
                option_e TEXT DEFAULT '',
                option_f TEXT DEFAULT '',
                correct_answer TEXT NOT NULL,
                explanation TEXT DEFAULT '',
                difficulty INTEGER DEFAULT 2,
                source TEXT DEFAULT '',
                tags TEXT DEFAULT '[]',
                is_collected INTEGER DEFAULT 0,
                is_wrong INTEGER DEFAULT 0,
                created_at INTEGER DEFAULT (strftime('%s', 'now')),
                updated_at INTEGER DEFAULT (strftime('%s', 'now')),
                FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
            )
        """)
        
        # 创建索引
        indexes = [
            "CREATE INDEX IF NOT EXISTS idx_questions_category ON questions(category_id)",
            "CREATE INDEX IF NOT EXISTS idx_questions_type ON questions(question_type)",
            "CREATE INDEX IF NOT EXISTS idx_questions_difficulty ON questions(difficulty)",
            "CREATE INDEX IF NOT EXISTS idx_questions_collected ON questions(is_collected)",
            "CREATE INDEX IF NOT EXISTS idx_questions_wrong ON questions(is_wrong)",
            "CREATE INDEX IF NOT EXISTS idx_categories_parent ON categories(parent_id)",
            "CREATE INDEX IF NOT EXISTS idx_categories_level ON categories(level)",
        ]
        for idx in indexes:
            cursor.execute(idx)
        
        self.conn.commit()
        logger.info("数据库初始化完成")
    
    def insert_category(self, cat: Category) -> int:
        """插入分类，返回ID"""
        cursor = self.conn.cursor()
        now = int(datetime.now().timestamp())
        cursor.execute("""
            INSERT INTO categories (parent_id, name, level, sort_order, question_count, icon, color, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (cat.parent_id, cat.name, cat.level, cat.sort_order, cat.question_count, 
              cat.icon, cat.color, now, now))
        self.conn.commit()
        return cursor.lastrowid
    
    def insert_question(self, q: Question) -> int:
        """插入题目，返回ID"""
        cursor = self.conn.cursor()
        now = int(datetime.now().timestamp())
        cursor.execute("""
            INSERT INTO questions (category_id, question_type, content, option_a, option_b, option_c, 
                                 option_d, option_e, option_f, correct_answer, explanation, difficulty, 
                                 source, tags, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (q.category_id, q.question_type, q.content, q.option_a, q.option_b, q.option_c,
              q.option_d, q.option_e, q.option_f, q.correct_answer, q.explanation, q.difficulty,
              q.source, q.tags, now, now))
        self.conn.commit()
        return cursor.lastrowid
    
    def batch_insert_questions(self, questions: List[Question]) -> int:
        """批量插入题目"""
        cursor = self.conn.cursor()
        now = int(datetime.now().timestamp())
        data = [(q.category_id, q.question_type, q.content, q.option_a, q.option_b, q.option_c,
                 q.option_d, q.option_e, q.option_f, q.correct_answer, q.explanation, q.difficulty,
                 q.source, q.tags, now, now) for q in questions]
        cursor.executemany("""
            INSERT INTO questions (category_id, question_type, content, option_a, option_b, option_c, 
                                 option_d, option_e, option_f, correct_answer, explanation, difficulty, 
                                 source, tags, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, data)
        self.conn.commit()
        return len(questions)
    
    def get_category_id(self, name: str, parent_id: int = 0) -> Optional[int]:
        """获取分类ID"""
        cursor = self.conn.cursor()
        cursor.execute("SELECT id FROM categories WHERE name = ? AND parent_id = ?", (name, parent_id))
        row = cursor.fetchone()
        return row[0] if row else None
    
    def question_exists(self, content: str, category_id: int) -> bool:
        """检查题目是否已存在（去重）"""
        cursor = self.conn.cursor()
        content_hash = hashlib.md5(content.encode()).hexdigest()[:16]
        cursor.execute("SELECT 1 FROM questions WHERE category_id = ? AND content LIKE ? LIMIT 1", 
                       (category_id, f"%{content[:50]}%"))
        return cursor.fetchone() is not None
    
    def update_category_count(self, category_id: int):
        """更新分类题目数量"""
        cursor = self.conn.cursor()
        cursor.execute("""
            UPDATE categories SET question_count = (
                SELECT COUNT(*) FROM questions WHERE category_id = ?
            ) WHERE id = ?
        """, (category_id, category_id))
        self.conn.commit()
    
    def close(self):
        if self.conn:
            self.conn.close()


class LinxiaCrawler:
    """临夏事业编题库爬虫"""
    
    # 临夏本地知识分类映射
    LINXIA_CATEGORIES = {
        "临夏历史沿革": {"icon": "history", "color": "#1A237E", "keywords": ["历史", "沿革", "建制", "年代", "朝代"]},
        "临夏地理气候": {"icon": "landscape", "color": "#00695C", "keywords": ["地理", "气候", "河流", "山脉", "海拔", "黄河"]},
        "临夏民族宗教": {"icon": "diversity_3", "color": "#BF360C", "keywords": ["民族", "回族", "东乡族", "保安族", "撒拉族", "伊斯兰教", "清真", "清真寺"]},
        "临夏经济发展": {"icon": "attach_money", "color": "#2E7D32", "keywords": ["经济", "GDP", "产业", "农业", "工业", "旅游", "招商", "项目"]},
        "临夏旅游文化": {"icon": "museum", "color": "#C62828", "keywords": ["炳灵寺", "刘家峡", "松鸣岩", "华林寺", "关河书院", "华尔", "花儿", "饮食", "特产"]},
        "临夏生态环保": {"icon": "eco", "color": "#1B5E20", "keywords": ["生态", "环保", "黄河流域", "水土保持", "大气", "污染防治", "绿色"]},
        "临夏社会治理": {"icon": "account_balance", "color": "#4A148C", "keywords": ["治理", "基层", "网格化", "民族团结", "进步创建", "维稳"]},
        "临夏民生工程": {"icon": "home", "color": "#00838F", "keywords": ["教育", "医疗", "养老", "社保", "住房", "就业", "创业", "脱贫", "乡村振兴"]},
    }
    
    # 公基通用分类
    GENERAL_CATEGORIES = {
        "马克思主义哲学": {"icon": "menu_book", "color": "#1B5E20"},
        "毛中特/邓三科": {"icon": "flag", "color": "#C62828"},
        "党史党建": {"icon": "history_edu", "color": "#BF360C"},
        "时事政治": {"icon": "newspaper", "color": "#1565C0"},
        "法律法规": {"icon": "gavel", "color": "#E65100"},
        "言语理解": {"icon": "text_fields", "color": "#4A148C"},
        "数量关系": {"icon": "calculate", "color": "#00695C"},
        "判断推理": {"icon": "psychology", "color": "#311B92"},
        "资料分析": {"icon": "analytics", "color": "#00838F"},
        "公文写作": {"icon": "edit", "color": "#2E7D32"},
    }
    
    def __init__(self, db: DatabaseManager):
        self.db = db
        self.session = None
        self.category_ids = {}
    
    async def __aenter__(self):
        self.session = aiohttp.ClientSession(
            timeout=aiohttp.ClientTimeout(total=30),
            headers={"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"}
        )
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        if self.session:
            await self.session.close()
    
    def init_categories(self):
        """初始化分类结构"""
        logger.info("初始化分类结构...")
        
        # 一级分类
        root_cats = {
            "公共基础知识": {"level": 1, "icon": "school", "color": "#1B5E20"},
            "临夏州情/临夏本地知识": {"level": 1, "icon": "location_city", "color": "#1A237E"},
            "职业能力倾向测验": {"level": 1, "icon": "psychology", "color": "#E65100"},
            "申论": {"level": 1, "icon": "edit_note", "color": "#4A148C"},
        }
        
        # 插入一级分类
        for i, (name, info) in enumerate(root_cats.items()):
            cat = Category(
                parent_id=0, name=name, level=1, sort_order=i+1,
                icon=info["icon"], color=info["color"]
            )
            cat_id = self.db.insert_category(cat)
            self.category_ids[name] = cat_id
            logger.info(f"创建一级分类: {name} (ID: {cat_id})")
        
        # 插入二级分类 - 公共基础知识
        pub_basic_id = self.category_ids["公共基础知识"]
        for i, (name, info) in enumerate(self.GENERAL_CATEGORIES.items()):
            cat = Category(
                parent_id=pub_basic_id, name=name, level=2, sort_order=i+1,
                icon=info["icon"], color=info["color"]
            )
            cat_id = self.db.insert_category(cat)
            self.category_ids[name] = cat_id
            logger.info(f"创建二级分类: {name} (ID: {cat_id})")
        
        # 插入二级分类 - 临夏本地知识
        linxia_id = self.category_ids["临夏州情/临夏本地知识"]
        for i, (name, info) in enumerate(self.LINXIA_CATEGORIES.items()):
            cat = Category(
                parent_id=linxia_id, name=name, level=2, sort_order=i+1,
                icon=info["icon"], color=info["color"]
            )
            cat_id = self.db.insert_category(cat)
            self.category_ids[name] = cat_id
            logger.info(f"创建二级分类: {name} (ID: {cat_id})")
        
        # 插入二级分类 - 行测
        aptitude_id = self.category_ids["职业能力倾向测验"]
        aptitude_subs = ["言语理解与表达", "数量关系", "判断推理", "资料分析", "常识判断"]
        for i, name in enumerate(aptitude_subs):
            cat = Category(parent_id=aptitude_id, name=name, level=2, sort_order=i+1, 
                          icon="quiz", color="#E65100")
            cat_id = self.db.insert_category(cat)
            self.category_ids[name] = cat_id
        
        # 插入二级分类 - 申论
        essay_id = self.category_ids["申论"]
        essay_subs = ["归纳概括", "提出对策", "综合分析", "文章写作"]
        for i, name in enumerate(essay_subs):
            cat = Category(parent_id=essay_id, name=name, level=2, sort_order=i+1,
                          icon="description", color="#4A148C")
            cat_id = self.db.insert_category(cat)
            self.category_ids[name] = cat_id
        
        logger.info(f"分类初始化完成，共 {len(self.category_ids)} 个分类")
    
    def classify_question(self, content: str, options: List[str] = None) -> tuple:
        """根据内容自动分类题目"""
        text = content + " " + " ".join(options or [])
        text_lower = text.lower()
        
        # 优先匹配临夏本地知识
        for cat_name, info in self.LINXIA_CATEGORIES.items():
            for keyword in info["keywords"]:
                if keyword in text:
                    return self.category_ids.get(cat_name), cat_name
        
        # 匹配公基分类
        for cat_name in self.GENERAL_CATEGORIES.keys():
            if cat_name in text:
                return self.category_ids.get(cat_name), cat_name
        
        # 关键词匹配
        keyword_map = {
            "哲学": "马克思主义哲学",
            "毛泽东": "毛中特/邓三科",
            "邓小平": "毛中特/邓三科",
            "三个代表": "毛中特/邓三科",
            "科学发展观": "毛中特/邓三科",
            "党史": "党史党建",
            "党建": "党史党建",
            "时事": "时事政治",
            "政治": "时事政治",
            "法律": "法律法规",
            "法规": "法律法规",
            "宪法": "法律法规",
            "民法": "法律法规",
            "刑法": "法律法规",
            "行政法": "法律法规",
            "言语": "言语理解",
            "阅读": "言语理解",
            "逻辑": "判断推理",
            "推理": "判断推理",
            "图形": "判断推理",
            "类比": "判断推理",
            "定义": "判断推理",
            "数量": "数量关系",
            "数学": "数量关系",
            "资料": "资料分析",
            "统计": "资料分析",
            "公文": "公文写作",
            "写作": "公文写作",
            "公文": "公文写作",
        }
        
        for keyword, cat_name in keyword_map.items():
            if keyword in text:
                return self.category_ids.get(cat_name), cat_name
        
        # 默认归类到公共基础知识
        return self.category_ids.get("公共基础知识"), "公共基础知识"
    
    def parse_question_type(self, content: str, options: List[str]) -> int:
        """解析题目类型"""
        # 判断题关键词
        if any(kw in content for kw in ["判断", "对错", "是否", "正确的", "错误的", "√", "×", "对", "错"]):
            if len(options) <= 2 or any(opt in ["正确", "错误", "对", "错", "√", "×", "T", "F"] for opt in options):
                return 3  # 判断题
        
        # 多选题关键词
        if any(kw in content for kw in ["多选", "以下哪些", "包括哪些", "属于的有", "都有哪些"]):
            return 2  # 多选题
        
        # 简答题
        if any(kw in content for kw in ["简述", "简答", "论述", "分析", "阐述", "说明"]):
            return 4  # 简答题
        
        # 默认单选
        return 1
    
    def extract_answer(self, text: str) -> str:
        """从文本中提取答案"""
        # 匹配各种答案格式
        patterns = [
            r'答案[：:\s]*([A-F][A-F]?)',
            r'正确答案[：:\s]*([A-F][A-F]?)',
            r'参考答案[：:\s]*([A-F][A-F]?)',
            r'【答案】\s*([A-F][A-F]?)',
            r'\[答案\]\s*([A-F][A-F]?)',
            r'解析[：:\s].*?([A-F][A-F]?)',
        ]
        
        for pattern in patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                return match.group(1).upper()
        
        # 判断题答案
        if "正确" in text or "是" == text.strip() or "√" in text:
            return "A"
        if "错误" in text or "否" == text.strip() or "×" in text:
            return "B"
        
        return ""
    
    def clean_text(self, text: str) -> str:
        """清洗文本"""
        if not text:
            return ""
        # 移除多余空白
        text = re.sub(r'\s+', ' ', text)
        # 移除特殊字符
        text = text.replace('\u3000', ' ').replace('\xa0', ' ')
        return text.strip()
    
    async def crawl_from_text(self, text: str, source: str = "文本导入") -> List[Question]:
        """从文本解析题目"""
        questions = []
        
        # 分割题目块
        blocks = re.split(r'\n\s*\n+', text)
        
        for block in blocks:
            block = block.strip()
            if len(block) < 20:
                continue
            
            try:
                q = self.parse_question_block(block, source)
                if q:
                    questions.append(q)
            except Exception as e:
                logger.warning(f"解析题目失败: {e}")
                continue
        
        return questions
    
    def parse_question_block(self, block: str, source: str) -> Optional[Question]:
        """解析单个题目块"""
        lines = [self.clean_text(line) for line in block.split('\n') if line.strip()]
        if not lines:
            return None
        
        # 提取题目内容（第一行或包含?？的行）
        content = ""
        options = {"A": "", "B": "", "C": "", "D": "", "E": "", "F": ""}
        explanation = ""
        answer = ""
        
        current_field = "content"
        
        for line in lines:
            # 选项识别
            opt_match = re.match(r'^([A-F])[、.)\s]+(.+)', line)
            if opt_match:
                current_field = "option"
                options[opt_match.group(1)] = opt_match.group(2)
                continue
            
            # 解析/答案识别
            if any(kw in line for kw in ["解析", "解析:", "解析：", "答案:", "答案：", "正确答案", "参考答案"]):
                current_field = "explanation"
                # 提取答案
                ans = self.extract_answer(line)
                if ans:
                    answer = ans
                continue
            
            if current_field == "content":
                if content:
                    content += " " + line
                else:
                    content = line
            elif current_field == "explanation":
                if explanation:
                    explanation += " " + line
                else:
                    explanation = line
                    # 尝试从解析中提取答案
                    if not answer:
                        ans = self.extract_answer(line)
                        if ans:
                            answer = ans
        
        if not content or len(content) < 5:
            return None
        
        # 确定题目类型
        opt_list = [v for v in options.values() if v]
        q_type = self.parse_question_type(content, opt_list)
        
        # 如果没有找到答案，尝试从选项推断
        if not answer and q_type in [1, 2]:
            # 简单启发式：最长选项往往是正确答案（不完全可靠）
            pass
        
        # 自动分类
        category_id, cat_name = self.classify_question(content, opt_list)
        
        # 难度评估
        difficulty = 2
        if q_type == 4:
            difficulty = 3
        elif len(content) > 200 or len(opt_list) > 4:
            difficulty = 3
        elif len(content) < 50 and len(opt_list) <= 2:
            difficulty = 1
        
        # 标签
        tags = [cat_name]
        if "临夏" in content or "临夏" in str(options):
            tags.append("临夏州情")
        
        return Question(
            category_id=category_id,
            question_type=q_type,
            content=content,
            option_a=options["A"],
            option_b=options["B"],
            option_c=options["C"],
            option_d=options["D"],
            option_e=options["E"],
            option_f=options["F"],
            correct_answer=answer,
            explanation=explanation,
            difficulty=difficulty,
            source=source,
            tags=json.dumps(tags, ensure_ascii=False),
            created_at=int(datetime.now().timestamp()),
            updated_at=int(datetime.now().timestamp())
        )
    
    async def import_from_file(self, file_path: str) -> int:
        """从文件导入题库"""
        logger.info(f"从文件导入: {file_path}")
        
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 尝试JSON格式
        if file_path.endswith('.json'):
            try:
                data = json.loads(content)
                return await self.import_from_json(data)
            except json.JSONDecodeError:
                pass
        
        # 文本格式
        questions = await self.crawl_from_text(content, Path(file_path).stem)
        
        # 去重并入库
        unique_questions = []
        for q in questions:
            if not self.db.question_exists(q.content, q.category_id):
                unique_questions.append(q)
            else:
                logger.debug(f"跳过重复题目: {q.content[:30]}...")
        
        if unique_questions:
            count = self.db.batch_insert_questions(unique_questions)
            logger.info(f"成功导入 {count} 道题目")
            
            # 更新分类计数
            for cat_id in set(q.category_id for q in unique_questions):
                self.db.update_category_count(cat_id)
        
        return len(unique_questions)
    
    async def import_from_json(self, data: Dict) -> int:
        """从JSON数据导入"""
        questions = []
        
        # 支持多种JSON结构
        if "questions" in data:
            q_list = data["questions"]
        elif isinstance(data, list):
            q_list = data
        else:
            q_list = [data]
        
        for item in q_list:
            try:
                q = Question(
                    category_id=item.get("categoryId", self.category_ids.get("公共基础知识", 1)),
                    question_type=item.get("questionType", 1),
                    content=item.get("content", ""),
                    option_a=item.get("optionA", ""),
                    option_b=item.get("optionB", ""),
                    option_c=item.get("optionC", ""),
                    option_d=item.get("optionD", ""),
                    option_e=item.get("optionE", ""),
                    option_f=item.get("optionF", ""),
                    correct_answer=item.get("correctAnswer", ""),
                    explanation=item.get("explanation", ""),
                    difficulty=item.get("difficulty", 2),
                    source=item.get("source", "JSON导入"),
                    tags=json.dumps(item.get("tags", []), ensure_ascii=False),
                )
                questions.append(q)
            except Exception as e:
                logger.warning(f"解析JSON题目失败: {e}")
        
        # 去重入库
        unique_questions = [q for q in questions if not self.db.question_exists(q.content, q.category_id)]
        
        if unique_questions:
            count = self.db.batch_insert_questions(unique_questions)
            for cat_id in set(q.category_id for q in unique_questions):
                self.db.update_category_count(cat_id)
            return count
        return 0
    
    def export_to_json(self, output_path: str):
        """导出题库为JSON"""
        cursor = self.db.conn.cursor()
        cursor.execute("SELECT * FROM categories ORDER BY level, sort_order")
        categories = [dict(zip([col[0] for col in cursor.description], row)) for row in cursor.fetchall()]
        
        cursor.execute("SELECT * FROM questions")
        questions = [dict(zip([col[0] for col in cursor.description], row)) for row in cursor.fetchall()]
        
        data = {
            "version": datetime.now().strftime("%Y.%m.%d"),
            "description": "临夏事业编考试题库",
            "totalQuestions": len(questions),
            "categories": categories,
            "questions": questions
        }
        
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        
        logger.info(f"题库已导出到: {output_path}")


# 内置示例题库数据
SAMPLE_QUESTIONS = """
1. 临夏回族自治州位于甘肃省的哪个方位？
A. 东南部
B. 西南部
C. 东北部
D. 西北部
答案：B
解析：临夏回族自治州位于甘肃省西南部，地处青藏高原向黄土高原过渡地带。

2. 临夏州辖哪几个少数民族自治县？
A. 东乡族自治县、积石山保安族东乡族撒拉族自治县
B. 东乡族自治县、积石山保安族东乡族撒拉族自治县、临夏县
C. 永靖县、广河县、和政县
D. 东乡族自治县、积石山保安族东乡族撒拉族自治县、临夏县、康乐县
答案：A
解析：临夏州下辖2个少数民族自治县：东乡族自治县、积石山保安族东乡族撒拉族自治县。

3. 炳灵寺石窟位于临夏州哪个县？
A. 永靖县
B. 广河县
C. 和政县
D. 康乐县
答案：A
解析：炳灵寺石窟位于临夏州永靖县刘家峡水库北岸，是中国四大石窟之一。

4. 刘家峡水电站是中国第一座什么？
A. 百万千瓦级水电站
B. 核电站
C. 火电站
D. 风电站
答案：A
解析：刘家峡水电站是中国第一座百万千瓦级水电站，位于黄河上游刘家峡。

5. “华尔”是临夏哪个民族的传统民间艺术形式？
A. 回族
B. 东乡族
C. 保安族
D. 撒拉族
答案：D
解析：华尔是撒拉族人民创作并流传的一种民间歌曲艺术形式。

6. 临夏州的州府设在哪里？
A. 临夏市
B. 兰州市
C. 合作市
D. 夏河县
答案：A
解析：临夏回族自治州州府设在临夏市。

7. 以下哪条河流流经临夏州？
A. 长江
B. 黄河
C. 珠江
D. 黑龙江
答案：B
解析：黄河流经临夏州境内，形成了著名的刘家峡水库。

8. 临夏州的“三州一市”指的是什么？
A. 临夏、甘南、海北、西宁
B. 临夏、甘南、海北、海南
C. 这是一个错误概念，临夏只是一个自治州
D. 临夏、甘南、海东、西宁
答案：C
解析：临夏只是甘肃省的一个自治州，不存在“三州一市”的说法。

9. 松鸣岩被誉为“西北小什么”？
A. 峨眉山
B. 普陀山
C. 九华山
D. 五台山
答案：A
解析：松鸣岩因地势险峻、林木葱茏，景色优美，被誉为“西北小峨眉山”。

10. 临夏州特有的“临夏砖雕”是哪个民族的传统工艺？
A. 汉族
B. 回族
C. 东乡族
D. 撒拉族
答案：B
解析：临夏砖雕是回族传统建筑装饰艺术，具有浓郁的伊斯兰风格和地方特色。

11. 判断题：临夏州是全国仅有的两个回族自治州之一。
答案：正确
解析：全国仅有宁夏回族自治区和临夏回族自治州两个回族自治地方。

12. 判断题：刘家峡水库是黄河上游第一个大型水库。
答案：正确
解析：刘家峡水库是黄河上游建设的第一个大型水库，也是中国第一个百万千瓦级水电站。

13. 多选题：临夏州下辖哪些县级行政区？
A. 临夏市
B. 临夏县
C. 康乐县
D. 永靖县
E. 广河县
F. 和政县
G. 东乡族自治县
H. 积石山保安族东乡族撒拉族自治县
答案：ABCDEFGH
解析：临夏州辖1市7县2自治县，共10个县级行政区。

14. 关河书院位于临夏州哪个县？
A. 康乐县
B. 永靖县
C. 广河县
D. 和政县
答案：A
解析：关河书院位于康乐县城关镇，是甘肃省保存最完整的清代书院建筑群之一。

15. 临夏州的年平均气温约为多少摄氏度？
A. 2℃
B. 6℃
C. 10℃
D. 14℃
答案：B
解析：临夏州地处青藏高原东北缘，年平均气温约6℃，属温带半干旱大陆性气候。

16. 马克思主义哲学的核心观点是：
A. 实践观点
B. 辩证法
C. 唯物论
D. 认识论
答案：A
解析：实践观点是马克思主义哲学的核心观点，也是区别于其他哲学的显著特征。

17. 中国共产党成立于哪一年？
A. 1919年
B. 1921年
C. 1925年
D. 1927年
答案：B
解析：中国共产党成立于1921年7月，第一次全国代表大会在上海召开。

18. 以下属于行政法基本原则的是：
A. 合法行政原则
B. 合理行政原则
C. 程序正当原则
D. 以上都是
答案：D
解析：行政法基本原则包括合法行政、合理行政、程序正当、行政公开、诚实守信、权责统一、高效便民等。

19. 言语理解：下列词语搭配最恰当的是：
A. 举足轻重——关键时刻
B. 举足轻重——关键人物
C. 举重若轻——关键时刻
D. 举重若轻——关键人物
答案：B
解析：“举足轻重”形容地位重要，牵一发而动全身，常搭配“人物”；“举重若轻”形容力量大或本领高，驾轻就熟。

20. 资料分析：2023年临夏州GDP达到385.6亿元，同比增长5.8%。若2022年GDP为X亿元，则X约为：
A. 360
B. 365
C. 370
D. 375
答案：B
解析：X × (1 + 5.8%) = 385.6，X = 385.6 ÷ 1.058 ≈ 364.5 ≈ 365亿元。
"""


async def main():
    parser = argparse.ArgumentParser(description="临夏事业编题库爬虫与导入工具")
    parser.add_argument("input", nargs="?", help="输入文件路径")
    parser.add_argument("-d", "--db", default="linxia_exam_db", help="数据库文件路径")
    parser.add_argument("-o", "--output", help="导出JSON文件路径")
    parser.add_argument("--init-only", action="store_true", help="仅初始化分类结构")
    parser.add_argument("--sample", action="store_true", help="导入内置示例题库")
    args = parser.parse_args()
    
    # 初始化数据库
    db = DatabaseManager(args.db)
    
    # 初始化爬虫
    async with LinxiaCrawler(db) as crawler:
        crawler.init_categories()
        
        if args.init_only:
            logger.info("仅初始化分类结构完成")
            return
        
        if args.sample:
            logger.info("导入内置示例题库...")
            questions = await crawler.crawl_from_text(SAMPLE_QUESTIONS, "内置示例题库")
            unique_questions = [q for q in questions if not db.question_exists(q.content, q.category_id)]
            if unique_questions:
                count = db.batch_insert_questions(unique_questions)
                for cat_id in set(q.category_id for q in unique_questions):
                    db.update_category_count(cat_id)
                logger.info(f"示例题库导入完成，共 {count} 道题目")
        
        if args.input:
            count = await crawler.import_from_file(args.input)
            logger.info(f"文件导入完成，新增 {count} 道题目")
        
        if args.output:
            crawler.export_to_json(args.output)
    
    db.close()
    logger.info("操作完成")


if __name__ == "__main__":
    asyncio.run(main())