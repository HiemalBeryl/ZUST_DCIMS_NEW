package com.ruoyi.system.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import com.ruoyi.system.service.IDcimsCompetitionAuditService;
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

    /**
     * 查询竞赛赛事基本信息列表
     */
    @SaCheckPermission("dcims:competition:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsCompetitionVo> list(DcimsCompetitionBo bo, PageQuery pageQuery) {
        return iDcimsCompetitionService.queryPageList(bo, pageQuery);
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
     * 查询待审核竞赛列表
     */
    @SaCheckPermission("dcims:competition:list")
    @GetMapping("/audit/list")
    public TableDataInfo<DcimsCompetitionVo> list(DcimsCompetitionAuditBo bo, PageQuery pageQuery) {
        TableDataInfo<DcimsCompetitionVo> dcimsCompetitionVoTableDataInfo = iDcimsCompetitionAuditService.queryPageList(bo, pageQuery);
        System.out.println(dcimsCompetitionVoTableDataInfo);
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
        return toAjax(iDcimsCompetitionService.insertByBo(bo));
    }

    /**
     * 修改竞赛赛事基本信息
     */
    @SaCheckPermission("dcims:competition:edit")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
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
    @DeleteMapping("/{ids}")
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
    @DeleteMapping("/tutor/{id}")
    public R<Void> removeTutor(@NotNull(message = "主键不能为空")
                               @PathVariable Long id){
        return toAjax(iDcimsCompetitionService.removeTutor(id));
    }

}
