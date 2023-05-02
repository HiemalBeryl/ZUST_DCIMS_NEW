package com.ruoyi.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.PermitGroup;
import com.ruoyi.common.core.validate.RefuseGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.bo.DcimsCompetitionAuditBo;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.service.IDcimsTeamAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 团队获奖审核
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/teamAudit")
public class DcimsTeamAuditController extends BaseController {

    private final IDcimsTeamAuditService iDcimsTeamAuditService;

    /**
     * 通过竞赛审核
     */
    @SaCheckPermission("dcims:teamAudit:add")
    @Log(title = "团队获奖审核", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(PermitGroup.class) @RequestBody List<DcimsTeamAuditBo> boList) {
        return toAjax(iDcimsTeamAuditService.insertByBo(boList));
    }

    /**
     * 驳回竞赛审核
     */
    @SaCheckPermission("dcims:teamAudit:remove")
    @Log(title = "团队获奖审核", businessType = BusinessType.DELETE)
    @DeleteMapping()
    public R<Void> remove(@Validated(RefuseGroup.class) @RequestBody List<DcimsTeamAuditBo> boList) {
        return toAjax(iDcimsTeamAuditService.deleteWithValidByIds(boList));
    }
}
