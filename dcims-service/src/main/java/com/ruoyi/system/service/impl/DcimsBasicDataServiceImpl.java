package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.DcimsStudent;
import com.ruoyi.system.domain.DcimsTeacher;
import com.ruoyi.system.domain.vo.DcimsBasicDataStudentVo;
import com.ruoyi.system.domain.vo.DcimsBasicDataTeacherVo;
import com.ruoyi.system.domain.vo.DcimsStudentVo;
import com.ruoyi.system.domain.vo.DcimsTeacherVo;
import com.ruoyi.system.mapper.DcimsCompetitionMapper;
import com.ruoyi.system.mapper.DcimsStudentMapper;
import com.ruoyi.system.mapper.DcimsTeacherMapper;
import com.ruoyi.system.service.IDcimsBasicDataService;
import com.ruoyi.system.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基本数据Service业务层处理
 *
 * @author hiemalberyl
 * @date 2023-05-09
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class DcimsBasicDataServiceImpl implements IDcimsBasicDataService {

    private final DcimsStudentMapper studentBaseMapper;
    private final DcimsTeacherMapper teacherBaseMapper;
    private final RestTemplate restTemplate;

    @Override
    public TableDataInfo<DcimsStudentVo> listStudentDict(String name, boolean exactMatch) {
        QueryWrapper<DcimsStudent> qw = new QueryWrapper<>();
        if (exactMatch) {
            qw.apply(
                StringUtils.isNotBlank(name),
                "REPLACE(name, ' ', '') = {0}",
                name
            );
        } else {
            qw.like("REPLACE(name, ' ', '')", name);
        }
        List<DcimsStudentVo> result = studentBaseMapper.selectVoList(qw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<DcimsTeacherVo> listTeacherDict(String name, boolean exactMatch) {
        QueryWrapper<DcimsTeacher> qw = new QueryWrapper<>();
        if (exactMatch) {
            qw.apply(
                StringUtils.isNotBlank(name),
                "REPLACE(name, ' ', '') = {0}",
                name
            );
        } else {
            qw.like("REPLACE(name, ' ', '')", name);
        }
        List<DcimsTeacherVo> result = teacherBaseMapper.selectVoList(qw);
        return TableDataInfo.build(result);
    }

    @Override
    public DcimsTeacherVo getTeacherNameById(Long teacherId) {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsTeacher::getTeacherId, teacherId);
        return teacherBaseMapper.selectVoOne(lqw);
    }

    @Override
    public DcimsStudentVo getStudentNameById(String studentId) {
        LambdaQueryWrapper<DcimsStudent> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsStudent::getStudentId, studentId);
        return studentBaseMapper.selectVoOne(lqw);
    }

    @Override
    public List<DcimsTeacherVo> getTeacherNameByIds(List<Long> teacherIds) {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.in(teacherIds.size() > 0, DcimsTeacher::getTeacherId, teacherIds);
        return teacherBaseMapper.selectVoList(lqw);
    }

    @Override
    public List<DcimsStudentVo> getStudentNameByIds(List<String> studentIds) {
        LambdaQueryWrapper<DcimsStudent> lqw = new LambdaQueryWrapper<>();
        lqw.in(studentIds.size() > 0, DcimsStudent::getStudentId, studentIds);
        return studentBaseMapper.selectVoList(lqw);
    }

    @Override
    public DcimsTeacherVo queryLoginTeacher() {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DcimsTeacher::getTeacherId,AccountUtils.getAccount().getTeacherId());
        List<DcimsTeacherVo> list = teacherBaseMapper.selectVoList(lqw);
        return list.get(0);
    }

    /**
     * 初始化访问请求头
     */
    private HttpHeaders initRemoteAccessKey() {
//        String appKey = "30477ad1db9129f7";
//        String token = getRemoteAccessKey();
//        Date date = new Date();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("appKey", appKey);
//        headers.set("requestTime", String.valueOf(date.getTime()));
//        headers.set("sign", SecureUtil.md5(appKey + token + date.getTime()));
//
//        return headers;
        log.info("初始化访问请求头");
        String appKey="30477ad1db9129f7";
        String token = getRemoteAccessKey();
        //获取当前时间时间戳
        long requestTime = System.currentTimeMillis();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("appKey", appKey);
        httpHeaders.set("requestTime", String.valueOf(requestTime));
        httpHeaders.set("sign", SecureUtil.md5(appKey + token + requestTime));
        return httpHeaders;
    }

    /**
     * 获取访问秘钥
     */
    private String getRemoteAccessKey() {
//        String appKey = "30477ad1db9129f7";
//        String appSecret = "a689f5ecbdace6664f968da37c33730d52290036a44829800addba8b3eae3526";
//        Date date = new Date();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("appKey", appKey);
//        headers.set("requestTime", String.valueOf(date.getTime()));
//        headers.set("sign", SecureUtil.md5(appKey + appSecret + date.getTime()));
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        String url = "http://172.16.11.51:50027/token/refreshtoken";
//        ResponseEntity<String> token = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//
//        return token.getBody();
        log.info("开始获取访问密钥");
        String appKey="30477ad1db9129f7";
        String appSecret="a689f5ecbdace6664f968da37c33730d52290036a44829800addba8b3eae3526";
        //获取当前时间时间戳
        long requestTime = System.currentTimeMillis();
        //设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        httpHeaders.set("appKey", appKey);
        httpHeaders.set("requestTime", String.valueOf(requestTime));
        httpHeaders.set("sign", SecureUtil.md5(appKey+appSecret+requestTime));
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        String url = "http://172.16.11.51:50027/token/requesttoken";
        //发送请求获得token
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JSONObject jsonObject = new JSONObject(exchange.getBody());
        String token = jsonObject.getStr("token");
        return token;
    }

    /**
     * 从远程同步教师信息
     */
    @Override
    @Scheduled(cron = "0 0 1 * *")
    public void syncTeacherInfo() {
        Boolean hasNext = true;
        Integer current = 1;
        while (hasNext) {
            //发送请求
            String teacherDataUrl = "http://172.16.11.51:50027/DCSBWeb/servlets/d794626b-03e6-4d75-8775-45924495488a@1.0";
            //发送请求,拿到数据
            ResponseEntity<String> exchange = restTemplate.exchange(teacherDataUrl + "?pageNum="+current+"&pageSize=500"
                , HttpMethod.GET
                , new HttpEntity<>(initRemoteAccessKey())
                , String.class);
            //将响应结果转成jsonObject对象,获取数据库中总共页面数量
            JSONObject jsonObject = new JSONObject(exchange.getBody());
            //拿到全部教师数据
            List<DcimsBasicDataTeacherVo> Teachers = jsonObject.getBeanList("data", DcimsBasicDataTeacherVo.class);
            Integer pages = jsonObject.getInt("pages");
            current = jsonObject.getInt("current");
            //判断,如果当前页面大于总页面数,停止请求
            if (current.equals(pages)  ) {
                hasNext = false;
            }

            //从全部数据中拿出teacherId
            List<Long> teacherIds = Teachers.stream()
                .map(DcimsBasicDataTeacherVo::getTeacherId)
                .collect(Collectors.toList());
            //从数据库中查找已有的教师数据
            List<DcimsTeacherVo> teacherAlreadeExist = getTeacherNameByIds(teacherIds);
            //拉出已有教师ID
            List<Long> teacherAlreadeExistId = teacherAlreadeExist.stream()
                .map(DcimsTeacherVo::getTeacherId)
                .collect(Collectors.toList());
            //使用流将需要更新的数据提出来
            List<DcimsBasicDataTeacherVo> teachersUpdate = Teachers.stream()
                .filter(teacher -> teacherAlreadeExistId.contains(teacher.getTeacherId()))
                .collect(Collectors.toList());
            //将已有数据更新
            teacherBaseMapper.updateByTeacherIds(teachersUpdate);
            //将数据库中不存在的数据提出来
            List<DcimsBasicDataTeacherVo> teachersNotExist = Teachers.stream()
                .filter(teacher -> !teacherAlreadeExistId.contains(teacher.getTeacherId()))
                .collect(Collectors.toList());
            teacherBaseMapper.insertByTeacherIds(teachersNotExist);
        }

    }

    /**
     * 从远程同步学生信息
     */
    @Override
    @Scheduled(cron = "0 0 1 * *")
    public void syncStudentInfo() {
        Boolean hasNext = true;
        Integer current = 1;
        while (hasNext) {
            //发送请求
            String studentDataUrl = "http://172.16.11.51:50027/DCSBWeb/servlets/c1145c3d-f4f4-41c9-a4fa-7e0f00319970@1.0";
            //发送请求,拿到数据
            ResponseEntity<String> exchange = restTemplate.exchange(studentDataUrl + "?pageNum="+current+"&pageSize=500"
                , HttpMethod.GET
                , new HttpEntity<>(initRemoteAccessKey())
                , String.class);
            //将响应结果转成jsonObject对象,获取数据库中总共页面数量
            JSONObject jsonObject = new JSONObject(exchange.getBody());
            //拿到全部学生数据
            List<DcimsBasicDataStudentVo> Students = jsonObject.getBeanList("data", DcimsBasicDataStudentVo.class);
            Integer pages = jsonObject.getInt("pages");
            current = jsonObject.getInt("current");
            //判断,如果当前页面大于总页面数,停止请求
            if (current.equals(pages)  ) {
                hasNext = false;
            }

            //从全部数据中拿出StudentId
            List<String> studentIds = Students.stream()
                .map(DcimsBasicDataStudentVo::getStudentId)
                .collect(Collectors.toList());
            //从数据库中查找已有的学生数据
            List<DcimsStudentVo> studentAlreadeExist = getStudentNameByIds(studentIds);
            //拉出已有学生ID
            List<String> studentExistIds = studentAlreadeExist.stream()
                .map(DcimsStudentVo::getStudentId)
                .collect(Collectors.toList());
            //使用流将需要更新的数据提出来
            List<DcimsBasicDataStudentVo> studentsUpdate = Students.stream()
                .filter(student -> studentExistIds.contains(student.getStudentId()))
                .collect(Collectors.toList());
            //将已有数据更新
            studentBaseMapper.updateByStudentIds(studentsUpdate);
            //将数据库中不存在的数据提出来
            List<DcimsBasicDataStudentVo> studentsNotExist = Students.stream()
                .filter(student -> !studentExistIds.contains(student.getStudentId()))
                .collect(Collectors.toList());
            studentBaseMapper.insertByStudentIds(studentsNotExist);
       }

  }
}
