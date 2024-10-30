package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.DcimsTeamAudit;
import com.ruoyi.system.domain.DcimsTeamWithCompetition;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.vo.DcimsTeamAuditVo;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.vo.DcimsTeamVoV2;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsTeamAuditMapper;
import com.ruoyi.system.mapper.DcimsTeamMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.service.IDcimsTeamAuditService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysOssService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 团队获奖审核Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-15
 */
@RequiredArgsConstructor
@Service
public class DcimsTeamAuditServiceImpl implements IDcimsTeamAuditService {

    private final DcimsTeamAuditMapper teamAuditBaseMapper;
    private final DcimsTeamMapper teamBaseMapper;
    private final SysDeptMapper sysDeptMapper;
    private final IDcimsCompetitionService competitionService;
    private final ISysOssService ossService;
    private final ISysDictTypeService dictTypeService;
    private final DcimsCompetitionMapper dcimsCompetitionMapper;

    /**
     * 查询团队获奖审核
     */
    @Override
    public DcimsTeamVo queryById(Long id){
        return teamBaseMapper.selectVoById(id);
    }

    /**
     * 查询团队获奖审核列表
     */
    @Override
    public TableDataInfo<DcimsTeamVoV2> queryPageListAudit(DcimsTeamAuditBo bo, PageQuery pageQuery) {
        // 自定义业务，获取当前登录账号对应的教师工号
        String id = StpUtil.getLoginIdAsString();
        String teacherId = AccountUtils.getAccount(id).getTeacherId().toString();
        LambdaQueryWrapper<DcimsTeam> lqw = new LambdaQueryWrapper<>();
        lqw.eq(teacherId != null&&teacherId != "", DcimsTeam::getNextAuditId,teacherId);
        // 获取状态为待审核或被退回的竞赛, 可以对这些竞赛重新提交或退回。
        List<String> status = new ArrayList<>();
        status.add("1");
        status.add("3");
        lqw.in(DcimsTeam::getAudit, status);
        Page<DcimsTeamVo> result = teamBaseMapper.selectVoPage(pageQuery.build(), lqw);

        //添加竞赛名称
        Set<Long> competitionIds = new HashSet<>();
        result.getRecords().forEach(e -> {
            competitionIds.add(e.getCompetitionId());
        });
        List<DcimsCompetitionVo> competitionVoList = competitionService.listById(new ArrayList<>(competitionIds));
        List<DcimsTeamVoV2> VoV2List = result.getRecords().stream().map(e -> {
            DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
            BeanUtils.copyProperties(e, voV2);
            // 添加教师和学生信息数组
            voV2.setStudentId(e.getStudentId().split(","));
            voV2.setStudentName(e.getStudentName().split(","));
            voV2.setTeacherId(e.getTeacherId().split(","));
            voV2.setTeacherName(e.getTeacherName().split(","));
            competitionVoList.forEach(c -> {
                if (c.getId().equals(voV2.getCompetitionId())){
                    voV2.setCompetition(c);
                    voV2.setCompetitionName(c.getName());
                }
            });
            return voV2;
        }).collect(Collectors.toList());
        // 获取团队对应oss信息
        Set<Long> OSSIds = new HashSet<>();
        VoV2List.forEach(e -> {
            OSSIds.add(e.getSupportMaterial());
        });
        OSSIds.removeAll(Collections.singleton(null));
        List<SysOssVo> ossVoList = ossService.listByIds(OSSIds);
        List<DcimsTeamVoV2> VoV2List2 = VoV2List;
        if(ossVoList.size() > 0){
            VoV2List2 = VoV2List.stream().map(e -> {
                DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
                BeanUtils.copyProperties(e, voV2);
                ossVoList.forEach(oss -> {
                    if (oss.getOssId().equals(voV2.getSupportMaterial())){
                        voV2.setSupportMaterialURL(oss.getUrl());
                        voV2.setOss(oss);
                    }
                });
                if (ObjectUtil.isNull(voV2.getOss()))
                    voV2.setOss(new SysOssVo());
                return voV2;
            }).collect(Collectors.toList());
        }
        VoV2List2.forEach(e -> {
            if (ObjectUtil.isNull(e.getOss())){
                e.setOss(null);
            }
        });
        // 填写所属学院与佐证材料文件名
        VoV2List2.forEach(e -> {
            if (ObjectUtil.isNotNull(e.getCompetition())){
                e.setCollege(e.getCompetition().getCollege());
                e.setCollegeName(String.valueOf(e.getCompetition().getCollege()));
            }
            if (ObjectUtil.isNotNull(e.getOss())){
                String fileName = translateAwardLevel(e.getAwardLevel()) + "-"+ Arrays.toString(e.getStudentName()).substring(
                    1,
                    Arrays.toString(e.getStudentName()).length() - 1
                ) + e.getOss().getFileSuffix();
                e.setSupportMaterialName(fileName);
            }
        });
        // 填写审核详情
        List<Long> teamIds = new ArrayList<>();
        for(DcimsTeamVoV2 vo : VoV2List2){
            teamIds.add(vo.getId());
        }
        LambdaQueryWrapper<DcimsTeamAudit> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(teamIds.size() > 0, DcimsTeamAudit::getTeamId, teamIds);
        List<DcimsTeamAuditVo> auditList = teamAuditBaseMapper.selectVoList(lqw2);
        Map<Long, DcimsTeamAuditVo> m = new HashMap<>();
        for (DcimsTeamAuditVo audit : auditList){
            DcimsTeamAuditVo audit1 = m.get(audit.getTeamId());
            if (audit1 != null){
                if (audit1.getId() < audit.getId()){
                    m.put(audit.getTeamId(), audit);
                }
            }else {
                m.put(audit.getTeamId(), audit);
            }
        }
        for (DcimsTeamAuditVo audit : m.values()){
            for (DcimsTeamVoV2 vo : VoV2List2){
                if (Objects.equals(audit.getTeamId(), vo.getId())){
                    vo.setAuditDetail(audit);
                }
            }
        }
        // 排序
        List<DcimsTeamVoV2> collect = VoV2List2.stream()
            .sorted(
                Comparator.comparing(DcimsTeamVoV2::getCompetitionId)
                .thenComparing(DcimsTeamVoV2::getAwardLevel)
                .thenComparing(vo -> PinyinUtil.getPinyin(Arrays.toString(vo.getStudentName())))
            )
            .collect(Collectors.toList()
        );


        // 添加团队对应竞赛信息
        return TableDataInfo.build(collect);
    }

