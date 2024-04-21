package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.validate.AddGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotNull;

/**
 * 竞赛赛事基本信息对象 dcims_competition
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_competition")
public class DcimsCompetition extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 排序号
     */
    private Long orderNum;
    /**
     * 赛事名称
     */
    private String name;
    /**
     * 赛事类别
     */
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
    private String term;
    /**
     * 赛事年份
     */
    private Integer annual;
    /**
     * 主办单位
     */
    private String organizer;
    /**
     * 竞赛负责人工号
     */
    private Long responsiblePersonId;
    /**
     * 竞赛负责人
     */
    private String responsiblePersonName;
    /**
     * 所属学院
     */
    private Long college;
    /**
     * 负责人手机号
     */
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
     * 立项结束时间
     */
    private Date stopTime;
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
    private String teachingHours;
    /**
     * 集中授课安排表
     */
    private Long teachingHoursAttachment;
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
     * 奖金核算状态
     */
    private String moneyAggregate;
    /**
     * 工作量核算状态
     */
    private String workloadAggregate;
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
    /**
     * 审核状态
     */
    private String state;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 版本
     */
    @Version
    private Long version;
    /**
     * 删除标志
     */
    @TableLogic
    private Long delFlag;

}
