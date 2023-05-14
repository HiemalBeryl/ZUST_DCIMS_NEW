package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 工作量分配竞赛视图对象 dcims_worktime_allocation_competition
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsWorktimeAllocationCompetitionVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 竞赛id
     */
    @ExcelProperty(value = "竞赛id")
    private Long competitionId;

    /**
     * 竞赛id
     */
    @ExcelProperty(value = "竞赛名称")
    private String competitionName;

    /**
     * 总计教学工作量
     */
    @ExcelProperty(value = "总计教学工作量")
    private BigDecimal total;

    /**
     * 未分配教学工作量
     */
    @ExcelProperty(value = "未分配教学工作量")
    private BigDecimal remain;

    /**
     * 审核状态
     */
    @ExcelProperty(value = "审核状态")
    private String status;


}
