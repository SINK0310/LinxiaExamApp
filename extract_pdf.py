#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
提取模拟卷PDF文本内容
"""

import PyPDF2
import os
from pathlib import Path

def extract_text_from_pdf(pdf_path):
    """从PDF提取文本"""
    text = ""
    try:
        with open(pdf_path, 'rb') as file:
            reader = PyPDF2.PdfReader(file)
            for page in reader.pages:
                text += page.extract_text() + "\n"
    except Exception as e:
        print(f"Error reading {pdf_path}: {e}")
    return text

def main():
    pdf_dir = Path(r"F:\手机APP\时政")
    
    pdf_files = list(pdf_dir.glob("*.pdf"))
    
    print(f"找到 {len(pdf_files)} 个PDF文件")
    
    for pdf_file in pdf_files:
        print(f"\n{'='*60}")
        print(f"文件: {pdf_file.name}")
        print(f"{'='*60}")
        
        text = extract_text_from_pdf(pdf_file)
        
        # 保存到文本文件
        txt_file = pdf_file.with_suffix('.txt')
        with open(txt_file, 'w', encoding='utf-8') as f:
            f.write(text)
        
        print(f"已提取 {len(text)} 字符")
        print(f"保存到: {txt_file}")
        
        # 显示前2000字符预览
        print(f"\n预览（前2000字符）:")
        print(text[:2000])

if __name__ == "__main__":
    main()
