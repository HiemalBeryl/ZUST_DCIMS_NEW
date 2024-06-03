package com.ruoyi.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.entity.OssFile;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysOssService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.DcimsCompetitionBo;
import com.ruoyi.system.service.IDcimsCompetitionService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 竞赛赛事基本信息Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-04-29
 */
@RequiredArgsConstructor
@Service
public class DcimsCompetitionServiceImpl implements IDcimsCompetitionService {

    private final DcimsCompetitionMapper baseMapper;
    private final DcimsTeacherMapper teacherMapper;
    private final DcimsCompetitionTeacherMapper competitionTeacherMapper;
    private final DcimsCompetitionAuditMapper competitionAuditBaseMapper;
    private final SysDeptMapper sysDeptMapper;
    private final ISysOssService ossService;
    private final ISysDeptService deptService;
    private final ISysDictDataService dictDataService;

    /**
     * 查询竞赛赛事基本信息
     */
    @Override
    public DcimsCompetitionVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询竞赛赛事基本信息列表
     */
    @Override
    public TableDataInfo<DcimsCompetitionVo> queryPageList(DcimsCompetitionBo bo, PageQuery pageQuery, boolean audit, boolean export) {
        LambdaQueryWrapper<DcimsCompetition> lqw = buildQueryWrapper(bo);
        // audit为true的话，只能查询出通过审核的竞赛
        lqw.eq(audit, DcimsCompetition::getState, 1);
        // 如果查询所有竞赛，那么根据id倒序
        lqw.orderBy(!audit, false, DcimsCompetition::getId);

        Integer college = getFromCollege();
        if (college != -1)
            lqw.eq(DcimsCompetition::getCollege, college);

        Page<DcimsCompetitionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        System.out.println(lqw);
        System.out.println(result.getRecords().stream().map(DcimsCompetitionVo::getName).collect(Collectors.toList()));
        TableDataInfo<DcimsCompetitionVo> build = TableDataInfo.build(result);
        // 获取团队对应oss信息
        Set<Long> OSSIds = new HashSet<>();
        result.getRecords().forEach(e -> {
            OSSIds.add(Long.valueOf(e.getAttachment()));
        });
        OSSIds.removeAll(Collections.singleton(null));
        System.out.println(OSSIds);
        List<SysOssVo> ossVoList = ossService.listByIds(OSSIds);
        List<DcimsCompetitionVo> voList = result.getRecords();
        if(ossVoList.size() > 0){
            voList = voList.stream().map(e -> {
                DcimsCompetitionVo vo = new DcimsCompetitionVo();
                BeanUtils.copyProperties(e, vo);
                ossVoList.forEach(oss -> {
                    if (oss.getOssId().equals(vo.getAttachment())){
                        vo.setOss(oss);
                    }
                });
                if (ObjectUtil.isNull(vo.getOss()))
                    vo.setOss(new SysOssVo());
                return vo;
            }).collect(Collectors.toList());
        }
        voList.forEach(e -> {
            if (ObjectUtil.isNull(e.getAttachment())){
                e.setOss(null);
            }
        });

        // 查询学院名
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("dcims_college");
        List<SysDictData> dictData = dictDataService.selectDictDataList(sysDictData);
        voList.forEach(com -> {
            for (SysDictData d : dictData){
                if(com.getCollege().equals(Long.parseLong(d.getDictValue()))){
                    com.setCollegeName(d.getDictLabel());
                }
            }
        });

        if(export){
            // 根据ABC类排序，A类在最前面，C类在最后
            voList = voList.stream()
                .sorted(Comparator.comparing(DcimsCompetitionVo::getCollege)
                    .thenComparing(DcimsCompetitionVo::getLevel)
                    .thenComparing(DcimsCompetitionVo::getId))
                .collect(Collectors.toList());
            // 填写序号
            for (int i = 0; i <= voList.size() - 1; i++){
                voList.get(i).setOrder(i + 1);
            }
        }

        TableDataInfo<DcimsCompetitionVo> build1 = TableDataInfo.build(voList);
        BeanUtils.copyProperties(build ,build1);
        build1.setRows(voList);
        System.out.println(build1);
        return build1;
    }

