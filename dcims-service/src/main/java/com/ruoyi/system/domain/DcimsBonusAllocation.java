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
 * 奖金分配总对象 dcims_bonus_allocation
 *
 * @author Andy
 * @date 2023-05-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_bonus_allocation")
public class DcimsBonusAllocation extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 年份
     */
    private Integer years;
    /**
     * 学院
     */
    private Integer college;
    /**
     * 奖金总数
     */
    private Long totalAmount;
    /**
     * 留存比例
     */
    private BigDecimal retentionRatio;
    /**
     * 可分配总额
     */
    private Long distributable;
    /**
     * 已分配金额
     */
    private Long allocated;
    /**
     * 未分配金额
     */
    private Long unallocated;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 负责教师
     */
    private Long teacherInCharge;
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
