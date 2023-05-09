package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.DcimsBonusAllocation;
import com.ruoyi.system.domain.DcimsBonusAllocationPersonal;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationBo;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationPersonalBo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationVo;
import com.ruoyi.system.mapper.DcimsBonusAllocationMapper;
import com.ruoyi.system.mapper.DcimsBonusAllocationPersonalMapper;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsTeamMapper;
import com.ruoyi.system.service.IDcimsBonusAllocationService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 奖金分配总Service业务层处理
 *
 * @author Andy
 * @date 2023-05-02
 */
@RequiredArgsConstructor
@Service
public class DcimsBonusAllocationServiceImpl implements IDcimsBonusAllocationService {

    private final DcimsBonusAllocationMapper bonusAllocationMapper;
    private final DcimsBonusAllocationPersonalMapper bonusAllocationPersonalMapper;
    private final DcimsTeamMapper teamMapper;
    private final DcimsCompetitionMapper competitionMapper;

    /**
     * 查询奖金分配总
     */
    @Override
    public DcimsBonusAllocationVo queryById(Long id){
        return bonusAllocationMapper.selectVoById(id);
    }

    /**
     * 查询奖金分配总列表
     */
    @Override
    public TableDataInfo<DcimsBonusAllocationVo> queryPageList(DcimsBonusAllocationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<com.ruoyi.system.domain.DcimsBonusAllocation> lqw = buildQueryWrapper(bo);
        Page<DcimsBonusAllocationVo> result = bonusAllocationMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询奖金分配总列表
     */
    @Override
    public List<DcimsBonusAllocationVo> queryList(DcimsBonusAllocationBo bo) {
        LambdaQueryWrapper<com.ruoyi.system.domain.DcimsBonusAllocation> lqw = buildQueryWrapper(bo);
        return bonusAllocationMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<com.ruoyi.system.domain.DcimsBonusAllocation> buildQueryWrapper(DcimsBonusAllocationBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<com.ruoyi.system.domain.DcimsBonusAllocation> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getYears() != null, com.ruoyi.system.domain.DcimsBonusAllocation::getYears, bo.getYears());
        lqw.eq(bo.getCollege() != null, com.ruoyi.system.domain.DcimsBonusAllocation::getCollege, bo.getCollege());
        return lqw;
    }

    /**
     * 新增奖金分配总
     */
    @Override
    public Boolean insertByBo(DcimsBonusAllocationBo bo) {
        com.ruoyi.system.domain.DcimsBonusAllocation add = BeanUtil.toBean(bo, com.ruoyi.system.domain.DcimsBonusAllocation.class);
        validEntityBeforeSave(add);
        boolean flag = bonusAllocationMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改奖金分配总
     */
    @Override
    public Boolean updateByBo(DcimsBonusAllocationBo bo) {
        com.ruoyi.system.domain.DcimsBonusAllocation update = BeanUtil.toBean(bo, com.ruoyi.system.domain.DcimsBonusAllocation.class);
        validEntityBeforeSave(update);
        return bonusAllocationMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(com.ruoyi.system.domain.DcimsBonusAllocation entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除奖金分配总
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return bonusAllocationMapper.deleteBatchIds(ids) > 0;
    }



    /**
    * 获取单条数据，用于获取总金额，可分配余额，以及日期
    */
    @Override
    public DcimsBonusAllocationVo getTotalAmount() {
        Long teacherId = AccountUtils.getAccount().getTeacherId();
        LambdaQueryWrapper<com.ruoyi.system.domain.DcimsBonusAllocation> lqw = new LambdaQueryWrapper<>();
        lqw.eq(com.ruoyi.system.domain.DcimsBonusAllocation::getYears,2022);
        lqw.eq(com.ruoyi.system.domain.DcimsBonusAllocation::getTeacherInCharge,teacherId);
        return bonusAllocationMapper.selectVoOne(lqw);
    }

    /**
     * 生成并查询某一段时间的竞赛奖金数据
     */
    @Override
    public List<Object> generateBonusDataByTime(Date startTime, Date endTime) {
        // 查询限定时间内的所有通过审核的获奖团队
        LambdaQueryWrapper<DcimsTeam>teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teamLambdaQueryWrapper.between(DcimsTeam::getAwardTime,startTime,endTime);
        teamLambdaQueryWrapper.eq(DcimsTeam::getAudit,2);
        List<DcimsTeam> teamList = teamMapper.selectList(teamLambdaQueryWrapper);
        // 根据团队查询所有含有获奖信息的竞赛
        LambdaQueryWrapper<DcimsCompetition> competitionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Set<Long> idSet = new HashSet<>();
        for (DcimsTeam team:teamList) {
            idSet.add(team.getCompetitionId());
        }
        competitionLambdaQueryWrapper.eq(DcimsCompetition::getState,1);
        competitionLambdaQueryWrapper.in(DcimsCompetition::getId,idSet);
        List<DcimsCompetition> competitionList = competitionMapper.selectList();
        // 创建奖金分配个人和奖金分配总列表对象
        // 校级奖金明细，key为学院数据字典中的id，value为数据对象
        Map<Long, DcimsBonusAllocation> bonusMap = new HashMap<>();
        // 学院级奖金明细，key为竞赛id，value为数据对象
        Map<Long, DcimsBonusAllocationPersonal> bonusPersonalMap = new HashMap<>();
        Long teacherInCharge = AccountUtils.getAccount().getTeacherId();
        for (DcimsTeam team:teamList) {
            DcimsBonusAllocationPersonal personalBo;
            if(bonusPersonalMap.containsKey(team.getCompetitionId()))   personalBo = bonusPersonalMap.get(team.getCompetitionId());
            else personalBo = new DcimsBonusAllocationPersonal();
            DcimsBonusAllocation allBo;
            Long collegeId = -1L;
            for (DcimsCompetition competition:competitionList) {
                if(competition.getId().equals(team.getCompetitionId())){
                    collegeId = competition.getCollege();
                }
            }
            if(bonusMap.containsKey(collegeId))   allBo = bonusMap.get(collegeId);
            else allBo = new DcimsBonusAllocation();

            // 计算该名队伍对应的应得奖金数额
            Long bonus = 100L;
            // 设置奖金分配个人
            personalBo.setCompetition(team.getCompetitionId());
            if (personalBo.getBonus() == null){
                personalBo.setBonus(bonus);
            }else{
                personalBo.setBonus(personalBo.getBonus() + bonus);
            }
            personalBo.setAllocateTime(new Date());
            competitionList.forEach(competition->{
                if(competition.getId().equals(team.getCompetitionId())){
                    personalBo.setGainer(competition.getResponsiblePersonId());
                }
            });
            personalBo.setYears(2023);
            personalBo.setTeacherInCharge(teacherInCharge);

            // 设置奖金分配总
            allBo.setYears(2023);
            allBo.setCollege(Math.toIntExact(collegeId));
            if (allBo.getTotalAmount() == null){
                allBo.setTotalAmount(bonus);
            }else{
                allBo.setTotalAmount(allBo.getTotalAmount() + bonus);
            }
            allBo.setRetentionRatio(BigDecimal.valueOf(1.00));
            allBo.setDistributable(allBo.getTotalAmount());
            allBo.setAllocated(0L);
            allBo.setUnallocated(allBo.getTotalAmount());
            allBo.setStartTime(startTime);
            allBo.setEndTime(endTime);

            if(bonusMap.putIfAbsent(collegeId,allBo) != null)
                bonusMap.replace(collegeId,allBo);
            if(bonusPersonalMap.putIfAbsent(team.getCompetitionId(),personalBo) != null)
                bonusPersonalMap.replace(team.getCompetitionId(),personalBo);
        }
        //boolean flag = bonusAllocationMapper.insertOrUpdateBatch(bonusMap.values()) && bonusAllocationPersonalMapper.insertOrUpdateBatch(bonusPersonalMap.values());;
        //LambdaQueryWrapper<DcimsBonusAllocation> lqw = new LambdaQueryWrapper<>();
        //lqw.eq(DcimsBonusAllocation::getYears,2023);
        //return bonusAllocationMapper.selectVoList(lqw);
        List<Object> list = new ArrayList<>();
        list.add(bonusMap.values());
        list.add(bonusPersonalMap.values());
        return list;
    }

    /**
     * 保存竞赛奖金数据
     */
    public Boolean insertYearsBonusData(List<DcimsBonusAllocationBo> allBo, List<DcimsBonusAllocationPersonalBo> personalBo){
        List<DcimsBonusAllocation> alls = new ArrayList<>();
        List<DcimsBonusAllocationPersonal> personals = new ArrayList<>();
        for (Object object:allBo) {
            // 将list中的数据转成json字符串
            String jsonObject= JSON.toJSONString(object);
            //将json转成需要的对象
            DcimsBonusAllocationBo element = JSONObject.parseObject(jsonObject,DcimsBonusAllocationBo.class);
            DcimsBonusAllocation all = BeanUtil.toBean(element, DcimsBonusAllocation.class);
            alls.add(all);
        }
        for(Object object:personalBo){
            // 将list中的数据转成json字符串
            String jsonObject= JSON.toJSONString(object);
            //将json转成需要的对象
            DcimsBonusAllocationPersonalBo element = JSONObject.parseObject(jsonObject,DcimsBonusAllocationPersonalBo.class);
            DcimsBonusAllocationPersonal personal = BeanUtil.toBean(element, DcimsBonusAllocationPersonal.class);
            personals.add(personal);
        }
        System.out.println(alls);
        System.out.println(personals);
        boolean flag = bonusAllocationPersonalMapper.insertBatch(personals)&& bonusAllocationMapper.insertBatch(alls);
        return flag;
    }
}
