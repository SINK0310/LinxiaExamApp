#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
扩充题库 - 甘肃省基本情况 + 分模块扩充 + 生成4套子卷
"""

import sqlite3
import json
import random
from datetime import datetime

# ==================== 甘肃省基本情况题目 ====================
GANSU_BASIC_QUESTIONS = [
    # 地理位置
    {"categoryId": 3, "questionType": 1, "content": "甘肃省位于中国哪个方位？", "optionA": "西北地区", "optionB": "华北地区", "optionC": "华东地区", "optionD": "西南地区", "correctAnswer": "A", "explanation": "甘肃省位于中国西北地区，黄河上游。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省的简称是什么？", "optionA": "甘", "optionB": "陇", "optionC": "甘或陇", "optionD": "秦", "correctAnswer": "C", "explanation": "甘肃省简称甘或陇。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省的省会城市是哪个？", "optionA": "天水", "optionB": "兰州", "optionC": "酒泉", "optionD": "白银", "correctAnswer": "B", "explanation": "甘肃省省会是兰州市。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省的总面积约为多少万平方公里？", "optionA": "42.58", "optionB": "45.58", "optionC": "48.58", "optionD": "52.58", "correctAnswer": "A", "explanation": "甘肃省总面积约42.58万平方公里。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省下辖多少个地级市和自治州？", "optionA": "12个", "optionB": "13个", "optionC": "14个", "optionD": "15个", "correctAnswer": "C", "explanation": "甘肃省下辖12个地级市和2个自治州，共14个。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃行政区划"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省地形呈什么形状？", "optionA": "圆形", "optionB": "方形", "optionC": "狭长形", "optionD": "三角形", "correctAnswer": "C", "explanation": "甘肃省地形狭长，像一柄玉如意。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省地处哪些高原的交汇地带？", "optionA": "黄土高原、青藏高原、内蒙古高原", "optionB": "黄土高原、云贵高原、内蒙古高原", "optionC": "青藏高原、云贵高原、内蒙古高原", "optionD": "黄土高原、青藏高原、云贵高原", "correctAnswer": "A", "explanation": "甘肃地处黄土高原、青藏高原和内蒙古高原三大高原交汇地带。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    {"categoryId": 3, "questionType": 1, "content": "黄河流经甘肃省多少个市州？", "optionA": "5个", "optionB": "6个", "optionC": "7个", "optionD": "8个", "correctAnswer": "C", "explanation": "黄河流经甘肃省7个市州。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    
    # 人口民族
    {"categoryId": 3, "questionType": 1, "content": "甘肃省常住人口约为多少万人？", "optionA": "2200", "optionB": "2400", "optionC": "2600", "optionD": "2800", "correctAnswer": "B", "explanation": "甘肃省常住人口约2400万人。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃人口"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省有哪个少数民族自治州？", "optionA": "临夏回族自治州", "optionB": "甘南藏族自治州", "optionC": "临夏回族自治州和甘南藏族自治州", "optionD": "天祝藏族自治县", "correctAnswer": "C", "explanation": "甘肃省有临夏回族自治州和甘南藏族自治州两个自治州。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃民族"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省世居的少数民族有多少个？", "optionA": "14个", "optionB": "15个", "optionC": "16个", "optionD": "17个", "correctAnswer": "C", "explanation": "甘肃省有16个世居少数民族。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃民族"]},
    
    # 经济发展
    {"categoryId": 3, "questionType": 1, "content": "甘肃省2025年GDP总量约为多少亿元？", "optionA": "1.1万亿", "optionB": "1.2万亿", "optionC": "1.3万亿", "optionD": "1.4万亿", "correctAnswer": "C", "explanation": "甘肃省2025年GDP总量约1.3万亿元。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃经济"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省的支柱产业不包括以下哪项？", "optionA": "石油化工", "optionB": "有色金属冶炼", "optionC": "电子信息", "optionD": "能源电力", "correctAnswer": "C", "explanation": "甘肃省支柱产业包括石油化工、有色金属冶炼、能源电力等，电子信息不是支柱产业。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃经济"]},
    
    # 历史文化
    {"categoryId": 3, "questionType": 1, "content": "甘肃这个名字来源于哪两个城市？", "optionA": "兰州和天水", "optionB": "甘州和肃州", "optionC": "甘南和肃北", "optionD": "甘谷和肃南", "correctAnswer": "B", "explanation": "甘肃名称源于甘州（今张掖）和肃州（今酒泉）。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃历史"]},
    {"categoryId": 3, "questionType": 1, "content": "丝绸之路经过甘肃省的哪个著名关口？", "optionA": "山海关", "optionB": "嘉峪关", "optionC": "居庸关", "optionD": "娘子关", "correctAnswer": "B", "explanation": "嘉峪关是丝绸之路的重要关口，位于甘肃省。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃历史"]},
    {"categoryId": 3, "questionType": 1, "content": "敦煌莫高窟始建于哪个朝代？", "optionA": "汉朝", "optionB": "魏晋", "optionC": "十六国时期", "optionD": "隋唐", "correctAnswer": "C", "explanation": "敦煌莫高窟始建于十六国时期的前秦。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃文化"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省被誉为以下哪个称号？", "optionA": "彩陶之乡", "optionB": "青铜之乡", "optionC": "玉器之乡", "optionD": "瓷器之乡", "correctAnswer": "A", "explanation": "甘肃临夏被誉为'中国彩陶之乡'。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃文化"]},
    
    # 自然资源
    {"categoryId": 3, "questionType": 1, "content": "甘肃省哪种有色金属储量居全国首位？", "optionA": "铜", "optionB": "铝", "optionC": "镍", "optionD": "锌", "correctAnswer": "C", "explanation": "甘肃省镍矿储量居全国首位，金昌被誉为'镍都'。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃资源"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省风能资源丰富的地区主要在哪个走廊？", "optionA": "河西走廊", "optionB": "陇东高原", "optionC": "甘南草原", "optionD": "陇南山地", "correctAnswer": "A", "explanation": "河西走廊是甘肃省风能资源最丰富的地区。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃资源"]},
    
    # 旅游景点
    {"categoryId": 3, "questionType": 1, "content": "以下哪个景点不在甘肃省？", "optionA": "莫高窟", "optionB": "麦积山石窟", "optionC": "龙门石窟", "optionD": "炳灵寺石窟", "correctAnswer": "C", "explanation": "龙门石窟位于河南省洛阳市，不在甘肃省。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃旅游"]},
    {"categoryId": 3, "questionType": 1, "content": "甘肃省有几处世界文化遗产？", "optionA": "3处", "optionB": "4处", "optionC": "5处", "optionD": "6处", "correctAnswer": "C", "explanation": "甘肃省有5处世界文化遗产。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃文化"]},
    
    # 多选题
    {"categoryId": 3, "questionType": 2, "content": "甘肃省的地级市包括以下哪些？", "optionA": "兰州市", "optionB": "天水市", "optionC": "酒泉市", "optionD": "临夏市", "correctAnswer": "ABC", "explanation": "兰州市、天水市、酒泉市是地级市，临夏市是县级市。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃行政区划"]},
    {"categoryId": 3, "questionType": 2, "content": "甘肃省的世界文化遗产包括以下哪些？", "optionA": "敦煌莫高窟", "optionB": "嘉峪关", "optionC": "麦积山石窟", "optionD": "炳灵寺石窟", "correctAnswer": "ABC", "explanation": "敦煌莫高窟、嘉峪关、麦积山石窟是世界文化遗产。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃文化"]},
    {"categoryId": 3, "questionType": 2, "content": "甘肃省的地形特点包括以下哪些？", "optionA": "地形狭长", "optionB": "地势西北高东南低", "optionC": "山地多平地少", "optionD": "沙漠广布", "correctAnswer": "ABC", "explanation": "甘肃省地形狭长，地势西北高东南低，山地多平地少。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    
    # 判断题
    {"categoryId": 3, "questionType": 3, "content": "甘肃省是中国面积最大的省份。", "optionA": "正确", "optionB": "错误", "correctAnswer": "B", "explanation": "新疆维吾尔自治区是中国面积最大的省级行政区。", "difficulty": 1, "source": "甘肃省基本情况", "tags": ["甘肃地理"]},
    {"categoryId": 3, "questionType": 3, "content": "兰州市是甘肃省唯一的副省级城市。", "optionA": "正确", "optionB": "错误", "correctAnswer": "B", "explanation": "甘肃省没有副省级城市，兰州市是地级市。", "difficulty": 2, "source": "甘肃省基本情况", "tags": ["甘肃行政区划"]},
]

# ==================== 临夏州情题目 ====================
LINXIA_QUESTIONS = [
    {"categoryId": 19, "questionType": 1, "content": "临夏回族自治州成立于哪一年？", "optionA": "1953年", "optionB": "1954年", "optionC": "1955年", "optionD": "1956年", "correctAnswer": "B", "explanation": "临夏回族自治州成立于1954年。", "difficulty": 2, "source": "临夏州情", "tags": ["临夏历史"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏州有多少个民族？", "optionA": "38个", "optionB": "40个", "optionC": "42个", "optionD": "44个", "correctAnswer": "C", "explanation": "临夏州有42个民族，其中回族是主体民族。", "difficulty": 2, "source": "临夏州情", "tags": ["临夏民族"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏州被誉为以下哪个称号？", "optionA": "中国彩陶之乡", "optionB": "中国花儿之乡", "optionC": "中国砖雕之乡", "optionD": "以上都是", "correctAnswer": "D", "explanation": "临夏州被誉为'中国彩陶之乡''中国花儿之乡''中国砖雕之乡'等。", "difficulty": 1, "source": "临夏州情", "tags": ["临夏文化"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏地质公园获批什么称号？", "optionA": "世界地质公园", "optionB": "国家地质公园", "optionC": "世界自然遗产", "optionD": "国家自然遗产", "correctAnswer": "A", "explanation": "2026年临夏地质公园获批世界地质公园。", "difficulty": 1, "source": "临夏州情", "tags": ["临夏旅游"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏州的首府是哪个城市？", "optionA": "临夏市", "optionB": "临夏县", "optionC": "和政县", "optionD": "永靖县", "correctAnswer": "A", "explanation": "临夏州首府是临夏市。", "difficulty": 1, "source": "临夏州情", "tags": ["临夏行政区划"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏州哪个县被誉为'古动物的伊甸园'？", "optionA": "和政县", "optionB": "康乐县", "optionC": "广河县", "optionD": "永靖县", "correctAnswer": "A", "explanation": "和政县被誉为'古动物的伊甸园'。", "difficulty": 2, "source": "临夏州情", "tags": ["临夏旅游"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏州的刘家峡水电站位于哪个县？", "optionA": "临夏县", "optionB": "和政县", "optionC": "永靖县", "optionD": "东乡县", "correctAnswer": "C", "explanation": "刘家峡水电站位于永靖县。", "difficulty": 2, "source": "临夏州情", "tags": ["临夏地理"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏州的保安族主要聚居在哪个县？", "optionA": "临夏县", "optionB": "和政县", "optionC": "积石山县", "optionD": "东乡县", "correctAnswer": "C", "explanation": "保安族主要聚居在积石山保安族东乡族撒拉族自治县。", "difficulty": 2, "source": "临夏州情", "tags": ["临夏民族"]},
    {"categoryId": 19, "questionType": 1, "content": "临夏州的东乡族是甘肃特有的少数民族吗？", "optionA": "是", "optionB": "不是", "optionC": "是全国特有", "optionD": "不确定", "correctAnswer": "A", "explanation": "东乡族是甘肃特有的少数民族，以临夏为主要聚居区。", "difficulty": 1, "source": "临夏州情", "tags": ["临夏民族"]},
    {"categoryId": 19, "questionType": 2, "content": "临夏州的特色文化包括以下哪些？", "optionA": "花儿", "optionB": "砖雕", "optionC": "木雕", "optionD": "保安腰刀", "correctAnswer": "ABCD", "explanation": "临夏州的特色文化包括花儿、砖雕、木雕、保安腰刀等。", "difficulty": 2, "source": "临夏州情", "tags": ["临夏文化"]},
]

# ==================== 时政热点扩充题目 ====================
CURRENT_AFFAIRS_EXPAND = [
    {"categoryId": 18, "questionType": 1, "content": "2025年7月19日，哪个水电工程在西藏开工？", "optionA": "三峡水电站", "optionB": "雅鲁藏布江下游水电工程", "optionC": "金沙江水电站", "optionD": "澜沧江水电站", "correctAnswer": "B", "explanation": "2025年7月19日，雅鲁藏布江下游水电工程在西藏开工。", "difficulty": 1, "source": "时政热点", "tags": ["科技时事"]},
    {"categoryId": 18, "questionType": 1, "content": "'奋斗者'号载人潜水器在哪个深度发现生命绿洲？", "optionA": "8533米", "optionB": "9533米", "optionC": "10533米", "optionD": "11533米", "correctAnswer": "B", "explanation": "'奋斗者'号在9533米深渊海底发现生命绿洲。", "difficulty": 2, "source": "时政热点", "tags": ["科技时事"]},
    {"categoryId": 18, "questionType": 1, "content": "2025年8月26日，哪个中微子实验装置开始运行？", "optionA": "江门中微子实验", "optionB": "大亚湾中微子实验", "optionC": "锦屏中微子实验", "optionD": "霞浦中微子实验", "correctAnswer": "A", "explanation": "2025年8月26日，江门中微子实验装置开始运行。", "difficulty": 2, "source": "时政热点", "tags": ["科技时事"]},
    {"categoryId": 18, "questionType": 1, "content": "2025年11月5日，我国第一艘电磁弹射型航空母舰入列，该航母名称是什么？", "optionA": "辽宁舰", "optionB": "山东舰", "optionC": "福建舰", "optionD": "广东舰", "correctAnswer": "C", "explanation": "2025年11月5日，福建舰入列，舷号18。", "difficulty": 1, "source": "时政热点", "tags": ["科技时事"]},
    {"categoryId": 18, "questionType": 1, "content": "2026年4月10日，我国自主研发的全球首款什么获批？", "optionA": "放射性创新药", "optionB": "基因治疗药物", "optionC": "细胞治疗药物", "optionD": "抗体药物", "correctAnswer": "A", "explanation": "2026年4月10日，全球首款放射性创新药获批。", "difficulty": 2, "source": "时政热点", "tags": ["科技时事"]},
    {"categoryId": 18, "questionType": 1, "content": "'九章四号'是什么类型的计算机？", "optionA": "超级计算机", "optionB": "量子计算机", "optionC": "云计算计算机", "optionD": "边缘计算机", "correctAnswer": "B", "explanation": "'九章四号'是世界最快量子计算机。", "difficulty": 1, "source": "时政热点", "tags": ["科技时事"]},
    {"categoryId": 18, "questionType": 1, "content": "2025年12月18日，哪个自由贸易港正式启动全岛封关？", "optionA": "上海自由贸易港", "optionB": "广东自由贸易港", "optionC": "海南自由贸易港", "optionD": "浙江自由贸易港", "correctAnswer": "C", "explanation": "2025年12月18日，海南自由贸易港正式启动全岛封关。", "difficulty": 1, "source": "时政热点", "tags": ["时政热点"]},
    {"categoryId": 18, "questionType": 1, "content": "2025年度全国十大考古新发现中，甘肃哪个遗址入选？", "optionA": "马家窑遗址", "optionB": "南佐遗址", "optionC": "齐家坪遗址", "optionD": "大地湾遗址", "correctAnswer": "B", "explanation": "甘肃庆阳南佐遗址入选2025年度全国十大考古新发现。", "difficulty": 2, "source": "时政热点", "tags": ["文化时事"]},
    {"categoryId": 18, "questionType": 1, "content": "中国绘本画家蔡皋获得2026年什么奖项？", "optionA": "诺贝尔文学奖", "optionB": "国际安徒生奖", "optionC": "茅盾文学奖", "optionD": "鲁迅文学奖", "correctAnswer": "B", "explanation": "蔡皋获得2026年国际安徒生奖插画家奖。", "difficulty": 2, "source": "时政热点", "tags": ["文化时事"]},
    {"categoryId": 18, "questionType": 1, "content": "世界上跨度最大的公铁两用无砟轨道斜拉桥是哪座？", "optionA": "港珠澳大桥", "optionB": "崇启公铁长江大桥", "optionC": "苏通大桥", "optionD": "沪苏通大桥", "correctAnswer": "B", "explanation": "崇启公铁长江大桥是世界最大跨度公铁两用无砟轨道斜拉桥。", "difficulty": 2, "source": "时政热点", "tags": ["科技时事"]},
    {"categoryId": 18, "questionType": 2, "content": "以下哪些是2025年我国科技成就？", "optionA": "福建舰入列", "optionB": "九章四号发布", "optionC": "江门中微子实验运行", "optionD": "奋斗者号发现深海生命", "correctAnswer": "ABCD", "explanation": "以上都是2025年我国科技成就。", "difficulty": 2, "source": "时政热点", "tags": ["科技时事"]},
]

# ==================== 政治理论扩充题目 ====================
POLITICS_EXPAND = [
    {"categoryId": 11, "questionType": 1, "content": "马克思主义最鲜明的特征是（）。", "optionA": "科学性", "optionB": "革命性", "optionC": "实践性", "optionD": "阶级性", "correctAnswer": "C", "explanation": "实践性是马克思主义最鲜明的特征。", "difficulty": 2, "source": "政治理论", "tags": ["马哲"]},
    {"categoryId": 11, "questionType": 1, "content": "哲学的基本问题是（）。", "optionA": "物质和运动的关系", "optionB": "思维和存在的关系", "optionC": "主体和客体的关系", "optionD": "理论和实践的关系", "correctAnswer": "B", "explanation": "哲学的基本问题是思维和存在的关系问题。", "difficulty": 1, "source": "政治理论", "tags": ["马哲"]},
    {"categoryId": 11, "questionType": 1, "content": "'人不能两次踏进同一条河流'说明（）。", "optionA": "运动是绝对的", "optionB": "静止是相对的", "optionC": "运动是相对的", "optionD": "静止是绝对的", "correctAnswer": "A", "explanation": "这句话说明运动是绝对的，静止是相对的。", "difficulty": 2, "source": "政治理论", "tags": ["马哲"]},
    {"categoryId": 12, "questionType": 1, "content": "习近平新时代中国特色社会主义思想的核心要义是（）。", "optionA": "实现中华民族伟大复兴", "optionB": "坚持和发展中国特色社会主义", "optionC": "全面深化改革", "optionD": "全面依法治国", "correctAnswer": "B", "explanation": "坚持和发展中国特色社会主义是核心要义。", "difficulty": 1, "source": "政治理论", "tags": ["习思想"]},
    {"categoryId": 12, "questionType": 1, "content": "中国特色社会主义最本质的特征是（）。", "optionA": "人民当家作主", "optionB": "依法治国", "optionC": "中国共产党领导", "optionD": "社会主义市场经济", "correctAnswer": "C", "explanation": "中国共产党领导是中国特色社会主义最本质的特征。", "difficulty": 1, "source": "政治理论", "tags": ["习思想"]},
    {"categoryId": 12, "questionType": 1, "content": "新发展理念中，（）是引领发展的第一动力。", "optionA": "协调", "optionB": "绿色", "optionC": "开放", "optionD": "创新", "correctAnswer": "D", "explanation": "创新是引领发展的第一动力。", "difficulty": 1, "source": "政治理论", "tags": ["习思想"]},
]

# ==================== 法律知识扩充题目 ====================
LAW_EXPAND = [
    {"categoryId": 14, "questionType": 1, "content": "我国宪法规定，中华人民共和国的一切权力属于（）。", "optionA": "人民", "optionB": "公民", "optionC": "国家", "optionD": "政府", "correctAnswer": "A", "explanation": "宪法规定中华人民共和国的一切权力属于人民。", "difficulty": 1, "source": "法律知识", "tags": ["宪法"]},
    {"categoryId": 14, "questionType": 1, "content": "我国的根本制度是（）。", "optionA": "人民代表大会制度", "optionB": "社会主义制度", "optionC": "中国共产党领导的多党合作制度", "optionD": "民族区域自治制度", "correctAnswer": "B", "explanation": "社会主义制度是我国的根本制度。", "difficulty": 1, "source": "法律知识", "tags": ["宪法"]},
    {"categoryId": 14, "questionType": 1, "content": "我国公民的基本权利不包括（）。", "optionA": "选举权和被选举权", "optionB": "言论自由", "optionC": "劳动权", "optionD": "迁徙自由", "correctAnswer": "D", "explanation": "我国宪法未明确规定迁徙自由。", "difficulty": 2, "source": "法律知识", "tags": ["宪法"]},
    {"categoryId": 14, "questionType": 1, "content": "根据《民法典》，自然人的民事权利能力始于（）。", "optionA": "受孕", "optionB": "出生", "optionC": "年满十周岁", "optionD": "年满十八周岁", "correctAnswer": "B", "explanation": "自然人的民事权利能力始于出生。", "difficulty": 1, "source": "法律知识", "tags": ["民法"]},
    {"categoryId": 14, "questionType": 1, "content": "根据《刑法》，已满十四周岁不满十六周岁的人，犯下列哪种罪应当负刑事责任？", "optionA": "盗窃罪", "optionB": "故意伤害罪（致人重伤）", "optionC": "诈骗罪", "optionD": "抢夺罪", "correctAnswer": "B", "explanation": "已满十四周岁不满十六周岁的人犯故意伤害罪致人重伤应当负刑事责任。", "difficulty": 2, "source": "法律知识", "tags": ["刑法"]},
    {"categoryId": 14, "questionType": 1, "content": "行政处罚法规定，违法行为在（）内未被发现的，不再给予行政处罚。", "optionA": "一年", "optionB": "两年", "optionC": "三年", "optionD": "五年", "correctAnswer": "B", "explanation": "违法行为在两年内未被发现的，不再给予行政处罚。", "difficulty": 2, "source": "法律知识", "tags": ["行政法"]},
]

# ==================== 经济知识扩充题目 ====================
ECONOMY_EXPAND = [
    {"categoryId": 1, "questionType": 1, "content": "GDP是指（）。", "optionA": "国内生产总值", "optionB": "国民生产总值", "optionC": "人均收入", "optionD": "财政收入", "correctAnswer": "A", "explanation": "GDP是国内生产总值的英文缩写。", "difficulty": 1, "source": "经济知识", "tags": ["经济基础"]},
    {"categoryId": 1, "questionType": 1, "content": "通货膨胀是指（）。", "optionA": "物价持续上涨", "optionB": "物价持续下跌", "optionC": "物价保持稳定", "optionD": "物价大幅波动", "correctAnswer": "A", "explanation": "通货膨胀是指物价持续普遍上涨。", "difficulty": 1, "source": "经济知识", "tags": ["经济基础"]},
    {"categoryId": 1, "questionType": 1, "content": "基尼系数为0.4-0.5时表示（）。", "optionA": "收入绝对平均", "optionB": "收入比较平均", "optionC": "收入差距较大", "optionD": "收入差距悬殊", "correctAnswer": "C", "explanation": "基尼系数0.4-0.5表示收入差距较大。", "difficulty": 2, "source": "经济知识", "tags": ["经济基础"]},
    {"categoryId": 1, "questionType": 1, "content": "恩格尔系数越高，说明（）。", "optionA": "生活水平越高", "optionB": "生活水平越低", "optionC": "收入越高", "optionD": "消费越高", "correctAnswer": "B", "explanation": "恩格尔系数越高，说明食品支出占消费总支出比重越大，生活水平越低。", "difficulty": 2, "source": "经济知识", "tags": ["经济基础"]},
]

def main():
    db_path = "linxia_exam_db"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    now = int(datetime.now().timestamp())
    
    # 统计导入前
    cursor.execute("SELECT COUNT(*) FROM questions")
    before_count = cursor.fetchone()[0]
    
    # 导入甘肃省基本情况题目
    all_new_questions = GANSU_BASIC_QUESTIONS + LINXIA_QUESTIONS + CURRENT_AFFAIRS_EXPAND + POLITICS_EXPAND + LAW_EXPAND + ECONOMY_EXPAND
    
    inserted = 0
    for q in all_new_questions:
        # 检查是否已存在
        cursor.execute("SELECT 1 FROM questions WHERE category_id = ? AND content LIKE ?", 
                      (q["categoryId"], f"%{q['content'][:30]}%"))
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
            q.get("optionA", ""),
            q.get("optionB", ""),
            q.get("optionC", ""),
            q.get("optionD", ""),
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
    
    # 统计导入后
    cursor.execute("SELECT COUNT(*) FROM questions")
    after_count = cursor.fetchone()[0]
    
    print(f"扩充前题目数: {before_count}")
    print(f"新增题目数: {inserted}")
    print(f"扩充后题目数: {after_count}")
    
    # 显示分类统计
    cursor.execute("""
        SELECT c.name, c.level, COUNT(q.id) as cnt 
        FROM categories c 
        LEFT JOIN questions q ON c.id = q.category_id 
        WHERE c.level <= 2
        GROUP BY c.id 
        HAVING cnt > 0
        ORDER BY cnt DESC
    """)
    print("\n分类题目统计:")
    for row in cursor.fetchall():
        print(f"  {row[0]}: {row[2]} 道")
    
    conn.close()
    print("\n扩充完成！")

if __name__ == "__main__":
    main()
