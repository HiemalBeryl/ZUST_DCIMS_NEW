package com.ruoyi.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 竞赛赛事基本信息业务对象 dcims_competition
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsCompetitionBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 赛事名称
     */
    @NotBlank(message = "赛事名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 赛事类别
     */
    //@NotBlank(message = "赛事类别不能为空", groups = { EditGroup.class })
    private String level;

    /**
     * 往届名称
     */
    private String pastName;

    /**
     * 赛事官网
     */
    private String website;

    /**
     * 赛事届次
     */
    @NotNull(message = "赛事届次不能为空", groups = { AddGroup.class, EditGroup.class })
    private String term;

    /**
     * 赛事年份
     */
    @NotNull(message = "赛事年份不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer annual;

    /**
     * 主办单位
     */
    @NotBlank(message = "主办单位不能为空", groups = { AddGroup.class, EditGroup.class })
    private String organizer;

    /**
     * 竞赛负责人工号
     */
    @NotNull(message = "竞赛负责人工号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long responsiblePersonId;

    /**
     * 竞赛负责人
     */
    @NotBlank(message = "竞赛负责人不能为空", groups = { AddGroup.class, EditGroup.class })
    private String responsiblePersonName;

    /**
     * 所属学院
     */
    @NotNull(message = "所属学院不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long college;

    /**
     * 负责人手机号
     */
    @NotNull(message = "手机号不能为空", groups = { AddGroup.class})
    private Long phone;

    /**
     * 校内选拔时间
     */
    private Date innerTime;

    /**
     * 省赛时间
     */
    private Date provinceTime;

    /**
     * 国赛时间
     */
    private Date nationalTime;

    /**
     * 本年度申报经费（万元）
     */
    private BigDecimal budget;

    /**
     * 本年度拨款（万元）
     */
    private BigDecimal appropriation;

    /**
     * 总学时
     */
    //@NotNull(message = "总学时不能为空", groups = { AddGroup.class, EditGroup.class })
    private String teachingHours;

    /**
     * 集中授课安排表
     */
    //@NotBlank(message = "请上传集中授课安排表", groups = { AddGroup.class, EditGroup.class })
    private String teachingHoursAttachment;

    /**
     * 获奖目标
     */
    private String goal;

    /**
     * 赛事简介
     */
    private String introduction;

    /**
     * 竞赛申报书
     */
    @NotNull(message = "请上传竞赛申报书", groups = { AddGroup.class, EditGroup.class })
    private Long attachment;

    /**
     * 官方红头文件类型，0-文件，1-url
     */
    private String redHeaderFileType;

    /**
     * 官方红头文件
     */
    private String redHeaderFile;

    /**
     * 个人赛限项
     */
    private Integer personLimit;

    /**
     * 团队赛限项
     */
    private Integer teamLimit;

    /**
     * 审核人工号
     */
    private Long nextAuditId;


}
