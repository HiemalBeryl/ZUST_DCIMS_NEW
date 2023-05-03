package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 奖金分配个人对象 dcims_bonus_allocation_personal
 *
 * @author Andy
 * @date 2023-05-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_bonus_allocation_personal")
public class DcimsBonusAllocationPersonal extends BaseEntity {

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
     * 获得人
     */
    private Long gainer;
    /**
     * 负责竞赛
     */
    private Long competition;
    /**
     * 获得奖金数
     */
    private Long bonus;
    /**
     * 分配时间
     */
    private Date allocateTime;
    /**
     * 分配者
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
