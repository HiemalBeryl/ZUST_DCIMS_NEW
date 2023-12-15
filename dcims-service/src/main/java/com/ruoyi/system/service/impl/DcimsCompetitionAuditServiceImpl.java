package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.bo.CompetitionPartialBo;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.vo.SysOssVo;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.service.ISysOssService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionAuditVo;
import com.ruoyi.system.domain.DcimsCompetitionAudit;
import com.ruoyi.system.mapper.DcimsCompetitionAuditMapper;
import com.ruoyi.system.service.IDcimsCompetitionAuditService;

import java.util.*;
import java.util.stream.Collectors;

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
    private final IDcimsCompetitionService competitionService;
    private final ISysOssService ossService;

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
        // 获取状态为待审核的竞赛
        lqw.eq(DcimsCompetition::getState,0);
        Page<DcimsCompetitionVo> result = competitionBaseMapper.selectVoPage(pageQuery.build(), lqw);

        // 获取团队对应oss信息
        Set<Long> OSSIds = new HashSet<>();
        result.getRecords().forEach(e -> {
            OSSIds.add(Long.valueOf(e.getAttachment()));
        });
        OSSIds.removeAll(Collections.singleton(null));
        System.out.println(OSSIds);
        List<SysOssVo> ossVoList = ossService.listByIds(OSSIds);
        List<DcimsCompetitionVo> voList = result.getRecords();
        if(ossVoList.size() > 0){
            voList = voList.stream().map(e -> {
                DcimsCompetitionVo vo = new DcimsCompetitionVo();
                BeanUtils.copyProperties(e, vo);
                ossVoList.forEach(oss -> {
                    if (oss.getOssId().equals(vo.getAttachment())){
                        vo.setOss(oss);
                    }
                });
                if (ObjectUtil.isNull(vo.getOss()))
                    vo.setOss(new SysOssVo());
                return vo;
            }).collect(Collectors.toList());
        }
        voList.forEach(e -> {
            if (ObjectUtil.isNull(e.getAttachment())){
                e.setOss(null);
            }
        });

        return TableDataInfo.build(voList);
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
                    lqw.eq(SysDept::getDeptName,"浙江科技学院教务处");
                    SysDept sysDept = sysDeptMapper.selectOne(lqw);
                    add1.setNextTeacherId(sysDept.getLeaderTeacherId());
                    comAuditList.add(add1);
                    add2.setNextAuditId(sysDept.getLeaderTeacherId());
                    comList.add(add2);
                }
            }
        }
        // 判断是否是最后一级审核，最后一级审核需要填写竞赛类别，否则不能通过
        if (StpUtil.hasRole("AcademicAffairsOffice")){
            for (DcimsCompetition com : comList){
                if (!Objects.equals("A", com.getLevel()) && !Objects.equals("B", com.getLevel()) && !Objects.equals("C", com.getLevel())){
                    return false;
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
                comList.add(add2);
                add1.setNextTeacherId(-1L);
                add2.setNextAuditId(-1L);
                add2.setState("2");
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
     * 修改竞赛部分信息 - 弃用
     */
    @Deprecated
    public Boolean updateByBoPartial(CompetitionPartialBo bo){
        //对角色进行权限认证，权限不满足的角色不能更改如赛事类别、拨款等字段
        DcimsCompetition competition = competitionBaseMapper.selectById(bo.getId());
        if(!StpUtil.hasRole("AcademicAffairsOffice")){
            bo.setLevel(competition.getLevel());
            bo.setAppropriation(competition.getAppropriation());
            bo.setPersonLimit(competition.getPersonLimit());
            bo.setTeamLimit(competition.getTeamLimit());
        }
        competition.setLevel(bo.getLevel());
        competition.setAppropriation(bo.getAppropriation());
        competition.setPersonLimit(bo.getPersonLimit());
        competition.setTeamLimit(bo.getTeamLimit());
        return competitionBaseMapper.updateById(competition) > 0;
    }


    /**
     * 修改竞赛信息
     */
    public Boolean updateByBoPartial(DcimsCompetitionBo bo) {
        // 判断用户角色，如果是负责人那么修改后重置审核状态。如果是学院或教务那么不重置
        List<String> roleList = StpUtil.getRoleList();
        Boolean flag = true;
        if (roleList.contains("AcademicAffairsOffice") || roleList.contains("AcademyCompetitionHead")){
            flag = false;
        }
        System.out.println(roleList);
        System.out.println(flag);
        return competitionService.updateByBo(bo, flag);
    }
}
