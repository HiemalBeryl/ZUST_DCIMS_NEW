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
 * 工作量分配视图对象 dcims_worktime_allocation
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsWorktimeAllocationVo {

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
    private String year;

    /**
     * 核算开始时间
     */
    @ExcelProperty(value = "核算开始时间")
    private Date startTime;

    /**
     * 核算结束时间
     */
    @ExcelProperty(value = "核算结束时间")
    private Date endTime;

    /**
     * 计算公式
     */
    @ExcelProperty(value = "计算公式")
    private String fomular;

    /**
     * 核算状态
     */
    @ExcelProperty(value = "核算状态")
    private String status;


}
