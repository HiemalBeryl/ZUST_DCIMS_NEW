package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsWorktimeAllocationCompetition;
import com.ruoyi.system.domain.vo.AllocationCompetitionDictVo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsWorktimeAllocationCompetitionMapper;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationTeacherBo;
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationTeacherVo;
import com.ruoyi.system.domain.DcimsWorktimeAllocationTeacher;
import com.ruoyi.system.mapper.DcimsWorktimeAllocationTeacherMapper;
import com.ruoyi.system.service.IDcimsWorktimeAllocationTeacherService;

import java.util.ArrayList;
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
public class DcimsWorktimeAllocationTeacherServiceImpl implements IDcimsWorktimeAllocationTeacherService {

    private final DcimsWorktimeAllocationTeacherMapper baseMapper;
    private final DcimsCompetitionMapper competitionBaseMapper;
    private final DcimsWorktimeAllocationCompetitionMapper worktimeAllocationCompetitionBaseMapper;

    /**
     * 查询工作量分配竞赛
     */
    public TableDataInfo<AllocationCompetitionDictVo> queryCompetitionList(){
        LambdaQueryWrapper<DcimsCompetition> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(DcimsCompetition::getResponsiblePersonId, AccountUtils.getAccount().getTeacherId());
        List<DcimsCompetition> competitionList = competitionBaseMapper.selectList();

        LambdaQueryWrapper<DcimsWorktimeAllocationCompetition> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(DcimsWorktimeAllocationCompetition::getCompetitionId,-1);
        competitionList.forEach(element -> {
            lqw2.in(DcimsWorktimeAllocationCompetition::getCompetitionId,element.getId());
        });
        // 3表示取出正在核算过程中的竞赛
        lqw2.eq(DcimsWorktimeAllocationCompetition::getStatus,3);
        List<DcimsWorktimeAllocationCompetition> dcimsWorktimeAllocationCompetitionList = worktimeAllocationCompetitionBaseMapper.selectList(lqw2);

        // 转封装
        List<AllocationCompetitionDictVo> result = new ArrayList<>();
        dcimsWorktimeAllocationCompetitionList.forEach(element -> {
            AllocationCompetitionDictVo vo = new AllocationCompetitionDictVo();
            competitionList.forEach(com -> {
                if(com.getId().equals(element.getCompetitionId())){
                    vo.setName(com.getName());
                    vo.setId(element.getWorktimeId());
                    vo.setAnnual(com.getAnnual());
                }
            });
            result.add(vo);
        });
        return TableDataInfo.build(result);
    }

    /**
     * 查询工作量分配
     */
    @Override
    public DcimsWorktimeAllocationTeacherVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询工作量分配列表
     */
    @Override
    public TableDataInfo<DcimsWorktimeAllocationTeacherVo> queryPageList(DcimsWorktimeAllocationTeacherBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsWorktimeAllocationTeacher> lqw = buildQueryWrapper(bo);
        Page<DcimsWorktimeAllocationTeacherVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询工作量分配列表
     */
    @Override
    public List<DcimsWorktimeAllocationTeacherVo> queryList(DcimsWorktimeAllocationTeacherBo bo) {
        LambdaQueryWrapper<DcimsWorktimeAllocationTeacher> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsWorktimeAllocationTeacher> buildQueryWrapper(DcimsWorktimeAllocationTeacherBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsWorktimeAllocationTeacher> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getWorktimeCompetitionId() != null, DcimsWorktimeAllocationTeacher::getWorktimeCompetitionId, bo.getWorktimeCompetitionId());
        lqw.eq(bo.getTeacherId() != null, DcimsWorktimeAllocationTeacher::getTeacherId, bo.getTeacherId());
        lqw.eq(bo.getWorktime() != null, DcimsWorktimeAllocationTeacher::getWorktime, bo.getWorktime());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DcimsWorktimeAllocationTeacher::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增工作量分配
     */
    @Override
    public Boolean insertByBo(DcimsWorktimeAllocationTeacherBo bo) {
        DcimsWorktimeAllocationTeacher add = BeanUtil.toBean(bo, DcimsWorktimeAllocationTeacher.class);
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
    public Boolean updateByBo(DcimsWorktimeAllocationTeacherBo bo) {
        DcimsWorktimeAllocationTeacher update = BeanUtil.toBean(bo, DcimsWorktimeAllocationTeacher.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsWorktimeAllocationTeacher entity){
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
