package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;
import java.util.Date;



/**
 * 竞赛审核视图对象 dcims_competition_audit
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsCompetitionAuditVo {

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
     * 审批人id
     */
    @ExcelProperty(value = "审批人id")
    private Long teacherId;

    /**
     * 审批结果
     */
    @ExcelProperty(value = "审批结果", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_audit_result")
    private Long result;

    /**
     * 审批意见
     */
    @ExcelProperty(value = "审批意见")
    private String reason;

    /**
     * 下一级审批人id
     */
    @ExcelProperty(value = "下一级审批人id")
    private Long nextTeacherId;


}
