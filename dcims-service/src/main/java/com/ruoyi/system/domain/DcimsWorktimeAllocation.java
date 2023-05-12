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
 * 工作量分配对象 dcims_worktime_allocation
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_worktime_allocation")
public class DcimsWorktimeAllocation extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 年份
     */
    private String year;
    /**
     * 核算开始时间
     */
    private Date startTime;
    /**
     * 核算结束时间
     */
    private Date endTime;
    /**
     * 计算公式
     */
    private String fomular;
    /**
     * 参数键值对
     */
    private String parameter;
    /**
     * 核算状态
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