    /**
     * 根据登录用户对应教师工号，查询竞赛赛事基本信息列表
     */
    @Override
    public TableDataInfo<DcimsCompetitionVo> queryPageListByTeacherId(DcimsCompetitionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DcimsCompetition> lqw = buildQueryWrapper(bo);
        lqw.eq(DcimsCompetition::getResponsiblePersonId, AccountUtils.getAccount().getTeacherId());
        Page<DcimsCompetitionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<DcimsCompetitionVo> build = TableDataInfo.build(result);
        // 查询审核状态信息
        List<Long> competitionIds = new ArrayList<>();
        for(DcimsCompetitionVo vo : build.getRows()){
            competitionIds.add(vo.getId());
        }
        System.out.println(competitionIds);
        LambdaQueryWrapper<DcimsCompetitionAudit> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(competitionIds.size() > 0, DcimsCompetitionAudit::getCompetitionId, competitionIds);
        List<DcimsCompetitionAuditVo> auditList = competitionAuditBaseMapper.selectVoList(lqw2);
        Map<Long, DcimsCompetitionAuditVo> m = new HashMap<>();
        for (DcimsCompetitionAuditVo audit : auditList){
            DcimsCompetitionAuditVo audit1 = m.get(audit.getCompetitionId());
            if (audit1 != null){
                if (audit1.getId() < audit.getId()){
                    m.put(audit.getCompetitionId(), audit);
                }
            }else {
                m.put(audit.getCompetitionId(), audit);
            }
        }
        for (DcimsCompetitionAuditVo audit : m.values()){
            for (DcimsCompetitionVo vo : build.getRows()){
                if (Objects.equals(audit.getCompetitionId(), vo.getId())){
                    vo.setAudit(audit);
                }
            }
        }
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("dcims_college");
        List<SysDictData> dictData = dictDataService.selectDictDataList(sysDictData);
        build.getRows().forEach(com -> {
            for (SysDictData d : dictData){
                if(com.getCollege().equals(Long.parseLong(d.getDictValue()))){
                    com.setCollegeName(d.getDictLabel());
                }
            }
        });
        return build;
    }

    /**
     * 查询竞赛赛事基本信息列表
     */
    @Override
    public List<DcimsCompetitionVo> queryList(DcimsCompetitionBo bo) {
        LambdaQueryWrapper<DcimsCompetition> lqw = buildQueryWrapper(bo);
        List<DcimsCompetitionVo> dcimsCompetitionVos = baseMapper.selectVoList(lqw);
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("dcims_college");
        List<SysDictData> dictData = dictDataService.selectDictDataList(sysDictData);
        dcimsCompetitionVos.forEach(com -> {
            for (SysDictData d : dictData){
                if(com.getCollege().equals(Long.parseLong(d.getDictValue()))){
                    com.setCollegeName(d.getDictLabel());
                }
            }
        });
        return dcimsCompetitionVos;
    }

    /**
     * 查询竞赛赛事基本信息列表
     */
    @Override
    public List<DcimsCompetitionVo> queryList(Integer annual) {
        DcimsCompetitionBo bo = new DcimsCompetitionBo();
        bo.setAnnual(annual);
        PageQuery pq = new PageQuery();
        pq.setPageNum(0);
        pq.setPageSize(10000);
        return this.queryPageList(bo, pq, true, false).getRows();
    }

    /**
     * 根据竞赛名列表，查询竞赛赛事基本信息列表
     */
    @Override
    public List<DcimsCompetitionVo> queryList(List<String> CompetitionNames) {
        LambdaQueryWrapper<DcimsCompetition> lqw = new LambdaQueryWrapper<>();
        lqw.in(DcimsCompetition::getName, CompetitionNames);
        List<DcimsCompetitionVo> dcimsCompetitionVos = baseMapper.selectVoList(lqw);
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("dcims_college");
        List<SysDictData> dictData = dictDataService.selectDictDataList(sysDictData);
        dcimsCompetitionVos.forEach(com -> {
            for (SysDictData d : dictData){
                if(com.getCollege().equals(Long.parseLong(d.getDictValue()))){
                    com.setCollegeName(d.getDictLabel());
                }
            }
        });
        return dcimsCompetitionVos;
    }

