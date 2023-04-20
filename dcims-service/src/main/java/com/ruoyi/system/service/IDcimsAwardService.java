package com.ruoyi.system.service;

import com.ruoyi.system.domain.DcimsAward;
import com.ruoyi.system.domain.vo.DcimsAwardVo;
import com.ruoyi.system.domain.bo.DcimsAwardBo;

import java.util.Collection;
import java.util.List;

/**
 * 获奖基本信息Service接口
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
public interface IDcimsAwardService {

    /**
     * 查询获奖基本信息
     */
    DcimsAwardVo queryById(Long id);


    /**
     * 查询获奖基本信息列表
     */
    List<DcimsAwardVo> queryList(DcimsAwardBo bo);

    /**
     * 新增获奖基本信息
     */
    Boolean insertByBo(DcimsAwardBo bo);

    /**
     * 修改获奖基本信息
     */
    Boolean updateByBo(DcimsAwardBo bo);

    /**
     * 校验并批量删除获奖基本信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
