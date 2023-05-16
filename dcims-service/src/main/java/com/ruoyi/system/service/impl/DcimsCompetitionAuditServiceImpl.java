package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.bo.CompetitionPartialBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionAuditVo;
import com.ruoyi.system.domain.DcimsCompetitionAudit;
import com.ruoyi.system.mapper.DcimsCompetitionAuditMapper;
import com.ruoyi.system.service.IDcimsCompetitionAuditService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 竞赛审核Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@RequiredArgsConstructor
@Service
public class DcimsCompetitionAuditServiceImpl implements IDcimsCompetitionAuditService {

    private final DcimsCompetitionAuditMapper competitionAuditBaseMapper;
    private final DcimsCompetitionMapper competitionBaseMapper;
    private final SysDeptMapper sysDeptMapper;

    /**
     * 查询竞赛审核
     */
    @Override
    public DcimsCompetitionVo queryById(Long id){
        return competitionBaseMapper.selectVoById(id);
    }

    /**
     * 查询竞赛审核列表
     */
    @Override
    public TableDataInfo<DcimsCompetitionVo> queryPageList(DcimsCompetitionAuditBo bo, PageQuery pageQuery) {
        // 自定义业务，获取当前登录账号对应的教师工号
        String id = StpUtil.getLoginIdAsString();
        String teacherId = AccountUtils.getAccount(id).getTeacherId().toString();
        LambdaQueryWrapper<DcimsCompetition> lqw = new LambdaQueryWrapper<>();
        lqw.eq(teacherId != null&&teacherId != "", DcimsCompetition::getNextAuditId,teacherId);

        Page<DcimsCompetitionVo> result = competitionBaseMapper.selectVoPage(pageQuery.build(), lqw);
        System.out.println(result);
        return TableDataInfo.build(result);
    }

    /**
     * 查询竞赛审核列表
     */
    @Override
    public List<DcimsCompetitionAuditVo> queryList(DcimsCompetitionAuditBo bo) {
        LambdaQueryWrapper<DcimsCompetitionAudit> lqw = buildQueryWrapper(bo);
        return competitionAuditBaseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsCompetitionAudit> buildQueryWrapper(DcimsCompetitionAuditBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsCompetitionAudit> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getCompetitionId() != null, DcimsCompetitionAudit::getCompetitionId, bo.getCompetitionId());
        lqw.eq(bo.getTeacherId() != null, DcimsCompetitionAudit::getTeacherId, bo.getTeacherId());
        lqw.eq(bo.getResult() != null, DcimsCompetitionAudit::getResult, bo.getResult());
        lqw.eq(StringUtils.isNotBlank(bo.getReason()), DcimsCompetitionAudit::getReason, bo.getReason());
        lqw.eq(bo.getNextTeacherId() != null, DcimsCompetitionAudit::getNextTeacherId, bo.getNextTeacherId());
        return lqw;
    }

    /**
     * 通过竞赛审核
     */
    @Override
    public Boolean insertByBo(List<DcimsCompetitionAuditBo> boList) {
        List<DcimsCompetition> comList = new ArrayList<>();
        List<DcimsCompetitionAudit> comAuditList = new ArrayList<>();
        List<String> roleList = StpUtil.getRoleList();
        for (DcimsCompetitionAuditBo bo:boList) {
            //手动为Bo添加操作者等部分数据
            if(roleList.contains("AcademicAffairsOffice")){
                StpUtil.checkRole("AcademicAffairsOffice");
                bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
                bo.setResult(1L);
                DcimsCompetitionAudit add1 = BeanUtil.toBean(bo, DcimsCompetitionAudit.class);
                DcimsCompetition add2 = competitionBaseMapper.selectById(bo.getCompetitionId());
                if(add2.getNextAuditId().equals(add1.getTeacherId())){
                    comAuditList.add(add1);
                    add2.setState("1");
                    add2.setNextAuditId(-1L);
                    comList.add(add2);
                }
            } else {
                bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
                bo.setResult(1L);
                DcimsCompetitionAudit add1 = BeanUtil.toBean(bo, DcimsCompetitionAudit.class);
                DcimsCompetition add2 = competitionBaseMapper.selectById(bo.getCompetitionId());
                if(add2.getNextAuditId().equals(add1.getTeacherId())){
                    // 获取校级管理员的工号
                    LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
                    lqw.eq(SysDept::getParentId,0);
                    lqw.eq(SysDept::getDeptName,"浙江科技学院");
                    SysDept sysDept = sysDeptMapper.selectOne(lqw);
                    add1.setNextTeacherId(sysDept.getLeaderTeacherId());
                    comAuditList.add(add1);
                    add2.setNextAuditId(sysDept.getLeaderTeacherId());
                    comList.add(add2);
                }
            }
        }
        return (competitionBaseMapper.updateBatchById(comList))&&(competitionAuditBaseMapper.insertBatch(comAuditList));
    }

    /**
     * 退回竞赛审核
     */
    @Override
    public Boolean deleteWithValidByIds(List<DcimsCompetitionAuditBo> boList) {
        List<DcimsCompetition> comList = new ArrayList<>();
        List<DcimsCompetitionAudit> comAuditList = new ArrayList<>();
        for (DcimsCompetitionAuditBo bo:boList) {
            //手动为Bo添加操作者等部分数据
            bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
            bo.setResult(0L);
            DcimsCompetitionAudit add1 = BeanUtil.toBean(bo, DcimsCompetitionAudit.class);
            DcimsCompetition add2 = competitionBaseMapper.selectById(bo.getCompetitionId());
            if(add2 == null)   add2 = new DcimsCompetition();
            if(add1.getTeacherId().equals(add2.getNextAuditId())){
                comAuditList.add(add1);
                add2.setNextAuditId(bo.getNextTeacherId());
                comList.add(add2);
                add1.setNextTeacherId(add2.getResponsiblePersonId());
                add2.setNextAuditId(add2.getResponsiblePersonId());
            }
        }
        boolean flag;
        if (!comList.isEmpty()) {
            competitionBaseMapper.updateBatchById(comList);
            competitionAuditBaseMapper.insertBatch(comAuditList);
            flag = true;
        }else {
            flag = false;
        }
        return flag;
    }

    /**
     * 修改竞赛部分信息
     */
    public Boolean updateByBoPartial(CompetitionPartialBo bo){
        //对角色进行权限认证，权限不满足的角色不能更改如赛事类别、拨款等字段
        DcimsCompetition competition = competitionBaseMapper.selectById(bo.getId());
        if (!bo.getLevel().equals(competition.getLevel()) || !bo.getAppropriation().equals(competition.getAppropriation()) || !bo.getPersonLimit().equals(competition.getPersonLimit()) || !bo.getTeamLimit().equals(competition.getTeamLimit())){
            System.out.println(StpUtil.getRoleList());
            if(!StpUtil.hasRole("AcademicAffairsOffice")){
                return false;
            }
        }
        competition.setLevel(bo.getLevel());
        competition.setAppropriation(bo.getAppropriation());
        competition.setPersonLimit(bo.getPersonLimit());
        competition.setTeamLimit(bo.getTeamLimit());
        return competitionBaseMapper.updateById(competition) > 0;
    }
}
