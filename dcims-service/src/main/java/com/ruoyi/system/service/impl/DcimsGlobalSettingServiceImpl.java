package com.ruoyi.system.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.DcimsGlobalSetting;
import com.ruoyi.system.mapper.DcimsGlobalSettingMapper;
import com.ruoyi.system.service.IDcimsGlobalSettingService;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DcimsGlobalSettingServiceImpl implements IDcimsGlobalSettingService {

    private final DcimsGlobalSettingMapper dcimsGlobalSettingMapper;
    private final ISysDictDataService sysDictDataService;
    private final ISysDictTypeService sysDictTypeService;

    /**
     * 获取可选年份列表
     */
    @Override
    public List<String> getAnnualList() {
        LambdaQueryWrapper<DcimsGlobalSetting> lqw = new LambdaQueryWrapper<>();
        lqw.groupBy(DcimsGlobalSetting::getYear);
        lqw.select(DcimsGlobalSetting::getYear);
        List<DcimsGlobalSetting> list = dcimsGlobalSettingMapper.selectList(lqw);
        System.out.println(list);
        return list.stream().map(DcimsGlobalSetting::getYear).collect(Collectors.toList());
    }

    /**
     * 删除某一年份
     */
    @Override
    public R<Boolean> deleteAnnual(String year) {
        // 入参不能为空
        if (StringUtils.isEmpty(year) || year.length() > 5) {
            return R.fail("年份输入异常", false);
        }

        // 关闭框架字典中存在的年份
        SysDictData dict = getDict(year);
        if(ObjectUtil.isNotNull(dict)){
            dict.setStatus("1");
            sysDictDataService.updateDictData(dict);
        } else {
            // 不存在则新建一个字典数据
            SysDictData initDict = initDict(year);
            initDict.setStatus("1");
            sysDictDataService.insertDictData(initDict);
        }

        // 逻辑删除用户全局设置表中的年份
        dcimsGlobalSettingMapper.delete(new QueryWrapper<DcimsGlobalSetting>().lambda().eq(DcimsGlobalSetting::getYear, year));
        return R.ok(true);
    }

    /**
     * 添加某一年份
     *
     * @param year
     */
    @Override
    public R<Boolean> addAnnual(String year) {
        // 入参不能为空
        if (StringUtils.isEmpty(year) || year.length() > 5) {
            return R.fail("年份输入异常", false);
        }

        // 判断是否已存在
        DcimsGlobalSetting dcimsGlobalSetting = new DcimsGlobalSetting();
        dcimsGlobalSetting.setYear(year);
        dcimsGlobalSetting.setStatus("2");
        List<DcimsGlobalSetting> list = dcimsGlobalSettingMapper.selectList(new QueryWrapper<>(dcimsGlobalSetting));
        if (ObjectUtil.isNotNull(list) && list.size() > 0) {
            return R.fail("年份已存在", false);
        }

        // 开启框架字典中存在的年份
        SysDictData dict = getDict(year);
        if(ObjectUtil.isNotNull(dict)){
            dict.setStatus("0");
            sysDictDataService.updateDictData(dict);
        } else {
            // 不存在则新建一个字典数据
            SysDictData initDict = initDict(year);
            initDict.setStatus("0");
            sysDictDataService.insertDictData(initDict);
        }

        // 插入新的年份或恢复逻辑删除
        DcimsGlobalSetting globalSetting = dcimsGlobalSettingMapper.selectOne(new QueryWrapper<DcimsGlobalSetting>().lambda().eq(DcimsGlobalSetting::getYear, year));
        if (ObjectUtil.isNull(globalSetting)) {
            globalSetting = new DcimsGlobalSetting();
            globalSetting.setYear(year);
            globalSetting.setEstablishCompetition(false);
            globalSetting.setCreateTeam(false);
            globalSetting.setStatus("0");
            dcimsGlobalSettingMapper.insert(globalSetting);
        } else {
            globalSetting.setStatus("0");
            dcimsGlobalSettingMapper.updateById(globalSetting);
        }
        return R.ok(true);
    }

    /**
     * 根据所选年份查询起止时间等内容
     *
     * @param year
     */
    @Override
    public DcimsGlobalSetting getDetail(String year) {
        // 每个年份对应的仅能查询出一个结果
        LambdaQueryWrapper<DcimsGlobalSetting> qw = new QueryWrapper<DcimsGlobalSetting>().lambda().eq(DcimsGlobalSetting::getYear, year);
        DcimsGlobalSetting one = dcimsGlobalSettingMapper.selectOne(qw);
        return one;
    }

    /**
     * 修改起止时间
     *
     * @param globalSetting
     */
    @Override
    public DcimsGlobalSetting changeDetail(DcimsGlobalSetting globalSetting) {
        dcimsGlobalSettingMapper.updateById(globalSetting);
        return dcimsGlobalSettingMapper.selectById(globalSetting.getId());
    }

    /**
     * 查询某年份是否存在于系统字典中
     */
    private SysDictData getDict(String year) {
        SysDictData query = new SysDictData();
        query.setDictType("dcims_years");
        List<SysDictData> list = sysDictDataService.selectDictDataList(query);
        List<SysDictData> collect = list.stream().filter(dict -> dict.getDictValue().equals(year)).collect(Collectors.toList());
        if (collect.size() == 0) {
            return null;
        }
        return collect.get(0);
    }

    /**
     * 初始化字典数据类，用于添加年份
     */
    private SysDictData initDict(String year) {
        SysDictData dict = new SysDictData();
        dict.setDictType("dcims_years");
        dict.setDictSort(5000);
        dict.setDictValue(year);
        dict.setDictLabel(year);
        dict.setListClass("default");
        dict.setIsDefault("N");
        dict.setStatus("0");
        return dict;
    }

    /**
     * 查询是否处于截止时间内
     *
     * @param year
     */
    @Override
    public boolean isDeadline(String year, String businessType) {
        DcimsGlobalSetting globalSetting = getDetail(year);
        // 年份为空，说明该年份未创建，不允许新增或修改
        if (ObjectUtil.isNull(globalSetting)) {
            return false;
        }
        // 获取到的起止时间存在空值，说明管理员仅仅创建了该年份，但未设置时间，故同样不允许修改
        Date now = new Date();
        if ("competition".equals(businessType)) {
            if (ObjectUtil.isNull(globalSetting.getCompetitionStartTime()) || ObjectUtil.isNull(globalSetting.getCompetitionEndTime())) {
                return false;
            }
            boolean flag1 = DateUtil.between(now, globalSetting.getCompetitionStartTime(), DateUnit.SECOND) > 0L;
            boolean flag2 = DateUtil.between(globalSetting.getCompetitionEndTime(), now, DateUnit.SECOND) > 0L;
            if (flag1 && flag2) {
                return true;
            }
        }else if("team".equals(businessType)){
            if (ObjectUtil.isNull(globalSetting.getTeamStartTime()) || ObjectUtil.isNull(globalSetting.getTeamEndTime())) {
                return false;
            }
            boolean flag1 = DateUtil.between(now, globalSetting.getTeamStartTime(), DateUnit.SECOND) > 0L;
            boolean flag2 = DateUtil.between(globalSetting.getTeamEndTime(), now, DateUnit.SECOND) > 0L;
            if (flag1 && flag2) {
                return true;
            }
        }
        return false;
    }
}
