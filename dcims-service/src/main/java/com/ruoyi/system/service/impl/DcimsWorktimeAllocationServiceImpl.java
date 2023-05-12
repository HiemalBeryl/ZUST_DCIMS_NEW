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
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationBo;
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationVo;
import com.ruoyi.system.domain.DcimsWorktimeAllocation;
import com.ruoyi.system.mapper.DcimsWorktimeAllocationMapper;
import com.ruoyi.system.service.IDcimsWorktimeAllocationService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

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
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
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
}
