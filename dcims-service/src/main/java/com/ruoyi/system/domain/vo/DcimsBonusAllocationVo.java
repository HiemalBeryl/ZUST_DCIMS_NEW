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
 * 奖金分配总视图对象 dcims_bonus_allocation
 *
 * @author Andy
 * @date 2023-05-02
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsBonusAllocationVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 年份
     */
    @ExcelProperty(value = "年份")
    private Integer years;

    /**
     * 学院
     */
    @ExcelProperty(value = "学院", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_college")
    private Integer college;

    /**
     * 奖金总数
     */
    @ExcelProperty(value = "奖金总数")
    private Long totalAmount;

    /**
     * 留存比例
     */
    @ExcelProperty(value = "留存比例")
    private BigDecimal retentionRatio;

    /**
     * 可分配总额
     */
    @ExcelProperty(value = "可分配总额")
    private Long distributable;

    /**
     * 已分配金额
     */
    @ExcelProperty(value = "已分配金额")
    private Long allocated;

    /**
     * 未分配金额
     */
    @ExcelProperty(value = "未分配金额")
    private Long unallocated;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 负责教师
     */
    @ExcelProperty(value = "负责教师")
    private Long teacherInCharge;

    /**
     * 负责教师详情
     */
    @ExcelProperty(value = "负责教师详情")
    private DcimsTeacherVo teacherDetail;
}
