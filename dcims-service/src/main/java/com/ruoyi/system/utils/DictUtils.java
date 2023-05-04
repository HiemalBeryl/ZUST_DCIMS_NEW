package com.ruoyi.system.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
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
    private static DcimsCompetitionMapper dcimsCompetitionMapper;

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
        int index = 0;
        boolean flag;
        for(DcimsTeacher teacher:teachers){
            allData = allData + '('+Math.toIntExact(teacher.getTeacherId())+','+'\"'+teacher.getName()+'\"'+','+teacher.getTeacherId()+','+'\"'+"dcims_teacher"+'\"'+','+'\"'+"default"+'\"'+','+'\''+"N"+'\''+','+"0"+')'+",";
            index++;
            if (index % 5000 == 0){
                allData = allData.substring(0, allData.length() - 1);
                allData += ";";
                flag = dictDataMapper.InsertList(allData);
                if(flag == false){
                    dictDataMapper.delete(dictDataQueryWrapper);
                    return false;
                }
                allData = "";
            }
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
        int index = 0;
        boolean flag;
        for(DcimsStudent student:students){
            allData = allData + '('+Math.toIntExact(student.getStudentId())+','+'\"'+student.getName()+'\"'+','+student.getStudentId()+','+'\"'+"dcims_student"+'\"'+','+'\"'+"default"+'\"'+','+'\''+"N"+'\''+','+"0"+')'+",";
            index++;
            if (index % 5000 == 0){
                allData = allData.substring(0, allData.length() - 1);
                allData += ";";
                flag = dictDataMapper.InsertList(allData);
                if(flag == false){
                    dictDataMapper.delete(dictDataQueryWrapper);
                    return false;
                }
                allData = "";
            }
        }
        allData = allData.substring(0, allData.length() - 1);
        allData += ";";
        return dictDataMapper.InsertList(allData);
    }

    /**
     * 同步年度竞赛字典
     */
    public static Boolean syncCompetitionDict(int year){
        // 查询字典id
        LambdaQueryWrapper<SysDictType> dictTypeQueryWrapper = new LambdaQueryWrapper<>();
        dictTypeQueryWrapper.eq(SysDictType::getDictType,"dcims_"+year+"competition");
        SysDictType sysDictType = dictTypeMapper.selectOne(dictTypeQueryWrapper);
        // 删除旧字典数据
        LambdaQueryWrapper<SysDictData> dictDataQueryWrapper = new LambdaQueryWrapper<>();
        dictDataQueryWrapper.eq(SysDictData::getDictType,"dcims_"+year+"competition");
        dictDataMapper.delete(dictDataQueryWrapper);
        // 创建新字典数据
        String allData = new String();
        LambdaQueryWrapper<DcimsCompetition> competitionQueryWrapper = new LambdaQueryWrapper<>();
        competitionQueryWrapper.eq(DcimsCompetition::getAnnual,year);
        List<DcimsCompetition> competitions = dcimsCompetitionMapper.selectList(competitionQueryWrapper);
        int index = 0;
        boolean flag;
        for(DcimsCompetition competition:competitions){
            allData = allData + '('+Math.toIntExact(competition.getId())+','+'\"'+competition.getName()+'\"'+','+competition.getId()+','+'\"'+"dcims_"+year+"competition"+'\"'+','+'\"'+"default"+'\"'+','+'\''+"N"+'\''+','+"0"+')'+",";
            index++;
            if (index % 50 == 0){
                allData = allData.substring(0, allData.length() - 1);
                allData += ";";
                flag = dictDataMapper.InsertList(allData);
                if(flag == false){
                    dictDataMapper.delete(dictDataQueryWrapper);
                    return false;
                }
                allData = "";
            }
        }
        allData = allData.substring(0, allData.length() - 1);
        allData += ";";
        return dictDataMapper.InsertList(allData);
    }
}
