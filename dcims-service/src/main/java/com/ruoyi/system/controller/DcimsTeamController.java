package com.ruoyi.system.controller;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.io.IoUtil;
import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.excel.DcimsTeamImportExcel;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.vo.DcimsTeamVoV2;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.service.IDcimsGlobalSettingService;
import com.ruoyi.system.service.IDcimsTeamAuditService;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.service.IDcimsTeamService;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 参赛团队
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dcims/team")
public class DcimsTeamController extends BaseController {

    private final IDcimsTeamService iDcimsTeamService;
    private final IDcimsTeamAuditService iDcimsTeamAuditService;
    private final IDcimsGlobalSettingService globalSettingService;
    private final IDcimsCompetitionService competitionService;

    /**
     * 查询参赛团队列表
     */
    @SaCheckPermission("dcims:team:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsTeamVoV2> list(DcimsTeamBo bo, PageQuery pageQuery) {
        return iDcimsTeamService.queryPageList(bo, pageQuery);
    }

    /**
     * 根据教师工号查询参赛团队列表
     */
    @SaCheckPermission("dcims:team:list")
    @GetMapping("/listTeamByTeacherId")
    public TableDataInfo<DcimsTeamVoV2> listTeamByTeacherId(DcimsTeamBo bo, PageQuery pageQuery) {
        return iDcimsTeamService.queryPageListByTeacherId(bo, pageQuery);
    }

    /**
     * 查询待审核竞赛列表
     */
    @SaCheckPermission("dcims:team:list")
    @GetMapping("/audit/list")
    public TableDataInfo<DcimsTeamVoV2> listByTeacherId(DcimsTeamAuditBo bo, PageQuery pageQuery) {
        return iDcimsTeamAuditService.queryPageListAudit(bo, pageQuery);
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
     * 批量下载附件
     */
    @SaCheckPermission("dcims:team:export")
    @Log(title = "参赛团队", businessType = BusinessType.EXPORT)
    @PostMapping("/download")
    public void downloadAttachment(DcimsTeamBo bo, HttpServletResponse response) {
        try {
            iDcimsTeamService.download(bo,response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取参赛团队详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("dcims:team:query")
    @GetMapping("/{id}")
    public R<DcimsTeamVoV2> getInfo(@NotNull(message = "主键不能为空")
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
        // 判断是否在截止日期内
        DcimsCompetitionVo competitionVo = competitionService.queryById(bo.getCompetitionId());
        boolean flag = globalSettingService.isDeadline(String.valueOf(competitionVo.getAnnual()), "team");
        if (!flag)
            return R.fail(bo.getAnnual()+"年团队获奖申报已截止（或未开放），如有疑问请联系学院或教务处。");
        return toAjax(iDcimsTeamService.insertByBo(bo));
    }

    /**
     * 修改参赛团队
     */
    @SaCheckPermission("dcims:team:edit")
    @Log(title = "参赛团队", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/put")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody DcimsTeamBo bo) {
        // 判断是否在截止日期内
        DcimsCompetitionVo competitionVo = competitionService.queryById(bo.getCompetitionId());
        boolean flag = globalSettingService.isDeadline(String.valueOf(competitionVo.getAnnual()), "team");
        if (!flag)
            return R.fail(bo.getAnnual()+"年团队获奖申报已截止（或未开放），如有疑问请联系学院或教务处。");
        return toAjax(iDcimsTeamService.updateByBo(bo));
    }

    /**
     * 为参赛团队添加佐证材料
     */
    @SaCheckPermission("dcims:team:edit")
    @Log(title = "参赛团队", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/award/put")
    public R<Void> addAward(@Validated(EditGroup.class) @RequestBody DcimsDeclareAwardBo bo) {
        return toAjax(iDcimsTeamService.declareAwardByBo(bo));
    }

    /**
     * 删除参赛团队
     *
     * @param ids 主键串
     */
    @SaCheckPermission("dcims:team:remove")
    @Log(title = "参赛团队", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(iDcimsTeamService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 获取批量导入模板
     */
    @SaIgnore
    @PostMapping("/importTemplate")
    public void importTemplate(@NotNull(message = "请选择模板对应的年份") Integer annual, HttpServletResponse response) {
        try {
            File file = iDcimsTeamService.getImportTemplate(annual);
            InputStream inputStream = new FileInputStream(file);
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("竞赛信息管理系统获奖导入模板.xlsx", StandardCharsets.UTF_8.toString()));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=utf-8");
            IoUtil.copy(inputStream, response.getOutputStream());
            response.setContentLength((int) file.length());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传批量导入压缩文件，返回文件内的数据
     */
    @SaIgnore
    @PostMapping("/uploadTemplate")
    public Map<String, Object> uploadTemplate(@RequestPart MultipartFile file) throws IOException {
        List<DcimsTeamImportExcel> importList = iDcimsTeamService.readDataFromTemplate(file.getInputStream());
        return iDcimsTeamService.saveDataToRedis(importList);
    }

    /**
     * 手动修改后的批量导入信息
     */
    @SaIgnore
    @PostMapping("/editImportData")
    public Map<String, Object> editImportData(String id, @RequestBody List<DcimsTeamImportExcel> importTeamData){
        return iDcimsTeamService.editImportData(id, importTeamData);
    }

    /**
     * 追加批量导入信息
     */
    @SaIgnore
    @PostMapping("/appendImportData")
    public Map<String, Object> appendImportData(String id, String type, @RequestPart MultipartFile file) throws IOException {
        return iDcimsTeamService.appendImportData(id, type, file.getInputStream());
    }

    /**
     * 确认批量导入信息，保存进入数据库
     */
    @SaIgnore
    @PostMapping("/submitImportData")
    public R<Boolean> submitImportData(String id){
        return iDcimsTeamService.submitImportData(id) ? R.ok("成功") : R.fail("失败");
    }
}
