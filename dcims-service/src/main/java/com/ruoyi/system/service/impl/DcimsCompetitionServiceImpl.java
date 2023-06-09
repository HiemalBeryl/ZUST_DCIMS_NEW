package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.service.IDcimsCompetitionService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 竞赛赛事基本信息Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-12
 */
@RequiredArgsConstructor
@Service
public class DcimsCompetitionServiceImpl implements IDcimsCompetitionService {

    private final DcimsCompetitionMapper baseMapper;

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
        lqw.eq(bo.getInnerTime() != null, DcimsCompetition::getInnerTime, bo.getInnerTime());
        lqw.eq(bo.getProvinceTime() != null, DcimsCompetition::getProvinceTime, bo.getProvinceTime());
        lqw.eq(bo.getNationalTime() != null, DcimsCompetition::getNationalTime, bo.getNationalTime());
        lqw.eq(bo.getStopTime() != null, DcimsCompetition::getStopTime, bo.getStopTime());
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
    public Boolean updateByBo(DcimsCompetitionBo bo) {
        DcimsCompetition update = BeanUtil.toBean(bo, DcimsCompetition.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsCompetition entity){
        //TODO 做一些数据校验,如唯一约束
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
}
