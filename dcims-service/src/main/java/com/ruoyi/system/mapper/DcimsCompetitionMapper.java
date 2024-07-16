package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 竞赛赛事基本信息Mapper接口
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */
@Mapper
public interface DcimsCompetitionMapper extends BaseMapperPlus<DcimsCompetitionMapper, DcimsCompetition, DcimsCompetitionVo> {


    List<DcimsCompetitionVo> selectCompetitionNameByCompetitionIds(@Param("competitionIds") List<Long> competitionIds);

}
