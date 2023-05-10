package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.vo.DcimsStudentVo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;

/**
 * 基本数据Service接口
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
public interface IDcimsBasicDataService {
    /**
     * 查询学生姓名学号字典
     */
    TableDataInfo<DcimsStudentVo> listStudentDict(String name);

    /**
     * 查询教师姓名工号字典
     */
    TableDataInfo<DcimsTeacherVo> listTeacherDict(String name);
}
