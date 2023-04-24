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
import com.ruoyi.system.domain.bo.DcimsTestBo;
import com.ruoyi.system.domain.vo.DcimsTestVo;
import com.ruoyi.system.domain.DcimsTest;
import com.ruoyi.system.mapper.DcimsTestMapper;
import com.ruoyi.system.service.IDcimsTestService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * testService业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-24
 */
@RequiredArgsConstructor
@Service
public class DcimsTestServiceImpl implements IDcimsTestService {

    private final DcimsTestMapper baseMapper;

    /**
     * 查询test
     */
    @Override
    public DcimsTestVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询test列表
     */
    @Override
    public TableDataInfo<DcimsTestVo> queryPageList(DcimsTestBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsTest> lqw = buildQueryWrapper(bo);
        Page<DcimsTestVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询test列表
     */
    @Override
    public List<DcimsTestVo> queryList(DcimsTestBo bo) {
        LambdaQueryWrapper<DcimsTest> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsTest> buildQueryWrapper(DcimsTestBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsTest> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getDeptId() != null, DcimsTest::getDeptId, bo.getDeptId());
        lqw.eq(bo.getUserId() != null, DcimsTest::getUserId, bo.getUserId());
        lqw.eq(bo.getOrderNum() != null, DcimsTest::getOrderNum, bo.getOrderNum());
        lqw.eq(StringUtils.isNotBlank(bo.getTestKey()), DcimsTest::getTestKey, bo.getTestKey());
        lqw.eq(StringUtils.isNotBlank(bo.getValue()), DcimsTest::getValue, bo.getValue());
        return lqw;
    }

    /**
     * 新增test
     */
    @Override
    public Boolean insertByBo(DcimsTestBo bo) {
        DcimsTest add = BeanUtil.toBean(bo, DcimsTest.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改test
     */
    @Override
    public Boolean updateByBo(DcimsTestBo bo) {
        DcimsTest update = BeanUtil.toBean(bo, DcimsTest.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsTest entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除test
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
