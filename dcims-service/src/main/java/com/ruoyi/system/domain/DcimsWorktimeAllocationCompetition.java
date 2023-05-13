package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作量分配竞赛对象 dcims_worktime_allocation_competition
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_worktime_allocation_competition")
public class DcimsWorktimeAllocationCompetition extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 竞赛id
     */
    private Long competitionId;
    /**
     * 工作量id
     */
    private Long worktimeId;
    /**
     * 总计教学工作量
     */
    private BigDecimal total;
    /**
     * 未分配教学工作量
     */
    private BigDecimal remain;
    /**
     * 审核状态
     */
    private String status;
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
