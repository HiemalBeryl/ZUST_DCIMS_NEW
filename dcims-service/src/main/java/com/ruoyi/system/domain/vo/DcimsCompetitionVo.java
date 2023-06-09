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
 * @date 2023-04-12
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
     * 部门id
     */
    @ExcelProperty(value = "部门id")
    private Long deptId;

    /**
     * 用户id
     */
    @ExcelProperty(value = "用户id")
    private Long userId;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Long orderNum;

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
     * 立项结束时间
     */
    @ExcelProperty(value = "立项结束时间")
    private Date stopTime;

    /**
     * 集中授课时数
     */
    @ExcelProperty(value = "集中授课时数")
    private BigDecimal concentrationOfTeachingHours;

    /**
     * 本年度申报经费（万元）
     */
    @ExcelProperty(value = "本年度申报经费", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "万=元")
    private BigDecimal budget;

    /**
     * 本年度获奖目标
     */
    @ExcelProperty(value = "本年度获奖目标")
    private String goal;

    /**
     * 本年度拨款
     */
    @ExcelProperty(value = "本年度拨款")
    private BigDecimal appropriation;

    /**
     * 奖金核算状态
     */
    @ExcelProperty(value = "奖金核算状态")
    private String moneyAggregate;

    /**
     * 工作量核算状态
     */
    @ExcelProperty(value = "工作量核算状态")
    private String workloadAggregate;

    /**
     * 审核状态
     */
    @ExcelProperty(value = "审核状态")
    private String state;


}
