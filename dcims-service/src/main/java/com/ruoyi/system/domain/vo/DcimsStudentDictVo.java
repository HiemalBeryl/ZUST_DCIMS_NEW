package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 学生基本信息字典对象 dcims_student
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
@Data
@ExcelIgnoreUnannotated
public class DcimsStudentDictVo {
    private static final long serialVersionUID = 1L;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 姓名
     */
    private String name;
}
