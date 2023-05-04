package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.mapper.DcimsTeamMapper;
import com.ruoyi.system.service.IDcimsTeamService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

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

    /**
     * 查询参赛团队
     */
    @Override
    public DcimsTeamVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询参赛团队列表
     */
    @Override
    public TableDataInfo<DcimsTeamVo> queryPageList(DcimsTeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        Page<DcimsTeamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 根据教师工号查询参赛团队列表
     */
    @Override
    public TableDataInfo<DcimsTeamVo> queryPageListByTeacherId(DcimsTeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        // 添加查询登录人工号的限制条件
        lqw.like(AccountUtils.getAccount().getTeacherId() != null, DcimsTeam::getTeacherId, AccountUtils.getAccount().getTeacherId());
        Page<DcimsTeamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
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
        DcimsTeam update = BeanUtil.toBean(bo, DcimsTeam.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsTeam entity){
        //TODO 做一些数据校验,如唯一约束
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