#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将三套模拟卷导入数据库
"""

import sqlite3
import re
import json
from datetime import datetime

def parse_exam_questions(txt_content, exam_name):
    """解析试卷文本，提取题目"""
    questions = []
    lines = txt_content.split('\n')
    
    current_section = ""
    question_num = 0
    current_question = None
    
    for line in lines:
        line = line.strip()
        if not line:
            continue
        
        # 检测题型部分
        if '单项选择题' in line:
            current_section = "single"
            continue
        elif '多项选择题' in line:
            current_section = "multiple"
            continue
        elif '判断题' in line:
            current_section = "judge"
            continue
        elif '不定项选择题' in line:
            current_section = "optional"
            continue
        
        # 跳过页眉页脚
        if '联系电话' in line or '临夏事业单位考试' in line or line.startswith('-') and line.endswith('-'):
            continue
        
        # 检测题目编号
        match = re.match(r'^(\d+)[\.、](.+)', line)
        if match:
            num = int(match.group(1))
            content = match.group(2)
            
            # 保存上一题
            if current_question and current_question['content']:
                questions.append(current_question)
            
            # 确定题型
            q_type = 1  # 单选
            if current_section == "multiple":
                q_type = 2  # 多选
            elif current_section == "judge":
                q_type = 3  # 判断
            elif current_section == "optional":
                q_type = 4  # 不定项
            
            current_question = {
                'num': num,
                'type': q_type,
                'content': content,
                'options': {},
                'answer': '',
                'explanation': '',
                'section': current_section
            }
            question_num = num
            continue
        
        # 检测选项
        opt_match = re.match(r'^([A-D])[\.、](.+)', line)
        if opt_match and current_question:
            opt_letter = opt_match.group(1)
            opt_content = opt_match.group(2)
            current_question['options'][opt_letter] = opt_content
            continue
        
        # 检测答案（如果有）
        if '答案' in line or '正确答案' in line:
            ans_match = re.search(r'[A-D]+', line)
            if ans_match and current_question:
                current_question['answer'] = ans_match.group(0)
    
    # 保存最后一题
    if current_question and current_question['content']:
        questions.append(current_question)
    
    return questions

def create_exam_tables(conn):
    """创建模拟考试相关表"""
    cursor = conn.cursor()
    
    # 创建模拟考试表
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS mock_exams (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            description TEXT DEFAULT '',
            total_questions INTEGER DEFAULT 0,
            total_score REAL DEFAULT 0,
            time_limit INTEGER DEFAULT 120,
            created_at INTEGER DEFAULT (strftime('%s', 'now')),
            updated_at INTEGER DEFAULT (strftime('%s', 'now'))
        )
    """)
    
    # 创建模拟考试题目表
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS mock_exam_questions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            exam_id INTEGER NOT NULL,
            question_id INTEGER,
            question_type INTEGER DEFAULT 1,
            content TEXT NOT NULL,
            option_a TEXT DEFAULT '',
            option_b TEXT DEFAULT '',
            option_c TEXT DEFAULT '',
            option_d TEXT DEFAULT '',
            correct_answer TEXT DEFAULT '',
            explanation TEXT DEFAULT '',
            score REAL DEFAULT 0,
            sort_order INTEGER DEFAULT 0,
            created_at INTEGER DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (exam_id) REFERENCES mock_exams(id) ON DELETE CASCADE
        )
    """)
    
    conn.commit()
    print("模拟考试表创建完成")

def import_exam(conn, exam_name, questions):
    """导入一套模拟考试"""
    cursor = conn.cursor()
    
    # 插入考试记录
    cursor.execute("""
        INSERT INTO mock_exams (name, description, total_questions, total_score, time_limit)
        VALUES (?, ?, ?, ?, ?)
    """, (exam_name, f"临夏事业单位考试{exam_name}", len(questions), 100, 120))
    
    exam_id = cursor.lastrowid
    
    # 插入题目
    for q in questions:
        options = q.get('options', {})
        cursor.execute("""
            INSERT INTO mock_exam_questions 
            (exam_id, question_type, content, option_a, option_b, option_c, option_d, 
             correct_answer, explanation, score, sort_order)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            exam_id,
            q['type'],
            q['content'],
            options.get('A', ''),
            options.get('B', ''),
            options.get('C', ''),
            options.get('D', ''),
            q.get('answer', ''),
            q.get('explanation', ''),
            0.64 if q['type'] == 1 else (0.87 if q['type'] == 2 else (0.56 if q['type'] == 3 else 0.65)),
            q['num']
        ))
    
    conn.commit()
    print(f"已导入 {exam_name}: {len(questions)} 道题目")
    return exam_id

def main():
    db_path = "linxia_exam_db"
    conn = sqlite3.connect(db_path)
    
    # 创建模拟考试表
    create_exam_tables(conn)
    
    # 读取三套试卷
    exam_files = [
        ("综合岗模考卷（一）", r"F:\手机APP\时政\综合岗 - 模考卷（一）7.10.txt"),
        ("综合岗模考卷（二）", r"F:\手机APP\时政\综合岗 - 模考卷（二）7.10 - 副本.txt"),
        ("综合岗模考卷（三）", r"F:\手机APP\时政\综合岗 - 模考卷（三）7.10 - 副本.txt"),
    ]
    
    for exam_name, file_path in exam_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            questions = parse_exam_questions(content, exam_name)
            import_exam(conn, exam_name, questions)
            
        except Exception as e:
            print(f"导入 {exam_name} 失败: {e}")
    
    # 显示统计
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM mock_exams")
    exam_count = cursor.fetchone()[0]
    cursor.execute("SELECT COUNT(*) FROM mock_exam_questions")
    question_count = cursor.fetchone()[0]
    
    print(f"\n模拟考试统计:")
    print(f"  考试数量: {exam_count}")
    print(f"  题目总数: {question_count}")
    
    # 按考试统计
    cursor.execute("""
        SELECT e.name, COUNT(q.id) 
        FROM mock_exams e 
        LEFT JOIN mock_exam_questions q ON e.id = q.exam_id 
        GROUP BY e.id
    """)
    print("\n各套试卷题目数:")
    for row in cursor.fetchall():
        print(f"  {row[0]}: {row[1]} 道")
    
    conn.close()
    print("\n导入完成！")

if __name__ == "__main__":
    main()
