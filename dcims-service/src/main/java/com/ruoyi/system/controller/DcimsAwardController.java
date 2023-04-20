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
import com.ruoyi.system.domain.vo.DcimsAwardVo;
import com.ruoyi.system.domain.bo.DcimsAwardBo;
import com.ruoyi.system.service.IDcimsAwardService;

/**
 * 获奖基本信息
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/award")
public class DcimsAwardController extends BaseController {

    private final IDcimsAwardService iDcimsAwardService;

    /**
     * 查询获奖基本信息列表
     */
    @SaCheckPermission("dcims:award:list")
    @GetMapping("/list")
    public R<List<DcimsAwardVo>> list(DcimsAwardBo bo) {
        List<DcimsAwardVo> list = iDcimsAwardService.queryList(bo);
        return R.ok(list);
    }

    /**
     * 导出获奖基本信息列表
     */
    @SaCheckPermission("dcims:award:export")
    @Log(title = "获奖基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsAwardBo bo, HttpServletResponse response) {
        List<DcimsAwardVo> list = iDcimsAwardService.queryList(bo);
        ExcelUtil.exportExcel(list, "获奖基本信息", DcimsAwardVo.class, response);
    }

    /**
     * 获取获奖基本信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:award:query")
    @GetMapping("/{id}")
    public R<DcimsAwardVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsAwardService.queryById(id));
    }

    /**
     * 新增获奖基本信息
     */
    @SaCheckPermission("dcims:award:add")
    @Log(title = "获奖基本信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsAwardBo bo) {
        return toAjax(iDcimsAwardService.insertByBo(bo));
    }

    /**
     * 修改获奖基本信息
     */
    @SaCheckPermission("dcims:award:edit")
    @Log(title = "获奖基本信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsAwardBo bo) {
        return toAjax(iDcimsAwardService.updateByBo(bo));
    }

    /**
     * 删除获奖基本信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:award:remove")
    @Log(title = "获奖基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsAwardService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
