package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.vo.DcimsTeamAuditVo;
import com.ruoyi.system.domain.vo.DcimsTeamVo;

import java.util.List;

/**
 * 团队获奖审核Service接口
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
public interface IDcimsTeamAuditService {

    /**
     * 查询竞赛审核
     */
    DcimsTeamVo queryById(Long id);

    /**
     * 查询竞赛审核列表
     */
    TableDataInfo<DcimsTeamVo> queryPageListAudit(DcimsTeamAuditBo bo, PageQuery pageQuery);

    /**
     * 查询竞赛审核列表
     */
    List<DcimsTeamAuditVo> queryList(DcimsTeamAuditBo bo);

    /**
     * 新增竞赛审核
     */
    Boolean insertByBo(List<DcimsTeamAuditBo> boList);

    /**
     * 校验并批量删除竞赛审核信息
     */
    Boolean deleteWithValidByIds(List<DcimsTeamAuditBo> boList);
}
