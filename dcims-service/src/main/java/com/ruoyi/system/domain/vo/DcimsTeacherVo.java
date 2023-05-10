package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 教师基本信息对象 dcims_student
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsTeacherVo {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
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
}
