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
 * 参赛团队视图对象 dcims_team
 *
 * @author hiemalberyl
 * @date 2023-04-24
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsTeamVo {

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
     * 佐证材料
     */
    @ExcelProperty(value = "佐证材料")
    private String supportMaterial;


}
