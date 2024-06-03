package com.ruoyi.system.domain.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AllocationCompetitionDictVo {
    /**
     * 竞赛名称
     */
    private String name;

    /**
     * 对应分配表中的主键
     */
    private Long id;

    /**
     * 年份
     */
    private Integer annual;
}
