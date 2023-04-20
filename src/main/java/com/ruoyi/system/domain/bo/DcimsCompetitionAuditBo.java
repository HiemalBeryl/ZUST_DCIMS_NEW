package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 竞赛审核业务对象 dcims_competition_audit
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsCompetitionAuditBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 竞赛id
     */
    @NotNull(message = "竞赛id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long competitionId;

    /**
     * 审批人id
     */
    @NotNull(message = "审批人id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long teacherId;

    /**
     * 审批结果
     */
    @NotNull(message = "审批结果不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long result;

    /**
     * 审批意见
     */
    @NotBlank(message = "审批意见不能为空", groups = { AddGroup.class, EditGroup.class })
    private String reason;

    /**
     * 下一级审批人id
     */
    @NotNull(message = "下一级审批人id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long nextTeacherId;


}