    /**
     * 查询团队获奖审核列表
     */
    @Override
    public List<DcimsTeamAuditVo> queryList(DcimsTeamAuditBo bo) {
        LambdaQueryWrapper<DcimsTeamAudit> lqw = buildQueryWrapper(bo);
        return teamAuditBaseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsTeamAudit> buildQueryWrapper(DcimsTeamAuditBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsTeamAudit> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getTeamId() != null, DcimsTeamAudit::getTeamId, bo.getTeamId());
        lqw.eq(bo.getTeacherId() != null, DcimsTeamAudit::getTeacherId, bo.getTeacherId());
        lqw.eq(bo.getResult() != null, DcimsTeamAudit::getResult, bo.getResult());
        lqw.eq(StringUtils.isNotBlank(bo.getReason()), DcimsTeamAudit::getReason, bo.getReason());
        lqw.eq(bo.getNextTeacherId() != null, DcimsTeamAudit::getNextTeacherId, bo.getNextTeacherId());
        return lqw;
    }

    /**
     * 通过获奖审核
     */
    @Override
    public Boolean insertByBo(List<DcimsTeamAuditBo> boList) {
        List<DcimsTeam> teamList = new ArrayList<>();
        List<DcimsTeamAudit> teamAuditList = new ArrayList<>();
        List<String> roleList = StpUtil.getRoleList();
        for (DcimsTeamAuditBo bo:boList) {
            //手动为Bo添加操作者等部分数据
            if(roleList.contains("AcademicAffairsOffice")){
                StpUtil.checkRole("AcademicAffairsOffice");
                bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
                bo.setResult(1L);
                DcimsTeamAudit add1 = BeanUtil.toBean(bo, DcimsTeamAudit.class);
                DcimsTeam add2 = teamBaseMapper.selectById(bo.getTeamId());
                if(add2.getNextAuditId().equals(add1.getTeacherId())){
                    teamAuditList.add(add1);
                    add2.setAudit(2);
                    add2.setNextAuditId(-1L);
                    teamList.add(add2);
                }
            } else {
                bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
                bo.setResult(1L);
                DcimsTeamAudit add1 = BeanUtil.toBean(bo, DcimsTeamAudit.class);
                DcimsTeam add2 = teamBaseMapper.selectById(bo.getTeamId());
                if(add2.getNextAuditId().equals(add1.getTeacherId())){
                    // 获取校级管理员的工号
                    LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
                    lqw.eq(SysDept::getParentId,0);
                    lqw.eq(SysDept::getDeptName,"浙江科技学院教务处");
                    SysDept sysDept = sysDeptMapper.selectOne(lqw);
                    add1.setNextTeacherId(sysDept.getLeaderTeacherId());
                    teamAuditList.add(add1);
                    add2.setNextAuditId(sysDept.getLeaderTeacherId());
                    add2.setAudit(1);
                    teamList.add(add2);
                }
            }
        }
        return (teamBaseMapper.updateBatchById(teamList))&&(teamAuditBaseMapper.insertBatch(teamAuditList));
    }

