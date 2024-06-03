package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsBonusAllocationPersonal;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationPersonalVo;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationPersonalBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 奖金分配个人Service接口
 *
 * @author Andy
 * @date 2023-05-03
 */
public interface IDcimsBonusAllocationPersonalService {

    /**
     * 查询奖金分配个人
     */
    DcimsBonusAllocationPersonalVo queryById(Long id);

    /**
     * 查询奖金分配个人列表
     */
    TableDataInfo<DcimsBonusAllocationPersonalVo> queryPageList(DcimsBonusAllocationPersonalBo bo, PageQuery pageQuery, Long id);

    /**
     * 查询奖金分配个人列表
     */
    List<DcimsBonusAllocationPersonalVo> queryList(DcimsBonusAllocationPersonalBo bo);

    /**
     * 新增奖金分配个人
     */
    Boolean insertByBo(DcimsBonusAllocationPersonalBo bo);

    /**
     * 修改奖金分配个人
     */
    Boolean updateByBo(DcimsBonusAllocationPersonalBo bo);

    /**
     * 校验并批量删除奖金分配个人信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
