package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;

import java.util.Collection;
import java.util.List;

/**
 * 竞赛赛事基本信息Service接口
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */
public interface IDcimsCompetitionService {

    /**
     * 查询竞赛赛事基本信息
     */
    DcimsCompetitionVo queryById(Long id);

    /**
     * 查询竞赛赛事基本信息列表
     */
    TableDataInfo<DcimsCompetitionVo> queryPageList(DcimsCompetitionBo bo, PageQuery pageQuery);

    /**
     * 根据登录用户对应教师工号，查询竞赛赛事基本信息列表
     */
    TableDataInfo<DcimsCompetitionVo> queryPageListByTeacherId(DcimsCompetitionBo bo, PageQuery pageQuery);

    /**
     * 查询竞赛赛事基本信息列表
     */
    List<DcimsCompetitionVo> queryList(DcimsCompetitionBo bo);

    /**
     * 新增竞赛赛事基本信息
     */
    Boolean insertByBo(DcimsCompetitionBo bo);

    /**
     * 修改竞赛赛事基本信息
     */
    Boolean updateByBo(DcimsCompetitionBo bo);

    /**
     * 校验并批量删除竞赛赛事基本信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 获取竞赛对应指导教师
     */
    TableDataInfo<DcimsTeacherVo> getTutorList(Long id);

    /**
     * 添加多个指导教师
     */
    Boolean addTutor(Long competitionId, Long[] teacherIds);

    /**
     * 根据id删除指导教师
     */
    Boolean removeTutor(Long id);
}
