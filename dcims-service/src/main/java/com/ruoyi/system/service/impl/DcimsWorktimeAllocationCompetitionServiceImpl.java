package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationCompetitionBo;
import com.ruoyi.system.domain.DcimsWorktimeAllocationCompetition;
import com.ruoyi.system.mapper.DcimsWorktimeAllocationCompetitionMapper;
import com.ruoyi.system.service.IDcimsWorktimeAllocationCompetitionService;

import java.util.*;

/**
 * 工作量分配竞赛Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@RequiredArgsConstructor
@Service
public class DcimsWorktimeAllocationCompetitionServiceImpl implements IDcimsWorktimeAllocationCompetitionService {

    private final DcimsWorktimeAllocationCompetitionMapper baseMapper;
    private final DcimsCompetitionMapper competitionBaseMapper;
    private final IDcimsCompetitionService competitionService;

    /**
     * 查询工作量分配竞赛
     */
    @Override
    public DcimsWorktimeAllocationCompetitionVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询工作量分配竞赛列表
     */
    @Override
    public TableDataInfo<DcimsWorktimeAllocationCompetitionVo> queryPageList(DcimsWorktimeAllocationCompetitionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsWorktimeAllocationCompetition> lqw = buildQueryWrapper(bo);
        Page<DcimsWorktimeAllocationCompetitionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        // 获取竞赛名称
        List<Long> competitionIds = new ArrayList<>();
        for (DcimsWorktimeAllocationCompetitionVo entity : result.getRecords()){
            competitionIds.add(entity.getCompetitionId());
        }
        List<DcimsCompetitionVo> competitionList = competitionService.listById(competitionIds);
        for (DcimsWorktimeAllocationCompetitionVo entity : result.getRecords()){
            // 设置竞赛详情
            for (DcimsCompetitionVo cvo: competitionList){
                if (Objects.equals(cvo.getId(), entity.getCompetitionId())){
                    entity.setCompetitionDetail(cvo);
                    break;
                }
            }
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询工作量分配竞赛列表
     */
    @Override
    public TableDataInfo<DcimsWorktimeAllocationCompetitionVo> queryPageListByTeacherId() {
        LambdaQueryWrapper<DcimsCompetition> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(DcimsCompetition::getResponsiblePersonId, AccountUtils.getAccount().getTeacherId());
        List<DcimsCompetition> competitionList = competitionBaseMapper.selectList(lqw1);
        Collection<Long> ids = new ArrayList<>();
        competitionList.forEach(element -> {
            ids.add(element.getId());
        });

        // 判断id是否为空，为空直接返回空值
        if(ids.size() == 0)
            return new TableDataInfo<>();
        LambdaQueryWrapper<DcimsWorktimeAllocationCompetition> lqw = new LambdaQueryWrapper<>();
        lqw.in(DcimsWorktimeAllocationCompetition::getCompetitionId, ids);
        lqw.eq(DcimsWorktimeAllocationCompetition::getStatus, 0);
        List<DcimsWorktimeAllocationCompetitionVo> result = baseMapper.selectVoList(lqw);
        result.forEach(element -> {
            for(DcimsCompetition competition:competitionList){
                if (competition.getId().equals(element.getCompetitionId())) {
                    element.setCompetitionName(competition.getName());
                    break;
                }
            }
        });
        return TableDataInfo.build(result);
    }

    /**
     * 查询工作量分配竞赛列表
     */
    @Override
    public List<DcimsWorktimeAllocationCompetitionVo> queryList(DcimsWorktimeAllocationCompetitionBo bo) {
        LambdaQueryWrapper<DcimsWorktimeAllocationCompetition> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsWorktimeAllocationCompetition> buildQueryWrapper(DcimsWorktimeAllocationCompetitionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsWorktimeAllocationCompetition> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getCompetitionId() != null, DcimsWorktimeAllocationCompetition::getCompetitionId, bo.getCompetitionId());
        lqw.eq(bo.getWorktimeId() != null, DcimsWorktimeAllocationCompetition::getWorktimeId, bo.getWorktimeId());
        lqw.eq(bo.getTotal() != null, DcimsWorktimeAllocationCompetition::getTotal, bo.getTotal());
        lqw.eq(bo.getRemain() != null, DcimsWorktimeAllocationCompetition::getRemain, bo.getRemain());
        return lqw;
    }

    /**
     * 新增工作量分配竞赛
     */
    @Override
    public Boolean insertByBo(DcimsWorktimeAllocationCompetitionBo bo) {
        DcimsWorktimeAllocationCompetition add = BeanUtil.toBean(bo, DcimsWorktimeAllocationCompetition.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改工作量分配竞赛
     */
    @Override
    public Boolean updateByBo(DcimsWorktimeAllocationCompetitionBo bo) {
        DcimsWorktimeAllocationCompetition update = BeanUtil.toBean(bo, DcimsWorktimeAllocationCompetition.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsWorktimeAllocationCompetition entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除工作量分配竞赛
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
