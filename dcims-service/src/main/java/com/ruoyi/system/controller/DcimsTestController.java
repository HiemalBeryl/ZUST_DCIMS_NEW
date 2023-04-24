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
import com.ruoyi.system.domain.vo.DcimsTestVo;
import com.ruoyi.system.domain.bo.DcimsTestBo;
import com.ruoyi.system.service.IDcimsTestService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * test
 *
 * @author hiemalberyl
 * @date 2023-04-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/test")
public class DcimsTestController extends BaseController {

    private final IDcimsTestService iDcimsTestService;

    /**
     * 查询test列表
     */
    @SaCheckPermission("dcims:test:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsTestVo> list(DcimsTestBo bo, PageQuery pageQuery) {
        return iDcimsTestService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出test列表
     */
    @SaCheckPermission("dcims:test:export")
    @Log(title = "test", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsTestBo bo, HttpServletResponse response) {
        List<DcimsTestVo> list = iDcimsTestService.queryList(bo);
        ExcelUtil.exportExcel(list, "test", DcimsTestVo.class, response);
    }

    /**
     * 获取test详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:test:query")
    @GetMapping("/{id}")
    public R<DcimsTestVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsTestService.queryById(id));
    }

    /**
     * 新增test
     */
    @SaCheckPermission("dcims:test:add")
    @Log(title = "test", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsTestBo bo) {
        return toAjax(iDcimsTestService.insertByBo(bo));
    }

    /**
     * 修改test
     */
    @SaCheckPermission("dcims:test:edit")
    @Log(title = "test", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsTestBo bo) {
        return toAjax(iDcimsTestService.updateByBo(bo));
    }

    /**
     * 删除test
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:test:remove")
    @Log(title = "test", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsTestService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
