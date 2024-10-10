package com.ruoyi.system.domain.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import com.ruoyi.system.domain.vo.DcimsTeamAuditVo;
import lombok.Data;

import java.util.Date;

@Data
@ExcelIgnoreUnannotated
public class DcimsTeamExportExcel {

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
     * 队伍名称
     */
    @ExcelProperty(value = "队伍名称")
    private String name;

    /**
     * 作品名称
     */
    @ExcelProperty(value = "作品名称")
    private String worksName;

    /**
     * 比赛类型
     */
    @ExcelProperty(value = "比赛类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_award_type")
    private String competitionType;

    /**
     * 奖项等级
     */
    @ExcelProperty(value = "奖项等级", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_award_level")
    private String awardLevel;

    /**
     * 指导教师工号
     */
    @ExcelProperty(value = "指导教师工号")
    private String teacherId;

    /**
     * 指导教师姓名
     */
    @ExcelProperty(value = "指导教师姓名")
    private String teacherName;

    /**
     * 参赛学生学号
     */
    @ExcelProperty(value = "参赛学生学号")
    private String studentId;

    /**
     * 参赛学生姓名
     */
    @ExcelProperty(value = "参赛学生姓名")
    private String studentName;

    /**
     * 更高级奖项id
     */
    private Long advancedAwardNumber;

    /**
     * 比赛时间
     */
    private Date competitionTime;

    /**
     * 获奖时间
     */
    @ExcelProperty(value = "获奖时间")
    @JsonFormat(locale="zh",timezone="GMT+8",pattern="yyyy/MM/dd")
    @ColumnWidth(20)
    private Date awardTime;

    /**
     * 所属学院
     */
    @ExcelProperty(value = "所属学院")
    private String collegeName;

    /**
     * 佐证材料
     */
    @ExcelProperty(value = "佐证材料")
    private Long supportMaterial;

    /**
     * 佐证材料地址
     */
    private String supportMaterialURL;

    /**
     * 审核状态
     */
    @ExcelProperty(value = "审核状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_declare_award_status")
    private Integer audit;

     /**
      * 下一级审核人工号
      */
     private Long nextAuditId;

     /**
      * 审核状态详细
      */
     private DcimsTeamAuditVo auditDetail;

     /**
      * 创建人
     */
    private String createBy;

}
