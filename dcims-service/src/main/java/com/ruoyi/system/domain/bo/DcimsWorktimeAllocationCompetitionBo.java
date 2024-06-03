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
 * 工作量分配竞赛业务对象 dcims_worktime_allocation_competition
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsWorktimeAllocationCompetitionBo extends BaseEntity {

    /**
     * 主键
     */
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


}
