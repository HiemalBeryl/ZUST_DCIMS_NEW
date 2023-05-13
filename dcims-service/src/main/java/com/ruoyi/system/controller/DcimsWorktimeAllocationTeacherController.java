package com.ruoyi.system.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.ruoyi.system.domain.vo.AllocationCompetitionDictVo;
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
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationTeacherVo;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationTeacherBo;
import com.ruoyi.system.service.IDcimsWorktimeAllocationTeacherService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工作量分配
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/worktimeAllocationTeacher")
public class DcimsWorktimeAllocationTeacherController extends BaseController {

    private final IDcimsWorktimeAllocationTeacherService iDcimsWorktimeAllocationTeacherService;


    /**
     * 查询工作量分配竞赛列表
     */
    @SaCheckPermission("dcims:worktimeAllocationTeacher:list")
    @GetMapping("/competition")
    public TableDataInfo<AllocationCompetitionDictVo> list() {
        return iDcimsWorktimeAllocationTeacherService.queryCompetitionList();
    }


    /**
     * 查询工作量分配列表
     */
    @SaCheckPermission("dcims:worktimeAllocationTeacher:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsWorktimeAllocationTeacherVo> list(DcimsWorktimeAllocationTeacherBo bo, PageQuery pageQuery) {
        return iDcimsWorktimeAllocationTeacherService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出工作量分配列表
     */
    @SaCheckPermission("dcims:worktimeAllocationTeacher:export")
    @Log(title = "工作量分配", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsWorktimeAllocationTeacherBo bo, HttpServletResponse response) {
        List<DcimsWorktimeAllocationTeacherVo> list = iDcimsWorktimeAllocationTeacherService.queryList(bo);
        ExcelUtil.exportExcel(list, "工作量分配", DcimsWorktimeAllocationTeacherVo.class, response);
    }

    /**
     * 获取工作量分配详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:worktimeAllocationTeacher:query")
    @GetMapping("/{id}")
    public R<DcimsWorktimeAllocationTeacherVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsWorktimeAllocationTeacherService.queryById(id));
    }

    /**
     * 新增工作量分配
     */
    @SaCheckPermission("dcims:worktimeAllocationTeacher:add")
    @Log(title = "工作量分配", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsWorktimeAllocationTeacherBo bo) {
        return toAjax(iDcimsWorktimeAllocationTeacherService.insertByBo(bo));
    }

    /**
     * 修改工作量分配
     */
    @SaCheckPermission("dcims:worktimeAllocationTeacher:edit")
    @Log(title = "工作量分配", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsWorktimeAllocationTeacherBo bo) {
        return toAjax(iDcimsWorktimeAllocationTeacherService.updateByBo(bo));
    }

    /**
     * 删除工作量分配
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:worktimeAllocationTeacher:remove")
    @Log(title = "工作量分配", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsWorktimeAllocationTeacherService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
