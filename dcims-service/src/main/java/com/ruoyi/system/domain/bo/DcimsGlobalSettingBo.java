package com.ruoyi.system.domain.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class DcimsGlobalSettingBo {
    /**
     * 主键
     */
    private Long id;

    /**
     * 是否允许赛事立项
     */
    private Boolean establishCompetition;

    /**
     * 立项开始时间
     */
    private Date competitionStartTime;

    /**
     * 立项结束时间
     */
    private Date competitionEndTime;

    /**
     * 是否允许获奖申报
     */
    private Boolean createTeam;

    /**
     * 获奖申报开始时间
     */
    private Date teamStartTime;

    /**
     * 获奖申报结束时间
     */
    private Date teamEndTime;
}
