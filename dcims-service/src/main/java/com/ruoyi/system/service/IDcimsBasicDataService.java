package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.DcimsTeacher;
import com.ruoyi.system.domain.vo.DcimsStudentVo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;

import java.util.List;

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
    TableDataInfo<DcimsStudentVo> listStudentDict(String name, boolean exactMatch);


    /**
     * 查询教师姓名工号字典
     */
    TableDataInfo<DcimsTeacherVo> listTeacherDict(String name, boolean exactMatch);



    /**
     * 根据工号查教师姓名
     */
    DcimsTeacherVo getTeacherNameById(Long teacherId);

    /**
     * 根据工号查学生姓名
     */
    DcimsStudentVo getStudentNameById(String studentId);

    /**
     * 根据工号查教师姓名
     */
    List<DcimsTeacherVo> getTeacherNameByIds(List<Long> teacherIds);

    /**
     * 根据工号查学生姓名
     */
    List<DcimsStudentVo> getStudentNameByIds(List<String> studentIds);

    /**
     * 查询登录用户所对应的教师信息
     */
    DcimsTeacherVo queryLoginTeacher();

    /**
     * 从远程同步教师信息
     */
    void syncTeacherInfo();

    /**
     * 从远程同步学生信息
     */
    void syncStudentInfo();
}
