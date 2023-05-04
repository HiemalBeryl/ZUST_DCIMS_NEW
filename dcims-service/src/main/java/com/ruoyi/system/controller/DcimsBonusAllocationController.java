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
import com.ruoyi.system.domain.vo.DcimsBonusAllocationVo;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationBo;
import com.ruoyi.system.service.IDcimsBonusAllocationService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 奖金分配总
 *
 * @author Andy
 * @date 2023-05-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/bonusAllocation")
public class DcimsBonusAllocationController extends BaseController {

    private final IDcimsBonusAllocationService iDcimsBonusAllocationService;

    /**
     * 查询奖金分配总列表
     */
    @SaCheckPermission("dcims:bonusAllocation:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsBonusAllocationVo> list(DcimsBonusAllocationBo bo, PageQuery pageQuery) {
        return iDcimsBonusAllocationService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出奖金分配总列表
     */
    @SaCheckPermission("dcims:bonusAllocation:export")
    @Log(title = "奖金分配总", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsBonusAllocationBo bo, HttpServletResponse response) {
        List<DcimsBonusAllocationVo> list = iDcimsBonusAllocationService.queryList(bo);
        ExcelUtil.exportExcel(list, "奖金分配总", DcimsBonusAllocationVo.class, response);
    }

    /**
     * 获取奖金分配总详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:bonusAllocation:query")
    @GetMapping("/{id}")
    public R<DcimsBonusAllocationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsBonusAllocationService.queryById(id));
    }

    /**
     * 新增奖金分配总
     */
    @SaCheckPermission("dcims:bonusAllocation:add")
    @Log(title = "奖金分配总", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsBonusAllocationBo bo) {
        return toAjax(iDcimsBonusAllocationService.insertByBo(bo));
    }

    /**
     * 修改奖金分配总
     */
    @SaCheckPermission("dcims:bonusAllocation:edit")
    @Log(title = "奖金分配总", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsBonusAllocationBo bo) {
        return toAjax(iDcimsBonusAllocationService.updateByBo(bo));
    }

    /**
     * 删除奖金分配总
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:bonusAllocation:remove")
    @Log(title = "奖金分配总", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsBonusAllocationService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}