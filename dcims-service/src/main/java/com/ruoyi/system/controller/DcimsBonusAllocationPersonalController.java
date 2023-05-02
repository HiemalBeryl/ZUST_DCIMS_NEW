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
import com.ruoyi.system.domain.vo.DcimsBonusAllocationPersonalVo;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationPersonalBo;
import com.ruoyi.system.service.IDcimsBonusAllocationPersonalService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 奖金分配个人
 *
 * @author ruoyi
 * @date 2023-05-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/bonusAllocationPersonal")
public class DcimsBonusAllocationPersonalController extends BaseController {

    private final IDcimsBonusAllocationPersonalService iDcimsBonusAllocationPersonalService;

    /**
     * 查询奖金分配个人列表
     */
    @SaCheckPermission("system:bonusAllocationPersonal:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsBonusAllocationPersonalVo> list(DcimsBonusAllocationPersonalBo bo, PageQuery pageQuery) {
        return iDcimsBonusAllocationPersonalService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出奖金分配个人列表
     */
    @SaCheckPermission("system:bonusAllocationPersonal:export")
    @Log(title = "奖金分配个人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsBonusAllocationPersonalBo bo, HttpServletResponse response) {
        List<DcimsBonusAllocationPersonalVo> list = iDcimsBonusAllocationPersonalService.queryList(bo);
        ExcelUtil.exportExcel(list, "奖金分配个人", DcimsBonusAllocationPersonalVo.class, response);
    }

    /**
     * 获取奖金分配个人详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:bonusAllocationPersonal:query")
    @GetMapping("/{id}")
    public R<DcimsBonusAllocationPersonalVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsBonusAllocationPersonalService.queryById(id));
    }

    /**
     * 新增奖金分配个人
     */
    @SaCheckPermission("system:bonusAllocationPersonal:add")
    @Log(title = "奖金分配个人", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsBonusAllocationPersonalBo bo) {
        return toAjax(iDcimsBonusAllocationPersonalService.insertByBo(bo));
    }

    /**
     * 修改奖金分配个人
     */
    @SaCheckPermission("system:bonusAllocationPersonal:edit")
    @Log(title = "奖金分配个人", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsBonusAllocationPersonalBo bo) {
        return toAjax(iDcimsBonusAllocationPersonalService.updateByBo(bo));
    }

    /**
     * 删除奖金分配个人
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:bonusAllocationPersonal:remove")
    @Log(title = "奖金分配个人", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsBonusAllocationPersonalService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
