package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生信息对象 dcims_student
 *
 * @author ruoyi
 * @date 2023-05-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_student")
public class DcimsStudent extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 学号
     */
    private String studentId;
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
    /**
     * 逻辑删除;0-未删除 1-已删除
     */
    private String deleted;

}
