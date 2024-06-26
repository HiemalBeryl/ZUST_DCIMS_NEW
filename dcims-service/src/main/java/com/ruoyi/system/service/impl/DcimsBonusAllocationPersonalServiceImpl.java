package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.DcimsBonusAllocationPersonal;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationBo;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationPersonalBo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationPersonalVo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationVo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import com.ruoyi.system.mapper.DcimsBonusAllocationPersonalMapper;
import com.ruoyi.system.service.IDcimsBasicDataService;
import com.ruoyi.system.service.IDcimsBonusAllocationPersonalService;
import com.ruoyi.system.service.IDcimsBonusAllocationService;
import com.ruoyi.system.service.IDcimsCompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 奖金分配个人Service业务层处理
 *
 * @author Andy
 * @date 2023-05-03
 */
@RequiredArgsConstructor
@Service
public class DcimsBonusAllocationPersonalServiceImpl implements IDcimsBonusAllocationPersonalService {

    private final DcimsBonusAllocationPersonalMapper baseMapper;
    private final IDcimsBasicDataService dataService;
    private final IDcimsCompetitionService competitionService;

    @Autowired
    private IDcimsBonusAllocationService iDcimsBonusAllocationService;

    /**
     * 查询奖金分配个人
     */
    @Override
    public DcimsBonusAllocationPersonalVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询奖金分配个人列表
     */
    @Override
    public TableDataInfo<DcimsBonusAllocationPersonalVo> queryPageList(DcimsBonusAllocationPersonalBo bo, PageQuery pageQuery, Long id) {
        LambdaQueryWrapper<DcimsBonusAllocationPersonal> lqw = buildQueryWrapper(bo);
        lqw.eq(id != null, DcimsBonusAllocationPersonal::getAllocationId, id);
        Page<DcimsBonusAllocationPersonalVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        // 获取详情
        List<Long> gainerIds = new ArrayList<>();
        List<Long> competitionIds = new ArrayList<>();
        List<Long> chargeIds = new ArrayList<>();
        for (DcimsBonusAllocationPersonalVo entity : result.getRecords()){
            gainerIds.add(entity.getGainer());
            competitionIds.add(entity.getCompetition());
            chargeIds.add(entity.getTeacherInCharge());
        }
        List<DcimsTeacherVo> gainerList = dataService.getTeacherNameByIds(gainerIds);
        List<DcimsCompetitionVo> competitionList = competitionService.listById(competitionIds);
        List<DcimsTeacherVo> chargeList = dataService.getTeacherNameByIds(chargeIds);
        for (DcimsBonusAllocationPersonalVo entity : result.getRecords()){
            // 设置获得人详情
            for (DcimsTeacherVo gvo: gainerList){
                System.out.println("前面:"+gvo.getTeacherId()+"前面:"+entity.getGainer());
                if (Objects.equals(gvo.getTeacherId(), entity.getGainer())){
                    System.out.println("前面:"+gvo.getTeacherId()+"前面:"+entity.getGainer());
                    entity.setGainerDetail(gvo);
                    break;
                }
            }

            // 设置分配者详情
            for (DcimsTeacherVo cvo: chargeList){
                System.out.println("后面:"+cvo.getTeacherId()+"后面:"+entity.getGainer());
                if (Objects.equals(cvo.getTeacherId(), entity.getTeacherInCharge())){
                    entity.setTeacherInChargeDetail(cvo);
                    break;
                }
            }

            // 设置竞赛详情
            for (DcimsCompetitionVo cvo: competitionList){
                if (Objects.equals(cvo.getId(), entity.getCompetition())){
                    entity.setCompetitionDetail(cvo);
                    break;
                }
            }
        }
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
        //总余额
        DcimsBonusAllocationVo totalAmount = iDcimsBonusAllocationService.getTotalAmount(bo.getAllocationId());
        Long oldUnallocated = totalAmount.getUnallocated();
        //个人修改前的金额
        DcimsBonusAllocationPersonalVo dcimsBonusAllocationPersonalVo = queryById(bo.getId());
        Long oldBonus = dcimsBonusAllocationPersonalVo.getBonus();
        //个人修改后的金额
        Long newBonus = bo.getBonus();
        //计算修改后的总余额
        Long newUnallocated = oldUnallocated - newBonus + oldBonus;
        //若修改后金额小于0，返回false
        if (newUnallocated<0){
            return false;
        }
        //计算修改后的留存比
        BigDecimal retentionRatio = BigDecimal.valueOf(newUnallocated * 1.0 / totalAmount.getTotalAmount());

        totalAmount.setUnallocated(newUnallocated);
        totalAmount.setRetentionRatio(retentionRatio);
        totalAmount.setAllocated(totalAmount.getTotalAmount()-newUnallocated);

        iDcimsBonusAllocationService.updateByBo(BeanUtil.toBean(totalAmount, DcimsBonusAllocationBo.class));

        DcimsBonusAllocationPersonal update = BeanUtil.toBean(bo, DcimsBonusAllocationPersonal.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsBonusAllocationPersonal entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除奖金分配个人
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