    private LambdaQueryWrapper<DcimsCompetition> buildQueryWrapper(DcimsCompetitionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<DcimsCompetition> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), DcimsCompetition::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getLevel()), DcimsCompetition::getLevel, bo.getLevel());
        lqw.like(StringUtils.isNotBlank(bo.getPastName()), DcimsCompetition::getPastName, bo.getPastName());
        lqw.eq(bo.getTerm() != null, DcimsCompetition::getTerm, bo.getTerm());
        lqw.eq(bo.getAnnual() != null, DcimsCompetition::getAnnual, bo.getAnnual());
        lqw.eq(StringUtils.isNotBlank(bo.getOrganizer()), DcimsCompetition::getOrganizer, bo.getOrganizer());
        lqw.like(StringUtils.isNotBlank(bo.getResponsiblePersonName()), DcimsCompetition::getResponsiblePersonName, bo.getResponsiblePersonName());
        lqw.eq(bo.getCollege() != null, DcimsCompetition::getCollege, bo.getCollege());
        lqw.between(params.get("beginInnerTime") != null && params.get("endInnerTime") != null,
            DcimsCompetition::getInnerTime ,params.get("beginInnerTime"), params.get("endInnerTime"));
        lqw.between(params.get("beginProvinceTime") != null && params.get("endProvinceTime") != null,
            DcimsCompetition::getProvinceTime ,params.get("beginProvinceTime"), params.get("endProvinceTime"));
        lqw.between(params.get("beginNationalTime") != null && params.get("endNationalTime") != null,
            DcimsCompetition::getNationalTime ,params.get("beginNationalTime"), params.get("endNationalTime"));
        return lqw;
    }

    /**
     * 新增竞赛赛事基本信息
     */
    @Override
    public Boolean insertByBo(DcimsCompetitionBo bo) {
        DcimsCompetition add = BeanUtil.toBean(bo, DcimsCompetition.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改竞赛赛事基本信息
     */
    @Override
    public Boolean updateByBo(DcimsCompetitionBo bo, Boolean resetAuditStatus) {
        //对角色进行权限认证，权限不满足的角色不能更改如赛事类别、拨款等字段
        DcimsCompetition competition = baseMapper.selectById(bo.getId());
        if (!Objects.equals(bo.getLevel(), competition.getLevel())||
            !Objects.equals(bo.getAppropriation(), competition.getAppropriation())||
            !Objects.equals(bo.getPersonLimit(), competition.getPersonLimit())||
            !Objects.equals(bo.getTeamLimit(), competition.getTeamLimit()))
        {
            System.out.println(StpUtil.getRoleList());
            if(!StpUtil.hasRole("AcademicAffairsOffice")){
                return false;
            }
        }
        DcimsCompetition update = BeanUtil.toBean(bo, DcimsCompetition.class);
        //判断是否修改了审核人，如果有修改，则重置审核状态
        if (!bo.getNextAuditId().equals(competition.getNextAuditId())){
            update.setState("0");
        }
        //是否需要重置审核状态
        if (resetAuditStatus)
            validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DcimsCompetition entity){
        // 为竞赛对象添加审核信息
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getParentId,100);
        lqw.eq(SysDept::getOrderNum,entity.getCollege());
        SysDept sysDept = sysDeptMapper.selectOne(lqw);
        if (sysDept != null){
            entity.setNextAuditId(sysDept.getLeaderTeacherId());
            entity.setState("0");
        }else{
            entity.setNextAuditId(-1L);
        }
    }

    /**
     * 批量删除竞赛赛事基本信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 获取竞赛对应指导教师
     */
    public TableDataInfo<DcimsTeacherVo> getTutorList(Long competitionId){
        LambdaQueryWrapper<DcimsCompetitionTeacher> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(DcimsCompetitionTeacher::getCompetitionId,competitionId);
        lqw1.eq(DcimsCompetitionTeacher::getDeleted,"0");
        List<DcimsCompetitionTeacher> tutorIdList = competitionTeacherMapper.selectList(lqw1);

        LambdaQueryWrapper<DcimsTeacher> lqw2 = new LambdaQueryWrapper<>();
        List<Long> teacherIds = new ArrayList<>();
        teacherIds.add(-1L);
        tutorIdList.forEach(element -> teacherIds.add(element.getTeacherId()));
        lqw2.in(DcimsTeacher::getTeacherId,teacherIds);
        List<DcimsTeacherVo> dcimsTeacherVoList = teacherMapper.selectVoList(lqw2);
        dcimsTeacherVoList.forEach(element -> tutorIdList.forEach(tutor -> {
            if(tutor.getTeacherId().equals(element.getTeacherId()))
                element.setCompetitionTeacherId(tutor.getId());
        }));

        return TableDataInfo.build(dcimsTeacherVoList);
    }

    /**
     * 添加多个指导教师
     */
    public Boolean addTutor(Long competitionId, Long[] teacherIds){
        LambdaQueryWrapper<DcimsCompetitionTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsCompetitionTeacher::getCompetitionId,competitionId);
        lqw.in(DcimsCompetitionTeacher::getTeacherId,teacherIds);
        lqw.eq(DcimsCompetitionTeacher::getDeleted,"0");
        if(competitionTeacherMapper.selectList(lqw).size() > 0){
            return false;
        }

        for (Long teacherId:teacherIds) {
            DcimsCompetitionTeacher entity = new DcimsCompetitionTeacher();
            entity.setCompetitionId(competitionId);
            entity.setTeacherId(teacherId);
            competitionTeacherMapper.insert(entity);
        }
        return true;
    }

    /**
     * 根据id删除指导教师
     */
    public Boolean removeTutor(Long id){
        return competitionTeacherMapper.deleteById(id) > 0;
    }

    /**
     * 根据主键查竞赛vo
     *
     * @param id
     */
    @Override
    public List<DcimsCompetitionVo> listById(List<Long> id) {
        LambdaQueryWrapper<DcimsCompetition> lqw = new LambdaQueryWrapper<>();
        lqw.in(id.size() > 0, DcimsCompetition::getId, id);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 批量下载竞赛立项申报书附件
     */
    public void download(DcimsCompetitionBo bo, HttpServletResponse response){
        // 查询竞赛赛事基本信息列表，获取附件id
        List<DcimsCompetitionVo> dataInfo = queryList(bo);
        PageQuery pq = new PageQuery();
        pq.setPageNum(0);
        pq.setPageSize(10000);
        List<Long> attachmentIds = queryPageList(bo, pq, true, false).getRows().stream().map(DcimsCompetitionVo::getAttachment).collect(Collectors.toList());
//        List<Long> attachmentIds = dataInfo.stream().map(DcimsCompetitionVo::getAttachment).collect(Collectors.toList());
        // 根据附件id查询oss文件
        List<OssFile> ossFileList;
        try {
            ossFileList = ossService.downloadBatchFiles(attachmentIds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 将oss文件写入本地，写入时依据竞赛名称和所属学院进行分类
        long timestamp = new Date().getTime();
        // 定义基础路径
        String basePath = String.valueOf(timestamp);
        for (DcimsCompetitionVo competition : dataInfo) {
            ossFileList.stream().filter(ossFile -> Objects.equals(ossFile.getSysOssVo().getOssId(), competition.getAttachment())).forEach(ossFile -> {
                String subDirectory = competition.getCollegeName();
                String fileName = competition.getAnnual() + "年" + competition.getName() + "-科技竞赛立项申报书" + ossFile.getSysOssVo().getFileSuffix();
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
            // 根据所属学院压缩文件
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

    public void download2(DcimsCompetitionBo bo, HttpServletResponse response){
        // 查询竞赛赛事基本信息列表，获取附件id
        List<DcimsCompetitionVo> dataInfo = queryList(bo);
        PageQuery pq = new PageQuery();
        pq.setPageNum(0);
        pq.setPageSize(10000);
        List<Long> attachmentIds = queryPageList(bo, pq, true, false).getRows().stream().filter(e -> e.getTeachingHoursAttachment() != null).map(DcimsCompetitionVo::getTeachingHoursAttachment).collect(Collectors.toList());
        System.out.println(attachmentIds);
//        List<Long> attachmentIds = dataInfo.stream().map(DcimsCompetitionVo::getAttachment).collect(Collectors.toList());
        // 根据附件id查询oss文件
        List<OssFile> ossFileList;
        try {
            ossFileList = ossService.downloadBatchFiles(attachmentIds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 将oss文件写入本地，写入时依据竞赛名称和所属学院进行分类
        long timestamp = new Date().getTime();
        // 定义基础路径
        String basePath = String.valueOf(timestamp);
        System.out.println(dataInfo);
        System.out.println(ossFileList);
        for (DcimsCompetitionVo competition : dataInfo) {
            ossFileList.stream().filter(ossFile -> Objects.equals(ossFile.getSysOssVo().getOssId(), competition.getTeachingHoursAttachment())).forEach(ossFile -> {
                String subDirectory = competition.getCollegeName();
                String fileName = competition.getAnnual() + "年" + competition.getName() + "-集中授课安排表" + ossFile.getSysOssVo().getFileSuffix();
                System.out.println("匹配到了："+ subDirectory + "  " + fileName);
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
            // 根据所属学院压缩文件
            File zip = ZipUtil.zip(basePath);
            InputStream inputStream = new FileInputStream(zip);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
            IoUtil.copy(inputStream, response.getOutputStream());
            response.setContentLength((int) zip.getTotalSpace());
            // 删除临时文件
//            FileUtil.del(new File(String.valueOf(timestamp)));
//            FileUtil.del(zip);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取当前教师所属学院
     */
    public Integer getFromCollege(){
        // 教务处可以查看全校，学院管理员可以查看学院，普通教师查看自己
        try {
            List<SysDept> depts = deptService.selectDeptList(new SysDept());
            long loginId = AccountUtils.getAccount().getTeacherId();
            Integer teacherCollege = -1;
            Optional<SysDept> first = depts.stream().filter(e -> e.getLeaderTeacherId() != null)
                .filter(e -> e.getLeaderTeacherId().equals(loginId)).findFirst();
            if (first.isPresent()){
                teacherCollege = first.get().getOrderNum();
            }
            if (first.isPresent() && first.get().getDeptName().equals("浙江科技学院教务处")){
                teacherCollege = -1;
            }
            return teacherCollege;
        }catch (Exception e){
            return -1;
        }
    }
}
