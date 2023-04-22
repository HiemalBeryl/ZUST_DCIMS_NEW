package com.ruoyi.system.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.ruoyi.system.utils.domain.Account;
import com.ruoyi.system.utils.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class AccoutUtils {

    private static AccountMapper accountMapper;

    @Autowired
    public void AccountUtils(AccountMapper accountMapper){
        AccoutUtils.accountMapper = accountMapper;
    }

    public static Account getTeacherId(String id){
        return accountMapper.selectById(id.substring(9));
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
