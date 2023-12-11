package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsTeamAudit;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsTeamAuditMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.IDcimsBasicDataService;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.mapper.DcimsTeamMapper;
import com.ruoyi.system.service.IDcimsTeamService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 参赛团队Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
@RequiredArgsConstructor
@Service
public class DcimsTeamServiceImpl implements IDcimsTeamService {

    private final DcimsTeamMapper baseMapper;
    private final DcimsCompetitionMapper dcimsCompetitionMapper;
    private final DcimsTeamAuditMapper teamAuditBaseMapper;
    private final IDcimsBasicDataService basicDataService;
    private final SysDeptMapper sysDeptMapper;
    private final IDcimsCompetitionService competitionService;

    /**
     * 查询参赛团队
     */
    @Override
    public DcimsTeamVoV2 queryById(Long id){
        DcimsTeamVo vo = baseMapper.selectVoById(id);
        DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
        BeanUtils.copyProperties(vo, voV2);
        voV2.setStudentId(vo.getStudentId().split(","));
        voV2.setStudentName(vo.getStudentName().split(","));
        voV2.setTeacherId(vo.getTeacherId().split(","));
        voV2.setTeacherName(vo.getTeacherName().split(","));
        return voV2;
    }

    /**
     * 查询参赛团队列表
     */
    @Override
    public TableDataInfo<DcimsTeamVoV2> queryPageList(DcimsTeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        Page<DcimsTeamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<DcimsTeamVo> voList = TableDataInfo.build(result);
        Set<Long> competitionIds = new HashSet<>();
        voList.getRows().forEach(e -> {
            competitionIds.add(e.getCompetitionId());
        });
        List<DcimsCompetitionVo> competitionVoList = competitionService.listById(new ArrayList<>(competitionIds));
        List<DcimsTeamVoV2> VoV2List = voList.getRows().stream().map(e -> {
            DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
            BeanUtils.copyProperties(e, voV2);
            competitionVoList.forEach(c -> {
                if (c.getId().equals(voV2.getCompetitionId())){
                    voV2.setCompetition(c);
                }
            });
            return voV2;
        }).collect(Collectors.toList());
        return TableDataInfo.build(VoV2List);
    }

    /**
     * 根据教师工号查询参赛团队列表
     */
    @Override
    public TableDataInfo<DcimsTeamVoV2> queryPageListByTeacherId(DcimsTeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        // 获取自己负责的竞赛
        PageQuery query = new PageQuery();
        query.setPageSize(100);
        query.setPageNum(1);
        TableDataInfo<DcimsCompetitionVo> myCompetition = competitionService.queryPageListByTeacherId(new DcimsCompetitionBo(), query);
        List<Long> ids = new ArrayList<>();
        for (DcimsCompetitionVo com : myCompetition.getRows()){
            ids.add(com.getId());
        }
        // 添加查询登录人工号的限制条件
        lqw.like(AccountUtils.getAccount().getTeacherId() != null, DcimsTeam::getTeacherId, AccountUtils.getAccount().getTeacherId());
        lqw.or(AccountUtils.getAccount().getTeacherId() != null);
        lqw.in( ids.size() > 0, DcimsTeam::getCompetitionId, ids);
        Page<DcimsTeamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);

        // 查询审核状态信息
        List<Long> teamIds = new ArrayList<>();
        for(DcimsTeamVo vo : result.getRecords()){
            teamIds.add(vo.getId());
        }
        System.out.println(teamIds);
        LambdaQueryWrapper<DcimsTeamAudit> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(teamIds.size() > 0, DcimsTeamAudit::getTeamId, teamIds);
        List<DcimsTeamAuditVo> auditList = teamAuditBaseMapper.selectVoList(lqw2);
        Map<Long, DcimsTeamAuditVo> m = new HashMap<>();
        for (DcimsTeamAuditVo audit : auditList){
            DcimsTeamAuditVo audit1 = m.get(audit.getTeamId());
            if (audit1 != null){
                if (audit1.getId() < audit.getId()){
                    m.put(audit.getTeamId(), audit);
                }
            }else {
                m.put(audit.getTeamId(), audit);
            }
        }
        System.out.println(m.values());
        for (DcimsTeamAuditVo audit : m.values()){
            for (DcimsTeamVo vo : result.getRecords()){
                System.out.println(audit.getTeamId() + "   " + vo.getId());
                if (Objects.equals(audit.getTeamId(), vo.getId())){
                    vo.setAuditDetail(audit);
                    System.out.println(vo.getId() + " " + audit.getReason());
                }
            }
        }

        //添加竞赛名称
        Set<Long> competitionIds = new HashSet<>();
        result.getRecords().forEach(e -> {
            competitionIds.add(e.getCompetitionId());
        });
        List<DcimsCompetitionVo> competitionVoList = competitionService.listById(new ArrayList<>(competitionIds));
        List<DcimsTeamVoV2> VoV2List = result.getRecords().stream().map(e -> {
            DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
            BeanUtils.copyProperties(e, voV2);
            competitionVoList.forEach(c -> {
                if (c.getId().equals(voV2.getCompetitionId())){
                    voV2.setCompetition(c);
                }
            });
            return voV2;
        }).collect(Collectors.toList());

