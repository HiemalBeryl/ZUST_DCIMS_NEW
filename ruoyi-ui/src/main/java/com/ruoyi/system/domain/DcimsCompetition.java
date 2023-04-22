package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 竞赛赛事基本信息对象 dcims_competition
 *
 * @author hiemalberyl
 * @date 2023-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_competition")
public class DcimsCompetition extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 用户id
     */
    private Long userId;
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
     * 赛事届次
     */
    private Long term;
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
     * 本年度拨款
     */
    private BigDecimal appropriation;
    /**
     * 本年度获奖目标
     */
    private String goal;
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
     * 审核人id
     */
    private Long nextAuditId;
    /**
     * 审核状态
     */
    private String state;
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
