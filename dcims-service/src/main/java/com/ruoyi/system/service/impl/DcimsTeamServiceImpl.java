package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.DcimsCompetition;
import com.ruoyi.system.domain.DcimsTeamAudit;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.domain.bo.DcimsDeclareAwardBo;
import com.ruoyi.system.domain.entity.OssFile;
import com.ruoyi.system.domain.excel.DcimsTeamImportExcel;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.handler.ExcelWriteHandler;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsTeamAuditMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.*;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsTeamBo;
import com.ruoyi.system.domain.DcimsTeam;
import com.ruoyi.system.mapper.DcimsTeamMapper;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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

    /**
     * 查询参赛团队
     */
    @Override
    public DcimsTeamVoV2 queryById(Long id){
        DcimsTeamVo vo = baseMapper.selectVoById(id);
        DcimsTeamVoV2 voV2 = new DcimsTeamVoV2();
        BeanUtils.copyProperties(vo, voV2);
        voV2.setStudentId(vo.getStudentId().split(","));
        voV2.setStudentName(vo.getStudentName().split(","));
        voV2.setTeacherId(vo.getTeacherId().split(","));
        voV2.setTeacherName(vo.getTeacherName().split(","));
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
        List<Long> competitionIds0 = competitionList.getRows().stream().map(DcimsCompetitionVo::getId).collect(Collectors.toList());
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
    public File getImportTemplate() {
//        EasyExcel.write("./批量导入获奖团队/浙江科技大学大学生科技竞赛获奖学生及指导教师名单批量导入模板.xlsx")
//            .registerWriteHandler(new ExcelWriteHandler()).sheet("批量导入获奖团队").doWrite(new ArrayList<>());
        return ZipUtil.zip("./批量导入获奖团队");
    }

    /**
     * 批量导入模板校验
     */
    public Object checkImportTemplate(File file){
        // 以时间戳为文件唯一标识
        Date now = new Date();
        String pathName = "./" + now.getTime() + new Random().nextInt(64);
        File unzipFile = new File(pathName);

        // 解压文件，打开表格文件
        File unzip = ZipUtil.unzip(file, unzipFile);
        // 文件上传
        List<File> oss = FileUtil.loopFiles(unzip.getPath() + "/佐证材料");

        List<SysOssVo> ossVo = oss.stream().map(o -> ossService.upload(o, o.getName())).collect(Collectors.toList());

        Arrays.stream(FileUtil.ls(unzip.getPath())).findFirst().ifPresent(excelFile -> {
            System.out.println(excelFile);
            try {
                List<DcimsTeamImportExcel> importTeamData = ExcelUtil.importExcel(new FileInputStream(excelFile), DcimsTeamImportExcel.class);
                // 匹配oss
                importTeamData.forEach(dcimsTeamImportExcel -> {
                    Optional<SysOssVo> match = ossVo.stream()
                        .filter(vo -> StrUtil.equals(vo.getFileName(), dcimsTeamImportExcel.getSupportMaterialFileName()))
                        .findFirst();
                    match.ifPresent(vo -> dcimsTeamImportExcel.setSupportMaterial(vo.getOssId()));
                });
                // 数据处理
                handleData(importTeamData);
                System.out.println(importTeamData);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        File[] files = unzip.listFiles();
        return null;
    }

    /**
     * 批量导入数据校验
     */
    public Object checkImportData(Object data){
        return null;
    }

    /**
     * 批量导入数据保存
     */
    public Object saveImportData(Object data){
        return null;
    }

    /**
     * 数据校验 + 数据转换
     */
    private void handleData(List<DcimsTeamImportExcel> importTeamData){
        // 1. 数据检验，找出不合理数据


        // 根据竞赛名和年份查询对应竞赛id
        List<String> competitionNames = importTeamData.stream().map(DcimsTeamImportExcel::getCompetitionName).collect(Collectors.toList());
        List<DcimsCompetitionVo> competitionVos = competitionService.queryList(competitionNames);
        importTeamData.forEach(dcimsTeamImportExcel -> {
            Optional<DcimsCompetitionVo> match = competitionVos.stream()
                .filter(dcimsExcel -> StrUtil.equals(dcimsExcel.getCollegeName(), dcimsTeamImportExcel.getCompetitionName())
                    && StrUtil.equals(String.valueOf(dcimsExcel.getAnnual()), dcimsTeamImportExcel.getYear()))
                .findFirst();
            match.ifPresent(dcimsExcel -> dcimsTeamImportExcel.setCompetitionId(dcimsExcel.getId()));
        });

        // 设置队伍名
        importTeamData.forEach(dcimsTeamImportExcel -> {
            dcimsTeamImportExcel.setName(dcimsTeamImportExcel.getStudentName() + "队伍");
        });

        // 判断比赛类型，1个学生-个人赛50，>=2个学生-团队赛100
        importTeamData.forEach(dcimsTeamImportExcel -> {
            if (dcimsTeamImportExcel.getStudentName().split(",").length > 1){
                dcimsTeamImportExcel.setCompetitionType("100");
            }else {
                dcimsTeamImportExcel.setCompetitionType("50");
            }
        });

        // 奖项等级转换
        List<SysDictData> dcimsAwardLevel = dictTypeService.selectDictDataByType("dcims_award_level");
        importTeamData.forEach(dcimsTeamImportExcel -> {
            Optional<SysDictData> match = dcimsAwardLevel.stream()
                .filter(dcimsExcel -> StrUtil.equals(dcimsExcel.getDictLabel(), dcimsTeamImportExcel.getAwardLevel()))
                .findFirst();
            match.ifPresent(dcimsExcel -> dcimsTeamImportExcel.setAwardLevel(dcimsExcel.getDictValue()));
            if (!match.isPresent()){
                dcimsTeamImportExcel.setAwardLevel("100");
            }
        });

        // 填充教师，学生工号
        importTeamData.forEach(dcimsTeamImportExcel -> {
            List<String> teachers = Arrays.stream(dcimsTeamImportExcel.getTeacherName().split(",")).collect(Collectors.toList());
            List<String> students = Arrays.stream(dcimsTeamImportExcel.getStudentName().split(",")).collect(Collectors.toList());
            dcimsTeamImportExcel.setTeacherId("");
            dcimsTeamImportExcel.setStudentId("");
            teachers.forEach(teacher -> {
                TableDataInfo<DcimsTeacherVo> teacherVo = basicDataService.listTeacherDict(teacher);
                if (teacherVo.getRows().size() == 1){
                    dcimsTeamImportExcel.setTeacherId(dcimsTeamImportExcel.getTeacherId() + teacherVo.getRows().get(0).getTeacherId() + ",");
                }else{
                    // TODO:报错
                }
            });
            students.forEach(student -> {
                TableDataInfo<DcimsStudentVo> studentVo = basicDataService.listStudentDict(student);
                if (studentVo.getRows().size() == 1){
                    dcimsTeamImportExcel.setStudentId(dcimsTeamImportExcel.getStudentId() + studentVo.getRows().get(0).getStudentId() + ",");
                }else{
                    // TODO:报错
                }
            });
        });
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
                String fileName = translateAwardLevel(team.getAwardLevel()) + team.getId() + ossFile.getSysOssVo().getFileSuffix();
                try{
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
