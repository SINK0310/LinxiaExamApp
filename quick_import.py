#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
临夏事业编题库快速导入脚本
直接运行即可导入内置示例题库
"""

import asyncio
from import_question_bank import DatabaseManager, LinxiaCrawler


async def main():
    print("=" * 50)
    print("临夏事业编题库 - 快速导入工具")
    print("=" * 50)
    
    # 连接数据库
    db = DatabaseManager("linxia_exam_db")
    
    # 检查是否已有数据
    cursor = db.conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM questions")
    existing = cursor.fetchone()[0]
    
    if existing > 0:
        print(f"\n数据库中已有 {existing} 道题目")
        confirm = input("是否清空重新导入？(y/N): ").strip().lower()
        if confirm != 'y':
            print("取消导入")
            db.close()
            return
        
        # 清空题目表
        cursor.execute("DELETE FROM questions")
        cursor.execute("DELETE FROM categories")
        db.conn.commit()
        print("已清空旧数据")
    
    # 初始化分类并导入示例题库
    print("\n正在初始化分类结构...")
    async with LinxiaCrawler(db) as crawler:
        crawler.init_categories()
        print("分类结构初始化完成")
        
        print("\n正在导入示例题库 (20道临夏特色题目)...")
        questions = await crawler.crawl_from_text(crawler.SAMPLE_QUESTIONS, "内置示例题库")
        
        unique_questions = [q for q in questions if not db.question_exists(q.content, q.category_id)]
        if unique_questions:
            count = db.batch_insert_questions(unique_questions)
            for cat_id in set(q.category_id for q in unique_questions):
                db.update_category_count(cat_id)
            print(f"\n✅ 成功导入 {count} 道题目！")
        else:
            print("❌ 没有新题目可导入")
    
    # 显示统计
    cursor.execute("SELECT COUNT(*) FROM categories")
    cat_count = cursor.fetchone()[0]
    cursor.execute("SELECT COUNT(*) FROM questions")
    q_count = cursor.fetchone()[0]
    
    print(f"\n📊 数据库统计:")
    print(f"   分类数量: {cat_count}")
    print(f"   题目数量: {q_count}")
    
    # 按分类统计
    cursor.execute("""
        SELECT c.name, COUNT(q.id) as cnt 
        FROM categories c 
        LEFT JOIN questions q ON c.id = q.category_id 
        WHERE c.level = 1
        GROUP BY c.id
        ORDER BY cnt DESC
    """)
    print("\n📚 一级分类题目分布:")
    for row in cursor.fetchall():
        print(f"   {row[0]}: {row[1]} 道")
    
    # 导出JSON备用
    crawler = LinxiaCrawler(db)
    crawler.export_to_json("question_bank_backup.json")
    print(f"\n💾 已导出备份文件: question_bank_backup.json")
    
    db.close()
    print("\n✨ 完成！可以在APP中使用了")


if __name__ == "__main__":
    asyncio.run(main())