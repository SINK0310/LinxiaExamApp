#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从三套母卷中随机抽取题目，生成4套子卷
每套子卷结构：单选50题 + 多选30题 + 判断40题 + 不定项30题 = 150题
"""

import sqlite3
import random
from datetime import datetime

def create_sub_exams():
    db_path = "linxia_exam_db"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # 清除旧子卷数据
    cursor.execute("DELETE FROM mock_exam_questions WHERE exam_id > 3")
    cursor.execute("DELETE FROM mock_exams WHERE id > 3")
    conn.commit()
    
    # 获取三套母卷的所有题目
    cursor.execute("""
        SELECT id, content, option_a, option_b, option_c, option_d,
               correct_answer, explanation, question_type
        FROM mock_exam_questions
        WHERE exam_id IN (1, 2, 3)
    """)
    all_questions = cursor.fetchall()
    
    # 按题型分组
    single_choice = []  # 单选 (question_type = 1)
    multiple_choice = []  # 多选 (question_type = 2)
    true_false = []  # 判断 (question_type = 3)
    non_fixed = []  # 不定项 (question_type = 4)
    
    for q in all_questions:
        q_type = q[8]  # question_type
        if q_type == 1:
            single_choice.append(q)
        elif q_type == 2:
            multiple_choice.append(q)
        elif q_type == 3:
            true_false.append(q)
        elif q_type == 4:
            non_fixed.append(q)
    
    print(f"可用题目统计:")
    print(f"  单选: {len(single_choice)} 题")
    print(f"  多选: {len(multiple_choice)} 题")
    print(f"  判断: {len(true_false)} 题")
    print(f"  不定项: {len(non_fixed)} 题")
    
    # 生成4套子卷
    sub_exam_names = [
        "综合岗专项练习（一）",
        "综合岗专项练习（二）",
        "综合岗专项练习（三）",
        "综合岗专项练习（四）"
    ]
    
    now = int(datetime.now().timestamp())
    exam_ids = []
    
    for i, name in enumerate(sub_exam_names):
        # 随机抽取题目
        random.shuffle(single_choice)
        random.shuffle(multiple_choice)
        random.shuffle(true_false)
        random.shuffle(non_fixed)
        
        # 每套子卷抽取的数量
        s_count = min(50, len(single_choice))
        m_count = min(30, len(multiple_choice))
        t_count = min(40, len(true_false))
        n_count = min(30, len(non_fixed))
        
        selected = (
            single_choice[:s_count] +
            multiple_choice[:m_count] +
            true_false[:t_count] +
            non_fixed[:n_count]
        )
        
        total = len(selected)
        
        # 插入试卷记录
        cursor.execute("""
            INSERT INTO mock_exams (name, description, total_questions, time_limit, 
                                   created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """, (
            name,
            f"由三套母卷题目随机组合生成的专项练习卷",
            total,
            120,
            now,
            now
        ))
        exam_id = cursor.lastrowid
        exam_ids.append(exam_id)
        
        # 插入题目
        for idx, q in enumerate(selected):
            cursor.execute("""
                INSERT INTO mock_exam_questions (exam_id, question_id, question_type, content, 
                                               option_a, option_b, option_c, option_d, 
                                               correct_answer, explanation, sort_order)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                exam_id,
                q[0],  # question_id
                q[8],  # question_type
                q[1],  # content
                q[2],  # option_a
                q[3],  # option_b
                q[4],  # option_c
                q[5],  # option_d
                q[6],  # correct_answer
                q[7],  # explanation
                idx + 1  # sort_order
            ))
        
        print(f"\n>>> {name} (ID: {exam_id})")
        print(f"   总题数: {total}")
        print(f"   单选: {s_count} | 多选: {m_count} | 判断: {t_count} | 不定项: {n_count}")
    
    conn.commit()
    
    # 显示所有试卷
    cursor.execute("SELECT id, name, total_questions FROM mock_exams ORDER BY id")
    print("\n\n所有试卷:")
    for row in cursor.fetchall():
        print(f"  ID {row[0]}: {row[1]} ({row[2]}题)")
    
    conn.close()
    print("\n>>> 子卷生成完成！")

if __name__ == "__main__":
    create_sub_exams()
