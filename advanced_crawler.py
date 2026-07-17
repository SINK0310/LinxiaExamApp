#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
临夏事业编题库高级爬虫
支持从多个公开网站采集试题
"""

import asyncio
import aiohttp
import re
import json
import logging
from typing import List, Dict, Optional, Set
from dataclasses import dataclass
from urllib.parse import urljoin, urlparse
from bs4 import BeautifulSoup
import hashlib
from datetime import datetime

# 复用之前的数据库管理器
from import_question_bank import DatabaseManager, Question, Category


logger = logging.getLogger(__name__)


@dataclass
class CrawlTarget:
    """爬取目标配置"""
    name: str
    base_url: str
    list_selector: str  # 题目列表页选择器
    detail_selector: str  # 详情页选择器
    next_page_selector: str  # 下一页选择器
    category_map: Dict[str, int]  # URL关键词到分类ID映射
    headers: Dict[str, str] = None


class AdvancedCrawler:
    """高级爬虫类"""
    
    # 预配置的爬取目标
    TARGETS = {
        "gwy_kaoshi": CrawlTarget(
            name="公务员考试网",
            base_url="https://www.gwykaoshi.com",
            list_selector=".question-list .item a",
            detail_selector=".question-content",
            next_page_selector=".pagination .next a",
            category_map={
                "gongji": 临夏": 100,  # 临夏本地知识分类ID
                "xingzheng": 27,  # 行政职业能力测验
                "shenlun": 300,  # 申论
                "gongji": 1,  # 公共基础知识
            }
        ),
        "huatu": CrawlTarget(
            name="华图教育",
            base_url="https://www.huatu.com",
            list_selector=".question-item a",
            detail_selector=".question-detail",
            next_page_selector=".page-next",
            category_map={
                "lianxia": 100,
                "xingce": 27,
                "shenlun": 300,
                "gongji": 1,
            }
        ),
        "zhonggong": CrawlTarget(
            name="中公教育",
            base_url="https://www.offcn.com",
            list_selector=".ti-lib-item a",
            detail_selector=".question-box",
            next_page_selector=".next-page",
            category_map={}
        )
    }
    
    def __init__(self, db: DatabaseManager, target_name: str = "gwy_kaoshi"):
        self.db = db
        self.target = self.TARGETS.get(target_name)
        if not self.target:
            raise ValueError(f"未知爬取目标: {target_name}")
        self.session = None
        self.visited_urls: Set[str] = set()
        self.question_hashes: Set[str] = set()
        self.stats = {"total": 0, "success": 0, "failed": 0, "duplicates": 0}
    
    async def __aenter__(self):
        connector = aiohttp.TCPConnector(limit=10, limit_per_host=5)
        timeout = aiohttp.ClientTimeout(total=30, connect=10)
        self.session = aiohttp.ClientSession(
            connector=connector,
            timeout=timeout,
            headers={
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
            }
        )
        # 加载已存在的题目哈希用于去重
        await self.load_existing_hashes()
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        if self.session:
            await self.session.close()
    
    async def load_existing_hashes(self):
        """加载数据库中已存在的题目哈希"""
        cursor = self.db.conn.cursor()
        cursor.execute("SELECT content FROM questions")
        for row in cursor.fetchall():
            content = row[0][:100]  # 取前100字符生成哈希
            self.question_hashes.add(hashlib.md5(content.encode()).hexdigest())
        logger.info(f"已加载 {len(self.question_hashes)} 个现有题目哈希")
    
    def compute_hash(self, content: str) -> str:
        """计算内容哈希"""
        return hashlib.md5(content[:200].encode()).hexdigest()
    
    async def fetch(self, url: str) -> Optional[str]:
        """获取页面内容"""
        if url in self.visited_urls:
            return None
        
        try:
            async with self.session.get(url) as response:
                if response.status == 200:
                    self.visited_urls.add(url)
                    return await response.text()
                else:
                    logger.warning(f"请求失败 {url}: {response.status}")
        except Exception as e:
            logger.error(f"请求异常 {url}: {e}")
        return None
    
    def parse_list_page(self, html: str, base_url: str) -> List[str]:
        """解析列表页，提取详情页链接"""
        soup = BeautifulSoup(html, 'lxml')
        links = []
        for a in soup.select(self.target.list_selector):
            href = a.get('href')
            if href:
                full_url = urljoin(base_url, href)
                links.append(full_url)
        return links
    
    def parse_detail_page(self, html: str, url: str) -> Optional[Dict]:
        """解析详情页，提取题目信息"""
        soup = BeautifulSoup(html, 'lxml')
        
        # 尝试多种选择器
        content_elem = soup.select_one(self.target.detail_selector)
        if not content_elem:
            # 备选选择器
            for sel in ['.question', '.content', '.article-content', 'main', '.post-content']:
                content_elem = soup.select_one(sel)
                if content_elem:
                    break
        
        if not content_elem:
            return None
        
        text = content_elem.get_text('\n', strip=True)
        
        # 提取题目内容
        question_data = self.extract_question(text, url)
        return question_data
    
    def extract_question(self, text: str, url: str) -> Optional[Dict]:
        """从文本中提取结构化题目"""
        # 清洗文本
        text = re.sub(r'\s+', ' ', text).strip()
        
        # 尝试提取题目编号
        q_match = re.search(r'[第]?\s*(\d+)[、.\s]', text)
        q_num = q_match.group(1) if q_match else ""
        
        # 分割题目和选项
        parts = re.split(r'\n\s*(?:[A-D][、.)]\s*|【答案】|答案[:：]|解析[:：])', text)
        
        if len(parts) < 2:
            return None
        
        content = parts[0].strip()
        if len(content) < 10:
            return None
        
        # 提取选项
        options = {}
        option_pattern = r'([A-F])[、.)]\s*([^A-F\n]+?)(?=\s*[A-F][、.)]|\s*$|答案|解析)'
        for match in re.finditer(option_pattern, text):
            options[match.group(1)] = match.group(2).strip()
        
        # 提取答案
        answer = ""
        ans_patterns = [
            r'答案[:：]\s*([A-F]+)',
            r'正确答案[:：]\s*([A-F]+)',
            r'【答案】\s*([A-F]+)',
            r'\[答案\]\s*([A-F]+)',
        ]
        for pattern in ans_patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                answer = match.group(1).upper()
                break
        
        # 提取解析
        explanation = ""
        exp_patterns = [
            r'解析[:：]\s*(.+?)(?:\n\n|\Z)',
            r'【解析】\s*(.+?)(?:\n\n|\Z)',
            r'解析[:：](.+)',
        ]
        for pattern in exp_patterns:
            match = re.search(pattern, text, re.DOTALL)
            if match:
                explanation = match.group(1).strip()[:1000]
                break
        
        # 确定分类
        category_id = self.classify_by_url(url, text)
        
        # 确定题型
        q_type = 1  # 默认单选
        if len(options) > 4:
            q_type = 2  # 多选
        elif "判断" in text or "对错" in text or answer in ["T", "F", "对", "错"]:
            q_type = 3  # 判断
        elif "简答" in text or "论述" in text:
            q_type = 4  # 简答
        
        return {
            "category_id": category_id,
            "question_type": q_type,
            "content": content,
            "options": options,
            "answer": answer,
            "explanation": explanation,
            "source_url": url,
            "difficulty": 2
        }
    
    def classify_by_url(self, url: str, content: str) -> int:
        """根据URL和内容分类"""
        url_lower = url.lower()
        content_lower = content.lower()
        
        # URL关键词匹配
        for keyword, cat_id in self.target.category_map.items():
            if keyword in url_lower:
                return cat_id
        
        # 内容关键词匹配
        if any(kw in content_lower for kw in ["临夏", "炳灵寺", "刘家峡", "松鸣岩", "华林寺", "关河书院", "华尔", "撒拉族", "东乡族", "保安族"]):
            return self.target.category_map.get("临夏", 100)
        
        if any(kw in content_lower for kw in ["行测", "言语理解", "数量关系", "判断推理", "资料分析", "常识判断"]):
            return self.target.category_map.get("xingzheng", 27)
        
        if any(kw in content_lower for kw in ["申论", "归纳概括", "提出对策", "综合分析", "文章写作"]):
            return self.target.category_map.get("shenlun", 300)
        
        if any(kw in content_lower for kw in ["马哲", "毛中特", "党史", "时事政治", "法律", "公文"]):
            return self.target.category_map.get("gongji", 1)
        
        return 1  # 默认公共基础知识
    
    async def crawl_category(self, start_url: str, max_pages: int = 10) -> int:
        """爬取某个分类"""
        current_url = start_url
        page = 0
        
        while current_url and page < max_pages:
            logger.info(f"爬取第 {page+1} 页: {current_url}")
            html = await self.fetch(current_url)
            
            if not html:
                break
            
            # 解析详情页链接
            detail_urls = self.parse_list_page(html, current_url)
            logger.info(f"发现 {len(detail_urls)} 个详情页链接")
            
            # 并发爬取详情页
            tasks = [self.process_detail(url) for url in detail_urls]
            results = await asyncio.gather(*tasks, return_exceptions=True)
            
            for result in results:
                if isinstance(result, Exception):
                    self.stats["failed"] += 1
                    logger.error(f"处理失败: {result}")
                elif result:
                    self.stats["success"] += 1
                else:
                    self.stats["duplicates"] += 1
            
            self.stats["total"] += len(detail_urls)
            
            # 查找下一页
            soup = BeautifulSoup(html, 'lxml')
            next_link = soup.select_one(self.target.next_page_selector)
            if next_link:
                next_href = next_link.get('href')
                if next_href:
                    current_url = urljoin(current_url, next_href)
                    page += 1
                    await asyncio.sleep(1)  # 礼貌延迟
                else:
                    break
            else:
                break
        
        return self.stats["success"]
    
    async def process_detail(self, url: str) -> bool:
        """处理单个详情页"""
        if url in self.visited_urls:
            return False
        
        html = await self.fetch(url)
        if not html:
            return False
        
        q_data = self.parse_detail_page(html, url)
        if not q_data:
            return False
        
        # 去重检查
        content_hash = self.compute_hash(q_data["content"])
        if content_hash in self.question_hashes:
            self.stats["duplicates"] += 1
            return False
        
        self.question_hashes.add(content_hash)
        
        # 构建Question对象
        options = q_data["options"]
        question = Question(
            category_id=q_data["category_id"],
            question_type=q_data["question_type"],
            content=q_data["content"],
            option_a=options.get("A", ""),
            option_b=options.get("B", ""),
            option_c=options.get("C", ""),
            option_d=options.get("D", ""),
            option_e=options.get("E", ""),
            option_f=options.get("F", ""),
            correct_answer=q_data["answer"],
            explanation=q_data["explanation"],
            difficulty=q_data["difficulty"],
            source=f"{self.target.name} - {url}",
            tags=json.dumps(["爬虫采集", self.target.name], ensure_ascii=False)
        )
        
        # 插入数据库
        try:
            self.db.insert_question(question)
            self.db.update_category_count(q_data["category_id"])
            return True
        except Exception as e:
            logger.error(f"入库失败: {e}")
            self.stats["failed"] += 1
            return False


# 使用示例
async def demo_crawl():
    """演示爬虫用法"""
    db = DatabaseManager("linxia_exam_db")
    
    # 初始化分类（如果还没初始化）
    cursor = db.conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM categories")
    if cursor.fetchone()[0] == 0:
        from import_question_bank import LinxiaCrawler
        async with LinxiaCrawler(db) as c:
            c.init_categories()
    
    # 运行爬虫
    async with AdvancedCrawler(db, "gwy_kaoshi") as crawler:
        # 示例：爬取某个分类页面
        # await crawler.crawl_category("https://www.gwykaoshi.com/question/list?cat=临夏", max_pages=3)
        
        # 打印统计
        print(f"爬取统计: {crawler.stats}")
    
    db.close()


if __name__ == "__main__":
    asyncio.run(demo_crawl())