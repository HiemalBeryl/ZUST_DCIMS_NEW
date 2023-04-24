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
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.service.IDcimsTeamService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 参赛团队
 *
 * @author hiemalberyl
 * @date 2023-04-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/team")
public class DcimsTeamController extends BaseController {

    private final IDcimsTeamService iDcimsTeamService;

    /**
     * 查询参赛团队列表
     */
    @SaCheckPermission("dcims:team:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsTeamVo> list(DcimsTeamBo bo, PageQuery pageQuery) {
        return iDcimsTeamService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出参赛团队列表
     */
    @SaCheckPermission("dcims:team:export")
    @Log(title = "参赛团队", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsTeamBo bo, HttpServletResponse response) {
        List<DcimsTeamVo> list = iDcimsTeamService.queryList(bo);
        ExcelUtil.exportExcel(list, "参赛团队", DcimsTeamVo.class, response);
    }

    /**
     * 获取参赛团队详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:team:query")
    @GetMapping("/{id}")
    public R<DcimsTeamVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsTeamService.queryById(id));
    }

    /**
     * 新增参赛团队
     */
    @SaCheckPermission("dcims:team:add")
    @Log(title = "参赛团队", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsTeamBo bo) {
        return toAjax(iDcimsTeamService.insertByBo(bo));
    }

    /**
     * 修改参赛团队
     */
    @SaCheckPermission("dcims:team:edit")
    @Log(title = "参赛团队", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsTeamBo bo) {
        return toAjax(iDcimsTeamService.updateByBo(bo));
    }

    /**
     * 删除参赛团队
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:team:remove")
    @Log(title = "参赛团队", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsTeamService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
