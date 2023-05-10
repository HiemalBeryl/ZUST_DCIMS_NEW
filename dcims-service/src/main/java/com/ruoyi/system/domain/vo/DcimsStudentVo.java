package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import lombok.Data;

/**
 * 学生基本信息对象 dcims_student
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsStudentVo {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 学号
     */
    private Long studentId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 学院
     */
    private String college;
    /**
     * 专业
     */
    private String major;
    /**
     * 班级
     */
    private String classes;
    /**
     * 电话
     */
    private String phone;
    /**
     * 电子邮件
     */
    private String email;
}
