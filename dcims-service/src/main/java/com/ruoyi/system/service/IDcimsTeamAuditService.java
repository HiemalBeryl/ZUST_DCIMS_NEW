package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.DcimsTeamWithCompetition;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.domain.vo.DcimsTeamAuditVo;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.vo.DcimsTeamVoV2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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
    TableDataInfo<DcimsTeamVoV2> queryPageListAudit(DcimsTeamAuditBo bo, PageQuery pageQuery);

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

    /**
     * 对已经归档的获奖信息进行删除
     */
    Boolean deleteOneById(Long id);

    /*
     * 查询省级以上获奖信息，附带竞赛信息返回
     */
    List<DcimsTeamWithCompetition> queryListWithCompetition(DcimsTeamBo bo);

    OutputStream getHuoJiangBiao(List<DcimsTeam> data) throws IOException;
}
