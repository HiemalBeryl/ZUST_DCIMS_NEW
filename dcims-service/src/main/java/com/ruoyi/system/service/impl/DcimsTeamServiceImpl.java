package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.compress.ZipReader;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.*;
import cn.hutool.extra.compress.CompressUtil;
import cn.hutool.extra.compress.extractor.Extractor;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.utils.redis.RedisUtils;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsCompetitionAudit;
import com.ruoyi.system.domain.DcimsTeamAudit;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.domain.entity.OssFile;
import com.ruoyi.system.domain.excel.DcimsTeamImportExcel;
import com.ruoyi.system.domain.excel.DcimsTeamImportExcelError;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.handler.ExcelWriteHandler;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsTeamAuditMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.*;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.ArchiveException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.mapper.DcimsTeamMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 参赛团队Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-05-01
 */
@RequiredArgsConstructor
@Service
public class DcimsTeamServiceImpl implements IDcimsTeamService {

    private final DcimsTeamMapper baseMapper;
    private final DcimsCompetitionMapper dcimsCompetitionMapper;
    private final DcimsTeamAuditMapper teamAuditBaseMapper;
    private final IDcimsBasicDataService basicDataService;
    private final SysDeptMapper sysDeptMapper;
    private final IDcimsCompetitionService competitionService;
    private final ISysOssService ossService;
    private final ISysDeptService deptService;
    private final ISysDictTypeService dictTypeService;
    private final ResourceLoader resourceLoader;

    /**
     * 查询参赛团队
     */
    @Override
    public DcimsTeamVoV2 queryById(Long id){
        DcimsTeamVo vo = baseMapper.selectVoById(id);
        DcimsCompetitionVo dcimsCompetitionVo = dcimsCompetitionMapper.selectVoById(vo.getCompetitionId());

        DcimsCompetitionVo competition = competitionService.queryById(vo.getCompetitionId());
        DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
        BeanUtils.copyProperties(vo, voV2);
        voV2.setStudentId(vo.getStudentId().split(","));
        voV2.setStudentName(vo.getStudentName().split(","));
        voV2.setTeacherId(vo.getTeacherId().split(","));
        voV2.setTeacherName(vo.getTeacherName().split(","));
        voV2.setCompetition(dcimsCompetitionVo);
        voV2.setCompetitionName(competition.getName());
        return voV2;
    }

    /**
     * 查询参赛团队列表
     */
    @Override
    public TableDataInfo<DcimsTeamVoV2> queryPageList(DcimsTeamBo bo, PageQuery pageQuery) {
        // TODO：特殊查询条件-竞赛名&年份，筛选有问题以后再改，如果没有查询到限制条件的竞赛，那么展示所有，年份还没加进去
        List<Long> cIds = new ArrayList<>();
        if (bo.getCompetitionName() != null && !bo.getCompetitionName().trim().equals("")){
            LambdaQueryWrapper<DcimsCompetition> l = new LambdaQueryWrapper<>();
            l.like(DcimsCompetition::getName, bo.getCompetitionName());
            List<DcimsCompetition> competitionList = dcimsCompetitionMapper.selectList(l);
            cIds = competitionList.stream().map(DcimsCompetition::getId).collect(Collectors.toList());
        }

        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        lqw.in(cIds.size()>0, DcimsTeam::getCompetitionId, cIds);
        // 教务处可以查看全校，学院管理员可以查看学院，普通教师查看自己
        PageQuery pq = new PageQuery();
        pq.setPageNum(0);
        pq.setPageSize(10000);
        TableDataInfo<DcimsCompetitionVo> competitionList = competitionService.queryPageList(new DcimsCompetitionBo(), pq, true, false);
        List<Long> competitionIds0 = competitionList.getRows().stream().filter(e ->
            e.getAnnual().equals(bo.getAnnual())
        ).map(DcimsCompetitionVo::getId).collect(Collectors.toList());
        //List<Long> competitionIds0 = competitionList.getRows().stream().map(DcimsCompetitionVo::getId).collect(Collectors.toList());
        if (competitionIds0.size() > 0) {
            lqw.in(DcimsTeam::getCompetitionId, competitionIds0);
        }


        Page<DcimsTeamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<DcimsTeamVo> build = TableDataInfo.build(result);
        // 获取团队对应竞赛信息
        Set<Long> competitionIds = new HashSet<>();
        build.getRows().forEach(e -> {
            competitionIds.add(e.getCompetitionId());
        });
        List<DcimsCompetitionVo> competitionVoList = competitionService.listById(new ArrayList<>(competitionIds));
        List<DcimsTeamVoV2> VoV2List = build.getRows().stream().map(e -> {
            DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
            BeanUtils.copyProperties(e, voV2);
            // 添加教师和学生信息数组
            System.out.println(e);
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
        // 获取团队对应oss信息
        Set<Long> OSSIds = new HashSet<>();
        build.getRows().forEach(e -> {
            OSSIds.add(e.getSupportMaterial());
        });
        OSSIds.removeAll(Collections.singleton(null));
        System.out.println(OSSIds);
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
            if (ObjectUtil.isNull(e.getSupportMaterial())){
                e.setOss(null);
            }
        });
        TableDataInfo<DcimsTeamVoV2> build1 = TableDataInfo.build(VoV2List2);
        BeanUtils.copyProperties(build, build1);
        build1.setRows(VoV2List2);
        return build1;
    }

