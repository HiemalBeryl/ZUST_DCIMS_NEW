package com.ruoyi.system.utils.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class Account {
    /**
     * 用户id
     */
    @TableId
    private Long userId;

    /**
     * 教师工号
     */
    private Long teacherId;
}
