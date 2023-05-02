package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞赛审核对象 dcims_team_audit
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_team_audit")
public class DcimsTeamAudit extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type=IdType.AUTO)
    private Long id;
    /**
     * 竞赛id
     */
    private Long teamId;
    /**
     * 审批人id
     */
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
    private Long nextTeacherId;
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
