#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
导出数据库种子数据为JSON，供Android App首次启动时导入
"""

import sqlite3
import json
from collections import OrderedDict

def main():
    db_path = "linxia_exam_db"
    conn = sqlite3.connect(db_path)
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    data = OrderedDict()

    # 导出分类
    cursor.execute("SELECT id, parent_id, name, level, sort_order, question_count, icon, color FROM categories ORDER BY id")
    data["categories"] = [dict(row) for row in cursor.fetchall()]

    # 导出题目
    cursor.execute("""
        SELECT id, category_id, question_type, content, option_a, option_b, option_c, option_d,
               option_e, option_f, correct_answer, explanation, difficulty, source, tags,
               is_collected, is_wrong, created_at, updated_at
        FROM questions ORDER BY id
    """)
    data["questions"] = [dict(row) for row in cursor.fetchall()]

    # 导出模拟考试
    cursor.execute("SELECT id, name, description, total_questions, total_score, time_limit, created_at FROM mock_exams ORDER BY id")
    data["mock_exams"] = [dict(row) for row in cursor.fetchall()]

    # 导出模拟考试题目
    cursor.execute("""
        SELECT id, exam_id, question_id, question_type, content, option_a, option_b, option_c, option_d,
               correct_answer, explanation, score, sort_order
        FROM mock_exam_questions ORDER BY id
    """)
    data["mock_exam_questions"] = [dict(row) for row in cursor.fetchall()]

    # 导出练习板块
    cursor.execute("SELECT id, name, description, topic, total_questions, created_at FROM practice_exercises ORDER BY id")
    data["practice_exercises"] = [dict(row) for row in cursor.fetchall()]

    # 导出练习题目
    cursor.execute("""
        SELECT id, exercise_id, question_type, content, option_a, option_b, option_c, option_d,
               correct_answer, explanation, sort_order
        FROM practice_questions ORDER BY id
    """)
    data["practice_questions"] = [dict(row) for row in cursor.fetchall()]

    conn.close()

    # 写入JSON
    output = "app/src/main/assets/seed_data.json"
    with open(output, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print("导出完成:")
    for key, val in data.items():
        print(f"  {key}: {len(val)} 条")
    print(f"\n文件: {output}")

if __name__ == "__main__":
    main()
