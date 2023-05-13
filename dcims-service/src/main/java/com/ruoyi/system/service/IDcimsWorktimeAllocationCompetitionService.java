package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsWorktimeAllocationCompetition;
import com.ruoyi.system.domain.vo.AllocationCompetitionDictVo;
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationCompetitionVo;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationCompetitionBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 工作量分配竞赛Service接口
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
public interface IDcimsWorktimeAllocationCompetitionService {

    /**
     * 查询工作量分配竞赛
     */
    DcimsWorktimeAllocationCompetitionVo queryById(Long id);

    /**
     * 查询工作量分配竞赛列表
     */
    TableDataInfo<DcimsWorktimeAllocationCompetitionVo> queryPageList(DcimsWorktimeAllocationCompetitionBo bo, PageQuery pageQuery);

    /**
     * 查询工作量分配竞赛列表
     */
    List<DcimsWorktimeAllocationCompetitionVo> queryList(DcimsWorktimeAllocationCompetitionBo bo);

    /**
     * 新增工作量分配竞赛
     */
    Boolean insertByBo(DcimsWorktimeAllocationCompetitionBo bo);

    /**
     * 修改工作量分配竞赛
     */
    Boolean updateByBo(DcimsWorktimeAllocationCompetitionBo bo);

    /**
     * 校验并批量删除工作量分配竞赛信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
