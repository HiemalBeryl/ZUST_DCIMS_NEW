package com.ruoyi.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 奖金分配个人视图对象 dcims_bonus_allocation_personal
 *
 * @author Andy
 * @date 2023-05-03
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsBonusAllocationPersonalVo {

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
     * 获得人
     */
    @ExcelProperty(value = "获得人")
    private Long gainer;

    /**
     * 负责竞赛
     */
    @ExcelProperty(value = "负责竞赛")
    private Long competition;

    /**
     * 获得奖金数
     */
    @ExcelProperty(value = "获得奖金数")
    private Long bonus;

    /**
     * 分配时间
     */
    @ExcelProperty(value = "分配时间")
    private Date allocateTime;

    /**
     * 分配者
     */
    @ExcelProperty(value = "分配者")
    private Long teacherInCharge;


}
