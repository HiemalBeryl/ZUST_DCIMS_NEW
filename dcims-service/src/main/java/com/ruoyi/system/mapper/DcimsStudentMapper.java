package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.system.domain.DcimsStudent;
import com.ruoyi.system.domain.vo.DcimsBasicDataStudentVo;
import com.ruoyi.system.domain.vo.DcimsStudentVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DcimsStudentMapper extends BaseMapperPlus<DcimsCompetitionMapper, DcimsStudent, DcimsStudentVo> {
    /*
    * 根据学号批量更新
    * */
    void updateByStudentIds(List<DcimsBasicDataStudentVo> studentsUpdate);


    /*
    * 根据学号批量插入
    * */
    void insertByStudentIds(List<DcimsBasicDataStudentVo> studentsInsert);
}
