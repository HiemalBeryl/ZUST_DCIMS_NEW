package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dcims_global_setting")
public class DcimsGlobalSetting {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    private String year;

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

    /**
     * 状态
     */
    @TableLogic
    private String status;
}
