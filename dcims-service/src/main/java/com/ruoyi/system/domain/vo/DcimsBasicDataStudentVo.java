package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 学生基本信息对象 dcims_student
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsBasicDataStudentVo {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 学号
     */
    @JsonProperty("xh")
    private String studentId;
    /**
     * 姓名
     */
    @JsonProperty("xm")
    private String name;
    /**
     * 学院
     */
    @JsonProperty("xymc")
    private String college;
    /**
     * 专业
     */
    @JsonProperty("zymc")
    private String major;
    /**
     * 班级
     */
    @JsonProperty("bjmc")
    private String classes;
    /**
     * 电话
     */
    @JsonProperty("lxdh")
    private String phone;
    /**
     * 电子邮件
     */
    @JsonProperty("dzyxdz")
    private String email;
}
