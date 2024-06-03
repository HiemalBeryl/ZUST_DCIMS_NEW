package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.DcimsGlobalSetting;

import java.util.List;
import java.util.Map;

/**
 * 允许用户自定义的全局设置Service接口
 *
 * @date 2024-02-14
 */
public interface IDcimsGlobalSettingService {
    /** 获取可选年份列表 */
    List<String> getAnnualList();
    /** 删除某一年份 */
    R<Boolean> deleteAnnual(String year);
    /** 添加某一年份*/
    R<Boolean> addAnnual(String year);
    /** 根据所选年份查询起止时间等内容 */
    DcimsGlobalSetting getDetail(String year);
    /** 修改起止时间 */
    DcimsGlobalSetting changeDetail(DcimsGlobalSetting globalSetting);
    /**
     * 查询是否处于截止时间内
     */
    boolean isDeadline(String year, String businessType);
    /**
     * 根据教师工号查询待办事项
     */
    Map<String, Object> getTodoList();
}
