package com.ruoyi.system.domain.bo;

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
 * @date 2023-04-12
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
     * 部门id
     */
    @NotNull(message = "部门id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deptId;

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 排序号
     */
    @NotNull(message = "排序号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long orderNum;

    /**
     * 赛事名称
     */
    @NotBlank(message = "赛事名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 赛事类别
     */
    @NotBlank(message = "赛事类别不能为空", groups = { AddGroup.class, EditGroup.class })
    private String level;

    /**
     * 往届名称
     */
    @NotBlank(message = "往届名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String pastName;

    /**
     * 赛事届次
     */
    @NotNull(message = "赛事届次不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long term;

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
     * 校内选拔时间
     */
    @NotNull(message = "校内选拔时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date innerTime;

    /**
     * 省赛时间
     */
    @NotNull(message = "省赛时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date provinceTime;

    /**
     * 国赛时间
     */
    @NotNull(message = "国赛时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date nationalTime;

    /**
     * 立项结束时间
     */
    @NotNull(message = "立项结束时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date stopTime;

    /**
     * 集中授课时数
     */
    @NotNull(message = "集中授课时数不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal concentrationOfTeachingHours;

    /**
     * 本年度申报经费（万元）
     */
    @NotNull(message = "本年度申报经费（万元）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal budget;

    /**
     * 本年度获奖目标
     */
    @NotBlank(message = "本年度获奖目标不能为空", groups = { AddGroup.class, EditGroup.class })
    private String goal;

    /**
     * 本年度拨款
     */
    private BigDecimal appropriation;

    /**
     * 奖金核算状态
     */
    private String moneyAggregate;

    /**
     * 工作量核算状态
     */
    private String workloadAggregate;

    /**
     * 审核状态
     */
    private String state;


}
