package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.PermitGroup;
import com.ruoyi.common.core.validate.RefuseGroup;
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
    private Long id;

    /**
     * 竞赛id
     */
    @NotNull(message = "竞赛id不能为空", groups = { PermitGroup.class, RefuseGroup.class })
    private Long competitionId;

    /**
     * 审批人id
     */
    @Null(message = "不能填入教师id", groups = { PermitGroup.class, RefuseGroup.class })
    private Long teacherId;

    /**
     * 审批结果
     */
    private Long result;

    /**
     * 审批意见
     */
    private String reason;

    /**
     * 下一级审批人id
     */
    @NotNull(message = "下一级审批人id不能为空", groups = { PermitGroup.class })
    private Long nextTeacherId;


}