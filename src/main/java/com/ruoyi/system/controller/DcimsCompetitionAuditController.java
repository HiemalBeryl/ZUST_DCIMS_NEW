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
import com.ruoyi.system.domain.vo.DcimsCompetitionAuditVo;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.system.service.IDcimsCompetitionAuditService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 竞赛审核
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/competitionAudit")
public class DcimsCompetitionAuditController extends BaseController {

    private final IDcimsCompetitionAuditService iDcimsCompetitionAuditService;

    /**
     * 查询竞赛审核列表
     */
    @SaCheckPermission("dcims:competitionAudit:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsCompetitionAuditVo> list(DcimsCompetitionAuditBo bo, PageQuery pageQuery) {
        return iDcimsCompetitionAuditService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出竞赛审核列表
     */
    @SaCheckPermission("dcims:competitionAudit:export")
    @Log(title = "竞赛审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsCompetitionAuditBo bo, HttpServletResponse response) {
        List<DcimsCompetitionAuditVo> list = iDcimsCompetitionAuditService.queryList(bo);
        ExcelUtil.exportExcel(list, "竞赛审核", DcimsCompetitionAuditVo.class, response);
    }

    /**
     * 获取竞赛审核详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:competitionAudit:query")
    @GetMapping("/{id}")
    public R<DcimsCompetitionAuditVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsCompetitionAuditService.queryById(id));
    }

    /**
     * 新增竞赛审核
     */
    @SaCheckPermission("dcims:competitionAudit:add")
    @Log(title = "竞赛审核", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsCompetitionAuditBo bo) {
        return toAjax(iDcimsCompetitionAuditService.insertByBo(bo));
    }

    /**
     * 修改竞赛审核
     */
    @SaCheckPermission("dcims:competitionAudit:edit")
    @Log(title = "竞赛审核", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsCompetitionAuditBo bo) {
        return toAjax(iDcimsCompetitionAuditService.updateByBo(bo));
    }

    /**
     * 删除竞赛审核
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:competitionAudit:remove")
    @Log(title = "竞赛审核", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsCompetitionAuditService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
