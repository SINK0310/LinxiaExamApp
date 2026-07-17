#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从时政图片分析中提取题目，导入到题库数据库
"""

import json
import sqlite3
from datetime import datetime
from pathlib import Path
import hashlib

# 时政题目数据 - 基于图片分析提取
CURRENT_AFFAIRS_QUESTIONS = [
    # ==================== 2026年甘肃省委一号文件 ====================
    {
        "categoryId": 18,  # 时事政治 - 甘肃时事
        "questionType": 1,
        "content": "2026年甘肃省委一号文件的全称是什么？",
        "optionA": "《中共甘肃省委甘肃省人民政府关于做好全面推进乡村振兴重点工作的实施意见》",
        "optionB": "《中共甘肃省委甘肃省人民政府关于锚定农业农村现代化扎实推进乡村全面振兴的实施意见》",
        "optionC": "《甘肃省农业农村现代化发展规划（2021-2025年）》",
        "optionD": "《中共甘肃省委关于实施乡村振兴战略的意见》",
        "correctAnswer": "B",
        "explanation": "2026年甘肃省委一号文件全称是《中共甘肃省委甘肃省人民政府关于锚定农业农村现代化扎实推进乡村全面振兴的实施意见》。",
        "difficulty": 1,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "乡村振兴"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件中，"一个锚定"指的是什么？",
        "optionA": "锚定农业现代化",
        "optionB": "锚定农业农村现代化",
        "optionC": "锚定乡村振兴",
        "optionD": "锚定农民增收",
        "correctAnswer": "B",
        "explanation": "一号文件提出"一个锚定"——锚定农业农村现代化。",
        "difficulty": 2,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件的"总抓手"是什么？",
        "optionA": "推进乡村全面振兴",
        "optionB": "学习运用"千万工程"经验",
        "optionC": "深化农村改革",
        "optionD": "加强农村基层党建",
        "correctAnswer": "B",
        "explanation": "一号文件提出"总抓手"是学习运用"千万工程"经验。",
        "difficulty": 2,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "千万工程"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出的"两个底线"是什么？",
        "optionA": "国家粮食安全底线、不发生规模性返贫致贫底线",
        "optionB": "耕地保护底线、粮食安全底线",
        "optionC": "不发生规模性返贫底线、不发生系统性风险底线",
        "optionD": "粮食安全底线、不发生区域性返贫底线",
        "correctAnswer": "A",
        "explanation": "一号文件明确"两个底线"：国家粮食安全底线、不发生规模性返贫致贫底线。",
        "difficulty": 2,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "粮食安全"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出的"三提升"指的是什么？",
        "optionA": "提升农业综合生产能力、乡村产业发展水平、乡村建设水平",
        "optionB": "提升粮食产量、农民收入、农业效益",
        "optionC": "提升农业科技水平、机械化水平、信息化水平",
        "optionD": "提升农村教育、医疗、养老水平",
        "correctAnswer": "A",
        "explanation": "一号文件提出"三提升"：提升农业综合生产能力、乡村产业发展水平、乡村建设水平、乡村治理水平。",
        "difficulty": 2,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出的"三重点"是什么？",
        "optionA": "产业振兴、人才振兴、文化振兴",
        "optionB": "提升农业综合生产能力、乡村产业发展水平、乡村建设水平",
        "optionC": "耕地保护、粮食安全、种业振兴",
        "optionD": "农村改革、城乡融合、绿色发展",
        "correctAnswer": "B",
        "explanation": "一号文件提出"三重点"：提升农业综合生产能力、乡村产业发展水平、乡村建设水平。",
        "difficulty": 2,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出的"载体"是什么？",
        "optionA": "打造全国现代寒旱特色农业先行基地",
        "optionB": "建设国家乡村振兴示范区",
        "optionC": "创建国家级农业现代化示范区",
        "optionD": "打造全国重要的农产品加工基地",
        "correctAnswer": "A",
        "explanation": "一号文件提出"载体"是打造全国现代寒旱特色农业先行基地。",
        "difficulty": 2,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "农业"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出的目标是：2026年全省第一产业增加值增长多少？",
        "optionA": "5%左右",
        "optionB": "6%左右",
        "optionC": "7%左右",
        "optionD": "8%左右",
        "correctAnswer": "B",
        "explanation": "一号文件提出2026年全省第一产业增加值增长6%左右，农村居民人均可支配收入增长6.5%左右。",
        "difficulty": 1,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "经济增长"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出全省第一产业增加值增长6%左右，农村居民人均可支配收入增长多少？",
        "optionA": "5%左右",
        "optionB": "6%左右",
        "optionC": "6.5%左右",
        "optionD": "7%左右",
        "correctAnswer": "C",
        "explanation": "农村居民人均可支配收入增长6.5%左右。",
        "difficulty": 1,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "农民收入"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出要保持省市两级常态化帮扶资金规模稳定，县级可根据帮扶任务合理安排什么？",
        "optionA": "人员",
        "optionB": "资金",
        "optionC": "土地",
        "optionD": "技术",
        "correctAnswer": "B",
        "explanation": "文件提出保持省市两级常态化帮扶资金规模稳定，县级可根据帮扶任务合理安排资金。",
        "difficulty": 1,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "帮扶"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出要构建什么全产业链发展体系？",
        "optionA": ""农头工尾、粮头食尾、畜头肉尾"",
        "optionB": ""粮头工尾、农头食尾、畜头肉尾"",
        "optionC": ""农头食尾、粮头工尾、畜头肉尾"",
        "optionD": ""畜头工尾、农头食尾、粮头肉尾"",
        "correctAnswer": "A",
        "explanation": "文件提出构建"农头工尾、粮头食尾、畜头肉尾"全产业链发展体系。",
        "difficulty": 2,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "产业发展"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃省委一号文件提出要坚持什么抓乡村振兴？",
        "optionA": "四级书记",
        "optionB": "五级书记",
        "optionC": "三级书记",
        "optionD": "六级书记",
        "correctAnswer": "B",
        "explanation": "文件提出坚持五级书记抓乡村振兴，健全省负总责、市县抓落实、乡村抓具体工作机制。",
        "difficulty": 1,
        "source": "2026年甘肃省委一号文件",
        "tags": ["甘肃时事", "一号文件", "乡村振兴"]
    },
    # ==================== 甘肃省"十五五"规划 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出全省经济总量连跨几个千亿元台阶？",
        "optionA": "3个",
        "optionB": "4个",
        "optionC": "5个",
        "optionD": "6个",
        "correctAnswer": "B",
        "explanation": "甘肃省"十五五"规划提出经济总量连跨4个千亿元台阶，2025年达到1.37万亿元。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "经济"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出到2025年全省经济总量达到多少？",
        "optionA": "1.2万亿元",
        "optionB": "1.3万亿元",
        "optionC": "1.37万亿元",
        "optionD": "1.5万亿元",
        "correctAnswer": "C",
        "explanation": "规划提出到2025年经济总量达到1.37万亿元。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": ""甘味"品牌实现中国区域农业形象品牌影响力指数百强榜"五连冠"，体现了甘肃省在哪个方面的成就？",
        "optionA": "工业品牌建设",
        "optionB": "农产品品牌建设",
        "optionC": "旅游品牌建设",
        "optionD": "文化品牌建设",
        "correctAnswer": "B",
        "explanation": ""甘味"品牌是甘肃省农产品区域公用品牌，实现百强榜"五连冠"。",
        "difficulty": 2,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "甘味品牌"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"时期面临的五大机遇是什么？",
        "optionA": "产业升级、扩大内需、开放合作、绿色转型、共享发展",
        "optionB": "科技创新、产业升级、扩大内需、开放合作、绿色转型",
        "optionC": "科技创新、产业升级、扩大内需、共享发展、绿色发展",
        "optionD": "科技创新、产业升级、扩大内需、开放合作、共享发展",
        "correctAnswer": "B",
        "explanation": "五大机遇：产业升级机遇、扩大内需机遇、开放合作机遇、绿色转型机遇、共享发展机遇。",
        "difficulty": 2,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要打造的"五大功能定位"不包括以下哪项？",
        "optionA": "国家生态屏障",
        "optionB": "能源基地",
        "optionC": "算力节点",
        "optionD": "金融中心",
        "correctAnswer": "D",
        "explanation": "五大功能定位：国家生态屏障、能源基地、算力节点、文旅高地、战略通道向西开放。",
        "difficulty": 2,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中"战略通道向西开放"体现了甘肃省的什么优势？",
        "optionA": "区位优势",
        "optionB": "资源优势",
        "optionC": "人才优势",
        "optionD": "技术优势",
        "correctAnswer": "A",
        "explanation": "甘肃省地处丝绸之路经济带黄金段，具有向西开放的区位优势。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "向西开放"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要打造什么目标？",
        "optionA": ""七地一屏一通道"",
        "optionB": ""六地一屏一通道"",
        "optionC": ""八地一屏一通道"",
        "optionD": ""五地一屏一通道"",
        "correctAnswer": "A",
        "explanation": "规划提出打造"七地一屏一通道"目标。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中"七地一屏一通道"的"一通道"指的是什么？",
        "optionA": "向西开放的战略通道",
        "optionB": "向东开放的战略通道",
        "optionC": "向南开放的战略通道",
        "optionD": "向北开放的战略通道",
        "correctAnswer": "A",
        "explanation": ""一通道"指战略通道向西开放。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划"]
    },
    # ==================== 甘肃省"十五五"规划 - 区域联动 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，兰州市的定位是什么？",
        "optionA": "西部地区重要中心城市、全国性综合交通枢纽城市",
        "optionB": "全国重要工业基地",
        "optionC": "全国科技创新中心",
        "optionD": "国际旅游城市",
        "correctAnswer": "A",
        "explanation": "兰州市定位：西部地区重要中心城市、全国性综合交通枢纽城市。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "兰州"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，白银市的定位是什么？",
        "optionA": "兰白经济圈协同发展重要增长极",
        "optionB": "国家新能源基地",
        "optionC": "全国现代种业发展高地",
        "optionD": "全国民族团结进步示范地",
        "correctAnswer": "A",
        "explanation": "白银市定位：兰白经济圈协同发展重要增长极。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "白银"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，定西市的定位是什么？",
        "optionA": "国家中医药传承创新发展样板区、全国马铃薯全产业链发展高地",
        "optionB": "全国民族团结进步示范地",
        "optionC": "国际知名旅游目的地",
        "optionD": "全国荒漠化治理样板区",
        "correctAnswer": "A",
        "explanation": "定西市定位：国家中医药传承创新发展样板区、全国马铃薯全产业链发展高地。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "定西"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，临夏州的定位是什么？",
        "optionA": "全国民族团结进步示范地、国内知名的黄河主题旅游目的地",
        "optionB": "全国荒漠化治理样板区",
        "optionC": "国际知名旅游目的地",
        "optionD": "全国现代种业发展高地",
        "correctAnswer": "A",
        "explanation": "临夏州定位：全国民族团结进步示范地、国内知名的黄河主题旅游目的地。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "临夏"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，武威市的定位是什么？",
        "optionA": "全国荒漠化治理样板区",
        "optionB": "全国现代种业发展高地",
        "optionC": "国际知名旅游目的地",
        "optionD": "全国民族团结进步示范地",
        "correctAnswer": "A",
        "explanation": "武威市定位：全国荒漠化治理样板区。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "武威"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，张掖市的定位是什么？",
        "optionA": "全国现代种业发展高地、国际知名旅游目的地",
        "optionB": "全国荒漠化治理样板区",
        "optionC": "全国民族团结进步示范地",
        "optionD": "国家新能源基地",
        "correctAnswer": "A",
        "explanation": "张掖市定位：全国现代种业发展高地、国际知名旅游目的地。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "张掖"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，金昌市的定位是什么？",
        "optionA": "镍都",
        "optionB": "钢城",
        "optionC": "铜城",
        "optionD": "铝都",
        "correctAnswer": "A",
        "explanation": "金昌市定位：镍都。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "金昌"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，酒泉市的定位是什么？",
        "optionA": "国家能源基地、做强区域中心城市、打造世界文化高地",
        "optionB": "全国现代种业发展高地",
        "optionC": "全国荒漠化治理样板区",
        "optionD": "全国民族团结进步示范地",
        "correctAnswer": "A",
        "explanation": "酒泉市定位：国家能源基地、做强区域中心城市、打造世界文化高地。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "酒泉"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，嘉峪关市的定位是什么？",
        "optionA": "全域城市化建设",
        "optionB": "全国重要工业基地",
        "optionC": "国际旅游城市",
        "optionD": "全国科技创新中心",
        "correctAnswer": "A",
        "explanation": "嘉峪关市定位：全域城市化建设。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "嘉峪关"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，庆阳市的定位是什么？",
        "optionA": "造能源产业大基地、"东数西算"枢纽地、红色文化传承地",
        "optionB": "全国现代种业发展高地",
        "optionC": "全国荒漠化治理样板区",
        "optionD": "全国民族团结进步示范地",
        "correctAnswer": "A",
        "explanation": "庆阳市定位：造能源产业大基地、"东数西算"枢纽地、红色文化传承地。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "庆阳"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，平凉市的定位是什么？",
        "optionA": "西北文旅康养融合先行区、全国生态文明建设先进城市（国家森林城市）",
        "optionB": "全国现代种业发展高地",
        "optionC": "全国荒漠化治理样板区",
        "optionD": "全国民族团结进步示范地",
        "correctAnswer": "A",
        "explanation": "平凉市定位：西北文旅康养融合先行区、全国生态文明建设先进城市（国家森林城市）。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "平凉"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，天水市的定位是什么？",
        "optionA": "陇东南区域现代化中心城市",
        "optionB": "全国重要工业基地",
        "optionC": "国际旅游城市",
        "optionD": "全国科技创新中心",
        "correctAnswer": "A",
        "explanation": "天水市定位：陇东南区域现代化中心城市。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "天水"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，陇南市的定位是什么？",
        "optionA": "美丽城市先行区",
        "optionB": "全国重要工业基地",
        "optionC": "国际旅游城市",
        "optionD": "全国科技创新中心",
        "correctAnswer": "A",
        "explanation": "陇南市定位：美丽城市先行区。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "陇南"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，甘南州的定位是什么？",
        "optionA": "民族团结的和谐甘南、生态优良的美丽甘南",
        "optionB": "全国重要工业基地",
        "optionC": "国际旅游城市",
        "optionD": "全国科技创新中心",
        "correctAnswer": "A",
        "explanation": "甘南州定位：民族团结的和谐甘南、生态优良的美丽甘南。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "甘南"]
    },
    # ==================== 甘肃省"十五五"规划 - 生态安全 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中生态安全的核心理念是什么？",
        "optionA": "绿水青山就是金山银山",
        "optionB": "生态优先、绿色发展",
        "optionC": "保护为主、开发为辅",
        "optionD": "生态修复、环境治理",
        "correctAnswer": "A",
        "explanation": "生态安全的核心理念是"绿水青山就是金山银山"。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "生态"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中，生态安全的"牵引"是什么？",
        "optionA": "碳达峰碳中和",
        "optionB": "生态保护修复",
        "optionC": "污染治理",
        "optionD": "绿色发展",
        "correctAnswer": "A",
        "explanation": "生态安全的"牵引"是碳达峰碳中和。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "双碳"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要深化哪个国家公园建设？",
        "optionA": "大熊猫国家公园",
        "optionB": "三江源国家公园",
        "optionC": "祁连山国家公园",
        "optionD": "若尔盖国家公园",
        "correctAnswer": "A",
        "explanation": "规划提出深化大熊猫国家公园建设，推进祁连山、若尔盖国家公园创建。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "国家公园"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要推进哪两个国家公园创建？",
        "optionA": "祁连山、若尔盖",
        "optionB": "祁连山、贺兰山",
        "optionC": "若尔盖、三江源",
        "optionD": "祁连山、三江源",
        "correctAnswer": "A",
        "explanation": "规划提出推进祁连山、若尔盖国家公园创建。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "国家公园"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划中"三北"防护林三大攻坚战之二指的是什么？",
        "optionA": "黄河"几字弯"攻坚战",
        "optionB": "河西走廊—塔克拉玛干沙漠边缘阻击战",
        "optionC": "科尔沁沙地歼灭战",
        "optionD": "浑善达克沙地歼灭战",
        "correctAnswer": "A",
        "explanation": ""三北"防护林三大攻坚战之二是黄河"几字弯"攻坚战。",
        "difficulty": 2,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "三北防护林"]
    },
    # ==================== 2026年甘肃政府工作报告 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出"十四五"时期主要工作回顾中，民生支出占一般公共预算支出比例稳定在多少？",
        "optionA": "70%左右",
        "optionB": "75%左右",
        "optionC": "80%左右",
        "optionD": "85%左右",
        "correctAnswer": "C",
        "explanation": "报告提出民生支出占一般公共预算支出比例稳定在80%左右。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "民生支出"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出甘肃省人均博物馆拥有量位居全国第几？",
        "optionA": "第1位",
        "optionB": "第2位",
        "optionC": "第3位",
        "optionD": "第4位",
        "correctAnswer": "A",
        "explanation": "报告提出人均博物馆拥有量位居全国第1位。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "博物馆"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出甘肃省新增了多少家5A级景区？",
        "optionA": "2家",
        "optionB": "3家",
        "optionC": "4家",
        "optionD": "5家",
        "correctAnswer": "B",
        "explanation": "报告提出新增炳灵寺、官鹅沟、冶力关3家5A级景区共8个5A。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "景区"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出临夏地质公园获批什么称号？",
        "optionA": "世界地质公园",
        "optionB": "国家地质公园",
        "optionC": "世界自然遗产",
        "optionD": "国家自然遗产",
        "correctAnswer": "A",
        "explanation": "报告提出临夏地质公园获批世界地质公园。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "临夏"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出皋兰什川古梨园系统入选什么？",
        "optionA": "全球重要农业文化遗产",
        "optionB": "世界文化遗产",
        "optionC": "国家重要农业文化遗产",
        "optionD": "世界农业遗产",
        "correctAnswer": "A",
        "explanation": "报告提出皋兰什川古梨园系统入选全球重要农业文化遗产。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "农业遗产"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出"十五五"时期是全面发力的关键时期，要持续盘活存量、培育增量、增强能量、提高什么？",
        "optionA": "质量",
        "optionB": "效率",
        "optionC": "效益",
        "optionD": "速度",
        "correctAnswer": "A",
        "explanation": "报告提出持续盘活存量、培育增量、增强能量、提高质量、做大总量。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出全省社会研发经费投入年均增长多少？",
        "optionA": "8.5%",
        "optionB": "9%",
        "optionC": "9.5%",
        "optionD": "10%",
        "correctAnswer": "C",
        "explanation": "报告提出全省社会研发经费投入年均增长9.5%。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "科技创新"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出全员劳动生产率年均增长多少？",
        "optionA": "4%",
        "optionB": "5%",
        "optionC": "6%",
        "optionD": "7%",
        "correctAnswer": "B",
        "explanation": "报告提出全员劳动生产率年均增长5%。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "劳动生产率"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要积极申建什么？",
        "optionA": "中国（甘肃）自由贸易试验区",
        "optionB": "中国（兰州）自由贸易试验区",
        "optionC": "中国（甘肃）综合保税区",
        "optionD": "中国（兰州）综合保税区",
        "correctAnswer": "A",
        "explanation": "报告提出积极申建中国（甘肃）自由贸易试验区。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "自贸区"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要坚持稳中求进工作总基调，坚持以什么为中心？",
        "optionA": "经济建设",
        "optionB": "社会发展",
        "optionC": "民生改善",
        "optionD": "科技创新",
        "correctAnswer": "A",
        "explanation": "报告提出坚持以经济建设为中心。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要牢牢把握什么这个首要任务？",
        "optionA": "高质量发展",
        "optionB": "经济增长",
        "optionC": "社会稳定",
        "optionD": "民生改善",
        "correctAnswer": "A",
        "explanation": "报告提出牢牢把握高质量发展这个首要任务。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "高质量发展"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出"三个着眼"不包括以下哪项？",
        "optionA": "着眼扩大内需",
        "optionB": "优化供给",
        "optionC": "做优增量、盘活存量",
        "optionD": "防范化解重点领域风险",
        "correctAnswer": "C",
        "explanation": ""三个着眼"：着眼扩大内需、优化供给、做优增量、盘活存量，着眼防范化解重点领域风险，着眼稳就业、稳企业、稳市场、稳预期。",
        "difficulty": 2,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要深入实施什么行动？",
        "optionA": "强科技、强工业、强县域、强省会、强基础",
        "optionB": "强科技、强工业、强县域、强省会",
        "optionC": "强科技、强工业、强县域",
        "optionD": "强科技、强工业",
        "correctAnswer": "A",
        "explanation": "报告提出深入实施强科技、强工业、强县域、强省会、强基础行动。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "五强行动"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出新能源装机规模达到多少？",
        "optionA": "8000万千瓦",
        "optionB": "9000万千瓦",
        "optionC": "1亿千瓦",
        "optionD": "1.2亿千瓦",
        "correctAnswer": "C",
        "explanation": "报告提出新能源装机规模达到1亿千瓦。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "新能源"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要建成什么工程？",
        "optionA": "陇电入浙工程",
        "optionB": "陇电入川工程",
        "optionC": "陇电入沪工程",
        "optionD": "陇电入粤工程",
        "correctAnswer": "A",
        "explanation": "报告提出建成陇电入浙工程，开工建设陇电入川工程。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "电力工程"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要开工建设什么工程？",
        "optionA": "陇电入川工程",
        "optionB": "陇电入浙工程",
        "optionC": "陇电入沪工程",
        "optionD": "陇电入粤工程",
        "correctAnswer": "A",
        "explanation": "报告提出开工建设陇电入川工程。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "电力工程"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要加快兰州中欧班列节点城市建设，深入实施什么行动？",
        "optionA": ""扩量、提质、延链、增效"",
        "optionB": ""扩量、提质"",
        "optionC": ""提质、延链"",
        "optionD": ""扩量、延链、增效"",
        "correctAnswer": "A",
        "explanation": "报告提出深入实施"扩量、提质、延链、增效"行动。",
        "difficulty": 2,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "中欧班列"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要强化什么支撑体系建设？",
        "optionA": "农业科技",
        "optionB": "工业科技",
        "optionC": "信息科技",
        "optionD": "教育科技",
        "correctAnswer": "A",
        "explanation": "报告提出强化农业科技支撑体系建设。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "农业科技"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要围绕"一核三带、多点支撑"区域发展格局，发展壮大哪个区域经济带？",
        "optionA": "河西走廊、陇东南经济带",
        "optionB": "兰白经济带",
        "optionC": "陇中经济带",
        "optionD": "甘南经济带",
        "correctAnswer": "A",
        "explanation": "报告提出发展壮大河西走廊、陇东南经济带。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "区域发展"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要加快建设哪个城市群？",
        "optionA": "兰白定临城市群",
        "optionB": "兰白城市群",
        "optionC": "陇中城市群",
        "optionD": "河西城市群",
        "correctAnswer": "A",
        "explanation": "报告提出加快建设兰白定临城市群。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "城市群"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "2026年甘肃政府工作报告提出要做大做强哪两个区域中心城市？",
        "optionA": "酒泉、天水",
        "optionB": "兰州、天水",
        "optionC": "酒泉、庆阳",
        "optionD": "天水、庆阳",
        "correctAnswer": "A",
        "explanation": "报告提出做大做强酒泉、天水两个区域中心城市。",
        "difficulty": 1,
        "source": "2026年甘肃政府工作报告",
        "tags": ["甘肃时事", "政府工作报告", "区域中心"]
    },
    # ==================== 高质量发展框架 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的首要任务是什么？",
        "optionA": "科技创新",
        "optionB": "产业升级",
        "optionC": "扩大内需",
        "optionD": "深化改革",
        "correctAnswer": "A",
        "explanation": "科技创新是高质量发展的首要任务。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "科技创新"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的引擎是什么？",
        "optionA": "科技创新",
        "optionB": "产业升级",
        "optionC": "扩大内需",
        "optionD": "深化改革",
        "correctAnswer": "A",
        "explanation": "科技创新是高质量发展的引擎。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "科技创新"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的基点是什么？",
        "optionA": "扩大内需",
        "optionB": "科技创新",
        "optionC": "深化改革",
        "optionD": "产业升级",
        "correctAnswer": "A",
        "explanation": "扩大内需是高质量发展的基点。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "扩大内需"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的根本动力是什么？",
        "optionA": "科技创新",
        "optionB": "深化改革",
        "optionC": "产业升级",
        "optionD": "扩大内需",
        "correctAnswer": "B",
        "explanation": "深化改革是高质量发展的根本动力。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "深化改革"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的必由之路是什么？",
        "optionA": "科技创新",
        "optionB": "产业升级",
        "optionC": "扩大内需",
        "optionD": "深化改革",
        "correctAnswer": "B",
        "explanation": "产业升级是高质量发展的必由之路。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "产业升级"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的重要基础是什么？",
        "optionA": "科技创新",
        "optionB": "产业升级",
        "optionC": "扩大内需",
        "optionD": "深化改革",
        "correctAnswer": "C",
        "explanation": "扩大内需是高质量发展的重要基础。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "扩大内需"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的内在要求是什么？",
        "optionA": "科技创新",
        "optionB": "产业升级",
        "optionC": "扩大内需",
        "optionD": "深化改革",
        "correctAnswer": "D",
        "explanation": "深化改革是高质量发展的内在要求。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "深化改革"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的应有之义是什么？",
        "optionA": "科技创新",
        "optionB": "产业升级",
        "optionC": "扩大内需",
        "optionD": "绿色发展",
        "correctAnswer": "D",
        "explanation": "绿色发展是高质量发展的应有之义。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "绿色发展"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的出发点和落脚点是什么？",
        "optionA": "满足人民日益增长的美好生活需要",
        "optionB": "推动经济持续健康发展",
        "optionC": "实现中华民族伟大复兴",
        "optionD": "建设现代化经济体系",
        "correctAnswer": "A",
        "explanation": "满足人民日益增长的美好生活需要是高质量发展的出发点和落脚点。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "民生"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的鲜明底色是什么？",
        "optionA": "科技创新",
        "optionB": "产业升级",
        "optionC": "绿色发展",
        "optionD": "深化改革",
        "correctAnswer": "C",
        "explanation": "绿色发展是高质量发展的鲜明底色。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "绿色发展"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的底线任务是什么？",
        "optionA": "维护安全和稳定",
        "optionB": "推动经济持续健康发展",
        "optionC": "实现中华民族伟大复兴",
        "optionD": "建设现代化经济体系",
        "correctAnswer": "A",
        "explanation": "维护安全和稳定是高质量发展的底线任务。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "安全稳定"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "高质量发展的根本保证是什么？",
        "optionA": "党的领导",
        "optionB": "科技创新",
        "optionC": "产业升级",
        "optionD": "深化改革",
        "correctAnswer": "A",
        "explanation": "党的领导是高质量发展的根本保证。",
        "difficulty": 1,
        "source": "高质量发展理论",
        "tags": ["高质量发展", "党的领导"]
    },
    # ==================== 甘肃省"十五五"规划 - 经济发展 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划的中心是什么？",
        "optionA": "经济建设",
        "optionB": "社会发展",
        "optionC": "民生改善",
        "optionD": "科技创新",
        "correctAnswer": "A",
        "explanation": "规划提出坚持以经济建设为中心。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划的主题是什么？",
        "optionA": "推动高质量发展",
        "optionB": "全面深化改革",
        "optionC": "扩大对外开放",
        "optionD": "推动经济持续健康发展",
        "correctAnswer": "A",
        "explanation": "规划提出主题是推动高质量发展。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "高质量发展"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划的根本动力是什么？",
        "optionA": "科技创新",
        "optionB": "深化改革",
        "optionC": "产业升级",
        "optionD": "扩大内需",
        "correctAnswer": "B",
        "explanation": "规划提出深化改革是根本动力。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "深化改革"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划的根本目的是什么？",
        "optionA": "满足人民日益增长的美好生活需要",
        "optionB": "推动经济持续健康发展",
        "optionC": "实现中华民族伟大复兴",
        "optionD": "建设现代化经济体系",
        "correctAnswer": "A",
        "explanation": "规划提出满足人民日益增长的美好生活需要是根本目的。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "民生"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划的根本保证是什么？",
        "optionA": "全面从严治党",
        "optionB": "党的领导",
        "optionC": "科技创新",
        "optionD": "深化改革",
        "correctAnswer": "A",
        "explanation": "规划提出全面从严治党是根本保证。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "从严治党"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出的"五强行动"是什么？",
        "optionA": "强科技、强工业、强县域、强省会、强基础",
        "optionB": "强科技、强工业、强县域、强省会",
        "optionC": "强科技、强工业、强县域",
        "optionD": "强科技、强工业",
        "correctAnswer": "A",
        "explanation": "规划提出"五强行动"：强科技、强工业、强县域、强省会、强基础。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "五强行动"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要更加注重加强党的什么？",
        "optionA": "全面领导",
        "optionB": "政治建设",
        "optionC": "思想建设",
        "optionD": "组织建设",
        "correctAnswer": "A",
        "explanation": "规划提出更加注重加强党的全面领导。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "党的领导"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要更加注重保障和改善什么？",
        "optionA": "民生",
        "optionB": "经济",
        "optionC": "社会",
        "optionD": "生态",
        "correctAnswer": "A",
        "explanation": "规划提出更加注重保障和改善民生。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "民生"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要更加注重提升发展什么？",
        "optionA": "质效",
        "optionB": "速度",
        "optionC": "规模",
        "optionD": "总量",
        "correctAnswer": "A",
        "explanation": "规划提出更加注重提升发展质效。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要更加注重什么数智赋能？",
        "optionA": "数据",
        "optionB": "科技",
        "optionC": "产业",
        "optionD": "金融",
        "correctAnswer": "A",
        "explanation": "规划提出更加注重数据数智赋能。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "数智赋能"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要更加注重激发市场什么？",
        "optionA": "活力",
        "optionB": "潜力",
        "optionC": "动力",
        "optionD": "能力",
        "correctAnswer": "A",
        "explanation": "规划提出更加注重激发市场活力。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "市场活力"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要更加注重主动创安主动创稳，体现了什么理念？",
        "optionA": "安全发展",
        "optionB": "创新发展",
        "optionC": "协调发展",
        "optionD": "绿色发展",
        "correctAnswer": "A",
        "explanation": "主动创安主动创稳体现了安全发展理念。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "安全发展"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要把发展经济的着力点放在什么上？",
        "optionA": "实体经济",
        "optionB": "虚拟经济",
        "optionC": "金融经济",
        "optionD": "服务经济",
        "correctAnswer": "A",
        "explanation": "规划提出把发展经济的着力点放在实体经济上。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "实体经济"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要打造"甘肃制造"、"甘肃创造"、"甘肃什么"？",
        "optionA": "设计",
        "optionB": "创新",
        "optionC": "智造",
        "optionD": "品牌",
        "correctAnswer": "A",
        "explanation": "规划提出打造"甘肃制造"、"甘肃创造"、"甘肃设计"。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "制造业"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要做强做大什么八大特色产业集群？",
        "optionA": "牛羊果菜薯药粮种",
        "optionB": "牛羊果菜薯药",
        "optionC": "牛羊果菜",
        "optionD": "牛羊",
        "correctAnswer": "A",
        "explanation": "规划提出做强做大"牛羊果菜薯药粮种"八大特色产业集群。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "特色产业"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要坚持产量产能、生产生态、增产增收一起抓，构建什么全产业链发展体系？",
        "optionA": ""农头工尾、粮头食尾、畜头肉尾"",
        "optionB": ""粮头工尾、农头食尾、畜头肉尾"",
        "optionC": ""农头食尾、粮头工尾、畜头肉尾"",
        "optionD": ""畜头工尾、农头食尾、粮头肉尾"",
        "correctAnswer": "A",
        "explanation": "规划提出构建"农头工尾、粮头食尾、畜头肉尾"全产业链发展体系。",
        "difficulty": 2,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "产业发展"]
    },
    # ==================== 甘肃省"十五五"规划 - 乡村振兴 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要坚持什么抓乡村振兴？",
        "optionA": "五级书记",
        "optionB": "四级书记",
        "optionC": "三级书记",
        "optionD": "六级书记",
        "correctAnswer": "A",
        "explanation": "规划提出坚持五级书记抓乡村振兴，健全省负总责、市县抓落实、乡村抓具体工作机制。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "乡村振兴"]
    },
    # ==================== 甘肃省"十五五"规划 - 人口与民生 ====================
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要以什么为重点完善人口发展战略？",
        "optionA": "应对人口老龄化、少子化",
        "optionB": "促进人口均衡发展",
        "optionC": "优化人口结构",
        "optionD": "提高人口素质",
        "correctAnswer": "A",
        "explanation": "规划提出以应对人口老龄化、少子化为重点完善人口发展战略。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "人口"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要建设什么型社会？",
        "optionA": "生育友好型",
        "optionB": "老年友好型",
        "optionC": "儿童友好型",
        "optionD": "家庭友好型",
        "correctAnswer": "A",
        "explanation": "规划提出建设生育友好型社会，努力稳定出生人口规模。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "生育"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要推动从以治病为中心向以什么为中心转变？",
        "optionA": "健康",
        "optionB": "预防",
        "optionC": "康复",
        "optionD": "保健",
        "correctAnswer": "A",
        "explanation": "规划提出推动从以治病为中心向以健康为中心转变。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "健康"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出实现人民对美好生活的向往是中国式现代化的什么？",
        "optionA": "出发点和落脚点",
        "optionB": "根本目的",
        "optionC": "基本要求",
        "optionD": "重要基础",
        "correctAnswer": "A",
        "explanation": "规划提出实现人民对美好生活的向往是中国式现代化的出发点和落脚点。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "现代化"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要加强什么性、基础性、兜底性民生建设？",
        "optionA": "普惠",
        "optionB": "基本",
        "optionC": "特殊",
        "optionD": "专项",
        "correctAnswer": "A",
        "explanation": "规划提出加强普惠性、基础性、兜底性民生建设。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "民生"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要着力解决什么矛盾？",
        "optionA": "结构性就业矛盾",
        "optionB": "供需矛盾",
        "optionC": "区域矛盾",
        "optionD": "城乡矛盾",
        "correctAnswer": "A",
        "explanation": "规划提出着力解决结构性就业矛盾。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "就业"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要构建什么型发展方式？",
        "optionA": "就业友好型",
        "optionB": "环境友好型",
        "optionC": "资源节约型",
        "optionD": "创新驱动型",
        "correctAnswer": "A",
        "explanation": "规划提出构建就业友好型发展方式。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "就业"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要推动形成什么型分配格局？",
        "optionA": "橄榄型",
        "optionB": "金字塔型",
        "optionC": "倒金字塔型",
        "optionD": "哑铃型",
        "correctAnswer": "A",
        "explanation": "规划提出推动形成橄榄型分配格局。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "分配"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要推进什么养老保险全国统筹？",
        "optionA": "基本养老",
        "optionB": "企业年金",
        "optionC": "商业养老",
        "optionD": "职业年金",
        "correctAnswer": "A",
        "explanation": "规划提出推进基本养老保险全国统筹。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "养老保险"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要推进什么基本医疗保险省级统筹？",
        "optionA": "基本医疗保险",
        "optionB": "大病保险",
        "optionC": "医疗救助",
        "optionD": "商业健康保险",
        "correctAnswer": "A",
        "explanation": "规划提出推进基本医疗保险省级统筹。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "医疗保险"]
    },
    {
        "categoryId": 18,
        "questionType": 1,
        "content": "甘肃省"十五五"规划提出要以什么为导向深化公立医院改革？",
        "optionA": "公益性",
        "optionB": "效益性",
        "optionC": "公平性",
        "optionD": "效率性",
        "correctAnswer": "A",
        "explanation": "规划提出以公益性为导向深化公立医院改革。",
        "difficulty": 1,
        "source": "甘肃省"十五五"规划",
        "tags": ["甘肃时事", "十五五规划", "医疗改革"]
    },
]


def create_database():
    """创建数据库"""
    db_path = "linxia_exam_db"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # 创建分类表
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
            updated_at INTEGER DEFAULT (strftime('%s', 'now'))
        )
    """)
    
    # 创建题目表
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
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_questions_category ON questions(category_id)")
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_questions_type ON questions(question_type)")
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_questions_difficulty ON questions(difficulty)")
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_categories_parent ON categories(parent_id)")
    
    conn.commit()
    return conn


