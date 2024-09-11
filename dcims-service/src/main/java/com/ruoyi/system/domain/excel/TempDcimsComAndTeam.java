package com.ruoyi.system.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TempDcimsComAndTeam {
    /**
     * 赛事名称
     */
    @ExcelProperty(value = {"科技竞赛省级及以上学生及指导教师名单", "项  目"}, index = 0)
    private String name;

    /**
     * 奖项等级
     */
    @ExcelProperty(value = {"科技竞赛省级及以上学生及指导教师名单", "获奖级别"}, index = 1)
    private String awardLevel;

    /**
     * 指导教师姓名
     */
    @ExcelProperty(value = {"科技竞赛省级及以上学生及指导教师名单", "指导教师"}, index = 2)
    private String teacherName;

    /**
     * 参赛学生姓名
     */
    @ExcelProperty(value = {"科技竞赛省级及以上学生及指导教师名单", "学生名单"}, index = 3)
    private String studentName;
}
