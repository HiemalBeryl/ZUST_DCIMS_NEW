package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.vo.AllocationCompetitionDictVo;
import com.ruoyi.system.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationBo;
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationVo;
import com.ruoyi.system.service.IDcimsWorktimeAllocationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 工作量分配Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@RequiredArgsConstructor
@Service
public class DcimsWorktimeAllocationServiceImpl implements IDcimsWorktimeAllocationService {

    private final DcimsWorktimeAllocationMapper baseMapper;
    private final DcimsWorktimeAllocationTeacherMapper worktimeTeacherMapper;
    private final DcimsWorktimeAllocationCompetitionMapper worktimeCompetitionMapper;
    private final DcimsTeamMapper teamMapper;
    private final DcimsCompetitionMapper competitionMapper;

    /**
     * 查询工作量分配
     */
    @Override
    public DcimsWorktimeAllocationVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询工作量分配列表
     */
    @Override
    public TableDataInfo<DcimsWorktimeAllocationVo> queryPageList(DcimsWorktimeAllocationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsWorktimeAllocation> lqw = buildQueryWrapper(bo);
        Page<DcimsWorktimeAllocationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询工作量分配列表
     */
    @Override
    public List<DcimsWorktimeAllocationVo> queryList(DcimsWorktimeAllocationBo bo) {
        LambdaQueryWrapper<DcimsWorktimeAllocation> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsWorktimeAllocation> buildQueryWrapper(DcimsWorktimeAllocationBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsWorktimeAllocation> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getYear()), DcimsWorktimeAllocation::getYear, bo.getYear());
        lqw.eq(bo.getStartTime() != null, DcimsWorktimeAllocation::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, DcimsWorktimeAllocation::getEndTime, bo.getEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getFomular()), DcimsWorktimeAllocation::getFomular, bo.getFomular());
        lqw.eq(StringUtils.isNotBlank(bo.getParameter()), DcimsWorktimeAllocation::getParameter, bo.getParameter());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DcimsWorktimeAllocation::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增工作量分配
     */
    @Override
    public Boolean insertByBo(DcimsWorktimeAllocationBo bo) {
        DcimsWorktimeAllocation add = BeanUtil.toBean(bo, DcimsWorktimeAllocation.class);
        boolean flag1 = baseMapper.insert(add) > 0;
        if (flag1) {
            bo.setId(add.getId());
        }

        Date startTime = bo.getStartTime();
        Date endTime = bo.getEndTime();
        // 查询限定时间内的所有通过审核的获奖团队
        LambdaQueryWrapper<DcimsTeam> teamLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teamLambdaQueryWrapper.between(DcimsTeam::getAwardTime,startTime,endTime);
        teamLambdaQueryWrapper.eq(DcimsTeam::getAudit,2);
        List<DcimsTeam> teamList = teamMapper.selectList(teamLambdaQueryWrapper);
        // 查询时间段内所有立项通过的竞赛
        LambdaQueryWrapper<DcimsCompetition> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(DcimsCompetition::getState,1);
        List<DcimsCompetition> competitionList = competitionMapper.selectList();

        // 工作量分配教师表
        List<DcimsWorktimeAllocationTeacher> allocationTeacherList = new ArrayList<>();
        // 工作量分配竞赛表，key为竞赛主键，value为实体类
        Map<Long,DcimsWorktimeAllocationCompetition> allocationCompetitionMap = new HashMap<>();
        for (DcimsTeam team:teamList) {
            DcimsWorktimeAllocationCompetition competitionEntity;
            if(allocationCompetitionMap.containsKey(team.getCompetitionId()))   competitionEntity = allocationCompetitionMap.get(team.getCompetitionId());
            else competitionEntity = new DcimsWorktimeAllocationCompetition();
            BigDecimal workTime = new BigDecimal(0);
            for (DcimsCompetition competition:competitionList) {
                if (competition.getId().equals(team.getCompetitionId())) {
                    competitionEntity.setCompetitionId(competition.getId());
                    workTime = workTime.add(getWorktimeByAwardLevel(team.getAwardLevel(),competition.getLevel(),team.getCompetitionType()));
                    System.out.println("teamname:"+ team.getName()+"  worktime:"+ workTime +" awardlevel:"+team.getAwardLevel()+" type:"+competition.getLevel()+" competition:"+team.getCompetitionType());
                }
            }

            // 设置工作量分配竞赛实体
            competitionEntity.setWorktimeId(add.getId());
            if (competitionEntity.getTotal() == null){
                competitionEntity.setTotal(workTime);
            } else {
                competitionEntity.setTotal(workTime.add(competitionEntity.getTotal()));
            }
            competitionEntity.setRemain(new BigDecimal("0"));
            allocationCompetitionMap.put(competitionEntity.getCompetitionId(),competitionEntity);

            // 设置工作量分配教师实体
            // 团队的指导教师工号数组
            String[] teacherIds = team.getTeacherId().split(",");
            System.out.println(teacherIds.length);
            BigDecimal workTimePersonal;
            BigDecimal workTimePersonalLeft;
            if (teacherIds.length <= 1){
                workTimePersonal = workTime;
                workTimePersonalLeft = workTimePersonal;
            } else {
                workTimePersonal = workTime.divide(BigDecimal.valueOf(teacherIds.length),3,RoundingMode.DOWN);
                workTimePersonalLeft = workTime.subtract(workTimePersonal.multiply(BigDecimal.valueOf(teacherIds.length - 1)));
            }
            System.out.println("teamname:"+ team.getName() +"   personal:"+workTimePersonal+"   left:"+workTimePersonalLeft);
            for (String id:teacherIds) {
                DcimsWorktimeAllocationTeacher teacherEntity = new DcimsWorktimeAllocationTeacher();
                teacherEntity.setWorktimeCompetitionId(competitionEntity.getCompetitionId());
                teacherEntity.setTeacherId(Long.parseLong(id));
                if (id.equals(teacherIds[teacherIds.length - 1])){
                    teacherEntity.setWorktime(workTimePersonalLeft);
                } else {
                    teacherEntity.setWorktime(workTimePersonal);
                }
                allocationTeacherList.add(teacherEntity);
            }
        }
        Boolean flag2 = worktimeCompetitionMapper.insertBatch(allocationCompetitionMap.values());
        for (DcimsWorktimeAllocationTeacher allocationTeacher:allocationTeacherList) {
            allocationTeacher.setWorktimeCompetitionId(allocationCompetitionMap.get(allocationTeacher.getWorktimeCompetitionId()).getId());
        }
        Boolean flag3 = worktimeTeacherMapper.insertBatch(allocationTeacherList);
        return flag1 && flag2 && flag3;
    }