    /**
     * 根据教师工号查询参赛团队列表
     */
    @Override
    public TableDataInfo<DcimsTeamVoV2> queryPageListByTeacherId(DcimsTeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        // 获取自己负责的竞赛
        PageQuery query = new PageQuery();
        query.setPageSize(100);
        query.setPageNum(1);
        TableDataInfo<DcimsCompetitionVo> myCompetition = competitionService.queryPageListByTeacherId(new DcimsCompetitionBo(), query);
        List<Long> ids = new ArrayList<>();
        for (DcimsCompetitionVo com : myCompetition.getRows()){
            ids.add(com.getId());
        }
        // 添加查询登录人工号的限制条件
        lqw.like(AccountUtils.getAccount().getTeacherId() != null, DcimsTeam::getTeacherId, AccountUtils.getAccount().getTeacherId());
        lqw.or(AccountUtils.getAccount().getTeacherId() != null);
        lqw.in( ids.size() > 0, DcimsTeam::getCompetitionId, ids);
        lqw.or(ids.size() > 0);
        lqw.eq(AccountUtils.getAccount().getTeacherId() != null, DcimsTeam::getCreateBy, AccountUtils.getAccount().getTeacherId());
        lqw.orderBy(true, false, DcimsTeam::getId);
        Page<DcimsTeamVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<DcimsTeamVo> build = TableDataInfo.build(result);

        // 查询审核状态信息
        List<Long> teamIds = new ArrayList<>();
        for(DcimsTeamVo vo : result.getRecords()){
            teamIds.add(vo.getId());
        }
        System.out.println(teamIds);
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
        System.out.println(m.values());
        for (DcimsTeamAuditVo audit : m.values()){
            for (DcimsTeamVo vo : result.getRecords()){
                System.out.println(audit.getTeamId() + "   " + vo.getId());
                if (Objects.equals(audit.getTeamId(), vo.getId())){
                    vo.setAuditDetail(audit);
                    System.out.println(vo.getId() + " " + audit.getReason());
                }
            }
        }

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

        // 获取团队对应oss信息
        Set<Long> OSSIds = new HashSet<>();
        result.getRecords().forEach(e -> {
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
            if (ObjectUtil.isNull(e.getSupportMaterial())){
                e.setOss(null);
            }
        });
        TableDataInfo<DcimsTeamVoV2> build1 = TableDataInfo.build(VoV2List2);
        BeanUtils.copyProperties(build, build1);
        build1.setRows(VoV2List2);
        return build1;
    }