def init_categories(conn):
    """初始化分类"""
    cursor = conn.cursor()
    
    # 检查是否已有分类
    cursor.execute("SELECT COUNT(*) FROM categories")
    if cursor.fetchone()[0] > 0:
        print("分类已存在，跳过初始化")
        return
    
    # 一级分类
    categories = [
        (0, "公共基础知识", 1, 1, "school", "#1B5E20"),
        (0, "时事政治", 1, 2, "newspaper", "#E65100"),
        (0, "临夏州情", 1, 3, "location_city", "#1A237E"),
        (0, "行政职业能力测验", 1, 4, "psychology", "#4A148C"),
        (0, "申论", 1, 5, "edit_note", "#2E7D32"),
    ]
    
    for parent_id, name, level, sort_order, icon, color in categories:
        cursor.execute("""
            INSERT INTO categories (parent_id, name, level, sort_order, icon, color)
            VALUES (?, ?, ?, ?, ?, ?)
        """, (parent_id, name, level, sort_order, icon, color))
    
    # 时事政治二级分类
    cursor.execute("SELECT id FROM categories WHERE name = '时事政治'")
    shizheng_id = cursor.fetchone()[0]
    
    shizheng_subs = [
        (shizheng_id, "国内时事", 2, 1, "flag", "#1565C0"),
        (shizheng_id, "国际时事", 2, 2, "public", "#00695C"),
        (shizheng_id, "甘肃/临夏时事", 2, 3, "location_on", "#BF360C"),
        (shizheng_id, "政策文件", 2, 4, "description", "#4A148C"),
        (shizheng_id, "重要讲话", 2, 5, "record_voice_over", "#C62828"),
    ]
    
    for parent_id, name, level, sort_order, icon, color in shizheng_subs:
        cursor.execute("""
            INSERT INTO categories (parent_id, name, level, sort_order, icon, color)
            VALUES (?, ?, ?, ?, ?, ?)
        """, (parent_id, name, level, sort_order, icon, color))
    
    conn.commit()
    print("分类初始化完成")


