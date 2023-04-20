package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsCompetitionAudit;
import com.ruoyi.system.domain.vo.DcimsCompetitionAuditVo;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

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
    DcimsCompetitionAuditVo queryById(Long id);

    /**
     * 查询竞赛审核列表
     */
    TableDataInfo<DcimsCompetitionAuditVo> queryPageList(DcimsCompetitionAuditBo bo, PageQuery pageQuery);

    /**
     * 查询竞赛审核列表
     */
    List<DcimsCompetitionAuditVo> queryList(DcimsCompetitionAuditBo bo);

    /**
     * 新增竞赛审核
     */
    Boolean insertByBo(DcimsCompetitionAuditBo bo);

    /**
     * 修改竞赛审核
     */
    Boolean updateByBo(DcimsCompetitionAuditBo bo);

    /**
     * 校验并批量删除竞赛审核信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
