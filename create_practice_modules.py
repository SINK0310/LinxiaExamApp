#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从三套母卷中分板块组织刷题
"""

import sqlite3, re
from datetime import datetime

def main():
    db_path = "linxia_exam_db"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS practice_exercises (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            description TEXT DEFAULT '',
            topic TEXT NOT NULL,
            total_questions INTEGER DEFAULT 0,
            created_at INTEGER DEFAULT (strftime('%s','now'))
        )
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS practice_questions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            exercise_id INTEGER NOT NULL,
            question_type INTEGER DEFAULT 1,
            content TEXT NOT NULL,
            option_a TEXT DEFAULT '',
            option_b TEXT DEFAULT '',
            option_c TEXT DEFAULT '',
            option_d TEXT DEFAULT '',
            correct_answer TEXT DEFAULT '',
            explanation TEXT DEFAULT '',
            sort_order INTEGER DEFAULT 0,
            FOREIGN KEY (exercise_id) REFERENCES practice_exercises(id) ON DELETE CASCADE
        )
    """)
    cursor.execute("DELETE FROM practice_questions")
    cursor.execute("DELETE FROM practice_exercises")
    conn.commit()

    cursor.execute("""
        SELECT id, question_type, content, option_a, option_b, option_c, option_d,
               correct_answer, explanation
        FROM mock_exam_questions
        WHERE exam_id IN (1,2,3)
    """)
    rows = cursor.fetchall()
    print("母卷共 %d 题" % len(rows))

    # ---------- 分类规则(按优先级) ----------
    classifiers = [
        ("政治理论", [
            "毛泽东思想", "邓小平理论", "三个代表", "科学发展观",
            "中国特色社会主义", "社会主义核心", "四个自信", "四个伟大",
            "社会主义初级阶段", "社会主义和谐社会", "社会主义道德",
            "集体主义", "公民道德", "民族精神", "时代精神",
            "五位一体", "对外开放", "国际交往",
            "中国特色社会主义理论体系", "我国解决民族问题",
            "社会主义核心价值体系", "社会意识"
        ]),
        ("习近平新时代", [
            "习近平", "总书记", "中国梦", "两个维护",
            "法治思想", "人类命运共同体", "中国式现代化",
            "深化改革", "简政放权", "放管服", "供给侧",
            "双碳", "绿水青山", "高质量发展", "新发展理念",
            "全面深化改革", "全面依法治国", "全面从严治党",
            "四个全面", "生物安全", "爱国卫生运动",
            "全面建设社会主义现代化国家",
            "把改善供给侧"
        ]),
        ("马哲", [
            "哲学", "唯物主义", "唯心主义", "辩证法", "认识论",
            "实践是检验", "实践是认识", "物质和意识", "意识对物质",
            "客观规律", "主观能动", "矛盾", "量变", "质变",
            "否定之否定", "对立统一", "静止是", "运动是",
            "真理", "存在", "反映", "知行", "认识的",
            "从物到感觉", "从思想到感觉", "仁者见仁", "因果关系",
            "实事求是", "时空", "物质运动", "主观见之于",
            "稳如泰山", "规律是", "一切从实际", "事物发展",
            "物质与运动",
            "时间和空间是物质运动",
            "认识是主体",
            "认识世界",
            "循环经济", "生态文明观",
            "从物到感觉", "从思想到感觉",
            "速滑", "哲学原理", "辩证"
        ]),
        ("法律", [
            "宪法", "民法典", "民法", "刑法", "行政法",
            "立法法",
            "行政处罚法", "行政许可法", "行政复议法", "行政诉讼法",
            "治安管理处罚法", "公务员", "法律关系", "法律责任",
            "正当防卫", "犯罪", "刑罚", "刑事责任", "民事法律",
            "物权", "合同", "债权", "侵权", "婚姻", "继承",
            "诉讼时效", "听证", "赔偿", "国家赔偿", "管辖",
            "证据", "复议", "执法", "监督权", "选举权",
            "被选举权", "拘留", "罚款", "没收",
            "刑事立案", "行政公益诉讼",
            "行政处罚", "行政许可", "行政拘留",
            "受案范围", "属于不作为犯罪",
            "属于行政行为", "属于行政诉讼",
            "根据《", "据《", "依照《",
            "属于可撤销", "属于无效",
            "赵某", "李某", "张某", "王某", "刘某",
            "讨债", "债权人", "债务人", "担保物权",
            "故意伤害", "盗窃", "抢劫",
            "醉酒驾车", "醉驾",
            "专利权", "知识产权",
            "公司法", "合同纠纷",
            "民事诉讼法", "刑事诉讼法",
            "挪用", "贪污", "受贿", "行贿",
            "本案", "原告", "被告", "法院判决",
            "起诉", "上诉", "申诉", "逮捕",
            "假释", "缓刑", "管制", "剥夺政治权利",
            "执法人员", "执法公示",
            "检查笔录", "行政处分",
            "任职回避", "治安案件",
            "国人民代表大会常务委员会",
            "犯罪客体", "犯罪主体",
            "犯罪预备",
            "标的物",
            "自然人民事",
            "从轻处罚", "从重处罚",
            "扣押",
            "申请人",
            "用人单位"
        ]),
        ("经济", [
            "市场经济", "价值规律", "剩余价值", "GDP", "国内生产总值",
            "货币", "通货膨胀", "通货紧缩", "财政", "税收", "银行",
            "消费", "商品", "价格", "供求", "竞争",
            "恩格尔", "基尼系数", "宏观调控",
            "失业", "结构性失业",
            "产业资本", "财政赤字", "储蓄", "投资",
            "替代品", "互补品", "第三产业",
            "买方", "卖方", "垄断",
            "自由竞争", "商品拜物教",
            "价值与使用价值", "可变资本",
            "按劳分配", "公有制", "股份制",
            "纸的价格", "流通中所需要的货币量",
            "市场经济有效运",
            "经济杠杆",
            "西晋",
            "降低生产成本"
        ]),
        ("管理", [
            "管理幅度", "管理层次", "管理方格",
            "决策", "行政决策", "公共政策",
            "领导", "组织领导",
            "行政监督", "监督的原则",
            "目标管理", "人际技能", "技术技能",
            "行政权力", "行政协调", "行政执行",
            "控制", "木桶原理", "群体决策",
            "马斯洛", "激励", "士为知己",
            "行政管理", "公共危机",
            "行政组织结构", "垂直领导",
            "人民军队建设",
            "行文规则",
            "行政主体的",
            "管理学", "管理心理学",
            "行政管理活动中"
        ]),
        ("公文", [
            "通知", "决定", "请示", "报告", "函", "纪要",
            "公告", "通告", "通报", "议案", "批复",
            "公文", "发文字号", "密级", "附件",
            "主送机关", "成文日期", "印发", "版头", "版记",
            "抄送", "标题", "文种", "落款", "发文机关",
            "收文办理", "发文办理", "整理归档",
            "拟制", "复核", "签发",
            "附注", "结束语",
            "文秘部门", "归档",
            "批转",
            "活动申请表", "活动承诺书",
            "该公文", "这份公文", "此公文",
            "行文"
        ]),
        ("科技常识", [
            "计算机", "网络", "北斗", "光纤",
            "人工智能", "物联网", "病毒",
            "信息", "数据", "程序",
            "新材料", "指纹", "人脸识别",
            "虹膜", "声纹",
            "云计算", "大数据",
            "CPU", "内存", "通信",
            "数字信号", "模拟信号",
            "信息技术", "纳米",
            "导航", "遥感",
            "计算机应用", "硬件",
            "机器视觉", "语音识别",
            "自然语言处理", "博弈系统",
            "灭火", "医疗", "医学",
            "合理饮食", "健康",
            "烧烫伤", "风化作用",
            "密码", "网络安全",
            "操作系统", "Windows",
            "外存储器"
        ]),
        ("地理环境", [
            "地球", "气候", "盆地", "河流", "湖",
            "地势", "土地", "位置", "天文", "太阳",
            "大气", "季风", "降水", "温度",
            "自然天气", "自然灾害", "地质灾害",
            "海洋", "人口密度",
            "生态", "环保", "可回收",
            "自然资源", "南海", "曾母暗沙",
            "长江", "黄河", "水土",
            "中国地理位置", "四至点",
            "秦岭", "淮河", "沿海",
            "太平洋西岸", "大陆性季风",
            "河西走廊", "商品粮基地",
            "泥石流", "山洪", "滑坡",
            "濒临", "海岸线",
            "早穿皮袄", "水资源"
        ]),
        ("历史文化", [
            "文学", "诗人", "诗词", "古代", "历史",
            "作品", "戏曲", "非物质文化遗产",
            "汉朝", "唐朝", "宋朝", "明朝", "清朝",
            "三国", "春秋", "战国",
            "作家", "小说", "散文",
            "四书五经", "史记",
            "青铜器", "考古", "文物",
            "传统节日", "节气",
            "李白", "杜甫", "苏轼", "韩愈", "柳宗元",
            "欧阳修", "王安石", "辛弃疾", "陆游",
            "王勃", "杨炯", "卢照邻", "骆宾王",
            "陶渊明", "屈原", "曹植", "曹操",
            "红楼梦", "水浒", "三国演义", "西游记",
            "将进酒", "唐诗", "宋词",
            "元曲", "乐府",
            "名医", "华佗", "张仲景", "孙思邈", "李时珍",
            "黄帝内经", "本草纲目",
            "文天祥", "岳飞",
            "黄宗羲", "顾炎武", "王夫之",
            "孔子", "孟子", "老子", "庄子",
            "儒家", "道家", "法家", "墨家",
            "书法", "琴棋",
            "生旦净丑", "京剧",
            "革命诗词", "红色基因",
            "仰韶", "马家窑", "龙山", "齐家",
            "中国古典小说",
            "魏晋", "竹林",
            "初唐", "盛唐", "晚唐",
            "端午", "中秋", "重阳",
            "黄发垂髫",
            "千古文章四大家",
            "民间舞蹈",
            "汉赋", "左思",
            "诗中的诗",
            "九日与钟繇",
            "天人合一",
            "本乡土",
            "汉朝"
        ]),
        ("甘肃省情/临夏州情", [
            "甘肃", "临夏", "兰州", "敦煌", "莫高窟",
            "河西走廊", "天水", "嘉峪关",
            "甘肃地形", "甘肃省", "丝路",
            "保安族", "东乡族", "花儿", "砖雕",
            "彩陶", "刘家峡", "炳灵寺",
            "麦积山", "古动物",
            "甘肃地理位置", "甘肃地形地貌",
            "兰新", "陇海",
            "黄土高原",
            "中华文明的重要起源",
            "第二阶梯",
            "黄河流经",
            "陇原"
        ]),
        ("时政热点", [
            "全会", "两会", "决议",
            "十四五", "十五五",
            "全面从严治党", "党风廉政",
            "纪检", "问责", "反腐",
            "二十大", "中央委员会",
            "经济社会发展",
            "党的纪律处分",
            "党的建设",
            "关于党的百年奋斗"
        ]),
    ]

    classified = {name: [] for name, _ in classifiers}
    classified["未分类"] = []

    for row in rows:
        content = row[2]
        found = False
        for name, keywords in classifiers:
            for kw in keywords:
                if kw in content:
                    classified[name].append(row)
                    found = True
                    break
            if found:
                break
        if not found:
            classified["未分类"].append(row)

    print("\n分类统计:")
    for name, items in sorted(classified.items(), key=lambda x: -len(x[1])):
        print("  %s: %d 题" % (name, len(items)))

    # 创建练习(未分类归入"综合")
    now = int(datetime.now().timestamp())
    total = 0

    for name, items in sorted(classified.items(), key=lambda x: -len(x[1])):
        if len(items) == 0:
            continue
        display_name = name if name != "未分类" else "综合(未分类)"
        cursor.execute("""
            INSERT INTO practice_exercises (name, description, topic, total_questions, created_at)
            VALUES (?, ?, ?, ?, ?)
        """, (display_name + "专项练习", "从三套母卷中提取的" + name + "题目", name, len(items), now))
        eid = cursor.lastrowid
        for idx, row in enumerate(items):
            _, qtype, content, oa, ob, oc, od, ans, expl = row
            cursor.execute("""
                INSERT INTO practice_questions (exercise_id, question_type, content, option_a, option_b,
                                               option_c, option_d, correct_answer, explanation, sort_order)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (eid, qtype, content, oa, ob, oc, od, ans, expl, idx + 1))
            total += 1
        print("  [%s] %d 题 -> 练习ID %d" % (display_name, len(items), eid))

    conn.commit()

    if len(classified["未分类"]) > 0:
        print("\n** 未分类 %d 题（已归入综合练习）:" % len(classified["未分类"]))
        for row in classified["未分类"][:5]:
            print("  %s..." % row[2][:60])

    cursor.execute("SELECT id, name, total_questions FROM practice_exercises ORDER BY id")
    print("\n板块练习列表:")
    for r in cursor.fetchall():
        print("  ID %d: %s (%d题)" % (r[0], r[1], r[2]))

    print("\n共分类 %d 题，创建 %d 个练习板块" % (total, len(classified)))
    conn.close()

if __name__ == "__main__":
    main()
