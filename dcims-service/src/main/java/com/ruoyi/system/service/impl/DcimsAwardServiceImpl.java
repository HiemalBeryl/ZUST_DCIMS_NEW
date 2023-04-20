package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsAwardBo;
import com.ruoyi.system.domain.vo.DcimsAwardVo;
import com.ruoyi.system.domain.DcimsAward;
import com.ruoyi.system.mapper.DcimsAwardMapper;
import com.ruoyi.system.service.IDcimsAwardService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 获奖基本信息Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@RequiredArgsConstructor
@Service
public class DcimsAwardServiceImpl implements IDcimsAwardService {

    private final DcimsAwardMapper baseMapper;

    /**
     * 查询获奖基本信息
     */
    @Override
    public DcimsAwardVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }


    /**
     * 查询获奖基本信息列表
     */
    @Override
    public List<DcimsAwardVo> queryList(DcimsAwardBo bo) {
        LambdaQueryWrapper<DcimsAward> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsAward> buildQueryWrapper(DcimsAwardBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsAward> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getParentId() != null, DcimsAward::getParentId, bo.getParentId());
        lqw.like(StringUtils.isNotBlank(bo.getCompetitionName()), DcimsAward::getCompetitionName, bo.getCompetitionName());
        lqw.like(StringUtils.isNotBlank(bo.getTeamName()), DcimsAward::getTeamName, bo.getTeamName());
        lqw.eq(StringUtils.isNotBlank(bo.getAwardLevel()), DcimsAward::getAwardLevel, bo.getAwardLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getCompetitionType()), DcimsAward::getCompetitionType, bo.getCompetitionType());
        lqw.like(StringUtils.isNotBlank(bo.getStudentName()), DcimsAward::getStudentName, bo.getStudentName());
        lqw.like(StringUtils.isNotBlank(bo.getTeacherName()), DcimsAward::getTeacherName, bo.getTeacherName());
        lqw.eq(bo.getCompetitionTime() != null, DcimsAward::getCompetitionTime, bo.getCompetitionTime());
        lqw.eq(bo.getAwardTime() != null, DcimsAward::getAwardTime, bo.getAwardTime());
        return lqw;
    }

    /**
     * 新增获奖基本信息
     */
    @Override
    public Boolean insertByBo(DcimsAwardBo bo) {
        DcimsAward add = BeanUtil.toBean(bo, DcimsAward.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改获奖基本信息
     */
    @Override
    public Boolean updateByBo(DcimsAwardBo bo) {
        DcimsAward update = BeanUtil.toBean(bo, DcimsAward.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsAward entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除获奖基本信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
