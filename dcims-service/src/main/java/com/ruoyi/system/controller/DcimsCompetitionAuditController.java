package com.ruoyi.system.controller;

import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.PermitGroup;
import com.ruoyi.common.core.validate.RefuseGroup;
import com.ruoyi.system.domain.bo.CompetitionPartialBo;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
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
     * 通过竞赛审核
     */
    @SaCheckPermission("dcims:competitionAudit:add")
    @Log(title = "竞赛审核", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(PermitGroup.class) @RequestBody List<DcimsCompetitionAuditBo> boList) {
        return toAjax(iDcimsCompetitionAuditService.insertByBo(boList));
    }

    /**
     * 驳回竞赛审核
     */
    @SaCheckPermission("dcims:competitionAudit:remove")
    @Log(title = "竞赛审核", businessType = BusinessType.DELETE)
    @DeleteMapping()
    public R<Void> remove(@Validated(RefuseGroup.class) @RequestBody List<DcimsCompetitionAuditBo> boList) {
        return toAjax(iDcimsCompetitionAuditService.deleteWithValidByIds(boList));
    }

    /**
     * 修改竞赛部分信息
     */
    @SaCheckPermission("dcims:competitionAudit:update")
    @Log(title = "团队获奖审核", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsCompetitionBo bo) {
        return toAjax(iDcimsCompetitionAuditService.updateByBoPartial(bo));
    }
}
