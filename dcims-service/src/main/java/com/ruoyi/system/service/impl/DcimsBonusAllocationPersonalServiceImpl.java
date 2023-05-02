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
import com.ruoyi.system.domain.bo.DcimsBonusAllocationPersonalBo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationPersonalVo;
import com.ruoyi.system.domain.DcimsBonusAllocationPersonal;
import com.ruoyi.system.mapper.DcimsBonusAllocationPersonalMapper;
import com.ruoyi.system.service.IDcimsBonusAllocationPersonalService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 奖金分配个人Service业务层处理
 *
 * @author ruoyi
 * @date 2023-05-02
 */
@RequiredArgsConstructor
@Service
public class DcimsBonusAllocationPersonalServiceImpl implements IDcimsBonusAllocationPersonalService {

    private final DcimsBonusAllocationPersonalMapper baseMapper;

    /**
     * 查询奖金分配个人
     */
    @Override
    public DcimsBonusAllocationPersonalVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询奖金分配个人列表
     */
    @Override
    public TableDataInfo<DcimsBonusAllocationPersonalVo> queryPageList(DcimsBonusAllocationPersonalBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsBonusAllocationPersonal> lqw = buildQueryWrapper(bo);
        Page<DcimsBonusAllocationPersonalVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询奖金分配个人列表
     */
    @Override
    public List<DcimsBonusAllocationPersonalVo> queryList(DcimsBonusAllocationPersonalBo bo) {
        LambdaQueryWrapper<DcimsBonusAllocationPersonal> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsBonusAllocationPersonal> buildQueryWrapper(DcimsBonusAllocationPersonalBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsBonusAllocationPersonal> lqw = Wrappers.lambdaQuery();
        return lqw;
    }

    /**
     * 新增奖金分配个人
     */
    @Override
    public Boolean insertByBo(DcimsBonusAllocationPersonalBo bo) {
        DcimsBonusAllocationPersonal add = BeanUtil.toBean(bo, DcimsBonusAllocationPersonal.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改奖金分配个人
     */
    @Override
    public Boolean updateByBo(DcimsBonusAllocationPersonalBo bo) {
        DcimsBonusAllocationPersonal update = BeanUtil.toBean(bo, DcimsBonusAllocationPersonal.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsBonusAllocationPersonal entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除奖金分配个人
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
