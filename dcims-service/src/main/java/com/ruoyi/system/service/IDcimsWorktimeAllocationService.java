package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsWorktimeAllocation;
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationVo;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 工作量分配Service接口
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
public interface IDcimsWorktimeAllocationService {

    /**
     * 查询工作量分配
     */
    DcimsWorktimeAllocationVo queryById(Long id);

    /**
     * 查询工作量分配列表
     */
    TableDataInfo<DcimsWorktimeAllocationVo> queryPageList(DcimsWorktimeAllocationBo bo, PageQuery pageQuery);

    /**
     * 查询工作量分配列表
     */
    List<DcimsWorktimeAllocationVo> queryList(DcimsWorktimeAllocationBo bo);

    /**
     * 新增工作量分配
     */
    Boolean insertByBo(DcimsWorktimeAllocationBo bo);

    /**
     * 修改工作量分配
     */
    Boolean updateByBo(DcimsWorktimeAllocationBo bo);

    /**
     * 校验并批量删除工作量分配信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
