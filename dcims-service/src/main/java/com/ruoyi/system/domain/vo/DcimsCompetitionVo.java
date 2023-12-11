package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 竞赛赛事基本信息视图对象 dcims_competition
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsCompetitionVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 赛事名称
     */
    @ExcelProperty(value = "赛事名称")
    private String name;

    /**
     * 赛事类别
     */
    @ExcelProperty(value = "赛事类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_competition_type")
    private String level;

    /**
     * 往届名称
     */
    @ExcelProperty(value = "往届名称")
    private String pastName;

    /**
     * 赛事官网
     */
    private String website;

    /**
     * 赛事届次
     */
    @ExcelProperty(value = "赛事届次")
    private Long term;

    /**
     * 赛事年份
     */
    @ExcelProperty(value = "赛事年份")
    private Integer annual;

    /**
     * 主办单位
     */
    @ExcelProperty(value = "主办单位")
    private String organizer;

    /**
     * 竞赛负责人工号
     */
    @ExcelProperty(value = "竞赛负责人工号")
    private Long responsiblePersonId;

    /**
     * 竞赛负责人
     */
    @ExcelProperty(value = "竞赛负责人")
    private String responsiblePersonName;

    /**
     * 所属学院
     */
    @ExcelProperty(value = "所属学院")
    private Long college;

    /**
     * 负责人手机号
     */
    @ExcelProperty(value = "负责人手机号")
    private Long phone;

    /**
     * 校内选拔时间
     */
    @ExcelProperty(value = "校内选拔时间")
    private Date innerTime;

    /**
     * 省赛时间
     */
    @ExcelProperty(value = "省赛时间")
    private Date provinceTime;

    /**
     * 国赛时间
     */
    @ExcelProperty(value = "国赛时间")
    private Date nationalTime;

    /**
     * 本年度申报经费（万元）
     */
    private BigDecimal budget;

    /**
     * 本年度拨款（万元）
     */
    @ExcelProperty(value = "本年度拨款", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "万=元")
    private BigDecimal appropriation;

    /**
     * 获奖目标
     */
    private String goal;

    /**
     * 赛事简介
     */
    private String introduction;

    /**
     * 竞赛申报书
     */
    private String attachment;

    /**
     * 奖金核算状态
     */
    private String moneyAggregate;

    /**
     * 工作量核算状态
     */
    private String workloadAggregate;

    /**
     * 个人赛限项
     */
    @ExcelProperty(value = "个人赛限项")
    private Integer personLimit;

    /**
     * 团队赛限项
     */
    @ExcelProperty(value = "团队赛限项")
    private Integer teamLimit;

    /**
     * 审核人工号
     */
    private Long nextAuditId;

    /**
     * 审核状态
     */
    private String state;

    /**
     * 审核状态详细
     */
    private DcimsCompetitionAuditVo audit;
}