def insert_questions(conn):
    """插入题目"""
    cursor = conn.cursor()
    
    # 检查是否已有题目
    cursor.execute("SELECT COUNT(*) FROM questions")
    existing = cursor.fetchone()[0]
    if existing > 0:
        print(f"已有 {existing} 道题目")
        confirm = input("是否清空重新导入？(y/N): ").strip().lower()
        if confirm != 'y':
            print("取消导入")
            return
        cursor.execute("DELETE FROM questions")
        conn.commit()
    
    now = int(datetime.now().timestamp())
    
    # 获取分类ID映射
    cursor.execute("SELECT id, name FROM categories")
    cat_map = {name: id for id, name in cursor.fetchall()}
    
    inserted = 0
    for q in CURRENT_AFFAIRS_QUESTIONS:
        # 获取题目内容的MD5哈希用于去重
        content_hash = hashlib.md5(q["content"].encode()).hexdigest()[:16]
        
        # 检查是否已存在
        cursor.execute("SELECT 1 FROM questions WHERE category_id = ? AND content LIKE ?", 
                      (q["categoryId"], f"%{q['content'][:50]}%"))
        if cursor.fetchone():
            continue
        
        cursor.execute("""
            INSERT INTO questions (category_id, question_type, content, option_a, option_b, 
                                 option_c, option_d, option_e, option_f, correct_answer, 
                                 explanation, difficulty, source, tags, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            q["categoryId"],
            q["questionType"],
            q["content"],
            q["optionA"],
            q["optionB"],
            q["optionC"],
            q["optionD"],
            q.get("optionE", ""),
            q.get("optionF", ""),
            q["correctAnswer"],
            q["explanation"],
            q["difficulty"],
            q["source"],
            json.dumps(q.get("tags", []), ensure_ascii=False),
            now,
            now
        ))
        inserted += 1
    
    # 更新分类题目数量
    cursor.execute("""
        UPDATE categories SET question_count = (
            SELECT COUNT(*) FROM questions WHERE category_id = categories.id
        )
    """)
    
    conn.commit()
    print(f"成功导入 {inserted} 道时政题目")


def export_to_json(conn, output_path):
    """导出为JSON"""
    cursor = conn.cursor()
    
    cursor.execute("SELECT * FROM categories ORDER BY level, sort_order")
    categories = [dict(zip([col[0] for col in cursor.description], row)) for row in cursor.fetchall()]
    
    cursor.execute("SELECT * FROM questions")
    questions = [dict(zip([col[0] for col in cursor.description], row)) for row in cursor.fetchall()]
    
    data = {
        "version": datetime.now().strftime("%Y.%m.%d"),
        "description": "临夏事业编考试题库 - 时政专题",
        "totalQuestions": len(questions),
        "categories": categories,
        "questions": questions
    }
    
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    print(f"已导出到: {output_path}")


def main():
    print("=" * 60)
    print("临夏事业编题库 - 时政题目导入工具")
    print("=" * 60)
    
    # 创建数据库
    conn = create_database()
    
    # 初始化分类
    init_categories(conn)
    
    # 插入题目
    insert_questions(conn)
    
    # 显示统计
    cursor = conn.cursor()
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
    
    # 导出JSON
    export_to_json(conn, "current_affairs_questions.json")
    
    conn.close()
    print("\n✨ 完成！")


if __name__ == "__main__":
    main()