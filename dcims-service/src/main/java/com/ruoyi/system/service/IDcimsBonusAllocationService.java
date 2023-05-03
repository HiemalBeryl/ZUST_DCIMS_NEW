package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationBo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationVo;

import java.util.Collection;
import java.util.List;

/**
 * 奖金分配总Service接口
 *
 * @author Andy
 * @date 2023-05-02
 */
public interface IDcimsBonusAllocationService {

    /**
     * 查询奖金分配总
     */
    DcimsBonusAllocationVo queryById(Long id);

    /**
     * 查询奖金分配总列表
     */
    TableDataInfo<DcimsBonusAllocationVo> queryPageList(DcimsBonusAllocationBo bo, PageQuery pageQuery);

    /**
     * 查询奖金分配总列表
     */
    List<DcimsBonusAllocationVo> queryList(DcimsBonusAllocationBo bo);

    /**
     * 新增奖金分配总
     */
    Boolean insertByBo(DcimsBonusAllocationBo bo);

    /**
     * 修改奖金分配总
     */
    Boolean updateByBo(DcimsBonusAllocationBo bo);

    /**
     * 校验并批量删除奖金分配总信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 获取学院总金额
     */
    DcimsBonusAllocationVo getTotalAmount();

}
