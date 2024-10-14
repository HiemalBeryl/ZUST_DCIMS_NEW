package com.ruoyi.system.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.system.converter.DateConverter;
import lombok.Data;

import java.util.Date;

@Data
public class TeamSingleStudent {
    @ExcelProperty(value = "学号")
    private String studentNumber;

    @ExcelProperty(value = "学生姓名")
    private String studentName;

    @ExcelProperty(value = "竞赛名称")
    private String competitionName;

    @ExcelProperty(value = "获奖时间", converter = DateConverter.class)
    private Date awardTime;

    @ExcelProperty(value = "获奖类别")
    private String awardLevel;

    @ExcelProperty(value = "获奖等级")
    private String awardLevelSubstring;

    @ExcelProperty(value = "竞赛类型")
    private String competitionType;

    @ExcelProperty(value = "说明")
    private String description;
}
