package com.ruoyi.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 奖金分配个人业务对象 dcims_bonus_allocation_personal
 *
 * @author Andy
 * @date 2023-05-03
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsBonusAllocationPersonalBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 父表的主键
     */
    private Long allocationId;

    /**
     * 年份
     */
    @NotNull(message = "年份不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer years;

    /**
     * 获得人
     */
    @NotNull(message = "获得人不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long gainer;

    /**
     * 负责竞赛
     */
    @NotNull(message = "负责竞赛不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long competition;

    /**
     * 学院
     */
    private Long college;

    /**
     * 获得奖金数
     */
    @NotNull(message = "获得奖金数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long bonus;

    /**
     * 分配时间
     */
    @NotNull(message = "分配时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date allocateTime;

    /**
     * 分配者
     */
    @NotNull(message = "分配者不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long teacherInCharge;


}
