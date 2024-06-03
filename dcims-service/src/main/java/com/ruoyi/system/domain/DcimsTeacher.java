package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 教师信息对象 dcims_teacher
 *
 * @author ruoyi
 * @date 2023-05-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_teacher")
public class DcimsTeacher extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 工号
     */
    private Long teacherId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 学院
     */
    private String college;
    /**
     * 系部
     */
    private String department;
    /**
     * 专业
     */
    private String major;
    /**
     * 职称
     */
    private String title;
    /**
     * 学历
     */
    private String education;
    /**
     * 学位
     */
    private String degree;
    /**
     * 电话
     */
    private String phone;
    /**
     * 电子邮件
     */
    private String email;
    /**
     * 是否为校内教师;0-校外教师 1-校内入职教师
     */
    private String isZust;
    /**
     * 逻辑删除;0-未删除 1-已删除
     */
    private String deleted;

}
