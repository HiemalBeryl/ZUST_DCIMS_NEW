package com.ruoyi.system.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationVo;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationBo;
import com.ruoyi.system.service.IDcimsWorktimeAllocationService;
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
@RequestMapping("/dcims/worktimeAllocation")
public class DcimsWorktimeAllocationController extends BaseController {

    private final IDcimsWorktimeAllocationService iDcimsWorktimeAllocationService;

    /**
     * 查询工作量分配列表
     */
    @SaCheckPermission("dcims:worktimeAllocation:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsWorktimeAllocationVo> list(DcimsWorktimeAllocationBo bo, PageQuery pageQuery) {
        return iDcimsWorktimeAllocationService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出工作量分配列表
     */
    @SaCheckPermission("dcims:worktimeAllocation:export")
    @Log(title = "工作量分配", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsWorktimeAllocationBo bo, HttpServletResponse response) {
        List<DcimsWorktimeAllocationVo> list = iDcimsWorktimeAllocationService.queryList(bo);
        ExcelUtil.exportExcel(list, "工作量分配", DcimsWorktimeAllocationVo.class, response);
    }

    /**
     * 获取工作量分配详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:worktimeAllocation:query")
    @GetMapping("/{id}")
    public R<DcimsWorktimeAllocationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsWorktimeAllocationService.queryById(id));
    }

    /**
     * 新增工作量分配
     */
    @SaCheckPermission("dcims:worktimeAllocation:add")
    @Log(title = "工作量分配", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsWorktimeAllocationBo bo) {
        return toAjax(iDcimsWorktimeAllocationService.insertByBo(bo));
    }

    /**
     * 修改工作量分配
     */
    @SaCheckPermission("dcims:worktimeAllocation:edit")
    @Log(title = "工作量分配", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsWorktimeAllocationBo bo) {
        return toAjax(iDcimsWorktimeAllocationService.updateByBo(bo));
    }

    /**
     * 删除工作量分配
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:worktimeAllocation:remove")
    @Log(title = "工作量分配", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsWorktimeAllocationService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
