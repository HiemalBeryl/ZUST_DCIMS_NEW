package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.DcimsStudent;
import com.ruoyi.system.domain.DcimsTeacher;
import com.ruoyi.system.domain.vo.DcimsStudentVo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsStudentMapper;
import com.ruoyi.system.mapper.DcimsTeacherMapper;
import com.ruoyi.system.service.IDcimsBasicDataService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 基本数据Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
@RequiredArgsConstructor
@Service
public class DcimsBasicDataServiceImpl implements IDcimsBasicDataService {

    private final DcimsStudentMapper studentBaseMapper;
    private final DcimsTeacherMapper teacherBaseMapper;

    @Override
    public TableDataInfo<DcimsStudentVo> listStudentDict(String name) {
        LambdaQueryWrapper<DcimsStudent> lqw = new LambdaQueryWrapper<>();
        lqw.like(DcimsStudent::getName,name);
        List<DcimsStudentVo> result = studentBaseMapper.selectVoList(lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<DcimsTeacherVo> listTeacherDict(String name) {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.like(DcimsTeacher::getName,name);
        List<DcimsTeacherVo> result = teacherBaseMapper.selectVoList(lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public DcimsTeacherVo getTeacherNameById(Long teacherId) {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsTeacher::getTeacherId, teacherId);
        return teacherBaseMapper.selectVoOne(lqw);
    }

    @Override
    public DcimsStudentVo getStudentNameById(Long studentId) {
        LambdaQueryWrapper<DcimsStudent> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsStudent::getStudentId, studentId);
        return studentBaseMapper.selectVoOne(lqw);
    }

    @Override
    public List<DcimsTeacherVo> getTeacherNameByIds(List<Long> teacherIds) {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.in(teacherIds.size() > 0, DcimsTeacher::getTeacherId, teacherIds);
        return teacherBaseMapper.selectVoList(lqw);
    }

    @Override
    public List<DcimsStudentVo> getStudentNameByIds(List<Long> studentIds) {
        LambdaQueryWrapper<DcimsStudent> lqw = new LambdaQueryWrapper<>();
        lqw.in(studentIds.size() > 0, DcimsStudent::getStudentId, studentIds);
        return studentBaseMapper.selectVoList(lqw);
    }

    @Override
    public DcimsTeacherVo queryLoginTeacher() {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsTeacher::getTeacherId,AccountUtils.getAccount().getTeacherId());
        return teacherBaseMapper.selectVoOne(lqw);
    }
}
