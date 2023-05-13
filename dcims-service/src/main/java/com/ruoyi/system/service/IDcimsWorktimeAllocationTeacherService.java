package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsWorktimeAllocationTeacher;
import com.ruoyi.system.domain.vo.AllocationCompetitionDictVo;
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationTeacherVo;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationTeacherBo;
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
public interface IDcimsWorktimeAllocationTeacherService {

    /**
     * 查询工作量分配竞赛
     */
    TableDataInfo<AllocationCompetitionDictVo> queryCompetitionList();

    /**
     * 查询工作量分配
     */
    DcimsWorktimeAllocationTeacherVo queryById(Long id);

    /**
     * 查询工作量分配列表
     */
    TableDataInfo<DcimsWorktimeAllocationTeacherVo> queryPageList(DcimsWorktimeAllocationTeacherBo bo, PageQuery pageQuery);

    /**
     * 查询工作量分配列表
     */
    List<DcimsWorktimeAllocationTeacherVo> queryList(DcimsWorktimeAllocationTeacherBo bo);

    /**
     * 新增工作量分配
     */
    Boolean insertByBo(DcimsWorktimeAllocationTeacherBo bo);

    /**
     * 修改工作量分配
     */
    Boolean updateByBo(DcimsWorktimeAllocationTeacherBo bo);

    /**
     * 校验并批量删除工作量分配信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
