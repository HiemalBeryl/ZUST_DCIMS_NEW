package com.ruoyi.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 竞赛赛事基本信息业务对象 dcims_competition
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CompetitionPartialBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 赛事类别
     */
    //@NotBlank(message = "赛事类别不能为空", groups = { EditGroup.class })
    private String level;

    /**
     * 本年度拨款（万元）
     */
    //@NotNull(message = "本年度拨款不能为空", groups = { EditGroup.class })
    private BigDecimal appropriation;

    /**
     * 个人赛限项
     */
    //@NotNull(message = "个人赛限项不能为空", groups = { EditGroup.class })
    private Integer personLimit;

    /**
     * 团队赛限项
     */
    //@NotNull(message = "团队赛限项不能为空", groups = { EditGroup.class })
    private Integer teamLimit;

}
