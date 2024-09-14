package com.ruoyi.system.controller;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsGlobalSetting;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import com.ruoyi.system.service.IDcimsCompetitionAuditService;
import com.ruoyi.system.service.IDcimsGlobalSettingService;
import com.ruoyi.system.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 竞赛赛事基本信息
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/competition")
public class DcimsCompetitionController extends BaseController {

    private final IDcimsCompetitionService iDcimsCompetitionService;
    private final IDcimsCompetitionAuditService iDcimsCompetitionAuditService;
    private final ISysDeptService deptService;
    private final IDcimsGlobalSettingService globalSettingService;

    /**
     * 查询竞赛赛事基本信息列表
     */
    @SaCheckPermission("dcims:competition:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsCompetitionVo> list(DcimsCompetitionBo bo, PageQuery pageQuery) {
        return iDcimsCompetitionService.queryPageList(bo, pageQuery, true, false);
    }

    /**
     * 根据登录用户对应教师工号，查询竞赛赛事基本信息列表
     */
    @SaCheckPermission("dcims:competition:listByTeacherId")
    @GetMapping("/listByTeacherId")
    public TableDataInfo<DcimsCompetitionVo> listByTeacherId(DcimsCompetitionBo bo, PageQuery pageQuery) {
        return iDcimsCompetitionService.queryPageListByTeacherId(bo, pageQuery);
    }

    /**
     * 根据登录用户对应教师工号，查询正在审核中的竞赛列表
     */
    @SaCheckPermission("dcims:competition:listByTeacherId")
    @GetMapping("/audit/listInProcessing")
    public TableDataInfo<DcimsCompetitionVo> listByTeacherIdInAuditProcessing(DcimsCompetitionBo bo, PageQuery pageQuery) {
        // 本接口只查询最新的30个
        pageQuery.setPageSize(2000);
        TableDataInfo<DcimsCompetitionVo> info = iDcimsCompetitionService.queryPageList(bo, pageQuery, false, false);
        // 对结果筛选，只返回正在审核中和被退回的竞赛，同时应该限制下一级审核人的工号为教务处老师
        List<DcimsCompetitionVo> result = info.getRows().stream()
            .filter(e -> e.getState().equals("0") || e.getState().equals("2"))
            .filter(e -> e.getNextAuditId().equals(102099L) || e.getNextAuditId().equals(-1L))
            .collect(Collectors.toList());
        // 如果当前登录账号为教务处，那么只显示被退回的竞赛
        if(StrUtil.equals(getUsername(), "102099")){
            result = result.stream()
                .filter(e -> e.getState().equals("2"))
                .collect(Collectors.toList());
        }
        return TableDataInfo.build(result);
    }


    /**
     * 查询待审核竞赛列表
     */
    @SaCheckPermission("dcims:competition:list")
    @GetMapping("/audit/list")
    public TableDataInfo<DcimsCompetitionVo> list(DcimsCompetitionAuditBo bo, PageQuery pageQuery) {
        TableDataInfo<DcimsCompetitionVo> dcimsCompetitionVoTableDataInfo = iDcimsCompetitionAuditService.queryPageList(bo, pageQuery);
        dcimsCompetitionVoTableDataInfo = TableDataInfo.build(
            dcimsCompetitionVoTableDataInfo
                .getRows().stream()
                .sorted(Comparator.comparing(DcimsCompetitionVo::getCollege))
                .collect(Collectors.toList())
        );
        return dcimsCompetitionVoTableDataInfo;
    }

    /**
     * 导出竞赛赛事基本信息列表
     */
    @SaCheckPermission("dcims:competition:export")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsCompetitionBo bo, HttpServletResponse response) {
        List<DcimsCompetitionVo> list = iDcimsCompetitionService.queryList(bo);
        ExcelUtil.exportExcel(list, "竞赛赛事基本信息", DcimsCompetitionVo.class, response);
    }

    /**
     * 导出立项汇总表
     */
    @SaCheckPermission("dcims:competition:export")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/exportByType")
    public void exportByType(DcimsCompetitionBo bo, HttpServletResponse response) {
        PageQuery pq = new PageQuery();
        pq.setPageSize(1000);
        pq.setPageNum(1);
        TableDataInfo<DcimsCompetitionVo> list = iDcimsCompetitionService.queryPageList(bo, pq, true, true);
        HashMap<Object, Object> map = new HashMap<>();
        if (bo.getAnnual() != null){
            map.put("year", bo.getAnnual());
        }else{
            map.put("year", "20xx");
        }
        ExcelUtil.exportTemplate(CollUtil.newArrayList(map, list.getRows()), "浙江科技学院大学生科技竞赛项目申报汇总表", "excel/竞赛项目申报汇总表-模板.xlsx", response);
    }

    /**
     * 获取竞赛赛事基本信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:competition:query")
    @GetMapping("/{id}")
    public R<DcimsCompetitionVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsCompetitionService.queryById(id));
    }

    /**
     * 新增竞赛赛事基本信息
     */
    @SaCheckPermission("dcims:competition:add")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsCompetitionBo bo) {
        // 判断是否在截止日期内
        boolean flag = globalSettingService.isDeadline(String.valueOf(bo.getAnnual()), "competition");
        if (!flag)
            return R.fail(bo.getAnnual()+"年竞赛立项申报已截止（或未开放），如有疑问请联系学院或教务处。");
        return toAjax(iDcimsCompetitionService.insertByBo(bo));
    }

    /**
     * 修改竞赛赛事基本信息
     */
    @SaCheckPermission("dcims:competition:edit")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/put")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsCompetitionBo bo) {
        return toAjax(iDcimsCompetitionService.updateByBo(bo, true));
    }

    /**
     * 删除竞赛赛事基本信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:competition:remove")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsCompetitionService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 获取竞赛对应指导教师
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:competition:getTutor")
    @GetMapping("/tutor/{id}")
    public TableDataInfo<DcimsTeacherVo> getTutorList(@NotNull(message = "主键不能为空")
                                         @PathVariable Long id) {
        return iDcimsCompetitionService.getTutorList(id);
    }

    /**
     * 添加多个指导教师
     *
     *
     */
    @SaCheckPermission("dcims:competition:addTutor")
    @PostMapping("/tutor/competitionId/{competitionId}/teacherIds/{teacherIds}")
    public R<Void> addTutor(@NotNull(message = "所属竞赛不能为空")
                            @PathVariable Long competitionId,
                            @NotEmpty(message = "教师工号不能为空")
                            @PathVariable Long[] teacherIds){
        return toAjax(iDcimsCompetitionService.addTutor(competitionId, teacherIds));
    }

    /**
     * 根据id删除指导教师
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:competition:removeTutor")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.DELETE)
    @PostMapping("/tutor/delete/{id}")
    public R<Void> removeTutor(@NotNull(message = "主键不能为空")
                               @PathVariable Long id){
        return toAjax(iDcimsCompetitionService.removeTutor(id));
    }


    /**
     * 批量下载竞赛立项申报书附件
     */
    @SaCheckPermission("dcims:competition:export")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/download")
    public void download(DcimsCompetitionBo bo, HttpServletResponse response) {
        try {
            iDcimsCompetitionService.download(bo,response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 批量下载集中授课表
     */
    @SaCheckPermission("dcims:competition:export")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/download2")
    public void download2(DcimsCompetitionBo bo, HttpServletResponse response) {
        try {
            iDcimsCompetitionService.download2(bo,response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
