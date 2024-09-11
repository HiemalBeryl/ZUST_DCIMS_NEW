package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 教师基本信息对象 dcims_student
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsBasicDataTeacherVo {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 工号
     */
    @JsonProperty("JSZGH")
    private Long teacherId;
    /**
     * 指导教师编号
     */
    private Long competitionTeacherId;
    /**
     * 姓名
     */
    @JsonProperty("XM")
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
    @JsonProperty("ZCDM")
    private String title;
    /**
     * 学历
     */
    @JsonProperty("XLDM")
    private String education;
    /**
     * 学位
     */
    @JsonProperty("XWDM")
    private String degree;
    /**
     * 电话
     */
    @JsonProperty("SJH")
    private String phone;
    /**
     * 电子邮件
     */
    @JsonProperty("DZXX")
    private String email;
    /**
     * 是否为校内教师;0-校外教师 1-校内入职教师
     */
    private String isZust;
}
