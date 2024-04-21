package com.ruoyi.system.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
public class DcimsTeamImportExcel {

    /**
     * 竞赛id
     */
    private Long competitionId;

    /**
     * 年份
     */
    @ExcelProperty(value = "年份")
    private String year;

    /**
     * 竞赛名称
     */
    @ExcelProperty(value = "竞赛名称")
    private String competitionName;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 作品名称
     */
    @ExcelProperty(value = "竞赛作品名")
    private String worksName;

    /**
     * 比赛类型
     */
    private String competitionType;

    /**
     * 奖项等级
     */
    @ExcelProperty(value = "获奖级别")
    private String awardLevel;

    /**
     * 指导教师工号
     */
    private String teacherId;

    /**
     * 指导教师姓名
     */
    @ExcelProperty(value = "指导教师名单")
    private String teacherName;

    /**
     * 参赛学生学号
     */
    private String studentId;

    /**
     * 参赛学生姓名
     */
    @ExcelProperty(value = "学生名单")
    private String studentName;

    /**
     * 比赛时间
     */
    @ExcelProperty(value = "比赛时间")
    private Date competitionTime;

    /**
     * 获奖时间
     */
    @ExcelProperty(value = "获奖时间")
    private Date awardTime;

    /**
     * 佐证材料
     */
    @ExcelProperty(value = "获奖佐证材料")
    private String supportMaterialFileName;

    /**
     * 佐证材料
     */
    private Long supportMaterial;

    /**
     * 审核状态
     */
    private Integer audit;
}
