package com.ruoyi.system.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.utils.domain.Account;
import com.ruoyi.system.utils.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AccountUtils {

    private static AccountMapper accountMapper;
    private static ISysDeptService deptService;

    @Autowired
    public AccountUtils(AccountMapper accountMapper, ISysDeptService deptService){
        AccountUtils.accountMapper = accountMapper;
        AccountUtils.deptService = deptService;
    }

    public static Account getAccount(){
        return accountMapper.selectById(StpUtil.getLoginIdAsString().substring(9));
    }

    public static Account getAccount(String id){
        return accountMapper.selectById(id.substring(9));
    }

    public static Long getCollegeId(){
        Long teacherId = AccountUtils.getAccount().getTeacherId();
        List<SysDept> sysDepts = deptService.selectDeptList(new SysDept());
        System.out.println("sysDepts = " + sysDepts);
        Optional<SysDept> firstDept = sysDepts.stream().filter(dept -> dept.getLeaderTeacherId().equals(teacherId)).findFirst();
        System.out.println("firstDept = " + firstDept);
        if (firstDept.isPresent()){
            return Long.valueOf(firstDept.get().getOrderNum());
        }
        return null;
    }

    public static Long getNextTeacherId(){
        List<String> roleList = StpUtil.getRoleList();
        System.out.println(roleList);
        for (String role:roleList) {
            if (role.equals("AcademicAffairsOffice")){
                //教务处为审核最高级，返回0
                return 0L;
            } else if (role.equals("AcademyCompetitionHead")) {
                //该教师为学院竞赛协调人，返回教务处工号

            }else if(role.equals("AcademyCompetitionTeacher")){
                //该教师为学科竞赛负责人，找到该教师对应的学院竞赛协调人工号即可。
            }
        }
        return 10001L;
    }
}
