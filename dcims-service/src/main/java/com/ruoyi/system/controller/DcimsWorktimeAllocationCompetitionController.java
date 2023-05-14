package com.ruoyi.system.controller;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
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
import com.ruoyi.system.domain.vo.DcimsWorktimeAllocationCompetitionVo;
import com.ruoyi.system.domain.bo.DcimsWorktimeAllocationCompetitionBo;
import com.ruoyi.system.service.IDcimsWorktimeAllocationCompetitionService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工作量分配竞赛
 *
 * @author hiemalberyl
 * @date 2023-05-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/worktimeAllocationCompetition")
public class DcimsWorktimeAllocationCompetitionController extends BaseController {

    private final IDcimsWorktimeAllocationCompetitionService iDcimsWorktimeAllocationCompetitionService;

    /*
     * 查询工作量分配竞赛列表
     */
    @SaCheckPermission("dcims:worktimeAllocationCompetition:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsWorktimeAllocationCompetitionVo> list(DcimsWorktimeAllocationCompetitionBo bo, PageQuery pageQuery) {
        return iDcimsWorktimeAllocationCompetitionService.queryPageList(bo, pageQuery);
    }

    /*
     * 根据用户工号查询工作量分配竞赛列表
     */
    @SaCheckPermission("dcims:worktimeAllocationCompetition:list")
    @GetMapping("/listByTeacherId")
    public TableDataInfo<DcimsWorktimeAllocationCompetitionVo> listByTeacherId() {
        return iDcimsWorktimeAllocationCompetitionService.queryPageListByTeacherId();
    }

    /**
     * 导出工作量分配竞赛列表
     */
    @SaCheckPermission("dcims:worktimeAllocationCompetition:export")
    @Log(title = "工作量分配竞赛", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(DcimsWorktimeAllocationCompetitionBo bo, HttpServletResponse response) {
        List<DcimsWorktimeAllocationCompetitionVo> list = iDcimsWorktimeAllocationCompetitionService.queryList(bo);
        ExcelUtil.exportExcel(list, "工作量分配竞赛", DcimsWorktimeAllocationCompetitionVo.class, response);
    }

    /**
     * 获取工作量分配竞赛详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:worktimeAllocationCompetition:query")
    @GetMapping("/{id}")
    public R<DcimsWorktimeAllocationCompetitionVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(iDcimsWorktimeAllocationCompetitionService.queryById(id));
    }

    /**
     * 新增工作量分配竞赛
     */
    @SaCheckPermission("dcims:worktimeAllocationCompetition:add")
    @Log(title = "工作量分配竞赛", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody DcimsWorktimeAllocationCompetitionBo bo) {
        return toAjax(iDcimsWorktimeAllocationCompetitionService.insertByBo(bo));
    }

    /**
     * 修改工作量分配竞赛
     */
    @SaCheckPermission("dcims:worktimeAllocationCompetition:edit")
    @Log(title = "工作量分配竞赛", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsWorktimeAllocationCompetitionBo bo) {
        return toAjax(iDcimsWorktimeAllocationCompetitionService.updateByBo(bo));
    }

    /**
     * 删除工作量分配竞赛
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:worktimeAllocationCompetition:remove")
    @Log(title = "工作量分配竞赛", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsWorktimeAllocationCompetitionService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
