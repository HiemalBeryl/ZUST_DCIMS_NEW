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
 * 奖金分配总业务对象 dcims_bonus_allocation
 *
 * @author Andy
 * @date 2023-05-02
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsBonusAllocationBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 年份
     */
    @NotNull(message = "年份不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer years;

    /**
     * 学院
     */
    @NotNull(message = "学院不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer college;

    /**
     * 奖金总数
     */
    @NotNull(message = "奖金总数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long totalAmount;

    /**
     * 留存比例
     */
    @NotNull(message = "留存比例不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal retentionRatio;

    /**
     * 可分配总额
     */
    @NotNull(message = "可分配总额不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long distributable;

    /**
     * 已分配金额
     */
    @NotNull(message = "已分配金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long allocated;

    /**
     * 未分配金额
     */
    @NotNull(message = "未分配金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long unallocated;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date endTime;

    /**
     * 负责教师
     */
    @NotNull(message = "负责教师不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long teacherInCharge;


}
