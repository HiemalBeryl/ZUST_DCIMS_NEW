package com.ruoyi.system.controller;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import java.util.*;
import java.util.stream.Collectors;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.service.DeptService;
import com.ruoyi.common.utils.JsonUtils;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.DcimsTeamWithCompetition;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.excel.DcimsComAndTeam;
import com.ruoyi.system.domain.excel.DcimsTeamImportExcel;
import com.ruoyi.system.domain.excel.TempDcimsComAndTeam;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.vo.DcimsTeamVoV2;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.service.IDcimsGlobalSettingService;
import com.ruoyi.system.service.IDcimsTeamAuditService;
import com.ruoyi.system.utils.AccountUtils;
import com.ruoyi.system.utils.WordUtil;
import io.swagger.v3.core.util.Json;
import com.ruoyi.system.service.*;
import lombok.RequiredArgsConstructor;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.apache.commons.compress.archivers.ArchiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.http.ResponseEntity;
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
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.boot.actuate.health.Health.down;

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

    private static final Logger log = LoggerFactory.getLogger(DcimsTeamController.class);
    private final IDcimsTeamService iDcimsTeamService;
    private final IDcimsTeamAuditService iDcimsTeamAuditService;
    private final IDcimsGlobalSettingService globalSettingService;
    private final IDcimsCompetitionService competitionService;
    private final ISysDictDataService dictDataService;
    private final ISysDeptService deptService;

    /**
     * 查询参赛团队列表
     */
    @SaCheckPermission("dcims:team:list")
    @GetMapping("/list")
    public TableDataInfo<DcimsTeamVoV2> list(DcimsTeamBo bo, PageQuery pageQuery) {
        TableDataInfo<DcimsTeamVoV2> queryresult = iDcimsTeamService.queryPageList(bo, pageQuery);
        if (bo.getAnnual() == null) {
            return queryresult;
        }
        TableDataInfo<DcimsTeamVoV2> returnResult = TableDataInfo.build(
            queryresult.getRows().stream()
                .filter(e -> bo.getAnnual().intValue() == e.getCompetition().getAnnual().intValue()).collect(Collectors.toList())
        );

        returnResult.setTotal(queryresult.getTotal());
        return returnResult;
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
    public TableDataInfo<DcimsTeamVoV2> listByTeacherId(DcimsTeamAuditBo bo, PageQuery pageQuery, Integer annual) {
        TableDataInfo<DcimsTeamVoV2> result = iDcimsTeamAuditService.queryPageListAudit(bo, pageQuery);
        if(annual != null){
            TableDataInfo<DcimsTeamVoV2> returnResult = TableDataInfo.build(
                result.getRows().stream()
                    .filter(e -> e.getCompetition().getAnnual().intValue() == annual.intValue())
                    .collect(Collectors.toList())
            );
            returnResult.setTotal(result.getTotal());
            return returnResult;
        }
        return result;
    }

    /**
     * 根据登录用户对应教师工号，查询正在审核中的竞赛列表
     */
    @SaCheckPermission("dcims:team:list")
    @GetMapping("/audit/listInProcessing")
    public TableDataInfo<DcimsTeamVoV2> listByTeacherIdInAuditProcessing(DcimsTeamBo bo, PageQuery pageQuery) {
        // 本接口只查询最新的30个
        pageQuery.setPageSize(50000);
        TableDataInfo<DcimsTeamVoV2> info = iDcimsTeamService.queryPageList(new DcimsTeamBo(), pageQuery);
        // 对结果筛选，只返回正在审核中和被退回的竞赛，同时应该限制下一级审核人的工号为教务处老师
        List<DcimsTeamVoV2> result = info.getRows().stream()
            .filter(e -> e.getAudit() != null && (e.getAudit() == 1 || e.getAudit() == 3))
            .filter(e -> e.getNextAuditId() != null && (e.getNextAuditId() == 102099 || e.getNextAuditId( )== -1))
            .collect(Collectors.toList());
        // 如果当前登录账号为教务处，那么只显示被退回的竞赛
        if(StrUtil.equals(getUsername(), "102099")){
            result = result.stream()
                .filter(e -> e.getAudit().equals(3))
                .collect(Collectors.toList());
        }else{
            // 如果不是教务处，则需要根据学院筛选
            Long teacherId = AccountUtils.getAccount().getTeacherId();
            List<SysDept> sysDepts = deptService.selectDeptList(new SysDept());
            Optional<SysDept> firstDept = sysDepts.stream().filter(dept -> dept.getLeaderTeacherId().equals(teacherId)).findFirst();
            Integer teacherCollege = null;
            if (firstDept.isPresent()){
                teacherCollege = firstDept.get().getOrderNum();
            }
            Integer finalTeacherCollege = teacherCollege;
            result = result.stream()
                .filter(e -> Objects.equals(finalTeacherCollege, e.getCompetition().getCollege().intValue()))
                .collect(Collectors.toList());
        }
        return TableDataInfo.build(result);
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

    /*
    * 导出参赛团队word
    * */
    @SaCheckPermission("dcims:team:export")
    @PostMapping("/exportTeamWord")
    public void exportWord(DcimsTeamBo bo, HttpServletResponse response) {
        HashMap<String, Object> map =iDcimsTeamService.queryAward(bo);
        WordUtil.exportWordByModel(response,map, "word/studentProvincialAwardsImportTeam.docx");

    }

    /**
     *  导出省级以上获奖情况学生登记表
     */
    @SaCheckPermission("dcims:team:export")
//    @SaIgnore
    @Log(title = "参赛团队", businessType = BusinessType.EXPORT)
    @PostMapping("/exportDengjiBiao")
    public void exportDengjiBiao(DcimsTeamBo bo, HttpServletResponse response) throws IOException {
        List<DcimsTeamWithCompetition> result = iDcimsTeamAuditService.queryListWithCompetition(bo);
//        OutputStream os = iDcimsTeamAuditService.getHuoJiangBiao(result);
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("dcims_award_level");
        List<SysDictData> awardDict = dictDataService.selectDictDataList(sysDictData);

        List<TempDcimsComAndTeam> tempResult = new ArrayList<>();
        result.forEach(e -> {
            TempDcimsComAndTeam t = new TempDcimsComAndTeam();
            BeanUtils.copyProperties(e, t);
            tempResult.add(t);
        });
        List<TempDcimsComAndTeam> data1 = tempResult.stream().filter(e -> Integer.parseInt(e.getAwardLevel()) <= 9).collect(Collectors.toList());


        ExcelUtil.exportExcel(data1, "参赛团队", TempDcimsComAndTeam.class, response);

    }


    /**
     * 导出省级以上获奖情况汇总表
     */
    @SaCheckPermission("dcims:team:export")
//    @SaIgnore
    @Log(title = "参赛团队", businessType = BusinessType.EXPORT)
    @PostMapping("/exportHuiZongBiao")
    public void exportHuiZongBiao(DcimsTeamBo bo, HttpServletResponse response){
        List<DcimsTeamWithCompetition> result = iDcimsTeamAuditService.queryListWithCompetition(bo);
        Map<Long, List<DcimsTeamWithCompetition>> separatedCompetition = new HashMap<>();
        for(DcimsTeamWithCompetition r : result){
            separatedCompetition.computeIfAbsent(r.getCompetitionId(), k -> new ArrayList<>()).add(r);
//            List<DcimsTeamWithCompetition> list = separatedCompetition.get(r.getCompetitionId());
//            if (ObjectUtil.isNull(list)){
//                ArrayList<DcimsTeamWithCompetition> arr = new ArrayList<>();
//                arr.add(r);
//                separatedCompetition.put(r.getCompetitionId(), arr);
//            }else {
//                list.add(r);
//            }
        }
        System.out.println(separatedCompetition);


        List<DcimsComAndTeam> excelDataList = new ArrayList<>();
        for (List<DcimsTeamWithCompetition> value : separatedCompetition.values()){
            System.out.println(value.size());
            DcimsComAndTeam excelData = new DcimsComAndTeam();
            BeanUtils.copyProperties(value.get(0), excelData);
            excelData.setCompetitionName(value.get(0).getName());
            excelData.setCompetitionType(value.get(0).getLevel());
            excelData.setCollege(String.valueOf(value.get(0).getCollege()));
            excelDataList.add(excelData);

            for (DcimsTeamWithCompetition com : value){
//                String teacherNames = com.getTeacherName();
                String studentNames = com.getStudentName();
//                int teacherNumber = teacherNames.split(",").length;
                int studentNumber = studentNames.split(",").length;


                int awardLevel = -1;
                switch(com.getAwardLevel()){
                    case "15":
                        awardLevel = 10;
                        break;
                    case "16":
                        awardLevel = 11;
                        break;
                    case "18":
                        awardLevel = 12;
                        break;
                    case "19":
                        awardLevel = 13;
                        break;
                    case "20":
                        awardLevel = 14;
                        break;
                    default:
                        awardLevel = Integer.parseInt(com.getAwardLevel());
                }
                if (!(awardLevel >= 1 && awardLevel <= 14)){
                    continue;
                }


                Method getCountMethod = ReflectUtil.getMethod(DcimsComAndTeam.class, "getHuojiangCount" + awardLevel);
                Method getNumberMethod = ReflectUtil.getMethod(DcimsComAndTeam.class, "getHuojiangNumber" + awardLevel);
                Method setCountMethod = ReflectUtil.getMethod(DcimsComAndTeam.class, "setHuojiangCount" + awardLevel, int.class);
                Method setNumberMethod = ReflectUtil.getMethod(DcimsComAndTeam.class, "setHuojiangNumber" + awardLevel, int.class);
                Object count = ReflectUtil.invoke(excelData, getCountMethod);
                Object number = ReflectUtil.invoke(excelData, getNumberMethod);
                int newCount = (int) count + 1;
                int newNumber = (int) number + studentNumber;
                ReflectUtil.invoke(excelData, setCountMethod, newCount);
                ReflectUtil.invoke(excelData, setNumberMethod, newNumber);
            }
        }


        for (DcimsComAndTeam excelData : excelDataList){
            int allCount = 0;
            int allNumber = 0;
            for (int i = 1; i <= 14; i++){
                Method getCountMethod = ReflectUtil.getMethod(DcimsComAndTeam.class, "getHuojiangCount" + i);
                Method getNumberMethod = ReflectUtil.getMethod(DcimsComAndTeam.class, "getHuojiangNumber" + i);
                Object count = ReflectUtil.invoke(excelData, getCountMethod);
                Object number = ReflectUtil.invoke(excelData, getNumberMethod);
                allCount += ObjectUtil.isNull(count) ? 0 : (int) count;
                allNumber += ObjectUtil.isNull(number) ? 0 : (int) number;
            }
            excelData.setTotalHuojiangNumber(allCount);
            excelData.setTotalHuojiangCount(allNumber);
        }


        // 只展示本学院的竞赛
        Long collegeId = AccountUtils.getCollegeId();
        System.out.println("collegeId:"+collegeId);
        // 表示该教师有所属学院且不是教务处
        if(collegeId != null && collegeId != 0){
            excelDataList = excelDataList.stream()
                .filter(e -> e.getCollege().equals(String.valueOf(collegeId)))
                .collect(Collectors.toList());
        }


        // 最后对所有竞赛按照学院排序，输出序号
        System.out.println(excelDataList);
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("dcims_college");
        List<SysDictData> collegeDict = dictDataService.selectDictDataList(sysDictData);
        excelDataList = excelDataList.stream().sorted(Comparator.comparing(DcimsComAndTeam::getCollege).thenComparing(DcimsComAndTeam::getCompetitionType)).collect(Collectors.toList());
        int order = 1;
        for(DcimsComAndTeam excelData :excelDataList){
            System.out.println(excelData.getCollege());
            excelData.setOrder(order++);
            if (StringUtils.isNotBlank(excelData.getCollege())){
                excelData.setCollege(
                    collegeDict.stream().filter(d -> StringUtils.equals(d.getDictValue(), excelData.getCollege()))
                        .findFirst().get().getDictLabel()
                );
            }
        }


        System.out.println(excelDataList);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("exportYear", bo.getAnnual() != null ? bo.getAnnual(): "xxxx");
        ExcelUtil.exportTemplate(CollUtil.newArrayList(map, excelDataList), "获奖汇总表", "excel/获奖汇总表.xlsx", response);

    }


    /**
     * 批量下载附件
     */
    @SaCheckPermission("dcims:team:export")
    @Log(title = "参赛团队", businessType = BusinessType.EXPORT)
    @PostMapping("/download")
    public void downloadAttachment(DcimsTeamBo bo, HttpServletResponse response) {
        try {
            PageQuery pq = new PageQuery();
            pq.setPageSize(100000);
            TableDataInfo<DcimsTeamVoV2> downloadList = list(bo, pq);
            iDcimsTeamService.download(downloadList.getRows(), response);
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
            Files.deleteIfExists(file.toPath());
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
    public Map<String, Object> uploadTemplate(@RequestPart MultipartFile file) throws IOException, ArchiveException {
        List<DcimsTeamImportExcel> importList = iDcimsTeamService.readDataFromTemplate(file.getInputStream());
        return iDcimsTeamService.saveDataToRedis(importList);
    }

    /**
     * 手动修改后的批量导入信息
     */
    @SaIgnore
    @PostMapping("/editImportData")
    public Map<String, Object> editImportData(String id, @RequestBody List<DcimsTeamImportExcel> importTeamData){
        System.out.println(importTeamData);
        return iDcimsTeamService.editImportData(id, importTeamData);
    }

    /**
     * 追加批量导入信息
     */
    @SaIgnore
    @PostMapping("/appendImportData")
    public Map<String, Object> appendImportData(String id, String type, @RequestPart MultipartFile file) throws IOException, ArchiveException {
        return iDcimsTeamService.appendImportData(id, type, file.getInputStream());
    }

    /**
     * 确认批量导入信息，保存进入数据库
     */
    @SaIgnore
    @PostMapping("/submitImportData")
    public R<Boolean> submitImportData(String id){
        return iDcimsTeamService.submitImportData(id) ? R.ok("成功") : R.fail("操作失败，请检查表格中的数据是否有误，请修改后重新提交");
    }
}
