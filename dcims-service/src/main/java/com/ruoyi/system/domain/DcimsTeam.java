package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 参赛团队对象 dcims_team
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_team")
public class DcimsTeam extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 排序号
     */
    private Long orderNum;
    /**
     * 竞赛id
     */
    private Long competitionId;
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
    private String awardLevel;
    /**
     * 指导教师工号
     */
    private String teacherId;
    /**
     * 指导教师姓名
     */
    private String teacherName;
    /**
     * 参赛学生学号
     */
    private String studentId;
    /**
     * 参赛学生姓名
     */
    private String studentName;
    /**
     * 更高级奖项id
     */
    private Long advancedAwardNumber;
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
    private Long supportMaterial;
    /**
     * 审核状态
     */
    private Integer audit;
    /**
     * 下一级审核人工号
     */
    private Long nextAuditId;
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
