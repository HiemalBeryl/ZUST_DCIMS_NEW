package com.ruoyi.system.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.DcimsGlobalSetting;
import com.ruoyi.system.domain.bo.DcimsGlobalSettingBo;
import com.ruoyi.system.service.IDcimsGlobalSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/setting")
public class DcimsSettingController {


    private final IDcimsGlobalSettingService iDcimsGlobalSettingService;


    @SaCheckPermission("dcims:globalSetting:1")
    @GetMapping("/list")
    public List<String> list() {
        return iDcimsGlobalSettingService.getAnnualList();
    }


    @SaCheckPermission("dcims:globalSetting:1")
    @Log(title = "截止时间设置", businessType = BusinessType.DELETE)
    @GetMapping("/del")
    public R<Boolean> delAnnual(String year) {
        return iDcimsGlobalSettingService.deleteAnnual(year);
    }


    @SaCheckPermission("dcims:globalSetting:1")
    @Log(title = "截止时间设置", businessType = BusinessType.INSERT)
    @GetMapping("/add")
    public R<Boolean> addAnnual(String year) {
        return iDcimsGlobalSettingService.addAnnual(year);
    }


    @SaCheckPermission("dcims:globalSetting:1")
    @GetMapping("/listDetail")
    public DcimsGlobalSetting getDetail(String year){
        return iDcimsGlobalSettingService.getDetail(year);
    }


    @SaCheckPermission("dcims:globalSetting:1")
    @Log(title = "截止时间设置", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public DcimsGlobalSetting changeDetail(@RequestBody DcimsGlobalSettingBo globalSettingBo) {
        DcimsGlobalSetting globalSetting = new DcimsGlobalSetting();
        BeanUtils.copyProperties(globalSettingBo, globalSetting);
        System.out.println("接收到的参数");
        System.out.println(globalSettingBo);
        System.out.println(globalSetting);
        return iDcimsGlobalSettingService.changeDetail(globalSetting);
    }

    @GetMapping("/myWorkload")
    @SaCheckLogin
    public Map<String, Object> myWorkload() {
        Map<String, Object> todoList = iDcimsGlobalSettingService.getTodoList();
        System.out.println(todoList);
        return todoList;
    }
}
