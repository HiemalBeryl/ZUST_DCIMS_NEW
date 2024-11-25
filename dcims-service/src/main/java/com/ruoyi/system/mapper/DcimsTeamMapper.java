package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.DcimsTeamWithCompetition;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 参赛团队Mapper接口
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
public interface DcimsTeamMapper extends BaseMapperPlus<DcimsTeamMapper, DcimsTeam, DcimsTeamVo> {
    @Select({
        "<script>",
        "SELECT c.name, c.level, c.organizer, c.college, c.responsible_person_name, c.responsible_person_id, c.phone, c.single_race, c.province_time, c.national_time, c.teaching_hours, t.competition_id, t.award_level, t.student_name, t.teacher_name, t.student_id, t.teacher_id, t.award_time, t.competition_type, t.competition_time FROM dcims_team AS t, dcims_competition AS c",
        "<where>",
        "   c.id = t.competition_id AND (c.del_flag = 0 AND t.del_flag = 0) AND (c.state = 1 AND t.audit = 2)",
        "   <if test='annual != null'>",
        "       AND annual = #{annual};",
        "   </if>",
        "</where>",
        "</script>",
    })
    List<DcimsTeamWithCompetition> selectTeamWithCompetition(@Param("annual") Integer annual);
}
