package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsTest;
import com.ruoyi.system.domain.vo.DcimsTestVo;
import com.ruoyi.system.domain.bo.DcimsTestBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * testService接口
 *
 * @author hiemalberyl
 * @date 2023-04-24
 */
public interface IDcimsTestService {

    /**
     * 查询test
     */
    DcimsTestVo queryById(Long id);

    /**
     * 查询test列表
     */
    TableDataInfo<DcimsTestVo> queryPageList(DcimsTestBo bo, PageQuery pageQuery);

    /**
     * 查询test列表
     */
    List<DcimsTestVo> queryList(DcimsTestBo bo);

    /**
     * 新增test
     */
    Boolean insertByBo(DcimsTestBo bo);

    /**
     * 修改test
     */
    Boolean updateByBo(DcimsTestBo bo);

    /**
     * 校验并批量删除test信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
