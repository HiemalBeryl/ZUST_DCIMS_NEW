package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.TreeEntity;

/**
 * 获奖基本信息对象 dcims_award
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_award")
public class DcimsAward extends TreeEntity<DcimsAward> {

    private static final long serialVersionUID=1L;

    /**
     * 数据编号
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 所属赛事编号
     */
    private Long competitionId;
    /**
     * 赛事名称
     */
    private String competitionName;
    /**
     * 队伍名称
     */
    private String teamName;
    /**
     * 奖项等级
     */
    private String awardLevel;
    /**
     * 比赛类型
     */
    private String competitionType;
    /**
     * 团队成员姓名
     */
    private String studentName;
    /**
     * 团队成员学号
     */
    private String studentId;
    /**
     * 指导教师姓名
     */
    private String teacherName;
    /**
     * 指导教师工号
     */
    private String teacherId;
    /**
     * 比赛时间
     */
    private Date competitionTime;
    /**
     * 获奖时间
     */
    private Date awardTime;
    /**
     * 审核状态
     */
    private String audit;
    /**
     * 审核信息;关于审核状态的详细说明
     */
    private String auditInformation;
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
