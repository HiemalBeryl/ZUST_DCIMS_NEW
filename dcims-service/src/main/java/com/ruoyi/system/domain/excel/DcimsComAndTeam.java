package com.ruoyi.system.domain.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.system.domain.DcimsTeam;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DcimsComAndTeam {
    @ExcelProperty(value = "序号")
    private int order;

    @ExcelProperty(value = "竞赛名称")
    private String competitionName;

    @ExcelProperty(value = "类别")
    private String competitionType;

    private String isSingle;

    @ExcelProperty(value = "主办单位")
    private String organizer;

    @ExcelProperty(value = "集中授课时数")
    private BigDecimal jiZhongShouKeShiShu;

    @ExcelProperty(value = {"竞赛负责人及联系电话", "姓名"})
    private String responsiblePersonName;

    @ExcelProperty(value = {"竞赛负责人及联系电话", "工号"})
    private Long responsiblePersonId;

    @ExcelProperty(value = {"竞赛负责人及联系电话", "手机号码"})
    private Long phone;

    @ExcelProperty(value = "所在单位")
    private String college;

    @ExcelIgnore
    private Map<Integer, DcimsTeam> teams;

    @ExcelProperty(value = {"获奖情况", "国际级一等奖", "项数"})
    private int huojiangCount1;
    @ExcelProperty(value = {"获奖情况", "国际级一等奖", "人数"})
    private int huojiangNumber1;

    @ExcelProperty(value = {"获奖情况", "国际级二等奖", "项数"})
    private int huojiangCount2;
    @ExcelProperty(value = {"获奖情况", "国际级二等奖", "人数"})
    private int huojiangNumber2;

    @ExcelProperty(value = {"获奖情况", "国际级三等奖", "项数"})
    private int huojiangCount3;
    @ExcelProperty(value = {"获奖情况", "国际级三等奖", "人数"})
    private int huojiangNumber3;

    @ExcelProperty(value = {"获奖情况", "国际级优胜奖", "项数"})
    private int huojiangCount4;
    @ExcelProperty(value = {"获奖情况", "国际级优胜奖", "人数"})
    private int huojiangNumber4;

    @ExcelProperty(value = {"获奖情况", "国家级特等奖", "项数"})
    private int huojiangCount5;
    @ExcelProperty(value = {"获奖情况", "国家级特等奖", "人数"})
    private int huojiangNumber5;

    @ExcelProperty(value = {"获奖情况", "国家级一等奖", "项数"})
    private int huojiangCount6;
    @ExcelProperty(value = {"获奖情况", "国家级一等奖", "人数"})
    private int huojiangNumber6;

    @ExcelProperty(value = {"获奖情况", "国家级二等奖", "项数"})
    private int huojiangCount7;
    @ExcelProperty(value = {"获奖情况", "国家级二等奖", "人数"})
    private int huojiangNumber7;

    @ExcelProperty(value = {"获奖情况", "国家级三胜奖", "项数"})
    private int huojiangCount8;
    @ExcelProperty(value = {"获奖情况", "国家级三胜奖", "人数"})
    private int huojiangNumber8;

    @ExcelProperty(value = {"获奖情况", "国家级优胜奖", "项数"})
    private int huojiangCount9;
    @ExcelProperty(value = {"获奖情况", "国家级优胜奖", "人数"})
    private int huojiangNumber9;

    @ExcelProperty(value = {"获奖情况", "省特等奖", "项数"})
    private int huojiangCount10;
    @ExcelProperty(value = {"获奖情况", "省特等奖", "人数"})
    private int huojiangNumber10;

    @ExcelProperty(value = {"获奖情况", "省一等奖", "项数"})
    private int huojiangCount11;
    @ExcelProperty(value = {"获奖情况", "省一等奖", "人数"})
    private int huojiangNumber11;

    @ExcelProperty(value = {"获奖情况", "省二等奖", "项数"})
    private int huojiangCount12;
    @ExcelProperty(value = {"获奖情况", "省二等奖", "人数"})
    private int huojiangNumber12;

    @ExcelProperty(value = {"获奖情况", "省三等奖", "项数"})
    private int huojiangCount13;
    @ExcelProperty(value = {"获奖情况", "省三等奖", "人数"})
    private int huojiangNumber13;

    @ExcelProperty(value = {"获奖情况", "省优胜奖", "项数"})
    private int huojiangCount14;
    @ExcelProperty(value = {"获奖情况", "省优胜奖", "人数"})
    private int huojiangNumber14;

    @ExcelProperty(value = {"合计", "获奖人次"})
    private int totalHuojiangCount;
    @ExcelProperty(value = {"合计", "获奖项数"})
    private int totalHuojiangNumber;
}
