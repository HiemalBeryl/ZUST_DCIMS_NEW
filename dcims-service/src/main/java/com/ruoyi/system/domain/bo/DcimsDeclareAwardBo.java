package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsDeclareAwardBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "团队id不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 竞赛id
     */
    @Null(message = "不能填入竞赛id", groups = {EditGroup.class})
    private Long competitionId;

    /**
     * 队伍名称
     */
    @Null(message = "不能填入队伍名称", groups = {EditGroup.class})
    private String name;

    /**
     * 比赛类型
     */
    @Null(message = "不能填入比赛类型", groups = {EditGroup.class})
    private String competitionType;

    /**
     * 奖项等级
     */
    @NotBlank(message = "奖项等级不能为空", groups = {EditGroup.class})
    private String awardLevel;

    /**
     * 指导教师工号
     */
    @Null(message = "不能填入指导教师工号", groups = {EditGroup.class})
    private String teacherId;

    /**
     * 指导教师姓名
     */
    @Null(message = "不能填入指导教师姓名", groups = {EditGroup.class})
    private String teacherName;

    /**
     * 参赛学生学号
     */
    @Null(message = "不能填入参赛学生学号", groups = {EditGroup.class})
    private String studentId;

    /**
     * 参赛学生姓名
     */
    @Null(message = "不能填入参赛学生姓名", groups = {EditGroup.class})
    private String studentName;

    /**
     * 比赛时间
     */
    @Null(message = "不能填入参赛比赛时间", groups = {EditGroup.class})
    private Date competitionTime;

    /**
     * 获奖时间
     */
    @NotNull(message = "获奖时间不能为空", groups = {EditGroup.class})
    private Date awardTime;

    /**
     * 佐证材料
     */
    @NotBlank(message = "佐证材料不能为空", groups = {EditGroup.class})
    private String supportMaterial;

    /**
     * 审核状态
     */
    @Null(message = "不能填入审核状态", groups = {EditGroup.class})
    private Integer audit;

    /**
     * 下一级审核人工号
     */
    @NotNull(message = "审核人工号不能为空", groups = {EditGroup.class})
    private Integer nextAuditId;
}
