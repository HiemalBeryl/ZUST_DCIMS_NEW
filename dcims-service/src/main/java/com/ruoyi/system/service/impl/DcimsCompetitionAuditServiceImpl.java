package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.utils.AccoutUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionAuditVo;
import com.ruoyi.system.domain.DcimsCompetitionAudit;
import com.ruoyi.system.mapper.DcimsCompetitionAuditMapper;
import com.ruoyi.system.service.IDcimsCompetitionAuditService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 竞赛审核Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@RequiredArgsConstructor
@Service
public class DcimsCompetitionAuditServiceImpl implements IDcimsCompetitionAuditService {

    private final DcimsCompetitionAuditMapper competitionAuditBaseMapper;
    private final DcimsCompetitionMapper competitionBaseMapper;

    /**
     * 查询竞赛审核
     */
    @Override
    public DcimsCompetitionVo queryById(Long id){
        return competitionBaseMapper.selectVoById(id);
    }

    /**
     * 查询竞赛审核列表
     */
    @Override
    public TableDataInfo<DcimsCompetitionVo> queryPageList(DcimsCompetitionAuditBo bo, PageQuery pageQuery) {
        // 自定义业务，获取当前登录账号对应的教师工号
        String id = StpUtil.getLoginIdAsString();
        String teacherId = AccoutUtils.getTeacherId(id).getTeacherId().toString();
        LambdaQueryWrapper<DcimsCompetition> lqw = new LambdaQueryWrapper<>();
        lqw.eq(teacherId != null&&teacherId != "", DcimsCompetition::getNextAuditId,teacherId);

        Page<DcimsCompetitionVo> result = competitionBaseMapper.selectVoPage(pageQuery.build(), lqw);
        System.out.println(result);
        return TableDataInfo.build(result);
    }

    /**
     * 查询竞赛审核列表
     */
    @Override
    public List<DcimsCompetitionAuditVo> queryList(DcimsCompetitionAuditBo bo) {
        LambdaQueryWrapper<DcimsCompetitionAudit> lqw = buildQueryWrapper(bo);
        return competitionAuditBaseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsCompetitionAudit> buildQueryWrapper(DcimsCompetitionAuditBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsCompetitionAudit> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getCompetitionId() != null, DcimsCompetitionAudit::getCompetitionId, bo.getCompetitionId());
        lqw.eq(bo.getTeacherId() != null, DcimsCompetitionAudit::getTeacherId, bo.getTeacherId());
        lqw.eq(bo.getResult() != null, DcimsCompetitionAudit::getResult, bo.getResult());
        lqw.eq(StringUtils.isNotBlank(bo.getReason()), DcimsCompetitionAudit::getReason, bo.getReason());
        lqw.eq(bo.getNextTeacherId() != null, DcimsCompetitionAudit::getNextTeacherId, bo.getNextTeacherId());
        return lqw;
    }

    /**
     * 新增竞赛审核
     */
    @Override
    public Boolean insertByBo(List<DcimsCompetitionAuditBo> boList) {
        //TODO: 将多次请求改成一次请求，提高效率
        List<DcimsCompetition> comList = new ArrayList<>();
        List<DcimsCompetitionAudit> comAuditList = new ArrayList<>();
        for (DcimsCompetitionAuditBo bo:boList) {
            //手动为Bo添加操作者等部分数据
            bo.setTeacherId(AccoutUtils.getTeacherId(StpUtil.getLoginIdAsString()).getTeacherId());
            bo.setResult(1L);
            DcimsCompetitionAudit add1 = BeanUtil.toBean(bo, DcimsCompetitionAudit.class);
            DcimsCompetition add2 = competitionBaseMapper.selectById(bo.getCompetitionId());
            if(add2.getNextAuditId().equals(add1.getTeacherId())){
                comAuditList.add(add1);
                add2.setNextAuditId(bo.getNextTeacherId());
                comList.add(add2);
            }
        }
        return (competitionBaseMapper.updateBatchById(comList))&&(competitionAuditBaseMapper.insertBatch(comAuditList));
    }

    /**
     * 修改竞赛审核
     */
    @Override
    public Boolean updateByBo(DcimsCompetitionAuditBo bo) {
        DcimsCompetitionAudit update = BeanUtil.toBean(bo, DcimsCompetitionAudit.class);
        validEntityBeforeSave(update);
        return competitionAuditBaseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsCompetitionAudit entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除竞赛审核
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        // 自定义业务，next_audit_id改为0，表示需要提交者重新修改后提交，并在audit表中添加一条驳回记录
        LambdaQueryWrapper<DcimsCompetition> lqw = new LambdaQueryWrapper<>();
        lqw.in(DcimsCompetition::getId,ids);
        List<DcimsCompetition> comList = competitionBaseMapper.selectList(lqw);
        List<DcimsCompetitionAudit> auditList = new ArrayList<>();
        for (DcimsCompetition entity:comList){
            Long teacherId = AccoutUtils.getTeacherId(StpUtil.getLoginIdAsString()).getTeacherId();
            System.out.println(teacherId);
            if(entity.getNextAuditId().equals(teacherId)){
                entity.setNextAuditId(entity.getResponsiblePersonId());

                DcimsCompetitionAudit audit = new DcimsCompetitionAudit();
                audit.setCompetitionId(entity.getId());
                audit.setTeacherId(teacherId);
                audit.setResult(0L);
                audit.setReason("审批不通过，驳回审批");
                audit.setNextTeacherId(entity.getResponsiblePersonId());
                auditList.add(audit);
            }else {
                comList.remove(entity);
            }
        }
        if(!comList.isEmpty()) competitionBaseMapper.updateBatchById(comList);
        if(!auditList.isEmpty()) competitionAuditBaseMapper.insertBatch(auditList);
        return !auditList.isEmpty();
    }
}
