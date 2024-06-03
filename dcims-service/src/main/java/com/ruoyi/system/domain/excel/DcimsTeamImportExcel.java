package com.ruoyi.system.domain.excel;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

@Data
public class DcimsTeamImportExcel {
    /**
     * 临时序号
     */
    private int index;

    /**
     * 年份
     */
    @ExcelProperty(value = "年份", index = 0)
    private String year;

    /**
     * 竞赛名称
     */
    @ExcelProperty(value = "竞赛名称", index = 1)
    private String competitionName;

    /**
     * 队伍名称
     */
    @ExcelProperty(value = "队伍名称（如有）", index = 5)
    private String name;

    /**
     * 作品名称
     */
    @ExcelProperty(value = "竞赛作品名（如有）", index = 6)
    private String worksName;

    /**
     * 是否为单人赛
     */
    @ExcelProperty(value = "是否为单人赛", index = 10)
    private String isSingle;

    /**
     * 奖项等级
     */
    @ExcelProperty(value = "获奖级别", index = 2)
    private String awardLevel;

    /**
     * 指导教师姓名
     */
    @ExcelProperty(value = "指导教师名单", index = 4)
    private String teacherName;

    /**
     * 参赛学生姓名
     */
    @ExcelProperty(value = "学生名单", index = 3)
    private String studentName;

    /**
     * 比赛时间
     */
    @ExcelProperty(value = "比赛时间", index = 7)
    private Date competitionTime;

    /**
     * 获奖时间
     */
    @ExcelProperty(value = "获奖时间", index = 8)
    private Date awardTime;

    /**
     * 佐证材料
     */
    @ExcelProperty(value = "获奖佐证材料", index = 9)
    private String supportMaterialFileName;

    /**
     * 竞赛对应主键
     */
    @ExcelIgnore
    private Long competitionId;

    /**
     * 佐证材料
     */
    @ExcelIgnore
    private Long supportMaterial;

    /**
     * 竞赛对应主键
     */
    @ExcelIgnore
    private String teacherId;

    /**
     * 竞赛对应主键
     */
    @ExcelIgnore
    private String studentId;

    /**
     * 审核状态
     */
    @ExcelIgnore
    private Integer audit;

    /**
     * 比赛类型
     */
    private String competitionType;

    /**
     * 导入时不正确的数据提示
     */
    private List<DcimsTeamImportExcelError> errors;

    /**
     * 是否允许编辑
     */
    private boolean edit;
}
