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
 * 获奖基本信息视图对象 dcims_award
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsAwardVo {

    private static final long serialVersionUID = 1L;

    /**
     * 数据编号
     */
    @ExcelProperty(value = "数据编号")
    private Long id;

    /**
     * 父id
     */
    @ExcelProperty(value = "父id")
    private Long parentId;

    /**
     * 赛事名称
     */
    @ExcelProperty(value = "赛事名称")
    private String competitionName;

    /**
     * 队伍名称
     */
    @ExcelProperty(value = "队伍名称")
    private String teamName;

    /**
     * 奖项等级
     */
    @ExcelProperty(value = "奖项等级", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_award_level")
    private String awardLevel;

    /**
     * 比赛类型
     */
    @ExcelProperty(value = "比赛类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "dcims_award_type")
    private String competitionType;

    /**
     * 团队成员姓名
     */
    @ExcelProperty(value = "团队成员姓名")
    private String studentName;

    /**
     * 指导教师姓名
     */
    @ExcelProperty(value = "指导教师姓名")
    private String teacherName;

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


}
