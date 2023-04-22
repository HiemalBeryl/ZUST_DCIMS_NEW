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
 * @date 2023-04-21
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
    @NotBlank(message = "赛事类别不能为空", groups = { AddGroup.class, EditGroup.class })
    private String level;

    /**
     * 往届名称
     */
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
     * 本年度获奖目标
     */
    private String goal;

    /**
     * 审核人id
     */
    private Long nextAuditId;


}
