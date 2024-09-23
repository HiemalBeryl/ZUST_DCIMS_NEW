package com.ruoyi.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
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
 * @date 2023-05-01
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsTeamBo extends BaseEntity {

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
     * 竞赛名称
     */
    private String competitionName;

    /**
     * 赛事年份
     */
    private Integer annual;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 作品名称
     */
    private String worksName;

    /**
     * 比赛类型
     */
    private String competitionType;

    /**
     * 奖项等级
     */
    @Null(message = "创建队伍时不能填写奖项等级，如需填写，请进入获奖申报页面", groups = { AddGroup.class })
    private String awardLevel;

    /**
     * 赛事类别
     */
    private String level;

    /**
     * 指导教师工号
     */
    @NotBlank(message = "指导教师工号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String teacherId;

    /**
     * 指导教师姓名
     */
    //@NotBlank(message = "指导教师姓名不能为空", groups = { AddGroup.class })
    private String teacherName;

    /**
     * 参赛学生学号
     */
    @NotBlank(message = "参赛学生学号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String studentId;

    /**
     * 参赛学生姓名
     */
    //@NotBlank(message = "参赛学生姓名不能为空", groups = { AddGroup.class })
    private String studentName;

    /**
     * 比赛时间
     */
    private Date competitionTime;

    /**
     * 获奖时间
     */
    @Null(message = "创建队伍时不能填写获奖时间，如需填写，请进入获奖申报页面", groups = { AddGroup.class })
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date awardTime;

    /**
     * 佐证材料
     */
    @Null(message = "创建队伍时不能上传佐证材料，如需上传，请进入获奖申报页面", groups = { AddGroup.class })
    private Long supportMaterial;

    /*
    * 下一级审核人Id
    * */
    private String next_audit_id;

    /**
     * 审核状态
     */
    private Integer audit;

    /**
     * 所属学院
     */
    private Long college;
}