    /**
     * 批量删除团队获奖审核
     */
    @Override
    public Boolean deleteWithValidByIds(List<DcimsTeamAuditBo> boList) {
        List<DcimsTeam> teamList = new ArrayList<>();
        List<DcimsTeamAudit> comAuditList = new ArrayList<>();
        for (DcimsTeamAuditBo bo:boList) {
            //手动为Bo添加操作者等部分数据
            bo.setTeacherId(AccountUtils.getAccount(StpUtil.getLoginIdAsString()).getTeacherId());
            bo.setResult(0L);
            DcimsTeamAudit add1 = BeanUtil.toBean(bo, DcimsTeamAudit.class);
            DcimsTeam add2 = teamBaseMapper.selectById(bo.getTeamId());
            if(add2 == null)   add2 = new DcimsTeam();
            // 正常判断是否该审核人进行操作
            if(add1.getTeacherId().equals(add2.getNextAuditId())){
                comAuditList.add(add1);
                add2.setNextAuditId(bo.getNextTeacherId());
                teamList.add(add2);
                // 教务处退回时，获取学院负责人的工号，审核人更改为学院负责人
                Long collegeNum = competitionService.queryById(add2.getCompetitionId()).getCollege();
                add1.setNextTeacherId(getCollegeLeaderId(collegeNum));
                add2.setNextAuditId(getCollegeLeaderId(collegeNum));
                add2.setAudit(3);
                // 学院退回时，检查审核人和当前登录用户的工号是否相同，如果相容则填写-1，待老师修改后重新提交
                if (add2.getNextAuditId().equals(AccountUtils.getAccount().getTeacherId())) {
                    add1.setNextTeacherId(-1L);
                    add2.setNextAuditId(-1L);
                    add2.setAudit(3);
                }
            }
        }
        boolean flag;
        if (!teamList.isEmpty()) {
            teamBaseMapper.updateBatchById(teamList);
            teamAuditBaseMapper.insertBatch(comAuditList);
            flag = true;
        }else {
            flag = false;
        }
        return flag;
    }

    /**
     * 对已经归档的获奖信息进行删除
     *
     * @param id
     */
    @Override
    public Boolean deleteOneById(Long id) {
        DcimsTeam team = teamBaseMapper.selectById(id);
        team.setAudit(3);
        team.setNextAuditId(-1L);
        return teamBaseMapper.updateById(team) > 0;
    }


    public List<DcimsTeamWithCompetition> queryListWithCompetition(DcimsTeamBo bo){
        List<DcimsTeamWithCompetition> result = teamBaseMapper.selectTeamWithCompetition(bo.getAnnual());

        // 查看当前用户是否是学科竞赛负责人,是的话只差负责学科
        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains("AcademyCompetitionTeacher") && !roleList.contains("AcademyCompetitionHead")&& !roleList.contains("AcademicAffairsOffice") ){
            Long teacherId = AccountUtils.getAccount().getTeacherId();
            LambdaQueryWrapper<DcimsCompetition> l = new LambdaQueryWrapper<>();
            l.eq(DcimsCompetition::getResponsiblePersonId, teacherId);
            List<DcimsCompetition> competitionList = dcimsCompetitionMapper.selectList(l);
            List<Long> ids = competitionList.stream().map(DcimsCompetition::getId).collect(Collectors.toList());
            result = result.stream().filter(e -> ids.contains(e.getCompetitionId())).collect(Collectors.toList());
        }

