package com.ruoyi.system.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.vo.DcimsStudentVo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import com.ruoyi.system.service.IDcimsBasicDataService;
import com.ruoyi.system.service.IDcimsCompetitionAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/basicData")
public class DcimsBasicDataController {

    private final IDcimsBasicDataService iDcimsBasicDataService;

    /**
     * 查询学生姓名学号字典
     */
    @SaCheckPermission("dcims:basicData:listStudentDict")
    @GetMapping("/listStudentDict")
    public TableDataInfo<DcimsStudentVo> listStudentDict(String name) {
        return iDcimsBasicDataService.listStudentDict(name);
    }

    /**
     * 查询教师姓名工号字典
     */
    @SaCheckPermission("dcims:basicData:listTeacherDict")
    @GetMapping("/listTeacherDict")
    public TableDataInfo<DcimsTeacherVo> listTeacherDict(String name) {
        return iDcimsBasicDataService.listTeacherDict(name);
    }
}
