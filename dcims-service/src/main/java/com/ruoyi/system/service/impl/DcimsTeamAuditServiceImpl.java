package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.DcimsTeamAudit;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.vo.DcimsTeamAuditVo;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.mapper.DcimsTeamAuditMapper;
import com.ruoyi.system.mapper.DcimsTeamMapper;
import com.ruoyi.system.service.IDcimsTeamAuditService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 团队获奖审核Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@RequiredArgsConstructor
@Service
public class DcimsTeamAuditServiceImpl implements IDcimsTeamAuditService {

    private final DcimsTeamAuditMapper teamAuditBaseMapper;
    private final DcimsTeamMapper teamBaseMapper;

    /**
     * 查询团队获奖审核
     */
    @Override
    public DcimsTeamVo queryById(Long id){
        return teamBaseMapper.selectVoById(id);
    }

    /**
     * 查询团队获奖审核列表
     */
    @Override
    public TableDataInfo<DcimsTeamVo> queryPageList(DcimsTeamAuditBo bo, PageQuery pageQuery) {
        // 自定义业务，获取当前登录账号对应的教师工号
        String id = StpUtil.getLoginIdAsString();
        String teacherId = AccountUtils.getAccount(id).getTeacherId().toString();
        LambdaQueryWrapper<DcimsTeam> lqw = new LambdaQueryWrapper<>();
        lqw.eq(teacherId != null&&teacherId != "", DcimsTeam::getNextAuditId,teacherId);

        Page<DcimsTeamVo> result = teamBaseMapper.selectVoPage(pageQuery.build(), lqw);
        System.out.println(result);
        return TableDataInfo.build(result);
    }

    /**
     * 查询团队获奖审核列表
     */
    @Override
    public List<DcimsTeamAuditVo> queryList(DcimsTeamAuditBo bo) {
        LambdaQueryWrapper<DcimsTeamAudit> lqw = buildQueryWrapper(bo);
        return teamAuditBaseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsTeamAudit> buildQueryWrapper(DcimsTeamAuditBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsTeamAudit> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getTeamId() != null, DcimsTeamAudit::getTeamId, bo.getTeamId());
        lqw.eq(bo.getTeacherId() != null, DcimsTeamAudit::getTeacherId, bo.getTeacherId());
        lqw.eq(bo.getResult() != null, DcimsTeamAudit::getResult, bo.getResult());
        lqw.eq(StringUtils.isNotBlank(bo.getReason()), DcimsTeamAudit::getReason, bo.getReason());
        lqw.eq(bo.getNextTeacherId() != null, DcimsTeamAudit::getNextTeacherId, bo.getNextTeacherId());
        return lqw;
    }

    /**
     * 通过获奖审核
     */
    @Override
    public Boolean insertByBo(List<DcimsTeamAuditBo> boList) {
        List<DcimsTeam> teamList = new ArrayList<>();
        List<DcimsTeamAudit> teamAuditList = new ArrayList<>();
        for (DcimsTeamAuditBo bo:boList) {
            //手动为Bo添加操作者等部分数据
            if(bo.getNextTeacherId().equals(0L)){
                StpUtil.checkRole("AcademicAffairsOffice");
                bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
                bo.setResult(1L);
                DcimsTeamAudit add1 = BeanUtil.toBean(bo, DcimsTeamAudit.class);
                DcimsTeam add2 = teamBaseMapper.selectById(bo.getTeamId());
                if(add2.getNextAuditId().equals(add1.getTeacherId())){
                    teamAuditList.add(add1);
                    teamList.add(add2);
                }
            } else {
                bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
                bo.setResult(1L);
                DcimsTeamAudit add1 = BeanUtil.toBean(bo, DcimsTeamAudit.class);
                DcimsTeam add2 = teamBaseMapper.selectById(bo.getTeamId());
                if(add2.getNextAuditId().equals(add1.getTeacherId())){
                    teamAuditList.add(add1);
                    add2.setNextAuditId(bo.getNextTeacherId());
                    teamList.add(add2);
                }
            }
        }
        return (teamBaseMapper.updateBatchById(teamList))&&(teamAuditBaseMapper.insertBatch(teamAuditList));
    }

    /**
     * 批量删除团队获奖审核
     */
    @Override
    public Boolean deleteWithValidByIds(List<DcimsTeamAuditBo> boList) {
        List<DcimsTeam> teamList = new ArrayList<>();
        List<DcimsTeamAudit> comAuditList = new ArrayList<>();
        for (DcimsTeamAuditBo bo:boList) {
            //手动为Bo添加操作者等部分数据
            bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
            bo.setResult(0L);
            DcimsTeamAudit add1 = BeanUtil.toBean(bo, DcimsTeamAudit.class);
            DcimsTeam add2 = teamBaseMapper.selectById(bo.getTeamId());
            if(add2 == null)   add2 = new DcimsTeam();
            if(add1.getTeacherId().equals(add2.getNextAuditId())){
                comAuditList.add(add1);
                add2.setNextAuditId(bo.getNextTeacherId());
                teamList.add(add2);
                add1.setNextTeacherId(Long.parseLong(add2.getTeacherId().split(",")[0]));
                add2.setNextAuditId(Long.parseLong(add2.getTeacherId().split(",")[0]));
            }
        }
        boolean flag;
        if (!teamList.isEmpty()) {
            teamBaseMapper.updateBatchById(teamList);
            teamAuditBaseMapper.insertBatch(comAuditList);
            flag = true;
        }else {
            flag = false;
        }
        return flag;
    }
}