        if (bo.getCompetitionName() != null && !bo.getCompetitionName().isEmpty()) {
            System.out.println(bo.getCompetitionName());
            result = result.stream().filter(e -> {
                return e.getName().contains(bo.getCompetitionName());
            }).collect(Collectors.toList());
        }
        return result;
    }

    public OutputStream getHuoJiangBiao(List<DcimsTeam> data) throws IOException {
        return getHuoJiangBiao();

//        File file = ResourceUtils.getFile("classpath:excel/HuoJiangDengJiBiao.docx");
//        Path tempFile = Files.createTempFile("temp", ".docx");
//
//        try (InputStream is = Files.newInputStream(file.toPath())) {
//            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//            try (XWPFDocument document = new XWPFDocument(Files.newInputStream(tempFile))) {
//
//                List<DcimsTeam> data1 = data.stream().filter(e -> Integer.parseInt(e.getAwardLevel()) <= 9).collect(Collectors.toList());
//                List<DcimsTeam> data2 = data.stream().filter(e -> Integer.parseInt(e.getAwardLevel()) <= 19 && Integer.parseInt(e.getAwardLevel()) >= 10).collect(Collectors.toList());
//
//
//                List<XWPFTable> tables = document.getTables();
//
//                System.out.println(tables.get(0).getText());
//                XWPFTableRow headerRow = tables.get(0).getRow(0);
//                fillTableWithData(tables.get(0), data1);
//                if ("项  目".equals(headerRow.getCell(0).getText())) {
//                    fillTableWithData(tables.get(0), data1);
//                }
//                System.out.println(tables.get(0).getText());
//
//                System.out.println(tables.get(1).getText());
//                headerRow = tables.get(1).getRow(0);
//                if ("项  目".equals(headerRow.getCell(0).getText())) {
//                    fillTableWithData(tables.get(1), data2);
//                }
//                System.out.println(tables.get(1).getText());
//
//                try (OutputStream os = Files.newOutputStream(tempFile)) {
//                    document.write(os);
//                    System.out.println(os.toString());
//                }
//
//
//                return Files.newOutputStream(tempFile);
//            }
//        }
    }

    public OutputStream getHuoJiangBiao() throws IOException {
        return null;
    }


    private static void fillTableWithData(XWPFTable table, List<DcimsTeam> teamList) {
        for (DcimsTeam team : teamList) {
            XWPFTableRow row = table.createRow();
//            row.getCell(0).setText(((DcimsCompetition)team).getName()); // 填充项目列
            row.getCell(1).setText(team.getAwardLevel());  // 填充获奖级别列
            row.getCell(2).setText(team.getStudentName()); // 填充学生名单列
            row.getCell(3).setText(team.getTeacherName());  // 填充指导教师列
            System.out.println();
        }
        System.out.println(teamList);
    }

    /**
     * 根据学院id获取学院负责人的工号
     */
    public Long getCollegeLeaderId(Long college){
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getParentId,100);
        lqw.eq(SysDept::getOrderNum, college);
        SysDept sysDept = sysDeptMapper.selectOne(lqw);
        System.out.println("传入学院："+ college + "结果：" + sysDept.getLeaderTeacherId());
        if (sysDept != null){
            return sysDept.getLeaderTeacherId();
        }
        return null;
    }

    /**
     * 从字典中翻译学院名称
     */
    private String translateCollegeName(Long collegeId) {
        List<SysDictData> dcimsAwardLevel = dictTypeService.selectDictDataByType("dcims_college");
        Optional<SysDictData> match = dcimsAwardLevel.stream()
            .filter(dcimsExcel -> StrUtil.equals(dcimsExcel.getDictValue(), String.valueOf(collegeId)))
            .findFirst();
        return match.map(SysDictData::getDictLabel).orElse("未知");
    }

    /**
     * 从字典中翻译获奖等级
     */
    private String translateAwardLevel(String awardLevel) {
        List<SysDictData> dcimsAwardLevel = dictTypeService.selectDictDataByType("dcims_award_level");
        Optional<SysDictData> match = dcimsAwardLevel.stream()
            .filter(dcimsExcel -> StrUtil.equals(dcimsExcel.getDictValue(), awardLevel))
            .findFirst();
        return match.map(SysDictData::getDictLabel).orElse("未知");
    }
}
