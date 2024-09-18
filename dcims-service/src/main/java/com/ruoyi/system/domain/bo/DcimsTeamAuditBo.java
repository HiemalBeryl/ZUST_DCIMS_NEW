package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.PermitGroup;
import com.ruoyi.common.core.validate.RefuseGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 竞赛审核业务对象 dcims_team_audit
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsTeamAuditBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 团队id
     */
    @NotNull(message = "团队id不能为空", groups = { PermitGroup.class, RefuseGroup.class })
    private Long teamId;

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
    @NotNull(message = "退回理由不能为空", groups = { RefuseGroup.class })
    private String reason;

    /**
     * 下一级审批人id
     */
    @NotNull(message = "请输入下一级审核教师工号", groups = { PermitGroup.class })
    private Long nextTeacherId;


}
