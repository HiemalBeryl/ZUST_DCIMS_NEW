package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工作量分配业务对象 dcims_worktime_allocation_teacher
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsWorktimeAllocationTeacherBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 工作量竞赛id
     */
    private Long worktimeCompetitionId;

    /**
     * 教师工号
     */
    private Long teacherId;

    /**
     * 教师应得工作量
     */
    private BigDecimal worktime;

    /**
     * 审核状态
     */
    private String status;


}
