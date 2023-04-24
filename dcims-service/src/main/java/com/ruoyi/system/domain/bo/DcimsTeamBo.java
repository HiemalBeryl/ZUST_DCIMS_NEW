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
 * 参赛团队业务对象 dcims_team
 *
 * @author hiemalberyl
 * @date 2023-04-24
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsTeamBo extends BaseEntity {

    /**
     * 竞赛id
     */
    @NotNull(message = "竞赛id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long competitionId;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 比赛类型
     */
    @NotBlank(message = "比赛类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String competitionType;

    /**
     * 奖项等级
     */
    @NotBlank(message = "奖项等级不能为空", groups = { AddGroup.class, EditGroup.class })
    private String awardLevel;

    /**
     * 指导教师工号
     */
    @NotBlank(message = "指导教师工号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String teacherId;

    /**
     * 指导教师姓名
     */
    @NotBlank(message = "指导教师姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String teacherName;

    /**
     * 参赛学生学号
     */
    @NotBlank(message = "参赛学生学号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String studentId;

    /**
     * 参赛学生姓名
     */
    @NotBlank(message = "参赛学生姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String studentName;

    /**
     * 比赛时间
     */
    private Date competitionTime;

    /**
     * 获奖时间
     */
    private Date awardTime;

    /**
     * 佐证材料
     */
    @NotBlank(message = "佐证材料不能为空", groups = { AddGroup.class, EditGroup.class })
    private String supportMaterial;


}
