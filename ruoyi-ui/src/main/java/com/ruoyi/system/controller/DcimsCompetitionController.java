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
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 竞赛赛事基本信息
 *
 * @author hiemalberyl
 * @date 2023-04-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/competition")
public class DcimsCompetitionController extends BaseController {

    private final IDcimsCompetitionService iDcimsCompetitionService;

    /**
     * 查询竞赛赛事基本信息列表
     */
    @SaCheckPermission("dcims:competition:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsCompetitionVo> list(DcimsCompetitionBo bo, PageQuery pageQuery) {
        return iDcimsCompetitionService.queryPageList(bo, pageQuery);
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
    @PostMapping("/put")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsCompetitionBo bo) {
        return toAjax(iDcimsCompetitionService.updateByBo(bo));
    }

    /**
     * 删除竞赛赛事基本信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:competition:remove")
    @Log(title = "竞赛赛事基本信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsCompetitionService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
