package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * test对象 dcims_test
 *
 * @author hiemalberyl
 * @date 2023-04-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dcims_test")
public class DcimsTest extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 排序号
     */
    private Long orderNum;
    /**
     * key键
     */
    private String testKey;
    /**
     * 值
     */
    private String value;
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
