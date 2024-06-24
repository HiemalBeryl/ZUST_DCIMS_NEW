package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.JsonUtils;
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
public class DcimsBasicDataServiceImpl implements IDcimsBasicDataService {

    private final DcimsStudentMapper studentBaseMapper;
    private final DcimsTeacherMapper teacherBaseMapper;
    private final RestTemplate restTemplate;

    @Override
    public TableDataInfo<DcimsStudentVo> listStudentDict(String name) {
        LambdaQueryWrapper<DcimsStudent> lqw = new LambdaQueryWrapper<>();
        lqw.like(DcimsStudent::getName,name);
        List<DcimsStudentVo> result = studentBaseMapper.selectVoList(lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<DcimsTeacherVo> listTeacherDict(String name) {
        LambdaQueryWrapper<DcimsTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.like(DcimsTeacher::getName,name);
        List<DcimsTeacherVo> result = teacherBaseMapper.selectVoList(lqw);
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
        String appKey = "30477ad1db9129f7";
        String token = getRemoteAccessKey();
        Date date = new Date();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appKey", appKey);
        headers.set("requestTime", String.valueOf(date.getTime()));
        headers.set("sign", SecureUtil.md5(appKey + token + date.getTime()));

        return headers;
    }

    /**
     * 获取访问秘钥
     */
    private String getRemoteAccessKey() {
        String appKey = "30477ad1db9129f7";
        String appSecret = "a689f5ecbdace6664f968da37c33730d52290036a44829800addba8b3eae3526";
        Date date = new Date();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appKey", appKey);
        headers.set("requestTime", String.valueOf(date.getTime()));
        headers.set("sign", SecureUtil.md5(appKey + appSecret + date.getTime()));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://172.16.11.51:50027/token/refreshtoken";
        ResponseEntity<String> token = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return token.getBody();
    }

    /**
     * 从远程同步教师信息
     */
    @Override
    @Scheduled(cron = "0 0 1 * *")
    public void syncTeacherInfo() {
        String teacherDataUrl = "http://172.16.11.51:50027/DCSBWeb/servlets/d794626b-03e6-4d75-8775-45924495488a@1.0";
        //第一遍请求，拿到总记录数
        ResponseEntity<String> exchange = restTemplate.exchange(teacherDataUrl + "?pageNum=1&pageSize=500", HttpMethod.GET, new HttpEntity<>(initRemoteAccessKey()), String.class);
        //将返回的数据转为list实体类对象
        List<DcimsBasicDataTeacherVo> Teachers = JSONUtil.toList(exchange.getBody(), DcimsBasicDataTeacherVo.class);
        //使用流提取teacherid
        List<Long> teacherIds = Teachers.stream()
            .map(DcimsBasicDataTeacherVo::getTeacherId)
            .collect(Collectors.toList());

        //根据teacherIds从数据库中查找已有的数据
        List<DcimsTeacherVo> teacherAlreadeExist = getTeacherNameByIds(teacherIds);

        //获取数据库中已有教师的ID
        List<Long> teacherAlreadeExistIds = teacherAlreadeExist.stream()
            .map(DcimsTeacherVo::getTeacherId)
            .collect(Collectors.toList());

        // 过滤出Teachers中需要更新的对象
        List<DcimsBasicDataTeacherVo> teachersToUpdate = Teachers.stream()
            .filter(teacher -> teacherAlreadeExistIds.contains(teacher.getTeacherId()))
            .collect(Collectors.toList());



        if(!teacherAlreadeExist.isEmpty()){
            //根据教职工ID批量更新
            teacherBaseMapper.updateByTeacherIds(teacherAlreadeExist);
        }

        //同理，将数据库中不存在的teacherId使用流过滤出来，进行插入操作

        //将响应结果转成jsonObject对象,获取数据库中总共页面数量
        JSONObject jsonObject = new JSONObject(exchange.getBody());
        Integer pages = jsonObject.getInt("pages");
        Integer current = jsonObject.getInt("current");

        //判断当前页面是否为最后一页,不是的话发送请求插入数据库
        while (current<=pages){
            current++;
            exchange = restTemplate
                .exchange(teacherDataUrl + "?pageNum="+current+"&pageSize=500"
                    , HttpMethod.GET
                    , new HttpEntity<>(initRemoteAccessKey())
                    , String.class);
            System.out.println(exchange.getBody());

            //将返回的数据转为list实体类对象
            Teachers = JSONUtil.toList(exchange.getBody(), DcimsBasicDataTeacherVo.class);
            for (DcimsBasicDataTeacherVo teacher : Teachers) {
                DcimsTeacherVo teacherNameById = getTeacherNameById(teacher.getTeacherId());
                //如果数据库中有该名老师,跳过数据
                if(teacherNameById!=null){
                    continue;
                }
                //如果数据库中没有该名老师,插入数据
                teacherBaseMapper.insert(BeanUtil.toBean(teacher,DcimsTeacher.class));
            }
        }

    }

    /**
     * 从远程同步学生信息
     */
    @Override
    @Scheduled(cron = "0 0 1 * *")
    public void syncStudentInfo() {
        String studentDataUrl = "http://172.16.11.51:50027/DCSBWeb/servlets/c1145c3d-f4f4-41c9-a4fa-7e0f00319970@1.0";
        //第一遍请求，拿到总记录数
        ResponseEntity<String> exchange = restTemplate.exchange(studentDataUrl + "?pageNum=1&pageSize=500"
                , HttpMethod.GET
                , new HttpEntity<>(initRemoteAccessKey())
                , String.class);

        //将返回的数据转为list实体类对象
        List<DcimsBasicDataStudentVo> Students = JSONUtil.toList(exchange.getBody(), DcimsBasicDataStudentVo.class);
        for (DcimsBasicDataStudentVo student : Students) {
            studentBaseMapper.insertOrUpdate(BeanUtil.toBean(student,DcimsStudent.class));
        }

        //将响应结果转成jsonObject对象,获取数据库中总共页面数量
        JSONObject jsonObject = new JSONObject(exchange.getBody());
        Integer pages = jsonObject.getInt("pages");
        Integer current = jsonObject.getInt("current");

        //判断当前页面是否为最后一页,不是的话发送请求插入数据库
        while (current<=pages){
            current++;
            exchange = restTemplate.exchange(studentDataUrl + "?pageNum="+current+"&pageSize=500"
                , HttpMethod.GET
                , new HttpEntity<>(initRemoteAccessKey())
                , String.class);
            System.out.println(exchange.getBody());

            //将返回的数据转为list实体类对象
            Students = JSONUtil.toList(exchange.getBody(), DcimsBasicDataStudentVo.class);
            for (DcimsBasicDataStudentVo student : Students) {
                DcimsStudentVo studentNameById = getStudentNameById(student.getStudentId());
                //如果数据库中有该名学生,跳过数据
                if(studentNameById!=null){
                    continue;
                }
                //如果数据库中没有该名学生,插入数据
                studentBaseMapper.insert(BeanUtil.toBean(student,DcimsStudent.class));
            }
        }

    }
}