    /**
     * 修改工作量分配
     */
    @Override
    public Boolean updateByBo(DcimsWorktimeAllocationBo bo) {
        DcimsWorktimeAllocation update = BeanUtil.toBean(bo, DcimsWorktimeAllocation.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsWorktimeAllocation entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除工作量分配
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 获取获奖等级和竞赛类型对应的奖金额度
     */
    public BigDecimal getWorktimeByAwardLevel(String awardLevel, String type, String isTeam){
        Map<String,BigDecimal> aMap = new HashMap<>();
        Map<String,BigDecimal> bMap = new HashMap<>();
        Map<String,BigDecimal> cMap = new HashMap<>();
        aMap.put("0",new BigDecimal(12));
        aMap.put("1",new BigDecimal(10));
        aMap.put("2",new BigDecimal(8));
        aMap.put("3",new BigDecimal(7));
        aMap.put("4",new BigDecimal(6));
        aMap.put("5",new BigDecimal(10));
        aMap.put("6",new BigDecimal(8));
        aMap.put("7",new BigDecimal(7));
        aMap.put("8",new BigDecimal(6));
        aMap.put("9",new BigDecimal(5));
        aMap.put("15",new BigDecimal(6));
        aMap.put("16",new BigDecimal(5));
        aMap.put("17",new BigDecimal(4));
        aMap.put("18",new BigDecimal(3));
        bMap.put("0",new BigDecimal(5));
        bMap.put("1",new BigDecimal("4.5"));
        bMap.put("2",new BigDecimal(4));
        bMap.put("3",new BigDecimal("3.5"));
        bMap.put("4",new BigDecimal("2.5"));
        bMap.put("5",new BigDecimal("4.5"));
        bMap.put("6",new BigDecimal(4));
        bMap.put("7",new BigDecimal("3.5"));
        bMap.put("8",new BigDecimal("2.5"));
        bMap.put("9",new BigDecimal("1.5"));
        bMap.put("15",new BigDecimal("2.5"));
        bMap.put("16",new BigDecimal("1.5"));
        bMap.put("17",new BigDecimal(1));
        bMap.put("18",new BigDecimal("0.8"));
        cMap.put("0",new BigDecimal("4.5"));
        cMap.put("1",new BigDecimal(4));
        cMap.put("2",new BigDecimal("3.5"));
        cMap.put("3",new BigDecimal("2.5"));
        cMap.put("4",new BigDecimal("1.5"));
        cMap.put("5",new BigDecimal(4));
        cMap.put("6",new BigDecimal("3.5"));
        cMap.put("7",new BigDecimal("2.5"));
        cMap.put("8",new BigDecimal("1.5"));
        cMap.put("9",new BigDecimal(1));
        cMap.put("15",new BigDecimal("1.5"));
        cMap.put("16",new BigDecimal(1));
        cMap.put("17",new BigDecimal("0.8"));
        cMap.put("18",new BigDecimal("0.5"));

        BigDecimal workTime = null;
        switch (type) {
            case "A" :
                workTime = aMap.get(awardLevel);
                break;
            case "B" :
                workTime = bMap.get(awardLevel);
                break;
            case "C" :
                workTime = cMap.get(awardLevel);
                break;
        }
        if (workTime != null && isTeam.equals("50"))   workTime = workTime.divide(new BigDecimal(3), RoundingMode.DOWN);
        return workTime == null ? new BigDecimal(0):workTime;
    }
}
