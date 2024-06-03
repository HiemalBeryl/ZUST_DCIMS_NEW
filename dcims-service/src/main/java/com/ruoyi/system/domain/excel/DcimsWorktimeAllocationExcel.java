package com.ruoyi.system.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;

@Data
public class DcimsWorktimeAllocationExcel {
    /**
     * 考核年度
     */
    @ExcelProperty(value = "考核年度")
    private String year;

    /**
     * 竞赛名称
     */
    @ExcelProperty(value = "竞赛名称")
    private String competitionName;

    /**
     * 竞赛类别
     */
    @ExcelProperty(value = "竞赛类别")
    private String type;

    /**
     * 竞赛时间
     */
    @ExcelProperty(value = "竞赛时间")
    private String time;

    /**
     * 竞赛方式（组队/单人）
     */
    @ExcelProperty(value = "竞赛方式")
    private String mode;

    /**
     * 获奖项数
     */
    @ExcelProperty(value = "获奖项数")
    private int awardNum;

    /**
     * 获奖等级
     */
    @ExcelProperty(value = "获奖等级")
    private String awardLevel;

    /**
     * 主办单位
     */
    @ExcelProperty(value = "主办单位")
    private String organizer;

    /**
     * 学时
     */
    @ExcelProperty(value = "学时")
    private BigDecimal credit;

    /**
     * 集中授课学时
     */
    @ExcelProperty(value = "集中授课学时")
    private BigDecimal concentratedCredit;

    /**
     * 负责人工号
     */
    @ExcelProperty(value = "负责人工号")
    private String leaderId;

    /**
     * 负责人姓名
     */
    @ExcelProperty(value = "负责人姓名")
    private String leader;

    /**
     * 所属部门（xx学院）
     */
    @ExcelProperty(value = "所属部门")
    private String department;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;
}