    /**
     * 查询参赛团队列表
     */
    @Override
    public List<DcimsTeamVo> queryList(DcimsTeamBo bo) {
        LambdaQueryWrapper<DcimsTeam> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<DcimsTeam> buildQueryWrapper(DcimsTeamBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsTeam> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getCompetitionId() != null, DcimsTeam::getCompetitionId, bo.getCompetitionId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), DcimsTeam::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCompetitionType()), DcimsTeam::getCompetitionType, bo.getCompetitionType());
        lqw.eq(StringUtils.isNotBlank(bo.getAwardLevel()), DcimsTeam::getAwardLevel, bo.getAwardLevel());
        lqw.like(StringUtils.isNotBlank(bo.getTeacherId()), DcimsTeam::getTeacherId, bo.getTeacherId());
        lqw.like(StringUtils.isNotBlank(bo.getTeacherName()), DcimsTeam::getTeacherName, bo.getTeacherName());
        lqw.like(StringUtils.isNotBlank(bo.getStudentId()), DcimsTeam::getStudentId, bo.getStudentId());
        lqw.like(StringUtils.isNotBlank(bo.getStudentName()), DcimsTeam::getStudentName, bo.getStudentName());
        lqw.like(StringUtils.isNotBlank(bo.getNext_audit_id()), DcimsTeam::getNextAuditId, bo.getNext_audit_id());
        lqw.eq(bo.getAudit() != null, DcimsTeam::getAudit, bo.getAudit());
        return lqw;
    }

    /**
     * 新增参赛团队
     */
    @Override
    public Boolean insertByBo(DcimsTeamBo bo) {
        // 添加教师名称和学生名称，前端数据不可靠
        List<String> studentIdSplit = Arrays.stream(bo.getStudentId().split(",")).collect(Collectors.toList());
        List<Long> teacherIdSplit = Arrays.stream(bo.getTeacherId().split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<DcimsStudentVo> students = basicDataService.getStudentNameByIds(studentIdSplit);
        List<DcimsTeacherVo> teachers = basicDataService.getTeacherNameByIds(teacherIdSplit);
        // 这样子写会导致教师名称顺序和工号顺序对不上，应该改正
        List<String> studentNameList = new ArrayList<>();
        for (String str : studentIdSplit){
            for (DcimsStudentVo student : students){
                if (Objects.equals(str, student.getStudentId())){
                    studentNameList.add(student.getName());
                }
            }
        }
        String studentName = StringUtils.join(studentNameList, ",");
        List<String> teacherNameList = new ArrayList<>();
        for (Long str : teacherIdSplit){
            for (DcimsTeacherVo teacher : teachers){
                if (Objects.equals(str, teacher.getTeacherId())){
                    teacherNameList.add(teacher.getName());
                }
            }
        }
        String teacherName = StringUtils.join(teacherNameList, ",");
        bo.setTeacherName(teacherName);
        bo.setStudentName(studentName);


        DcimsTeam add = BeanUtil.toBean(bo, DcimsTeam.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改参赛团队
     */
    @Override
    public Boolean updateByBo(DcimsTeamBo bo) {
        DcimsTeam update = BeanUtil.toBean(bo, DcimsTeam.class);
        validEntityBeforeSave(update);
        // 重置审核信息
        DcimsCompetition competition = dcimsCompetitionMapper.selectById(update.getCompetitionId());
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getParentId,100);
        lqw.eq(SysDept::getOrderNum,competition.getCollege());
        SysDept sysDept = sysDeptMapper.selectOne(lqw);
        // 判断状态，原来是未添加佐证材料的状态不变，否则变为待审核
        DcimsTeam entity = baseMapper.selectById(update.getId());
        if(entity.getAudit() == 0){
            update.setAudit(0);
        } else {
            update.setAudit(1);
        }
        update.setNextAuditId(sysDept.getLeaderTeacherId());
        // 填写教师、学生姓名
        String studentIds = bo.getStudentId();
        String[] splitStudentIds = studentIds.split(",");
        List<String> studentIdsString = new ArrayList<>(Arrays.asList(splitStudentIds));
        List<DcimsStudentVo> students = basicDataService.getStudentNameByIds(studentIdsString);
        String studentName = "";
        for(DcimsStudentVo student : students){
            studentName = studentName.concat(student.getName() + ',');
        }
        studentName = studentName.substring(0,studentName.length() - 1);
        update.setStudentName(studentName);

        String teacherIds = bo.getTeacherId();
        String[] splitTeacherIds = teacherIds.split(",");
        List<Long> teacherIdsLong = new ArrayList<>();
        for(String id : splitTeacherIds){
            teacherIdsLong.add(Long.parseLong(id));
        }
        List<DcimsTeacherVo> teachers = basicDataService.getTeacherNameByIds(teacherIdsLong);
        String teacherName = "";
        for(DcimsTeacherVo teacher : teachers){
            teacherName = teacherName.concat(teacher.getName() + ',');
        }
        teacherName = teacherName.substring(0,teacherName.length() - 1);
        update.setTeacherName(teacherName);


        return baseMapper.updateById(update) > 0;
    }

    /**
     * 为团队添加获奖信息
     */
    @Override
    public Boolean declareAwardByBo(DcimsDeclareAwardBo bo) {
        // 先查询对应竞赛，获取到id以外的其他数据
        DcimsTeam dcimsTeam = baseMapper.selectById(bo.getId());
        if (dcimsTeam == null) {
            return false;
        }
        // 判断是更新还是插入
        String flag;
        if (dcimsTeam.getSupportMaterial() == null){
            // 更新
            flag = "update";
        }else {
            // 插入
            flag = "insert";
        }
        bo.setCompetitionId(dcimsTeam.getCompetitionId());
        bo.setName(dcimsTeam.getName());
        bo.setCompetitionType(dcimsTeam.getCompetitionType());
        bo.setTeacherId(dcimsTeam.getTeacherId());
        bo.setTeacherName(dcimsTeam.getTeacherName());
        bo.setStudentId(dcimsTeam.getStudentId());
        bo.setStudentName(dcimsTeam.getStudentName());
        bo.setCompetitionTime(dcimsTeam.getCompetitionTime());
        bo.setAudit(dcimsTeam.getAudit() + 1);
        // 执行更新操作
        if (Objects.equals(flag, "update")){
            DcimsTeam update = BeanUtil.toBean(bo, DcimsTeam.class);
            validEntityBeforeSave(update);
            return baseMapper.updateById(update) > 0;
        }
        // 执行插入操作
        DcimsTeam insert = BeanUtil.toBean(bo, DcimsTeam.class);
        insert.setAudit(1);
        validEntityBeforeSave(insert);
        // 设置父项目，同时清空主键
        insert.setAdvancedAwardNumber(bo.getId());
        insert.setId(null);
        return baseMapper.insert(insert) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsTeam entity){
        // 为团队对象添加审核信息
        DcimsCompetition competition = dcimsCompetitionMapper.selectById(entity.getCompetitionId());
        if(competition != null){
            LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SysDept::getParentId,100);
            lqw.eq(SysDept::getOrderNum,competition.getCollege());
            SysDept sysDept = sysDeptMapper.selectOne(lqw);
            if (sysDept != null){
                entity.setNextAuditId(sysDept.getLeaderTeacherId());
            }else{
                entity.setNextAuditId(-1L);
            }
        }else{
            entity.setNextAuditId(-1L);
        }
        // 只要是修改，那么就重置审核状态

    }

    /**
     * 批量删除参赛团队
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 获取批量导入模板
     */
    @Override
    public File getImportTemplate(Integer annual) throws IOException {
        List<String> competitionNames = competitionService.queryList(annual).stream().map(DcimsCompetitionVo::getName).collect(Collectors.toList());
        List<String> awardLevels =  dictTypeService.selectDictDataByType("dcims_award_level").stream().map(SysDictData::getDictLabel).collect(Collectors.toList());

        ExcelWriteHandler excelWriteHandler = new ExcelWriteHandler(annual, competitionNames, awardLevels);
//        ClassPathResource templateResource = new ClassPathResource("excel/批量导入获奖团队");
        Resource templateResource = resourceLoader.getResource("classpath:excel/批量导入获奖团队/teamImportTemplate.xlsx");
//        File excelTemplateFile = templateResource.getFile();
//        File outputFile = FileUtil.copyFile(excelTemplateFile.getPath(), excelTemplateFile.getPath().substring(0, excelTemplateFile.getPath().length() - 24) + "/teamImportTemplate"+ new Date().getTime() +".xlsx");
        String outputFilePath = "/teamImportTemplate" + new Date().getTime() + ".xlsx";
        File outputFile = new File(outputFilePath);
//        System.out.println(templateResource.getURI());
//        System.out.println(outputFile.getPath());

        // 使用resource InputStream
        InputStream templateInputStream = templateResource.getInputStream();

        // 创建一个临时文件来存储模板内容，用于EasyExcel
        File tempFile = File.createTempFile("teamImportTemplate", ".xlsx");
        Files.copy(templateInputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // 确保输入流关闭
        templateInputStream.close();


        List<DcimsTeamImportExcel> temp = new ArrayList<>();
        EasyExcel.write(outputFile)
            .registerWriteHandler(excelWriteHandler).withTemplate(tempFile).sheet("工作表1").doWrite(temp);

        // 记得在操作完成后清理临时文件（根据实际情况决定是否删除）
        tempFile.deleteOnExit();
        return outputFile;
    }

    /**
     * 对批量导入模板内填写的数据进行读取，格式转换
     */
    public List<DcimsTeamImportExcel> readDataFromTemplate(InputStream file) throws IOException, ArchiveException {
        // 以时间戳为文件唯一标识
        Path tempDir = Files.createTempDirectory("temp");
        System.out.println("临时目录路径: " + tempDir);
//        ClassPathResource templateResource = new ClassPathResource("temp");
        Date now = new Date();
        String pathName = tempDir.getFileName() + "/" + now.getTime() + new Random().nextInt(64);
        File unzipFile = new File(pathName);

        // 解压文件，打开表格文件
        File unzip = ZipUtil.unzip(file, unzipFile, Charset.forName("GBK"));
        // 解压后的所有文件，包括表格和佐证材料
        List<File> oss = FileUtil.loopFiles(unzip.getAbsolutePath());
        oss = oss.stream().filter(e -> !e.getAbsolutePath().contains("__MACOSX")).collect(Collectors.toList());

        List<SysOssVo> ossVo = oss.stream().filter(e -> {
            return !e.getName().endsWith(".xlsx") || !e.getName().endsWith(".xls") || !e.getName().endsWith(".xlsm");
        }).map(o -> ossService.upload(o, o.getName())).collect(Collectors.toList());
        System.out.println(ossVo);
        System.out.println(Arrays.stream(unzip.listFiles()).map(File::getName).collect(Collectors.toList()));
        Optional<File> optionalFile = oss.stream().filter(e -> e.getName().endsWith(".xlsx") || e.getName().endsWith(".xls") || e.getName().endsWith(".xlsm"))
            .findFirst();
        File excelFile = null;
        if (optionalFile.isPresent())
            excelFile = optionalFile.get();
        System.out.println(excelFile);
        try {
            assert excelFile != null;
            List<DcimsTeamImportExcel> importTeamData = EasyExcel.read(new FileInputStream(excelFile)).head(DcimsTeamImportExcel.class).autoCloseStream(false).sheet().headRowNumber(3).doReadSync();

            // 匹配oss
            importTeamData.forEach(dcimsTeamImportExcel -> {
                Optional<SysOssVo> match = ossVo.stream()
                    .filter(vo -> StrUtil.equals(vo.getOriginalName(), dcimsTeamImportExcel.getSupportMaterialFileName()))
                    .findFirst();
                match.ifPresent(vo -> dcimsTeamImportExcel.setSupportMaterial(vo.getOssId()));
                match.ifPresent(dcimsTeamImportExcel::setOss);
            });
            // 未匹配到的添加错误信息
            importTeamData.forEach(dcimsTeamImportExcel -> {
                if(ObjectUtil.isNull(dcimsTeamImportExcel.getSupportMaterial())){
                    if(ObjectUtil.isNull(dcimsTeamImportExcel.getErrors())){
                        dcimsTeamImportExcel.setErrors(new ArrayList<>());
                    }
                    dcimsTeamImportExcel.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.fileNotFoundError, "未找到对应的的佐证材料，请重新上传！"));
                }
            });
            // 数据处理
            System.out.println(importTeamData);
            handleData(importTeamData, true,true ,true);
            System.out.println(importTeamData);

            return importTeamData;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ExcelDataConvertException e){
            throw new RuntimeException("表格数据格式读取错误，请检查模板表格文件内容是否正确填写！");
        }


//        Arrays.stream(FileUtil.ls(unzip.getAbsolutePath())).findFirst().ifPresent(excelFile -> {
//            System.out.println(excelFile);
//            try {
//                List<DcimsTeamImportExcel> importTeamData = ExcelUtil.importExcel(new FileInputStream(excelFile), DcimsTeamImportExcel.class);
//                // 匹配oss
//                importTeamData.forEach(dcimsTeamImportExcel -> {
//                    Optional<SysOssVo> match = ossVo.stream()
//                        .filter(vo -> StrUtil.equals(vo.getFileName(), dcimsTeamImportExcel.getSupportMaterialFileName()))
//                        .findFirst();
//                    match.ifPresent(vo -> dcimsTeamImportExcel.setSupportMaterial(vo.getOssId()));
//                });
//                // 数据处理
//                handleData(importTeamData);
//                System.out.println(importTeamData);
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        File[] files = unzip.listFiles();

        // 打开表格文件
//        File excelFile = null;
//        if(FileUtil.isDirectory(unzip)){
//            List<File> fileList = FileUtil.loopFiles(unzip.getPath() + "/佐证材料");
//            for (File f:fileList){
//                String name = f.getName();
//                if (name.endsWith(".xlsx") || name.endsWith(".xls") || name.endsWith(".xlsm")){
//                    excelFile = f;
//                    break;
//                }
//            }
//        }
//        List<DcimsTeamImportExcel> importDataList = new ArrayList<>();
//        if (excelFile != null){
//            EasyExcel.read(excelFile, DcimsTeamImportExcel.class, new PageReadListener<DcimsTeamImportExcel>(importDataList::addAll)).sheet().headRowNumber(3).doRead();
//        }
//        System.out.println(importDataList);
//        return null;
    }

    /**
     * 将数据保存在Redis中
     * @param importTeamData
     * @return data+唯一id（用于在Redis中再次查找数据）
     */
    public Map<String, Object> saveDataToRedis(List<DcimsTeamImportExcel> importTeamData) {
        // 未最终确认的数据存入redis中，确认后再入库
        String id = new Date().getTime() + RandomUtil.randomString(4);
        RedisUtils.setCacheObject("dcims_team_list:" + id, JsonUtils.toJsonString(importTeamData));
        // 将id与数据同时返回
        Map<String, Object> ret = new HashMap<>();
        ret.put("data", importTeamData);
        ret.put("id", id);
        return ret;
    }

    /**
     * 批量导入数据校验
     */
    public Object checkImportData(Object data){
        return null;
    }

    /**
     * 手动修改批量导入的数据
     */
    public Map<String, Object> editImportData(String id, List<DcimsTeamImportExcel> importTeamData){
        // 从redis中读取保存的数据，防止用户篡改
        List<DcimsTeamImportExcel> redisImportTeamData = JSONUtil.toList(
            RedisUtils.getCacheObject("dcims_team_list:" + id).toString(),
            DcimsTeamImportExcel.class
        );
        if (ObjectUtil.isEmpty(redisImportTeamData))
            return null;

        Map<Integer, DcimsTeamImportExcel> origin = redisImportTeamData.stream().collect(Collectors.toMap(DcimsTeamImportExcel::getIndex, e -> e));
        Map<Integer, DcimsTeamImportExcel> destination = importTeamData.stream().collect(Collectors.toMap(DcimsTeamImportExcel::getIndex, e -> e));
        // 从传入数据中搜索redis数据，如果匹配到则修改redis的数据
        redisImportTeamData.forEach(r -> {
            DcimsTeamImportExcel d = destination.get(r.getIndex());
            if (ObjectUtil.isNotEmpty(d))
                BeanUtils.copyProperties(d, r, "index", "competitionId", "supportMaterial", "audit", "competitionType", "errors");
            r.setErrors(new ArrayList<>());
        });
        // 遍历传入数据，如果不存在在redis中则说明是新增数据
        for (DcimsTeamImportExcel d : importTeamData) {
            DcimsTeamImportExcel o = origin.get(d.getIndex());
            if(ObjectUtil.isEmpty(o)){
                redisImportTeamData.add(o);
            }
        }
        // 遍历Redis数据，如果不存在在传入数据中则说明数据被用户删除，应该在redis中也删除掉
        List<Integer> deletedIndexList = new ArrayList<>();
        int in = 0;
        for(DcimsTeamImportExcel e : redisImportTeamData){
            boolean f = false;
            for (DcimsTeamImportExcel i : importTeamData){
                if (i.getIndex() == e.getIndex()) {
                    f = true;
                    break;
                }
            }
            if (!f)
                deletedIndexList.add(new Integer(in));
            in++;
        }
        for (Integer i: deletedIndexList){
            redisImportTeamData.remove(i.intValue());
        }
        //TODO: 删除的检测

        handleData(redisImportTeamData, false, false, false);
        return saveDataToRedis(redisImportTeamData);
    }

    /**
     * 为批量导入追加数据
     */
    public Map<String, Object> appendImportData(String id, String type, InputStream file) throws IOException, ArchiveException {
        // 从redis中读取保存的数据，防止用户篡改
        List<DcimsTeamImportExcel> redisImportTeamData = JSONUtil.toList(
            RedisUtils.getCacheObject("dcims_team_list:" + id).toString(),
            DcimsTeamImportExcel.class
        );
        if (ObjectUtil.isEmpty(redisImportTeamData))
            throw new NullPointerException("未找到对应的数据，请检查压缩包格式是否正确！");

        // 追加的类型判断
        List<DcimsTeamImportExcel> appendList = readDataFromTemplate(file);
        if (Objects.equals(type, "append")){
            redisImportTeamData.addAll(appendList);
        }else if (Objects.equals(type, "cover")){
            redisImportTeamData = appendList;
        }else{
            return null;
        }
        RedisUtils.deleteKeys("dcims_team_list:" +id);
        return saveDataToRedis(redisImportTeamData);
    }

    /**
     * 批量导入数据保存
     */
    public boolean submitImportData(String importDataId){
        // 从redis中读取保存的数据，防止用户篡改
        List<DcimsTeamImportExcel> redisImportTeamData = JSONUtil.toList(
            RedisUtils.getCacheObject("dcims_team_list:" + importDataId).toString(),
            DcimsTeamImportExcel.class
        );
        if (ObjectUtil.isEmpty(redisImportTeamData))
            return false;


        // 判断数据中是否存在错误，如果在存在则不允许保存
        System.out.println(redisImportTeamData.getClass());
        boolean flag = redisImportTeamData.stream().anyMatch(e -> e.getErrors().size() > 0);
        if (flag)
            return false;


        // 将数据转换为Bean，保存到数据库，同时添加必要的与审核有关的信息
        List<DcimsTeam> teamList = new ArrayList<>();
        for (DcimsTeamImportExcel e : redisImportTeamData){
            DcimsTeam team = new DcimsTeam();
            DcimsTeamBo bo = new DcimsTeamBo();
            BeanUtils.copyProperties(e, bo);
            BeanUtils.copyProperties(bo, team);
            validEntityBeforeSave(team);
            team.setAudit(1);
            teamList.add(team);
        }

        // 确定数据正确保存后删除redis中的缓存
        if(baseMapper.insertBatch(teamList)){
            RedisUtils.deleteKeys("dcims_team_list:" + importDataId);
        }

        return true;
    }

    /**
     * 导出已保存的数据用于核对
     */
    public void exportRedisData(){

    }

    /**
     * 数据校验 + 数据转换
     */
    private List<DcimsTeamImportExcel> handleData(List<DcimsTeamImportExcel> importTeamData, boolean editTeamName, boolean isSetId, boolean isFillName){
        // 1. 数据检验，找出不合理数据
        if(isSetId){
            for(DcimsTeamImportExcel e : importTeamData){
                e.setIndex(RandomUtil.randomInt());
                if(ObjectUtil.isNull(e.getErrors())){
                    e.setErrors(new ArrayList<>());
                    e.setEdit(false);
                }
            }
        }

        // 根据竞赛名和年份查询对应竞赛id
        List<String> competitionNames = importTeamData.stream().map(DcimsTeamImportExcel::getCompetitionName).distinct().collect(Collectors.toList());
        List<DcimsCompetitionVo> competitionVos = competitionService.queryList(competitionNames);
        importTeamData.forEach(dcimsTeamImportExcel -> {
            Optional<DcimsCompetitionVo> match = competitionVos.stream()
                .filter(dcimsExcel -> StrUtil.equals(dcimsExcel.getName(), dcimsTeamImportExcel.getCompetitionName())
                    && StrUtil.equals(String.valueOf(dcimsExcel.getAnnual()), dcimsTeamImportExcel.getYear()))
                .findFirst();
            match.ifPresent(dcimsExcel -> dcimsTeamImportExcel.setCompetitionId(dcimsExcel.getId()));
        });
        // 如果存在值为null的竞赛id，填入对应错误消息
        importTeamData.forEach(e -> {
            if (ObjectUtil.isNull(e.getCompetitionId()))
                e.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.competitionNameError, "竞赛名称错误，请检查年份与竞赛名称是否正确填写！"));
        });

        // 设置队伍名
        if(editTeamName){
            importTeamData.forEach(dcimsTeamImportExcel -> {
                if (StringUtils.isBlank(dcimsTeamImportExcel.getName()))
                    dcimsTeamImportExcel.setName(dcimsTeamImportExcel.getStudentName() + "队伍");
            });
        }

        // 判断比赛类型，将表格中读取到的 是否为单人赛 转换为 比赛类型
        importTeamData.forEach(dcimsTeamImportExcel -> {
            if (dcimsTeamImportExcel.getIsSingle() == null){
                dcimsTeamImportExcel.setIsSingle("是");
            }

            if (dcimsTeamImportExcel.getIsSingle().equals("否")){
                dcimsTeamImportExcel.setCompetitionType("100");
            }else if (dcimsTeamImportExcel.getIsSingle().equals("是")){
                dcimsTeamImportExcel.setCompetitionType("50");
            }
        });
        // 检查比赛类型，填入对应错误消息
        importTeamData.forEach(e -> {
            if (ObjectUtil.isNull(e.getCompetitionType()))
                e.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.isSingleError, "请确认比赛类型（单人赛或团队赛），在该列填写‘是’或‘否’！"));
        });


        // 奖项等级转换
        List<SysDictData> dcimsAwardLevel = dictTypeService.selectDictDataByType("dcims_award_level");
        importTeamData.forEach(dcimsTeamImportExcel -> {
            Optional<SysDictData> first = dcimsAwardLevel.stream().filter(dcimsExcel -> StrUtil.equals(dcimsExcel.getDictValue(), dcimsTeamImportExcel.getAwardLevel())).findFirst();
            first.ifPresent(dcimsExcel -> dcimsTeamImportExcel.setAwardLevel(dcimsExcel.getDictValue()));
            if(!first.isPresent()){
                Optional<SysDictData> match = dcimsAwardLevel.stream()
                    .filter(dcimsExcel -> StrUtil.equals(dcimsExcel.getDictLabel(), dcimsTeamImportExcel.getAwardLevel()))
                    .findFirst();
                match.ifPresent(dcimsExcel -> dcimsTeamImportExcel.setAwardLevel(dcimsExcel.getDictValue()));
                if (!match.isPresent()){
                    dcimsTeamImportExcel.setAwardLevel("100");
                }
            }

        });
        // 检查奖项等级，填入对应错误消息
        importTeamData.forEach(e -> {
            if (ObjectUtil.isNull(e.getAwardLevel()))
                e.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.awardLevelError, "请选择正确的获奖等级！"));
        });

        // 填充教师，学生工号
        importTeamData.forEach(dcimsTeamImportExcel -> {
            if (dcimsTeamImportExcel.getStudentName() != null && dcimsTeamImportExcel.getTeacherName() != null){
                dcimsTeamImportExcel.setTeacherName(dcimsTeamImportExcel.getTeacherName().replace(" ", "").replace("，", ",").replace("、", ",").replace(";", ",").replace("；", ","));
                dcimsTeamImportExcel.setStudentName(dcimsTeamImportExcel.getStudentName().replace(" ", "").replace("，", ",").replace("、", ",").replace(";", ",").replace("；", ","));
                dcimsTeamImportExcel.setTeacherName(dcimsTeamImportExcel.getTeacherName().trim());
                dcimsTeamImportExcel.setStudentName(dcimsTeamImportExcel.getStudentName().trim());
                List<String> teachers = Arrays.stream(dcimsTeamImportExcel.getTeacherName().split(",")).collect(Collectors.toList());
                List<String> students = Arrays.stream(dcimsTeamImportExcel.getStudentName().split(",")).collect(Collectors.toList());
                dcimsTeamImportExcel.setTeacherId("");
                dcimsTeamImportExcel.setStudentId("");
                teachers.forEach(teacher -> {
                    TableDataInfo<DcimsTeacherVo> teacherVo = basicDataService.listTeacherDict(teacher, true);
                    if (teacherVo.getRows().size() == 1){
                        dcimsTeamImportExcel.setTeacherId(dcimsTeamImportExcel.getTeacherId() + teacherVo.getRows().get(0).getTeacherId() + ",");
                    }else if (teacherVo.getRows().size() == 0){
                        // 教师不存在
                        dcimsTeamImportExcel.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.teacherNameNotFoundError, "教师"+ teacher +"不存在，请确认姓名是否填写正确！"));
                    }else{
                        // 教师存在重名
                        dcimsTeamImportExcel.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.teacherNameRepeatError, "教师"+ teacher +"存在重名，请选择正确的教师！"));
                    }
                });
                students.forEach(student -> {
                    TableDataInfo<DcimsStudentVo> studentVo = basicDataService.listStudentDict(student, true);
                    if (studentVo.getRows().size() == 1){
                        dcimsTeamImportExcel.setStudentId(dcimsTeamImportExcel.getStudentId() + studentVo.getRows().get(0).getStudentId() + ",");
                    }else if (studentVo.getRows().size() == 0){
                        // 学生不存在
                        dcimsTeamImportExcel.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.studentNameNotFoundError, "学生"+ student +"不存在，请确认姓名是否填写正确！"));
                    }else{
                        // 学生存在重名
                        dcimsTeamImportExcel.getErrors().add(new DcimsTeamImportExcelError(DcimsTeamImportExcelError.ErrorType.studentNameRepeatError, "学生"+ student +"存在重名，请选择正确的学生！"));
                    }
                });
            }
        });


        return importTeamData;
    }


    /**
     * 批量下载团队信息以及佐证材料
     */
    public void download(DcimsTeamBo bo, HttpServletResponse response){
        // 查询竞赛赛事基本信息列表，获取附件id
        List<DcimsTeamVo> dataInfo = queryList(bo);
        PageQuery pq = new PageQuery();
        pq.setPageNum(0);
        pq.setPageSize(10000);
        List<Long> attachmentIds = queryPageList(bo, pq).getRows().stream().map(DcimsTeamVoV2::getSupportMaterial).collect(Collectors.toList());
//        List<Long> attachmentIds = dataInfo.stream().map(DcimsTeamBo::getAttachment).collect(Collectors.toList());
        // 根据附件id查询oss文件
        List<OssFile> ossFileList;
        try {
            ossFileList = ossService.downloadBatchFilesByHttp(attachmentIds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 将oss文件写入本地，写入时依据竞赛名称和所属学院进行分类
        long timestamp = new Date().getTime();
        // 定义基础路径
        String basePath = String.valueOf(timestamp);
        for (DcimsTeamVo team : dataInfo) {
            ossFileList.stream().filter(ossFile -> Objects.equals(ossFile.getSysOssVo().getOssId(), team.getSupportMaterial())).forEach(ossFile -> {
                String subDirectory = String.valueOf(team.getCompetitionId());
                String fileName = translateAwardLevel(team.getAwardLevel()) + "-"+ team.getStudentName() + ossFile.getSysOssVo().getFileSuffix();
                try{
                    // 如果同名文件已经存在，则不进行创建
                    File f = FileUtil.touch(basePath + "/" + subDirectory + "/" + fileName);

                    BufferedInputStream is = new BufferedInputStream(ossFile.getFileContent());
                    BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
                    long copySize = IoUtil.copy(is, os, IoUtil.DEFAULT_BUFFER_SIZE);
                }catch (IORuntimeException e){
                    System.out.println(e.getMessage());
                }catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        try{
            // 翻译id为竞赛名
            List<Long> competitionIds = dataInfo.stream().map(DcimsTeamVo::getCompetitionId).collect(Collectors.toList());
            List<DcimsCompetitionVo> competitionVoList = competitionService.listById(competitionIds);
            File dir = FileUtil.file(basePath);
            // 检查目录是否存在
            System.out.println(dir.exists());
            System.out.println(dir.isDirectory());
            if (dir.exists() && dir.isDirectory()) {
                // 获取目录下的所有文件夹
                File[] folders = dir.listFiles(File::isDirectory);

                // 遍历文件夹
                for (File folder : folders) {
                    // 判断id是否存在，如果存在则重命名文件夹
                    if(competitionVoList.stream().anyMatch(e -> Objects.equals(e.getId(), Long.valueOf(folder.getName())))){
                        // 定义重命名规则
                        String newName = competitionVoList.stream()
                            .filter(e -> Objects.equals(e.getId(), Long.valueOf(folder.getName())))
                            .findFirst()
                            .map(e -> {
                                return translateCollegeName(e.getCollege()) + "-" + e.getName();
                            })
                            .orElse("未知");

                        // 构建新的File对象，用于重命名
                        File newFolder = new File(folder.getParent(), newName);

                        // 执行重命名
                        if (folder.renameTo(newFolder)) {
                            System.out.println("文件夹 " + folder.getName() + " 重命名为 " + newName);
                        } else {
                            System.out.println("无法重命名文件夹 " + folder.getName());
                        }
                    }
                }
            } else {
                System.out.println("指定的目录不存在或不是一个目录。");
            }

            // 根据竞赛id压缩文件
            File zip = ZipUtil.zip(String.valueOf(timestamp));
            InputStream inputStream = new FileInputStream(zip);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
            IoUtil.copy(inputStream, response.getOutputStream());
            response.setContentLength((int) zip.getTotalSpace());
            // 删除临时文件
            FileUtil.del(new File(String.valueOf(timestamp)));
            FileUtil.del(zip);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /*
     *查询获奖情况并处理数据
     * */
    public HashMap<String, Object> queryAward(DcimsTeamBo bo) {
        List<DcimsTeamVo> list = queryList(bo);
        List<DcimsTeamBo> dcimsTeamBos = new ArrayList<>();
        //将DcimsTeamVo中的属性拷贝到DcimsTeamBo中去
        for (DcimsTeamVo team : list) {
            DcimsTeamBo teamBo = new DcimsTeamBo();
            BeanUtils.copyProperties(team, teamBo);
            dcimsTeamBos.add(teamBo);
        }

        List<Long> competitionIds = dcimsTeamBos.stream()
            .map(DcimsTeamBo::getCompetitionId)
            .collect(Collectors.toList());

        //// 翻译id为竞赛名
        List<DcimsCompetitionVo> competitions = competitionService.listById(
            list.stream().map(DcimsTeamVo::getCompetitionId).collect(Collectors.toList())
        );

        // 然后，遍历dcimsTeamBos，并使用上面创建的Map来更新competitionName
        dcimsTeamBos.forEach(teamBo -> {
            teamBo.setCompetitionName(
                competitions.stream().filter(com -> com.getId().equals(teamBo.getCompetitionId())).findFirst().get().getName()
            );
            teamBo.setAnnual(
                competitions.stream().filter(com -> com.getId().equals(teamBo.getCompetitionId())).findFirst().get().getAnnual()
            );
        });
        System.out.println(dcimsTeamBos);

        // 判断年份与竞赛名是否符合筛选条件
        if (bo.getAnnual() != null){
            dcimsTeamBos = dcimsTeamBos.stream().filter(e -> Objects.equals(e.getAnnual(), bo.getAnnual())).collect(Collectors.toList());
        }
        if (bo.getCompetitionName() != null){
            dcimsTeamBos = dcimsTeamBos.stream().filter(e -> e.getCompetitionName().contains(bo.getCompetitionName())).collect(Collectors.toList());
        }

        // 创建一个Map来存储字典项的映射（例如，字典项的值作为键，字典项的标签作为值）
        Map<Object, String> dictMap = new HashMap<>();

        // 查询奖项等级对应的字典项
        List<SysDictData> dictDataList = dictTypeService.selectDictDataByType("dcims_award_level");
        for (SysDictData dictData : dictDataList) {
            dictMap.put(dictData.getDictValue(), dictData.getDictLabel());
        }

        //使用流提取出国家及国际奖项
        List<DcimsTeamBo> nationalAwards = dcimsTeamBos.stream()
            .filter(teamVo -> {
                try {
                    // 尝试将awardLevel字符串转换为整数
                    int level = Integer.parseInt(teamVo.getAwardLevel());
                    // 如果转换成功，则检查是否小于等于9
                    return level <= 9;
                } catch (NumberFormatException e) {
                    // 如果转换失败，则默认不满足条件（可以记录日志或抛出异常，这里选择忽略）
                    return false;
                }
            })
            .collect(Collectors.toList());
        //提取省级奖项
        List<DcimsTeamBo> provincialAwards = dcimsTeamBos.stream()
            .filter(teamVo -> {
                try {
                    // 尝试将awardLevel字符串转换为整数
                    int level = Integer.parseInt(teamVo.getAwardLevel());
                    // 如果转换成功，则检查是否小于等于9
                    return  level <= 19 && level >=15;
                } catch (NumberFormatException e) {
                    // 如果转换失败，则默认不满足条件（可以记录日志或抛出异常，这里选择忽略）
                    return false;
                }
            })
            .collect(Collectors.toList());

        // 遍历列表并替换awardLevel
        for (DcimsTeamBo teamVo : nationalAwards) {
            // 假设awardLevel是一个String或Integer，这里需要根据实际情况进行调整
            String originalAwardLevel = String.valueOf(teamVo.getAwardLevel()); // 可能需要转换
            if (dictMap.containsKey(originalAwardLevel)) {
                // 如果存在，则替换
                teamVo.setAwardLevel(dictMap.get(originalAwardLevel));
            }
        }

        // 遍历列表并替换awardLevel
        for (DcimsTeamBo teamVo : provincialAwards) {
            // 假设awardLevel是一个String或Integer，这里需要根据实际情况进行调整
            String originalAwardLevel = String.valueOf(teamVo.getAwardLevel()); // 可能需要转换
            if (dictMap.containsKey(originalAwardLevel)) {
                // 如果存在，则替换
                teamVo.setAwardLevel(dictMap.get(originalAwardLevel));
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("nationalAwards", nationalAwards);
        map.put("provincialAwards",provincialAwards);
        return map;
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
}
