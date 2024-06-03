package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.system.domain.DcimsTeacher;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DcimsTeacherMapper extends BaseMapperPlus<DcimsTeacherMapper, DcimsTeacher, DcimsTeacherVo> {
}
