package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.domain.DcimsTeamAudit;
import com.ruoyi.system.domain.DcimsTeamWithCompetition;
import com.ruoyi.system.domain.bo.DcimsTeamAuditBo;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.domain.vo.DcimsCompetitionVo;
import com.ruoyi.system.domain.vo.DcimsTeamAuditVo;
import com.ruoyi.system.domain.vo.DcimsTeamVo;
import com.ruoyi.system.domain.vo.DcimsTeamVoV2;
import com.ruoyi.system.mapper.DcimsTeamAuditMapper;
import com.ruoyi.system.mapper.DcimsTeamMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.IDcimsCompetitionService;
import com.ruoyi.system.service.IDcimsTeamAuditService;
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
        // 获取审核状态为待审核的团队
        lqw.eq(DcimsTeam::getAudit,1);

        Page<DcimsTeamVo> result = teamBaseMapper.selectVoPage(pageQuery.build(), lqw);
        System.out.println(result);

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
                }
            });
            return voV2;
        }).collect(Collectors.toList());

        // 添加团队对应竞赛信息
        return TableDataInfo.build(VoV2List);
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
            if(add1.getTeacherId().equals(add2.getNextAuditId())){
                comAuditList.add(add1);
                add2.setNextAuditId(bo.getNextTeacherId());
                teamList.add(add2);
                add1.setNextTeacherId(-1L);
                add2.setNextAuditId(-1L);
                add2.setAudit(3);
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
}
