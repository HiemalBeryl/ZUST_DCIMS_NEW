package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import com.ruoyi.system.domain.DcimsStudent;
import com.ruoyi.system.domain.vo.DcimsStudentVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DcimsStudentMapper extends BaseMapperPlus<DcimsCompetitionMapper, DcimsStudent, DcimsStudentVo> {
}
