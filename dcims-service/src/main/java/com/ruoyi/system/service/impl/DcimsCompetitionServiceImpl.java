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
import com.ruoyi.system.domain.DcimsCompetitionTeacher;
import com.ruoyi.system.domain.DcimsTeacher;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import com.ruoyi.system.mapper.DcimsCompetitionTeacherMapper;
import com.ruoyi.system.mapper.DcimsTeacherMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.service.IDcimsCompetitionService;

import java.util.*;

/**
 * 竞赛赛事基本信息Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */
@RequiredArgsConstructor
@Service
public class DcimsCompetitionServiceImpl implements IDcimsCompetitionService {

    private final DcimsCompetitionMapper baseMapper;
    private final DcimsTeacherMapper teacherMapper;
    private final DcimsCompetitionTeacherMapper competitionTeacherMapper;
    private final SysDeptMapper sysDeptMapper;

    /**
     * 查询竞赛赛事基本信息
     */
    @Override
    public DcimsCompetitionVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询竞赛赛事基本信息列表
     */
    @Override
    public TableDataInfo<DcimsCompetitionVo> queryPageList(DcimsCompetitionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsCompetition> lqw = buildQueryWrapper(bo);
        Page<DcimsCompetitionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 根据登录用户对应教师工号，查询竞赛赛事基本信息列表
     */
    @Override
    public TableDataInfo<DcimsCompetitionVo> queryPageListByTeacherId(DcimsCompetitionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsCompetition> lqw = buildQueryWrapper(bo);
        lqw.eq(DcimsCompetition::getResponsiblePersonId, AccountUtils.getAccount().getTeacherId());
        Page<DcimsCompetitionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询竞赛赛事基本信息列表
     */
    @Override
    public List<DcimsCompetitionVo> queryList(DcimsCompetitionBo bo) {
        LambdaQueryWrapper<DcimsCompetition> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsCompetition> buildQueryWrapper(DcimsCompetitionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsCompetition> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), DcimsCompetition::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getLevel()), DcimsCompetition::getLevel, bo.getLevel());
        lqw.like(StringUtils.isNotBlank(bo.getPastName()), DcimsCompetition::getPastName, bo.getPastName());
        lqw.eq(bo.getTerm() != null, DcimsCompetition::getTerm, bo.getTerm());
        lqw.eq(bo.getAnnual() != null, DcimsCompetition::getAnnual, bo.getAnnual());
        lqw.eq(StringUtils.isNotBlank(bo.getOrganizer()), DcimsCompetition::getOrganizer, bo.getOrganizer());
        lqw.like(StringUtils.isNotBlank(bo.getResponsiblePersonName()), DcimsCompetition::getResponsiblePersonName, bo.getResponsiblePersonName());
        lqw.like(bo.getCollege() != null, DcimsCompetition::getCollege, bo.getCollege());
        lqw.between(params.get("beginInnerTime") != null && params.get("endInnerTime") != null,
            DcimsCompetition::getInnerTime ,params.get("beginInnerTime"), params.get("endInnerTime"));
        lqw.between(params.get("beginProvinceTime") != null && params.get("endProvinceTime") != null,
            DcimsCompetition::getProvinceTime ,params.get("beginProvinceTime"), params.get("endProvinceTime"));
        lqw.between(params.get("beginNationalTime") != null && params.get("endNationalTime") != null,
            DcimsCompetition::getNationalTime ,params.get("beginNationalTime"), params.get("endNationalTime"));
        return lqw;
    }

    /**
     * 新增竞赛赛事基本信息
     */
    @Override
    public Boolean insertByBo(DcimsCompetitionBo bo) {
        DcimsCompetition add = BeanUtil.toBean(bo, DcimsCompetition.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改竞赛赛事基本信息
     */
    @Override
    public Boolean updateByBo(DcimsCompetitionBo bo, Boolean resetAuditStatus) {
        //对角色进行权限认证，权限不满足的角色不能更改如赛事类别、拨款等字段
        DcimsCompetition competition = baseMapper.selectById(bo.getId());
        if (!Objects.equals(bo.getLevel(), competition.getLevel())||
            !Objects.equals(bo.getAppropriation(), competition.getAppropriation())||
            !Objects.equals(bo.getPersonLimit(), competition.getPersonLimit())||
            !Objects.equals(bo.getTeamLimit(), competition.getTeamLimit()))
        {
            System.out.println(StpUtil.getRoleList());
            if(!StpUtil.hasRole("AcademicAffairsOffice")){
                return false;
            }
        }
        DcimsCompetition update = BeanUtil.toBean(bo, DcimsCompetition.class);
        //判断是否修改了审核人，如果有修改，则重置审核状态
        if (!bo.getNextAuditId().equals(competition.getNextAuditId())){
            update.setState("0");
        }
        //是否需要重置审核状态
        if (resetAuditStatus)
            validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsCompetition entity){
        // 为竞赛对象添加审核信息
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getParentId,100);
        lqw.eq(SysDept::getOrderNum,entity.getCollege());
        SysDept sysDept = sysDeptMapper.selectOne(lqw);
        if (sysDept != null){
            entity.setNextAuditId(sysDept.getLeaderTeacherId());
            entity.setState("0");
        }else{
            entity.setNextAuditId(-1L);
        }
    }

    /**
     * 批量删除竞赛赛事基本信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 获取竞赛对应指导教师
     */
    public TableDataInfo<DcimsTeacherVo> getTutorList(Long competitionId){
        LambdaQueryWrapper<DcimsCompetitionTeacher> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(DcimsCompetitionTeacher::getCompetitionId,competitionId);
        lqw1.eq(DcimsCompetitionTeacher::getDeleted,"0");
        List<DcimsCompetitionTeacher> tutorIdList = competitionTeacherMapper.selectList(lqw1);

        LambdaQueryWrapper<DcimsTeacher> lqw2 = new LambdaQueryWrapper<>();
        List<Long> teacherIds = new ArrayList<>();
        teacherIds.add(-1L);
        tutorIdList.forEach(element -> teacherIds.add(element.getTeacherId()));
        lqw2.in(DcimsTeacher::getTeacherId,teacherIds);
        List<DcimsTeacherVo> dcimsTeacherVoList = teacherMapper.selectVoList(lqw2);
        dcimsTeacherVoList.forEach(element -> tutorIdList.forEach(tutor -> {
            if(tutor.getTeacherId().equals(element.getTeacherId()))
                element.setCompetitionTeacherId(tutor.getId());
        }));

        return TableDataInfo.build(dcimsTeacherVoList);
    }

    /**
     * 添加多个指导教师
     */
    public Boolean addTutor(Long competitionId, Long[] teacherIds){
        LambdaQueryWrapper<DcimsCompetitionTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsCompetitionTeacher::getCompetitionId,competitionId);
        lqw.in(DcimsCompetitionTeacher::getTeacherId,teacherIds);
        lqw.eq(DcimsCompetitionTeacher::getDeleted,"0");
        if(competitionTeacherMapper.selectList(lqw).size() > 0){
            return false;
        }

        for (Long teacherId:teacherIds) {
            DcimsCompetitionTeacher entity = new DcimsCompetitionTeacher();
            entity.setCompetitionId(competitionId);
            entity.setTeacherId(teacherId);
            competitionTeacherMapper.insert(entity);
        }
        return true;
    }

    /**
     * 根据id删除指导教师
     */
    public Boolean removeTutor(Long id){
        return competitionTeacherMapper.deleteById(id) > 0;
    }
}
