package com.ruoyi.system.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.system.utils.domain.DcimsStudent;
import com.ruoyi.system.utils.domain.DcimsTeacher;
import com.ruoyi.system.utils.mapper.DcimsStudentMapper;
import com.ruoyi.system.utils.mapper.DcimsTeacherMapper;
import com.ruoyi.system.utils.mapper.DictDataMapper;
import com.ruoyi.system.utils.mapper.DictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class DictUtils {

    private static DictDataMapper dictDataMapper;
    private static DictTypeMapper dictTypeMapper;
    private static DcimsStudentMapper dcimsStudentMapper;
    private static DcimsTeacherMapper dcimsTeacherMapper;

    @Autowired
    public DictUtils(DictDataMapper dictDataMapper, DictTypeMapper dictTypeMapper, DcimsStudentMapper dcimsStudentMapper, DcimsTeacherMapper dcimsTeacherMapper){
        DictUtils.dictDataMapper = dictDataMapper;
        DictUtils.dictTypeMapper = dictTypeMapper;
        DictUtils.dcimsStudentMapper = dcimsStudentMapper;
        DictUtils.dcimsTeacherMapper = dcimsTeacherMapper;
    }

    /**
     * 同步教师信息字典
     */
    public static Boolean syncTeacherDict(){
        // 查询字典id
        LambdaQueryWrapper<SysDictType> dictTypeQueryWrapper = new LambdaQueryWrapper<>();
        dictTypeQueryWrapper.eq(SysDictType::getDictType,"dcims_teacher");
        SysDictType sysDictType = dictTypeMapper.selectOne(dictTypeQueryWrapper);
        // 删除旧字典数据
        LambdaQueryWrapper<SysDictData> dictDataQueryWrapper = new LambdaQueryWrapper<>();
        dictDataQueryWrapper.eq(SysDictData::getDictType,"dcims_teacher");
        dictDataMapper.delete(dictDataQueryWrapper);
        // 创建新字典数据
        String allData = new String();
        List<DcimsTeacher> teachers = dcimsTeacherMapper.selectList(new QueryWrapper<>());
        for(DcimsTeacher teacher:teachers){
            allData = allData + '('+Math.toIntExact(teacher.getTeacherId())+','+'\"'+teacher.getName()+'\"'+','+teacher.getTeacherId()+','+'\"'+"dcims_teacher"+'\"'+','+'\"'+"default"+'\"'+','+'\''+"N"+'\''+','+"0"+')'+",";
        }
        allData = allData.substring(0, allData.length() - 1);
        allData += ";";
        return dictDataMapper.InsertList(allData);
    }

    /**
     * 同步学生信息字典
     */
    public static Boolean syncStudentDict(){
        // 查询字典id
        LambdaQueryWrapper<SysDictType> dictTypeQueryWrapper = new LambdaQueryWrapper<>();
        dictTypeQueryWrapper.eq(SysDictType::getDictType,"dcims_student");
        SysDictType sysDictType = dictTypeMapper.selectOne(dictTypeQueryWrapper);
        // 删除旧字典数据
        LambdaQueryWrapper<SysDictData> dictDataQueryWrapper = new LambdaQueryWrapper<>();
        dictDataQueryWrapper.eq(SysDictData::getDictType,"dcims_student");
        dictDataMapper.delete(dictDataQueryWrapper);
        // 创建新字典数据
        String allData = new String();
        List<DcimsStudent> students = dcimsStudentMapper.selectList(new QueryWrapper<>());
        for(DcimsStudent student:students){
            allData = allData + '('+Math.toIntExact(student.getStudentId())+','+'\"'+student.getName()+'\"'+','+student.getStudentId()+','+'\"'+"dcims_student"+'\"'+','+'\"'+"default"+'\"'+','+'\''+"N"+'\''+','+"0"+')'+",";
        }
        allData = allData.substring(0, allData.length() - 1);
        allData += ";";
        return dictDataMapper.InsertList(allData);
    }
}
