package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.DcimsBonusAllocation;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationBo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationVo;
import com.ruoyi.system.mapper.DcimsBonusAllocationMapper;
import com.ruoyi.system.service.IDcimsBonusAllocationService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 奖金分配总Service业务层处理
 *
 * @author Andy
 * @date 2023-05-02
 */
@RequiredArgsConstructor
@Service
public class DcimsBonusAllocationServiceImpl implements IDcimsBonusAllocationService {

    private final DcimsBonusAllocationMapper baseMapper;

    /**
     * 查询奖金分配总
     */
    @Override
    public DcimsBonusAllocationVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询奖金分配总列表
     */
    @Override
    public TableDataInfo<DcimsBonusAllocationVo> queryPageList(DcimsBonusAllocationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsBonusAllocation> lqw = buildQueryWrapper(bo);
        Page<DcimsBonusAllocationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询奖金分配总列表
     */
    @Override
    public List<DcimsBonusAllocationVo> queryList(DcimsBonusAllocationBo bo) {
        LambdaQueryWrapper<DcimsBonusAllocation> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsBonusAllocation> buildQueryWrapper(DcimsBonusAllocationBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsBonusAllocation> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getYears() != null, DcimsBonusAllocation::getYears, bo.getYears());
        lqw.eq(bo.getCollege() != null, DcimsBonusAllocation::getCollege, bo.getCollege());
        return lqw;
    }

    /**
     * 新增奖金分配总
     */
    @Override
    public Boolean insertByBo(DcimsBonusAllocationBo bo) {
        DcimsBonusAllocation add = BeanUtil.toBean(bo, DcimsBonusAllocation.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
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
        DcimsBonusAllocation update = BeanUtil.toBean(bo, DcimsBonusAllocation.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsBonusAllocation entity){
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
        return baseMapper.deleteBatchIds(ids) > 0;
    }



    /**
    * 获取单条数据，用于获取总金额，可分配余额，以及日期
    */
    @Override
    public DcimsBonusAllocationVo getTotalAmount() {
        Long teacherId = AccountUtils.getAccount().getTeacherId();
        LambdaQueryWrapper<DcimsBonusAllocation> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsBonusAllocation::getYears,2022);
        lqw.eq(DcimsBonusAllocation::getTeacherInCharge,teacherId);
        return baseMapper.selectVoOne(lqw);
    }




}
