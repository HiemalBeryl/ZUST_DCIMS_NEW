package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 工作量分配视图对象 dcims_worktime_allocation_teacher
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsWorktimeAllocationTeacherVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 工作量竞赛id
     */
    @ExcelProperty(value = "工作量竞赛id")
    private Long worktimeCompetitionId;

    /**
     * 教师工号
     */
    @ExcelProperty(value = "教师工号")
    private Long teacherId;

    /**
     * 教师应得工作量
     */
    @ExcelProperty(value = "教师应得工作量")
    private BigDecimal worktime;

    /**
     * 审核状态
     */
    @ExcelProperty(value = "审核状态")
    private String status;


}