        return TableDataInfo.build(VoV2List);
    }

    /**
     * 查询参赛团队列表
     */
    @Override
    public List<DcimsTeamVo> queryList(DcimsTeamBo bo) {
        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsTeam> buildQueryWrapper(DcimsTeamBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsTeam> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getCompetitionId() != null, DcimsTeam::getCompetitionId, bo.getCompetitionId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DcimsTeam::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCompetitionType()), DcimsTeam::getCompetitionType, bo.getCompetitionType());
        lqw.eq(StringUtils.isNotBlank(bo.getAwardLevel()), DcimsTeam::getAwardLevel, bo.getAwardLevel());
        lqw.like(StringUtils.isNotBlank(bo.getTeacherId()), DcimsTeam::getTeacherId, bo.getTeacherId());
        lqw.like(StringUtils.isNotBlank(bo.getTeacherName()), DcimsTeam::getTeacherName, bo.getTeacherName());
        lqw.like(StringUtils.isNotBlank(bo.getStudentId()), DcimsTeam::getStudentId, bo.getStudentId());
        lqw.like(StringUtils.isNotBlank(bo.getStudentName()), DcimsTeam::getStudentName, bo.getStudentName());
        lqw.eq(bo.getAudit() != null, DcimsTeam::getAudit, bo.getAudit());
        return lqw;
    }

    /**
     * 新增参赛团队
     */
    @Override
    public Boolean insertByBo(DcimsTeamBo bo) {
        DcimsTeam add = BeanUtil.toBean(bo, DcimsTeam.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改参赛团队
     */
    @Override
    public Boolean updateByBo(DcimsTeamBo bo) {
        DcimsTeam update = BeanUtil.toBean(bo, DcimsTeam.class);
        validEntityBeforeSave(update);
        // 重置审核信息
        DcimsCompetition competition = dcimsCompetitionMapper.selectById(update.getCompetitionId());
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getParentId,100);
        lqw.eq(SysDept::getOrderNum,competition.getCollege());
        SysDept sysDept = sysDeptMapper.selectOne(lqw);
        // 判断状态，原来是未添加佐证材料的状态不变，否则变为待审核
        DcimsTeam entity = baseMapper.selectById(update.getId());
        if(entity.getAudit() == 0){
            update.setAudit(0);
        } else {
            update.setAudit(1);
        }
        update.setNextAuditId(sysDept.getLeaderTeacherId());
        // 填写教师、学生姓名
        String studentIds = bo.getStudentId();
        String[] splitStudentIds = studentIds.split(",");
        List<String> studentIdsString = new ArrayList<>(Arrays.asList(splitStudentIds));
        List<DcimsStudentVo> students = basicDataService.getStudentNameByIds(studentIdsString);
        String studentName = "";
        for(DcimsStudentVo student : students){
            studentName = studentName.concat(student.getName() + ',');
        }
        studentName = studentName.substring(0,studentName.length() - 1);
        update.setStudentName(studentName);

        String teacherIds = bo.getTeacherId();
        String[] splitTeacherIds = teacherIds.split(",");
        List<Long> teacherIdsLong = new ArrayList<>();
        for(String id : splitTeacherIds){
            teacherIdsLong.add(Long.parseLong(id));
        }
        List<DcimsTeacherVo> teachers = basicDataService.getTeacherNameByIds(teacherIdsLong);
        String teacherName = "";
        for(DcimsTeacherVo teacher : teachers){
            teacherName = teacherName.concat(teacher.getName() + ',');
        }
        teacherName = teacherName.substring(0,teacherName.length() - 1);
        update.setTeacherName(teacherName);

        return baseMapper.updateById(update) > 0;
    }

    /**
     * 为团队添加获奖信息
     */
    @Override
    public Boolean declareAwardByBo(DcimsDeclareAwardBo bo) {
        // 先查询对应竞赛，获取到id以外的其他数据
        DcimsTeam dcimsTeam = baseMapper.selectById(bo.getId());
        if (dcimsTeam == null) {
            return false;
        }
        // 判断是更新还是插入
        String flag;
        if (dcimsTeam.getSupportMaterial() == null || Objects.equals(dcimsTeam.getSupportMaterial().trim(), "")){
            // 更新
            flag = "update";
        }else {
            // 插入
            flag = "insert";
        }
        bo.setCompetitionId(dcimsTeam.getCompetitionId());
        bo.setName(dcimsTeam.getName());
        bo.setCompetitionType(dcimsTeam.getCompetitionType());
        bo.setTeacherId(dcimsTeam.getTeacherId());
        bo.setTeacherName(dcimsTeam.getTeacherName());
        bo.setStudentId(dcimsTeam.getStudentId());
        bo.setStudentName(dcimsTeam.getStudentName());
        bo.setCompetitionTime(dcimsTeam.getCompetitionTime());
        bo.setAudit(dcimsTeam.getAudit() + 1);
        // 执行更新操作
        if (Objects.equals(flag, "update")){
            DcimsTeam update = BeanUtil.toBean(bo, DcimsTeam.class);
            validEntityBeforeSave(update);
            return baseMapper.updateById(update) > 0;
        }
        // 执行插入操作
        DcimsTeam insert = BeanUtil.toBean(bo, DcimsTeam.class);
        insert.setAudit(1);
        validEntityBeforeSave(insert);
        // 设置父项目，同时清空主键
        insert.setAdvancedAwardNumber(bo.getId());
        insert.setId(null);
        return baseMapper.insert(insert) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsTeam entity){
        // 为团队对象添加审核信息
        DcimsCompetition competition = dcimsCompetitionMapper.selectById(entity.getCompetitionId());
        if(competition != null){
            LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SysDept::getParentId,100);
            lqw.eq(SysDept::getOrderNum,competition.getCollege());
            SysDept sysDept = sysDeptMapper.selectOne(lqw);
            if (sysDept != null){
                entity.setNextAuditId(sysDept.getLeaderTeacherId());
            }else{
                entity.setNextAuditId(-1L);
            }
        }else{
            entity.setNextAuditId(-1L);
        }
        // 只要是修改，那么就重置审核状态

    }

    /**
     * 批量删除参赛团队
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
