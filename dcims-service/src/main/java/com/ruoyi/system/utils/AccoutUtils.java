package com.ruoyi.system.utils;

import com.ruoyi.system.utils.domain.Account;
import com.ruoyi.system.utils.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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
}
