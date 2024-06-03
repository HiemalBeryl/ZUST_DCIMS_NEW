package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作量分配业务对象 dcims_worktime_allocation
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsWorktimeAllocationBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 年份
     */
    @NotBlank(message = "年份不能为空", groups = { AddGroup.class, EditGroup.class })
    private String year;

    /**
     * 核算开始时间
     */
    @NotNull(message = "核算开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date startTime;

    /**
     * 核算结束时间
     */
    @NotNull(message = "核算结束时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date endTime;

    /**
     * 计算公式
     */
    //@NotBlank(message = "计算公式不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fomular;

    /**
     * 参数键值对
     */
    //@NotBlank(message = "参数键值对不能为空", groups = { AddGroup.class, EditGroup.class })
    private String parameter;

    /**
     * 核算状态
     */
    private String status;


}
