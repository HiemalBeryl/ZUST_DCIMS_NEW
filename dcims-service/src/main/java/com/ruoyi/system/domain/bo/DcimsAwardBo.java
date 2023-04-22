package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.TreeEntity;

/**
 * 获奖基本信息业务对象 dcims_award
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DcimsAwardBo extends TreeEntity<DcimsAwardBo> {

    /**
     * 数据编号
     */

    private Long id;

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
     * 指导教师姓名
     */
    private String teacherName;

    /**
     * 比赛时间
     */
    private Date competitionTime;

    /**
     * 获奖时间
     */
    private Date awardTime;


}
