package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsCompetitionAudit;
import com.ruoyi.system.domain.bo.CompetitionPartialBo;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionAuditVo;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;

import java.util.Collection;
import java.util.List;

/**
 * 竞赛审核Service接口
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
public interface IDcimsCompetitionAuditService {

    /**
     * 查询竞赛审核
     */
    DcimsCompetitionVo queryById(Long id);

    /**
     * 查询竞赛审核列表
     */
    TableDataInfo<DcimsCompetitionVo> queryPageList(DcimsCompetitionAuditBo bo, PageQuery pageQuery);

    /**
     * 查询竞赛审核列表
     */
    List<DcimsCompetitionAuditVo> queryList(DcimsCompetitionAuditBo bo);

    /**
     * 新增竞赛审核
     */
    Boolean insertByBo(List<DcimsCompetitionAuditBo> boList);

    /**
     * 校验并批量删除竞赛审核信息
     */
    Boolean deleteWithValidByIds(List<DcimsCompetitionAuditBo> boList);

    /**
     * 修改竞赛部分信息
     */
    Boolean updateByBoPartial(CompetitionPartialBo bo);

    /**
     * 修改竞赛信息
     */
    Boolean updateByBoPartial(DcimsCompetitionBo bo);
}
