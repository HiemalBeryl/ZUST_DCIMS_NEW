package com.ruoyi.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.bo.DcimsBonusAllocationPersonalBo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationPersonalVo;
import com.ruoyi.system.domain.vo.DcimsBonusAllocationVo;
import com.ruoyi.system.service.IDcimsBonusAllocationPersonalService;
import com.ruoyi.system.service.IDcimsBonusAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 奖金分配个人
 *
 * @author Andy
 * @date 2023-05-03
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/bonusAllocationPersonal")
public class DcimsBonusAllocationPersonalController extends BaseController {

    private final IDcimsBonusAllocationPersonalService iDcimsBonusAllocationPersonalService;

    private final IDcimsBonusAllocationService iDcimsBonusAllocationService;

    /**
     * 查询奖金分配个人列表
     */
    @SaCheckPermission("dcims:bonusAllocationPersonal:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsBonusAllocationPersonalVo> list(
        DcimsBonusAllocationPersonalBo bo,
        Long id,
        PageQuery pageQuery
    ) {
        return iDcimsBonusAllocationPersonalService.queryPageList(bo, pageQuery, id);
    }

    /**
     * 导出奖金分配个人列表
     */
    @SaCheckPermission("dcims:bonusAllocationPersonal:export")
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
    @SaCheckPermission("dcims:bonusAllocationPersonal:query")
    @GetMapping("/{id}")
    public R<DcimsBonusAllocationPersonalVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsBonusAllocationPersonalService.queryById(id));
    }

    /**
     * 新增奖金分配个人
     */
    @SaCheckPermission("dcims:bonusAllocationPersonal:add")
    @Log(title = "奖金分配个人", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsBonusAllocationPersonalBo bo) {
        return toAjax(iDcimsBonusAllocationPersonalService.insertByBo(bo));
    }

    /**
     * 修改奖金分配个人
     */
    @SaCheckPermission("dcims:bonusAllocationPersonal:edit")
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
    @SaCheckPermission("dcims:bonusAllocationPersonal:remove")
    @Log(title = "奖金分配个人", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsBonusAllocationPersonalService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 获取学院总金额
     *
     *
     */
    @SaCheckPermission("dcims:bonusAllocation:query")
    @GetMapping("/getTotal")
    public R<DcimsBonusAllocationVo> getTotalAmount(@RequestParam Long id) {
        return R.ok(iDcimsBonusAllocationService.getTotalAmount(id));
    }

}
