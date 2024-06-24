package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.system.domain.DcimsTeacher;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DcimsTeacherMapper extends BaseMapperPlus<DcimsTeacherMapper, DcimsTeacher, DcimsTeacherVo> {

    /*
    * 根据教职工Id批量更新
    * */
    void updateByTeacherIds(List<DcimsTeacherVo> teacherAlreadeExist);
}
