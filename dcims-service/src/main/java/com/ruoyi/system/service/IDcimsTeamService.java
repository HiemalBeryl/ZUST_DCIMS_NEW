package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 参赛团队Service接口
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
public interface IDcimsTeamService {

    /**
     * 查询参赛团队
     */
    DcimsTeamVo queryById(Long id);

    /**
     * 查询参赛团队列表
     */
    TableDataInfo<DcimsTeamVo> queryPageList(DcimsTeamBo bo, PageQuery pageQuery);

    /**
     * 根据教师工号查询参赛团队列表
     */
    TableDataInfo<DcimsTeamVo> queryPageListByTeacherId(DcimsTeamBo bo, PageQuery pageQuery);

    /**
     * 查询参赛团队列表
     */
    List<DcimsTeamVo> queryList(DcimsTeamBo bo);

    /**
     * 新增参赛团队
     */
    Boolean insertByBo(DcimsTeamBo bo);

    /**
     * 修改参赛团队
     */
    Boolean updateByBo(DcimsTeamBo bo);

    /**
     * 为团队添加获奖信息
     */

    Boolean declareAwardByBo(DcimsDeclareAwardBo bo);

    /**
     * 校验并批量删除参赛团队信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
